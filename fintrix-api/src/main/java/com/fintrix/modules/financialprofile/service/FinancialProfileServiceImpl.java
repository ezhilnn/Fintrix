// ================================================================
// FILE 2: FinancialProfileServiceImpl.java — Business Logic
// ================================================================
package com.fintrix.modules.financialprofile.service;

import com.fintrix.common.exception.ResourceNotFoundException;
import com.fintrix.modules.financialprofile.domain.EmploymentType;
import com.fintrix.modules.financialprofile.domain.FinancialProfile;
import com.fintrix.modules.financialprofile.domain.RiskLevel;
import com.fintrix.modules.financialprofile.dto.FinancialProfileRequest;
import com.fintrix.modules.financialprofile.dto.FinancialProfileResponse;
import com.fintrix.modules.financialprofile.repository.FinancialProfileRepository;
import com.fintrix.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * FinancialProfileServiceImpl
 *
 * Core business logic for financial profile management.
 *
 * Key computations done here:
 *
 * 1. FOIR (Fixed Obligation to Income Ratio)
 *    Formula: (existingEMI / monthlyIncome) * 100
 *    Example: EMI=₹15,000, Income=₹50,000 → FOIR = 30%
 *    Banks reject if FOIR > 50-60% (varies by lender)
 *
 * 2. Monthly Savings
 *    Formula: income - expenses - existingEMI
 *    Shows how much user actually saves per month
 *
 * 3. Credit Score Range
 *    CIBIL score bands used by Indian banks:
 *    750-900 → EXCELLENT  (best rates, easy approval)
 *    700-749 → GOOD       (good rates, likely approval)
 *    650-699 → FAIR       (higher rates, harder approval)
 *    300-649 → POOR       (may be rejected, needs improvement)
 *
 * 4. Risk Level (preliminary, full score done by DecisionEngine)
 *    Based on FOIR + credit score + utilization
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FinancialProfileServiceImpl implements FinancialProfileService {

    private final FinancialProfileRepository profileRepository;
    private final UserRepository             userRepository;

    // ── CREATE — first time setup ─────────────────────────────
    @Override
    @Transactional
    public FinancialProfileResponse createProfile(
            String userId, FinancialProfileRequest request) {

        log.info("Creating financial profile for userId: {}", userId);

        // Verify user exists
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User", "id", userId));

        // Prevent duplicate profiles
        if (profileRepository.existsByUserId(userId)) {
            log.warn("Profile already exists for userId: {} — " +
                    "redirecting to update", userId);
            return updateProfile(userId, request);
        }

        FinancialProfile profile = buildProfile(userId, request);
        FinancialProfile saved   = profileRepository.save(profile);

        log.info("Financial profile created for userId: {}", userId);
        return mapToResponse(saved);
    }

    // ── GET ───────────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "financial-health", key = "#userId")
    public FinancialProfileResponse getProfile(String userId) {
        log.debug("Fetching financial profile for userId: {}", userId);

        FinancialProfile profile = findByUserId(userId);
        return mapToResponse(profile);
    }

    // ── UPDATE ────────────────────────────────────────────────
    @Override
    @Transactional
    @CacheEvict(value = "financial-health", key = "#userId")
    public FinancialProfileResponse updateProfile(
            String userId, FinancialProfileRequest request) {

        log.info("Updating financial profile for userId: {}", userId);

        FinancialProfile profile = findByUserId(userId);

        // Update all fields from request
        applyRequestToProfile(profile, request);

        // Recompute derived fields
        recomputeDerivedFields(profile);

        FinancialProfile saved = profileRepository.save(profile);
        return mapToResponse(saved);
    }

    // ================================================================
    // PRIVATE HELPERS
    // ================================================================

    // ── Build new profile entity from request ─────────────────
    private FinancialProfile buildProfile(
            String userId, FinancialProfileRequest request) {

        FinancialProfile profile = new FinancialProfile();
        profile.setUserId(userId);
        applyRequestToProfile(profile, request);
        recomputeDerivedFields(profile);
        return profile;
    }

    // ── Copy request fields onto entity ──────────────────────
    private void applyRequestToProfile(
            FinancialProfile profile, FinancialProfileRequest req) {

        profile.setEmploymentType(req.getEmploymentType());
        profile.setEmployerName(req.getEmployerName());
        profile.setYearsOfExperience(req.getYearsOfExperience());
        profile.setMonthlyIncome(req.getMonthlyIncome());
        profile.setMonthlyExpenses(
                nullSafe(req.getMonthlyExpenses()));
        profile.setExistingEmiTotal(
                nullSafe(req.getExistingEmiTotal()));
        profile.setNumberOfActiveLoans(
                req.getNumberOfActiveLoans() != null
                        ? req.getNumberOfActiveLoans() : 0);
        profile.setCreditScore(req.getCreditScore());
        profile.setNumberOfCreditCards(
                req.getNumberOfCreditCards() != null
                        ? req.getNumberOfCreditCards() : 0);
        profile.setTotalCreditLimit(
                nullSafe(req.getTotalCreditLimit()));
        profile.setCurrentCreditUtilization(
                nullSafe(req.getCurrentCreditUtilization()));
        profile.setPreferredRewardType(req.getPreferredRewardType());
        profile.setTopSpendingCategory(req.getTopSpendingCategory());
    }

    // ── Compute FOIR, savings, credit range, risk ─────────────
    private void recomputeDerivedFields(FinancialProfile profile) {

        BigDecimal income   = profile.getMonthlyIncome();
        BigDecimal expenses = nullSafe(profile.getMonthlyExpenses());
        BigDecimal emi      = nullSafe(profile.getExistingEmiTotal());

        // 1. Monthly savings
        BigDecimal savings = income.subtract(expenses).subtract(emi);
        profile.setMonthlySavings(savings);

        // 2. FOIR — Fixed Obligation to Income Ratio
        //    Formula: (EMI / Income) × 100
        //    Scale 2 = two decimal places, HALF_UP rounding
        BigDecimal foir = BigDecimal.ZERO;
        if (income.compareTo(BigDecimal.ZERO) > 0) {
            foir = emi
                    .divide(income, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);
        }
        profile.setFoir(foir);

        /*
         * Real-world learning — RoundingMode.HALF_UP:
         * 0.045 → 0.05  (rounds up at midpoint)
         * 0.044 → 0.04  (rounds down below midpoint)
         * Used in all Indian banking calculations.
         * NEVER use HALF_EVEN (banker's rounding) for user-facing numbers.
         */

        // 3. Credit score range label
        profile.setCreditScoreRange(
                computeCreditScoreRange(profile.getCreditScore()));

        // 4. Preliminary risk level
        profile.setRiskLevel(
                computeRiskLevel(foir, profile.getCreditScore(),
                        profile.getCurrentCreditUtilization()));
    }

    // ── CIBIL Score Range — Indian banking standard ───────────
    private String computeCreditScoreRange(Integer creditScore) {
        if (creditScore == null) return "UNKNOWN";

        if (creditScore >= 750) return "EXCELLENT";   // best rates
        if (creditScore >= 700) return "GOOD";         // likely approved
        if (creditScore >= 650) return "FAIR";         // higher rates
        return "POOR";                                 // improve first

        /*
         * Why these bands?
         * CIBIL (TransUnion) uses 300-900 range.
         * Indian banks use these thresholds internally:
         *   750+  → Priority customer, best interest rates
         *   700+  → Standard approval, market rates
         *   650+  → Conditional approval, higher rates
         *   <650  → Likely rejection or secured products only
         */
    }

    // ── Preliminary risk level from 3 key indicators ─────────
    private RiskLevel computeRiskLevel(
            BigDecimal foir,
            Integer    creditScore,
            BigDecimal utilization) {

        int riskPoints = 0;

        // FOIR risk (most important factor — 40% weight)
        if (foir.compareTo(BigDecimal.valueOf(60)) > 0)
            riskPoints += 4;   // FOIR > 60% → critical
        else if (foir.compareTo(BigDecimal.valueOf(50)) > 0)
            riskPoints += 3;   // FOIR 50-60% → high
        else if (foir.compareTo(BigDecimal.valueOf(40)) > 0)
            riskPoints += 1;   // FOIR 40-50% → slight risk

        // Credit score risk (30% weight)
        if (creditScore != null) {
            if (creditScore < 600)      riskPoints += 3;
            else if (creditScore < 650) riskPoints += 2;
            else if (creditScore < 700) riskPoints += 1;
        }

        // Utilization risk (30% weight)
        // Rule: keep utilization below 30%
        if (utilization != null) {
            if (utilization.compareTo(BigDecimal.valueOf(75)) > 0)
                riskPoints += 3;  // >75% utilization → very risky
            else if (utilization.compareTo(BigDecimal.valueOf(50)) > 0)
                riskPoints += 2;  // 50-75% → high risk
            else if (utilization.compareTo(BigDecimal.valueOf(30)) > 0)
                riskPoints += 1;  // 30-50% → moderate risk
        }

        // Map risk points → RiskLevel
        if (riskPoints >= 7) return RiskLevel.CRITICAL;
        if (riskPoints >= 4) return RiskLevel.HIGH;
        if (riskPoints >= 2) return RiskLevel.MEDIUM;
        return RiskLevel.LOW;
    }

    // ── Find profile or throw 404 ─────────────────────────────
    private FinancialProfile findByUserId(String userId) {
        return profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "FinancialProfile", "userId", userId));
    }

    // ── Null-safe BigDecimal — treats null as ZERO ────────────
    private BigDecimal nullSafe(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    // ── Map entity → response DTO ─────────────────────────────
    private FinancialProfileResponse mapToResponse(FinancialProfile p) {
        return FinancialProfileResponse.builder()
                .id(p.getId())
                .userId(p.getUserId())
                .employmentType(p.getEmploymentType())
                .employerName(p.getEmployerName())
                .yearsOfExperience(p.getYearsOfExperience())
                .monthlyIncome(p.getMonthlyIncome())
                .monthlyExpenses(p.getMonthlyExpenses())
                .monthlySavings(p.getMonthlySavings())
                .existingEmiTotal(p.getExistingEmiTotal())
                .creditScore(p.getCreditScore())
                .creditScoreRange(p.getCreditScoreRange())
                .numberOfCreditCards(p.getNumberOfCreditCards())
                .totalCreditLimit(p.getTotalCreditLimit())
                .currentCreditUtilization(p.getCurrentCreditUtilization())
                .foir(p.getFoir())
                .financialHealthScore(p.getFinancialHealthScore())
                .riskLevel(p.getRiskLevel())
                .preferredRewardType(p.getPreferredRewardType())
                .topSpendingCategory(p.getTopSpendingCategory())
                .isComplete(isProfileComplete(p))
                .build();
    }

    // ── Check if all key fields are filled ────────────────────
    private boolean isProfileComplete(FinancialProfile p) {
        return p.getEmploymentType()  != null
            && p.getMonthlyIncome()   != null
            && p.getCreditScore()     != null
            && p.getExistingEmiTotal()!= null;
    }
}