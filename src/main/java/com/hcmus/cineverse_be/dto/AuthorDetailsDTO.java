package com.hcmus.cineverse_be.dto;


import com.google.auto.value.AutoValue;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AutoValue.Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthorDetailsDTO {
    String name;
    String username;
    String avatarPath;
    String rating;
}
