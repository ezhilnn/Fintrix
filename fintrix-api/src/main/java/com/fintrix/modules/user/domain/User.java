package com.fintrix.modules.user.domain;

import com.fintrix.infrastructure.persistence.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * Core User entity.
 * Created on first Google OAuth login.
 * Stores only non-sensitive identity data — no Aadhaar, PAN stored here.
 */
@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_users_email", columnList = "email", unique = true),
    @Index(name = "idx_users_google_id", columnList = "google_id", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    // ── Identity ────────────────────────────────────────────
    @Column(name = "google_id", unique = true, nullable = false)
    private String googleId;

    @Column(name = "email", unique = true, nullable = false, length = 255)
    private String email;

    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;

    @Column(name = "profile_picture_url", length = 500)
    private String profilePictureUrl;

    // ── Profile ─────────────────────────────────────────────
    @Column(name = "phone_number", length = 15)
    private String phoneNumber;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "state", length = 100)
    private String state;

    @Column(name = "age")
    private Integer age;

    // ── Role & Status ────────────────────────────────────────
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    @Builder.Default
    private UserRole role = UserRole.USER;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "is_profile_complete", nullable = false)
    @Builder.Default
    private Boolean isProfileComplete = false;
}