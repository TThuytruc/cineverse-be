package com.hcmus.cineverse_be.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import jakarta.annotation.PostConstruct;

import com.hcmus.cineverse_be.dto.CastDTO;
import com.hcmus.cineverse_be.dto.CastDetailDTO;
import com.hcmus.cineverse_be.entity.Cast;
import com.hcmus.cineverse_be.entity.CastDetail;
import com.hcmus.cineverse_be.exception.ResourceNotFoundException;
import com.hcmus.cineverse_be.mapper.CastMapper;
import com.hcmus.cineverse_be.mapper.MovieMapper;
import com.hcmus.cineverse_be.response.cast.PopularCastResponse;

@Service
public class CastService {
    private final String SMALL_PROFILE_SIZE = "w185";

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private MovieMapper movieMapper;

    @Autowired
    private CastMapper castMapper;

    private final int CASTS_PER_PAGE = 20;
    private final String DB_PEOPLE_POPULAR = "people_popular";
    private final String DB_PEOPLE = "people";

    private String baseSmallProfileUrl;
    private String baseProfileUrl = "https://media.themoviedb.org/t/p/w300_and_h450_bestv2";

    @Value("${tmdb.api.base-image-url}")
    private String baseImageUrl;

    @PostConstruct
    public void init() {
        this.baseSmallProfileUrl = baseImageUrl + SMALL_PROFILE_SIZE;
    }

    public PopularCastResponse getPopularCast(String query, int page) {

        if (page <= 0) {
            throw new IllegalArgumentException("Invalid page: Page must be greater than 0.");
        }

        String collectionName = DB_PEOPLE_POPULAR;
        Query countQuery = new Query();

        if (query != null && !query.isEmpty()) {
            countQuery.addCriteria(Criteria.where("name").regex(query, "i"));
        }

        long totalResults = mongoTemplate.count(countQuery, Cast.class, collectionName);
        int totalPages = (int) Math.ceil((double) totalResults / CASTS_PER_PAGE);

        if (page > totalPages + 1) {
            throw new IllegalArgumentException("Invalid page: Page must be less than or equal to " + totalPages + ".");
        }


        Query searchQuery = new Query();
        if (query != null && !query.isEmpty()) {
            searchQuery.addCriteria(Criteria.where("name").regex(query, "i"));
        }
        searchQuery.skip((long) (page - 1) * CASTS_PER_PAGE);
        searchQuery.limit(CASTS_PER_PAGE);
        List<Cast> popularCasts = mongoTemplate.find(searchQuery, Cast.class, collectionName);

        List<CastDTO> results = popularCasts.stream()
                .map(cast -> {
                    if(cast.getProfilePath() != null) {
                        cast.setProfilePath(baseSmallProfileUrl + cast.getProfilePath());
                    }
                    return movieMapper.toCastDTO(cast);
                })
                .collect(Collectors.toList());

        return new PopularCastResponse(page, results, totalPages, (int) totalResults);
    }

    public CastDetailDTO getCastDetail(long castId) {

        Query query = new Query(Criteria.where("id").is(castId));
        CastDetail castDetail = mongoTemplate.findOne(query, CastDetail.class, DB_PEOPLE);

        if (castDetail == null) {
            throw new ResourceNotFoundException("Cast not found.");
        }

        CastDetailDTO castDetailDTO = castMapper.toCastDetailDTO(castDetail);

        if(castDetailDTO.getProfilePath() != null) {
            castDetailDTO.setProfilePath(baseProfileUrl + castDetailDTO.getProfilePath());
        }


        return castDetailDTO;
    }
}
