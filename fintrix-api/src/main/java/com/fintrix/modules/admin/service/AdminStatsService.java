// ────────────────────────────────────────────────────────────────
package com.fintrix.modules.admin.service;

import com.fintrix.modules.admin.dto.AdminDashboardStats;
import com.fintrix.modules.audit.repository.DecisionAuditRepository;
import com.fintrix.modules.tracking.repository.AffiliateClickRepository;
import com.fintrix.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AdminStatsService {

    private final UserRepository          userRepository;
    private final DecisionAuditRepository auditRepository;
    private final AffiliateClickRepository clickRepository;

    @Transactional(readOnly = true)
    public AdminDashboardStats buildStats() {

        long totalUsers   = userRepository.count();
        long activeUsers  = userRepository.countByIsActiveTrue();

        long loanChecks   = auditRepository
                .countByDecisionType("LOAN_ELIGIBILITY");
        long cardChecks   = auditRepository
                .countByDecisionType("CARD_RECOMMENDATION");
        long fraudChecks  = auditRepository
                .countByDecisionType("FRAUD_CHECK");

        long totalClicks  = clickRepository.count();
        long conversions  = clickRepository.countByIsConvertedTrue();
        BigDecimal revenue = clickRepository.sumCommissionEarned();

        return AdminDashboardStats.builder()
                .totalUsers(totalUsers)
                .activeUsers(activeUsers)
                .totalLoanChecks(loanChecks)
                .totalCardChecks(cardChecks)
                .totalFraudChecks(fraudChecks)
                .totalAffiliateClicks(totalClicks)
                .totalConversions(conversions)
                .estimatedRevenue(revenue != null
                        ? revenue : BigDecimal.ZERO)
                .build();
    }
}