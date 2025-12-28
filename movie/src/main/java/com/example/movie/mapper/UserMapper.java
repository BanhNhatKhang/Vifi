package com.example.movie.mapper;

import com.example.movie.dto.request.RegisterRequest;
import com.example.movie.dto.response.UserResponse;
import com.example.movie.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    // Từ Request -> Entity 
    @Mapping(target = "passwordHash", ignore = true) 
    User toEntity(RegisterRequest request);

    // Từ Entity -> Response 
    UserResponse toResponse(User user);
}