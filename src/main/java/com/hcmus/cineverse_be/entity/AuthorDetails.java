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
    String name;
    String username;

    @Field("avatar_path")
    String avatarPath;
    Integer rating;

}
