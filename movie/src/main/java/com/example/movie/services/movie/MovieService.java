package com.example.movie.services.movie;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.example.movie.dto.request.MovieRequest;
import com.example.movie.dto.response.MovieBriefResponse;
import com.example.movie.dto.response.MovieResponse;
import com.example.movie.exception.AppException;
import com.example.movie.mapper.MovieMapper;
import com.example.movie.models.Movie;
import com.example.movie.repositories.MovieRepository;
import com.example.movie.utils.SlugUtils;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;

    @Transactional(readOnly = true)
    public Page<MovieBriefResponse> getAllMovies(Pageable pageable) {
        return movieRepository.findAllByActiveTrue(pageable).map(movieMapper::toBriefResponse);
    }

    @Transactional(readOnly = true)
    public MovieResponse getMovieBySlug(String slug) {
        Movie movie = movieRepository.findBySlugAndActiveTrue(slug)
                            .orElseThrow(() -> new AppException("Không tìm thấy phim", HttpStatus.NOT_FOUND));
        return movieMapper.toResponse(movie);
    }

    @Transactional
    public MovieResponse createMovie(MovieRequest request) {

        String rawSlug = (request.getSlug() == null || request.getSlug().isBlank()) 
                                    ? request.getTitle() 
                                    : request.getSlug();
        String slug = SlugUtils.makeSlug(rawSlug);

        if (movieRepository.existsBySlug(request.getSlug())) {
            throw new AppException("Slug phim đã tồn tại", HttpStatus.BAD_REQUEST);
        }

        request.setSlug(slug);
        Movie movie = movieMapper.toEntity(request);
        return movieMapper.toResponse(movieRepository.save(movie));
    }

    @Transactional
    public MovieResponse updateMovie(UUID id, MovieRequest request) {
        Movie movie = movieRepository.findById(id)
                        .orElseThrow(() -> new AppException("Phim không tồn tại", HttpStatus.NOT_FOUND));
        if (!movie.getSlug().equals(request.getSlug()) && movieRepository.existsBySlug(request.getSlug())) {
            throw new AppException("Slug mới đã được sử dụng bởi phim khác", HttpStatus.BAD_REQUEST);
        }

        movieMapper.updateMovieFromRequest(request, movie);

        return movieMapper.toResponse(movieRepository.save(movie));
    }

    @Transactional
    public void deactivateMovie(UUID id) {
        Movie movie = movieRepository.findById(id)
                        .orElseThrow(() -> new AppException("Phim không tồn tại", HttpStatus.NOT_FOUND));
        movie.deactivate();
        movieRepository.save(movie);
    }
    
}
