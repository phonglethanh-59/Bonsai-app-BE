package com.vti.bevtilib.controller;

import com.vti.bevtilib.dto.ReviewDTO;
import com.vti.bevtilib.model.User;
import com.vti.bevtilib.service.ReviewService;
import com.vti.bevtilib.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final UserService userService;

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ReviewDTO>> getReviewsByProductId(@PathVariable Long productId) {
        List<ReviewDTO> reviews = reviewService.getReviewsForProduct(productId);
        return ResponseEntity.ok(reviews);
    }

    @PostMapping
    public ResponseEntity<?> createReview(@RequestBody Map<String, Object> payload, Authentication authentication) {
        try {
            User user = userService.findByUsername(authentication.getName())
                    .orElseThrow(() -> new Exception("Không tìm thấy người dùng"));
            Long productId = Long.valueOf(payload.get("productId").toString());
            int rating = Integer.parseInt(payload.get("rating").toString());
            String comment = (String) payload.get("comment");
            ReviewDTO review = reviewService.createReview(user, productId, rating, comment);
            return ResponseEntity.ok(Map.of("message", "Đánh giá thành công!", "review", review));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
