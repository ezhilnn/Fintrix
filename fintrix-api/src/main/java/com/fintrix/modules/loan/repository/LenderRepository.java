package com.fintrix.modules.loan.repository;

import com.fintrix.modules.loan.domain.Lender;
import com.fintrix.modules.loan.domain.LoanType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * LenderRepository
 *
 * Fetches lender data seeded by Flyway V7 migration.
 *
 * Key query — pre-filter lenders before rule engine runs:
 *
 * findCandidateLenders():
 *   We do NOT load all lenders and filter in Java.
 *   That is wasteful — imagine 500 lenders in DB.
 *
 *   Instead we push basic filters to DB (SQL WHERE clause):
 *     - correct loan type
 *     - minimum credit score check
 *     - minimum income check
 *     - age range check
 *     - lender is active
 *
 *   This reduces 500 lenders → ~10 candidates.
 *   Rule engine then runs detailed checks on those 10 only.
 *
 *   This is called "two-phase filtering":
 *     Phase 1 → DB query (cheap, fast, indexed)
 *     Phase 2 → Rule engine (detailed Java logic)
 */
@Repository
public interface LenderRepository extends JpaRepository<Lender, String> {

    // Fetch ALL active lenders for a loan type
    List<Lender> findByLoanTypeAndIsActiveTrue(LoanType loanType);

    // Pre-filter candidates before rule engine
    // Pushes basic numeric checks to DB layer
    @Query("""
        SELECT l FROM Lender l
        WHERE l.loanType              = :loanType
          AND l.isActive              = true
          AND l.minCreditScore        <= :creditScore
          AND l.minMonthlyIncome      <= :monthlyIncome
          AND l.minAge                <= :age
          AND l.maxAge                >= :age
          AND l.minLoanAmount         <= :requestedAmount
          AND l.maxLoanAmount         >= :requestedAmount
        ORDER BY l.minInterestRate ASC
        """)
    List<Lender> findCandidateLenders(
            @Param("loanType")        LoanType   loanType,
            @Param("creditScore")     int        creditScore,
            @Param("monthlyIncome")   BigDecimal monthlyIncome,
            @Param("age")             int        age,
            @Param("requestedAmount") BigDecimal requestedAmount
    );
}