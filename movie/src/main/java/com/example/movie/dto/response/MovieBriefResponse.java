package com.example.movie.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

import com.example.movie.enums.MovieQuality;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MovieBriefResponse {
    private UUID id;
    private String title;
    private String slug;
    private String posterUrl;
    private Integer releaseYear;
    private MovieQuality quality;
    private BigDecimal averageRating;
}