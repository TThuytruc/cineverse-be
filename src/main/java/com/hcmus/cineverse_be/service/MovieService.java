package com.hcmus.cineverse_be.service;

import com.hcmus.cineverse_be.dto.*;
import com.hcmus.cineverse_be.entity.MovieDetail;
import com.hcmus.cineverse_be.entity.MovieSearch;
import com.hcmus.cineverse_be.entity.MovieTrending;
import com.hcmus.cineverse_be.exception.ResourceNotFoundException;
import com.hcmus.cineverse_be.mapper.MovieMapper;
import com.hcmus.cineverse_be.response.AIApiResponse;
import com.hcmus.cineverse_be.response.movie.SearchMovieResponse;
import com.hcmus.cineverse_be.response.movie.TrendingMoviesResponse;
import com.hcmus.cineverse_be.response.retriever.RetrieverResponse;
import jakarta.annotation.PostConstruct;

import org.checkerframework.checker.units.qual.m;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

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


    private WebClient webClient;

    @Value("${tmdb.api.base-image-url}")
    private String baseImageUrl;

    @Value("${tmdb.api.original-image-url}")
    private String originalImageUrl;

    private String baseSmallPosterUrl;
    private String baseLargePosterUrl;
    private String baseSmallProfileUrl;

    @Autowired
    private MovieMapper movieMapper;

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

        List<ReviewDTO> listReviews = movieDetailDTO.getReviews();
        if (listReviews != null) {
            listReviews.forEach(review -> {
                if(review.getAuthorDetails().getAvatarPath() != null) {
                    review.getAuthorDetails().setAvatarPath(baseSmallProfileUrl + review.getAuthorDetails().getAvatarPath());
                }
            });
        }

        return movieDetailDTO;
    }

    public SearchMovieResponse getSearchMovies(String query, int page) {

        if (page <= 0) {
            throw new IllegalArgumentException("Invalid page: Page must be greater than 0.");
        }
        if (query == null || query.isEmpty()) {
            throw new IllegalArgumentException("There are no movies that matched your query");
        }

        String collectionName = "movies";

        Query countQuery = new Query();
        countQuery.addCriteria(
                Criteria.where("title").regex(query, "i")
        );

        long totalResults = mongoTemplate.count(countQuery, MovieSearch.class, collectionName);
        int totalPages = (int) Math.ceil((double) totalResults / MOVIES_PER_PAGE);

        if (page > totalPages + 1) {
            throw new IllegalArgumentException("Invalid page: Page must be less than or equal to " + totalPages + ".");
        }


        Query searchQuery = new Query();
        searchQuery.addCriteria(
                Criteria.where("title").regex(query, "i")
        );
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

    public RetrieverResponse getLlmMovieRetrieverResponse(
            String collectionName,
            String query,
            int amount,
            double threshold) {

        RetrieverResponse response = webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/retriever/")
                                .queryParam("llm_api_key", llmApiKey)
                                .queryParam("collection_name", collectionName)
                                .queryParam("query", query)
                                .queryParam("amount", amount)
                                .queryParam("threshold", threshold)
                                .build())
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<AIApiResponse<RetrieverResponse>>() {})
                        .map(AIApiResponse::getData)
                        .block();


         return response;
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

}
