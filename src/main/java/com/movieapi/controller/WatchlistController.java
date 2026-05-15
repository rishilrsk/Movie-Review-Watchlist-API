package com.movieapi.controller;

import com.movieapi.model.Watchlist;
import com.movieapi.service.WatchlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/watchlist")
public class WatchlistController {

    @Autowired
    private WatchlistService watchlistService;

    // 1. GET personal watchlist
    // Notice how we don't pass a userId in the URL? It's fully extracted from the JWT token!
    @GetMapping
    public ResponseEntity<List<Watchlist>> getMyWatchlist() {
        return ResponseEntity.ok(watchlistService.getMyWatchlist());
    }

    // 2. POST to add a movie to the watchlist
    @PostMapping("/{movieId}")
    public ResponseEntity<?> addToWatchlist(@PathVariable Long movieId) {
        try {
            Watchlist savedEntry = watchlistService.addToWatchlist(movieId);
            return ResponseEntity.ok(savedEntry);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 3. DELETE to remove a movie from the watchlist
    @DeleteMapping("/{movieId}")
    public ResponseEntity<?> removeFromWatchlist(@PathVariable Long movieId) {
        try {
            watchlistService.removeFromWatchlist(movieId);
            return ResponseEntity.ok("Movie removed from your watchlist!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
