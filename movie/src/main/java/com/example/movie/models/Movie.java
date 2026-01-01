package com.example.movie.models;

import com.example.movie.enums.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;

import org.hibernate.annotations.*;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import jakarta.persistence.Index;
import lombok.*;

@Entity
@Table(
    name = "movies",
    indexes = {
        @Index(name = "idx_movies_slug", columnList = "slug"),
        @Index(name = "idx_movies_release_year", columnList = "release_year"),
        @Index(name = "idx_movies_status", columnList = "status")
    }
)
@Builder
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
public class Movie {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, unique = true, length = 250)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "release_year",nullable = false)
    private Integer releaseYear;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private MovieQuality quality;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private MovieStatus status;

    @Column(name = "poster_url", length = 500)
    private String posterUrl;

    @Column(name = "trailer_url", length = 500)
    private String trailerUrl;

    @Builder.Default
    @Column(name = "views_count", nullable = false)
    private Long viewsCount = 0L;

    @Builder.Default
    @Column(name = "likes_count")
    private Long likesCount = 0L;

    @Builder.Default
    @Column(name = "average_rating", precision = 3, scale = 2)
    private BigDecimal averageRating = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public void increaseViewCount() {
        this.viewsCount = (this.viewsCount == null) ? 1L : this.viewsCount + 1;
    }

    public void increaseLikeCount() {
        this.likesCount = (this.likesCount == null) ? 1L : this.likesCount + 1;
    }

    public void deactivate() {
        this.active = false;
    }


    public void activate() {
        this.active = true;
    }
}
