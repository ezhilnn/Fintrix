package com.fintrix.jobs;
 
import com.fintrix.modules.emi.domain.EmiTracker;
import com.fintrix.modules.emi.repository.EmiTrackerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
 
import java.time.LocalDate;
import java.util.List;
 
/**
 * EmiReminderJob (Production version)
 *
 * Runs daily at 9:00 AM IST.
 * Finds all EMIs due in the next 3 days and logs them.
 *
 * Production: replace log.info with notification service calls.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EmiReminderJobV2 {
 
    private final EmiTrackerRepository emiRepository;
 
    @Scheduled(cron = "0 0 9 * * *", zone = "Asia/Kolkata")
    public void sendEmiReminders() {
        log.info("[EmiReminderJob] Running daily EMI reminder check");
 
        int today   = LocalDate.now().getDayOfMonth();
        int endDay  = Math.min(today + 3, 28); // safe upper bound
 
        List<EmiTracker> dueSoon =
                emiRepository.findDueSoon(today, endDay);
 
        if (dueSoon.isEmpty()) {
            log.info("[EmiReminderJob] No EMIs due in next 3 days");
            return;
        }
 
        for (EmiTracker emi : dueSoon) {
            log.info("[EmiReminderJob] Reminder: userId={} loan={} emi=₹{} due={}th",
                    emi.getUserId(), emi.getLoanName(),
                    emi.getEmiAmount(), emi.getDueDateOfMonth());
 
            /*
             * Production: send push notification via Firebase FCM
             * notificationService.sendPush(
             *     emi.getUserId(),
             *     "EMI Due Soon",
             *     "Your " + emi.getLoanName() + " EMI of ₹" +
             *     emi.getEmiAmount() + " is due on the " +
             *     emi.getDueDateOfMonth() + "th."
             * );
             */
        }
 
        log.info("[EmiReminderJob] Sent {} EMI reminders", dueSoon.size());
    }
}