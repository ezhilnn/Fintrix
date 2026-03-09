
// ────────────────────────────────────────────────────────────────
package com.fintrix.modules.creditcard.service;

import com.fintrix.common.exception.ResourceNotFoundException;
import com.fintrix.modules.creditcard.domain.CreditCard;
import com.fintrix.modules.creditcard.dto.*;
import com.fintrix.modules.creditcard.repository.CreditCardRepository;
import com.fintrix.modules.creditcard.rules.CardRecommendationEngine;
import com.fintrix.modules.financialprofile.domain.FinancialProfile;
import com.fintrix.modules.financialprofile.repository.FinancialProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreditCardRecommendationServiceImpl
        implements CreditCardRecommendationService {

    private final FinancialProfileRepository profileRepository;
    private final CreditCardRepository       cardRepository;
    private final CardRecommendationEngine   engine;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "card-recommendation", key = "#userId")
    public CardRecommendationResponse getRecommendations(
            String userId, CardRecommendationRequest request) {

        log.info("Card recommendations requested for userId: {}", userId);

        FinancialProfile profile = profileRepository
                .findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "FinancialProfile", "userId", userId));

        int userScore = profile.getCreditScore() != null
                ? profile.getCreditScore() : 0;
        int userAge   = 25; // TODO: load from user entity in BFF step

        // Phase 1: DB pre-filter
        List<CreditCard> candidates = cardRepository.findCandidateCards(
                userScore,
                profile.getMonthlyIncome(),
                userAge
        );

        // Phase 2: Engine evaluation
        List<CardResult> recommended = new ArrayList<>();
        List<CardResult> other       = new ArrayList<>();
        List<CardResult> future      = new ArrayList<>();

        // Get all active cards for future cards check
        List<CreditCard> allCards = cardRepository.findAll();
        for (CreditCard card : allCards) {
            if (!candidates.contains(card)
                    && engine.isFutureCard(card, profile)) {
                CardResult res = engine.evaluate(card, profile, request);
                future.add(res);
            }
        }

        for (CreditCard card : candidates) {
            CardResult result = engine.evaluate(card, profile, request);
            if (Boolean.TRUE.equals(result.getIsEligible())) {
                if (result.getApprovalProbability() >= 75) {
                    recommended.add(result);
                } else {
                    other.add(result);
                }
            }
        }

        // Sort by match probability
        recommended.sort(
                Comparator.comparingInt(CardResult::getApprovalProbability)
                          .reversed());
        future.sort(
                Comparator.comparingInt(CardResult::getApprovalProbability)
                          .reversed());

        String overallTip = buildTip(profile, recommended.size());
        String warning    = recommended.size() > 0
                ? "Apply to only ONE card at a time. Multiple applications " +
                  "create hard inquiries and lower your CIBIL score."
                : null;

        return CardRecommendationResponse.builder()
                .recommendedCards(recommended)
                .otherEligibleCards(other)
                .futureCards(future)
                .overallTip(overallTip)
                .multipleCardWarning(warning)
                .build();
    }

    private String buildTip(FinancialProfile profile, int count) {
        if (count == 0)
            return "Improve your credit score to unlock credit card offers. " +
                   "Check 'Future Cards' to see what you can unlock in 6 months.";
        if (profile.getNumberOfCreditCards() >= 3)
            return "You already have " + profile.getNumberOfCreditCards() +
                   " cards. Consider whether a new card adds value " +
                   "before applying.";
        return "We found " + count +
               " card(s) that match your profile and preferences.";
    }
}
