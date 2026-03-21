// ================================================================
// FILE 2: Notification.java
// ================================================================
package com.fintrix.modules.notification.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "body", nullable = false, columnDefinition = "TEXT")
    private String body;

    @Column(name = "notification_type", nullable = false, length = 50)
    private String notificationType; // EMI_REMINDER, SCORE_UPDATE, FRAUD_ALERT, OFFER

    @Column(name = "payload", columnDefinition = "JSONB")
    private String payload;          // JSON: { route: "/loans", entityId: "xxx" }

    @Column(name = "is_read", nullable = false)
    @Builder.Default
    private Boolean isRead = false;

    @Column(name = "is_sent", nullable = false)
    @Builder.Default
    private Boolean isSent = false;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @Column(name = "fcm_message_id", length = 255)
    private String fcmMessageId;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}

