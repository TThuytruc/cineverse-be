package com.hcmus.cineverse_be.mapper;

import com.hcmus.cineverse_be.dto.*;
import com.hcmus.cineverse_be.entity.*;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MovieMapper {
    MovieTrendingDTO toMovieTrendingDTO(MovieTrending movie);
    MovieDetailDTO toMovieDetailDTO(MovieDetail movieDetail);
    GenreDTO toGenreDTO(Genre genre);
    CastDTO toCastDTO(Cast cast);
    CrewDTO toCrewDTO(Crew crew);
}
