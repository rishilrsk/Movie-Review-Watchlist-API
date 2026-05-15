package com.movieapi.repository;

import com.movieapi.model.Watchlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WatchlistRepository extends JpaRepository<Watchlist, Long> {

    // Fetch all watchlist entries for a specific user
    List<Watchlist> findByUserId(Long userId);

    // Check if a specific movie is already in a specific user's watchlist
    Optional<Watchlist> findByUserIdAndMovieId(Long userId, Long movieId);
}
