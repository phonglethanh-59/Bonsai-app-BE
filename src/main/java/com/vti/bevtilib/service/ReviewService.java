package com.vti.bevtilib.service;

import com.vti.bevtilib.dto.ReviewDTO;
import com.vti.bevtilib.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReviewService {
    List<ReviewDTO> getReviewsForProduct(Long productId);
    Page<ReviewDTO> getReviewsForProduct(Long productId, Pageable pageable);
    ReviewDTO createReview(User user, Long productId, int rating, String comment);
}
