package cn.bugstack.domain.catalog.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CatalogProductEntity {

    private String productId;
    private String productName;
    private String productDesc;
    private String category;
    private Long activityId;
    private BigDecimal originalPrice;
    private BigDecimal groupPrice;
    private String imageUrl;
    private String badge;
    private Integer participantCount;
}
