
// ================================================================
// FILE 2: FinancialHealthRecalcJob.java
// ================================================================
package com.fintrix.jobs;

import com.fintrix.modules.decisionengine.service.FinancialHealthService;
import com.fintrix.modules.financialprofile.repository.FinancialProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * FinancialHealthRecalcJob
 *
 * Runs every Sunday at 2:00 AM IST.
 * Recalculates financial health score for all active users.
 *
 * Why weekly recalculation?
 *  - User's financial situation changes (new EMIs, salary change)
 *  - Score history builds up weekly → trend chart populated
 *  - Off-peak time (2 AM Sunday) → minimal DB load impact
 *
 * Why not daily?
 *  - Financial profiles don't change daily
 *  - Unnecessary DB load for 100k users
 *  - Weekly cadence is sufficient for trend analysis
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FinancialHealthRecalcJob {

    private final FinancialProfileRepository profileRepository;
    private final FinancialHealthService     healthService;

    @Scheduled(cron = "0 0 2 * * SUN", zone = "Asia/Kolkata")
    public void recalculateAllScores() {
        log.info("[HealthRecalcJob] Starting weekly score recalculation");

        var profiles = profileRepository.findAll();
        int success = 0, failed = 0;

        for (var profile : profiles) {
            try {
                healthService.computeAndSave(profile.getUserId());
                success++;
            } catch (Exception e) {
                // Never let one user failure stop the whole job
                log.error("[HealthRecalcJob] Failed for userId: {} — {}",
                        profile.getUserId(), e.getMessage());
                failed++;
            }
        }

        log.info("[HealthRecalcJob] Complete — success: {} failed: {}",
                success, failed);
    }
}

