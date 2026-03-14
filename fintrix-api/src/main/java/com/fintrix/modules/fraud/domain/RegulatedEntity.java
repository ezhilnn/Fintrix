// ================================================================
// FILE 1: RegulatedEntity.java
// ================================================================
package com.fintrix.modules.fraud.domain;

import com.fintrix.infrastructure.persistence.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "regulated_entities")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RegulatedEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Column(name = "registration_number", length = 100)
    private String registrationNumber;

    @Column(name = "regulator", nullable = false, length = 20)
    private String regulator;      // RBI, SEBI, IRDAI, PFRDA, NHB

    @Column(name = "category", nullable = false, length = 100)
    private String category;       // NBFC, STOCK_BROKER, INSURANCE, etc.

    @Column(name = "license_status", nullable = false, length = 30)
    private String licenseStatus;  // ACTIVE, CANCELLED, SUSPENDED

    @Column(name = "website", length = 300)
    private String website;

    @Column(name = "is_verified", nullable = false)
    @Builder.Default
    private Boolean isVerified = true;
}


