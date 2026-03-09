package com.fintrix.modules.financialprofile.repository;

import com.fintrix.modules.financialprofile.domain.FinancialProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.fintrix.modules.financialprofile.domain.RiskLevel;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * FinancialProfileRepository
 *
 * One financial profile per user (1:1 relationship).
 * All monetary queries use BigDecimal for precision.
 *
 * Real-world learning — Spring Data JPA query methods:
 *
 * findByUserId(userId)
 *   → SELECT * FROM financial_profiles WHERE user_id = ?
 *     Generated automatically from method name.
 *
 * existsByUserId(userId)
 *   → SELECT COUNT(*) > 0 FROM financial_profiles WHERE user_id = ?
 *     Used to check if profile exists before create vs update.
 *
 * @Query with @Modifying
 *   → for UPDATE/DELETE queries that don't return an entity
 *   → @Modifying tells Spring this query changes data
 *   → must be inside @Transactional method
 */
@Repository
public interface FinancialProfileRepository
        extends JpaRepository<FinancialProfile, String> {

    // Primary lookup — every service method uses this
    Optional<FinancialProfile> findByUserId(String userId);

    // Existence check — used before create to avoid duplicates
    boolean existsByUserId(String userId);

    // Partial update — update only computed fields
    // Called by DecisionEngine after score calculation
    // More efficient than loading full entity just to update 3 fields
    @Modifying
    @Query("""
        UPDATE FinancialProfile fp
        SET fp.foir                 = :foir,
            fp.financialHealthScore = :score,
            fp.riskLevel            = :riskLevel
        WHERE fp.userId = :userId
        """)
    void updateComputedFields(
            @Param("userId")    String     userId,
            @Param("foir")      BigDecimal foir,
            @Param("score")     Integer    score,
            @Param("riskLevel") RiskLevel riskLevel
);

    /*
     * Why JPQL (Java Persistence Query Language) not SQL?
     *
     * SQL   : UPDATE financial_profiles SET foir = ?  WHERE user_id = ?
     * JPQL  : UPDATE FinancialProfile fp SET fp.foir = ? WHERE fp.userId = ?
     *
     * JPQL uses Java class names and field names, not table/column names.
     * This means if you rename a column in DB → only entity annotation changes.
     * JPQL query stays the same. Less brittle.
     */
}