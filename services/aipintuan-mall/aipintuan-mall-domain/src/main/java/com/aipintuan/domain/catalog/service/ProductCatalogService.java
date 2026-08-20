package com.aipintuan.domain.catalog.service;

import com.aipintuan.domain.catalog.adapter.repository.IProductCatalogRepository;
import com.aipintuan.domain.catalog.model.entity.CatalogProductEntity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductCatalogService implements IProductCatalogService {

    @Resource
    private IProductCatalogRepository repository;

    @Override
    public List<CatalogProductEntity> queryProducts(String keyword, String category, BigDecimal maxPrice) {
        return repository.queryProducts(keyword, category, maxPrice);
    }

    @Override
    public CatalogProductEntity queryProductById(String productId) {
        return repository.queryProductById(productId);
    }
}
