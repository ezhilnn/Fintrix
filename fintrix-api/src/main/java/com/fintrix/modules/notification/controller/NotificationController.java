// ================================================================
// FILE 7: NotificationController.java
// ================================================================
package com.fintrix.modules.notification.controller;

import com.fintrix.common.response.ApiResponse;
import com.fintrix.modules.notification.dto.NotificationResponse;
import com.fintrix.modules.notification.dto.RegisterTokenRequest;
import com.fintrix.modules.notification.service.NotificationService;
import com.fintrix.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * NotificationController
 *
 * POST /api/v1/notifications/token       → register FCM device token
 * GET  /api/v1/notifications             → get notification history (paginated)
 * GET  /api/v1/notifications/unread-count → unread badge count
 * PUT  /api/v1/notifications/read-all    → mark all as read
 */
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/token")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Void>> registerToken(
            @AuthenticationPrincipal UserPrincipal user,
            @Valid @RequestBody RegisterTokenRequest request) {
        notificationService.registerToken(user.getId(), request);
        return ResponseEntity.ok(
                ApiResponse.success("Device token registered", null));
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Page<NotificationResponse>>> getNotifications(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(ApiResponse.success(
                notificationService.getMyNotifications(user.getId(), page)));
    }

    @GetMapping("/unread-count")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(
            @AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(ApiResponse.success(
                notificationService.getUnreadCount(user.getId())));
    }

    @PutMapping("/read-all")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Void>> markAllRead(
            @AuthenticationPrincipal UserPrincipal user) {
        notificationService.markAllRead(user.getId());
        return ResponseEntity.ok(ApiResponse.success("All marked as read", null));
    }
}