package com.movieapi.service;

import com.movieapi.model.Movie;
import com.movieapi.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieService {

    @Autowired
    private MovieRepository movieRepository;

    // Create
    public Movie addMovie(Movie movie) {
        return movieRepository.save(movie);
    }

    // Read (All)
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    // Read (One by ID)
    public Movie getMovieById(Long id) {
        // If we can't find the movie, we throw an exception
        return movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found with id: " + id));
    }

    // Update
    public Movie updateMovie(Long id, Movie updatedMovieDetails) {
        // First, check if it exists
        Movie existingMovie = getMovieById(id);
        
        // Then, update the fields
        existingMovie.setTitle(updatedMovieDetails.getTitle());
        existingMovie.setDescription(updatedMovieDetails.getDescription());
        existingMovie.setGenre(updatedMovieDetails.getGenre());
        
        // Save the updated object back to the database
        return movieRepository.save(existingMovie);
    }

    // Delete
    public void deleteMovie(Long id) {
        // First, make sure it exists (throws error if not)
        Movie movie = getMovieById(id);
        movieRepository.delete(movie);
    }
}
