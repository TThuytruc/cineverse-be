package com.hcmus.cineverse_be.service;

import com.hcmus.cineverse_be.dto.FavoriteDTO;
import com.hcmus.cineverse_be.dto.RatingDTO;
import com.hcmus.cineverse_be.dto.WatchListDTO;
import com.hcmus.cineverse_be.entity.Favorite;
import com.hcmus.cineverse_be.entity.MovieProfile;
import com.hcmus.cineverse_be.entity.Rating;
import com.hcmus.cineverse_be.entity.WatchList;
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

        long totalResults = mongoTemplate.count(query, Rating.class, DB_RATINGS);
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

        List<Rating> ratings = mongoTemplate.find(query, Rating.class, DB_RATINGS);

        List<RatingDTO> results = ratings.stream()
                .map(rating -> movieMapper.toRatingDTO(rating))
                .collect(Collectors.toList());

        return new RatingsResponse(page, results, totalPages, (int) totalResults);
    }


    public WatchListResponse getWatchListByUser(int page) {

        if (page <= 0) {
            throw new IllegalArgumentException("Invalid page: Page must be greater than 0.");
        }

        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Query query = new Query();
        query.addCriteria(Criteria.where("user_id").is(userId));

        long totalResults = mongoTemplate.count(query, WatchList.class, DB_WATCHLIST);
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

        List<WatchList> watchList = mongoTemplate.find(query, WatchList.class, DB_WATCHLIST);

        List<WatchListDTO> results = watchList.stream()
                .map(item -> profileMapper.toWatchListDTO(item))
                .collect(Collectors.toList());

        return new WatchListResponse(page, results, totalPages, (int) totalResults);
    }

    public WatchListDTO addWatchList(long movieId) {
        // Check input
        Query query = new Query(Criteria.where("id").is(movieId));
        MovieProfile movie = mongoTemplate.findOne(query, MovieProfile.class, DB_ALL);

        if (movie == null) {
            throw new ResourceNotFoundException("Movie not found.");
        }

        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Check if movie exists in the watchlist
        Query watchlistQuery = new Query(Criteria.where("user_id").is(userId).and("movie").is(movie));
        WatchList watchList = mongoTemplate.findOne(watchlistQuery, WatchList.class, DB_WATCHLIST);

        if (watchList != null) {
            throw new ConflictException("Movie is already in the watchlist.");
        }

        // Add to watchlist
        WatchList newWatchList = new WatchList();
        newWatchList.setUserId(userId);
        newWatchList.setMovie(movie);
        WatchList savedWatchList = mongoTemplate.save(newWatchList, DB_WATCHLIST);

        return profileMapper.toWatchListDTO(savedWatchList);
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
        Query watchlistQuery = new Query(Criteria.where("user_id").is(userId).and("movie").is(movie));
        WatchList watchList = mongoTemplate.findOne(watchlistQuery, WatchList.class, DB_WATCHLIST);

        if (watchList == null) {
            throw new ResourceNotFoundException("Movie not found in the watchlist.");
        }

        // Remove from watchlist
        mongoTemplate.remove(watchlistQuery, WatchList.class, DB_WATCHLIST);
    }


    public FavoriteResponse getFavoriteMoviesByUser(int page) {

        if (page <= 0) {
            throw new IllegalArgumentException("Invalid page: Page must be greater than 0.");
        }

        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Query query = new Query();
        query.addCriteria(Criteria.where("user_id").is(userId));

        long totalResults = mongoTemplate.count(query, Favorite.class, DB_FAVORITE);
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

        List<Favorite> favorites = mongoTemplate.find(query, Favorite.class, DB_FAVORITE);

        List<FavoriteDTO> results = favorites.stream()
                .map(item -> profileMapper.toFavoriteDTO(item))
                .collect(Collectors.toList());

        return new FavoriteResponse(page, results, totalPages, (int) totalResults);
    }

    public FavoriteDTO addFavorite(long movieId) {
        // Check input
        Query query = new Query(Criteria.where("id").is(movieId));
        MovieProfile movie = mongoTemplate.findOne(query, MovieProfile.class, DB_ALL);

        if (movie == null) {
            throw new ResourceNotFoundException("Movie not found.");
        }

        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Check if movie exists in favorite list
        Query favoriteQuery = new Query(Criteria.where("user_id").is(userId).and("movie").is(movie));
        Favorite favorite = mongoTemplate.findOne(favoriteQuery, Favorite.class, DB_FAVORITE);

        if (favorite != null) {
            throw new ConflictException("Movie is already in favorite list.");
        }

        // Add to favorite list
        Favorite newFavorite = new Favorite();
        newFavorite.setUserId(userId);
        newFavorite.setMovie(movie);
        Favorite savedFavorite = mongoTemplate.save(newFavorite, DB_FAVORITE);

        return profileMapper.toFavoriteDTO(savedFavorite);
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

        // Check if movie exists in the favorite list
        Query favoriteQuery = new Query(Criteria.where("user_id").is(userId).and("movie").is(movie));
        Favorite favorite = mongoTemplate.findOne(favoriteQuery, Favorite.class, DB_FAVORITE);

        if (favorite == null) {
            throw new ResourceNotFoundException("Movie not found in the favorite list.");
        }

        // Remove from favorite list
        mongoTemplate.remove(favoriteQuery, Favorite.class, DB_FAVORITE);
    }
}
