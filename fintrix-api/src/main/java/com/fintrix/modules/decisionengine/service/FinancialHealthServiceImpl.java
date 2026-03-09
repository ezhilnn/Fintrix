

// ────────────────────────────────────────────────────────────────
package com.fintrix.modules.decisionengine.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fintrix.common.exception.ResourceNotFoundException;
import com.fintrix.modules.decisionengine.domain.FinancialHealthScore;
import com.fintrix.modules.decisionengine.dto.FinancialHealthResponse;
import com.fintrix.modules.decisionengine.repository.FinancialHealthScoreRepository;
import com.fintrix.modules.decisionengine.scoring.ScoreCalculator;
import com.fintrix.modules.financialprofile.domain.FinancialProfile;
import com.fintrix.modules.financialprofile.repository.FinancialProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FinancialHealthServiceImpl implements FinancialHealthService {

    private final FinancialProfileRepository      profileRepository;
    private final FinancialHealthScoreRepository  scoreRepository;
    private final ScoreCalculator                 scoreCalculator;
    private final ObjectMapper                    objectMapper;

    @Override
    @Transactional
    @CacheEvict(value = "financial-health", key = "#userId")
    public FinancialHealthResponse computeAndSave(String userId) {

        log.info("Computing financial health score for userId: {}", userId);

        FinancialProfile profile = profileRepository
                .findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "FinancialProfile", "userId", userId));

        // Run score calculation
        ScoreCalculator.ScoreResult result =
                scoreCalculator.calculate(profile);

        // Mark all previous scores as not latest
        scoreRepository.markAllAsNotLatest(userId);

        // Persist new score
        FinancialHealthScore entity = buildEntity(userId, result, profile);
        FinancialHealthScore saved  = scoreRepository.save(entity);

        // Also update financial profile with latest score
        profileRepository.updateComputedFields(
                userId,
                profile.getFoir(),
                result.overallScore(),
                result.riskLevel()
        );

        return buildResponse(saved, profile, true);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "financial-health", key = "#userId")
    public FinancialHealthResponse getLatestScore(String userId) {

        FinancialHealthScore score = scoreRepository
                .findByUserIdAndIsLatestTrue(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "FinancialHealthScore", "userId", userId));

        FinancialProfile profile = profileRepository
                .findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "FinancialProfile", "userId", userId));

        return buildResponse(score, profile, false);
    }

    // ── Build entity from ScoreResult ────────────────────────
    private FinancialHealthScore buildEntity(
            String userId,
            ScoreCalculator.ScoreResult result,
            FinancialProfile profile) {

        String tipsJson     = toJson(result.improvementTips());
        String warningsJson = toJson(result.riskWarnings());

        return FinancialHealthScore.builder()
                .userId(userId)
                .overallScore(result.overallScore())
                .debtBurdenScore(result.debtBurdenScore())
                .savingsRateScore(result.savingsRateScore())
                .creditScoreComponent(result.creditScoreComponent())
                .creditUtilizationScore(result.creditUtilizationScore())
                .foirAtScoring(profile.getFoir())
                .utilizationAtScoring(profile.getCurrentCreditUtilization())
                .savingsRateAtScoring(computeSavingsRate(profile))
                .riskLevel(result.riskLevel())
                .improvementTips(tipsJson)
                .riskWarnings(warningsJson)
                .scoredOn(result.scoredOn())
                .isLatest(true)
                .build();
    }

    // ── Build response DTO ────────────────────────────────────
    private FinancialHealthResponse buildResponse(
            FinancialHealthScore score,
            FinancialProfile profile,
            boolean isFirst) {

        // Load trend history
        List<FinancialHealthResponse.ScoreTrend> trend =
                scoreRepository
                        .findTop12ByUserIdOrderByScoredOnDesc(
                                score.getUserId())
                        .stream()
                        .map(s -> FinancialHealthResponse.ScoreTrend
                                .builder()
                                .scoredOn(s.getScoredOn())
                                .score(s.getOverallScore())
                                .riskLevel(s.getRiskLevel())
                                .build())
                        .toList();

        return FinancialHealthResponse.builder()
                .overallScore(score.getOverallScore())
                .riskLevel(score.getRiskLevel())
                .riskLabel(riskLabel(score.getOverallScore()))
                .debtBurdenScore(score.getDebtBurdenScore())
                .savingsRateScore(score.getSavingsRateScore())
                .creditScoreComponent(score.getCreditScoreComponent())
                .utilizationScore(score.getCreditUtilizationScore())
                .foir(profile.getFoir())
                .creditScore(profile.getCreditScore())
                .creditScoreRange(profile.getCreditScoreRange())
                .creditUtilization(profile.getCurrentCreditUtilization())
                .savingsRate(computeSavingsRate(profile))
                .improvementTips(fromJson(score.getImprovementTips()))
                .riskWarnings(fromJson(score.getRiskWarnings()))
                .scoreTrend(trend)
                .scoredOn(score.getScoredOn())
                .isFirstScore(isFirst)
                .build();
    }

    private String riskLabel(int score) {
        if (score >= 75) return "Financially Healthy";
        if (score >= 50) return "Needs Attention";
        if (score >= 25) return "At Risk";
        return "Critical — Take Action Now";
    }

    private BigDecimal computeSavingsRate(FinancialProfile p) {
        if (p.getMonthlyIncome() == null ||
                p.getMonthlyIncome().compareTo(BigDecimal.ZERO) == 0
                || p.getMonthlySavings() == null)
            return BigDecimal.ZERO;
        return p.getMonthlySavings()
                .divide(p.getMonthlyIncome(), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private String toJson(Object obj) {
        try { return objectMapper.writeValueAsString(obj); }
        catch (JsonProcessingException e) { return "[]"; }
    }

    private List<String> fromJson(String json) {
        if (json == null) return List.of();
        try {
            return objectMapper.readValue(json,
                    new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) { return List.of(); }
    }
}

