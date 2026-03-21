package com.fintrix.modules.fraud.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * KeywordScanRequest
 *
 * User pastes ANY text — WhatsApp message, SMS, investment pitch,
 * social media post — and we scan it for fraud patterns.
 */
@Getter
@Setter
public class KeywordScanRequest {

    @NotBlank(message = "Text to scan is required")
    @Size(max = 5000, message = "Text cannot exceed 5000 characters")
    private String text;

    // Enum — user selects from predefined options, no free typing
    private ContentType contentType = ContentType.OTHER;

    public enum ContentType {
        WHATSAPP_MESSAGE,
        SMS,
        EMAIL,
        SOCIAL_MEDIA_POST,   // Facebook, Instagram, Twitter
        WEBSITE_URL,
        PHONE_CALL_SCRIPT,   // user types what caller said
        INVESTMENT_PITCH,    // brochure / document text
        LOAN_OFFER,
        JOB_OFFER,
        OTHER;

        // Human readable label for frontend dropdown
        public String getLabel() {
            return switch (this) {
                case WHATSAPP_MESSAGE  -> "WhatsApp Message";
                case SMS               -> "SMS / Text Message";
                case EMAIL             -> "Email";
                case SOCIAL_MEDIA_POST -> "Social Media Post";
                case WEBSITE_URL       -> "Website / URL";
                case PHONE_CALL_SCRIPT -> "Phone Call Script";
                case INVESTMENT_PITCH  -> "Investment Pitch / Brochure";
                case LOAN_OFFER        -> "Loan Offer";
                case JOB_OFFER         -> "Job Offer";
                case OTHER             -> "Other";
            };
        }
    }
}