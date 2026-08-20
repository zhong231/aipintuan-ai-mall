package com.jichi.voiceshopping.dto;

public record MallCatalogSessionResponse(
        String sessionId,
        Long agentUserId,
        int indexedProductCount
) {}
