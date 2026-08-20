package com.aipintuan.voiceagent.service;

import com.aipintuan.voiceagent.dto.RecommendedItem;
import com.aipintuan.voiceagent.dto.SessionScope;
import com.aipintuan.voiceagent.entity.ProductEntity;
import com.aipintuan.voiceagent.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class RecommendCandidatesService {

    private final ProductVectorService vector;
    private final ProductRepository repo;
    private final ScopeFilterBuilder scopeFilterBuilder; // 新增

    public List<RecommendedItem> fetchCandidates(String query, Map<String, Object> slots,
                                                 SessionScope scope, int topN) {
        // 从商城商品详情页进入时，当前商品就是唯一可信的商品上下文。
        // 不再让向量检索从 Agent 演示商品库里推荐鞋子等无关商品。
        if (scope != null && scope.boundProductId() != null) {
            return repo.findById(scope.boundProductId())
                    .filter(p -> "ON_SALE".equals(p.getStatus()))
                    .map(p -> List.of(new RecommendedItem(
                            p.getId(), p.getName(), p.getPrice(), null, 1.0,
                            p.getAttributes() == null ? Map.of() : p.getAttributes())))
                    .orElseGet(List::of);
        }

        SqlFilterBuilder.Filter f = SqlFilterBuilder.fromSlots(slots);
        if ("跑鞋".equals(slots.get("category"))) {
            f = SqlFilterBuilder.merge(f, SqlFilterBuilder.runningShoeFilter(slots));
        }
        // 叠加 scope 过滤（platformWide 时返回空片段，不影响原逻辑）
        f = SqlFilterBuilder.merge(f, scopeFilterBuilder.build(scope));

        List<Long> ids = vector.search(query, f.clause(), f.params(), topN);
        if (ids.isEmpty()) return List.of();

        // 详情查询也带 scope：即使向量库脏数据漏出别家商品，PG 这层再兜一刀
        List<Long> allowed = (scope == null || scope.isPlatformWide())
                ? null : scope.allowedMerchantIds();
        Map<Long, ProductEntity> map = new HashMap<>();
        repo.findByIdInWithScope(ids, allowed).forEach(p -> map.put(p.getId(), p));

        // 下面组装 RecommendedItem 的逻辑保持不变
        List<RecommendedItem> out = new ArrayList<>();
        for (int i = 0; i < ids.size(); i++) {
            ProductEntity p = map.get(ids.get(i));
            if (p == null) continue;  // 被 scope 过滤掉的 id，这里自然丢弃
            out.add(new RecommendedItem(p.getId(), p.getName(), p.getPrice(),
                    null, 1.0 - i * 0.03,
                    p.getAttributes() == null ? Map.of() : p.getAttributes()));
        }
        return out;
    }
}
