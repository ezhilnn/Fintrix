// ── AlertSeverity.java ───────────────────────────────────────────
package com.fintrix.modules.fraud.domain;

public enum AlertSeverity {
    LOW,       // suspicious but unconfirmed
    MEDIUM,    // strong signals of illegitimate operation
    HIGH,      // confirmed regulatory violation
    CRITICAL   // known scam / blacklisted entity
}

