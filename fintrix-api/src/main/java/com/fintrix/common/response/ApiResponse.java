// ================================================================
// FILE 1: ApiResponse.java — standard response wrapper
// com/fintrix/common/response/ApiResponse.java
// ================================================================
package com.fintrix.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * ApiResponse — Standard API Response Wrapper
 *
 * Every API response from Fintrix follows this structure:
 *
 * Success:
 * {
 *   "success": true,
 *   "message": "Profile updated successfully",
 *   "data": { ...actual data... },
 *   "timestamp": "2024-04-01T10:30:00"
 * }
 *
 * Error:
 * {
 *   "success": false,
 *   "message": "User not found with id: abc123",
 *   "data": null,
 *   "timestamp": "2024-04-01T10:30:00"
 * }
 *
 * Why wrap responses?
 *  - Frontend always knows what structure to expect
 *  - Easy to add metadata (pagination, request id) later
 *  - Consistent error handling across all modules
 *
 * @JsonInclude(NON_NULL) → fields with null value
 *   are excluded from JSON response (cleaner output)
 */
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private final boolean       success;
    private final String        message;
    private final T             data;
    private final LocalDateTime timestamp;

    // ── Factory methods ───────────────────────────────────────

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
