package com.movieapi.config;

import com.movieapi.security.CustomUserDetailsService;
import com.movieapi.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity // Enables Spring Security's web security support
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthFilter;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    // 1. Password Encoder (Uses BCrypt to hash passwords)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 2. Authentication Provider (Connects our UserDetailsService and PasswordEncoder)
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    // 3. Authentication Manager (Handles the actual login process)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // 4. The main Security Filter Chain (The Bouncer!)
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF since we are using JWT tokens (stateless)
            .csrf(csrf -> csrf.disable())
            
            // Define which endpoints are public and which are private
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/index.html", "/css/**", "/js/**").permitAll() // PUBLIC: Frontend files
                .requestMatchers("/api/auth/**").permitAll() // PUBLIC: Anyone can register/login
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/movies/**").permitAll() // PUBLIC: Anyone can browse movies
                .requestMatchers("/error").permitAll()       // PUBLIC: Let Spring Boot show error messages (like 500 Internal Server Error)
                .anyRequest().authenticated()                // PRIVATE: Everything else requires a token
            )
            
            // Set session management to STATELESS (No cookies, just JWTs)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // Tell Spring to use our Authentication Provider
            .authenticationProvider(authenticationProvider())
            
            // IMPORTANT: Add our JWT Filter BEFORE the standard Spring Security Login Filter
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
