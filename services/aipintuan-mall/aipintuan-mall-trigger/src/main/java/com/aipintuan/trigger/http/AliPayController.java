package com.aipintuan.trigger.http;

import com.aipintuan.api.IPayService;
import com.aipintuan.api.dto.CreatePayRequestDTO;
import com.aipintuan.api.dto.NotifyRequestDTO;
import com.aipintuan.api.dto.QueryOrderListRequestDTO;
import com.aipintuan.api.dto.QueryOrderListResponseDTO;
import com.aipintuan.api.dto.RefundOrderRequestDTO;
import com.aipintuan.api.dto.RefundOrderResponseDTO;
import com.aipintuan.api.response.Response;
import com.aipintuan.domain.order.model.entity.OrderEntity;
import com.aipintuan.domain.order.model.entity.PayOrderEntity;
import com.aipintuan.domain.order.model.entity.ShopCartEntity;
import com.aipintuan.domain.order.model.valobj.MarketTypeVO;
import com.aipintuan.domain.order.service.IOrderService;
import com.aipintuan.types.common.Constants;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeQueryRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController()
@CrossOrigin("*")
@RequestMapping("/api/v1/alipay/")
public class AliPayController implements IPayService {

    @Value("${alipay.alipay_public_key}")
    private String alipayPublicKey;

    @Resource
    private IOrderService orderService;
    
    @Resource
    private AlipayClient alipayClient;

    /**
     * http://localhost:8080/api/v1/alipay/create_pay_order
     * <p>
     * {
     * "userId": "10001",
     * "productId": "100001"
     * }
     */
    @RequestMapping(value = "create_pay_order", method = RequestMethod.POST)
    @Override
    public Response<String> createPayOrder(@RequestBody CreatePayRequestDTO createPayRequestDTO) {
        try {
            log.info("商品下单，根据商品ID创建支付单开始 userId:{} productId:{}", createPayRequestDTO.getUserId(), createPayRequestDTO.getUserId());
            String userId = createPayRequestDTO.getUserId();
            String productId = createPayRequestDTO.getProductId();
            String teamId = createPayRequestDTO.getTeamId();
            Integer marketType = createPayRequestDTO.getMarketType();

            // 下单
            PayOrderEntity payOrderEntity = orderService.createOrder(ShopCartEntity.builder()
                    .userId(userId)
                    .productId(productId)
                    .teamId(teamId)
                    .marketTypeVO(MarketTypeVO.valueOf(marketType))
                    .activityId(createPayRequestDTO.getActivityId())
                    .build());

            log.info("商品下单，根据商品ID创建支付单完成 userId:{} productId:{} orderId:{}", userId, productId, payOrderEntity.getOrderId());
            return Response.<String>builder()
                    .code(Constants.ResponseCode.SUCCESS.getCode())
                    .info(Constants.ResponseCode.SUCCESS.getInfo())
                    .data(payOrderEntity.getPayUrl())
                    .build();
        } catch (Exception e) {
            log.error("商品下单，根据商品ID创建支付单失败 userId:{} productId:{}", createPayRequestDTO.getUserId(), createPayRequestDTO.getUserId(), e);
            return Response.<String>builder()
                    .code(Constants.ResponseCode.UN_ERROR.getCode())
                    .info(Constants.ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "group_buy_notify", method = RequestMethod.POST)
    @Override
    public String groupBuyNotify(@RequestBody NotifyRequestDTO requestDTO) {
        log.info("拼团回调，组队完成，结算开始 {}", JSON.toJSONString(requestDTO));
        try {
            // 营销结算
            orderService.changeOrderMarketSettlement(requestDTO.getOutTradeNoList());
            return "success";
        } catch (Exception e) {
            log.error("拼团回调，组队完成，结算失败 {}", JSON.toJSONString(requestDTO), e);
            return "error";
        }
    }

    /**
     * http://localhost:8088/api/v1/alipay/alipay_notify_url
     */
    @RequestMapping(value = "alipay_notify_url", method = RequestMethod.POST)
    public String payNotify(HttpServletRequest request) throws AlipayApiException, ParseException {
        log.info("支付回调，消息接收 {}", request.getParameter("trade_status"));

        if (!request.getParameter("trade_status").equals("TRADE_SUCCESS")) {
            return "false";
        }

        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (String name : requestParams.keySet()) {
            params.put(name, request.getParameter(name));
        }

        String tradeNo = params.get("out_trade_no");
        String gmtPayment = params.get("gmt_payment");
        String alipayTradeNo = params.get("trade_no");

        String sign = params.get("sign");
        String content = AlipaySignature.getSignCheckContentV1(params);
        boolean checkSignature = AlipaySignature.rsa256CheckContent(content, sign, alipayPublicKey, "UTF-8"); // 验证签名
        // 支付宝验签
        if (!checkSignature) {
            return "false";
        }

        // 验签通过
        log.info("支付回调，交易名称: {}", params.get("subject"));
        log.info("支付回调，交易状态: {}", params.get("trade_status"));
        log.info("支付回调，支付宝交易凭证号: {}", params.get("trade_no"));
        log.info("支付回调，商户订单号: {}", params.get("out_trade_no"));
        log.info("支付回调，交易金额: {}", params.get("total_amount"));
        log.info("支付回调，买家在支付宝唯一id: {}", params.get("buyer_id"));
        log.info("支付回调，买家付款时间: {}", params.get("gmt_payment"));
        log.info("支付回调，买家付款金额: {}", params.get("buyer_pay_amount"));
        log.info("支付回调，支付回调，更新订单 {}", tradeNo);

        orderService.changeOrderPaySuccess(tradeNo, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(params.get("gmt_payment")));

        return "success";
    }

    /**
     * http://localhost:8080/api/v1/alipay/query_user_order_list
     * <p>
     * {
     * "userId": "10001",
     * "lastId": null,
     * "pageSize": 10
     * }
     */
    @RequestMapping(value = "query_user_order_list", method = RequestMethod.POST)
    @Override
    public Response<QueryOrderListResponseDTO> queryUserOrderList(@RequestBody QueryOrderListRequestDTO requestDTO) {
        try {
            log.info("查询用户订单列表开始 userId:{} lastId:{} pageSize:{}", requestDTO.getUserId(), requestDTO.getLastId(), requestDTO.getPageSize());
            
            String userId = requestDTO.getUserId();
            Long lastId = requestDTO.getLastId();
            Integer pageSize = requestDTO.getPageSize();
            
            // 查询订单列表，多查询一条用于判断是否还有更多数据
            List<OrderEntity> orderList = orderService.queryUserOrderList(userId, lastId, pageSize + 1);
            
            // 判断是否还有更多数据
            boolean hasMore = orderList.size() > pageSize;
            if (hasMore) {
                orderList = orderList.subList(0, pageSize);
            }
            
            // 转换为响应对象
            List<QueryOrderListResponseDTO.OrderInfo> orderInfoList = orderList.stream().map(order -> {
                QueryOrderListResponseDTO.OrderInfo orderInfo = new QueryOrderListResponseDTO.OrderInfo();
                orderInfo.setId(order.getId());
                orderInfo.setUserId(order.getUserId());
                orderInfo.setProductId(order.getProductId());
                orderInfo.setProductName(order.getProductName());
                orderInfo.setOrderId(order.getOrderId());
                orderInfo.setOrderTime(order.getOrderTime());
                orderInfo.setTotalAmount(order.getTotalAmount());
                orderInfo.setStatus(order.getOrderStatusVO() != null ? order.getOrderStatusVO().getCode() : null);
                orderInfo.setPayUrl(order.getPayUrl());
                orderInfo.setMarketType(order.getMarketType());
                orderInfo.setMarketDeductionAmount(order.getMarketDeductionAmount());
                orderInfo.setPayAmount(order.getPayAmount());
                orderInfo.setPayTime(order.getPayTime());
                return orderInfo;
            }).collect(Collectors.toList());
            
            QueryOrderListResponseDTO responseDTO = new QueryOrderListResponseDTO();
            responseDTO.setOrderList(orderInfoList);
            responseDTO.setHasMore(hasMore);
            responseDTO.setLastId(!orderList.isEmpty() ? orderList.get(orderList.size() - 1).getId() : null);
            
            log.info("查询用户订单列表完成 userId:{} 返回订单数量:{} hasMore:{}", userId, orderInfoList.size(), hasMore);
            return Response.<QueryOrderListResponseDTO>builder()
                    .code(Constants.ResponseCode.SUCCESS.getCode())
                    .info(Constants.ResponseCode.SUCCESS.getInfo())
                    .data(responseDTO)
                    .build();
        } catch (Exception e) {
            log.error("查询用户订单列表失败 userId:{}", requestDTO.getUserId(), e);
            return Response.<QueryOrderListResponseDTO>builder()
                    .code(Constants.ResponseCode.UN_ERROR.getCode())
                    .info(Constants.ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    /**
     * http://localhost:8080/api/v1/alipay/refund_order
     * <p>
     * {
     * "userId": "xfg02",
     * "orderId": "928263928388"
     * }
     */
    @RequestMapping(value = "refund_order", method = RequestMethod.POST)
    @Override
    public Response<RefundOrderResponseDTO> refundOrder(@RequestBody RefundOrderRequestDTO requestDTO) {
        try {
            log.info("用户退单开始 userId:{} orderId:{}", requestDTO.getUserId(), requestDTO.getOrderId());
            
            String userId = requestDTO.getUserId();
            String orderId = requestDTO.getOrderId();
            
            // 执行退单操作
            boolean success = orderService.refundMarketOrder(userId, orderId);
            
            RefundOrderResponseDTO responseDTO = new RefundOrderResponseDTO();
            responseDTO.setSuccess(success);
            responseDTO.setOrderId(orderId);
            responseDTO.setMessage(success ? "退单成功" : "退单失败，订单不存在、已关闭或不属于该用户");
            
            log.info("用户退单完成 userId:{} orderId:{} success:{}", userId, orderId, success);
            return Response.<RefundOrderResponseDTO>builder()
                    .code(Constants.ResponseCode.SUCCESS.getCode())
                    .info(Constants.ResponseCode.SUCCESS.getInfo())
                    .data(responseDTO)
                    .build();
        } catch (Exception e) {
            log.error("用户退单失败 userId:{} orderId:{}", requestDTO.getUserId(), requestDTO.getOrderId(), e);
            
            RefundOrderResponseDTO responseDTO = new RefundOrderResponseDTO();
            responseDTO.setSuccess(false);
            responseDTO.setOrderId(requestDTO.getOrderId());
            responseDTO.setMessage("退单失败，系统异常");
            
            return Response.<RefundOrderResponseDTO>builder()
                    .code(Constants.ResponseCode.UN_ERROR.getCode())
                    .info(Constants.ResponseCode.UN_ERROR.getInfo())
                    .data(responseDTO)
                    .build();
        }
    }

    /**
     * 测试回调接口 - 主动查询支付宝交易状态
     * @param outTradeNo 商户订单号
     * @return 处理结果
     */
    @RequestMapping(value = "active_pay_notify", method = RequestMethod.POST)
    public Response<String> activePayNotify(@RequestParam String outTradeNo) {
        try {
            log.info("测试回调接口，开始查询订单: {}", outTradeNo);
            
            // 构建支付宝交易查询请求
            AlipayTradeQueryModel bizModel = new AlipayTradeQueryModel();
            bizModel.setOutTradeNo(outTradeNo);
            
            AlipayTradeQueryRequest queryRequest = new AlipayTradeQueryRequest();
            queryRequest.setBizModel(bizModel);
            
            // 调用支付宝API查询交易状态
            String body = alipayClient.execute(queryRequest).getBody();
            log.info("支付宝查询结果: {}", body);
            
            // 解析查询结果
            JSONObject responseJson = JSON.parseObject(body);
            JSONObject queryResponse = responseJson.getJSONObject("alipay_trade_query_response");
            
            if (queryResponse != null && "10000".equals(queryResponse.getString("code"))) {
                String tradeStatus = queryResponse.getString("trade_status");
                String tradeNo = queryResponse.getString("trade_no");
                String totalAmount = queryResponse.getString("total_amount");
                String gmtPayment = queryResponse.getString("send_pay_date");
                
                log.info("查询成功 - 交易状态: {}, 支付宝交易号: {}, 金额: {}, 支付时间: {}", 
                        tradeStatus, tradeNo, totalAmount, gmtPayment);
                
                // 如果交易成功，执行后续流程处理
                if ("TRADE_SUCCESS".equals(tradeStatus)) {
                    log.info("交易成功，开始处理后续流程，订单号: {}", outTradeNo);
                    
                    // 调用订单服务更新订单状态
                    orderService.changeOrderPaySuccess(outTradeNo, 
                            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(gmtPayment));
                    
                    log.info("订单状态更新成功，订单号: {}", outTradeNo);
                    
                    return Response.<String>builder()
                            .code(Constants.ResponseCode.SUCCESS.getCode())
                            .info(Constants.ResponseCode.SUCCESS.getInfo())
                            .data("交易成功，订单状态已更新")
                            .build();
                } else {
                    log.info("交易状态非成功状态: {}, 订单号: {}", tradeStatus, outTradeNo);
                    return Response.<String>builder()
                            .code(Constants.ResponseCode.SUCCESS.getCode())
                            .info(Constants.ResponseCode.SUCCESS.getInfo())
                            .data("交易状态: " + tradeStatus)
                            .build();
                }
            } else {
                String errorMsg = queryResponse != null ? queryResponse.getString("msg") : "查询失败";
                log.error("支付宝查询失败: {}, 订单号: {}", errorMsg, outTradeNo);
                return Response.<String>builder()
                        .code(Constants.ResponseCode.UN_ERROR.getCode())
                        .info(Constants.ResponseCode.UN_ERROR.getInfo())
                        .data("查询失败: " + errorMsg)
                        .build();
            }
            
        } catch (Exception e) {
            log.error("测试回调接口异常，订单号: {}", outTradeNo, e);
            return Response.<String>builder()
                    .code(Constants.ResponseCode.UN_ERROR.getCode())
                    .info(Constants.ResponseCode.UN_ERROR.getInfo())
                    .data("系统异常: " + e.getMessage())
                    .build();
        }
    }

}

