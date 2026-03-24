package com.vti.bevtilib.repository;

import com.vti.bevtilib.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProduct_IdOrderByReviewDateDesc(Long productId);
    boolean existsByProduct_IdAndUser_UserId(Long productId, String userId);
}
