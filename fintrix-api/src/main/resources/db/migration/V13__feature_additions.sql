-- ================================================================
-- V13__feature_additions.sql
-- Push notifications, consent, user view tracking,
-- affiliate/referral tracking, ML training data
-- ================================================================

-- ── 1. Device tokens for push notifications ───────────────────────
CREATE TABLE IF NOT EXISTS device_tokens (
    id           VARCHAR(36)  PRIMARY KEY DEFAULT gen_random_uuid()::TEXT,
    user_id      VARCHAR(36)  NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    fcm_token    TEXT         NOT NULL,
    device_type  VARCHAR(20)  NOT NULL DEFAULT 'ANDROID', -- ANDROID, IOS, WEB
    device_id    VARCHAR(255),
    app_version  VARCHAR(20),
    is_active    BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at   TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP    NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_dt_user_id   ON device_tokens(user_id);
CREATE INDEX idx_dt_fcm_token ON device_tokens(fcm_token);

-- ── 2. Notification history ────────────────────────────────────────
CREATE TABLE IF NOT EXISTS notifications (
    id                VARCHAR(36)  PRIMARY KEY DEFAULT gen_random_uuid()::TEXT,
    user_id           VARCHAR(36)  NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title             VARCHAR(200) NOT NULL,
    body              TEXT         NOT NULL,
    notification_type VARCHAR(50)  NOT NULL, -- EMI_REMINDER, SCORE_UPDATE, FRAUD_ALERT, OFFER, SYSTEM
    payload           JSONB,                 -- deep link data for frontend routing
    is_read           BOOLEAN      NOT NULL DEFAULT FALSE,
    is_sent           BOOLEAN      NOT NULL DEFAULT FALSE,
    sent_at           TIMESTAMP,
    read_at           TIMESTAMP,
    fcm_message_id    VARCHAR(255),          -- Firebase delivery receipt
    created_at        TIMESTAMP    NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_notif_user_id    ON notifications(user_id);
CREATE INDEX idx_notif_is_read    ON notifications(user_id, is_read);
CREATE INDEX idx_notif_created_at ON notifications(created_at);

-- ── 3. User consent records (DPDP Act 2023) ───────────────────────
CREATE TABLE IF NOT EXISTS user_consents (
    id               VARCHAR(36)  PRIMARY KEY DEFAULT gen_random_uuid()::TEXT,
    user_id          VARCHAR(36)  NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    consent_type     VARCHAR(50)  NOT NULL,    -- DATA_PROCESSING, MARKETING, CREDIT_CHECK, THIRD_PARTY_SHARE
    consent_version  VARCHAR(20)  NOT NULL,    -- "v1.0", "v1.1" — bump on policy changes
    is_granted       BOOLEAN      NOT NULL,
    ip_address       VARCHAR(50),
    user_agent       VARCHAR(500),
    granted_at       TIMESTAMP,
    withdrawn_at     TIMESTAMP,
    expires_at       TIMESTAMP,                -- some consents expire (e.g. credit check)
    created_at       TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMP    NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_uc_user_id      ON user_consents(user_id);
CREATE UNIQUE INDEX idx_uc_user_type_version
    ON user_consents(user_id, consent_type, consent_version);

-- ── 4. User view / page tracking ──────────────────────────────────
CREATE TABLE IF NOT EXISTS user_events (
    id           VARCHAR(36)  PRIMARY KEY DEFAULT gen_random_uuid()::TEXT,
    user_id      VARCHAR(36)  REFERENCES users(id) ON DELETE SET NULL,
    session_id   VARCHAR(100),
    event_type   VARCHAR(50)  NOT NULL,   -- PAGE_VIEW, BUTTON_CLICK, LOAN_CHECK, CARD_VIEW, FRAUD_CHECK
    page         VARCHAR(200),            -- /dashboard, /loans, /cards, /fraud
    element_id   VARCHAR(100),            -- button/card ID clicked
    entity_id    VARCHAR(100),            -- lender/card ID viewed
    metadata     JSONB,                   -- flexible extra data
    duration_ms  INTEGER,                 -- time spent on page
    ip_address   VARCHAR(50),
    device_type  VARCHAR(20),
    created_at   TIMESTAMP    NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_ue_user_id    ON user_events(user_id);
CREATE INDEX idx_ue_event_type ON user_events(event_type);
CREATE INDEX idx_ue_created_at ON user_events(created_at);
-- Partition hint for large volumes (implement when > 10M rows)
-- PARTITION BY RANGE (created_at)

-- ── 5. Affiliate partners ─────────────────────────────────────────
CREATE TABLE IF NOT EXISTS affiliate_partners (
    id               VARCHAR(36)  PRIMARY KEY DEFAULT gen_random_uuid()::TEXT,
    partner_name     VARCHAR(200) NOT NULL,
    partner_type     VARCHAR(50)  NOT NULL,  -- LENDER, CARD_ISSUER, INSURANCE
    entity_id        VARCHAR(36),            -- lender.id or credit_card.id
    utm_source       VARCHAR(100) NOT NULL UNIQUE, -- "hdfc-personal-loan"
    base_url         VARCHAR(500) NOT NULL,  -- https://hdfc.com/apply?ref=...
    commission_type  VARCHAR(30)  NOT NULL,  -- CPA (per approved), CPL (per lead), FLAT
    commission_amount NUMERIC(10,2),         -- CPA: ₹1500 per approved loan
    commission_rate   NUMERIC(5,4),          -- CPL: 0.5% of loan amount
    is_active        BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at       TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMP    NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_ap_utm_source ON affiliate_partners(utm_source);
CREATE INDEX idx_ap_entity_id  ON affiliate_partners(entity_id);

-- ── 6. Referral / click tracking ─────────────────────────────────
CREATE TABLE IF NOT EXISTS affiliate_clicks (
    id                  VARCHAR(36)  PRIMARY KEY DEFAULT gen_random_uuid()::TEXT,
    user_id             VARCHAR(36)  REFERENCES users(id) ON DELETE SET NULL,
    affiliate_partner_id VARCHAR(36) NOT NULL REFERENCES affiliate_partners(id),
    click_ref           VARCHAR(100) NOT NULL UNIQUE, -- unique ID per click for attribution
    product_type        VARCHAR(50)  NOT NULL,  -- PERSONAL_LOAN, CREDIT_CARD, HOME_LOAN
    product_id          VARCHAR(36),            -- lender.id or card.id
    utm_campaign        VARCHAR(100),
    utm_medium          VARCHAR(50),
    approval_probability NUMERIC(5,2),          -- probability shown to user at click time
    ip_address          VARCHAR(50),
    user_agent          VARCHAR(500),
    clicked_at          TIMESTAMP    NOT NULL DEFAULT NOW(),
    -- Conversion tracking (filled when partner confirms)
    is_converted        BOOLEAN      NOT NULL DEFAULT FALSE,
    converted_at        TIMESTAMP,
    loan_amount         NUMERIC(12,2),          -- if loan was approved
    commission_earned   NUMERIC(10,2),
    payout_status       VARCHAR(30)  DEFAULT 'PENDING' -- PENDING, CONFIRMED, PAID
);
CREATE INDEX idx_ac_user_id    ON affiliate_clicks(user_id);
CREATE INDEX idx_ac_partner_id ON affiliate_clicks(affiliate_partner_id);
CREATE INDEX idx_ac_click_ref  ON affiliate_clicks(click_ref);
CREATE INDEX idx_ac_clicked_at ON affiliate_clicks(clicked_at);

-- ── 7. ML training data — approval outcomes ───────────────────────
-- Stores anonymised feature vectors for training approval probability model
-- Fields mirror what the rule engine uses for scoring
CREATE TABLE IF NOT EXISTS ml_approval_training_data (
    id                      VARCHAR(36)  PRIMARY KEY DEFAULT gen_random_uuid()::TEXT,
    -- Input features (anonymised — no PII)
    credit_score            INTEGER,
    foir                    NUMERIC(5,2),
    monthly_income_bucket   VARCHAR(20),  -- "10K-25K", "25K-50K", "50K-100K", "100K+"
    employment_type         VARCHAR(30),
    years_of_experience     INTEGER,
    loan_type               VARCHAR(50),
    requested_amount_bucket VARCHAR(30),  -- "0-1L", "1L-5L", "5L-20L", "20L+"
    tenure_months           INTEGER,
    credit_utilization      NUMERIC(5,2),
    number_of_active_loans  INTEGER,
    -- Lender features
    lender_id               VARCHAR(36),
    lender_type             VARCHAR(30),  -- BANK, NBFC
    -- Outcome (the label)
    rule_probability        INTEGER,      -- what rule engine predicted (0-100)
    was_approved            BOOLEAN,      -- actual outcome (filled from partner webhook)
    interest_rate_offered   NUMERIC(5,2), -- actual rate offered
    -- Meta
    data_source             VARCHAR(30)  NOT NULL DEFAULT 'RULE_ENGINE', -- RULE_ENGINE, PARTNER_API
    created_at              TIMESTAMP    NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_ml_loan_type   ON ml_approval_training_data(loan_type);
CREATE INDEX idx_ml_lender_id   ON ml_approval_training_data(lender_id);
CREATE INDEX idx_ml_created_at  ON ml_approval_training_data(created_at);