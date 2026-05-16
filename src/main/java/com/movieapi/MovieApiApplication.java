package com.movieapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// We have configured Database and Security, so no more exclusions!
@SpringBootApplication
public class MovieApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(MovieApiApplication.class, args);
		System.out.println("=============================================");
		System.out.println("Movie Review & Watchlist API is running! 🚀");
		System.out.println("=============================================");
	}

}
