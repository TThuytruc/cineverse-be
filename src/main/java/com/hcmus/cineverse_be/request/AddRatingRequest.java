package com.hcmus.cineverse_be.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AddRatingRequest {
    private long movieId;
    private int rating;
}
