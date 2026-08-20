package com.aipintuan.infrastructure.adapter.repository;

import com.aipintuan.domain.catalog.adapter.repository.IProductCatalogRepository;
import com.aipintuan.domain.catalog.model.entity.CatalogProductEntity;
import com.aipintuan.infrastructure.dao.IProductCatalogDao;
import com.aipintuan.infrastructure.dao.po.ProductCatalog;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ProductCatalogRepository implements IProductCatalogRepository {

    @Resource
    private IProductCatalogDao productCatalogDao;

    @Override
    public List<CatalogProductEntity> queryProducts(String keyword, String category, BigDecimal maxPrice) {
        List<ProductCatalog> products = productCatalogDao.queryProducts(keyword, category, maxPrice);
        if (products == null) return Collections.emptyList();
        return products.stream().map(this::toEntity).collect(Collectors.toList());
    }

    @Override
    public CatalogProductEntity queryProductById(String productId) {
        return toEntity(productCatalogDao.queryProductById(productId));
    }

    private CatalogProductEntity toEntity(ProductCatalog product) {
        if (product == null) return null;
        return CatalogProductEntity.builder()
                .productId(product.getProductId())
                .productName(product.getProductName())
                .productDesc(product.getProductDesc())
                .category(product.getCategory())
                .activityId(product.getActivityId())
                .originalPrice(product.getOriginalPrice())
                .groupPrice(product.getGroupPrice())
                .imageUrl(product.getImageUrl())
                .badge(product.getBadge())
                .participantCount(product.getParticipantCount())
                .build();
    }
}
