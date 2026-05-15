package com.movieapi.service;

import com.movieapi.model.Movie;
import com.movieapi.model.User;
import com.movieapi.model.Watchlist;
import com.movieapi.repository.MovieRepository;
import com.movieapi.repository.UserRepository;
import com.movieapi.repository.WatchlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WatchlistService {

    @Autowired
    private WatchlistRepository watchlistRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private UserRepository userRepository;

    // Helper method to get the currently logged-in user securely from the JWT Token
    private User getLoggedInUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }

    // 1. Get the user's personal watchlist
    public List<Watchlist> getMyWatchlist() {
        User user = getLoggedInUser();
        return watchlistRepository.findByUserId(user.getId());
    }

    // 2. Add a movie to the watchlist
    public Watchlist addToWatchlist(Long movieId) {
        User user = getLoggedInUser();
        
        // Ensure the movie exists
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Movie not found with id: " + movieId));

        // Check if it's already on their watchlist to prevent duplicates
        Optional<Watchlist> existingEntry = watchlistRepository.findByUserIdAndMovieId(user.getId(), movieId);
        if (existingEntry.isPresent()) {
            throw new RuntimeException("Movie is already in your watchlist!");
        }

        Watchlist watchlist = new Watchlist();
        watchlist.setUser(user);
        watchlist.setMovie(movie);
        
        return watchlistRepository.save(watchlist);
    }

    // 3. Remove a movie from the watchlist
    public void removeFromWatchlist(Long movieId) {
        User user = getLoggedInUser();
        
        // Find the specific watchlist entry for this user and this movie
        Watchlist watchlist = watchlistRepository.findByUserIdAndMovieId(user.getId(), movieId)
                .orElseThrow(() -> new RuntimeException("Movie is not in your watchlist!"));
                
        watchlistRepository.delete(watchlist);
    }
}
