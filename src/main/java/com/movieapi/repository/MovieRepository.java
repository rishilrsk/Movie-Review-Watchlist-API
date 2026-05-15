package com.movieapi.repository;

import com.movieapi.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    // JpaRepository already gives us save(), findAll(), findById(), and deleteById() for free!
}
