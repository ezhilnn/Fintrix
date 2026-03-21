// ================================================================
// FILE 2: ConsentType.java
// ================================================================
package com.fintrix.modules.consent.domain;

public enum ConsentType {
    DATA_PROCESSING,   // mandatory — without this, app cannot function
    MARKETING,         // optional — promotional emails/push
    CREDIT_CHECK,      // expires — user must re-consent every 6 months
    THIRD_PARTY_SHARE  // required per click to send user to lender/card
}
