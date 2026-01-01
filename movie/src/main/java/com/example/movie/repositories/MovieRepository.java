package com.example.movie.repositories;

import com.example.movie.enums.MovieStatus;
import com.example.movie.models.Movie;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MovieRepository extends JpaRepository<Movie, UUID> {

    // PUBLIC
    Optional<Movie> findBySlugAndActiveTrue(String slug); // Trang chi tiết phim

    Page<Movie> findAllByActiveTrue(Pageable pageable); //Trang chủ/ danh sách phim

    Page<Movie> findByStatusAndActiveTrue(
        MovieStatus status,
        Pageable pageable
    ); // Lọc theo trạng thái phim đang chiếu/ sắp chiếu

    Page<Movie> findByReleaseYearAndActiveTrue(
        Integer releaseYear,
        Pageable pageable
    );

    // ADMIN

    boolean existsBySlug(String slug); // chechk khi tạo phim và cập nhật phim

    Optional<Movie> findByIdAndActiveTrue(UUID id); // tránh xóa phim đã xóa mềm rồi
}
