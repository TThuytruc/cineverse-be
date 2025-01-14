package com.hcmus.cineverse_be.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Field;

import static lombok.AccessLevel.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = PRIVATE)
public class AuthorDetails {
    @Field(write = Field.Write.ALWAYS)
    String name;

    @Field(write = Field.Write.ALWAYS)
    String username;

    @Field(name = "avatar_path", write = Field.Write.ALWAYS)
    String avatarPath;

    Integer rating;
}
