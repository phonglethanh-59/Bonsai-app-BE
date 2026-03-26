package com.vti.bevtilib.repository;

import com.vti.bevtilib.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProduct_IdOrderByReviewDateDesc(Long productId);
    Page<Review> findByProduct_IdOrderByReviewDateDesc(Long productId, Pageable pageable);
    boolean existsByProduct_IdAndUser_UserId(Long productId, String userId);

    @Query("SELECT COALESCE(AVG(r.rating), 0.0) FROM Review r WHERE r.product.id = :productId")
    double averageRatingByProductId(@Param("productId") Long productId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.product.id = :productId")
    int countByProductId(@Param("productId") Long productId);
}
