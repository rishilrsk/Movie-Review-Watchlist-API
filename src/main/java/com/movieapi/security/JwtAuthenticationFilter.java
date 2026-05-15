package com.movieapi.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// This filter intercepts EVERY single request hitting our API to see if it has a valid JWT
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Look for the "Authorization" header in the HTTP Request
        final String authHeader = request.getHeader("Authorization");
        String username = null;
        String jwt = null;

        // 2. Check if it exists and starts with "Bearer " (standard JWT format)
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7); // Remove "Bearer " to get just the token
            try {
                username = jwtUtil.extractUsername(jwt);
            } catch (Exception e) {
                System.out.println("Invalid JWT Token!");
            }
        }

        // 3. If we found a username, and they aren't already authenticated...
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            
            // Load the user from the database
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // 4. Validate the token
            if (jwtUtil.validateToken(jwt, userDetails)) {
                
                // 5. If valid, manually authenticate the user in Spring Security's context
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        
        // 6. Pass the request along to the next filter in the chain
        filterChain.doFilter(request, response);
    }
}
