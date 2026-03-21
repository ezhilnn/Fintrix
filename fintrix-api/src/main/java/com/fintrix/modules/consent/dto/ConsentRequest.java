// ================================================================
// FILE 5: Consent DTOs
// ================================================================
package com.fintrix.modules.consent.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ConsentRequest {
    @NotBlank
    private String consentType;  // DATA_PROCESSING, MARKETING, etc.
}