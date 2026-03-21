// ================================================================
// FILE 1: KeywordScanService.java
// ================================================================
package com.fintrix.modules.fraud.service;

import com.fintrix.modules.fraud.dto.KeywordScanRequest;
import com.fintrix.modules.fraud.dto.KeywordScanResponse;

public interface KeywordScanService {
    KeywordScanResponse scan(String userId, KeywordScanRequest request);
}


