package com.hcmus.cineverse_be.dto;

import java.util.List;

import com.hcmus.cineverse_be.entity.VideoDetails;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LastestTrailersDTO {
    private String id;
    private String title;
    private List<VideoDetails> trailers;
}
