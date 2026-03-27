package com.vti.bevtilib.controller;

import com.vti.bevtilib.dto.CreateReviewRequest;
import com.vti.bevtilib.dto.ReviewDTO;
import com.vti.bevtilib.dto.UpdateReviewRequest;
import com.vti.bevtilib.exception.ResourceNotFoundException;
import com.vti.bevtilib.model.User;
import com.vti.bevtilib.service.ReviewService;
import com.vti.bevtilib.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final UserService userService;

    @GetMapping("/product/{productId}")
    public ResponseEntity<Page<ReviewDTO>> getReviewsByProductId(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100));
        Page<ReviewDTO> reviews = reviewService.getReviewsForProduct(productId, pageable);
        return ResponseEntity.ok(reviews);
    }

    @PostMapping
    public ResponseEntity<?> createReview(@Valid @RequestBody CreateReviewRequest request, Authentication authentication) {
        User user = getUser(authentication);
        ReviewDTO review = reviewService.createReview(user, request.getProductId(), request.getRating(), request.getComment());
        return ResponseEntity.ok(Map.of("message", "Đánh giá thành công!", "review", review));
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<?> updateReview(@PathVariable Long reviewId,
                                          @Valid @RequestBody UpdateReviewRequest request,
                                          Authentication authentication) {
        User user = getUser(authentication);
        ReviewDTO review = reviewService.updateReview(reviewId, user, request.getRating(), request.getComment());
        return ResponseEntity.ok(Map.of("message", "Cập nhật đánh giá thành công!", "review", review));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable Long reviewId, Authentication authentication) {
        User user = getUser(authentication);
        reviewService.deleteReview(reviewId, user);
        return ResponseEntity.ok(Map.of("message", "Xóa đánh giá thành công!"));
    }

    private User getUser(Authentication authentication) {
        return userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));
    }
}
