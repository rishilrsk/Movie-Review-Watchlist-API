// DOM Elements
const views = {
    home: document.getElementById('view-home'),
    auth: document.getElementById('view-auth'),
    movieDetail: document.getElementById('view-movie-detail'),
    watchlist: document.getElementById('view-watchlist'),
    addMovie: document.getElementById('view-add-movie')
};

const nav = {
    home: document.getElementById('nav-home'),
    watchlist: document.getElementById('nav-watchlist'),
    addMovie: document.getElementById('nav-add-movie'),
    login: document.getElementById('nav-login'),
    logout: document.getElementById('nav-logout'),
    logo: document.getElementById('nav-logo')
};

// UI State
let currentMovieId = null;

// --- View Management ---

function showView(viewName) {
    // Hide all
    Object.values(views).forEach(v => v.classList.remove('active'));
    // Show target
    if (views[viewName]) {
        views[viewName].classList.add('active');
    }
    // Update nav active states
    document.querySelectorAll('.nav-btn').forEach(btn => btn.classList.remove('active'));
    if (nav[viewName]) {
        nav[viewName].classList.add('active');
    }
}

function updateNavVisibility() {
    const isLoggedIn = api.isLoggedIn();
    if (isLoggedIn) {
        nav.login.style.display = 'none';
        nav.logout.style.display = 'block';
        nav.watchlist.style.display = 'block';
        nav.addMovie.style.display = 'block';
        document.getElementById('add-review-container').style.display = 'block';
    } else {
        nav.login.style.display = 'block';
        nav.logout.style.display = 'none';
        nav.watchlist.style.display = 'none';
        nav.addMovie.style.display = 'none';
        document.getElementById('add-review-container').style.display = 'none';
    }
}

// --- Toast Notifications ---

function showToast(message, isError = false) {
    const toast = document.getElementById('toast');
    toast.textContent = message;
    if (isError) {
        toast.classList.add('error');
    } else {
        toast.classList.remove('error');
    }
    toast.classList.add('show');
    
    setTimeout(() => {
        toast.classList.remove('show');
    }, 3000);
}

// --- Rendering Data ---

async function loadHome() {
    showView('home');
    const container = document.getElementById('movies-container');
    container.innerHTML = '<div class="loader">Loading movies...</div>';

    try {
        const movies = await api.getMovies();
        if (movies.length === 0) {
            container.innerHTML = '<p style="grid-column: 1/-1; text-align: center; color: var(--text-muted);">No movies found in the database. Add some from the backend!</p>';
            return;
        }

        // Fetch reviews for all movies to calculate average ratings
        const moviesWithRatings = await Promise.all(movies.map(async (movie) => {
            try {
                const reviews = await api.getReviews(movie.id);
                if (reviews && reviews.length > 0) {
                    const sum = reviews.reduce((acc, rev) => acc + rev.rating, 0);
                    movie.averageRating = sum / reviews.length;
                }
            } catch (e) {
                // Ignore errors for individual movie reviews
            }
            return movie;
        }));

        container.innerHTML = moviesWithRatings.map(movie => `
            <div class="movie-card" onclick="loadMovieDetail(${movie.id})">
                <h3>${movie.title}</h3>
                <span class="genre">${movie.genre}</span>
                <p>${movie.description}</p>
                <div class="rating-display">
                    ★ ${movie.averageRating ? movie.averageRating.toFixed(1) : 'No ratings'}
                </div>
            </div>
        `).join('');
    } catch (e) {
        container.innerHTML = `<div class="error-msg">Failed to load movies: ${e.message}</div>`;
    }
}

async function loadMovieDetail(id) {
    currentMovieId = id;
    showView('movieDetail');
    const container = document.getElementById('movie-detail-container');
    const reviewsList = document.getElementById('reviews-list');
    
    container.innerHTML = 'Loading...';
    reviewsList.innerHTML = 'Loading reviews...';

    try {
        const [movie, reviews] = await Promise.all([
            api.getMovie(id),
            api.getReviews(id).catch(() => []) // Fetch reviews, default to empty array if fails
        ]);
        
        container.innerHTML = `
            <h2>${movie.title}</h2>
            <div class="genre">${movie.genre}</div>
            <p style="font-size: 1.1rem; line-height: 1.6; margin-bottom: 2rem;">${movie.description}</p>
            <div style="display: flex; gap: 1rem; align-items: center;">
                <div style="font-size: 1.5rem; color: #fbbf24; font-weight: bold;">
                    ★ ${movie.averageRating ? movie.averageRating.toFixed(1) : 'No ratings yet'}
                </div>
                ${api.isLoggedIn() ? `<button class="action-btn" onclick="addToWatchlist(${movie.id})">+ Add to Watchlist</button>` : ''}
            </div>
        `;

        if (reviews && reviews.length > 0) {
            reviewsList.innerHTML = reviews.map(review => `
                <div class="review-item">
                    <div class="review-header">
                        <span class="reviewer-name">@${review.user ? review.user.username : 'anonymous'}</span>
                        <span class="review-rating">★ ${review.rating}/5</span>
                    </div>
                    <p style="color: var(--text-muted);">${review.comment}</p>
                </div>
            `).join('');
        } else {
            reviewsList.innerHTML = '<p style="color: var(--text-muted);">No reviews yet. Be the first!</p>';
        }

    } catch (e) {
        container.innerHTML = `<div class="error-msg">Failed to load movie details: ${e.message}</div>`;
        reviewsList.innerHTML = '';
    }
}

async function loadWatchlist() {
    showView('watchlist');
    const container = document.getElementById('watchlist-container');
    container.innerHTML = '<div class="loader">Loading watchlist...</div>';

    try {
        const watchlistEntries = await api.getWatchlist();
        
        if (watchlistEntries.length === 0) {
            container.innerHTML = '<p style="grid-column: 1/-1; text-align: center; color: var(--text-muted);">Your watchlist is empty.</p>';
            return;
        }

        container.innerHTML = watchlistEntries.map(entry => `
            <div class="movie-card">
                <h3>${entry.movie.title}</h3>
                <span class="genre">${entry.movie.genre}</span>
                <p style="margin-bottom: 1rem;">${entry.movie.description}</p>
                <div style="display: flex; gap: 1rem;">
                    <button class="action-btn" onclick="loadMovieDetail(${entry.movie.id})" style="flex: 1; margin: 0;">View</button>
                    <button class="action-btn remove" onclick="removeFromWatchlist(${entry.movie.id})" style="margin: 0;">Remove</button>
                </div>
            </div>
        `).join('');
    } catch (e) {
        container.innerHTML = `<div class="error-msg">Failed to load watchlist: ${e.message}</div>`;
    }
}

// --- Actions ---

async function addToWatchlist(movieId) {
    try {
        await api.addToWatchlist(movieId);
        showToast("Added to watchlist!");
    } catch (e) {
        showToast(e.message, true);
    }
}

async function removeFromWatchlist(movieId) {
    try {
        await api.removeFromWatchlist(movieId);
        showToast("Removed from watchlist!");
        loadWatchlist(); // refresh
    } catch (e) {
        showToast(e.message, true);
    }
}

// --- Event Listeners ---

// Navigation
nav.logo.addEventListener('click', loadHome);
nav.home.addEventListener('click', loadHome);
nav.watchlist.addEventListener('click', loadWatchlist);
nav.addMovie.addEventListener('click', () => showView('addMovie'));
nav.login.addEventListener('click', () => {
    showView('auth');
    document.getElementById('tab-login').click();
});
nav.logout.addEventListener('click', () => {
    api.logout();
    updateNavVisibility();
    loadHome();
    showToast("Logged out successfully");
});
document.getElementById('btn-back-home').addEventListener('click', loadHome);

// Auth Tabs
document.getElementById('tab-login').addEventListener('click', (e) => {
    e.target.classList.add('active');
    document.getElementById('tab-register').classList.remove('active');
    document.getElementById('form-login').classList.add('active');
    document.getElementById('form-register').classList.remove('active');
});

document.getElementById('tab-register').addEventListener('click', (e) => {
    e.target.classList.add('active');
    document.getElementById('tab-login').classList.remove('active');
    document.getElementById('form-register').classList.add('active');
    document.getElementById('form-login').classList.remove('active');
});

// Auth Forms
document.getElementById('form-login').addEventListener('submit', async (e) => {
    e.preventDefault();
    const btn = e.target.querySelector('button');
    const err = document.getElementById('login-error');
    err.textContent = '';
    btn.textContent = 'Logging in...';
    
    try {
        await api.login(
            document.getElementById('login-username').value,
            document.getElementById('login-password').value
        );
        updateNavVisibility();
        e.target.reset();
        loadHome();
        showToast("Login successful!");
    } catch (error) {
        err.textContent = error.message;
    } finally {
        btn.textContent = 'Login';
    }
});

document.getElementById('form-register').addEventListener('submit', async (e) => {
    e.preventDefault();
    const btn = e.target.querySelector('button');
    const err = document.getElementById('reg-error');
    err.textContent = '';
    btn.textContent = 'Registering...';
    
    try {
        await api.register(
            document.getElementById('reg-username').value,
            document.getElementById('reg-email').value,
            document.getElementById('reg-password').value
        );
        e.target.reset();
        document.getElementById('tab-login').click(); // Switch to login tab
        showToast("Registration successful! Please login.");
    } catch (error) {
        err.textContent = error.message;
    } finally {
        btn.textContent = 'Register';
    }
});

// Review Form
document.getElementById('form-review').addEventListener('submit', async (e) => {
    e.preventDefault();
    if (!currentMovieId) return;

    const btn = e.target.querySelector('button');
    btn.textContent = 'Posting...';
    
    try {
        await api.addReview(
            currentMovieId,
            document.getElementById('review-rating').value,
            document.getElementById('review-comment').value
        );
        e.target.reset();
        loadMovieDetail(currentMovieId); // Refresh details
        showToast("Review posted!");
    } catch (error) {
        showToast(error.message, true);
    } finally {
        btn.textContent = 'Post Review';
    }
});

// Add Movie Form
document.getElementById('form-add-movie').addEventListener('submit', async (e) => {
    e.preventDefault();
    const btn = e.target.querySelector('button');
    const err = document.getElementById('add-movie-error');
    err.textContent = '';
    btn.textContent = 'Creating...';
    
    try {
        const newMovie = await api.addMovie(
            document.getElementById('add-title').value,
            document.getElementById('add-genre').value,
            document.getElementById('add-desc').value
        );
        e.target.reset();
        showToast("Movie added successfully!");
        loadMovieDetail(newMovie.id); // Go straight to the new movie's page
    } catch (error) {
        err.textContent = error.message;
    } finally {
        btn.textContent = 'Create Movie';
    }
});

// Session expiry listener
window.addEventListener('auth-expired', () => {
    updateNavVisibility();
    showView('auth');
    document.getElementById('login-error').textContent = "Session expired. Please log in again.";
});

// --- Initialization ---
updateNavVisibility();
loadHome();
