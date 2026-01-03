package com.example.movie.dto.request;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequest {

    private String displayName;
    private String avatarUrl;

    @Email(message = "Email không đúng định dạng")
    private String email;

    private String oldPassword;
    private String newPassword;
}