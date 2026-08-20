package com.aipintuan.voiceagent.dto;

public record StartSessionRequest(
        String sessionId,
        Channel channel,
        Long merchantId,        // MERCHANT_HOME 必填
        Long boundProductId     // PRODUCT_PAGE 必填
) {}