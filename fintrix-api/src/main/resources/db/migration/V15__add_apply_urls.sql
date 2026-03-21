-- ================================================================
-- V15__add_apply_urls.sql
-- Adds apply_url column to lenders and credit_cards
-- Updates all existing records with real bank application URLs
-- This is what the "Apply Now" button links to in the frontend
-- ================================================================

-- ── Add apply_url column ──────────────────────────────────────────
ALTER TABLE lenders      ADD COLUMN IF NOT EXISTS apply_url VARCHAR(500);
ALTER TABLE credit_cards ADD COLUMN IF NOT EXISTS apply_url VARCHAR(500);

-- ================================================================
-- LENDER APPLY URLs
-- These are the direct personal loan / home loan application pages
-- ================================================================

-- ── Public sector banks ───────────────────────────────────────────
UPDATE lenders SET apply_url = 'https://www.onlinesbi.sbi/personal/personal-loans.html'
    WHERE name = 'State Bank of India' AND loan_type = 'PERSONAL_LOAN';

UPDATE lenders SET apply_url = 'https://www.sbi.co.in/web/home-loan'
    WHERE name = 'SBI' AND loan_type = 'HOME_LOAN';

UPDATE lenders SET apply_url = 'https://www.bankofbaroda.in/personal-banking/loans/personal-loan'
    WHERE name = 'Bank of Baroda' AND loan_type = 'PERSONAL_LOAN';

UPDATE lenders SET apply_url = 'https://www.pnbindia.in/personal-loan.html'
    WHERE name = 'Punjab National Bank' AND loan_type = 'PERSONAL_LOAN';

UPDATE lenders SET apply_url = 'https://www.canarabank.com/personal-banking/loans/personal-loan'
    WHERE name = 'Canara Bank' AND loan_type = 'PERSONAL_LOAN';

-- ── HDFC Bank ─────────────────────────────────────────────────────
UPDATE lenders SET apply_url = 'https://www.hdfcbank.com/personal/borrow/popular-loans/personal-loan'
    WHERE name = 'HDFC Bank' AND loan_type = 'PERSONAL_LOAN';

UPDATE lenders SET apply_url = 'https://www.hdfcbank.com/personal/borrow/popular-loans/home-loan'
    WHERE name = 'HDFC Bank' AND loan_type = 'HOME_LOAN';

UPDATE lenders SET apply_url = 'https://www.hdfcbank.com/personal/borrow/popular-loans/car-loan'
    WHERE name = 'HDFC Bank' AND loan_type = 'CAR_LOAN';

-- ── ICICI Bank ────────────────────────────────────────────────────
UPDATE lenders SET apply_url = 'https://www.icicibank.com/personal-banking/loans/personal-loan'
    WHERE name = 'ICICI Bank' AND loan_type = 'PERSONAL_LOAN';

UPDATE lenders SET apply_url = 'https://www.icicibank.com/personal-banking/loans/home-loan'
    WHERE name = 'ICICI Bank' AND loan_type = 'HOME_LOAN';

-- ── Axis Bank ─────────────────────────────────────────────────────
UPDATE lenders SET apply_url = 'https://www.axisbank.com/retail/loans/personal-loan'
    WHERE name = 'Axis Bank' AND loan_type = 'PERSONAL_LOAN';

-- ── Kotak Mahindra Bank ───────────────────────────────────────────
UPDATE lenders SET apply_url = 'https://www.kotak.com/en/personal-banking/loans/personal-loan.html'
    WHERE name = 'Kotak Mahindra Bank' AND loan_type = 'PERSONAL_LOAN';

-- ── IndusInd Bank ─────────────────────────────────────────────────
UPDATE lenders SET apply_url = 'https://www.indusind.com/in/en/personal/loans/personal-loan.html'
    WHERE name = 'IndusInd Bank' AND loan_type = 'PERSONAL_LOAN';

-- ── Yes Bank ──────────────────────────────────────────────────────
UPDATE lenders SET apply_url = 'https://www.yesbank.in/personal-banking/yes-individual/loans/personal-loan'
    WHERE name = 'Yes Bank' AND loan_type = 'PERSONAL_LOAN';

-- ── IDFC First Bank ───────────────────────────────────────────────
UPDATE lenders SET apply_url = 'https://www.idfcfirstbank.com/personal-banking/loans/personal-loan'
    WHERE name = 'IDFC First Bank' AND loan_type = 'PERSONAL_LOAN';

-- ── Federal Bank ──────────────────────────────────────────────────
UPDATE lenders SET apply_url = 'https://www.federalbank.co.in/personal-loans'
    WHERE name = 'Federal Bank' AND loan_type = 'PERSONAL_LOAN';

-- ── RBL Bank ──────────────────────────────────────────────────────
UPDATE lenders SET apply_url = 'https://www.rblbank.com/loans/personal-loan'
    WHERE name = 'RBL Bank' AND loan_type = 'PERSONAL_LOAN';

-- ── Bandhan Bank ─────────────────────────────────────────────────
UPDATE lenders SET apply_url = 'https://www.bandhanbank.com/personal-banking/loans/personal-loan'
    WHERE name = 'Bandhan Bank' AND loan_type = 'PERSONAL_LOAN';

-- ── LIC Housing Finance ───────────────────────────────────────────
UPDATE lenders SET apply_url = 'https://www.lichousingfin.com/apply-for-loan'
    WHERE name = 'LIC Housing Finance' AND loan_type = 'HOME_LOAN';

-- ── NBFCs ─────────────────────────────────────────────────────────
UPDATE lenders SET apply_url = 'https://www.bajajfinserv.in/personal-loan'
    WHERE name = 'Bajaj Finance' AND loan_type = 'PERSONAL_LOAN';

UPDATE lenders SET apply_url = 'https://www.bajajfinserv.in/consumer-durable-loans'
    WHERE name = 'Bajaj Finance' AND loan_type = 'CONSUMER_DURABLE_LOAN';

UPDATE lenders SET apply_url = 'https://www.tatacapital.com/personal-loan.html'
    WHERE name = 'Tata Capital' AND loan_type = 'PERSONAL_LOAN';

UPDATE lenders SET apply_url = 'https://www.fullertonindia.com/personal-loan.aspx'
    WHERE name = 'Fullerton India' AND loan_type = 'PERSONAL_LOAN';

UPDATE lenders SET apply_url = 'https://www.hdbfs.com/personal-loan'
    WHERE name = 'HDB Financial Services' AND loan_type = 'PERSONAL_LOAN';

UPDATE lenders SET apply_url = 'https://www.muthootfin.com/personal-loan'
    WHERE name = 'Muthoot Finance' AND loan_type = 'PERSONAL_LOAN';

UPDATE lenders SET apply_url = 'https://www.shriramfinance.in/personal-loan'
    WHERE name = 'Shriram Finance' AND loan_type = 'PERSONAL_LOAN';

UPDATE lenders SET apply_url = 'https://www.ltfs.com/personal-loan.html'
    WHERE name = 'L&T Finance' AND loan_type = 'PERSONAL_LOAN';

-- ── Digital / Fintech lenders ─────────────────────────────────────
UPDATE lenders SET apply_url = 'https://moneyview.in/loan'
    WHERE name = 'MoneyView';

UPDATE lenders SET apply_url = 'https://navi.com/loans/personal-loan'
    WHERE name = 'Navi';

UPDATE lenders SET apply_url = 'https://paysense.in/personal-loan'
    WHERE name = 'PaySense (LazyPay)';

UPDATE lenders SET apply_url = 'https://www.kreditbee.in/personal-loan'
    WHERE name = 'KreditBee';

UPDATE lenders SET apply_url = 'https://cashe.co.in'
    WHERE name = 'CASHe';

UPDATE lenders SET apply_url = 'https://www.lendingkart.com/business-loan'
    WHERE name = 'Lendingkart';

UPDATE lenders SET apply_url = 'https://loantap.in/personal-loan'
    WHERE name = 'Loantap';

UPDATE lenders SET apply_url = 'https://www.faircent.com/personal-loan'
    WHERE name = 'Faircent';

-- ================================================================
-- CREDIT CARD APPLY URLs
-- Direct links to bank's credit card application pages
-- ================================================================

-- ── HDFC Bank cards ───────────────────────────────────────────────
UPDATE credit_cards SET apply_url = 'https://www.hdfcbank.com/personal/pay/cards/credit-cards/millennia-credit-card'
    WHERE bank_name = 'HDFC Bank' AND card_name LIKE '%Millennia%';

UPDATE credit_cards SET apply_url = 'https://www.hdfcbank.com/personal/pay/cards/credit-cards/regalia-credit-card'
    WHERE bank_name = 'HDFC Bank' AND card_name LIKE '%Regalia%';

UPDATE credit_cards SET apply_url = 'https://www.hdfcbank.com/personal/pay/cards/credit-cards/infinia-credit-card'
    WHERE bank_name = 'HDFC Bank' AND card_name LIKE '%Infinia%';

UPDATE credit_cards SET apply_url = 'https://www.hdfcbank.com/personal/pay/cards/credit-cards/moneyback-plus-credit-card'
    WHERE bank_name = 'HDFC Bank' AND card_name LIKE '%MoneyBack%';

UPDATE credit_cards SET apply_url = 'https://www.hdfcbank.com/personal/pay/cards/credit-cards/tata-neu-infinity-hdfc-bank-credit-card'
    WHERE bank_name = 'HDFC Bank' AND card_name LIKE '%Tata Neu%';

UPDATE credit_cards SET apply_url = 'https://www.hdfcbank.com/personal/pay/cards/credit-cards/diners-club-privilege'
    WHERE bank_name = 'HDFC Bank' AND card_name LIKE '%Diners%';

UPDATE credit_cards SET apply_url = 'https://www.hdfcbank.com/personal/pay/cards/credit-cards/pixel-play'
    WHERE bank_name = 'HDFC Bank' AND card_name LIKE '%Pixel%';

-- ── ICICI Bank cards ──────────────────────────────────────────────
UPDATE credit_cards SET apply_url = 'https://www.icicibank.com/card/amazon-pay-credit-card'
    WHERE bank_name = 'ICICI Bank' AND card_name LIKE '%Amazon Pay%';

UPDATE credit_cards SET apply_url = 'https://www.icicibank.com/card/coral-credit-card'
    WHERE bank_name = 'ICICI Bank' AND card_name LIKE '%Coral%';

UPDATE credit_cards SET apply_url = 'https://www.icicibank.com/card/sapphiro-credit-card'
    WHERE bank_name = 'ICICI Bank' AND card_name LIKE '%Sapphiro%';

UPDATE credit_cards SET apply_url = 'https://www.icicibank.com/card/hpcl-super-saver-credit-card'
    WHERE bank_name = 'ICICI Bank' AND card_name LIKE '%HPCL%';

UPDATE credit_cards SET apply_url = 'https://www.icicibank.com/card/mmt-icici-bank-platinum'
    WHERE bank_name = 'ICICI Bank' AND card_name LIKE '%MMT%';

UPDATE credit_cards SET apply_url = 'https://www.icicibank.com/card/emeralde-private-metal'
    WHERE bank_name = 'ICICI Bank' AND card_name LIKE '%Emeralde%';

UPDATE credit_cards SET apply_url = 'https://www.icicibank.com/card/rubyx-credit-card'
    WHERE bank_name = 'ICICI Bank' AND card_name LIKE '%Rubyx%';

-- ── Axis Bank cards ───────────────────────────────────────────────
UPDATE credit_cards SET apply_url = 'https://www.axisbank.com/retail/cards/credit-card/flipkart-axis-bank-credit-card'
    WHERE bank_name = 'Axis Bank' AND card_name LIKE '%Flipkart%';

UPDATE credit_cards SET apply_url = 'https://www.axisbank.com/retail/cards/credit-card/magnus-credit-card'
    WHERE bank_name = 'Axis Bank' AND card_name LIKE '%Magnus%';

UPDATE credit_cards SET apply_url = 'https://www.axisbank.com/retail/cards/credit-card/ace-credit-card'
    WHERE bank_name = 'Axis Bank' AND card_name LIKE '%Ace%';

UPDATE credit_cards SET apply_url = 'https://www.axisbank.com/retail/cards/credit-card/vistara-credit-card'
    WHERE bank_name = 'Axis Bank' AND card_name LIKE '%Vistara%';

UPDATE credit_cards SET apply_url = 'https://www.axisbank.com/retail/cards/credit-card/select-credit-card'
    WHERE bank_name = 'Axis Bank' AND card_name LIKE '%Select%';

UPDATE credit_cards SET apply_url = 'https://www.axisbank.com/retail/cards/credit-card/my-zone-credit-card'
    WHERE bank_name = 'Axis Bank' AND card_name LIKE '%My Zone%';

-- ── SBI Card ──────────────────────────────────────────────────────
UPDATE credit_cards SET apply_url = 'https://www.sbicard.com/en/personal/credit-cards/rewards/simplyclick-sbi-card.page'
    WHERE bank_name = 'SBI Card' AND card_name LIKE '%SimplyCLICK%';

UPDATE credit_cards SET apply_url = 'https://www.sbicard.com/en/personal/credit-cards/fuel/bpcl-octane-sbi-card.page'
    WHERE bank_name = 'SBI Card' AND card_name LIKE '%BPCL%';

UPDATE credit_cards SET apply_url = 'https://www.sbicard.com/en/personal/credit-cards/travel-and-lifestyle/sbi-card-prime.page'
    WHERE bank_name = 'SBI Card' AND card_name LIKE '%PRIME%';

UPDATE credit_cards SET apply_url = 'https://www.sbicard.com/en/personal/credit-cards/travel-and-lifestyle/sbi-card-elite.page'
    WHERE bank_name = 'SBI Card' AND card_name LIKE '%ELITE%';

UPDATE credit_cards SET apply_url = 'https://www.sbicard.com/en/personal/credit-cards/cashback/cashback-sbi-card.page'
    WHERE bank_name = 'SBI Card' AND card_name LIKE '%Cashback%';

UPDATE credit_cards SET apply_url = 'https://www.sbicard.com/en/personal/credit-cards/rewards/simplysave-sbi-card.page'
    WHERE bank_name = 'SBI Card' AND card_name LIKE '%SimplySAVE%';

-- ── Kotak Bank cards ──────────────────────────────────────────────
UPDATE credit_cards SET apply_url = 'https://www.kotak.com/en/personal-banking/cards/credit-cards/811-dream-different-credit-card.html'
    WHERE bank_name = 'Kotak Bank' AND card_name LIKE '%811%';

UPDATE credit_cards SET apply_url = 'https://www.kotak.com/en/personal-banking/cards/credit-cards/league-platinum-credit-card.html'
    WHERE bank_name = 'Kotak Bank' AND card_name LIKE '%League%';

UPDATE credit_cards SET apply_url = 'https://www.kotak.com/en/personal-banking/cards/credit-cards/zen-signature-credit-card.html'
    WHERE bank_name = 'Kotak Bank' AND card_name LIKE '%Zen%';

UPDATE credit_cards SET apply_url = 'https://www.kotak.com/en/personal-banking/cards/credit-cards/royale-signature-credit-card.html'
    WHERE bank_name = 'Kotak Bank' AND card_name LIKE '%Royale%';

-- ── IndusInd Bank cards ───────────────────────────────────────────
UPDATE credit_cards SET apply_url = 'https://www.indusind.com/in/en/personal/cards/credit-card/indulge-credit-card.html'
    WHERE bank_name = 'IndusInd Bank' AND card_name LIKE '%Indulge%';

UPDATE credit_cards SET apply_url = 'https://www.indusind.com/in/en/personal/cards/credit-card/eazydiner-indusind-bank-platinum-credit-card.html'
    WHERE bank_name = 'IndusInd Bank' AND card_name LIKE '%EazyDiner%';

UPDATE credit_cards SET apply_url = 'https://www.indusind.com/in/en/personal/cards/credit-card/legend-credit-card.html'
    WHERE bank_name = 'IndusInd Bank' AND card_name LIKE '%Legend%';

UPDATE credit_cards SET apply_url = 'https://www.indusind.com/in/en/personal/cards/credit-card/duo-card.html'
    WHERE bank_name = 'IndusInd Bank' AND card_name LIKE '%Duo%';

-- ── Yes Bank cards ────────────────────────────────────────────────
UPDATE credit_cards SET apply_url = 'https://www.yesbank.in/personal-banking/yes-individual/cards/credit-cards/yes-preferred-credit-card'
    WHERE bank_name = 'Yes Bank' AND card_name LIKE '%Preferred%';

UPDATE credit_cards SET apply_url = 'https://www.yesbank.in/personal-banking/yes-individual/cards/credit-cards/yes-prosperity-reward-plus-credit-card'
    WHERE bank_name = 'Yes Bank' AND card_name LIKE '%Prosperity%';

UPDATE credit_cards SET apply_url = 'https://www.yesbank.in/personal-banking/yes-individual/cards/credit-cards/yes-first-exclusive-credit-card'
    WHERE bank_name = 'Yes Bank' AND card_name LIKE '%First Exclusive%';

-- ── Standard Chartered cards ──────────────────────────────────────
UPDATE credit_cards SET apply_url = 'https://www.sc.com/in/credit-cards/smart-credit-card/'
    WHERE bank_name = 'Standard Chartered' AND card_name LIKE '%Smart%';

UPDATE credit_cards SET apply_url = 'https://www.sc.com/in/credit-cards/rewards-credit-card/'
    WHERE bank_name = 'Standard Chartered' AND card_name LIKE '%Rewards%';

UPDATE credit_cards SET apply_url = 'https://www.sc.com/in/credit-cards/ultimate-credit-card/'
    WHERE bank_name = 'Standard Chartered' AND card_name LIKE '%Ultimate%';

-- ── American Express cards ────────────────────────────────────────
UPDATE credit_cards SET apply_url = 'https://www.americanexpress.com/in/credit-cards/smart-earn-credit-card/'
    WHERE bank_name = 'American Express' AND card_name LIKE '%SmartEarn%';

UPDATE credit_cards SET apply_url = 'https://www.americanexpress.com/in/credit-cards/gold-card/'
    WHERE bank_name = 'American Express' AND card_name LIKE '%Gold%';

UPDATE credit_cards SET apply_url = 'https://www.americanexpress.com/in/credit-cards/platinum-card/'
    WHERE bank_name = 'American Express' AND card_name LIKE '%Platinum%';

-- ── Other banks ───────────────────────────────────────────────────
UPDATE credit_cards SET apply_url = 'https://www.rblbank.com/cards/credit-cards/shoprite-credit-card'
    WHERE bank_name = 'RBL Bank' AND card_name LIKE '%Shoprite%';

UPDATE credit_cards SET apply_url = 'https://www.rblbank.com/cards/credit-cards/world-safari-credit-card'
    WHERE bank_name = 'RBL Bank' AND card_name LIKE '%World Safari%';

UPDATE credit_cards SET apply_url = 'https://www.aubank.in/credit-cards/altura-credit-card'
    WHERE bank_name = 'AU Small Finance Bank';

UPDATE credit_cards SET apply_url = 'https://scapia.app/credit-card'
    WHERE bank_name = 'Federal Bank' AND card_name LIKE '%Scapia%';

UPDATE credit_cards SET apply_url = 'https://www.idfcfirstbank.com/credit-card/wealth-credit-card'
    WHERE bank_name = 'IDFC First Bank' AND card_name LIKE '%Wealth%';

UPDATE credit_cards SET apply_url = 'https://www.idfcfirstbank.com/credit-card/select-credit-card'
    WHERE bank_name = 'IDFC First Bank' AND card_name LIKE '%Select%';

UPDATE credit_cards SET apply_url = 'https://www.getonecard.app/apply'
    WHERE bank_name = 'OneCard';

UPDATE credit_cards SET apply_url = 'https://www.hsbc.co.in/credit-cards/products/cashback/'
    WHERE bank_name = 'HSBC Bank';

UPDATE credit_cards SET apply_url = 'https://www.bankofbaroda.in/personal-banking/cards/credit-cards/bob-premier-credit-card'
    WHERE bank_name = 'Bank of Baroda';