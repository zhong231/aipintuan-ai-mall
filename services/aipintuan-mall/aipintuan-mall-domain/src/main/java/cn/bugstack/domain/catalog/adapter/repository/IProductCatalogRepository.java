package cn.bugstack.domain.catalog.adapter.repository;

import cn.bugstack.domain.catalog.model.entity.CatalogProductEntity;

import java.math.BigDecimal;
import java.util.List;

public interface IProductCatalogRepository {

    List<CatalogProductEntity> queryProducts(String keyword, String category, BigDecimal maxPrice);

    CatalogProductEntity queryProductById(String productId);
}
