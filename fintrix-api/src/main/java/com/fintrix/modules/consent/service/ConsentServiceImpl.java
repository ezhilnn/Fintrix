// ────────────────────────────────────────────────────────────────
package com.fintrix.modules.consent.service;

import com.fintrix.modules.consent.domain.ConsentType;
import com.fintrix.modules.consent.domain.UserConsent;
import com.fintrix.modules.consent.dto.ConsentRequest;
import com.fintrix.modules.consent.dto.ConsentStatusResponse;
import com.fintrix.modules.consent.repository.ConsentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ConsentServiceImpl
 *
 * DPDP Act 2023 (India) compliance implementation.
 *
 * Key rules:
 *  1. Consent must be FREE — no forced bundling with core service
 *     (DATA_PROCESSING is mandatory; MARKETING is optional)
 *  2. Consent must be INFORMED — shown purpose before asking
 *  3. Consent must be SPECIFIC — separate consent per purpose
 *  4. Consent must be WITHDRAWABLE — user can withdraw anytime
 *  5. Audit trail — every consent action logged with IP + timestamp
 *  6. CREDIT_CHECK expires — re-consent required every 6 months
 *
 * Current version: "v1.0"
 * Bump to "v1.1" if privacy policy changes → users re-consent on next login
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ConsentServiceImpl implements ConsentService {

    private static final String CONSENT_VERSION = "v1.0";

    private final ConsentRepository consentRepository;

    @Override
    @Transactional
    public void grantConsent(String userId, ConsentRequest request,
                              String ipAddress, String userAgent) {

        LocalDateTime now    = LocalDateTime.now();
        LocalDateTime expiry = computeExpiry(request.getConsentType());

        UserConsent consent = UserConsent.builder()
                .userId(userId)
                .consentType(request.getConsentType())
                .consentVersion(CONSENT_VERSION)
                .isGranted(true)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .grantedAt(now)
                .expiresAt(expiry)
                .build();

        consentRepository.save(consent);
        log.info("Consent GRANTED: userId={} type={} ip={}",
                userId, request.getConsentType(), ipAddress);
    }

    @Override
    @Transactional
    public void withdrawConsent(String userId, String consentType) {

        if (ConsentType.DATA_PROCESSING.name().equals(consentType)) {
            throw new IllegalArgumentException(
                    "DATA_PROCESSING consent cannot be withdrawn. " +
                    "Please delete your account instead.");
        }

        consentRepository
                .findTopByUserIdAndConsentTypeOrderByCreatedAtDesc(
                        userId, consentType)
                .ifPresent(c -> {
                    c.setIsGranted(false);
                    c.setWithdrawnAt(LocalDateTime.now());
                    consentRepository.save(c);
                    log.info("Consent WITHDRAWN: userId={} type={}",
                            userId, consentType);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public ConsentStatusResponse getConsentStatus(String userId) {
        LocalDateTime now = LocalDateTime.now();
        Map<String, Boolean> statusMap = new HashMap<>();

        for (ConsentType type : ConsentType.values()) {
            boolean active = consentRepository
                    .findActiveConsent(userId, type.name(), now)
                    .isPresent();
            statusMap.put(type.name(), active);
        }

        return ConsentStatusResponse.builder()
                .userId(userId)
                .consentVersion(CONSENT_VERSION)
                .dataProcessing(statusMap.get("DATA_PROCESSING"))
                .marketing(statusMap.get("MARKETING"))
                .creditCheck(statusMap.get("CREDIT_CHECK"))
                .thirdPartyShare(statusMap.get("THIRD_PARTY_SHARE"))
                .build();
    }

    @Override
    public boolean hasActiveConsent(String userId, String consentType) {
        return consentRepository
                .findActiveConsent(userId, consentType, LocalDateTime.now())
                .isPresent();
    }

    // CREDIT_CHECK consent expires after 6 months per RBI data minimisation
    private LocalDateTime computeExpiry(String consentType) {
        if (ConsentType.CREDIT_CHECK.name().equals(consentType)) {
            return LocalDateTime.now().plusMonths(6);
        }
        return null; // other consents don't expire, only withdrawable
    }
}







