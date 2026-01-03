package com.example.movie.mapper;

import com.example.movie.dto.request.MovieRequest;
import com.example.movie.dto.response.MovieResponse;
import com.example.movie.dto.response.MovieBriefResponse;
import com.example.movie.models.Movie;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, builder = @Builder(disableBuilder = false))
public interface MovieMapper {
    
    // chuyển từ request sang entity để lưu vào db
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "viewsCount", ignore = true)
    @Mapping(target = "likesCount", ignore = true)
    @Mapping(target = "averageRating", ignore = true)
    @Mapping(target = "active", ignore = true)
    Movie toEntity(MovieRequest request);

    // chuyển từ entity sang response để trả về client
    MovieResponse toResponse(Movie movie);

    // chuyển từ entity sang response ngắn gọn
    MovieBriefResponse toBriefResponse(Movie movie);

    // phân trang danh sách MovieBriefResponse
    List<MovieBriefResponse> toBriefResponseList(List<Movie> movies);

    // cập nhật Entity hiện tại từ dữ liệu Request (Dùng cho Update logic)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "viewsCount", ignore = true)
    void updateMovieFromRequest(MovieRequest request, @MappingTarget Movie movie);

}
