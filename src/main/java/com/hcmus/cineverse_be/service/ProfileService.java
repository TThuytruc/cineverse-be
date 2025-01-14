package com.hcmus.cineverse_be.service;

import com.hcmus.cineverse_be.dto.*;
import com.hcmus.cineverse_be.entity.*;
import com.hcmus.cineverse_be.exception.ConflictException;
import com.hcmus.cineverse_be.exception.ResourceNotFoundException;
import com.hcmus.cineverse_be.mapper.MovieMapper;
import com.hcmus.cineverse_be.mapper.ProfileMapper;
import com.hcmus.cineverse_be.response.profile.FavoriteResponse;
import com.hcmus.cineverse_be.response.profile.RatingsResponse;
import com.hcmus.cineverse_be.response.profile.WatchListResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProfileService {
    private final int RATINGS_PER_PAGE = 10;
    private final int WATCHLIST_PER_PAGE = 20;
    private final int FAVORITE_PER_PAGE = 20;

    private final String DB_RATINGS = "user_ratings";
    private final String DB_WATCHLIST = "user_watchlist";
    private final String DB_FAVORITE = "user_favorites";
    private final String DB_ALL = "movies";
    private final String DB_USER_MOVIE = "users_movies";
    private final String DB_REVIEW = "users_reviews";

    @Autowired
    private MovieMapper movieMapper;

    @Autowired
    private ProfileMapper profileMapper;

    @Autowired
    private MongoTemplate mongoTemplate;


    public RatingsResponse getRatingsByUser(int page) {

        if (page <= 0) {
            throw new IllegalArgumentException("Invalid page: Page must be greater than 0.");
        }

        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Query query = new Query();
        query.addCriteria(Criteria.where("user_id").is(userId));
        query.addCriteria(Criteria.where("rating").ne(null));

        long totalResults = mongoTemplate.count(query, UserMovie.class, DB_USER_MOVIE);
        int totalPages = (int) Math.ceil((double) totalResults / RATINGS_PER_PAGE);

        if (totalPages == 0) {
            totalPages++;
        }

        if (page > totalPages) {
            throw new IllegalArgumentException("Invalid page: Page must be less than or equal to " + totalPages + ".");
        }

        if (totalResults == 0) {
            return new RatingsResponse(page, new ArrayList<>(), 1, 0);
        }


        query.skip((long) (page - 1) * RATINGS_PER_PAGE);
        query.limit(RATINGS_PER_PAGE);

        List<UserMovie> infos = mongoTemplate.find(query, UserMovie.class, DB_USER_MOVIE);
        List<RatingDTO> ratings = new ArrayList<>();

        for (UserMovie info : infos) {
            UserMovieDTO infoDTO = profileMapper.toUserMovieDTO(info);

            Query queryReview = new Query();
            queryReview.addCriteria(Criteria.where("user_id").is(info.getUserId()));
            queryReview.addCriteria(Criteria.where("movie_id").is(info.getMovie().getId()));

            List<Review> reviews = mongoTemplate.find(queryReview, Review.class, DB_REVIEW);
            List<ReviewDTO> reviewDTOs = reviews.stream()
                    .map(review -> {
                        return movieMapper.toReviewDTO(review);
                    })
                    .toList();

            RatingDTO rating = new RatingDTO();
            rating.setInfo(infoDTO);
            rating.setReviews(reviewDTOs);
            ratings.add(rating);
        }

        return new RatingsResponse(page, ratings, totalPages, (int) totalResults);
    }


    public WatchListResponse getWatchListByUser(int page) {

        if (page <= 0) {
            throw new IllegalArgumentException("Invalid page: Page must be greater than 0.");
        }

        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Query query = new Query();
        query.addCriteria(Criteria.where("user_id").is(userId));
        query.addCriteria(Criteria.where("in_watchlist").is(true));

        long totalResults = mongoTemplate.count(query, UserMovie.class, DB_USER_MOVIE);
        int totalPages = (int) Math.ceil((double) totalResults / WATCHLIST_PER_PAGE);

        if (totalPages == 0) {
            totalPages++;
        }

        if (page > totalPages) {
            throw new IllegalArgumentException("Invalid page: Page must be less than or equal to " + totalPages + ".");
        }

        if (totalResults == 0) {
            return new WatchListResponse(page, new ArrayList<>(), 1, 0);
        }


        query.skip((long) (page - 1) * WATCHLIST_PER_PAGE);
        query.limit(WATCHLIST_PER_PAGE);

        List<UserMovie> watchList = mongoTemplate.find(query, UserMovie.class, DB_USER_MOVIE);

        List<UserMovieDTO> results = watchList.stream()
                .map(item -> profileMapper.toUserMovieDTO(item))
                .collect(Collectors.toList());

        return new WatchListResponse(page, results, totalPages, (int) totalResults);
    }

    public UserMovieDTO addWatchList(long movieId) {
        // Check input
        Query query = new Query(Criteria.where("id").is(movieId));
        MovieProfile movie = mongoTemplate.findOne(query, MovieProfile.class, DB_ALL);

        if (movie == null) {
            throw new ResourceNotFoundException("Movie not found.");
        }

        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Check if movie exists in the watchlist
        Query watchlistQuery = new Query();
        watchlistQuery.addCriteria(Criteria.where("user_id").is(userId));
        watchlistQuery.addCriteria(Criteria.where("movie").is(movie));
        watchlistQuery.addCriteria(Criteria.where("in_watchlist").is(true));
        UserMovie userMovie = mongoTemplate.findOne(watchlistQuery, UserMovie.class, DB_USER_MOVIE);

        if (userMovie != null) {
            throw new ConflictException("Movie is already in the watchlist.");
        }

        // Add to watchlist
        String userMovieId = userId + "_" + movieId;

        Query queryAdd = new Query(Criteria.where("_id").is(userMovieId));
        Update updateAdd = new Update()
                .set("user_id", userId)
                .set("movie", movie)
                .setOnInsert("rating", null)
                .set("in_watchlist", true)
                .setOnInsert("is_favorite", false);

        mongoTemplate.upsert(queryAdd, updateAdd, UserMovie.class, DB_USER_MOVIE);
        UserMovie result = mongoTemplate.findOne(queryAdd, UserMovie.class, DB_USER_MOVIE);
        return profileMapper.toUserMovieDTO(result);
    }

    public void deleteWatchList(long movieId) {
        // Check input
        Query movieQuery = new Query(Criteria.where("id").is(movieId));
        MovieProfile movie = mongoTemplate.findOne(movieQuery, MovieProfile.class, DB_ALL);

        if (movie == null) {
            throw new ResourceNotFoundException("Movie not found.");
        }

        // Get current user
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Check if movie exists in the watchlist
        Query watchlistQuery = new Query();
        watchlistQuery.addCriteria(Criteria.where("user_id").is(userId));
        watchlistQuery.addCriteria(Criteria.where("movie").is(movie));
        watchlistQuery.addCriteria(Criteria.where("in_watchlist").is(true));

        UserMovie userMovie = mongoTemplate.findOne(watchlistQuery, UserMovie.class, DB_USER_MOVIE);

        if (userMovie == null) {
            throw new ResourceNotFoundException("Movie not found in the watchlist.");
        }

        // Remove from watchlist
        Update updateDelete = new Update()
                .set("in_watchlist", false);

        mongoTemplate.updateFirst(watchlistQuery, updateDelete, UserMovie.class, DB_USER_MOVIE);
    }


    public FavoriteResponse getFavoriteMoviesByUser(int page) {

        if (page <= 0) {
            throw new IllegalArgumentException("Invalid page: Page must be greater than 0.");
        }

        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Query query = new Query();
        query.addCriteria(Criteria.where("user_id").is(userId));
        query.addCriteria(Criteria.where("is_favorite").is(true));

        long totalResults = mongoTemplate.count(query, UserMovie.class, DB_USER_MOVIE);
        int totalPages = (int) Math.ceil((double) totalResults / FAVORITE_PER_PAGE);

        if (totalPages == 0) {
            totalPages++;
        }

        if (page > totalPages) {
            throw new IllegalArgumentException("Invalid page: Page must be less than or equal to " + totalPages + ".");
        }

        if (totalResults == 0) {
            return new FavoriteResponse(page, new ArrayList<>(), 1, 0);
        }


        query.skip((long) (page - 1) * FAVORITE_PER_PAGE);
        query.limit(FAVORITE_PER_PAGE);

        List<UserMovie> favorites = mongoTemplate.find(query, UserMovie.class, DB_USER_MOVIE);

        List<UserMovieDTO> results = favorites.stream()
                .map(item -> profileMapper.toUserMovieDTO(item))
                .collect(Collectors.toList());

        return new FavoriteResponse(page, results, totalPages, (int) totalResults);
    }

    public UserMovieDTO addFavorite(long movieId) {
        // Check input
        Query query = new Query(Criteria.where("id").is(movieId));
        MovieProfile movie = mongoTemplate.findOne(query, MovieProfile.class, DB_ALL);

        if (movie == null) {
            throw new ResourceNotFoundException("Movie not found.");
        }

        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Check if movie exists in the favorite list
        Query favoriteQuery = new Query();
        favoriteQuery.addCriteria(Criteria.where("user_id").is(userId));
        favoriteQuery.addCriteria(Criteria.where("movie").is(movie));
        favoriteQuery.addCriteria(Criteria.where("is_favorite").is(true));
        UserMovie userMovie = mongoTemplate.findOne(favoriteQuery, UserMovie.class, DB_USER_MOVIE);

        if (userMovie != null) {
            throw new ConflictException("Movie is already in the favorite list.");
        }

        // Add to favorite list
        String userMovieId = userId + "_" + movieId;

        Query queryAdd = new Query(Criteria.where("_id").is(userMovieId));
        Update updateAdd = new Update()
                .set("user_id", userId)
                .set("movie", movie)
                .setOnInsert("rating", null)
                .setOnInsert("in_watchlist", false)
                .set("is_favorite", true);

        mongoTemplate.upsert(queryAdd, updateAdd, UserMovie.class, DB_USER_MOVIE);
        UserMovie result = mongoTemplate.findOne(queryAdd, UserMovie.class, DB_USER_MOVIE);
        return profileMapper.toUserMovieDTO(result);
    }

    public void deleteFavorite(long movieId) {
        // Check input
        Query movieQuery = new Query(Criteria.where("id").is(movieId));
        MovieProfile movie = mongoTemplate.findOne(movieQuery, MovieProfile.class, DB_ALL);

        if (movie == null) {
            throw new ResourceNotFoundException("Movie not found.");
        }

        // Get current user
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Check if movie exists in the watchlist
        Query favoriteQuery = new Query();
        favoriteQuery.addCriteria(Criteria.where("user_id").is(userId));
        favoriteQuery.addCriteria(Criteria.where("movie").is(movie));
        favoriteQuery.addCriteria(Criteria.where("is_favorite").is(true));

        UserMovie userMovie = mongoTemplate.findOne(favoriteQuery, UserMovie.class, DB_USER_MOVIE);

        if (userMovie == null) {
            throw new ResourceNotFoundException("Movie not found in the favorite list.");
        }

        // Remove from favorite list
        Update updateDelete = new Update()
                .set("is_favorite", false);

        mongoTemplate.updateFirst(favoriteQuery, updateDelete, UserMovie.class, DB_USER_MOVIE);
    }

    public UserMovieDTO getMovieDetailsByMovieIdAndUserId(long movieId) {
        // Get current user
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        System.out.println("movieId: " + movieId);

        Query movieQuery = new Query(Criteria.where("id").is(movieId));
        MovieProfile movie = mongoTemplate.findOne(movieQuery, MovieProfile.class, DB_ALL);

        if (movie == null) {
            throw new ResourceNotFoundException("Movie not found.");
        }


        Query query = new Query(Criteria.where("movie").is(movie).and("user_id").is(userId));
        UserMovie userMovie = mongoTemplate.findOne(query, UserMovie.class, DB_USER_MOVIE);

        if (userMovie == null) {
            throw new ResourceNotFoundException("Movie not found.");
        }
        return profileMapper.toUserMovieDTO(userMovie);
    }
}
