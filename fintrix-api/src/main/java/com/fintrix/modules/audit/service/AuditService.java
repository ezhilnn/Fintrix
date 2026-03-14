// ================================================================
// FILE 3: AuditService.java
// ================================================================
package com.fintrix.modules.audit.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fintrix.modules.audit.domain.DecisionAuditLog;
import com.fintrix.modules.audit.repository.DecisionAuditRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * AuditService
 *
 * Logs every financial decision asynchronously.
 * @Async ensures audit logging never slows down the main request.
 *
 * Compliance requirement:
 *  SEBI/RBI guidelines require financial intermediaries to maintain
 *  audit trails of all recommendations for minimum 5 years.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuditService {

    private final DecisionAuditRepository auditRepository;
    private final ObjectMapper            objectMapper;

    @Async
    public void logDecision(String userId, String decisionType,
                             Object input, Object output,
                             String ipAddress, long durationMs) {
        try {
            DecisionAuditLog entry = DecisionAuditLog.builder()
                    .userId(userId)
                    .decisionType(decisionType)
                    .inputParameters(toJson(input))
                    .decisionOutput(toJson(output))
                    .ipAddress(ipAddress)
                    .durationMs((int) durationMs)
                    .build();
            auditRepository.save(entry);
        } catch (Exception e) {
            // Audit failure must NEVER affect main flow
            log.error("Failed to write audit log for userId: {} type: {} — {}",
                    userId, decisionType, e.getMessage());
        }
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return "{\"error\": \"serialization_failed\"}";
        }
    }
}