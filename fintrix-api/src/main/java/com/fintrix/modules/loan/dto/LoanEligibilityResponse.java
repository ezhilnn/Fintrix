
// ================================================================
// FILE 3: LoanEligibilityResponse.java
// ================================================================
package com.fintrix.modules.loan.dto;

import com.fintrix.modules.loan.domain.LoanType;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

/**
 * LoanEligibilityResponse
 *
 * Complete response returned to frontend.
 *
 * Structure:
 * {
 *   loanType: "PERSONAL_LOAN",
 *   requestedAmount: 500000,
 *   tenureMonths: 36,
 *   userFoir: 16.00,
 *   userCreditScore: 720,
 *   eligibleLenders: [
 *     { lenderName: "HDFC Bank", approvalProbability: 92, ... },
 *     { lenderName: "ICICI Bank", approvalProbability: 88, ... }
 *   ],
 *   ineligibleLenders: [
 *     { lenderName: "Axis Bank", approvalProbability: 35,
 *       failureReasons: ["FOIR too high"], ... }
 *   ],
 *   overallSuggestion: "You are eligible at 2 lenders. ..."
 * }
 */
@Getter
@Builder
public class LoanEligibilityResponse {

    // What was requested
    private LoanType   loanType;
    private BigDecimal requestedAmount;
    private Integer    tenureMonths;
    private String     purpose;

    // User's financial snapshot at time of check
    private BigDecimal userFoir;
    private Integer    userCreditScore;
    private String     userCreditScoreRange;
    private BigDecimal userMonthlyIncome;

    // Results split into two lists for clear UX
    private List<LenderResult> eligibleLenders;      // probability >= 60
    private List<LenderResult> ineligibleLenders;    // probability < 60

    // Summary message for dashboard
    private String overallSuggestion;

    // Warning if user should NOT apply right now
    private String creditScoreWarning;
}