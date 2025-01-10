package com.hcmus.cineverse_be.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Field;

import static lombok.AccessLevel.PRIVATE;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = PRIVATE)
public class Review {
    String author;

    @Field("author_details")
    AuthorDetails authorDetails;

    String content;

    @Field("created_at")
    String createdAt;
    @Field("updated_at")
    String updatedAt;

    @Field("id")
    String id;
    String url;

}
