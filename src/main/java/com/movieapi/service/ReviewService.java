package com.movieapi.service;

import com.movieapi.dto.ReviewRequest;
import com.movieapi.model.Movie;
import com.movieapi.model.Review;
import com.movieapi.model.User;
import com.movieapi.repository.MovieRepository;
import com.movieapi.repository.ReviewRepository;
import com.movieapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private UserRepository userRepository;

    // 1. Fetch all reviews for a specific movie
    public List<Review> getReviewsByMovie(Long movieId) {
        return reviewRepository.findByMovieId(movieId);
    }

    // 2. Add a new review
    public Review addReview(Long movieId, ReviewRequest reviewRequest) {
        // Step 1: Find the Movie
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Movie not found with id: " + movieId));

        // Step 2: Find the logged-in User
        // We get the username directly from the Spring Security Context (which was populated by our JWT Filter!)
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        // Step 3: Create the Review object
        Review review = new Review();
        review.setRating(reviewRequest.getRating());
        review.setComment(reviewRequest.getComment());
        review.setMovie(movie); // Link the movie
        review.setUser(user);   // Link the user

        // Step 4: Save to database
        return reviewRepository.save(review);
    }
}
