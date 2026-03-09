
// ================================================================
// FILE 2: FraudCheckRequest.java
// ================================================================
package com.fintrix.modules.fraud.dto;

import com.fintrix.modules.fraud.domain.EntityType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FraudCheckRequest {

    @NotBlank(message = "Entity name is required")
    private String entityName;       // "ABC Investment Scheme"

    @NotNull(message = "Entity type is required")
    private EntityType entityType;   // INVESTMENT_SCHEME, LENDER etc.
}

