package com.aipintuan.infrastructure.gateway;

import com.aipintuan.domain.catalog.adapter.repository.IProductCatalogRepository;
import com.aipintuan.domain.catalog.model.entity.CatalogProductEntity;
import com.aipintuan.infrastructure.gateway.dto.ProductDTO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;

@Service
public class ProductRPC {

    @Resource
    private IProductCatalogRepository productCatalogRepository;

    public ProductDTO queryProductByProductId(String productId){
        CatalogProductEntity catalogProduct = productCatalogRepository.queryProductById(productId);
        ProductDTO productVO = new ProductDTO();
        productVO.setProductId(productId);
        if (catalogProduct == null) {
            productVO.setProductName("商品" + productId);
            productVO.setProductDesc("商品" + productId);
            productVO.setPrice(new BigDecimal("100.00"));
            return productVO;
        }
        productVO.setProductName(catalogProduct.getProductName());
        productVO.setProductDesc(catalogProduct.getProductDesc());
        productVO.setPrice(catalogProduct.getOriginalPrice());
        return productVO;
    }

}
