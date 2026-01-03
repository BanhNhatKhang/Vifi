package com.example.movie.services.movie;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.movie.dto.request.MovieRequest;
import com.example.movie.dto.response.MovieBriefResponse;
import com.example.movie.dto.response.MovieResponse;
import com.example.movie.exception.AppException;
import com.example.movie.mapper.MovieMapper;
import com.example.movie.models.Movie;
import com.example.movie.repositories.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    @Mock private MovieRepository movieRepository;
    @Mock private MovieMapper movieMapper;

    @InjectMocks private MovieService movieService;

    private Movie movie;
    private MovieRequest movieRequest;
    private UUID movieId;

    @BeforeEach
    void setUp() {
        movieId = UUID.randomUUID();
        movie = Movie.builder()
                .id(movieId)
                .title("Inception")
                .slug("inception")
                .active(true)
                .build();

        movieRequest = new MovieRequest();
        movieRequest.setTitle("Inception");
        movieRequest.setSlug("inception");
    }

    // --- TEST GET ALL MOVIES ---
    @Test
    @DisplayName("Should return a page of MovieBriefResponse")
    void getAllMovies_ShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Movie> moviePage = new PageImpl<>(Collections.singletonList(movie));

        when(movieRepository.findAllByActiveTrue(pageable)).thenReturn(moviePage);
        when(movieMapper.toBriefResponse(any(Movie.class))).thenReturn(new MovieBriefResponse());

        Page<MovieBriefResponse> result = movieService.getAllMovies(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(movieRepository).findAllByActiveTrue(pageable);
    }

    // --- TEST GET BY SLUG ---
    @Test
    @DisplayName("Should return MovieResponse when slug exists and active")
    void getMovieBySlug_ShouldReturnResponse_WhenExists() {
        when(movieRepository.findBySlugAndActiveTrue("inception")).thenReturn(Optional.of(movie));
        when(movieMapper.toResponse(movie)).thenReturn(MovieResponse.builder().title("Inception").build());

        MovieResponse result = movieService.getMovieBySlug("inception");

        assertEquals("Inception", result.getTitle());
    }

    @Test
    @DisplayName("Should throw 404 when slug does not exist")
    void getMovieBySlug_ShouldThrowNotFound_WhenNotExists() {
        when(movieRepository.findBySlugAndActiveTrue("none")).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> movieService.getMovieBySlug("none"));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    // --- TEST CREATE MOVIE ---
    @Test
    @DisplayName("Should create movie successfully when slug is unique")
    void createMovie_ShouldSave_WhenSlugIsUnique() {
        // Giả lập slug chưa tồn tại
        when(movieRepository.existsBySlug(anyString())).thenReturn(false);
        when(movieMapper.toEntity(any(MovieRequest.class))).thenReturn(movie);
        when(movieRepository.save(any(Movie.class))).thenReturn(movie);
        when(movieMapper.toResponse(any(Movie.class))).thenReturn(MovieResponse.builder().title("Inception").build());

        MovieResponse response = movieService.createMovie(movieRequest);

        assertNotNull(response);
        assertEquals("Inception", response.getTitle());
        verify(movieRepository).save(any(Movie.class));
    }

    @Test
    @DisplayName("Should throw 400 when creating movie with duplicate slug")
    void createMovie_ShouldThrowBadRequest_WhenSlugExists() {
        when(movieRepository.existsBySlug(anyString())).thenReturn(true);

        AppException exception = assertThrows(AppException.class, () -> movieService.createMovie(movieRequest));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    // --- TEST UPDATE MOVIE ---
    @Test
    @DisplayName("Should update movie successfully")
    void updateMovie_ShouldUpdate_WhenExists() {
        when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));
        when(movieRepository.save(any(Movie.class))).thenReturn(movie);
        when(movieMapper.toResponse(any(Movie.class))).thenReturn(MovieResponse.builder().title("Updated").build());

        MovieResponse response = movieService.updateMovie(movieId, movieRequest);

        assertEquals("Updated", response.getTitle());
        verify(movieMapper).updateMovieFromRequest(eq(movieRequest), eq(movie));
    }

    // --- TEST DEACTIVATE ---
    @Test
    @DisplayName("Should deactivate movie successfully")
    void deactivateMovie_ShouldCallDeactivate() {
        when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));

        movieService.deactivateMovie(movieId);

        assertFalse(movie.isActive()); // Kiểm tra logic deactivate trong Entity
        verify(movieRepository).save(movie);
    }
}