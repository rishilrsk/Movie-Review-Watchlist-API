package com.movieapi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// @Data, @NoArgsConstructor, @AllArgsConstructor are Lombok annotations. 
// They automatically generate getters, setters, and constructors for us!
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity // Tells JPA this class represents a database table
@Table(name = "users") // We name it 'users' because 'user' is often a reserved keyword in SQL
public class User {

    @Id // Marks this field as the Primary Key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Tells MySQL to Auto-Increment this ID
    private Long id;

    @Column(nullable = false, unique = true) // Cannot be null, and must be unique in the database
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    // We will use roles later for Security (e.g., "ROLE_USER" or "ROLE_ADMIN")
    @Column(nullable = false)
    private String role = "ROLE_USER"; 
}
