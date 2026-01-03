package com.example.movie.dto.response;

import com.example.movie.enums.MovieQuality;
import com.example.movie.enums.MovieStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieResponse {
    private UUID id;
    private String title;
    private String slug;
    private String description;
    private Integer releaseYear;
    private Integer durationMinutes;
    private MovieQuality quality;
    private MovieStatus status;
    private String posterUrl;
    private String trailerUrl;
    private Long viewsCount;
    private Long likesCount;
    private BigDecimal averageRating;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}