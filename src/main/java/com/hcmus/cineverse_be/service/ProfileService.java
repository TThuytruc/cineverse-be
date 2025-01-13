package com.hcmus.cineverse_be.service;

import com.hcmus.cineverse_be.dto.RatingDTO;
import com.hcmus.cineverse_be.entity.Rating;
import com.hcmus.cineverse_be.mapper.MovieMapper;
import com.hcmus.cineverse_be.response.rating.RatingsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProfileService {
    private final int RATINGS_PER_PAGE = 10;

    private final String DB_RATINGS = "ratings";

    @Autowired
    private MovieMapper movieMapper;

    @Autowired
    private MongoTemplate mongoTemplate;


    public RatingsResponse getRatingsByUser(int page) {

        if (page <= 0) {
            throw new IllegalArgumentException("Invalid page: Page must be greater than 0.");
        }

        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Query query = new Query();
        query.addCriteria(Criteria.where("user_id").is(userId));

        long totalResults = mongoTemplate.count(query, Rating.class, DB_RATINGS);
        int totalPages = (int) Math.ceil((double) totalResults / RATINGS_PER_PAGE);

        if (page > totalPages) {
            throw new IllegalArgumentException("Invalid page: Page must be less than or equal to " + totalPages + ".");
        }

        query.skip((long) (page - 1) * RATINGS_PER_PAGE);
        query.limit(RATINGS_PER_PAGE);

        List<Rating> ratings = mongoTemplate.find(query, Rating.class, DB_RATINGS);

        List<RatingDTO> results = ratings.stream()
                .map(rating -> movieMapper.toRatingDTO(rating))
                .collect(Collectors.toList());

        return new RatingsResponse(page, results, totalPages, (int) totalResults);
    }
}
