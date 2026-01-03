package com.example.movie.controllers.movie;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.movie.dto.request.MovieRequest;
import com.example.movie.dto.response.MovieBriefResponse;
import com.example.movie.dto.response.MovieResponse;
// import com.example.movie.models.Movie;
import com.example.movie.services.movie.MovieService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
// import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;




@RequiredArgsConstructor
@RestController
@RequestMapping("/api/movies")
public class MovieController {
    
    private final MovieService movieService;

    @GetMapping
    public ResponseEntity<Page<MovieBriefResponse>> getAllMovies(
        @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(movieService.getAllMovies(pageable));
    }

    @GetMapping("/{slug}")
    public ResponseEntity<MovieResponse> getMovieBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(movieService.getMovieBySlug(slug));
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MovieResponse> createMovie(@Valid @RequestBody MovieRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(movieService.createMovie(request));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MovieResponse> updateMovie(@PathVariable UUID id, @Valid @RequestBody MovieRequest request) {
        return ResponseEntity.ok(movieService.updateMovie(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMovie(@PathVariable UUID id) {
        movieService.deactivateMovie(id);
        return ResponseEntity.noContent().build();
    }

}
