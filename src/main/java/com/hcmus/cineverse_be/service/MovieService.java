package com.hcmus.cineverse_be.service;

import com.hcmus.cineverse_be.dto.*;
import com.hcmus.cineverse_be.entity.*;
import com.hcmus.cineverse_be.exception.ResourceNotFoundException;
import com.hcmus.cineverse_be.mapper.MovieMapper;
import com.hcmus.cineverse_be.mapper.ProfileMapper;
import com.hcmus.cineverse_be.response.AIApiResponse;
import com.hcmus.cineverse_be.response.movie.SearchMovieResponse;
import com.hcmus.cineverse_be.response.movie.TrendingMoviesResponse;
import com.hcmus.cineverse_be.response.navigate.NavigationResponse;
import com.hcmus.cineverse_be.response.retriever.RetrieverResponse;
import com.mongodb.client.result.UpdateResult;
import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MovieService {
    private final String SMALL_POSTER_SIZE = "w342";
    private final String LARGE_POSTER_SIZE = "w780";
    private final String SMALL_PROFILE_SIZE = "w185";
//    private final String LARGE_PROFILE_SIZE = "h632";

    private final int MOVIES_PER_PAGE = 20;

    private final String DB_ALL = "movies";
    private final String DB_TRENDING_DAY = "movies_trending_day";
    private final String DB_TRENDING_WEEK = "movies_trending_week";
    private final String DB_GENRES = "movie_genres";
//    private final String DB_RATINGS = "user_ratings";
    private final String DB_USER_MOVIE = "users_movies";
    private final String DB_REVIEW = "users_reviews";

    private final String CAST_PAGE = "CAST_PAGE";
    private final String MOVIE_PAGE = "MOVIE_PAGE";
    private final String GENRE_PAGE = "GENRE_PAGE";
    private final String SEARCH_PAGE = "SEARCH_PAGE";
    private final String HOME_PAGE = "HOME_PAGE";
    private final String PROFILE_PAGE = "PROFILE_PAGE";
    private final String NONE  = "NONE";

    @Value("${tmdb.api.base-image-url}")
    private String baseImageUrl;

    @Value("${tmdb.api.original-image-url}")
    private String originalImageUrl;

    private String baseSmallPosterUrl;
    private String baseLargePosterUrl;
    private String baseSmallProfileUrl;


    private WebClient webClient;

    @Autowired
    private UserService userService;

    @Autowired
    private MovieMapper movieMapper;

    @Autowired
    private ProfileMapper profileMapper;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Value("${llm.api.key}")
    private String llmApiKey;

    @PostConstruct
    public void init() {
        this.baseSmallPosterUrl = baseImageUrl + SMALL_POSTER_SIZE;
        this.baseLargePosterUrl = baseImageUrl + LARGE_POSTER_SIZE;
        this.baseSmallProfileUrl = baseImageUrl + SMALL_PROFILE_SIZE;
    }

    public MovieService(WebClient webClient) {
        this.webClient = webClient;
    }


    public TrendingMoviesResponse getTrending(String period, int page) {

        if (page <= 0) {
            throw new IllegalArgumentException("Invalid page: Page must be greater than 0.");
        }

        String collectionName = "";

        if ("day".equals(period)) {
            collectionName = DB_TRENDING_DAY;
        } else if ("week".equals(period)) {
            collectionName = DB_TRENDING_WEEK;
        } else {
            throw new IllegalArgumentException("Invalid period: Period must be 'day' or 'week'.");
        }

        long totalResults = mongoTemplate.count(new Query(), MovieTrending.class, collectionName);
        int totalPages = (int) Math.ceil((double) totalResults / MOVIES_PER_PAGE);

        if (page > totalPages) {
            throw new IllegalArgumentException("Invalid page: Page must be less than or equal to " + totalPages + ".");
        }


        Query query = new Query();
        query.skip((long) (page - 1) * MOVIES_PER_PAGE);
        query.limit(MOVIES_PER_PAGE);
        List<MovieTrending> trendingMovies = mongoTemplate.find(query, MovieTrending.class, collectionName);

        List<MovieTrendingDTO> results = trendingMovies.stream()
                .map(movie -> {
                    if(movie.getPosterPath() != null) {
                        movie.setPosterPath(baseSmallPosterUrl + movie.getPosterPath());
                    }
                    if(movie.getBackdropPath() != null) {
                        movie.setBackdropPath(originalImageUrl + movie.getBackdropPath());
                    }

                    return movieMapper.toMovieTrendingDTO(movie);
                })
                .collect(Collectors.toList());

        return new TrendingMoviesResponse(page, results, totalPages, (int) totalResults);
    }


    public MovieDetailDTO getMovieDetail(long movieId) {

        Query query = new Query(Criteria.where("id").is(movieId));
        MovieDetail movieDetail = mongoTemplate.findOne(query, MovieDetail.class, DB_ALL);

        if (movieDetail == null) {
            throw new ResourceNotFoundException("Movie not found.");
        }

        MovieDetailDTO movieDetailDTO = movieMapper.toMovieDetailDTO(movieDetail);

        if(movieDetailDTO.getPosterPath() != null) {
            movieDetailDTO.setPosterPath(baseLargePosterUrl + movieDetailDTO.getPosterPath());
        }

        List<CastDTO> listCast = movieDetailDTO.getCast();
        if (listCast != null) {
            listCast.forEach(cast -> {
                if(cast.getProfilePath() != null) {
                    cast.setProfilePath(baseSmallProfileUrl + cast.getProfilePath());
                }
            });
        }

        List<CrewDTO> listCrew = movieDetailDTO.getCrew();
        if (listCrew != null) {
            listCrew.forEach(crew -> {
                if(crew.getProfilePath() != null) {
                    crew.setProfilePath(baseSmallProfileUrl + crew.getProfilePath());
                }
            });
        }

        List<ReviewDetailDTO> listReviews = movieDetailDTO.getReviews();
        if (listReviews != null) {
            listReviews.forEach(review -> {
                if(review.getAuthorDetails().getAvatarPath() != null) {
                    review.getAuthorDetails().setAvatarPath(baseSmallProfileUrl + review.getAuthorDetails().getAvatarPath());
                }
            });
        }

        return movieDetailDTO;
    }

    public SearchMovieResponse getSearchMovies(String query, int page, String fromDate, String toDate, List<Integer> genreIds) {

        if (page <= 0) {
            throw new IllegalArgumentException("Invalid page: Page must be greater than 0.");
        }

        String collectionName = "movies";

        Criteria criteria = new Criteria();
        if (query != null && !query.trim().isEmpty()) {
            criteria.and("title").regex(query.trim(), "i");
        }

        if (fromDate != null || toDate != null) {
            Criteria dateCriteria = new Criteria("release_date");
            if (fromDate != null) {
                dateCriteria.gte(fromDate); // greater than or equal
            }
            if (toDate != null) {
                dateCriteria.lte(toDate); // less than or equal
            }
            criteria.andOperator(dateCriteria);
        }

        if (genreIds != null && !genreIds.isEmpty()) {
            criteria.and("genres_id").in(genreIds);
        }

        Query countQuery = new Query().addCriteria(criteria);
        long totalResults = mongoTemplate.count(countQuery, MovieSearch.class, collectionName);
        int totalPages = (int) Math.ceil((double) totalResults / MOVIES_PER_PAGE);

        if (page > totalPages + 1) {
            throw new IllegalArgumentException("Invalid page: Page must be less than or equal to " + totalPages + ".");
        }


        Query searchQuery = new Query().addCriteria(criteria);
        searchQuery.skip((long) (page - 1) * MOVIES_PER_PAGE);
        searchQuery.limit(MOVIES_PER_PAGE);
        List<MovieSearch> searchMovies = mongoTemplate.find(searchQuery, MovieSearch.class, collectionName);

        List<MovieSearchDTO> results = searchMovies.stream()
                .map(movie -> {
                    if(movie.getPosterPath() != null) {
                        movie.setPosterPath(baseSmallPosterUrl + movie.getPosterPath());
                    }
                    if(movie.getBackdropPath() != null) {
                        movie.setBackdropPath(originalImageUrl + movie.getBackdropPath());
                    }

                    return movieMapper.toMovieSearchDTO(movie);
                })
                .collect(Collectors.toList());

        return new SearchMovieResponse(page, results, totalPages, (int) totalResults);
    }

    private RetrieverResponse getLlmMovieRetrieverResponse(
            String collectionName,
            String query,
            int amount,
            double threshold) {
        try {
            
        
            RetrieverResponse response = webClient.get()
                            .uri(uriBuilder -> {uriBuilder
                                    .path("/retriever/")
                                    .queryParam("llm_api_key", llmApiKey)
                                    .queryParam("collection_name", collectionName)
                                    .queryParam("query", query)
                                    .queryParam("amount", amount)
                                    .queryParam("threshold", threshold);
                                    
                                    System.out.println(uriBuilder.build());
                                    return uriBuilder.build();})
                            .retrieve()
                            .bodyToMono(new ParameterizedTypeReference<AIApiResponse<RetrieverResponse>>() {})
                            .map(AIApiResponse::getData)
                            .block();


            return response;
        } catch (Exception e) {
            System.out.println("Error when retrieve: "+ e);
            return null;
        }
    }

    public SearchMovieResponse getMoviesFromLlmRetriever(String collectionName, String query, int amount, double threshold) {
        RetrieverResponse retrieverResponse = getLlmMovieRetrieverResponse(collectionName, query, amount, threshold);
        List<String> movieIds = retrieverResponse.getResult();

        Query queryMovies = new Query(Criteria.where("_id").in(movieIds));
        List<MovieSearch> movies = mongoTemplate.find(queryMovies, MovieSearch.class, DB_ALL);

        List<MovieSearchDTO> results = movies.stream()
                .map(movie -> {
                    if (movie.getPosterPath() != null) {
                        movie.setPosterPath(baseSmallPosterUrl + movie.getPosterPath());
                    }
                    if (movie.getBackdropPath() != null) {
                        movie.setBackdropPath(originalImageUrl + movie.getBackdropPath());
                    }
                    return movieMapper.toMovieSearchDTO(movie);
                })
                .collect(Collectors.toList());

        return new SearchMovieResponse(1, results, 1, results.size());
    }

    private NavigationResponse getMovieNavigationResponse(String query) {


        /*NavigationResponse response = webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/navigate/")
                        .queryParam("llm_api_key", llmApiKey)
                        .queryParam("query", query)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<AIApiResponse<NavigationResponse>>() {})
                .map(AIApiResponse::getData)
                .block();*/

        NavigationResponse response2 = webClient.post()
                .uri(uriBuilder -> { uriBuilder
                        .path("/navigate/")
                        .queryParam("llm_api_key", llmApiKey)
                        .queryParam("query", query);

                        System.out.println(uriBuilder.build());


                        return uriBuilder.build();}
                )
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<AIApiResponse<NavigationResponse>>() {})
                .map(AIApiResponse::getData)
                .block();

        System.out.println(response2);

        return response2;
        //return response;
    }

    public NavigationResponse getAINavigation(String query) {
        NavigationResponse navigationResponse = getMovieNavigationResponse(query);
        if (navigationResponse == null || navigationResponse.getParams() == null) {
            return navigationResponse;
        }

        if (navigationResponse.getRoute().equals(CAST_PAGE) 
            || navigationResponse.getRoute().equals(MOVIE_PAGE)) {
            List<String> movieIds = (List<String>) navigationResponse.getParams().get("movie_ids");

            if (movieIds == null || movieIds.isEmpty()) {
                return navigationResponse;
            }

            Query queryMovies = new Query(Criteria.where("_id").in(movieIds));
            List<MovieSearch> movies = mongoTemplate.find(queryMovies, MovieSearch.class, DB_ALL);
    
            List<MovieSearchDTO> results = movies.stream()
                    .map(movie -> {
                        if (movie.getPosterPath() != null) {
                            movie.setPosterPath(baseSmallPosterUrl + movie.getPosterPath());
                        }
                        if (movie.getBackdropPath() != null) {
                            movie.setBackdropPath(originalImageUrl + movie.getBackdropPath());
                        }
                        return movieMapper.toMovieSearchDTO(movie);
                    })
                    .collect(Collectors.toList());

            return new NavigationResponse(
                navigationResponse.getRoute(), 
                Collections.singletonMap("movies", results), 
                true);
            
        }

        if (navigationResponse.getRoute().equals(GENRE_PAGE)) {
           
            List<String> genreIds = extractListIdsFromMapObject(navigationResponse, "genre_ids");

            if (genreIds == null || genreIds.isEmpty()) {
                return navigationResponse;
            }

            List<Integer> genresTmdbIds = getGenresTmdbIdByIds(genreIds);

            Query queryMovies = new Query(Criteria.where("genres.id").in(genresTmdbIds));
            
            List<MovieSearch> movies = mongoTemplate.find(queryMovies, MovieSearch.class, DB_ALL);
    
            List<MovieSearchDTO> results = movies.stream()
                    .map(movie -> {
                        if (movie.getPosterPath() != null) {
                            movie.setPosterPath(baseSmallPosterUrl + movie.getPosterPath());
                        }
                        if (movie.getBackdropPath() != null) {
                            movie.setBackdropPath(originalImageUrl + movie.getBackdropPath());
                        }
                        return movieMapper.toMovieSearchDTO(movie);
                    })
                    .collect(Collectors.toList());

            return new NavigationResponse(
                navigationResponse.getRoute(), 
                Collections.singletonMap("movies", results), 
                true);
        }

        if (navigationResponse.getRoute().equals(SEARCH_PAGE)) {
            //handle in frontend, do not need to handle here
            return navigationResponse;
            /*String keyword = (String) navigationResponse.getParams().get("keyword");
            if (keyword == null || keyword.isEmpty()) {
                return navigationResponse;
            }

            Query countQuery = new Query();
            countQuery.addCriteria(
                    Criteria.where("title").regex(keyword, "i")
            );

            long totalResults = mongoTemplate.count(countQuery, MovieSearch.class, DB_ALL);
            int totalPages = (int) Math.ceil((double) totalResults / MOVIES_PER_PAGE);

            Query searchQuery = new Query();
            searchQuery.addCriteria(
                    Criteria.where("title").regex(keyword, "i")
            );
            searchQuery.skip(0);
            searchQuery.limit(MOVIES_PER_PAGE);
            List<MovieSearch> searchMovies = mongoTemplate.find(searchQuery, MovieSearch.class, DB_ALL);

            List<MovieSearchDTO> results = searchMovies.stream()
                    .map(movie -> {
                        if(movie.getPosterPath() != null) {
                            movie.setPosterPath(baseSmallPosterUrl + movie.getPosterPath());
                        }
                        if(movie.getBackdropPath() != null) {
                            movie.setBackdropPath(originalImageUrl + movie.getBackdropPath());
                        }

                        return movieMapper.toMovieSearchDTO(movie);
                    })
                    .collect(Collectors.toList());

            return new NavigationResponse(
                navigationResponse.getRoute(), 
                Collections.singletonMap("movies", results), 
                true);*/
        }

        if (navigationResponse.getRoute().equals(HOME_PAGE)
            || navigationResponse.getRoute().equals(PROFILE_PAGE)
            || navigationResponse.getRoute().equals(NONE)) {
                return navigationResponse;
        }
        
        return navigationResponse;
       // return new NavigationResponse(navigationResponse.getRoute(), navigationResponse.getParams(), false);
       
    }

    public List<GenreDTO> getGenresByIds(List<String> genreIds) {
        Query query = new Query(Criteria.where("_id").in(genreIds));
        List<Genre> genres = mongoTemplate.find(query, Genre.class, DB_GENRES);

        return genres.stream()
                .map(movieMapper::toGenreDTO)
                .collect(Collectors.toList());
    }

    public List<Integer> getGenresTmdbIdByIds(List<String> genreIds) {
        Query query = new Query(Criteria.where("_id").in(genreIds));
        List<Genre> genres = mongoTemplate.find(query, Genre.class, DB_GENRES);

        return genres.stream()
                .map(Genre::getTmdbId)
                .collect(Collectors.toList());
    }

    private List<String> extractListIdsFromMapObject(NavigationResponse navigationResponse, String key) {
        Object IdsObj = navigationResponse.getParams().get(key);
        if (!(IdsObj instanceof List<?>)) {
            return null;
        }
        List<?> IdsList = (List<?>) IdsObj;
        return IdsList.stream()
                .filter(item -> item instanceof String)
                .map(item -> (String) item)
                .collect(Collectors.toList());
    }

    public List<GenreDTO> getAllGenres() {
        List<Genre> genres = mongoTemplate.findAll(Genre.class, DB_GENRES);

        return genres.stream()
                .map(movieMapper::toGenreDTO)
                .collect(Collectors.toList());
    }

    public List<LastestTrailersDTO> getLastestTrailers() {
        Query query = new Query();
        query.limit(12);

        List<LastestTrailers> lastTrailers = mongoTemplate.find(query, LastestTrailers.class, "movies");
        List<LastestTrailersDTO> lastestTrailersDTO = lastTrailers.stream()
                .map(item -> {
                    LastestTrailersDTO dto = new LastestTrailersDTO();
                    dto.setId(item.getId());
                    dto.setTitle(item.getTitle());
                    
                    if (item.getTrailers() != null && !item.getTrailers().isEmpty()) {
                        dto.setTrailers(List.of(item.getTrailers().get(0)));
                    } else {
                        dto.setTrailers(null);
                    }

                    return dto;
                })
                .collect(Collectors.toList());

        return lastestTrailersDTO;
    }

    public List<MovieTrendingDTO> getMoviePopular(int page) {

        if (page <= 0) {
            throw new IllegalArgumentException("Invalid page: Page must be greater than 0.");
        }

        String collectionName = "movies_popular";

        long totalResults = mongoTemplate.count(new Query(), MovieTrending.class, collectionName);
        int totalPages = (int) Math.ceil((double) totalResults / MOVIES_PER_PAGE);

        if (page > totalPages) {
            throw new IllegalArgumentException("Invalid page: Page must be less than or equal to " + totalPages + ".");
        }


        Query query = new Query();
        query.skip((long) (page - 1) * MOVIES_PER_PAGE);
        query.limit(MOVIES_PER_PAGE);
        List<MovieTrending> trendingMovies = mongoTemplate.find(query, MovieTrending.class, collectionName);

        List<MovieTrendingDTO> results = trendingMovies.stream()
                .map(movie -> {
                    if(movie.getPosterPath() != null) {
                        movie.setPosterPath(baseSmallPosterUrl + movie.getPosterPath());
                    }
                    if(movie.getBackdropPath() != null) {
                        movie.setBackdropPath(originalImageUrl + movie.getBackdropPath());
                    }

                    return movieMapper.toMovieTrendingDTO(movie);
                })
                .collect(Collectors.toList());

        return results;
    }

    public SimilarMoviesDTO getSimilarMovies(long movieId) {

        Query query = new Query(Criteria.where("tmdb_id").is(movieId));
        SimilarMovies similarMovies = mongoTemplate.findOne(query, SimilarMovies.class, "similar");

        if (similarMovies == null) {
            throw new ResourceNotFoundException("Similar movies not found.");
        }

        SimilarMoviesDTO similarMoviesDTO = movieMapper.toSimilarMoviesDTO(similarMovies);
        return similarMoviesDTO;
    }
    
    public UserMovieDTO addRating(long movieId, int rating) {
        // Check input
        Query query = new Query(Criteria.where("id").is(movieId));
        MovieDetail movieDetail = mongoTemplate.findOne(query, MovieDetail.class, DB_ALL);

        if (movieDetail == null) {
            throw new ResourceNotFoundException("Movie not found.");
        }

        if (rating < 0 || rating > 10) {
            throw new IllegalArgumentException("Invalid rating point: Rating point must be between 1 and 10.");
        }

        // Update movie vote count & vote average
        double currentTotalRating = movieDetail.getVoteAverage() * movieDetail.getVoteCount();
        int newVoteCount = movieDetail.getVoteCount() + 1;
        double newVoteAverage = (currentTotalRating + rating) / newVoteCount;

        Update update = new Update();
        update.set("vote_count", newVoteCount);
        update.set("vote_average", newVoteAverage);
        mongoTemplate.updateFirst(query, update, MovieDetail.class, DB_ALL);

        // Add rating
        MovieProfile movie = mongoTemplate.findOne(query, MovieProfile.class, DB_ALL);
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userMovieId = userId + "_" + movieId;

        Query queryAdd = new Query(Criteria.where("_id").is(userMovieId));
        Update updateAdd = new Update()
                .set("user_id", userId)
                .set("movie", movie)
                .set("rating", rating)
                .setOnInsert("in_watchlist", false)
                .setOnInsert("is_favorite", false);

        mongoTemplate.upsert(queryAdd, updateAdd, UserMovie.class, DB_USER_MOVIE);
        UserMovie result = mongoTemplate.findOne(queryAdd, UserMovie.class, DB_USER_MOVIE);
        return profileMapper.toUserMovieDTO(result);
    }

    public ReviewDTO addReview(long movieId, String review) {

        // Check input
        Query queryCheck = new Query(Criteria.where("id").is(movieId));
        MovieDetail movieDetail = mongoTemplate.findOne(queryCheck, MovieDetail.class, DB_ALL);

        if (movieDetail == null) {
            throw new ResourceNotFoundException("Movie not found.");
        }

        // Add review
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Review newReview = new Review();
        newReview.setUserId(userId);
        newReview.setMovieId(movieId);
        newReview.setReview(review);

        Review savedReview = mongoTemplate.save(newReview, DB_REVIEW);


        // Update movie review
        AuthorDetails authorDetails = new AuthorDetails();
        if (userService.isGoogleUser()) {
            authorDetails.setName(userService.getUserName());
            authorDetails.setUsername(null);
        }
        else {
            authorDetails.setUsername(userService.getUserName());
            authorDetails.setName(null);
        }
        authorDetails.setAvatarPath(null);

        String userMovieId = userId + "_" + movieId;
        Query query = new Query(Criteria.where("_id").is(userMovieId));
        UserMovie userMovie = mongoTemplate.findOne(query, UserMovie.class, DB_USER_MOVIE);
        if (userMovie == null || userMovie.getRating() == null) {
            authorDetails.setRating(null);
        }
        else {
            authorDetails.setRating(userMovie.getRating());
        }

        ReviewDetail newReviewDetail = new ReviewDetail();
        newReviewDetail.setAuthor(userService.getUserName());
        newReviewDetail.setAuthorDetails(authorDetails);
        newReviewDetail.setContent(review);
        newReviewDetail.setCreatedAt(LocalDateTime.now().toString());
        newReviewDetail.setUpdatedAt(null);
        newReviewDetail.setId(savedReview.get_id());
        newReviewDetail.setUrl(null);

        Update updateReview = new Update();
        updateReview.push("reviews", newReviewDetail);

        mongoTemplate.updateFirst(query, updateReview, MovieDetail.class, DB_ALL);

        return movieMapper.toReviewDTO(savedReview);
    }


}
