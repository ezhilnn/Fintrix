// ================================================================
// FILE 1: RegulatedEntityRepository.java
// ================================================================
package com.fintrix.modules.fraud.repository;

import com.fintrix.modules.fraud.domain.RegulatedEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegulatedEntityRepository
        extends JpaRepository<RegulatedEntity, String> {

    // Case-insensitive exact match
    Optional<RegulatedEntity> findByCompanyNameIgnoreCase(String name);

    // Case-insensitive partial match — for fuzzy lookup
    @Query("SELECT e FROM RegulatedEntity e " +
           "WHERE LOWER(e.companyName) LIKE LOWER(CONCAT('%', :name, '%')) " +
           "AND e.isVerified = true " +
           "ORDER BY e.companyName ASC")
    List<RegulatedEntity> searchByName(@Param("name") String name);

    List<RegulatedEntity> findByRegulatorAndLicenseStatus(
            String regulator, String licenseStatus);
}

