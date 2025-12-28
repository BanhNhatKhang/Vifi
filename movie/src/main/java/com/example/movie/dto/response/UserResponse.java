package com.example.movie.dto.response;

import com.example.movie.enums.Role;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class UserResponse {
    private UUID id;
    private String username;
    private String email;
    private String displayName;
    private String avatarUrl;
    private Role role;
    private LocalDateTime createdAt;
}