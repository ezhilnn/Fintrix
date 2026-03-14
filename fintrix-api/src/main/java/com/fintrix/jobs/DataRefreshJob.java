package com.fintrix.jobs;
 
import com.fintrix.modules.creditcard.repository.CreditCardRepository;
import com.fintrix.modules.loan.repository.LenderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
 
import java.time.LocalDateTime;
 
/**
 * DataRefreshJob
 *
 * Schedule:
 *  Lenders + Credit cards  → 1st of every month at 1:00 AM
 *  Fraud registry          → Every Sunday at 3:00 AM
 *
 * MVP implementation:
 *  Logs data freshness stats.
 *  In production: pull from curated data API or admin CMS.
 *
 * Production architecture for data updates:
 *  Option A: Admin CMS  → internal team updates data → triggers cache clear
 *  Option B: Partner API → RBI/SEBI API integration → nightly pull + validate
 *  Option C: Hybrid     → CMS for products + API for regulated entities
 *
 * Why not scrape bank websites?
 *  Web scraping financial data violates terms of service.
 *  Data must come from official sources or licensed data providers.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataRefreshJob {
 
    private final LenderRepository     lenderRepository;
    private final CreditCardRepository cardRepository;
 
    @Scheduled(cron = "0 0 1 1 * *", zone = "Asia/Kolkata")
    public void refreshFinancialProducts() {
        log.info("[DataRefreshJob] Monthly financial product data refresh — {}",
                LocalDateTime.now());
 
        long lenderCount = lenderRepository.count();
        long cardCount   = cardRepository.count();
 
        log.info("[DataRefreshJob] Current data: {} lenders | {} credit cards",
                lenderCount, cardCount);
 
        /*
         * Production implementation steps:
         * 1. Pull updated lender rates from data provider API
         * 2. Validate data schema and range checks
         * 3. Update interest_rate_min/max, processing_fee
         * 4. Flag lenders with stale data (last_updated > 45 days)
         * 5. Notify admin if critical lenders have stale data
         * 6. Clear Redis cache: loan-eligibility, card-recommendation
         * 7. Log refresh summary report
         */
 
        log.info("[DataRefreshJob] Data refresh complete. " +
                "Note: Manual verification recommended for rate changes.");
    }
}