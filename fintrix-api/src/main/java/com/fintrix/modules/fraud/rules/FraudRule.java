

// ================================================================
// FILE 4: FraudRule.java — interface
// ================================================================
package com.fintrix.modules.fraud.rules;

import com.fintrix.modules.fraud.dto.FraudCheckRequest;

public interface FraudRule {

    FraudRuleResult evaluate(FraudCheckRequest request);

    record FraudRuleResult(
            boolean     flagged,
            String      reason,
            String      tip,
            int         severityScore  // 0=clean, 1=low, 2=medium, 3=high, 4=critical
    ) {
        static FraudRuleResult clean() {
            return new FraudRuleResult(false, null, null, 0);
        }
        static FraudRuleResult flag(String reason, String tip, int score) {
            return new FraudRuleResult(true, reason, tip, score);
        }
    }
}

