package com.example.movie.services.auth;

import com.example.movie.security.principal.UserPrincipal;
import com.example.movie.dto.request.*;
import com.example.movie.dto.response.UserResponse;
import com.example.movie.enums.*;
import com.example.movie.models.*;
import com.example.movie.exception.*;
import com.example.movie.mapper.UserMapper;
import com.example.movie.repositories.*;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.Optional;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public AuthService(AuthenticationManager authenticationManager,
                       UserRepository userRepository, 
                       PasswordEncoder passwordEncoder, 
                       UserMapper userMapper) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }
    public UserResponse getCurrentUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException("Không tìm thấy người dùng", HttpStatus.NOT_FOUND));
        return userMapper.toResponse(user);
    }

    @Transactional
    public UserResponse registerUser(RegisterRequest registerRequest) {
        
        if (userRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            throw new DuplicateResourceException("Tên đăng nhập đã tồn tại!");
        }

        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Email đã tồn tại!");
        }

        User userFromDto = userMapper.toEntity(registerRequest);
        if (userFromDto == null) {
            throw new AppException("Lỗi xử lý dữ liệu đầu vào", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        User user = User.builder()
                .username(userFromDto.getUsername())
                .email(userFromDto.getEmail())
                .displayName(userFromDto.getDisplayName())
                .passwordHash(passwordEncoder.encode(registerRequest.getPassword()))
                .role(Role.USER)
                .provider(AuthProvider.LOCAL)
                .enabled(true)
                .build();

        User userToSave = user; 
        User savedUser = userRepository.save(userToSave);

        return userMapper.toResponse(savedUser);
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
        request.getSession(true)
                .setAttribute(
                    HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    SecurityContextHolder.getContext()
                );

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        
        UUID userId = Optional.ofNullable(principal.getId())
                .orElseThrow(() -> new AppException("ID người dùng không hợp lệ", HttpStatus.UNAUTHORIZED));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException("Không tìm thấy thông tin người dùng", HttpStatus.NOT_FOUND));

        user.setLastLoginAt(LocalDateTime.now());
        User updatedUser = userRepository.save(user);

        return userMapper.toResponse(updatedUser);
    }
}