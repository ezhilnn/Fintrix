// ================================================================
// FILE 1: LoanEligibilityRule.java — Rule Interface
// ================================================================
package com.fintrix.modules.loan.rules;

import com.fintrix.modules.financialprofile.domain.FinancialProfile;
import com.fintrix.modules.loan.domain.Lender;
import com.fintrix.modules.loan.dto.LoanEligibilityRequest;

import java.util.List;

/**
 * LoanEligibilityRule — Rule Interface
 *
 * This is the Strategy Pattern.
 *
 * Why Strategy Pattern for rules?
 *
 *  Without Strategy:
 *    if (creditScoreFails) { ... }
 *    if (foirFails) { ... }
 *    if (employmentFails) { ... }
 *    if (ageFails) { ... }
 *    → One giant if-else method
 *    → Adding a new rule = editing existing code
 *    → Hard to test individual rules
 *
 *  With Strategy Pattern:
 *    Each rule = one class implementing this interface
 *    Adding a new rule = create new class, register in engine
 *    Existing code never changes (Open/Closed Principle)
 *    Each rule tested independently
 *
 * RuleResult:
 *   passed    → did this rule pass?
 *   score     → how much does this rule contribute to approval score?
 *   reason    → why did it fail? (shown to user)
 *   tip       → how to fix it? (educational output)
 */
public interface LoanEligibilityRule {

    RuleResult evaluate(
            FinancialProfile      profile,
            Lender                lender,
            LoanEligibilityRequest request
    );

    // Inner record — result of one rule evaluation
    record RuleResult(
            boolean     passed,
            int         scoreImpact,    // positive = adds to approval score
            String      reason,         // shown if failed
            String      tip             // improvement tip
    ) {
        // Factory for passed rule
        static RuleResult pass(int scoreImpact) {
            return new RuleResult(true, scoreImpact, null, null);
        }

        // Factory for failed rule with reason and tip
        static RuleResult fail(String reason, String tip) {
            return new RuleResult(false, 0, reason, tip);
        }

        // Factory for partial pass (eligible but not ideal)
        static RuleResult partial(int scoreImpact,
                                  String reason, String tip) {
            return new RuleResult(true, scoreImpact, reason, tip);
        }
    }
}
