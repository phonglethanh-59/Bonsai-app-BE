package com.vti.bevtilib.repository;

import com.vti.bevtilib.model.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    List<ProductImage> findByProductIdOrderByDisplayOrderAsc(Long productId);

    @Query("SELECT COALESCE(MAX(pi.displayOrder), -1) FROM ProductImage pi WHERE pi.product.id = :productId")
    int findMaxDisplayOrder(@Param("productId") Long productId);

    void deleteByProductId(Long productId);
}
