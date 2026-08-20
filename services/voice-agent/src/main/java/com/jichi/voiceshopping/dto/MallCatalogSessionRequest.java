package com.jichi.voiceshopping.dto;

import java.util.List;

public record MallCatalogSessionRequest(
        String sessionId,
        String mallUserId,
        List<MallCatalogProductRequest> products
) {}
