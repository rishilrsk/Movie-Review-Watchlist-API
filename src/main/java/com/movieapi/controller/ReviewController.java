package com.movieapi.controller;

import com.movieapi.dto.ReviewRequest;
import com.movieapi.model.Review;
import com.movieapi.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies/{movieId}/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    // 1. GET all reviews for a movie (Public)
    @GetMapping
    public ResponseEntity<List<Review>> getReviewsForMovie(@PathVariable Long movieId) {
        return ResponseEntity.ok(reviewService.getReviewsByMovie(movieId));
    }

    // 2. POST a new review (Requires JWT Token)
    @PostMapping
    public ResponseEntity<?> addReview(@PathVariable Long movieId, @RequestBody ReviewRequest reviewRequest) {
        try {
            // Validate the rating is between 1 and 5
            if (reviewRequest.getRating() < 1 || reviewRequest.getRating() > 5) {
                return ResponseEntity.badRequest().body("Rating must be between 1 and 5 stars!");
            }
            
            Review savedReview = reviewService.addReview(movieId, reviewRequest);
            return ResponseEntity.ok(savedReview);
            
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}
