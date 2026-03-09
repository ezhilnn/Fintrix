package com.fintrix.modules.fraud.domain;

import com.fintrix.infrastructure.persistence.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "fraud_alerts")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FraudAlert extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "entity_name", nullable = false, length = 255)
    private String entityName;

    @Enumerated(EnumType.STRING)
    @Column(name = "entity_type", nullable = false)
    private EntityType entityType;

    @Enumerated(EnumType.STRING)
    @Column(name = "alert_severity", nullable = false)
    private AlertSeverity alertSeverity;

    @Column(name = "reason", nullable = false, columnDefinition = "TEXT")
    private String reason;

    @Column(name = "is_sebi_listed")
    private Boolean isSebiListed;

    @Column(name = "is_rbi_listed")
    private Boolean isRbiListed;

    @Column(name = "checked_at", nullable = false)
    @Builder.Default
    private LocalDateTime checkedAt = LocalDateTime.now();
}