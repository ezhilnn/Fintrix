package com.fintrix.modules.financialprofile.service;

import com.fintrix.common.exception.ResourceNotFoundException;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class FinancialProfileServiceImpl implements FinancialProfileService {

    private final FinancialProfileRepository profileRepository;
    private final UserRepository             userRepository;

    @Override
    @Transactional
    public FinancialProfileResponse createProfile(String userId,
            FinancialProfileRequest request) {
        log.info("Creating financial profile for userId: {}", userId);
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User", "id", userId));
        if (profileRepository.existsByUserId(userId)) {
            return updateProfile(userId, request);
        }
        FinancialProfile profile = buildProfile(userId, request);
        return mapToResponse(profileRepository.save(profile));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "financial-health", key = "#userId")
    public FinancialProfileResponse getProfile(String userId) {
        return mapToResponse(findByUserId(userId));
    }

    @Override
    @Transactional
    @CacheEvict(value = "financial-health", key = "#userId")
    public FinancialProfileResponse updateProfile(String userId,
            FinancialProfileRequest request) {
        log.info("Updating financial profile for userId: {}", userId);
        FinancialProfile profile = findByUserId(userId);
        applyRequestToProfile(profile, request);
        recomputeDerivedFields(profile);
        return mapToResponse(profileRepository.save(profile));
    }

    private FinancialProfile buildProfile(String userId,
            FinancialProfileRequest request) {
        FinancialProfile p = new FinancialProfile();
        p.setUserId(userId);
        applyRequestToProfile(p, request);
        recomputeDerivedFields(p);
        return p;
    }

    private void applyRequestToProfile(FinancialProfile p,
            FinancialProfileRequest req) {
        p.setEmploymentType(req.getEmploymentType());
        p.setEmployerName(req.getEmployerName());
        p.setYearsOfExperience(req.getYearsOfExperience());
        p.setMonthlyIncome(req.getMonthlyIncome());
        p.setMonthlyExpenses(nullSafe(req.getMonthlyExpenses()));
        p.setExistingEmiTotal(nullSafe(req.getExistingEmiTotal()));
        p.setNumberOfActiveLoans(req.getNumberOfActiveLoans() != null
                ? req.getNumberOfActiveLoans() : 0);
        p.setCreditScore(req.getCreditScore());
        p.setNumberOfCreditCards(req.getNumberOfCreditCards() != null
                ? req.getNumberOfCreditCards() : 0);
        p.setTotalCreditLimit(nullSafe(req.getTotalCreditLimit()));
        p.setCurrentCreditUtilization(
                nullSafe(req.getCurrentCreditUtilization()));
        p.setPreferredRewardType(req.getPreferredRewardType());
        p.setTopSpendingCategory(req.getTopSpendingCategory());
    }

    private void recomputeDerivedFields(FinancialProfile p) {
        BigDecimal income   = p.getMonthlyIncome();
        BigDecimal expenses = nullSafe(p.getMonthlyExpenses());
        BigDecimal emi      = nullSafe(p.getExistingEmiTotal());

        // Monthly savings = income - all outflows
        p.setMonthlySavings(income.subtract(expenses).subtract(emi));

        // ── FOIR: Fixed Obligation to Income Ratio ────────────
        // Formula: existingEMI / income × 100
        // What banks use for loan eligibility — only EMIs counted
        BigDecimal foir = BigDecimal.ZERO;
        if (income.compareTo(BigDecimal.ZERO) > 0) {
            foir = emi.divide(income, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);
        }
        p.setFoir(foir);

        // ── DTI: Debt-to-Income Ratio ─────────────────────────
        // Formula: (existingEMI + monthlyExpenses) / income × 100
        //
        // DTI is broader than FOIR — it includes ALL obligations:
        // rent, groceries, utilities, EMIs, credit card minimums.
        //
        // Why both matter:
        //   FOIR = 10% (only ₹5K EMI on ₹50K income) — banks say ELIGIBLE
        //   DTI  = 90% (₹45K rent + ₹5K EMI on ₹50K income) — actually broke
        //
        // FOIR tells you bank eligibility.
        // DTI tells you real financial health.
        //
        // Financial planners use DTI thresholds:
        //   < 30%  → LOW      — financially comfortable
        //   30-43% → MODERATE — manageable but watch it
        //   43-50% → HIGH     — lenders flag this
        //   > 50%  → CRITICAL — at risk of default
        BigDecimal dti = BigDecimal.ZERO;
        if (income.compareTo(BigDecimal.ZERO) > 0) {
            dti = expenses.add(emi)
                    .divide(income, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);
        }
        p.setDti(dti);
        p.setDtiRange(computeDtiRange(dti.doubleValue()));

        p.setCreditScoreRange(computeCreditScoreRange(p.getCreditScore()));
        p.setRiskLevel(computeRiskLevel(foir, dti,
                p.getCreditScore(), p.getCurrentCreditUtilization()));
    }

    private String computeDtiRange(double dti) {
        if (dti < 30) return "LOW";
        if (dti < 43) return "MODERATE";
        if (dti < 50) return "HIGH";
        return "CRITICAL";
    }

    private String computeCreditScoreRange(Integer score) {
        if (score == null) return "UNKNOWN";
        if (score >= 750)  return "EXCELLENT";
        if (score >= 700)  return "GOOD";
        if (score >= 650)  return "FAIR";
        return "POOR";
    }

    private RiskLevel computeRiskLevel(BigDecimal foir, BigDecimal dti,
            Integer creditScore, BigDecimal utilization) {
        int pts = 0;

        // FOIR contribution
        if (foir.compareTo(BigDecimal.valueOf(60)) > 0)       pts += 4;
        else if (foir.compareTo(BigDecimal.valueOf(50)) > 0)  pts += 3;
        else if (foir.compareTo(BigDecimal.valueOf(40)) > 0)  pts += 1;

        // DTI contribution — now also factors in
        if (dti.compareTo(BigDecimal.valueOf(50)) > 0)        pts += 2;
        else if (dti.compareTo(BigDecimal.valueOf(43)) > 0)   pts += 1;

        // Credit score contribution
        if (creditScore != null) {
            if (creditScore < 600)      pts += 3;
            else if (creditScore < 650) pts += 2;
            else if (creditScore < 700) pts += 1;
        }

        // Utilization contribution
        if (utilization != null) {
            if (utilization.compareTo(BigDecimal.valueOf(75)) > 0)      pts += 3;
            else if (utilization.compareTo(BigDecimal.valueOf(50)) > 0) pts += 2;
            else if (utilization.compareTo(BigDecimal.valueOf(30)) > 0) pts += 1;
        }

        if (pts >= 7) return RiskLevel.CRITICAL;
        if (pts >= 4) return RiskLevel.HIGH;
        if (pts >= 2) return RiskLevel.MEDIUM;
        return RiskLevel.LOW;
    }

    private FinancialProfile findByUserId(String userId) {
        return profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "FinancialProfile", "userId", userId));
    }

    private BigDecimal nullSafe(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

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
                .numberOfActiveLoans(p.getNumberOfActiveLoans())
                .creditScore(p.getCreditScore())
                .creditScoreRange(p.getCreditScoreRange())
                .numberOfCreditCards(p.getNumberOfCreditCards())
                .totalCreditLimit(p.getTotalCreditLimit())
                .currentCreditUtilization(p.getCurrentCreditUtilization())
                .foir(p.getFoir())
                .dti(p.getDti())
                .dtiRange(p.getDtiRange())
                .financialHealthScore(p.getFinancialHealthScore())
                .riskLevel(p.getRiskLevel())
                .preferredRewardType(p.getPreferredRewardType())
                .topSpendingCategory(p.getTopSpendingCategory())
                .isComplete(p.getEmploymentType() != null
                        && p.getMonthlyIncome() != null
                        && p.getCreditScore() != null)
                .build();
    }
}