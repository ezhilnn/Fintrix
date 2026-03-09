// ================================================================
// FILE 1: CreditCardRepository.java
// ================================================================
package com.fintrix.modules.creditcard.repository;

import com.fintrix.modules.creditcard.domain.CardCategory;
import com.fintrix.modules.creditcard.domain.CreditCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface CreditCardRepository
        extends JpaRepository<CreditCard, String> {

    // Pre-filter candidates before recommendation engine
    @Query("""
        SELECT c FROM CreditCard c
        WHERE c.isActive          = true
          AND c.minCreditScore    <= :creditScore
          AND c.minMonthlyIncome  <= :monthlyIncome
          AND c.minAge            <= :age
          AND c.maxAge            >= :age
        ORDER BY c.minCreditScore DESC
        """)
    List<CreditCard> findCandidateCards(
            @Param("creditScore")   int        creditScore,
            @Param("monthlyIncome") BigDecimal monthlyIncome,
            @Param("age")           int        age
    );

    List<CreditCard> findByCardCategoryAndIsActiveTrue(
            CardCategory category);
}

