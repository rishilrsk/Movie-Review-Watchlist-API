# Movie Review & Watchlist API

A full-stack, production-ready web application designed for browsing movies, submitting reviews, and managing a private watchlist. 

This project features a robust **Spring Boot (Java)** backend paired with a sleek, custom **Vanilla JavaScript** frontend. The frontend is served directly by the backend, meaning the entire application can be built into a single, deployable `.jar` file.

## 🚀 Features

### Backend (Spring Boot 3)
* **RESTful Architecture:** Modular `Controller`, `Service`, and `Repository` layers.
* **Security:** JWT-based stateless authentication (JSON Web Tokens).
* **Database:** MySQL database integration via Spring Data JPA and Hibernate.
* **Entity Relationships:** Complex `@ManyToOne` and `@OneToMany` relationships mapping Users, Movies, and Reviews.

### Frontend (Vanilla JS + CSS3)
* **Single Page Application (SPA):** Seamless view switching without page reloads.
* **Premium UI:** Custom dark-theme design inspired by Netflix and IMDb, featuring glassmorphism and smooth CSS animations.
* **Asynchronous Fetching:** Non-blocking `fetch()` calls communicating directly with the REST API.
* **Dynamic Content:** JavaScript logic to automatically calculate and render average movie ratings in real-time.
* **Session Management:** Automatic injection of JWT tokens from `localStorage` into API request headers.

## 🛠️ Tech Stack

* **Java 17** & **Spring Boot 3**
* **Spring Security & JWT** (io.jsonwebtoken)
* **Spring Data JPA** & **Hibernate**
* **MySQL**
* **Maven**
* **HTML5, CSS3, Vanilla JavaScript**

## 💻 Getting Started

### Prerequisites
* Java 17 or higher
* Maven
* MySQL Server (running on localhost:3306)

### Setup Instructions
1. Clone the repository.
2. Ensure your local MySQL server is running.
3. The application is configured to automatically create the database `movie_db` if it doesn't exist. Update the credentials in `src/main/resources/application.properties` if your MySQL root password is not `Rishi123*`.
4. Run the application using Maven:
   ```bash
   mvn spring-boot:run
   ```
5. Open your browser and navigate to `http://localhost:8080/`.

## 📸 Screenshots
*(Add screenshots of your UI here!)*
