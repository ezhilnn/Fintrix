package com.fintrix.modules.fraud.repository;

import com.fintrix.modules.fraud.domain.FraudKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FraudKeywordRepository
        extends JpaRepository<FraudKeyword, String> {

    // Load all active keywords — used by KeywordScanServiceImpl
    // for full-text scanning of user-submitted content
    List<FraudKeyword> findByIsActiveTrue();

    // Used by FraudRuleEngine — check if a short entity name
    // contains any fraud keyword (reverse of the above)
    @Query("SELECT k FROM FraudKeyword k " +
           "WHERE k.isActive = true " +
           "AND LOWER(:text) LIKE LOWER(CONCAT('%', k.keyword, '%'))")
    List<FraudKeyword> findMatchingKeywords(@Param("text") String text);
}