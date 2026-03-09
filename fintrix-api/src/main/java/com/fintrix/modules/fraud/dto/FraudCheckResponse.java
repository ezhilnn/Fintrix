
// ================================================================
// FILE 3: FraudCheckResponse.java
// ================================================================
package com.fintrix.modules.fraud.dto;

import com.fintrix.modules.fraud.domain.AlertSeverity;
import com.fintrix.modules.fraud.domain.EntityType;
import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class FraudCheckResponse {

    private String        entityName;
    private EntityType    entityType;
    private Boolean       isSafe;              // overall verdict
    private AlertSeverity severity;
    private Boolean       isSebiRegistered;
    private Boolean       isRbiRegistered;
    private List<String>  redFlags;            // specific warnings
    private List<String>  safetyTips;          // what user should do
    private String        verdict;             // plain language summary
    private String        regulatorCheckUrl;   // link to verify themselves
}
