package com.hcmus.cineverse_be.response.navigate;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MovieNavigationParams {
    List<String> movie_ids;
}
