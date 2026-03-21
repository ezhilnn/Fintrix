// ================================================================
// FILE 1: UserConsent.java
// ================================================================
package com.fintrix.modules.consent.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_consents")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserConsent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "consent_type", nullable = false, length = 50)
    private String consentType;
    // DATA_PROCESSING  — core app functionality (mandatory)
    // MARKETING        — promotional notifications (optional)
    // CREDIT_CHECK     — pull credit data (expires 6 months)
    // THIRD_PARTY_SHARE — share with lenders/card issuers on click

    @Column(name = "consent_version", nullable = false, length = 20)
    private String consentVersion; // bump when policy changes, e.g. "v1.1"

    @Column(name = "is_granted", nullable = false)
    private Boolean isGranted;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "granted_at")
    private LocalDateTime grantedAt;

    @Column(name = "withdrawn_at")
    private LocalDateTime withdrawnAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
}




