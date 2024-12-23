package com.hcmus.cineverse_be.service;

import com.hcmus.cineverse_be.dto.MovieDetailDTO;
import com.hcmus.cineverse_be.dto.MovieTrendingDTO;
import com.hcmus.cineverse_be.entity.MovieDetail;
import com.hcmus.cineverse_be.entity.MovieTrending;
import com.hcmus.cineverse_be.exception.ResourceNotFoundException;
import com.hcmus.cineverse_be.mapper.MovieMapper;
import com.hcmus.cineverse_be.response.PaginationResponse;
import com.hcmus.cineverse_be.response.movie.TrendingMoviesResponse;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MovieService {
    private final String SMALL_POSTER_SIZE = "w342";
    private final String LARGE_POSTER_SIZE = "w780";

    private final int MOVIES_PER_PAGE = 20;
    private final String DB_ALL = "movies";
    private final String DB_TRENDING_DAY = "movies_trending_day";
    private final String DB_TRENDING_WEEK = "movies_trending_week";


    @Value("${tmdb.api.base-image-url}")
    private String baseImageUrl;

    private String baseSmallPosterUrl;
    private String baseLargePosterUrl;

    @Autowired
    private MovieMapper movieMapper;

    @Autowired
    private MongoTemplate mongoTemplate;

    @PostConstruct
    public void init() {
        this.baseSmallPosterUrl = baseImageUrl + SMALL_POSTER_SIZE;
        this.baseLargePosterUrl = baseImageUrl + LARGE_POSTER_SIZE;
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
                    movie.setPosterPath(baseSmallPosterUrl + movie.getPosterPath());
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
        movieDetailDTO.setPosterPath(baseLargePosterUrl + movieDetailDTO.getPosterPath());

        return movieDetailDTO;
    }


//    public Mono<SearchMoviesResponse> getSearchMovies(String query, int page) {
//        return webClient.get()
//                .uri("search/movie" + "?query=" + query + "&page=" + page)
//                .retrieve()
//                .onStatus(
//                        status -> status.value() == 400,
//                        clientResponse -> clientResponse.bodyToMono(Map.class).flatMap(body -> {
//                            if (body.containsKey("status_code")) {
//                                int statusCode = (Integer) body.get("status_code");
//
//                                if (statusCode == 22) {
//                                    return Mono.error(new IllegalArgumentException((String) body.get("status_message")));
//                                } else {
//                                    return Mono.error(new RuntimeException("An error occurred while searching movies: " + body.get("status_message")));
//                                }
//                            }
//
//                            return Mono.error(new RuntimeException("An error occurred while searching movies."));
//                        })
//                )
//                .onStatus(
//                        status -> status.value() != 200 && status.value() != 400,
//                        clientResponse -> Mono.error(new RuntimeException("An error occurred while searching movies."))
//                )
//                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
//                .map(response -> {
//                    @SuppressWarnings("unchecked")
//                    List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");
//                    List<MovieDetailResponse> filteredData = results.stream()
//                            .map(item -> {
//                                MovieDetailResponse moviesSearchResult = new MovieDetailResponse();
//                                moviesSearchResult.setId(((Number) item.get("id")).longValue());
//                                moviesSearchResult.setTitle((String) item.get("title"));
//                                moviesSearchResult.setPosterPath(baseSmallPosterUrl + item.get("poster_path"));
//                                moviesSearchResult.setReleaseDate((String) item.get("release_date"));
//                                moviesSearchResult.setVoteAverage((double) item.get("vote_average"));
//                                moviesSearchResult.setVoteCount((int) item.get("vote_count"));
//
//                                return moviesSearchResult;
//
//                            })
//                            .collect(Collectors.toList());
//
//                    SearchMoviesResponse searchMoviesResponse = new SearchMoviesResponse();
//                    searchMoviesResponse.setPage((int) response.get("page"));
//                    searchMoviesResponse.setResults(filteredData);
//                    searchMoviesResponse.setTotalPages((int) response.get("total_pages"));
//                    searchMoviesResponse.setTotalResults((int) response.get("total_results"));
//
//                    return searchMoviesResponse;
//                });
//    }
}
