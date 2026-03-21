// ================================================================
// FILE 4: NotificationService.java
// ================================================================
package com.fintrix.modules.notification.service;

import com.fintrix.modules.notification.dto.NotificationResponse;
import com.fintrix.modules.notification.dto.RegisterTokenRequest;
import org.springframework.data.domain.Page;

public interface NotificationService {
    void registerToken(String userId, RegisterTokenRequest request);
    void sendToUser(String userId, String title, String body,
                    String type, String payloadJson);
    void sendToAllUsers(String title, String body, String type);
    Page<NotificationResponse> getMyNotifications(String userId, int page);
    long getUnreadCount(String userId);
    void markAllRead(String userId);
}