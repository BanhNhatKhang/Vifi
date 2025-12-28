package com.example.movie.utils;

import com.example.movie.security.principal.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class AuthUtils {

    /**
     * Lấy đối tượng UserPrincipal của người dùng hiện tại từ SecurityContext
     */
    public Optional<UserPrincipal> getCurrentUserPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated() 
            || !(authentication.getPrincipal() instanceof UserPrincipal)) {
            return Optional.empty();
        }
        
        return Optional.of((UserPrincipal) authentication.getPrincipal());
    }

    /**
     * Lấy ID của người dùng hiện tại
     */
    public UUID getCurrentUserId() {
        return getCurrentUserPrincipal()
                .map(UserPrincipal::getId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin người dùng trong session"));
    }
}