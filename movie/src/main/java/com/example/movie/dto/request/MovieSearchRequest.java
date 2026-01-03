package com.example.movie.dto.request;

import com.example.movie.enums.MovieQuality;
import com.example.movie.enums.MovieStatus;
import lombok.Data;

@Data
public class MovieSearchRequest {
    private String keyword;
    private Integer year;
    private MovieStatus status;
    private MovieQuality quality;
}