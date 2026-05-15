package com.movieapi.controller;

import com.movieapi.dto.AuthRequest;
import com.movieapi.dto.AuthResponse;
import com.movieapi.dto.RegisterRequest;
import com.movieapi.model.User;
import com.movieapi.repository.UserRepository;
import com.movieapi.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth") // Base URL for all endpoints in this controller
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 1. REGISTRATION ENDPOINT
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest request) {
        
        // Check if username or email already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest().body("Error: Username is already taken!");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body("Error: Email is already in use!");
        }

        // Create a new User object
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        
        // NEVER save a plain text password! We use BCrypt to hash it first.
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("ROLE_USER");

        // Save to MySQL database
        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully!");
    }

    // 2. LOGIN ENDPOINT
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody AuthRequest request) {
        
        // Ask Spring Security to verify the username and password
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Error: Invalid username or password");
        }

        // If we reach here, the login was successful! 
        // Let's generate their digital passport (JWT)
        String jwtToken = jwtUtil.generateToken(request.getUsername());

        // Send the token back to the user
        return ResponseEntity.ok(new AuthResponse(jwtToken));
    }
}
