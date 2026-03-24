package com.vti.bevtilib.service;

import com.vti.bevtilib.dto.ReviewDTO;
import com.vti.bevtilib.model.Product;
import com.vti.bevtilib.model.Review;
import com.vti.bevtilib.model.User;
import com.vti.bevtilib.repository.ProductRepository;
import com.vti.bevtilib.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;

    @Override
    public List<ReviewDTO> getReviewsForProduct(Long productId) {
        return reviewRepository.findByProduct_IdOrderByReviewDateDesc(productId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ReviewDTO createReview(User user, Long productId, int rating, String comment) throws Exception {
        if (reviewRepository.existsByProduct_IdAndUser_UserId(productId, user.getUserId())) {
            throw new Exception("Bạn đã đánh giá sản phẩm này rồi.");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new Exception("Không tìm thấy sản phẩm."));

        Review review = new Review();
        review.setUser(user);
        review.setProduct(product);
        review.setRating(rating);
        review.setComment(comment);

        Review savedReview = reviewRepository.save(review);

        // Cập nhật rating trung bình cho sản phẩm
        List<Review> allReviews = reviewRepository.findByProduct_IdOrderByReviewDateDesc(productId);
        double avgRating = allReviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
        product.setAverageRating(avgRating);
        product.setReviewCount(allReviews.size());
        productRepository.save(product);

        return convertToDto(savedReview);
    }

    private ReviewDTO convertToDto(Review review) {
        ReviewDTO dto = new ReviewDTO();
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());

        String reviewerName = (review.getUser().getUserDetail() != null && review.getUser().getUserDetail().getFullName() != null)
                ? review.getUser().getUserDetail().getFullName()
                : review.getUser().getUsername();
        dto.setReviewerName(reviewerName);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy 'lúc' HH:mm");
        dto.setReviewDate(review.getReviewDate().format(formatter));
        return dto;
    }
}
