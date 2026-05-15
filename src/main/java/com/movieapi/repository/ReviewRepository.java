package com.movieapi.repository;

import com.movieapi.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    // Spring Data JPA magic: We want all reviews for a specific movie ID.
    // Behind the scenes, Spring writes: SELECT * FROM reviews WHERE movie_id = ?
    List<Review> findByMovieId(Long movieId);
    
    // We can also find reviews by a specific user if we wanted to build a "My Reviews" page later!
    List<Review> findByUserId(Long userId);
}
