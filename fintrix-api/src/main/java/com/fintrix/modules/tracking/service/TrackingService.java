// ================================================================
// FILE 4: TrackingService.java + AffiliateService.java
// ================================================================
package com.fintrix.modules.tracking.service;

import com.fintrix.modules.tracking.dto.TrackEventRequest;
import com.fintrix.modules.tracking.dto.AffiliateClickResponse;

public interface TrackingService {
    void trackEvent(String userId, TrackEventRequest request,
                    String ipAddress, String deviceType);
    AffiliateClickResponse getAffiliateLink(String userId,
                    String entityId, String productType,
                    Integer approvalProbability, String ipAddress);
}
