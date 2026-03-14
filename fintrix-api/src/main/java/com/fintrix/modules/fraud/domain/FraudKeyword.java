// ================================================================
// FILE 2: FraudKeyword.java
// ================================================================
package com.fintrix.modules.fraud.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "fraud_keywords")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FraudKeyword {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "keyword", nullable = false, unique = true, length = 200)
    private String keyword;

    @Column(name = "risk_level", nullable = false, length = 20)
    private String riskLevel;    // LOW, MEDIUM, HIGH, CRITICAL

    @Column(name = "fraud_type", nullable = false, length = 100)
    private String fraudType;    // PONZI, ADVANCE_FEE, FAKE_INVESTMENT, etc.

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}