// ================================================================
// FILE 1: DecisionAuditLog.java
// ================================================================
package com.fintrix.modules.audit.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "decision_audit_logs")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DecisionAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "decision_type", nullable = false, length = 50)
    private String decisionType;   // LOAN_ELIGIBILITY, CARD_RECOMMENDATION, HEALTH_SCORE, FRAUD_CHECK

    @Column(name = "input_parameters", nullable = false, columnDefinition = "JSONB")
    private String inputParameters;

    @Column(name = "decision_output", nullable = false, columnDefinition = "JSONB")
    private String decisionOutput;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "duration_ms")
    private Integer durationMs;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}


