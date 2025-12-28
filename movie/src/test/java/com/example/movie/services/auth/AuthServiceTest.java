package com.example.movie.services.auth;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.example.movie.dto.request.LoginRequest;
import com.example.movie.dto.request.RegisterRequest;
import com.example.movie.dto.response.UserResponse;
import com.example.movie.exception.AppException;
import com.example.movie.exception.DuplicateResourceException;
import com.example.movie.mapper.UserMapper;
import com.example.movie.models.User;
import com.example.movie.repositories.UserRepository;
import com.example.movie.security.principal.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private UserMapper userMapper;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private HttpServletRequest httpServletRequest;
    @Mock private HttpSession httpSession;

    @InjectMocks private AuthService authService;

    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setEmail("test@gmail.com");
        registerRequest.setPassword("password123");
    }

    // --- TEST REGISTER ---

    @Test
    void registerUser_ShouldThrowException_WhenEmailExists() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(User.builder().build()));
        
        // THÊM DÒNG NÀY: Giả lập mapper không trả về null để vượt qua bước check đầu vào
        // when(userMapper.toEntity(any())).thenReturn(User.builder().build());

        assertThrows(DuplicateResourceException.class, () -> authService.registerUser(registerRequest));
    }

    // --- TEST AUTHENTICATE/LOGIN ---

    @Test
    void authenticateUser_ShouldReturnUserResponse_WhenCredentialsAreValid() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        // Giả lập xác thực thành công
        Authentication auth = mock(Authentication.class);
        User user = User.builder()
            .id(UUID.randomUUID()) // PHẢI CÓ ID Ở ĐÂY
            .username("testuser")
            .build();
        UserPrincipal principal = UserPrincipal.fromUser(user);
        
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(auth.getPrincipal()).thenReturn(principal);
        when(httpServletRequest.getSession(true)).thenReturn(httpSession);
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(userMapper.toResponse(any())).thenReturn(UserResponse.builder().username("testuser").build());

        UserResponse response = authService.authenticateUser(loginRequest, httpServletRequest);

        assertNotNull(response);
        assertEquals("testuser", response.getUsername());
    }

    @Test
    void authenticateUser_ShouldThrowException_WhenPasswordIsWrong() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("wrongpass");

        // Giả lập AuthenticationManager ném lỗi sai mật khẩu
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Sai mật khẩu"));

        assertThrows(BadCredentialsException.class, () -> authService.authenticateUser(loginRequest, httpServletRequest));
    }

    // --- TEST GET CURRENT USER ---

    @Test
    void getCurrentUser_ShouldThrowException_WhenUserNotFound() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(AppException.class, () -> authService.getCurrentUser(id));
    }
}