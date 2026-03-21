// ================================================================
// FILE 1: UserEvent.java
// ================================================================
package com.fintrix.modules.tracking.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_events")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "user_id")
    private String userId;          // null for anonymous sessions

    @Column(name = "session_id", length = 100)
    private String sessionId;

    @Column(name = "event_type", nullable = false, length = 50)
    private String eventType;
    // PAGE_VIEW, BUTTON_CLICK, LOAN_CHECK, CARD_VIEW,
    // CARD_CLICK, LENDER_CLICK, FRAUD_CHECK, SCORE_COMPUTE

    @Column(name = "page", length = 200)
    private String page;            // /dashboard, /loans, /cards

    @Column(name = "element_id", length = 100)
    private String elementId;       // "apply-btn", "hdfc-card"

    @Column(name = "entity_id", length = 100)
    private String entityId;        // lender.id or card.id

    @Column(name = "metadata", columnDefinition = "JSONB")
    private String metadata;        // flexible extra data as JSON

    @Column(name = "duration_ms")
    private Integer durationMs;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(name = "device_type", length = 20)
    private String deviceType;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}




