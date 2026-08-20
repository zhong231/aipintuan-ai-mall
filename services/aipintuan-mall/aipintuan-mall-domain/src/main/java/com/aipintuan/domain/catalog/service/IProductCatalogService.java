package com.aipintuan.domain.catalog.service;

import com.aipintuan.domain.catalog.model.entity.CatalogProductEntity;

import java.math.BigDecimal;
import java.util.List;

public interface IProductCatalogService {

    List<CatalogProductEntity> queryProducts(String keyword, String category, BigDecimal maxPrice);

    CatalogProductEntity queryProductById(String productId);
}
