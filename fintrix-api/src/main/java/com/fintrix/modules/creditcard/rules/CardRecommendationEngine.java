package com.fintrix.modules.creditcard.rules;

import com.fintrix.modules.creditcard.domain.CreditCard;
import com.fintrix.modules.creditcard.dto.CardRecommendationRequest;
import com.fintrix.modules.creditcard.dto.CardResult;
import com.fintrix.modules.financialprofile.domain.FinancialProfile;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * CardRecommendationEngine (Updated)
 *
 * Now properly delegates to the 4 CardEligibilityRule implementations
 * using the Strategy Pattern — same architecture as LoanRuleEngine.
 *
 * Rule execution order:
 *  1. CardCreditScoreRule  → primary hard gate (scoreImpact up to 30)
 *  2. CardIncomeRule       → income sufficiency   (scoreImpact up to 25)
 *  3. CardEmploymentRule   → employment accepted  (scoreImpact up to 20)
 *  4. CardUtilizationRule  → utilization health   (scoreImpact up to 20)
 *
 * Max total scoreImpact = 95
 *
 * Approval probability:
 *  Hard fail present  → 5  + (score / 95 × 45)  → max 50%
 *  All rules pass     → 55 + (score / 95 × 43)  → max 98%
 *
 * isEligible = no hard fail AND probability >= 55
 *
 * isFutureCard:
 *  Credit score is the only hard gate we check.
 *  If gap <= 80 points → show as future card with goal score.
 *  This drives re-engagement — user comes back when score improves.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CardRecommendationEngine {

    // ── Inject all 4 rules via constructor (Spring wires them) ─
    private final CardCreditScoreRule  creditScoreRule;
    private final CardIncomeRule       incomeRule;
    private final CardEmploymentRule   employmentRule;
    private final CardUtilizationRule  utilizationRule;
    private final ObjectMapper         objectMapper;

    private static final int MAX_SCORE         = 95;
    private static final int ELIGIBLE_THRESHOLD = 55;
    private static final int FUTURE_CARD_GAP   = 80;

    /**
     * Evaluate one card for a user.
     * Runs all 4 rules, aggregates score, builds CardResult.
     */
    public CardResult evaluate(
            CreditCard card,
            FinancialProfile profile,
            CardRecommendationRequest request) {

        log.debug("Evaluating card: {} {} for userId: {}",
                card.getBankName(), card.getCardName(),
                profile.getUserId());

        List<String> failureReasons  = new ArrayList<>();
        List<String> improvementTips = new ArrayList<>();
        int          totalScore      = 0;
        boolean      hardFailed      = false;

        // ── Run all 4 rules in order ──────────────────────────
        List<CardEligibilityRule> rules = List.of(
                creditScoreRule,
                incomeRule,
                employmentRule,
                utilizationRule
        );

        for (CardEligibilityRule rule : rules) {
            CardEligibilityRule.RuleResult result =
                    rule.evaluate(profile, card, request);

            totalScore += result.scoreImpact();

            if (!result.passed()) {
                hardFailed = true;
                if (result.reason() != null)
                    failureReasons.add(result.reason());
                if (result.tip() != null)
                    improvementTips.add(result.tip());
            } else {
                // Partial passes may still carry tips
                if (result.tip() != null)
                    improvementTips.add(result.tip());
            }
        }

        // ── Approval probability ──────────────────────────────
        int approvalProbability = hardFailed
                ? computeIneligibleProbability(totalScore)
                : computeEligibleProbability(totalScore);

        boolean isEligible = !hardFailed
                && approvalProbability >= ELIGIBLE_THRESHOLD;

        String matchReason = buildMatchReason(
                card, profile, totalScore, hardFailed);

        return CardResult.builder()
                .cardId(card.getId())
                .bankName(card.getBankName())
                .cardName(card.getCardName())
                .logoUrl(card.getLogoUrl())
                .cardCategory(card.getCardCategory())
                .rewardType(card.getRewardType())
                .approvalProbability(approvalProbability)
                .isEligible(isEligible)
                .joiningFee(card.getJoiningFee())
                .annualFee(card.getAnnualFee())
                .annualFeeWaiverCondition(card.getAnnualFeeWaiverCondition())
                .rewardRate(card.getRewardRate())
                .welcomeBenefit(card.getWelcomeBenefit())
                .keyBenefits(parseBenefits(card.getKeyBenefits()))
                .matchReason(matchReason)
                .failureReasons(failureReasons)
                .build();
    }

    /**
     * Is this card reachable within ~6 months of score improvement?
     * Only credit score gap matters here — other factors are adjustable.
     */
    public boolean isFutureCard(CreditCard card,
                                 FinancialProfile profile) {
        int userScore = profile.getCreditScore() != null
                ? profile.getCreditScore() : 0;
        int gap = card.getMinCreditScore() - userScore;
        return gap > 0 && gap <= FUTURE_CARD_GAP;
    }

    // ── Probability when all rules pass ──────────────────────
    // Maps 0–95 → 55–98 range
    private int computeEligibleProbability(int totalScore) {
        double ratio = (double) totalScore / MAX_SCORE;
        return (int) Math.min(98, 55 + ratio * 43);
    }

    // ── Probability when hard fail occurred ──────────────────
    // Maps 0–95 → 5–50 range (shows partial info, not eligible)
    private int computeIneligibleProbability(int totalScore) {
        double ratio = (double) totalScore / MAX_SCORE;
        return (int) Math.min(50, 5 + ratio * 45);
    }

    // ── Human readable match reason ───────────────────────────
    private String buildMatchReason(CreditCard card,
                                     FinancialProfile profile,
                                     int totalScore,
                                     boolean hardFailed) {
        if (hardFailed)
            return "Not eligible yet — see improvement tips below";

        if (totalScore >= 70)
            return "Excellent match — rewards and benefits align " +
                   "perfectly with your profile";

        if (totalScore >= 45)
            return "Good match — " + card.getRewardType().name()
                    .toLowerCase().replace("_", " ") +
                   " rewards suit your spending";

        return "Basic match — meets your eligibility criteria";
    }

    // ── Parse JSON benefits array stored in DB ────────────────
    private List<String> parseBenefits(String json) {
        if (json == null || json.isBlank()) return List.of();
        try {
            return objectMapper.readValue(json,
                    new TypeReference<List<String>>() {});
        } catch (Exception e) {
            log.warn("Failed to parse card benefits JSON: {}", json);
            return List.of();
        }
    }
}