// ================================================================
// FILE 1: FinancialHealthScoreRepository.java
// ================================================================
package com.fintrix.modules.decisionengine.repository;

import com.fintrix.modules.decisionengine.domain.FinancialHealthScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FinancialHealthScoreRepository
        extends JpaRepository<FinancialHealthScore, String> {

    // Latest score for dashboard display
    Optional<FinancialHealthScore> findByUserIdAndIsLatestTrue(String userId);

    // Full history for trend chart (last 12 records)
    List<FinancialHealthScore> findTop12ByUserIdOrderByScoredOnDesc(
            String userId);

    // Before saving new score, mark all previous as not latest
    @Modifying
    @Query("UPDATE FinancialHealthScore s " +
           "SET s.isLatest = false " +
           "WHERE s.userId = :userId")
    void markAllAsNotLatest(@Param("userId") String userId);
}

