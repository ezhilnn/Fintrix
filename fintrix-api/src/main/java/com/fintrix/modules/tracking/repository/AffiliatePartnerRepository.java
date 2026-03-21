// ────────────────────────────────────────────────────────────────
package com.fintrix.modules.tracking.repository;

import com.fintrix.modules.tracking.domain.AffiliatePartner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AffiliatePartnerRepository
        extends JpaRepository<AffiliatePartner, String> {
    Optional<AffiliatePartner> findByEntityIdAndIsActiveTrue(String entityId);
    Optional<AffiliatePartner> findByUtmSourceAndIsActiveTrue(String utmSource);
}
