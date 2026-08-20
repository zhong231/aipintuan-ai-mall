package com.aipintuan.voiceagent.dto;

public record MallCatalogSessionResponse(
        String sessionId,
        Long agentUserId,
        int indexedProductCount
) {}
