// API Helper functions to interact with the Spring Boot backend

const API_BASE_URL = '/api';

class Api {
    constructor() {
        this.token = localStorage.getItem('jwtToken');
    }

    setToken(token) {
        this.token = token;
        if (token) {
            localStorage.setItem('jwtToken', token);
        } else {
            localStorage.removeItem('jwtToken');
        }
    }

    isLoggedIn() {
        return !!this.token;
    }

    // Helper for fetch calls
    async fetchAuth(endpoint, options = {}) {
        const headers = {
            'Content-Type': 'application/json',
            ...options.headers
        };

        if (this.token) {
            headers['Authorization'] = `Bearer ${this.token}`;
        }

        const response = await fetch(`${API_BASE_URL}${endpoint}`, {
            ...options,
            headers
        });

        if (!response.ok) {
            // Handle 403 Forbidden specifically (usually means token expired)
            if (response.status === 403 && this.token) {
                this.setToken(null);
                window.dispatchEvent(new Event('auth-expired'));
                throw new Error("Session expired. Please log in again.");
            }
            
            // Try to get error message from server
            try {
                const errorData = await response.text();
                throw new Error(errorData || `Request failed with status ${response.status}`);
            } catch (e) {
                throw new Error(`Request failed with status ${response.status}`);
            }
        }

        // Return JSON if there is content, else null
        const text = await response.text();
        return text ? JSON.parse(text) : null;
    }

    // --- Auth Endpoints ---

    async login(username, password) {
        const response = await fetch(`${API_BASE_URL}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });

        if (!response.ok) throw new Error("Invalid credentials");
        
        const data = await response.json();
        this.setToken(data.jwtToken);
        return data;
    }

    async register(username, email, password) {
        const response = await fetch(`${API_BASE_URL}/auth/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, email, password })
        });

        if (!response.ok) throw new Error("Registration failed");
        return response.text();
    }

    logout() {
        this.setToken(null);
    }

    // --- Movie Endpoints ---

    async getMovies() {
        return this.fetchAuth('/movies');
    }

    async getMovie(id) {
        return this.fetchAuth(`/movies/${id}`);
    }

    async addMovie(title, genre, description) {
        return this.fetchAuth('/movies', {
            method: 'POST',
            body: JSON.stringify({ title, genre, description })
        });
    }

    // --- Review Endpoints ---

    async getReviews(movieId) {
        return this.fetchAuth(`/movies/${movieId}/reviews`);
    }

    async addReview(movieId, rating, comment) {
        return this.fetchAuth(`/movies/${movieId}/reviews`, {
            method: 'POST',
            body: JSON.stringify({ rating: parseInt(rating), comment })
        });
    }

    // --- Watchlist Endpoints ---

    async getWatchlist() {
        return this.fetchAuth('/watchlist');
    }

    async addToWatchlist(movieId) {
        return this.fetchAuth(`/watchlist/${movieId}`, {
            method: 'POST'
        });
    }

    async removeFromWatchlist(movieId) {
        return this.fetchAuth(`/watchlist/${movieId}`, {
            method: 'DELETE'
        });
    }
}

// Create a global instance
const api = new Api();
