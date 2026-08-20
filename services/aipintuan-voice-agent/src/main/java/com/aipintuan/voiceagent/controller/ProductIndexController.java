package com.aipintuan.voiceagent.controller;

import com.aipintuan.voiceagent.entity.FaqEntryEntity;
import com.aipintuan.voiceagent.entity.ProductEntity;
import com.aipintuan.voiceagent.repository.FaqEntryRepository;
import com.aipintuan.voiceagent.repository.ProductRepository;
import com.aipintuan.voiceagent.service.FaqVectorService;
import com.aipintuan.voiceagent.service.ProductVectorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class ProductIndexController {

    private final ProductRepository repo;
    private final ProductVectorService vector;
    private final FaqEntryRepository faqRepo;
    private final FaqVectorService faqVector;

    @PostMapping("/reindex")
    public Map<String, Object> reindex() {
        List<ProductEntity> all = repo.findByStatus("ON_SALE");
        int ok = 0, fail = 0;
        for (ProductEntity p : all) {
            try {
                vector.upsertEmbedding(p);
                ok++;
            } catch (Exception e) {
                fail++;
                log.warn("向量化失败 productId={}: {}", p.getId(), e.getMessage());
            }
        }
        return Map.of("total", all.size(), "indexed", ok, "failed", fail);
    }



    @PostMapping("/faq-reindex")
    public Map<String, Object> faqReindex() {
        List<FaqEntryEntity> all = faqRepo.findAll()
                .stream()
                .filter(f -> "PUBLISHED".equals(f.getStatus()))
                .toList();
        int ok = 0, fail = 0;
        for (FaqEntryEntity f : all) {
            try {
                faqVector.upsertEmbedding(f);
                ok++;
            } catch (Exception e) {
                fail++;
                log.warn("FAQ 向量化失败 id={}: {}", f.getId(), e.getMessage());
            }
        }
        return Map.of("total", all.size(), "indexed", ok, "failed", fail);
    }
}