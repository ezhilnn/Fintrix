package com.fintrix.modules.emi.dto;
 
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
 
@Getter
@Setter
public class EmiTrackerRequest {
 
    @NotBlank(message = "Loan name is required")
    private String loanName;
 
    private String    lenderName;
    private String    loanType;
 
    @NotNull
    @DecimalMin("1000")
    private BigDecimal principalAmount;
 
    @NotNull
    @DecimalMin("100")
    private BigDecimal emiAmount;
 
    @NotNull
    @Min(1) @Max(31)
    private Integer dueDateOfMonth;
 
    @NotNull
    private LocalDate startDate;
 
    @NotNull
    private LocalDate endDate;
 
    @Min(1) @Max(10)
    private Integer reminderDaysBefore = 3;
}