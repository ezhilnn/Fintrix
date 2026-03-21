// ────────────────────────────────────────────────────────────────
package com.fintrix.modules.consent.dto;

import lombok.Builder;
import lombok.Getter;

@Getter @Builder
public class ConsentStatusResponse {
    private String  userId;
    private String  consentVersion;
    private Boolean dataProcessing;   // mandatory
    private Boolean marketing;        // optional
    private Boolean creditCheck;      // expires 6 months
    private Boolean thirdPartyShare;  // per-click
}