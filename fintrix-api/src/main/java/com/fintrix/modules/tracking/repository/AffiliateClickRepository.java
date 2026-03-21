// ================================================================
// FILE 1: AffiliateClickRepository.java
// ================================================================
package com.fintrix.modules.tracking.repository;

import com.fintrix.modules.tracking.domain.AffiliateClick;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface AffiliateClickRepository
        extends JpaRepository<AffiliateClick, String> {

    long countByIsConvertedTrue();

    @Query("SELECT SUM(c.commissionEarned) FROM AffiliateClick c " +
           "WHERE c.commissionEarned IS NOT NULL")
    BigDecimal sumCommissionEarned();

    long countByUserId(String userId);
}



