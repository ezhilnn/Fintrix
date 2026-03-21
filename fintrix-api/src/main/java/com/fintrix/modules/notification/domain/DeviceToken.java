// ================================================================
// FILE 1: DeviceToken.java
// ================================================================
package com.fintrix.modules.notification.domain;

import com.fintrix.infrastructure.persistence.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "device_tokens")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DeviceToken extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "fcm_token", nullable = false, columnDefinition = "TEXT")
    private String fcmToken;

    @Column(name = "device_type", length = 20)
    @Builder.Default
    private String deviceType = "ANDROID";  // ANDROID, IOS, WEB

    @Column(name = "device_id", length = 255)
    private String deviceId;

    @Column(name = "app_version", length = 20)
    private String appVersion;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;
}

















