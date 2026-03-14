// ================================================================
// FILE 2: FraudKeywordRepository.java
// ================================================================
package com.fintrix.modules.fraud.repository;

import com.fintrix.modules.fraud.domain.FraudKeyword;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FraudKeywordRepository
        extends JpaRepository<FraudKeyword, String> {

    // Load all active keywords on startup (cached in FraudRuleEngine)
    List<FraudKeyword> findByIsActiveTrue();

    // Find matching keywords for a given text (DB-side LIKE matching)
    @Query("SELECT k FROM FraudKeyword k " +
           "WHERE k.isActive = true " +
           "AND LOWER(:text) LIKE LOWER(CONCAT('%', k.keyword, '%'))")
    List<FraudKeyword> findMatchingKeywords(@Param("text") String text);
}