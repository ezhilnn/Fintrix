// ================================================================
// FILE 6: DTOs
// ================================================================
package com.fintrix.modules.notification.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RegisterTokenRequest {
    @NotBlank
    private String fcmToken;
    private String deviceType = "ANDROID";
    private String deviceId;
    private String appVersion;
}