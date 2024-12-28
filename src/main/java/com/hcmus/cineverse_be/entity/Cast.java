package com.hcmus.cineverse_be.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
public class Cast {
    @Field("id")
    private long id;

    private String name;

    @Field("profile_path")
    private String profilePath;

    private String character;
}
