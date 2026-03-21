// ================================================================
// FILE 2: Updated DecisionAuditRepository — add countByDecisionType
// ================================================================
package com.fintrix.modules.audit.repository;

import com.fintrix.modules.audit.domain.DecisionAuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DecisionAuditRepository
        extends JpaRepository<DecisionAuditLog, String> {

    Page<DecisionAuditLog> findByUserIdOrderByCreatedAtDesc(
            String userId, Pageable pageable);

    Page<DecisionAuditLog> findByUserIdAndDecisionTypeOrderByCreatedAtDesc(
            String userId, String decisionType, Pageable pageable);

    long countByDecisionType(String decisionType);
}

