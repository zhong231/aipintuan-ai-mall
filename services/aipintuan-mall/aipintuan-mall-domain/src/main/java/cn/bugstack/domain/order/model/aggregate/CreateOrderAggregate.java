package cn.bugstack.domain.order.model.aggregate;

import cn.bugstack.domain.order.model.entity.OrderEntity;
import cn.bugstack.domain.order.model.entity.ProductEntity;
import cn.bugstack.domain.order.model.valobj.OrderStatusVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderAggregate {

    private String userId;

    private ProductEntity productEntity;

    private OrderEntity orderEntity;

    public static OrderEntity buildOrderEntity(String productId, String productName, Integer marketType){
        return OrderEntity.builder()
                .productId(productId)
                .productName(productName)
                .orderId(RandomStringUtils.randomNumeric(12))
                .orderTime(new Date())
                .orderStatusVO(OrderStatusVO.CREATE)
                .marketType(marketType)
                .build();
    }

}
