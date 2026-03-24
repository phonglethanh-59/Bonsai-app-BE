package com.vti.bevtilib.service;

import com.vti.bevtilib.dto.ReviewDTO;
import com.vti.bevtilib.model.User;

import java.util.List;

public interface ReviewService {
    List<ReviewDTO> getReviewsForProduct(Long productId);
    ReviewDTO createReview(User user, Long productId, int rating, String comment) throws Exception;
}
