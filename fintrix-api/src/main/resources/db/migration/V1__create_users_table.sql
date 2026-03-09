-- ================================================================
-- V1__create_users_table.sql
-- ================================================================
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE users (
    id                  VARCHAR(36)  PRIMARY KEY DEFAULT gen_random_uuid()::TEXT,
    google_id           VARCHAR(255) NOT NULL UNIQUE,
    email               VARCHAR(255) NOT NULL UNIQUE,
    full_name           VARCHAR(150) NOT NULL,
    profile_picture_url VARCHAR(500),
    phone_number        VARCHAR(15),
    city                VARCHAR(100),
    state               VARCHAR(100),
    age                 INTEGER,
    role                VARCHAR(20)  NOT NULL DEFAULT 'USER',
    is_active           BOOLEAN      NOT NULL DEFAULT TRUE,
    is_profile_complete BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at          TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_users_email     ON users(email);
CREATE INDEX idx_users_google_id ON users(google_id);

-- ================================================================
-- V2__create_financial_profiles_table.sql
-- ================================================================
CREATE TABLE financial_profiles (
    id                          VARCHAR(36)    PRIMARY KEY DEFAULT gen_random_uuid()::TEXT,
    user_id                     VARCHAR(36)    NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    employment_type             VARCHAR(30)    NOT NULL,
    employer_name               VARCHAR(200),
    years_of_experience         INTEGER,
    monthly_income              NUMERIC(12,2)  NOT NULL,
    monthly_expenses            NUMERIC(12,2),
    monthly_savings             NUMERIC(12,2),
    existing_emi_total          NUMERIC(12,2)  NOT NULL DEFAULT 0,
    number_of_active_loans      INTEGER        NOT NULL DEFAULT 0,
    credit_score                INTEGER,
    credit_score_range          VARCHAR(20),
    number_of_credit_cards      INTEGER        NOT NULL DEFAULT 0,
    total_credit_limit          NUMERIC(12,2)  NOT NULL DEFAULT 0,
    current_credit_utilization  NUMERIC(5,2)   NOT NULL DEFAULT 0,
    preferred_reward_type       VARCHAR(50),
    top_spending_category       VARCHAR(100),
    foir                        NUMERIC(5,2),
    financial_health_score      INTEGER,
    risk_level                  VARCHAR(20),
    created_at                  TIMESTAMP      NOT NULL DEFAULT NOW(),
    updated_at                  TIMESTAMP      NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_fp_user_id ON financial_profiles(user_id);

-- ================================================================
-- V3__create_loans_lenders_table.sql
-- ================================================================
CREATE TABLE lenders (
    id                       VARCHAR(36)   PRIMARY KEY DEFAULT gen_random_uuid()::TEXT,
    name                     VARCHAR(150)  NOT NULL,
    logo_url                 VARCHAR(300),
    loan_type                VARCHAR(50)   NOT NULL,
    min_credit_score         INTEGER       NOT NULL,
    min_monthly_income       NUMERIC(12,2) NOT NULL,
    max_foir                 NUMERIC(5,2)  NOT NULL,
    min_age                  INTEGER       NOT NULL,
    max_age                  INTEGER       NOT NULL,
    min_employment_years     INTEGER,
    allowed_employment_types VARCHAR(200),
    min_loan_amount          NUMERIC(12,2),
    max_loan_amount          NUMERIC(12,2),
    min_interest_rate        NUMERIC(5,2),
    max_interest_rate        NUMERIC(5,2),
    processing_fee_percent   NUMERIC(5,2),
    is_active                BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at               TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at               TIMESTAMP     NOT NULL DEFAULT NOW()
);

CREATE TABLE loan_applications (
    id              VARCHAR(36)   PRIMARY KEY DEFAULT gen_random_uuid()::TEXT,
    user_id         VARCHAR(36)   NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    lender_id       VARCHAR(36)   REFERENCES lenders(id),
    loan_type       VARCHAR(50)   NOT NULL,
    requested_amount NUMERIC(12,2) NOT NULL,
    tenure_months   INTEGER       NOT NULL,
    purpose         VARCHAR(200),
    approval_probability NUMERIC(5,2),
    estimated_emi   NUMERIC(12,2),
    status          VARCHAR(30)   NOT NULL DEFAULT 'CHECKED',
    created_at      TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP     NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_loan_app_user_id ON loan_applications(user_id);

-- ================================================================
-- V4__create_credit_cards_table.sql
-- ================================================================
CREATE TABLE credit_cards (
    id                         VARCHAR(36)   PRIMARY KEY DEFAULT gen_random_uuid()::TEXT,
    bank_name                  VARCHAR(150)  NOT NULL,
    card_name                  VARCHAR(200)  NOT NULL,
    logo_url                   VARCHAR(300),
    card_category              VARCHAR(50)   NOT NULL,
    reward_type                VARCHAR(50)   NOT NULL,
    min_credit_score           INTEGER       NOT NULL,
    min_monthly_income         NUMERIC(12,2) NOT NULL,
    min_age                    INTEGER       NOT NULL,
    max_age                    INTEGER       NOT NULL,
    allowed_employment_types   VARCHAR(200),
    joining_fee                NUMERIC(10,2) NOT NULL DEFAULT 0,
    annual_fee                 NUMERIC(10,2) NOT NULL DEFAULT 0,
    annual_fee_waiver_condition VARCHAR(300),
    reward_rate                VARCHAR(200),
    welcome_benefit            VARCHAR(300),
    key_benefits               TEXT,
    is_active                  BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at                 TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at                 TIMESTAMP     NOT NULL DEFAULT NOW()
);

-- ================================================================
-- V5__create_financial_health_scores_table.sql
-- ================================================================
CREATE TABLE financial_health_scores (
    id                       VARCHAR(36)  PRIMARY KEY DEFAULT gen_random_uuid()::TEXT,
    user_id                  VARCHAR(36)  NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    overall_score            INTEGER      NOT NULL,
    savings_rate_score       INTEGER,
    debt_burden_score        INTEGER,
    credit_utilization_score INTEGER,
    credit_score_component   INTEGER,
    foir_at_scoring          NUMERIC(5,2),
    utilization_at_scoring   NUMERIC(5,2),
    savings_rate_at_scoring  NUMERIC(5,2),
    risk_level               VARCHAR(20)  NOT NULL,
    improvement_tips         TEXT,
    risk_warnings            TEXT,
    scored_on                DATE         NOT NULL,
    is_latest                BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at               TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at               TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_fhs_user_id   ON financial_health_scores(user_id);
CREATE INDEX idx_fhs_scored_on ON financial_health_scores(scored_on);
CREATE INDEX idx_fhs_is_latest ON financial_health_scores(user_id, is_latest);

-- ================================================================
-- V6__create_fraud_alerts_table.sql
-- ================================================================
CREATE TABLE fraud_alerts (
    id              VARCHAR(36)  PRIMARY KEY DEFAULT gen_random_uuid()::TEXT,
    user_id         VARCHAR(36)  REFERENCES users(id) ON DELETE SET NULL,
    entity_name     VARCHAR(255) NOT NULL,
    entity_type     VARCHAR(50)  NOT NULL,   -- INVESTMENT_SCHEME, LENDER, BROKER
    alert_severity  VARCHAR(20)  NOT NULL,   -- LOW, MEDIUM, HIGH, CRITICAL
    reason          TEXT         NOT NULL,
    is_sebi_listed  BOOLEAN,
    is_rbi_listed   BOOLEAN,
    checked_at      TIMESTAMP    NOT NULL DEFAULT NOW(),
    created_at      TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- ================================================================
-- V7__seed_lenders_data.sql
-- ================================================================
INSERT INTO lenders (name, loan_type, min_credit_score, min_monthly_income, max_foir, min_age, max_age, min_employment_years, allowed_employment_types, min_loan_amount, max_loan_amount, min_interest_rate, max_interest_rate, processing_fee_percent) VALUES
('HDFC Bank',        'PERSONAL_LOAN', 700, 25000, 0.50, 21, 60, 1, 'SALARIED,SELF_EMPLOYED,GOVERNMENT', 50000,   4000000, 10.50, 24.00, 2.50),
('ICICI Bank',       'PERSONAL_LOAN', 700, 25000, 0.55, 23, 58, 2, 'SALARIED,SELF_EMPLOYED,GOVERNMENT', 50000,   5000000, 10.75, 23.00, 2.25),
('Axis Bank',        'PERSONAL_LOAN', 675, 15000, 0.50, 21, 60, 1, 'SALARIED,GOVERNMENT',               50000,   1500000, 10.49, 22.00, 1.50),
('Bajaj Finance',    'PERSONAL_LOAN', 650, 20000, 0.60, 21, 67, 1, 'SALARIED,SELF_EMPLOYED',             30000,   3500000, 11.00, 26.00, 3.93),
('SBI',              'PERSONAL_LOAN', 650, 15000, 0.50, 21, 58, 1, 'SALARIED,GOVERNMENT,PSU',            25000,   2000000, 11.45, 14.60, 1.00),
('HDFC Bank',        'HOME_LOAN',     700, 30000, 0.50, 21, 65, 2, 'SALARIED,SELF_EMPLOYED,GOVERNMENT', 300000, 100000000, 8.50, 10.00, 0.50),
('ICICI Bank',       'HOME_LOAN',     700, 30000, 0.55, 21, 65, 2, 'SALARIED,SELF_EMPLOYED,GOVERNMENT', 300000, 100000000, 8.75,  9.80, 0.50),
('SBI',              'HOME_LOAN',     650, 25000, 0.50, 18, 70, 2, 'SALARIED,GOVERNMENT,PSU,SELF_EMPLOYED', 300000, 150000000, 8.40, 9.65, 0.35),
('HDFC Bank',        'CAR_LOAN',      680, 20000, 0.50, 21, 60, 1, 'SALARIED,SELF_EMPLOYED,GOVERNMENT',  100000, 10000000, 8.75, 13.00, 0.50),
('Bajaj Finance',    'CONSUMER_DURABLE_LOAN', 600, 15000, 0.60, 21, 65, 0, 'SALARIED,SELF_EMPLOYED', 5000, 200000, 0.00, 18.00, 2.00);

-- ================================================================
-- V8__seed_credit_cards_data.sql
-- ================================================================
INSERT INTO credit_cards (bank_name, card_name, card_category, reward_type, min_credit_score, min_monthly_income, min_age, max_age, allowed_employment_types, joining_fee, annual_fee, annual_fee_waiver_condition, reward_rate, welcome_benefit, key_benefits) VALUES
('HDFC Bank',  'Millennia Credit Card',      'CASHBACK',   'CASHBACK',        700, 25000, 21, 60, 'SALARIED,SELF_EMPLOYED', 1000, 1000, 'Spend ₹1L in a year',    '5% cashback on Amazon, Flipkart; 1% others', '₹1000 welcome voucher', '["Lounge access 2/quarter","Zomato/Swiggy 5% back"]'),
('ICICI Bank', 'Amazon Pay ICICI Card',      'CASHBACK',   'CASHBACK',        700, 25000, 21, 58, 'SALARIED,SELF_EMPLOYED', 0,    0,    'Lifetime free',           '5% on Amazon Prime; 2% others',              'Instant approval online', '["No annual fee","5% on Amazon always"]'),
('Axis Bank',  'Flipkart Axis Bank Card',    'CASHBACK',   'CASHBACK',        700, 15000, 21, 60, 'SALARIED,SELF_EMPLOYED', 500,  500,  'Spend ₹2L in a year',    '5% on Flipkart; 4% on Myntra; 1.5% others',  '500 Flipkart voucher',  '["No cost EMI on Flipkart","Cleartrip cashback"]'),
('SBI',        'SimplyCLICK SBI Card',       'CASHBACK',   'REWARD_POINTS',   700, 20000, 21, 65, 'SALARIED,GOVERNMENT,PSU', 499, 499, 'Spend ₹1L in a year',   '10X points on Amazon/BookMyShow',             'Amazon.in voucher ₹500','["Movie ticket discounts","Fuel surcharge waiver"]'),
('HDFC Bank',  'Regalia Credit Card',        'TRAVEL',     'REWARD_POINTS',   750, 100000,21, 60, 'SALARIED,SELF_EMPLOYED', 2500,2500,'Spend ₹3L in a year',    '4 reward points per ₹150 spent',              '2500 reward points',   '["6 intl lounge access","Travel insurance ₹1Cr"]'),
('Axis Bank',  'Magnus Credit Card',         'PREMIUM',    'AIRLINE_MILES',   780, 150000,18, 70, 'SALARIED,SELF_EMPLOYED', 12500,12500,'Spend ₹15L in a year',  '12 EDGE Miles per ₹200 spent',                'Magnus membership kit','["Unlimited lounge","TATA CLiQ voucher ₹25k"]'),
('SBI',        'BPCL Octane SBI Card',       'FUEL',       'FUEL_SURCHARGE_WAIVER', 680, 20000, 21, 65, 'SALARIED,GOVERNMENT', 1499,1499,'Spend ₹50k in a year', '7.25% value back on BPCL fuel',             '6000 bonus reward pts', '["Fuel savings up to ₹3750/yr","Lounge access"]'),
('HDFC Bank',  'MoneyBack+ Credit Card',     'ENTRY_LEVEL','CASHBACK',        650, 15000, 21, 60, 'SALARIED',              500,  500, 'Spend ₹50k in a year',   '2 CashPoints per ₹150',                      '₹500 gift voucher',    '["Good for beginners","Low income eligibility"]'),
('Kotak',      'Kotak 811 #DreamDifferent',  'ENTRY_LEVEL','CASHBACK',        600, 0,     18, 75, 'SALARIED,SELF_EMPLOYED,STUDENT', 0, 0, 'Lifetime free',        '2% cashback on online; 1% offline',           'Instant digital card', '["Lifetime free","Good for students","Low threshold"]'),
('ICICI Bank', 'Coral Credit Card',          'ENTRY_LEVEL','REWARD_POINTS',   680, 20000, 23, 58, 'SALARIED,SELF_EMPLOYED', 500, 500, 'Spend ₹1.5L in a year', '2 PAYBACK points per ₹100',                   'Welcome vouchers',     '["Movie offers","Fuel surcharge waiver","Easy upgrade path"]');