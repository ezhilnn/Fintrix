// ────────────────────────────────────────────────────────────────
package com.fintrix.modules.notification.dto;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter @Builder
public class NotificationResponse {
    private String        id;
    private String        title;
    private String        body;
    private String        notificationType;
    private String        payload;
    private Boolean       isRead;
    private LocalDateTime createdAt;
}