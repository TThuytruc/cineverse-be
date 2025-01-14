package com.hcmus.cineverse_be.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AddReviewRequest {
    private long movieId;
    private String review;
}
