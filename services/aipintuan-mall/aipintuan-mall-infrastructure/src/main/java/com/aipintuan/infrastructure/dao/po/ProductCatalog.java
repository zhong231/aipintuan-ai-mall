package com.aipintuan.infrastructure.dao.po;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductCatalog {

    private Long id;
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
    private Integer status;
    private Integer sortOrder;
}
