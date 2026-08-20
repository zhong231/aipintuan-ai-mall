package cn.bugstack.domain.order.service;

import cn.bugstack.domain.order.adapter.port.IProductPort;
import cn.bugstack.domain.order.adapter.repository.IOrderRepository;
import cn.bugstack.domain.order.model.aggregate.CreateOrderAggregate;
import cn.bugstack.domain.order.model.entity.MarketPayDiscountEntity;
import cn.bugstack.domain.order.model.entity.OrderEntity;
import cn.bugstack.domain.order.model.entity.PayOrderEntity;
import cn.bugstack.domain.order.model.valobj.MarketTypeVO;
import cn.bugstack.domain.order.model.valobj.OrderStatusVO;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeRefundResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class OrderService extends AbstractOrderService {

    @Value("${alipay.notify_url}")
    private String notifyUrl;
    @Value("${alipay.return_url}")
    private String returnUrl;

    @Resource
    private AlipayClient alipayClient;

    public OrderService(IOrderRepository repository, IProductPort port) {
        super(repository, port);
    }

    @Override
    protected void doSaveOrder(CreateOrderAggregate orderAggregate) {
        repository.doSaveOrder(orderAggregate);
    }

    @Override
    protected MarketPayDiscountEntity lockMarketPayOrder(String userId, String teamId, Long activityId, String productId, String orderId) {
        return port.lockMarketPayOrder(userId, teamId, activityId, productId, orderId);
    }

    @Override
    protected PayOrderEntity doPrepayOrder(String userId, String productId, String productName, String orderId, BigDecimal totalAmount) throws AlipayApiException {
        return doPrepayOrder(userId, productId, productName, orderId, totalAmount, null);
    }

    @Override
    protected PayOrderEntity doPrepayOrder(String userId, String productId, String productName, String orderId, BigDecimal totalAmount, MarketPayDiscountEntity marketPayDiscountEntity) throws AlipayApiException {
        // 支付金额
        BigDecimal payAmount = null == marketPayDiscountEntity ? totalAmount : marketPayDiscountEntity.getPayPrice();

        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setNotifyUrl(notifyUrl);
        request.setReturnUrl(returnUrl);

        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", orderId);
        bizContent.put("total_amount", payAmount);
        bizContent.put("subject", productName);
        bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");
        request.setBizContent(bizContent.toString());

        String form = alipayClient.pageExecute(request).getBody();

        PayOrderEntity payOrderEntity = new PayOrderEntity();
        payOrderEntity.setOrderId(orderId);
        payOrderEntity.setPayUrl(form);
        payOrderEntity.setOrderStatus(OrderStatusVO.PAY_WAIT);

        // 营销信息
        payOrderEntity.setMarketType(null == marketPayDiscountEntity ? MarketTypeVO.NO_MARKET.getCode() : MarketTypeVO.GROUP_BUY_MARKET.getCode());
        payOrderEntity.setMarketDeductionAmount(null == marketPayDiscountEntity ? BigDecimal.ZERO : marketPayDiscountEntity.getDeductionPrice());
        payOrderEntity.setPayAmount(payAmount);

        repository.updateOrderPayInfo(payOrderEntity);

        return payOrderEntity;
    }

    @Override
    public void changeOrderPaySuccess(String orderId, Date payTime) {
        OrderEntity orderEntity = repository.queryOrderByOrderId(orderId);
        if (null == orderEntity) return;

        if (MarketTypeVO.GROUP_BUY_MARKET.getCode().equals(orderEntity.getMarketType())) {
            repository.changeMarketOrderPaySuccess(orderId);
            // 发起营销结算。这个过程可以是http/rpc直接调用，也可以发一个商城交易支付完成的消息，之后拼团系统自己接收做结算。
            port.settlementMarketPayOrder(orderEntity.getUserId(), orderId, payTime);
            // 注意；在公司中，发起结算的http/rpc调用可能会失败，这个时候还会有增加job任务补偿。条件为，检查一笔走了拼团的订单，超过n分钟后，仍然没有做拼团结算状态变更。
            // 我们这里失败了，会抛异常，借助支付宝回调/job来重试。你可以单独实现一个独立的job来处理。
        } else {
            repository.changeOrderPaySuccess(orderId, payTime);
        }

    }

    @Override
    public List<String> queryNoPayNotifyOrder() {
        return repository.queryNoPayNotifyOrder();
    }

    @Override
    public List<String> queryTimeoutCloseOrderList() {
        return repository.queryTimeoutCloseOrderList();
    }

    @Override
    public boolean changeOrderClose(String orderId) {
        return repository.changeOrderClose(orderId);
    }

    @Override
    public void changeOrderMarketSettlement(List<String> outTradeNoList) {
        repository.changeOrderMarketSettlement(outTradeNoList);
    }

    @Override
    public boolean refundMarketOrder(String userId, String orderId) {
        // 1. 查询订单信息，验证订单是否存在且属于该用户
        OrderEntity orderEntity = repository.queryOrderByUserIdAndOrderId(userId, orderId);
        if (null == orderEntity) {
            log.warn("退单失败，订单不存在或不属于该用户 userId:{} orderId:{}", userId, orderId);
            return false;
        }

        // 2. 检查订单状态，只有create、pay_wait、pay_success、deal_done状态的订单可以退单
        String status = orderEntity.getOrderStatusVO().getCode();
        if (OrderStatusVO.CLOSE.getCode().equals(status)) {
            log.warn("退单失败，订单已关闭 userId:{} orderId:{} status:{}", userId, orderId, status);
            return false;
        }

        // 3. 对于营销类型的单子，调用拼团执行组队退单
        port.refundMarketPayOrder(userId, orderId);

        // 4. 执行退单操作；CREATE 新创建订单，不需要退款
        if (OrderStatusVO.CREATE.getCode().equals(status) || OrderStatusVO.PAY_WAIT.getCode().equals(status)) {
            return repository.refundOrder(userId, orderId);
        } else {
            boolean result = repository.refundMarketOrder(userId, orderId);
            if (result) {
                log.info("退单成功 userId:{} orderId:{}", userId, orderId);
            } else {
                log.warn("退单失败 userId:{} orderId:{}", userId, orderId);
            }
            return result;
        }

    }

    @Override
    public boolean refundPayOrder(String userId, String orderId) throws AlipayApiException {
        // 1. 查询订单信息，验证订单是否存在且属于该用户
        OrderEntity orderEntity = repository.queryOrderByUserIdAndOrderId(userId, orderId);
        if (null == orderEntity) {
            log.warn("退款失败，订单不存在或不属于该用户 userId:{} orderId:{}", userId, orderId);
            return false;
        }

        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
        AlipayTradeRefundModel refundModel = new AlipayTradeRefundModel();
        refundModel.setOutTradeNo(orderEntity.getOrderId());
        refundModel.setRefundAmount(orderEntity.getPayAmount().toString());
        refundModel.setRefundReason("交易退单");
        request.setBizModel(refundModel);

        // 交易退款
        AlipayTradeRefundResponse execute = alipayClient.execute(request);
        if (!execute.isSuccess()) return false;

        // 状态变更
        repository.refundOrder(userId, orderId);

        return true;
    }

}
