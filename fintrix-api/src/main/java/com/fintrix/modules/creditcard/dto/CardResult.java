
// ────────────────────────────────────────────────────────────────
package com.fintrix.modules.creditcard.dto;

import com.fintrix.modules.creditcard.domain.CardCategory;
import com.fintrix.modules.creditcard.domain.RewardType;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import lombok.*;

/**
 * CardResult — one card's recommendation result
 */


@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardResult {
    private String      cardId;
    private String      bankName;
    private String      cardName;
    private String      logoUrl;
    private CardCategory cardCategory;
    private RewardType   rewardType;

    // Eligibility
    private Integer     approvalProbability;   // 0-100
    private Boolean     isEligible;

    // Fees
    private BigDecimal  joiningFee;
    private BigDecimal  annualFee;
    private String      annualFeeWaiverCondition;

    // Benefits
    private String      rewardRate;
    private String      welcomeBenefit;
    private List<String> keyBenefits;

    // Why recommended for this user
    private String      matchReason;
    private List<String> failureReasons;
}

