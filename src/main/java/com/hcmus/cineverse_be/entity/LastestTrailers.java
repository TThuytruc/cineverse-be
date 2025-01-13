package com.hcmus.cineverse_be.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
public class LastestTrailers {
    @Field("id")
    private String id;
    private String title;
    private List<VideoDetails> trailers;
}
