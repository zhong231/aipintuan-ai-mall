package com.jichi.voiceshopping.dto;

import java.math.BigDecimal;

public record MallSessionRequest(
        String sessionId,
        String mallUserId,
        String goodsId,
        String productName,
        String description,
        String category,
        String imageUrl,
        BigDecimal originalPrice,
        BigDecimal groupPrice,
        Long activityId,
        Integer teamCount,
        Integer groupUserCount
) {}
