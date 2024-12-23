package com.hcmus.cineverse_be.mapper;

import com.hcmus.cineverse_be.dto.GenreDTO;
import com.hcmus.cineverse_be.dto.MovieTrendingDTO;
import com.hcmus.cineverse_be.dto.MovieDetailDTO;
import com.hcmus.cineverse_be.entity.Genre;
import com.hcmus.cineverse_be.entity.MovieDetail;
import com.hcmus.cineverse_be.entity.MovieTrending;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MovieMapper {
    MovieTrendingDTO toMovieTrendingDTO(MovieTrending movie);
    MovieDetailDTO toMovieDetailDTO(MovieDetail movieDetail);
    GenreDTO toGenreDTO(Genre genre);
}
