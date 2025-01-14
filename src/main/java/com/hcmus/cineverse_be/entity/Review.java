package com.hcmus.cineverse_be.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
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
    @Field(name = "updated_at", write = Field.Write.ALWAYS)
    String updatedAt;

    @Field("id")
    String id;
    @Field(write = Field.Write.ALWAYS)
    String url;
}
