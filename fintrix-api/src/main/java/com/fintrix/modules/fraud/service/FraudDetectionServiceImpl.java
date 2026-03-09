
// ================================================================
// FILE 2: FraudDetectionServiceImpl.java
// ================================================================
package com.fintrix.modules.fraud.service;

import com.fintrix.modules.fraud.domain.FraudAlert;
import com.fintrix.modules.fraud.dto.FraudCheckRequest;
import com.fintrix.modules.fraud.dto.FraudCheckResponse;
import com.fintrix.modules.fraud.repository.FraudAlertRepository;
import com.fintrix.modules.fraud.rules.FraudRuleEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FraudDetectionServiceImpl implements FraudDetectionService {

    private final FraudRuleEngine      fraudRuleEngine;
    private final FraudAlertRepository alertRepository;

    @Override
    @Transactional
    public FraudCheckResponse checkEntity(String userId,
                                           FraudCheckRequest request) {

        log.info("Fraud check by userId: {} for entity: {}",
                userId, request.getEntityName());

        // Run rule engine
        FraudCheckResponse response = fraudRuleEngine.evaluate(request);

        // Persist alert if flagged (for user's history)
        if (!Boolean.TRUE.equals(response.getIsSafe())) {
            FraudAlert alert = FraudAlert.builder()
                    .userId(userId)
                    .entityName(request.getEntityName())
                    .entityType(request.getEntityType())
                    .alertSeverity(response.getSeverity())
                    .reason(String.join(" | ", response.getRedFlags()))
                    .isSebiListed(response.getIsSebiRegistered())
                    .isRbiListed(response.getIsRbiRegistered())
                    .build();
            alertRepository.save(alert);
        }

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<FraudCheckResponse> getMyAlerts(String userId) {
        return alertRepository
                .findByUserIdOrderByCheckedAtDesc(userId)
                .stream()
                .map(alert -> FraudCheckResponse.builder()
                        .entityName(alert.getEntityName())
                        .entityType(alert.getEntityType())
                        .isSafe(false)
                        .severity(alert.getAlertSeverity())
                        .isSebiRegistered(alert.getIsSebiListed())
                        .isRbiRegistered(alert.getIsRbiListed())
                        .redFlags(List.of(alert.getReason()))
                        .safetyTips(List.of())
                        .verdict("Previously flagged entity")
                        .build())
                .toList();
    }
}

