package com.hcmus.cineverse_be.mapper;

import com.hcmus.cineverse_be.dto.*;
import com.hcmus.cineverse_be.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MovieMapper {
    MovieTrendingDTO toMovieTrendingDTO(MovieTrending movie);
    MovieDetailDTO toMovieDetailDTO(MovieDetail movieDetail);
    MovieSearchDTO toMovieSearchDTO(MovieSearch movieSearch);
    MovieProfileDTO toMovieProfileDTO(MovieProfile movieProfile);
    LastestTrailersDTO toLastestTrailersDTO(LastestTrailers lastestTrailers);
    SimilarMoviesDTO toSimilarMoviesDTO(SimilarMovies similarMovies);
    GenreDTO toGenreDTO(Genre genre);
    CastDTO toCastDTO(Cast cast);
    CrewDTO toCrewDTO(Crew crew);

    @Mapping(source = "authorDetails", target = "authorDetails")
    ReviewDetailDTO toReviewDetailDTO(ReviewDetail reviewDetail);
    AuthorDetailsDTO toAuthorDetailsDTO(AuthorDetails authorDetails);
    ReviewDTO toReviewDTO(Review review);
}
