// ================================================================
// FILE 1: FraudDetectionService.java
// ================================================================
package com.fintrix.modules.fraud.service;

import com.fintrix.modules.fraud.dto.FraudCheckRequest;
import com.fintrix.modules.fraud.dto.FraudCheckResponse;
import java.util.List;

public interface FraudDetectionService {
    FraudCheckResponse checkEntity(String userId, FraudCheckRequest request);
    List<FraudCheckResponse> getMyAlerts(String userId);
}

