package com.example.movie.models;

import com.example.movie.enums.*;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "users",
    indexes = {
        @Index(name = "idx_users_username", columnList = "username"),
        @Index(name = "idx_users_email", columnList = "email")
    }
)
@Builder 
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 100)
    private String passwordHash;

    @Column(length = 100)
    private String displayName;

    @Column(length = 255)
    private String avatarUrl;

    @Column(name = "role", nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "provider", nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    private AuthProvider provider;

    @Builder.Default
    @Column(name = "is_enabled", nullable = false)
    private boolean enabled = true;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private LocalDateTime lastLoginAt;

    public void updateEmail(String newEmail) {
        this.email = newEmail;
    }

    public void updatePassword(String newPasswordHash) {
        this.passwordHash = newPasswordHash;
    }

    public void updateProfile(String displayName, String avatarUrl) {
        if (displayName != null && !displayName.isBlank()) {
            this.displayName = displayName;
        }
        this.avatarUrl = avatarUrl;
    }

    public void recordLogin() {
        this.lastLoginAt = LocalDateTime.now();
    }

    public void disable() {
        this.enabled = false;
    }

    public void enable() {
        this.enabled = true;
    }
}

