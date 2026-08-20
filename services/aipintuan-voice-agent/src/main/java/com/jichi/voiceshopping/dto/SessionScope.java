package com.jichi.voiceshopping.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

public record SessionScope(
        Long userId,
        List<Long> allowedMerchantIds,  // null = 全平台
        Long boundProductId             // 若从商详页进入，挂载的商品
) {
    @JsonIgnore
    public boolean isPlatformWide() {
        return allowedMerchantIds == null || allowedMerchantIds.isEmpty();
    }
}
