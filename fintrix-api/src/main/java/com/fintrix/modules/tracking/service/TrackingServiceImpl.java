// ────────────────────────────────────────────────────────────────
package com.fintrix.modules.tracking.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fintrix.modules.tracking.domain.AffiliateClick;
import com.fintrix.modules.tracking.domain.AffiliatePartner;
import com.fintrix.modules.tracking.domain.UserEvent;
import com.fintrix.modules.tracking.dto.AffiliateClickResponse;
import com.fintrix.modules.tracking.dto.TrackEventRequest;
import com.fintrix.modules.tracking.repository.AffiliateClickRepository;
import com.fintrix.modules.tracking.repository.AffiliatePartnerRepository;
import com.fintrix.modules.tracking.repository.UserEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrackingServiceImpl implements TrackingService {

    private final UserEventRepository       eventRepository;
    private final AffiliatePartnerRepository partnerRepository;
    private final AffiliateClickRepository  clickRepository;
    private final ObjectMapper              objectMapper;

    /**
     * Fire-and-forget event tracking.
     * @Async so it NEVER blocks the main API response.
     * If tracking fails, user experience is unaffected.
     */
    @Override
    @Async
    @Transactional
    public void trackEvent(String userId, TrackEventRequest request,
                            String ipAddress, String deviceType) {
        try {
            String metadataJson = request.getMetadata() != null
                    ? objectMapper.writeValueAsString(request.getMetadata())
                    : null;

            eventRepository.save(UserEvent.builder()
                    .userId(userId)
                    .sessionId(request.getSessionId())
                    .eventType(request.getEventType())
                    .page(request.getPage())
                    .elementId(request.getElementId())
                    .entityId(request.getEntityId())
                    .metadata(metadataJson)
                    .durationMs(request.getDurationMs())
                    .ipAddress(ipAddress)
                    .deviceType(deviceType)
                    .build());
        } catch (Exception e) {
            // Tracking failure must NEVER surface to user
            log.warn("Event tracking failed silently: {}", e.getMessage());
        }
    }

    /**
     * Generate a tracked affiliate link for a lender or card.
     *
     * Flow:
     *  1. Find affiliate partner by entityId
     *  2. Generate unique clickRef (UUID)
     *  3. Save click record with approval probability at time of click
     *  4. Return tracked URL: partner.baseUrl + ?ref={clickRef}
     *
     * When partner confirms conversion (webhook):
     *  AffiliateClick.isConverted = true
     *  AffiliateClick.loanAmount  = approved amount
     *  Commission calculated based on commissionType
     */
    @Override
    @Transactional
    public AffiliateClickResponse getAffiliateLink(
            String userId, String entityId, String productType,
            Integer approvalProbability, String ipAddress) {

        AffiliatePartner partner = partnerRepository
                .findByEntityIdAndIsActiveTrue(entityId)
                .orElse(null);

        if (partner == null) {
            // No affiliate partnership — return direct link if possible
            return AffiliateClickResponse.builder()
                    .hasPartnership(false)
                    .trackedUrl(null)
                    .clickRef(null)
                    .build();
        }

        String clickRef = UUID.randomUUID().toString().replace("-", "");
        String trackedUrl = partner.getBaseUrl()
                + (partner.getBaseUrl().contains("?") ? "&" : "?")
                + "ref=" + clickRef
                + "&utm_source=fintrix"
                + "&utm_campaign=" + partner.getUtmSource();

        clickRepository.save(AffiliateClick.builder()
                .userId(userId)
                .affiliatePartnerId(partner.getId())
                .clickRef(clickRef)
                .productType(productType)
                .productId(entityId)
                .utmCampaign(partner.getUtmSource())
                .approvalProbability(approvalProbability != null
                        ? BigDecimal.valueOf(approvalProbability) : null)
                .ipAddress(ipAddress)
                .build());

        log.info("Affiliate click tracked: userId={} partner={} ref={}",
                userId, partner.getPartnerName(), clickRef);

        return AffiliateClickResponse.builder()
                .hasPartnership(true)
                .trackedUrl(trackedUrl)
                .clickRef(clickRef)
                .partnerName(partner.getPartnerName())
                .commissionType(partner.getCommissionType())
                .build();
    }
}





