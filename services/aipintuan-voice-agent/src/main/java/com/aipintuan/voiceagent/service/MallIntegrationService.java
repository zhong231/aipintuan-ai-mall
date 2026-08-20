package com.aipintuan.voiceagent.service;

import com.aipintuan.voiceagent.dto.*;
import com.aipintuan.voiceagent.entity.AppUserEntity;
import com.aipintuan.voiceagent.entity.ProductEntity;
import com.aipintuan.voiceagent.repository.AppUserRepository;
import com.aipintuan.voiceagent.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class MallIntegrationService {

    private static final long MALL_MERCHANT_ID = 100L;

    private final JdbcTemplate jdbc;
    private final AppUserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProductVectorService productVectorService;
    private final SessionService sessionService;
    private final SessionScopeCache scopeCache;

    @Transactional
    public MallSessionResponse register(MallSessionRequest request) {
        require(request.sessionId(), "sessionId");
        require(request.mallUserId(), "mallUserId");
        require(request.goodsId(), "goodsId");
        require(request.productName(), "productName");

        ensureMerchant();
        AppUserEntity user = ensureUser(request.mallUserId());
        ProductEntity product = upsertProduct(new MallCatalogProductRequest(
                request.goodsId(), request.productName(), request.description(), request.category(),
                request.activityId(), request.originalPrice(), request.groupPrice(), request.imageUrl(),
                "限时拼团", request.groupUserCount()), request.teamCount(), request.groupUserCount());

        sessionService.openIfAbsent(request.sessionId(), user.getId(), "PRODUCT_PAGE");
        scopeCache.put(request.sessionId(),
                new SessionScope(user.getId(), List.of(MALL_MERCHANT_ID), product.getId()));

        return new MallSessionResponse(request.sessionId(), user.getId(), product.getId());
    }

    @Transactional
    public MallCatalogSessionResponse registerCatalog(MallCatalogSessionRequest request) {
        require(request.sessionId(), "sessionId");
        require(request.mallUserId(), "mallUserId");
        if (request.products() == null || request.products().isEmpty()) {
            throw new IllegalArgumentException("products 不能为空");
        }

        ensureMerchant();
        AppUserEntity user = ensureUser(request.mallUserId());
        int count = 0;
        for (MallCatalogProductRequest product : request.products()) {
            require(product.productId(), "productId");
            require(product.productName(), "productName");
            upsertProduct(product, null, product.participantCount());
            count++;
        }

        sessionService.openIfAbsent(request.sessionId(), user.getId(), "MALL_HOME");
        scopeCache.put(request.sessionId(),
                new SessionScope(user.getId(), List.of(MALL_MERCHANT_ID), null));
        return new MallCatalogSessionResponse(request.sessionId(), user.getId(), count);
    }

    private void ensureMerchant() {
        jdbc.update("""
                INSERT INTO merchant (id, name, status, scale_level, contact_email)
                VALUES (?, '爱拼团商城', 'ACTIVE', 'HEAD', 'local@mall.demo')
                ON CONFLICT (id) DO UPDATE SET name = EXCLUDED.name, status = 'ACTIVE'
                """, MALL_MERCHANT_ID);
    }

    private AppUserEntity ensureUser(String mallUserId) {
        String rawUsername = "mall:" + mallUserId;
        String username = rawUsername.substring(0, Math.min(64, rawUsername.length()));
        return userRepository.findByUsernameAndStatus(username, "ACTIVE")
                .orElseGet(() -> {
                    AppUserEntity created = new AppUserEntity();
                    created.setUsername(username);
                    created.setNickname("商城用户-" + mallUserId);
                    return userRepository.save(created);
                });
    }

    private ProductEntity upsertProduct(MallCatalogProductRequest request,
                                        Integer teamCount, Integer groupUserCount) {
        String skuCode = "MALL-" + request.productId();
        ProductEntity product = productRepository.findBySkuCode(skuCode).orElseGet(ProductEntity::new);
        String productDescription = description(request);
        boolean reindex = product.getId() == null
                || !Objects.equals(product.getName(), request.productName())
                || !Objects.equals(product.getDescription(), productDescription)
                || !Objects.equals(product.getCategoryL1(), category(request));

        String category = category(request);
        product.setMerchantId(MALL_MERCHANT_ID);
        product.setSkuCode(skuCode);
        product.setName(request.productName());
        product.setCategoryL1(category);
        product.setCategoryL2(categoryL2(request));
        product.setBrand("爱拼团");
        product.setPrice(defaultPrice(request.groupPrice(), request.originalPrice()));
        product.setOriginalPrice(defaultPrice(request.originalPrice(), request.groupPrice()));
        product.setStock(999);
        product.setDescription(productDescription);
        product.setSellingPoints("真实商城商品／支持单独购买／支持拼团优惠");
        product.setStatus("ON_SALE");
        product.setIsNewArrival(false);

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("mallGoodsId", request.productId());
        attributes.put("mallSource", "aipintuan-mall");
        attributes.put("groupBuyAvailable", true);
        attributes.put("groupPrice", request.groupPrice());
        attributes.put("originalPrice", request.originalPrice());
        attributes.put("activityId", request.activityId());
        attributes.put("category", category);
        attributes.put("imageUrl", request.imageUrl());
        attributes.put("badge", request.badge());
        attributes.put("participantCount", request.participantCount());
        attributes.put("teamCount", teamCount);
        attributes.put("groupUserCount", groupUserCount);
        product.setAttributes(attributes);

        product = productRepository.saveAndFlush(product);
        if (reindex) productVectorService.upsertEmbedding(product);
        return product;
    }

    private static String description(MallCatalogProductRequest request) {
        String base = request.productDesc() == null || request.productDesc().isBlank()
                ? request.productName() : request.productDesc();
        return base + "。爱拼团商城商品ID " + request.productId()
                + "，可单独购买，也可参加拼团享受优惠价。";
    }

    private static String category(MallCatalogProductRequest request) {
        return request.category() == null || request.category().isBlank() ? "百货" : request.category();
    }

    private static String categoryL2(MallCatalogProductRequest request) {
        String category = category(request);
        String text = (request.productName() + " " + Objects.toString(request.productDesc(), "")).toLowerCase();
        if (text.contains("跑鞋") || text.contains("慢跑")) return "跑鞋";
        if (text.contains("键盘")) return "键盘";
        if (text.contains("耳机")) return "耳机";
        if (text.contains("咖啡")) return "咖啡";
        if (text.contains("mybatis") || category.contains("图书")) return "编程图书";
        return category;
    }

    private static BigDecimal defaultPrice(BigDecimal primary, BigDecimal fallback) {
        if (primary != null) return primary;
        if (fallback != null) return fallback;
        return BigDecimal.ZERO;
    }

    private static void require(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " 不能为空");
        }
    }
}
