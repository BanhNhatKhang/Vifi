package com.example.movie.services.auth;

import com.example.movie.security.principal.UserPrincipal;
import com.example.movie.utils.AuthUtils;
import com.example.movie.dto.request.*;
import com.example.movie.dto.response.UserResponse;
import com.example.movie.enums.*;
import com.example.movie.models.*;
import com.example.movie.exception.*;
import com.example.movie.mapper.UserMapper;
import com.example.movie.repositories.*;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final AuthUtils authUtils;

    public UserResponse getCurrentUser(UUID userId) {
        if (userId == null) {
             throw new AppException("ID người dùng không được để trống", HttpStatus.BAD_REQUEST);
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException("Không tìm thấy người dùng", HttpStatus.NOT_FOUND));
        return userMapper.toResponse(user);
    }

    @Transactional
    public UserResponse registerUser(RegisterRequest registerRequest) {
        if (userRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            throw new AppException("Tên đăng nhập đã tồn tại!", HttpStatus.BAD_REQUEST);
        }

        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new AppException("Email đã tồn tại!", HttpStatus.BAD_REQUEST);
        }

        User user = User.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .displayName(registerRequest.getDisplayName())
                .passwordHash(passwordEncoder.encode(registerRequest.getPassword()))
                .role(Role.USER)
                .provider(AuthProvider.LOCAL)
                .enabled(true)
                .build();

        return userMapper.toResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponse updateUser(UUID userId, UpdateUserRequest request) {
        // Lấy ID từ token để so sánh bảo mật
        UUID currentUserId = authUtils.getCurrentUserId();
        if (!currentUserId.equals(userId)) {
            throw new AppException("Bạn không có quyền thực hiện hành động này", HttpStatus.FORBIDDEN);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException("Người dùng không tồn tại", HttpStatus.NOT_FOUND));

        // Cập nhật Profile
        if (request.getDisplayName() != null || request.getAvatarUrl() != null) {
            user.updateProfile(request.getDisplayName(), request.getAvatarUrl());
        }

        // Thay đổi Email
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new AppException("Email này đã được sử dụng", HttpStatus.BAD_REQUEST);
            }
            user.updateEmail(request.getEmail());
        }

        // Thay đổi Mật khẩu
        if (request.getNewPassword() != null && !request.getNewPassword().isBlank()) {
            if (request.getOldPassword() == null || 
                !passwordEncoder.matches(request.getOldPassword(), user.getPasswordHash())) {
                throw new AppException("Mật khẩu cũ không chính xác hoặc không được cung cấp", HttpStatus.UNAUTHORIZED);
            }
            user.updatePassword(passwordEncoder.encode(request.getNewPassword()));
        }

        return userMapper.toResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponse authenticateUser(LoginRequest loginRequest, HttpServletRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        request.getSession(true).setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext()
        );

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        
        // Xử lý an toàn cho UUID
        UUID userId = principal.getId();
        if (userId == null) {
            throw new AppException("ID người dùng không hợp lệ", HttpStatus.UNAUTHORIZED);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException("Không tìm thấy thông tin người dùng", HttpStatus.NOT_FOUND));

        user.recordLogin();
        return userMapper.toResponse(userRepository.save(user));
    }
}