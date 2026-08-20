package com.jichi.voiceshopping.dto;

public record MallSessionResponse(
        String sessionId,
        Long agentUserId,
        Long agentProductId
) {}
