package com.aipintuan.voiceagent.dto;

import java.util.List;

public record MallCatalogSessionRequest(
        String sessionId,
        String mallUserId,
        List<MallCatalogProductRequest> products
) {}
