
// ================================================================
// FILE 2: LenderResult.java  — one lender's result inside response
// ================================================================
package com.fintrix.modules.loan.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * LenderResult
 *
 * Represents one lender's eligibility result.
 * The final response contains a LIST of these.
 *
 * approvalProbability:
 *   0–100 score computed by LoanRuleEngine.
 *   Higher = more likely to be approved.
 *   Shown as a progress bar on frontend.
 *
 * failureReasons:
 *   If probability < 100, these explain WHY.
 *   Example: ["Credit score 680 — lender requires 700",
 *              "FOIR 52% — lender max is 50%"]
 *   This is the KEY educational value of Fintrix.
 *
 * estimatedEmi:
 *   Monthly EMI computed using standard formula.
 *   Helps user decide if they can afford repayment.
 */

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LenderResult {

    private String     lenderId;
    private String     lenderName;
    private String     logoUrl;
    private String     applyUrl;          // "Apply Now" button URL

    // Eligibility result
    private Integer    approvalProbability;    // 0–100
    private Boolean    isEligible;             // true if >= 60%
    private BigDecimal minInterestRate;
    private BigDecimal maxInterestRate;
    private BigDecimal processingFeePercent;

    // Computed for this user
    private BigDecimal estimatedEmi;           // monthly EMI in ₹
    private BigDecimal totalInterestPayable;   // total interest over tenure

    // Educational output — why approved/rejected
    private List<String> failureReasons;       // empty if fully eligible
    private List<String> improvementTips;      // how to improve chances
}

