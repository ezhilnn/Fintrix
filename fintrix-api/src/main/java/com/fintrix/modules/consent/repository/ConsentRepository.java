// ================================================================
// FILE 3: ConsentRepository.java
// ================================================================
package com.fintrix.modules.consent.repository;

import com.fintrix.modules.consent.domain.UserConsent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConsentRepository
        extends JpaRepository<UserConsent, String> {

    List<UserConsent> findByUserIdOrderByCreatedAtDesc(String userId);

    // Latest consent of a specific type for a user
    Optional<UserConsent> findTopByUserIdAndConsentTypeOrderByCreatedAtDesc(
            String userId, String consentType);

    // Check if active, non-expired consent exists
    @Query("""
        SELECT c FROM UserConsent c
        WHERE c.userId = :userId
          AND c.consentType = :type
          AND c.isGranted = true
          AND (c.expiresAt IS NULL OR c.expiresAt > :now)
          AND c.withdrawnAt IS NULL
        ORDER BY c.createdAt DESC
        LIMIT 1
        """)
    Optional<UserConsent> findActiveConsent(
            @Param("userId") String userId,
            @Param("type")   String type,
            @Param("now")    LocalDateTime now);
}




