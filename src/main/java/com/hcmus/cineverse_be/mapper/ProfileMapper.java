package com.hcmus.cineverse_be.mapper;

import com.hcmus.cineverse_be.dto.*;
import com.hcmus.cineverse_be.entity.*;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProfileMapper {
    WatchListDTO toWatchListDTO(WatchList watchList);
//    MovieProfileDTO toMovieProfileDTO(MovieProfile movieProfile);
}
