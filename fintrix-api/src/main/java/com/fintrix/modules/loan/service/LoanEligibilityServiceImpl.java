// ================================================================
// FILE 2: LoanEligibilityServiceImpl.java — Business Logic
// ================================================================
package com.fintrix.modules.loan.service;

import com.fintrix.common.exception.ResourceNotFoundException;
import com.fintrix.modules.financialprofile.domain.FinancialProfile;
import com.fintrix.modules.financialprofile.repository.FinancialProfileRepository;
import com.fintrix.modules.loan.domain.Lender;
import com.fintrix.modules.loan.dto.*;
import com.fintrix.modules.loan.repository.LenderRepository;
import com.fintrix.modules.loan.rules.LoanRuleEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * LoanEligibilityServiceImpl
 *
 * Orchestrates the full loan eligibility check:
 *
 * Step 1: Load user's financial profile from DB
 * Step 2: Pre-filter candidate lenders via DB query
 * Step 3: Run LoanRuleEngine on each candidate
 * Step 4: Split into eligible vs ineligible lists
 * Step 5: Sort eligible by approval probability (best first)
 * Step 6: Generate overall suggestion message
 * Step 7: Return complete response
 *
 * Cache strategy:
 *   Key = userId + loanType + amount + tenure
 *   If user checks same loan twice in 30 mins → serve from Redis
 *   If user updates financial profile → cache auto-expires (30 min TTL)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LoanEligibilityServiceImpl implements LoanEligibilityService {

    private final FinancialProfileRepository profileRepository;
    private final LenderRepository           lenderRepository;
    private final LoanRuleEngine             ruleEngine;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(
        value = "loan-eligibility",
        key   = "#userId + '_' + #request.loanType + '_' + #request.requestedAmount + '_' + #request.tenureMonths"
    )
    public LoanEligibilityResponse checkEligibility(
            String userId, LoanEligibilityRequest request) {

        log.info("Checking loan eligibility for userId: {} loanType: {}",
                userId, request.getLoanType());

        // ── Step 1: Load financial profile ────────────────────
        FinancialProfile profile = profileRepository
                .findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "FinancialProfile", "userId", userId));

        // ── Step 2: Pre-filter lenders from DB ────────────────
        int userCreditScore = profile.getCreditScore() != null
                ? profile.getCreditScore() : 0;

        int userAge = 25; // default — loaded from User in real flow
        // TODO Step 6 (BFF): load user age from User entity

        List<Lender> candidates = lenderRepository.findCandidateLenders(
                request.getLoanType(),
                userCreditScore,
                profile.getMonthlyIncome(),
                userAge,
                request.getRequestedAmount()
        );

        log.debug("Found {} candidate lenders after DB filter",
                candidates.size());

        // ── Step 3: Run rule engine on each candidate ─────────
        List<LenderResult> eligible   = new ArrayList<>();
        List<LenderResult> ineligible = new ArrayList<>();

        for (Lender lender : candidates) {
            LenderResult result =
                    ruleEngine.evaluate(profile, lender, request);

            if (Boolean.TRUE.equals(result.getIsEligible())) {
                eligible.add(result);
            } else {
                ineligible.add(result);
            }
        }

        // ── Step 4: Sort eligible by probability (best first) ─
        eligible.sort(
                Comparator.comparingInt(LenderResult::getApprovalProbability)
                          .reversed());

        // ── Step 5: Sort ineligible by probability (closest first) ─
        ineligible.sort(
                Comparator.comparingInt(LenderResult::getApprovalProbability)
                          .reversed());

        // ── Step 6: Build overall suggestion ──────────────────
        String suggestion = buildSuggestion(
                eligible.size(), ineligible.size(),
                profile, request);

        String creditWarning = buildCreditWarning(profile);

        return LoanEligibilityResponse.builder()
                .loanType(request.getLoanType())
                .requestedAmount(request.getRequestedAmount())
                .tenureMonths(request.getTenureMonths())
                .purpose(request.getPurpose())
                .userFoir(profile.getFoir())
                .userCreditScore(profile.getCreditScore())
                .userCreditScoreRange(profile.getCreditScoreRange())
                .userMonthlyIncome(profile.getMonthlyIncome())
                .eligibleLenders(eligible)
                .ineligibleLenders(ineligible)
                .overallSuggestion(suggestion)
                .creditScoreWarning(creditWarning)
                .build();
    }

    // ── Generate human-readable summary ──────────────────────
    private String buildSuggestion(
            int eligibleCount, int ineligibleCount,
            FinancialProfile profile,
            LoanEligibilityRequest request) {

        if (eligibleCount == 0) {
            return String.format(
                "You currently do not meet eligibility criteria for any " +
                "lender for a ₹%.0f %s. Key areas to improve: credit score " +
                "and FOIR. See improvement tips below.",
                request.getRequestedAmount(),
                request.getLoanType().name()
                        .replace("_", " ").toLowerCase());
        }

        return String.format(
            "Great news! You are eligible at %d lender(s) for your " +
            "₹%.0f %s. Apply with the top result for best approval chances. " +
            "Avoid applying to multiple lenders simultaneously as each " +
            "application creates a hard inquiry on your CIBIL report.",
            eligibleCount,
            request.getRequestedAmount(),
            request.getLoanType().name()
                    .replace("_", " ").toLowerCase());
    }

    // ── Credit score inquiry warning ──────────────────────────
    private String buildCreditWarning(FinancialProfile profile) {
        Integer score = profile.getCreditScore();
        if (score == null)
            return "Check your CIBIL score before applying. " +
                   "Rejections due to unknown score can hurt your profile.";
        if (score < 650)
            return "⚠️ Your credit score is low. Applying now risks " +
                   "rejection which will further reduce your score. " +
                   "Focus on improvement for 3–6 months first.";
        if (score < 700)
            return "Your score is borderline. Apply only to lenders " +
                   "where your probability is above 70%.";
        return null; // no warning needed for good scores
    }
}

