// ================================================================
// FILE 1: EmiReminderJob.java
// ================================================================
package com.fintrix.jobs;

import com.fintrix.modules.financialprofile.repository.FinancialProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * EmiReminderJob
 *
 * Runs every day at 9:00 AM IST.
 * In MVP: logs users with active EMIs as placeholder.
 * Production: integrate with notification service
 * (Firebase FCM / email / SMS via Twilio).
 *
 * @Scheduled cron format:
 *  second  minute  hour  day-of-month  month  day-of-week
 *  0       0       9     *             *      *
 *  = every day at 09:00:00
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EmiReminderJob {

    private final FinancialProfileRepository profileRepository;

    @Scheduled(cron = "0 0 9 * * *", zone = "Asia/Kolkata")
    public void sendEmiReminders() {
        log.info("[EmiReminderJob] Starting daily EMI reminder job");

        long usersWithEmi = profileRepository.findAll()
                .stream()
                .filter(p -> p.getExistingEmiTotal() != null
                        && p.getExistingEmiTotal().doubleValue() > 0)
                .count();

        log.info("[EmiReminderJob] {} users have active EMIs — " +
                "reminders would be sent here", usersWithEmi);

        /*
         * Production implementation:
         * 1. Load users with EMIs due in next 3 days
         * 2. Call NotificationService.sendPushNotification()
         * 3. Log delivery status
         * 4. Retry failed notifications
         */
    }
}

