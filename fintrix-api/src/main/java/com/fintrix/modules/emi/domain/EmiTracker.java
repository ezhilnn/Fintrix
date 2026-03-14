package com.fintrix.modules.emi.domain;
 
import com.fintrix.infrastructure.persistence.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
 
@Entity
@Table(name = "emi_trackers")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EmiTracker extends AuditableEntity {
 
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
 
    @Column(name = "user_id", nullable = false)
    private String userId;
 
    @Column(name = "loan_name", nullable = false)
    private String loanName;
 
    @Column(name = "lender_name")
    private String lenderName;
 
    @Column(name = "loan_type", length = 50)
    private String loanType;
 
    @Column(name = "principal_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal principalAmount;
 
    @Column(name = "emi_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal emiAmount;
 
    @Column(name = "due_date_of_month", nullable = false)
    private Integer dueDateOfMonth;     // 1-31
 
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
 
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;
 
    @Column(name = "remaining_emis")
    private Integer remainingEmis;
 
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;
 
    @Column(name = "reminder_days_before", nullable = false)
    @Builder.Default
    private Integer reminderDaysBefore = 3;
}
 