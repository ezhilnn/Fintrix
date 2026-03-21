// ================================================================
// FILE 2: AffiliatePartner.java + AffiliateClick.java
// ================================================================
package com.fintrix.modules.tracking.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "affiliate_partners")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AffiliatePartner {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "partner_name",   nullable = false, length = 200)
    private String partnerName;

    @Column(name = "partner_type",   nullable = false, length = 50)
    private String partnerType;       // LENDER, CARD_ISSUER, INSURANCE

    @Column(name = "entity_id",      length = 36)
    private String entityId;          // lenders.id or credit_cards.id

    @Column(name = "utm_source",     nullable = false, unique = true, length = 100)
    private String utmSource;         // "hdfc-personal-loan"

    @Column(name = "base_url",       nullable = false, length = 500)
    private String baseUrl;           // https://hdfc.com/apply?ref=fintrix

    @Column(name = "commission_type",nullable = false, length = 30)
    private String commissionType;    // CPA, CPL, FLAT

    @Column(name = "commission_amount", precision = 10, scale = 2)
    private BigDecimal commissionAmount; // ₹1500 per CPA

    @Column(name = "commission_rate", precision = 5, scale = 4)
    private BigDecimal commissionRate;   // 0.005 = 0.5% for CPL

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
