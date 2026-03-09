
// ================================================================
// FILE 3: CreditScoreRefreshJob.java
// ================================================================
package com.fintrix.jobs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * CreditScoreRefreshJob
 *
 * Placeholder for future CIBIL API integration.
 *
 * In MVP:
 *  Logs a reminder — credit score is self-reported by user.
 *
 * Production (requires CIBIL commercial API access):
 *  1. Fetch updated CIBIL scores for consenting users
 *  2. Update financial_profiles.credit_score
 *  3. Trigger score recalculation
 *  4. Notify user of score change
 *
 * Runs 1st of every month at 3:00 AM IST.
 * cron: 0 0 3 1 * *
 */
@Slf4j
@Component
public class CreditScoreRefreshJob {

    @Scheduled(cron = "0 0 3 1 * *", zone = "Asia/Kolkata")
    public void refreshCreditScores() {
        log.info("[CreditScoreRefreshJob] Monthly credit score refresh — " +
                "CIBIL API integration pending. " +
                "Users should manually update their scores.");

        /*
         * Production implementation:
         * 1. CIBIL commercial API partnership required
         * 2. User consent required (RBI data privacy norms)
         * 3. Fetch score → update profile → recalc health score
         */
    }
}