-- ================================================================
-- V4__seed_lenders_full.sql
-- 35 real Indian personal loan lenders - banks and NBFCs
-- Replaces the 10-lender seed from V1
-- ================================================================

TRUNCATE TABLE lenders RESTART IDENTITY CASCADE;

INSERT INTO lenders (
    name, lender_type, regulator,
    loan_type, min_credit_score, min_monthly_income,
    max_foir, min_age, max_age, min_employment_years,
    allowed_employment_types, min_loan_amount, max_loan_amount,
    min_interest_rate, max_interest_rate, processing_fee_percent,
    is_active
) VALUES

-- ══════════════════════════════════════════════════════════════════
-- PERSONAL LOANS — PUBLIC SECTOR BANKS
-- ══════════════════════════════════════════════════════════════════
('State Bank of India', 'BANK', 'RBI',
 'PERSONAL_LOAN', 650, 15000,
 0.50, 21, 58, 1,
 'SALARIED,GOVERNMENT,PSU,SELF_EMPLOYED',
 25000, 2000000, 11.45, 14.60, 1.00, TRUE),

('Bank of Baroda', 'BANK', 'RBI',
 'PERSONAL_LOAN', 650, 15000,
 0.50, 21, 58, 1,
 'SALARIED,GOVERNMENT,PSU',
 50000, 1500000, 10.90, 16.25, 2.00, TRUE),

('Punjab National Bank', 'BANK', 'RBI',
 'PERSONAL_LOAN', 650, 15000,
 0.50, 21, 58, 1,
 'SALARIED,GOVERNMENT,PSU',
 50000, 1200000, 10.40, 15.95, 1.80, TRUE),

('Canara Bank', 'BANK', 'RBI',
 'PERSONAL_LOAN', 650, 15000,
 0.50, 21, 58, 1,
 'SALARIED,GOVERNMENT,PSU',
 50000, 1000000, 12.05, 14.00, 1.00, TRUE),

-- ══════════════════════════════════════════════════════════════════
-- PERSONAL LOANS — PRIVATE SECTOR BANKS
-- ══════════════════════════════════════════════════════════════════
('HDFC Bank', 'BANK', 'RBI',
 'PERSONAL_LOAN', 700, 25000,
 0.50, 21, 60, 1,
 'SALARIED,SELF_EMPLOYED,GOVERNMENT',
 50000, 4000000, 10.50, 24.00, 2.50, TRUE),

('ICICI Bank', 'BANK', 'RBI',
 'PERSONAL_LOAN', 700, 25000,
 0.55, 23, 58, 2,
 'SALARIED,SELF_EMPLOYED,GOVERNMENT',
 50000, 5000000, 10.75, 23.00, 2.25, TRUE),

('Axis Bank', 'BANK', 'RBI',
 'PERSONAL_LOAN', 675, 15000,
 0.50, 21, 60, 1,
 'SALARIED,GOVERNMENT',
 50000, 1500000, 10.49, 22.00, 1.50, TRUE),

('Kotak Mahindra Bank', 'BANK', 'RBI',
 'PERSONAL_LOAN', 700, 20000,
 0.50, 21, 58, 1,
 'SALARIED,SELF_EMPLOYED',
 50000, 4000000, 10.99, 24.00, 2.50, TRUE),

('IndusInd Bank', 'BANK', 'RBI',
 'PERSONAL_LOAN', 700, 25000,
 0.55, 21, 60, 2,
 'SALARIED,SELF_EMPLOYED',
 50000, 5000000, 10.49, 26.00, 2.50, TRUE),

('Yes Bank', 'BANK', 'RBI',
 'PERSONAL_LOAN', 700, 25000,
 0.55, 21, 60, 1,
 'SALARIED,SELF_EMPLOYED',
 100000, 4000000, 10.99, 21.00, 2.00, TRUE),

('IDFC First Bank', 'BANK', 'RBI',
 'PERSONAL_LOAN', 700, 20000,
 0.55, 23, 60, 1,
 'SALARIED,SELF_EMPLOYED',
 20000, 4000000, 10.49, 24.00, 2.00, TRUE),

('Federal Bank', 'BANK', 'RBI',
 'PERSONAL_LOAN', 680, 20000,
 0.50, 21, 58, 2,
 'SALARIED,SELF_EMPLOYED,GOVERNMENT',
 50000, 2000000, 11.49, 14.49, 1.00, TRUE),

('RBL Bank', 'BANK', 'RBI',
 'PERSONAL_LOAN', 700, 25000,
 0.55, 23, 60, 2,
 'SALARIED,SELF_EMPLOYED',
 100000, 2000000, 14.00, 23.00, 3.50, TRUE),

('Bandhan Bank', 'BANK', 'RBI',
 'PERSONAL_LOAN', 650, 15000,
 0.55, 21, 60, 1,
 'SALARIED,SELF_EMPLOYED',
 50000, 1000000, 11.55, 18.00, 1.00, TRUE),

-- ══════════════════════════════════════════════════════════════════
-- PERSONAL LOANS — NBFCs
-- ══════════════════════════════════════════════════════════════════
('Bajaj Finance', 'NBFC', 'RBI',
 'PERSONAL_LOAN', 650, 20000,
 0.60, 21, 67, 1,
 'SALARIED,SELF_EMPLOYED',
 30000, 3500000, 11.00, 26.00, 3.93, TRUE),

('Tata Capital', 'NBFC', 'RBI',
 'PERSONAL_LOAN', 680, 20000,
 0.55, 22, 58, 1,
 'SALARIED,SELF_EMPLOYED',
 75000, 3500000, 10.99, 35.00, 2.75, TRUE),

('Fullerton India', 'NBFC', 'RBI',
 'PERSONAL_LOAN', 650, 20000,
 0.55, 21, 60, 2,
 'SALARIED,SELF_EMPLOYED',
 50000, 2500000, 11.99, 36.00, 3.00, TRUE),

('HDB Financial Services', 'NBFC', 'RBI',
 'PERSONAL_LOAN', 650, 15000,
 0.60, 21, 60, 1,
 'SALARIED,SELF_EMPLOYED',
 50000, 2000000, 11.00, 36.00, 2.00, TRUE),

('Muthoot Finance', 'NBFC', 'RBI',
 'PERSONAL_LOAN', 600, 15000,
 0.65, 21, 65, 0,
 'SALARIED,SELF_EMPLOYED,RETIRED',
 50000, 1500000, 14.00, 24.00, 2.00, TRUE),

('Shriram Finance', 'NBFC', 'RBI',
 'PERSONAL_LOAN', 600, 15000,
 0.65, 21, 65, 0,
 'SALARIED,SELF_EMPLOYED',
 50000, 1000000, 14.99, 28.00, 2.00, TRUE),

('L&T Finance', 'NBFC', 'RBI',
 'PERSONAL_LOAN', 680, 20000,
 0.55, 21, 58, 1,
 'SALARIED,SELF_EMPLOYED',
 100000, 2000000, 12.00, 24.00, 3.00, TRUE),

-- ══════════════════════════════════════════════════════════════════
-- PERSONAL LOANS — DIGITAL/FINTECH LENDERS (RBI registered NBFCs)
-- ══════════════════════════════════════════════════════════════════
('MoneyView', 'NBFC', 'RBI',
 'PERSONAL_LOAN', 600, 13500,
 0.60, 21, 57, 0,
 'SALARIED,SELF_EMPLOYED',
 10000, 500000, 15.96, 39.99, 2.00, TRUE),

('Navi', 'NBFC', 'RBI',
 'PERSONAL_LOAN', 650, 15000,
 0.55, 21, 55, 0,
 'SALARIED,SELF_EMPLOYED',
 10000, 2000000, 9.90, 45.00, 0.00, TRUE),

('PaySense (LazyPay)', 'NBFC', 'RBI',
 'PERSONAL_LOAN', 600, 18000,
 0.60, 21, 60, 0,
 'SALARIED',
 5000, 500000, 16.00, 36.00, 2.50, TRUE),

('KreditBee', 'NBFC', 'RBI',
 'PERSONAL_LOAN', 600, 10000,
 0.65, 21, 45, 0,
 'SALARIED,SELF_EMPLOYED',
 1000, 300000, 17.00, 29.95, 6.00, TRUE),

('CASHe', 'NBFC', 'RBI',
 'PERSONAL_LOAN', 600, 15000,
 0.60, 23, 55, 0,
 'SALARIED',
 10000, 400000, 27.00, 33.00, 3.00, TRUE),

('Lendingkart', 'NBFC', 'RBI',
 'PERSONAL_LOAN', 650, 20000,
 0.55, 21, 65, 0,
 'SALARIED,SELF_EMPLOYED',
 50000, 2000000, 12.00, 27.00, 2.00, TRUE),

('Loantap', 'NBFC', 'RBI',
 'PERSONAL_LOAN', 680, 25000,
 0.55, 23, 58, 1,
 'SALARIED',
 50000, 1000000, 15.00, 24.00, 2.00, TRUE),

('Faircent', 'NBFC', 'RBI',
 'PERSONAL_LOAN', 600, 18000,
 0.60, 25, 55, 0,
 'SALARIED,SELF_EMPLOYED',
 30000, 1000000, 12.00, 28.00, 8.00, TRUE),

-- ══════════════════════════════════════════════════════════════════
-- HOME LOANS
-- ══════════════════════════════════════════════════════════════════
('HDFC Bank', 'BANK', 'RBI',
 'HOME_LOAN', 700, 30000,
 0.50, 21, 65, 2,
 'SALARIED,SELF_EMPLOYED,GOVERNMENT',
 300000, 100000000, 8.50, 10.00, 0.50, TRUE),

('SBI', 'BANK', 'RBI',
 'HOME_LOAN', 650, 25000,
 0.50, 18, 70, 2,
 'SALARIED,GOVERNMENT,PSU,SELF_EMPLOYED',
 300000, 150000000, 8.40, 9.65, 0.35, TRUE),

('ICICI Bank', 'BANK', 'RBI',
 'HOME_LOAN', 700, 30000,
 0.55, 21, 65, 2,
 'SALARIED,SELF_EMPLOYED,GOVERNMENT',
 300000, 100000000, 8.75, 9.80, 0.50, TRUE),

('LIC Housing Finance', 'NBFC', 'NHB',
 'HOME_LOAN', 650, 25000,
 0.50, 21, 65, 2,
 'SALARIED,SELF_EMPLOYED,GOVERNMENT,PSU',
 200000, 150000000, 8.50, 10.75, 0.25, TRUE),

-- ══════════════════════════════════════════════════════════════════
-- CAR LOANS
-- ══════════════════════════════════════════════════════════════════
('HDFC Bank', 'BANK', 'RBI',
 'CAR_LOAN', 680, 20000,
 0.50, 21, 60, 1,
 'SALARIED,SELF_EMPLOYED,GOVERNMENT',
 100000, 10000000, 8.75, 13.00, 0.50, TRUE),

('Bajaj Finance', 'NBFC', 'RBI',
 'CONSUMER_DURABLE_LOAN', 600, 15000,
 0.60, 21, 65, 0,
 'SALARIED,SELF_EMPLOYED',
 5000, 200000, 0.00, 18.00, 2.00, TRUE);