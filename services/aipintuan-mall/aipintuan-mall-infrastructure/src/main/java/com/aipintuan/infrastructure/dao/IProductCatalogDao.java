package com.aipintuan.infrastructure.dao;

import com.aipintuan.infrastructure.dao.po.ProductCatalog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface IProductCatalogDao {

    List<ProductCatalog> queryProducts(@Param("keyword") String keyword,
                                       @Param("category") String category,
                                       @Param("maxPrice") BigDecimal maxPrice);

    ProductCatalog queryProductById(@Param("productId") String productId);
}
