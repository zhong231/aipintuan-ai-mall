package com.aipintuan.trigger.http;

import com.aipintuan.api.response.Response;
import com.aipintuan.domain.catalog.model.entity.CatalogProductEntity;
import com.aipintuan.domain.catalog.service.IProductCatalogService;
import com.aipintuan.types.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/catalog")
public class ProductCatalogController {

    @Resource
    private IProductCatalogService productCatalogService;

    @GetMapping("/products")
    public Response<List<CatalogProductEntity>> products(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) BigDecimal maxPrice) {
        try {
            return Response.<List<CatalogProductEntity>>builder()
                    .code(Constants.ResponseCode.SUCCESS.getCode())
                    .info(Constants.ResponseCode.SUCCESS.getInfo())
                    .data(productCatalogService.queryProducts(keyword, category, maxPrice))
                    .build();
        } catch (Exception e) {
            log.error("查询商品目录失败 keyword:{} category:{} maxPrice:{}", keyword, category, maxPrice, e);
            return Response.<List<CatalogProductEntity>>builder()
                    .code(Constants.ResponseCode.UN_ERROR.getCode())
                    .info(Constants.ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @GetMapping("/products/{productId}")
    public Response<CatalogProductEntity> product(@PathVariable String productId) {
        CatalogProductEntity product = productCatalogService.queryProductById(productId);
        if (product == null) {
            return Response.<CatalogProductEntity>builder()
                    .code(Constants.ResponseCode.ILLEGAL_PARAMETER.getCode())
                    .info("商品不存在")
                    .build();
        }
        return Response.<CatalogProductEntity>builder()
                .code(Constants.ResponseCode.SUCCESS.getCode())
                .info(Constants.ResponseCode.SUCCESS.getInfo())
                .data(product)
                .build();
    }
}
