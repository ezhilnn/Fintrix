-- ================================================================
-- V2__schema_enhancements.sql
-- Adds missing columns + new tables needed for production features
-- ================================================================

-- ── 1. Add missing columns to credit_cards ───────────────────────
ALTER TABLE credit_cards ADD COLUMN IF NOT EXISTS card_network          VARCHAR(30);
ALTER TABLE credit_cards ADD COLUMN IF NOT EXISTS fuel_surcharge_waiver BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE credit_cards ADD COLUMN IF NOT EXISTS international_usage   BOOLEAN NOT NULL DEFAULT TRUE;
ALTER TABLE credit_cards ADD COLUMN IF NOT EXISTS lounge_access         VARCHAR(100);
ALTER TABLE credit_cards ADD COLUMN IF NOT EXISTS interest_rate         NUMERIC(5,2) NOT NULL DEFAULT 42.00;

-- ── 2. Add missing columns to lenders ────────────────────────────
ALTER TABLE lenders ADD COLUMN IF NOT EXISTS lender_type        VARCHAR(30) NOT NULL DEFAULT 'BANK';
ALTER TABLE lenders ADD COLUMN IF NOT EXISTS regulator          VARCHAR(20) NOT NULL DEFAULT 'RBI';

-- ── 3. Regulated entities registry ───────────────────────────────
-- Stores RBI/SEBI/IRDAI/PFRDA registered entities for fraud checks
CREATE TABLE IF NOT EXISTS regulated_entities (
    id                  VARCHAR(36)  PRIMARY KEY DEFAULT gen_random_uuid()::TEXT,
    company_name        VARCHAR(255) NOT NULL,
    registration_number VARCHAR(100),
    regulator           VARCHAR(20)  NOT NULL,  -- RBI, SEBI, IRDAI, PFRDA
    category            VARCHAR(100) NOT NULL,  -- NBFC, BROKER, INVESTMENT_ADVISOR, INSURANCE, MF_AMC
    license_status      VARCHAR(30)  NOT NULL DEFAULT 'ACTIVE',
    website             VARCHAR(300),
    is_verified         BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_re_company_name ON regulated_entities(LOWER(company_name));
CREATE INDEX idx_re_regulator    ON regulated_entities(regulator);
CREATE INDEX idx_re_category     ON regulated_entities(category);

-- ── 4. Fraud keywords dataset ─────────────────────────────────────
CREATE TABLE IF NOT EXISTS fraud_keywords (
    id           VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid()::TEXT,
    keyword      VARCHAR(200) NOT NULL UNIQUE,
    risk_level   VARCHAR(20)  NOT NULL,  -- LOW, MEDIUM, HIGH, CRITICAL
    fraud_type   VARCHAR(100) NOT NULL,  -- PONZI, ADVANCE_FEE, FAKE_INVESTMENT etc.
    description  VARCHAR(500),
    is_active    BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at   TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_fk_keyword    ON fraud_keywords(LOWER(keyword));
CREATE INDEX idx_fk_risk_level ON fraud_keywords(risk_level);

-- ── 5. Decision audit logs ────────────────────────────────────────
-- Required for financial compliance - every recommendation logged
CREATE TABLE IF NOT EXISTS decision_audit_logs (
    id               VARCHAR(36)  PRIMARY KEY DEFAULT gen_random_uuid()::TEXT,
    user_id          VARCHAR(36)  NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    decision_type    VARCHAR(50)  NOT NULL,  -- LOAN_ELIGIBILITY, CARD_RECOMMENDATION, HEALTH_SCORE, FRAUD_CHECK
    input_parameters JSONB        NOT NULL,
    decision_output  JSONB        NOT NULL,
    ip_address       VARCHAR(50),
    user_agent       VARCHAR(500),
    duration_ms      INTEGER,
    created_at       TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_dal_user_id       ON decision_audit_logs(user_id);
CREATE INDEX idx_dal_decision_type ON decision_audit_logs(decision_type);
CREATE INDEX idx_dal_created_at    ON decision_audit_logs(created_at);

-- ── 6. EMI and credit cycle tracking ─────────────────────────────
CREATE TABLE IF NOT EXISTS emi_trackers (
    id                      VARCHAR(36)  PRIMARY KEY DEFAULT gen_random_uuid()::TEXT,
    user_id                 VARCHAR(36)  NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    loan_name               VARCHAR(200) NOT NULL,   -- "HDFC Personal Loan"
    lender_name             VARCHAR(150),
    loan_type               VARCHAR(50),
    principal_amount        NUMERIC(12,2) NOT NULL,
    emi_amount              NUMERIC(12,2) NOT NULL,
    due_date_of_month       INTEGER       NOT NULL,  -- 1-31
    start_date              DATE          NOT NULL,
    end_date                DATE          NOT NULL,
    remaining_emis          INTEGER,
    is_active               BOOLEAN       NOT NULL DEFAULT TRUE,
    reminder_days_before    INTEGER       NOT NULL DEFAULT 3,
    created_at              TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMP     NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_emi_user_id ON emi_trackers(user_id);

-- ── 7. Rate limiting table ────────────────────────────────────────
CREATE TABLE IF NOT EXISTS api_rate_limits (
    id              VARCHAR(36)  PRIMARY KEY DEFAULT gen_random_uuid()::TEXT,
    identifier      VARCHAR(255) NOT NULL,   -- userId or IP
    endpoint        VARCHAR(200) NOT NULL,
    request_count   INTEGER      NOT NULL DEFAULT 1,
    window_start    TIMESTAMP    NOT NULL DEFAULT NOW(),
    created_at      TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE UNIQUE INDEX idx_arl_identifier_endpoint
    ON api_rate_limits(identifier, endpoint);