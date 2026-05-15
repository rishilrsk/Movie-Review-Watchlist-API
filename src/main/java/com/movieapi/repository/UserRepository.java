package com.movieapi.repository;

import com.movieapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Spring Data JPA is magic. By simply naming the method "findByUsername",
    // it will automatically write the SQL query: SELECT * FROM users WHERE username = ?
    Optional<User> findByUsername(String username);

    // We'll also need this to check if an email or username already exists during registration
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
