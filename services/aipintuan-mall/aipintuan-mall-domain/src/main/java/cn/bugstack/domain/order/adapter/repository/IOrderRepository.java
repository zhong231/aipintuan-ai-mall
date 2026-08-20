package cn.bugstack.domain.order.adapter.repository;

import cn.bugstack.domain.order.model.aggregate.CreateOrderAggregate;
import cn.bugstack.domain.order.model.entity.OrderEntity;
import cn.bugstack.domain.order.model.entity.PayOrderEntity;
import cn.bugstack.domain.order.model.entity.ShopCartEntity;

import java.util.Date;
import java.util.List;

public interface IOrderRepository {
    void doSaveOrder(CreateOrderAggregate orderAggregate);

    OrderEntity queryUnPayOrder(ShopCartEntity shopCartEntity);

    void updateOrderPayInfo(PayOrderEntity payOrderEntity);

    void changeOrderPaySuccess(String orderId, Date payTime);

    void changeMarketOrderPaySuccess(String orderId);

    List<String> queryNoPayNotifyOrder();

    List<String> queryTimeoutCloseOrderList();

    boolean changeOrderClose(String orderId);

    void changeOrderMarketSettlement(List<String> outTradeNoList);

    OrderEntity queryOrderByOrderId(String orderId);

    List<OrderEntity> queryUserOrderList(String userId, Long lastId, Integer pageSize);

    OrderEntity queryOrderByUserIdAndOrderId(String userId, String orderId);

    boolean refundOrder(String userId, String orderId);

    boolean refundMarketOrder(String userId, String orderId);

}
