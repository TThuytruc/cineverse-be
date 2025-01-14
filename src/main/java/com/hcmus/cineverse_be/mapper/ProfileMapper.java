package com.hcmus.cineverse_be.mapper;

import com.hcmus.cineverse_be.dto.*;
import com.hcmus.cineverse_be.entity.*;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProfileMapper {
    MovieProfileDTO toMovieProfileDTO(MovieProfile movieProfile);
    UserMovieDTO toUserMovieDTO(UserMovie userMovie);
}
