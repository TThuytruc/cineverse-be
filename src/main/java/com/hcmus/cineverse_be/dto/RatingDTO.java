package com.hcmus.cineverse_be.dto;

import com.hcmus.cineverse_be.entity.MovieProfile;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.time.LocalDateTime;

@Getter
@Setter
public class RatingDTO {
    private String _id;
    private String userId;
    private MovieProfileDTO movie;
    private LocalDateTime createdAt;
    private int rating;
    private String review;
}
