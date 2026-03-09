// ================================================================
// FILE 1: FraudAlertRepository.java
// ================================================================
package com.fintrix.modules.fraud.repository;

import com.fintrix.modules.fraud.domain.FraudAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FraudAlertRepository
        extends JpaRepository<FraudAlert, String> {

    List<FraudAlert> findByUserIdOrderByCheckedAtDesc(String userId);
    List<FraudAlert> findByEntityNameContainingIgnoreCase(String name);
}

