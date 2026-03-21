// ================================================================
// FILE 5: Tracking DTOs
// ================================================================
package com.fintrix.modules.tracking.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.Map;

@Getter @Setter
public class TrackEventRequest {
    private String            sessionId;
    private String            eventType;   // PAGE_VIEW, CARD_VIEW, etc.
    private String            page;
    private String            elementId;
    private String            entityId;
    private Map<String,Object> metadata;
    private Integer           durationMs;
}
