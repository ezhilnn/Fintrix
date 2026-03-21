// ────────────────────────────────────────────────────────────────
package com.fintrix.modules.tracking.dto;

import lombok.Builder;
import lombok.Getter;

@Getter @Builder
public class AffiliateClickResponse {
    private Boolean hasPartnership;
    private String  trackedUrl;
    private String  clickRef;
    private String  partnerName;
    private String  commissionType;
}
