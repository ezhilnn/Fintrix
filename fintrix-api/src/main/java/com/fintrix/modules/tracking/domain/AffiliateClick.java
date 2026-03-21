// ================================================================
// FILE 2: AffiliatePartner.java + AffiliateClick.java
// ================================================================
package com.fintrix.modules.tracking.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

// ────────────────────────────────────────────────────────────────
@Entity
@Table(name = "affiliate_clicks")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AffiliateClick {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "affiliate_partner_id", nullable = false)
    private String affiliatePartnerId;

    @Column(name = "click_ref", nullable = false, unique = true, length = 100)
    private String clickRef;          // UUID per click for attribution

    @Column(name = "product_type",    nullable = false, length = 50)
    private String productType;

    @Column(name = "product_id",      length = 36)
    private String productId;

    @Column(name = "utm_campaign",    length = 100)
    private String utmCampaign;

    @Column(name = "approval_probability", precision = 5, scale = 2)
    private BigDecimal approvalProbability;

    @Column(name = "ip_address",      length = 50)
    private String ipAddress;

    @Column(name = "clicked_at",      nullable = false)
    @Builder.Default
    private LocalDateTime clickedAt = LocalDateTime.now();

    @Column(name = "is_converted",    nullable = false)
    @Builder.Default
    private Boolean isConverted = false;

    @Column(name = "converted_at")
    private LocalDateTime convertedAt;

    @Column(name = "loan_amount",     precision = 12, scale = 2)
    private BigDecimal loanAmount;

    @Column(name = "commission_earned", precision = 10, scale = 2)
    private BigDecimal commissionEarned;

    @Column(name = "payout_status",   length = 30)
    @Builder.Default
    private String payoutStatus = "PENDING";
}








