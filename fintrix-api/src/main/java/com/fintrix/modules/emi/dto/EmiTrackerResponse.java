package com.fintrix.modules.emi.dto;
 
import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;
import java.time.LocalDate;
 
@Getter
@Builder
public class EmiTrackerResponse {
    private String     id;
    private String     loanName;
    private String     lenderName;
    private String     loanType;
    private BigDecimal principalAmount;
    private BigDecimal emiAmount;
    private Integer    dueDateOfMonth;
    private LocalDate  startDate;
    private LocalDate  endDate;
    private Integer    remainingEmis;
    private Boolean    isDueSoon;        // due within 3 days
    private String     dueDateLabel;     // "Due on 5th of every month"
}