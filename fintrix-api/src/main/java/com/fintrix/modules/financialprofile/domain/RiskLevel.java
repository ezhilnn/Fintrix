// ================================================================
// FILE: RiskLevel.java
// com/fintrix/modules/financialprofile/domain/RiskLevel.java
// ================================================================
package com.fintrix.modules.financialprofile.domain;

public enum RiskLevel {
    LOW,        // Financial health score 75–100
    MEDIUM,     // Financial health score 50–74
    HIGH,       // Financial health score 25–49
    CRITICAL    // Financial health score 0–24
}