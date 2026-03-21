-- ================================================================
-- V17__add_dti_columns.sql
-- Adds DTI (Debt-to-Income Ratio) columns to financial_profiles
--
-- DTI = (existingEMI + monthlyExpenses) / income × 100
-- Broader than FOIR — includes ALL financial obligations
--
-- FOIR only counts EMIs (what banks check for loan eligibility)
-- DTI counts everything: rent + food + utilities + EMIs
-- A person can have FOIR=0% but DTI=90% (high rent, no loans)
-- They look eligible for loans but are actually financially stretched
-- ================================================================

ALTER TABLE financial_profiles
    ADD COLUMN IF NOT EXISTS dti       NUMERIC(5,2),
    ADD COLUMN IF NOT EXISTS dti_range VARCHAR(20);

-- Backfill DTI for existing profiles where we have enough data
-- DTI = (existing_emi_total + monthly_expenses) / monthly_income * 100
UPDATE financial_profiles
SET
    dti = CASE
        WHEN monthly_income > 0
        THEN ROUND(
            ((COALESCE(existing_emi_total, 0) + COALESCE(monthly_expenses, 0))
             / monthly_income * 100)::NUMERIC, 2)
        ELSE 0
    END,
    dti_range = CASE
        WHEN monthly_income <= 0 THEN 'UNKNOWN'
        WHEN ((COALESCE(existing_emi_total, 0) + COALESCE(monthly_expenses, 0))
               / monthly_income * 100) < 30  THEN 'LOW'
        WHEN ((COALESCE(existing_emi_total, 0) + COALESCE(monthly_expenses, 0))
               / monthly_income * 100) < 43  THEN 'MODERATE'
        WHEN ((COALESCE(existing_emi_total, 0) + COALESCE(monthly_expenses, 0))
               / monthly_income * 100) < 50  THEN 'HIGH'
        ELSE 'CRITICAL'
    END
WHERE monthly_income IS NOT NULL;