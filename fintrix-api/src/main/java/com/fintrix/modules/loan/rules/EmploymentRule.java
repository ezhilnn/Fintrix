
// ================================================================
// FILE 4: EmploymentRule.java
// ================================================================
package com.fintrix.modules.loan.rules;

import com.fintrix.modules.financialprofile.domain.FinancialProfile;
import com.fintrix.modules.loan.domain.Lender;
import com.fintrix.modules.loan.dto.LoanEligibilityRequest;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * EmploymentRule
 *
 * Checks:
 *  1. User's employment type is accepted by lender
 *  2. Years of experience meets lender's minimum
 *
 * Real-world insight:
 *   HDFC Bank personal loans require minimum 1 year employment.
 *   Bajaj Finance accepts self-employed but not students.
 *   SBI home loans prefer government/PSU employees (lower rates).
 *
 *   We store allowedEmploymentTypes as CSV in lender table:
 *   "SALARIED,GOVERNMENT,PSU"
 *   Parse and check at runtime.
 */
@Component
public class EmploymentRule implements LoanEligibilityRule {

    @Override
    public RuleResult evaluate(
            FinancialProfile profile,
            Lender lender,
            LoanEligibilityRequest request) {

        if (profile.getEmploymentType() == null) {
            return RuleResult.fail(
                    "Employment type not provided",
                    "Please complete your financial profile " +
                    "with your employment details.");
        }

        String userEmployment = profile.getEmploymentType().name();

        // Parse lender's allowed employment types from CSV
        String allowedCsv = lender.getAllowedEmploymentTypes();
        if (allowedCsv == null || allowedCsv.isBlank()) {
            return RuleResult.pass(15); // lender accepts all types
        }

        List<String> allowedTypes = Arrays.asList(
                allowedCsv.split(","));

        // Employment type not accepted
        if (!allowedTypes.contains(userEmployment)) {
            return RuleResult.fail(
                    String.format(
                        "%s does not offer loans to %s employees",
                        lender.getName(),
                        userEmployment.toLowerCase()
                            .replace("_", " ")),
                    "Consider lenders that accept your employment type, " +
                    "or explore secured loan options."
            );
        }

        // Check years of experience if lender requires it
        Integer minYears = lender.getMinEmploymentYears();
        Integer userYears = profile.getYearsOfExperience();

        if (minYears != null && minYears > 0) {
            if (userYears == null || userYears < minYears) {
                return RuleResult.fail(
                        String.format(
                            "%s requires minimum %d year(s) of employment. " +
                            "You have %d year(s).",
                            lender.getName(),
                            minYears,
                            userYears != null ? userYears : 0),
                        String.format(
                            "Apply again after completing %d year(s) " +
                            "at your current employer.",
                            minYears)
                );
            }
        }

        // Bonus points for stable employment
        if (userYears != null && userYears >= 3)
            return RuleResult.pass(20);

        return RuleResult.pass(15);
    }
}