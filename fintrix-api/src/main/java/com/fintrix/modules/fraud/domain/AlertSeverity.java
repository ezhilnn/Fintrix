package com.fintrix.modules.fraud.domain;
 
public enum AlertSeverity {
    SAFE,        // found in regulator registry as ACTIVE — verified clean
    UNVERIFIED,  // not found in registry — cannot confirm legitimacy
    LOW,         // minor informational flag — check before proceeding
    MEDIUM,      // strong signals of illegitimate operation
    HIGH,        // confirmed regulatory violation or suspended entity
    CRITICAL     // matches known fraud/scam keyword patterns
}