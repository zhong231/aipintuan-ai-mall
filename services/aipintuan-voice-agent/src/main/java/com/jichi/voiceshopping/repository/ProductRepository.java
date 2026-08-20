package com.jichi.voiceshopping.repository;

import com.jichi.voiceshopping.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    List<ProductEntity> findByStatus(String status);

    List<ProductEntity> findByIdIn(List<Long> ids);

    Optional<ProductEntity> findBySkuCode(String skuCode);

    @Query("SELECT p FROM ProductEntity p WHERE p.id IN :ids AND " +
            "(:merchantIds IS NULL OR p.merchantId IN :merchantIds)")
    List<ProductEntity> findByIdInWithScope(@Param("ids") List<Long> ids,
                                            @Param("merchantIds") List<Long> merchantIds);

}
