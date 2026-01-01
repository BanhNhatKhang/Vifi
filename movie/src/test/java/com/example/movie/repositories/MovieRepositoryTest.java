package com.example.movie.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.movie.enums.MovieQuality;
import com.example.movie.enums.MovieStatus;
import com.example.movie.models.Movie;

import java.util.Optional;

@DataJpaTest
public class MovieRepositoryTest {

    @Autowired
    private MovieRepository movieRepository;

    private Movie buildMovie( 
        String title, 
        String slug,
        Integer releaseYear,
        MovieStatus status,
        boolean active

    ) {
        return Movie.builder()
                .title(title)
                .slug(slug)
                .description("Test description")
                .releaseYear(releaseYear)
                .durationMinutes(120)
                .quality(MovieQuality.HD)
                .status(status)
                .active(active)
                .build();
    }

    // FindBySlugAndActiveTrue
    @Test
    @DisplayName("Should find movie by slug when active = true")
    void shouldFindMovieBySlug_WhenActive() {
        Movie movie = buildMovie(
            "Interstella", 
            "Interstella", 
            2014, 
            MovieStatus.RELEASED, 
            true);

        movieRepository.save(movie);
        
        Optional<Movie> result = movieRepository.findBySlugAndActiveTrue("Interstella");

        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("Interstella");
    }

    // FindBySlugAndActiveFalse
    @Test
    @DisplayName("Should not find movie by slug when active = false")
    void shouldNotFindMovieBySlug_WhenInactive() {
        Movie movie = buildMovie(
            "Inception", 
            "Inception", 
            2010, 
            MovieStatus.RELEASED, 
            false);

        movieRepository.save(movie);
        
        Optional<Movie> result = movieRepository.findBySlugAndActiveTrue("Inception");

        assertThat(result).isEmpty();
    }

    // FindByStatusAndActiveTrue
    @Test
    @DisplayName("Should return movies by status and active")
    void shouldReturnMovieByStatusAndActive() {
        Movie movie1 = buildMovie(
            "Movie One", 
            "movie-one", 
            2022, 
            MovieStatus.ONGOING, 
            true);
        Movie movie2 = buildMovie(
            "Movie Two", 
            "movie-two", 
            2023, 
            MovieStatus.ONGOING, 
            true);
        Movie movie3 = buildMovie(
            "Movie Three", 
            "movie-three", 
            2021, 
            MovieStatus.ONGOING, 
            false);

        movieRepository.save(movie1);
        movieRepository.save(movie2);
        movieRepository.save(movie3);

        Page<Movie> result = movieRepository.findByStatusAndActiveTrue(MovieStatus.ONGOING, PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).allMatch(movie -> movie.getStatus() == MovieStatus.ONGOING && movie.isActive());
    }

    // findByReleaseYearAndActiveTrue
    @Test
    @DisplayName("Should return movies by release year and active")
    void shouldReturnMoviesByReleaseYearAndActive() {
        movieRepository.save(
            buildMovie("Movie 2024", "movie-2024", 2024, MovieStatus.RELEASED, true)
        );
        movieRepository.save(
            buildMovie("Movie 2023", "movie-2023", 2023, MovieStatus.RELEASED, true)
        );
        movieRepository.save(
            buildMovie("Movie 2024 inactive", "movie-2024-x", 2024, MovieStatus.RELEASED, false)
        );

        Page<Movie> result =
            movieRepository.findByReleaseYearAndActiveTrue(
                2024,
                PageRequest.of(0, 10)
            );

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getReleaseYear()).isEqualTo(2024);
    }

    // findAllByActiveTrue
    @Test
    @DisplayName("Should return only active movies")
    void shouldReturnOnlyActiveMovies() {
        movieRepository.save(
            buildMovie("Active Movie", "active-movie", 2022, MovieStatus.RELEASED, true)
        );
        movieRepository.save(
            buildMovie("Inactive Movie", "inactive-movie", 2022, MovieStatus.RELEASED, false)
        );

        Page<Movie> result =
            movieRepository.findAllByActiveTrue(PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).isActive()).isTrue();
    }

    // existsBySlug 
    @Test
    @DisplayName("Should return true when slug exists")
    void shouldCheckExistsBySlug() {
        movieRepository.save(
            buildMovie("Slug Test", "slug-test", 2021, MovieStatus.RELEASED, true)
        );

        boolean exists = movieRepository.existsBySlug("slug-test");

        assertThat(exists).isTrue();
        assertThat(movieRepository.existsBySlug("not-exist")).isFalse();
    }

    // findByIdAndActiveTrue
    @Test
    @DisplayName("Should find active movie by id")
    void shouldFindMovieByIdAndActive() {
        Movie movie = movieRepository.save(
            buildMovie("Admin Movie", "admin-movie", 2020, MovieStatus.RELEASED, true)
        );

        Optional<Movie> result =
            movieRepository.findByIdAndActiveTrue(movie.getId());

        assertThat(result).isPresent();
    }

}
