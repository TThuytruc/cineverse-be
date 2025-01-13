package com.hcmus.cineverse_be.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VideoDetailsDTO {
    private String id;
    private String name;
    private String key;
    private String site;
    private String type;
    private String official;
}
