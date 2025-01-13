package com.hcmus.cineverse_be.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
public class VideoDetails {
    @Field("id")
    private String id;

    private String name;
    private String key;
    private String site;
    private String type;
    private String official;

}
