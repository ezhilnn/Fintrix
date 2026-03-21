// ================================================================
// FILE 4: ConsentService.java + Impl
// ================================================================
package com.fintrix.modules.consent.service;

import com.fintrix.modules.consent.dto.ConsentRequest;
import com.fintrix.modules.consent.dto.ConsentStatusResponse;

import java.util.List;

public interface ConsentService {
    void grantConsent(String userId, ConsentRequest request,
                      String ipAddress, String userAgent);
    void withdrawConsent(String userId, String consentType);
    ConsentStatusResponse getConsentStatus(String userId);
    boolean hasActiveConsent(String userId, String consentType);
}