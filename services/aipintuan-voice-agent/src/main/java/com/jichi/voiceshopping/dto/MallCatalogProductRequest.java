package com.jichi.voiceshopping.dto;

import java.math.BigDecimal;

public record MallCatalogProductRequest(
        String productId,
        String productName,
        String productDesc,
        String category,
        Long activityId,
        BigDecimal originalPrice,
        BigDecimal groupPrice,
        String imageUrl,
        String badge,
        Integer participantCount
) {}
