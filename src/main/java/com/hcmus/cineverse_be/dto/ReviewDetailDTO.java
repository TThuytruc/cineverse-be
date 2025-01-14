package com.hcmus.cineverse_be.dto;

import com.google.auto.value.AutoValue.Builder;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewDetailDTO {
    AuthorDetailsDTO authorDetails;
    String author;
    String content;
    String createdAt;
    String id;
    String updatedAt;
    String url;
}
