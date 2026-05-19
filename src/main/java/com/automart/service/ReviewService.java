package com.automart.service;

import com.automart.model.Review;
import com.automart.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    // --- CREATE ---
    public Review addReview(Long reviewerId, String reviewerUsername, Long sellerId, String sellerUsername,
                            int rating, String comment) {
        Review review = new Review(reviewerId, reviewerUsername, sellerId, sellerUsername, rating, comment);
        return reviewRepository.save(review);
    }

    // --- READ ---
    public Optional<Review> findById(Long id) { return reviewRepository.findById(id); }
    public List<Review> findBySellerId(Long sellerId) { return reviewRepository.findBySellerIdOrderByCreatedAtDesc(sellerId); }
    public List<Review> findByReviewerId(Long reviewerId) { return reviewRepository.findByReviewerIdOrderByCreatedAtDesc(reviewerId); }
    public Double getAverageRating(Long sellerId) {
        Double avg = reviewRepository.findAverageRatingBySellerId(sellerId);
        return avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0;
    }
    public long getReviewCount(Long sellerId) { return reviewRepository.countBySellerId(sellerId); }

    // --- UPDATE ---
    public Review updateReview(Long id, int rating, String comment) {
        Review review = reviewRepository.findById(id).orElseThrow(() -> new RuntimeException("Review not found"));
        review.setRating(rating);
        review.setComment(comment);
        return reviewRepository.save(review);
    }

    // --- DELETE ---
    public void deleteReview(Long id) { reviewRepository.deleteById(id); }
}
