package com.example.movie.controllers.movie;

// import com.example.movie.controllers.movie.MovieController;
import com.example.movie.dto.request.MovieRequest;
import com.example.movie.dto.response.MovieBriefResponse;
import com.example.movie.dto.response.MovieResponse;
import com.example.movie.enums.MovieQuality;
import com.example.movie.enums.MovieStatus;
import com.example.movie.services.movie.MovieService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MovieController.class)
@AutoConfigureMockMvc(addFilters = false)
class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MovieService movieService;

    @Autowired
    private ObjectMapper objectMapper;

    // ---------- GET /api/movies ----------

    @Test
    @DisplayName("GET /api/movies - should return paged movie list")
    void getAllMovies_shouldReturnPage() throws Exception {

        MovieBriefResponse movie = new MovieBriefResponse();
        movie.setId(UUID.randomUUID());
        movie.setTitle("Inception");
        movie.setSlug("inception");
        movie.setPosterUrl("poster.jpg");
        movie.setReleaseYear(2010);
        movie.setQuality(MovieQuality.FHD);
        movie.setAverageRating(BigDecimal.valueOf(8.8));

        Page<MovieBriefResponse> page = new PageImpl<>(List.of(movie));

        Mockito.when(movieService.getAllMovies(Mockito.any()))
                .thenReturn(page);

        mockMvc.perform(get("/api/movies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].title").value("Inception"))
                .andExpect(jsonPath("$.content[0].slug").value("inception"));
    }

    // ---------- GET /api/movies/{slug} ----------

    @Test
    @DisplayName("GET /api/movies/{slug} - should return movie detail")
    void getMovieBySlug_shouldReturnMovie() throws Exception {

        MovieResponse response = MovieResponse.builder()
                .id(UUID.randomUUID())
                .title("Inception")
                .slug("inception")
                .status(MovieStatus.ONGOING)
                .quality(MovieQuality.FHD)
                .releaseYear(2010)
                .durationMinutes(148)
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();

        Mockito.when(movieService.getMovieBySlug("inception"))
                .thenReturn(response);

        mockMvc.perform(get("/api/movies/inception"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Inception"))
                .andExpect(jsonPath("$.slug").value("inception"));
    }

    // ---------- POST /api/movies ----------

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /api/movies - ADMIN can create movie")
    void createMovie_adminSuccess() throws Exception {

        MovieRequest request = MovieRequest.builder()
                .title("Inception")
                .slug("inception")
                .releaseYear(2010)
                .durationMinutes(148)
                .quality(MovieQuality.FHD)
                .status(MovieStatus.ONGOING)
                .build();

        MovieResponse response = MovieResponse.builder()
                .id(UUID.randomUUID())
                .title("Inception")
                .slug("inception")
                .build();

        Mockito.when(movieService.createMovie(Mockito.any()))
                .thenReturn(response);

        mockMvc.perform(post("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Inception"));
    }

    @Test
    @DisplayName("POST /api/movies - should return 400 when not admin")
    void createMovie_forbidden() throws Exception {

        mockMvc.perform(post("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(jsonPath("$.message").value("Dữ liệu không hợp lệ"))
                .andExpect(jsonPath("$.errors.title").exists());
    }

    // ---------- PUT /api/movies/{id} ----------

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("PUT /api/movies/{id} - admin can update movie")
    void updateMovie_adminSuccess() throws Exception {

        UUID id = UUID.randomUUID();

        MovieRequest request = MovieRequest.builder()
                .title("Updated title")
                .slug("updated-title")
                .releaseYear(2020)
                .durationMinutes(120)
                .quality(MovieQuality.HD)
                .status(MovieStatus.COMING_SOON)
                .build();

        MovieResponse response = MovieResponse.builder()
                .id(id)
                .title("Updated title")
                .slug("updated-title")
                .build();

        Mockito.when(movieService.updateMovie(Mockito.eq(id), Mockito.any()))
                .thenReturn(response);

        mockMvc.perform(put("/api/movies/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated title"));
    }

    // ---------- DELETE /api/movies/{id} ----------

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("DELETE /api/movies/{id} - admin can deactivate movie")
    void deleteMovie_adminSuccess() throws Exception {

        UUID id = UUID.randomUUID();

        Mockito.doNothing().when(movieService).deactivateMovie(id);

        mockMvc.perform(delete("/api/movies/{id}", id))
                .andExpect(status().isNoContent());
    }
}
