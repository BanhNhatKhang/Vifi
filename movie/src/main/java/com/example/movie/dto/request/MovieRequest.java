package com.example.movie.dto.request;

import com.example.movie.enums.MovieQuality;
import com.example.movie.enums.MovieStatus;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieRequest {

    @NotBlank(message = "Title không được để trống")
    @Size(max = 200, message = "Title không được vượt quá 200 ký tự")
    private String title;

    @NotBlank(message = "Slug không được để trống")
    @Size(max = 250, message = "Slug không được vượt quá 250 ký tự")
    private String slug;

    private String description;

    @NotNull(message = "Năm phát hành không được để trống")
    @Min(value = 1900, message = "Năm phát hành không hợp lệ")
    private Integer releaseYear;

    @NotNull(message = "Thời lượng không được để trống")
    @Positive(message = "Thời lượng phải là số dương")
    private Integer durationMinutes;

    @NotNull(message = "Chất lượng phim không được để trống")
    private MovieQuality quality;

    @NotNull(message = "Trạng thái phim không được để trống")
    private MovieStatus status;

    @Size(max = 500, message = "URL poster không được vượt quá 500 ký tự")
    private String posterUrl;

    @Size(max = 500, message = "URL trailer không được vượt quá 500 ký tự")
    private String trailerUrl;
    
}
