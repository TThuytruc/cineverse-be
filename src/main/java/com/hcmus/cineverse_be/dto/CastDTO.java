package com.hcmus.cineverse_be.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CastDTO {
    private long id;
    private String name;
    private String profilePath;
    private String character;
}
