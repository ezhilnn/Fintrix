
// ================================================================
// FILE 2: CacheKeys.java
// ================================================================
package com.fintrix.infrastructure.cache;

/**
 * CacheKeys — centralised cache name constants
 * Prevents typos when referencing cache names across services.
 */
public final class CacheKeys {
    private CacheKeys() {}

    public static final String USER_PROFILE        = "user-profile";
    public static final String FINANCIAL_HEALTH    = "financial-health";
    public static final String LOAN_ELIGIBILITY    = "loan-eligibility";
    public static final String CARD_RECOMMENDATION = "card-recommendation";
}
