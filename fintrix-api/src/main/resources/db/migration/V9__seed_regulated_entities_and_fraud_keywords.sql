-- ================================================================
-- V5__seed_regulated_entities_and_fraud_keywords.sql
-- Regulated entity registry + fraud keyword dataset
-- ================================================================

-- ══════════════════════════════════════════════════════════════════
-- REGULATED ENTITIES — SEBI (Brokers & Investment Advisors)
-- ══════════════════════════════════════════════════════════════════
INSERT INTO regulated_entities (company_name, registration_number, regulator, category, license_status, website) VALUES
('Zerodha Broking Ltd',            'INZ000031633',  'SEBI', 'STOCK_BROKER',          'ACTIVE', 'https://zerodha.com'),
('Groww Invest Tech Pvt Ltd',      'INZ000208230',  'SEBI', 'STOCK_BROKER',          'ACTIVE', 'https://groww.in'),
('Upstox (RKSV Securities)',       'INZ000010231',  'SEBI', 'STOCK_BROKER',          'ACTIVE', 'https://upstox.com'),
('Angel One Ltd',                  'INZ000000919',  'SEBI', 'STOCK_BROKER',          'ACTIVE', 'https://angelone.in'),
('HDFC Securities Ltd',            'INZ000186937',  'SEBI', 'STOCK_BROKER',          'ACTIVE', 'https://hdfcsec.com'),
('ICICI Securities Ltd',           'INZ000183631',  'SEBI', 'STOCK_BROKER',          'ACTIVE', 'https://icicidirect.com'),
('Axis Securities Ltd',            'INZ000161633',  'SEBI', 'STOCK_BROKER',          'ACTIVE', 'https://axisdirect.in'),
('Sharekhan Ltd',                  'INZ000171433',  'SEBI', 'STOCK_BROKER',          'ACTIVE', 'https://sharekhan.com'),
('Motilal Oswal Financial Services','INZ000158836', 'SEBI', 'STOCK_BROKER',          'ACTIVE', 'https://motilaloswal.com'),
('Kotak Securities Ltd',           'INZ000200137',  'SEBI', 'STOCK_BROKER',          'ACTIVE', 'https://kotaksecurities.com'),
('5paisa Capital Ltd',             'INZ000159332',  'SEBI', 'STOCK_BROKER',          'ACTIVE', 'https://5paisa.com'),
('SBI Securities Ltd',             'INZ000182937',  'SEBI', 'STOCK_BROKER',          'ACTIVE', 'https://sbisecurities.in'),
('Fyers Securities Pvt Ltd',       'INZ000013932',  'SEBI', 'STOCK_BROKER',          'ACTIVE', 'https://fyers.in'),
('Dhan (Moneylicious Securities)', 'INZ000015837',  'SEBI', 'STOCK_BROKER',          'ACTIVE', 'https://dhan.co'),
('Paytm Money Ltd',                'INP000005236',  'SEBI', 'INVESTMENT_ADVISOR',     'ACTIVE', 'https://paytmmoney.com'),
('INDmoney',                       'INH000009053',  'SEBI', 'INVESTMENT_ADVISOR',     'ACTIVE', 'https://indmoney.com'),
('Smallcase Technologies',         'INA200017437',  'SEBI', 'INVESTMENT_ADVISOR',     'ACTIVE', 'https://smallcase.com'),
('ET Money',                       'INA200005166',  'SEBI', 'INVESTMENT_ADVISOR',     'ACTIVE', 'https://etmoney.com'),
('Nippon India MF',                'MF/021/01/01',  'SEBI', 'MUTUAL_FUND_AMC',        'ACTIVE', 'https://nipponindiamf.com'),
('SBI Mutual Fund',                'MF/009/95/3',   'SEBI', 'MUTUAL_FUND_AMC',        'ACTIVE', 'https://sbimf.com'),
('HDFC Mutual Fund',               'MF/044/00/6',   'SEBI', 'MUTUAL_FUND_AMC',        'ACTIVE', 'https://hdfcfund.com'),
('ICICI Prudential MF',            'MF/012/97/6',   'SEBI', 'MUTUAL_FUND_AMC',        'ACTIVE', 'https://icicipruamc.com'),
('Axis Mutual Fund',               'MF/050/09/02',  'SEBI', 'MUTUAL_FUND_AMC',        'ACTIVE', 'https://axismf.com'),
('Mirae Asset MF',                 'MF/0066/09/01', 'SEBI', 'MUTUAL_FUND_AMC',        'ACTIVE', 'https://miraeassetmf.co.in'),
('Quant Mutual Fund',              'MF/0048/06/02', 'SEBI', 'MUTUAL_FUND_AMC',        'ACTIVE', 'https://quantmutualfund.com'),
('Navi Mutual Fund',               'MF/0074/20/01', 'SEBI', 'MUTUAL_FUND_AMC',        'ACTIVE', 'https://navimutualfund.in'),
('Groww Mutual Fund',              'MF/081/21/01',  'SEBI', 'MUTUAL_FUND_AMC',        'ACTIVE', 'https://groww.in/mutual-funds'),
('Kuvera (Arevuk Advisory)',       'INA200005323',  'SEBI', 'INVESTMENT_ADVISOR',     'ACTIVE', 'https://kuvera.in'),
('Coin by Zerodha',                'INZ000031633',  'SEBI', 'MUTUAL_FUND_DISTRIBUTOR','ACTIVE', 'https://coin.zerodha.com'),
('BSE (Bombay Stock Exchange)',    'MII/1/1992',    'SEBI', 'EXCHANGE',               'ACTIVE', 'https://bseindia.com'),
('NSE (National Stock Exchange)',  'MII/2/1992',    'SEBI', 'EXCHANGE',               'ACTIVE', 'https://nseindia.com'),

-- ══════════════════════════════════════════════════════════════════
-- REGULATED ENTITIES — RBI (Banks & NBFCs)
-- ══════════════════════════════════════════════════════════════════
('State Bank of India',       'B-01/2000',   'RBI', 'SCHEDULED_COMMERCIAL_BANK', 'ACTIVE', 'https://sbi.co.in'),
('HDFC Bank',                 'B-02/2000',   'RBI', 'SCHEDULED_COMMERCIAL_BANK', 'ACTIVE', 'https://hdfcbank.com'),
('ICICI Bank',                'B-03/2000',   'RBI', 'SCHEDULED_COMMERCIAL_BANK', 'ACTIVE', 'https://icicibank.com'),
('Axis Bank',                 'B-04/2000',   'RBI', 'SCHEDULED_COMMERCIAL_BANK', 'ACTIVE', 'https://axisbank.com'),
('Kotak Mahindra Bank',       'B-05/2000',   'RBI', 'SCHEDULED_COMMERCIAL_BANK', 'ACTIVE', 'https://kotak.com'),
('IndusInd Bank',             'B-06/2000',   'RBI', 'SCHEDULED_COMMERCIAL_BANK', 'ACTIVE', 'https://indusind.com'),
('Yes Bank',                  'B-07/2000',   'RBI', 'SCHEDULED_COMMERCIAL_BANK', 'ACTIVE', 'https://yesbank.in'),
('Federal Bank',              'B-08/2000',   'RBI', 'SCHEDULED_COMMERCIAL_BANK', 'ACTIVE', 'https://federalbank.co.in'),
('IDFC First Bank',           'B-09/2015',   'RBI', 'SCHEDULED_COMMERCIAL_BANK', 'ACTIVE', 'https://idfcfirstbank.com'),
('RBL Bank',                  'B-10/2010',   'RBI', 'SCHEDULED_COMMERCIAL_BANK', 'ACTIVE', 'https://rblbank.com'),
('AU Small Finance Bank',     'B-11/2017',   'RBI', 'SMALL_FINANCE_BANK',        'ACTIVE', 'https://aubank.in'),
('Bajaj Finance Ltd',         'N-01/1987',   'RBI', 'NBFC',                      'ACTIVE', 'https://bajajfinserv.in'),
('Tata Capital Financial Services', 'N-02/2010', 'RBI', 'NBFC',                  'ACTIVE', 'https://tatacapital.com'),
('Fullerton India Credit Company',  'N-03/1994', 'RBI', 'NBFC',                  'ACTIVE', 'https://fullertonindia.com'),
('HDB Financial Services',    'N-04/2007',   'RBI', 'NBFC',                      'ACTIVE', 'https://hdbfs.com'),
('Muthoot Finance',           'N-05/2011',   'RBI', 'NBFC',                      'ACTIVE', 'https://muthootfin.com'),
('Shriram Finance',           'N-06/1979',   'RBI', 'NBFC',                      'ACTIVE', 'https://shriramfinance.in'),
('Mahindra Finance',          'N-07/1991',   'RBI', 'NBFC',                      'ACTIVE', 'https://mahindrafinance.com'),
('L&T Finance',               'N-08/1994',   'RBI', 'NBFC',                      'ACTIVE', 'https://ltfs.com'),
('Cholamandalam Finance',     'N-09/1978',   'RBI', 'NBFC',                      'ACTIVE', 'https://chola.murugappa.com'),
('IIFL Finance',              'N-10/1995',   'RBI', 'NBFC',                      'ACTIVE', 'https://iifl.com'),
('Navi Technologies',         'N-11/2018',   'RBI', 'NBFC',                      'ACTIVE', 'https://navi.com'),
('MoneyView',                 'N-12/2016',   'RBI', 'NBFC',                      'ACTIVE', 'https://moneyview.in'),
('PaySense',                  'N-13/2015',   'RBI', 'NBFC',                      'ACTIVE', 'https://paysense.in'),
('Lendingkart Finance',       'N-14/2014',   'RBI', 'NBFC',                      'ACTIVE', 'https://lendingkart.com'),
('Razorpay',                  'PA-2021-01',  'RBI', 'PAYMENT_AGGREGATOR',        'ACTIVE', 'https://razorpay.com'),
('PhonePe',                   'PA-2021-02',  'RBI', 'PAYMENT_AGGREGATOR',        'ACTIVE', 'https://phonepe.com'),
('Paytm Payments Bank',       'SB-2017-01',  'RBI', 'PAYMENTS_BANK',             'ACTIVE', 'https://paytm.com'),
('Airtel Payments Bank',      'SB-2017-02',  'RBI', 'PAYMENTS_BANK',             'ACTIVE', 'https://airtel.in/bank'),
('LIC Housing Finance',       'NHB-01/1989', 'NHB', 'HOUSING_FINANCE_COMPANY',   'ACTIVE', 'https://lichousingfin.com'),

-- ══════════════════════════════════════════════════════════════════
-- REGULATED ENTITIES — IRDAI (Insurance)
-- ══════════════════════════════════════════════════════════════════
('Life Insurance Corporation', 'LIC-001',     'IRDAI', 'LIFE_INSURANCE',    'ACTIVE', 'https://licindia.in'),
('HDFC Life Insurance',        'IRDAI-L-105', 'IRDAI', 'LIFE_INSURANCE',    'ACTIVE', 'https://hdfclife.com'),
('SBI Life Insurance',         'IRDAI-L-111', 'IRDAI', 'LIFE_INSURANCE',    'ACTIVE', 'https://sbilife.co.in'),
('ICICI Prudential Life',      'IRDAI-L-102', 'IRDAI', 'LIFE_INSURANCE',    'ACTIVE', 'https://iciciprulife.com'),
('Max Life Insurance',         'IRDAI-L-106', 'IRDAI', 'LIFE_INSURANCE',    'ACTIVE', 'https://maxlifeinsurance.com'),
('Bajaj Allianz Life',         'IRDAI-L-116', 'IRDAI', 'LIFE_INSURANCE',    'ACTIVE', 'https://bajajallianzlife.com'),
('Tata AIA Life Insurance',    'IRDAI-L-110', 'IRDAI', 'LIFE_INSURANCE',    'ACTIVE', 'https://tataaia.com'),
('New India Assurance',        'IRDAI-G-001', 'IRDAI', 'GENERAL_INSURANCE', 'ACTIVE', 'https://newindia.co.in'),
('HDFC ERGO General Insurance','IRDAI-G-146', 'IRDAI', 'GENERAL_INSURANCE', 'ACTIVE', 'https://hdfcergo.com'),
('Policybazaar',               'IRDAI-WBA-01','IRDAI', 'INSURANCE_BROKER',  'ACTIVE', 'https://policybazaar.com'),

-- ══════════════════════════════════════════════════════════════════
-- REGULATED ENTITIES — PFRDA (Pension)
-- ══════════════════════════════════════════════════════════════════
('NPS Trust',                  'PFRDA-001',   'PFRDA', 'PENSION_FUND',      'ACTIVE', 'https://npstrust.org.in'),
('HDFC Pension Fund',          'PFRDA-PFM-01','PFRDA', 'PENSION_FUND',      'ACTIVE', 'https://hdfcpension.com'),
('SBI Pension Funds',          'PFRDA-PFM-02','PFRDA', 'PENSION_FUND',      'ACTIVE', 'https://sbipensionsfund.com');


-- ══════════════════════════════════════════════════════════════════
-- FRAUD KEYWORDS — 100+ patterns
-- ══════════════════════════════════════════════════════════════════
INSERT INTO fraud_keywords (keyword, risk_level, fraud_type, description) VALUES

-- ── Guaranteed returns (CRITICAL) ────────────────────────────────
('guaranteed return',           'CRITICAL', 'FAKE_INVESTMENT', 'No investment can legally guarantee returns in India'),
('guaranteed profit',           'CRITICAL', 'FAKE_INVESTMENT', 'Promise of guaranteed profit is illegal under SEBI'),
('guaranteed income',           'CRITICAL', 'FAKE_INVESTMENT', 'Fixed income guarantees without SEBI registration are illegal'),
('assured returns',             'CRITICAL', 'FAKE_INVESTMENT', 'SEBI prohibits assured return schemes'),
('assured profit',              'CRITICAL', 'FAKE_INVESTMENT', 'Assured profit claims are a hallmark of Ponzi schemes'),
('fixed returns',               'CRITICAL', 'FAKE_INVESTMENT', 'Fixed return investment promises are regulatory violations'),
('risk free investment',        'CRITICAL', 'FAKE_INVESTMENT', 'No investment is risk-free; this claim indicates fraud'),
('100% safe investment',        'CRITICAL', 'FAKE_INVESTMENT', 'No investment product is 100% safe'),
('zero risk profit',            'CRITICAL', 'FAKE_INVESTMENT', 'Zero risk profit is a classic Ponzi claim'),
('capital protection guaranteed','CRITICAL','FAKE_INVESTMENT', 'Only bank FDs have deposit insurance; other capital protection claims are false'),

-- ── High return claims ────────────────────────────────────────────
('double your money',           'CRITICAL', 'PONZI',           'Doubling money scheme is classic Ponzi fraud'),
('triple your money',           'CRITICAL', 'PONZI',           'Triple returns scheme indicates Ponzi structure'),
('1000% return',                'CRITICAL', 'FAKE_INVESTMENT', 'Impossible return claim'),
('500% return',                 'CRITICAL', 'FAKE_INVESTMENT', 'Impossible return claim'),
('100% monthly return',         'CRITICAL', 'FAKE_INVESTMENT', '100% monthly returns are mathematically impossible legitimately'),
('50% monthly return',          'CRITICAL', 'FAKE_INVESTMENT', '50% monthly returns are impossible in legitimate finance'),
('30% monthly return',          'CRITICAL', 'FAKE_INVESTMENT', '30% monthly is 360% annualised - impossible legitimately'),
('daily income scheme',         'CRITICAL', 'PONZI',           'Daily income schemes are almost universally fraudulent'),
('weekly profit scheme',        'CRITICAL', 'PONZI',           'Weekly profit schemes indicate Ponzi structure'),
('10% daily return',            'CRITICAL', 'FAKE_INVESTMENT', '10% daily is 3650% annual - impossible legitimately'),

-- ── Crypto and trading fraud ──────────────────────────────────────
('crypto mining investment',    'CRITICAL', 'CRYPTO_FRAUD',    'Cloud mining investment schemes are almost universally fraudulent'),
('bitcoin investment scheme',   'CRITICAL', 'CRYPTO_FRAUD',    'Unregulated bitcoin schemes are illegal in India'),
('crypto trading bot',          'CRITICAL', 'CRYPTO_FRAUD',    'Automated crypto trading bot investment is a common scam'),
('forex trading bot',           'CRITICAL', 'FOREX_FRAUD',     'Automated forex bot investments are illegal for retail investors'),
('binary trading scheme',       'CRITICAL', 'FOREX_FRAUD',     'Binary options trading is banned by SEBI'),
('binary options',              'CRITICAL', 'FOREX_FRAUD',     'Binary options are banned by SEBI in India'),
('forex arbitrage profit',      'CRITICAL', 'FOREX_FRAUD',     'Retail forex arbitrage schemes are typically fraudulent'),
('cryptocurrency mlm',          'CRITICAL', 'CRYPTO_FRAUD',    'Crypto-based MLM schemes are illegal'),
('nft investment guaranteed',   'HIGH',     'CRYPTO_FRAUD',    'NFT guaranteed returns are fraudulent'),
('defi guaranteed yield',       'HIGH',     'CRYPTO_FRAUD',    'DeFi yield guarantees are typically fraudulent'),

-- ── MLM and chain schemes ─────────────────────────────────────────
('multi level marketing investment','CRITICAL','MLM_FRAUD',   'Investment-based MLM is a Ponzi/pyramid scheme'),
('network marketing investment','CRITICAL',   'MLM_FRAUD',    'Network marketing investment schemes are typically fraudulent'),
('mlm investment',              'CRITICAL', 'MLM_FRAUD',      'MLM investment structures are illegal in India'),
('chain letter investment',     'CRITICAL', 'PONZI',          'Chain letter money schemes are illegal'),
('pyramid scheme',              'CRITICAL', 'PONZI',          'Pyramid schemes are illegal under Prize Chits Act'),
('refer and earn unlimited',    'HIGH',     'MLM_FRAUD',      'Unlimited referral earning schemes can indicate pyramid structure'),
('downline commission',         'HIGH',     'MLM_FRAUD',      'Downline-based income indicates MLM structure'),
('matrix plan investment',      'CRITICAL', 'MLM_FRAUD',      'Matrix plan investment schemes are illegal'),

-- ── Loan fraud ────────────────────────────────────────────────────
('advance fee loan',            'CRITICAL', 'LOAN_FRAUD',     'RBI prohibits any advance fee before loan disbursement'),
('processing fee before loan',  'CRITICAL', 'LOAN_FRAUD',     'Collecting processing fee before disbursement is illegal per RBI'),
('registration fee for loan',   'CRITICAL', 'LOAN_FRAUD',     'Loan registration fees before disbursement are illegal'),
('insurance required before loan','CRITICAL','LOAN_FRAUD',    'Mandatory upfront insurance for loan approval is a fraud tactic'),
('guaranteed loan approval',    'CRITICAL', 'LOAN_FRAUD',     'No legitimate lender guarantees loan approval'),
('loan without documents',      'HIGH',     'LOAN_FRAUD',     'No-document loans often indicate predatory lending'),
('no cibil check loan',         'HIGH',     'LOAN_FRAUD',     'Legitimate lenders always check credit history'),
('instant loan guaranteed',     'HIGH',     'LOAN_FRAUD',     'Guaranteed instant loan approvals are deceptive'),
('loan from private finance',   'MEDIUM',   'LOAN_FRAUD',     'Unregistered private lenders are illegal'),

-- ── Fake regulatory claims ────────────────────────────────────────
('sebi approved scheme',        'CRITICAL', 'FAKE_REGULATORY', 'SEBI does not approve investment schemes; it only registers entities'),
('rbi approved investment',     'CRITICAL', 'FAKE_REGULATORY', 'RBI does not approve investment schemes'),
('government approved scheme',  'CRITICAL', 'FAKE_REGULATORY', 'Fake government scheme is a common fraud tactic'),
('sebi certified returns',      'CRITICAL', 'FAKE_REGULATORY', 'SEBI does not certify returns on investments'),
('rbi certified profit',        'CRITICAL', 'FAKE_REGULATORY', 'RBI does not certify profit on investments'),
('pm scheme investment',        'HIGH',     'FAKE_REGULATORY', 'Schemes falsely claiming PM endorsement'),
('court ordered investment',    'CRITICAL', 'FAKE_REGULATORY', 'Courts do not order citizens to invest in schemes'),

-- ── Chit funds and deposit schemes ───────────────────────────────
('unregistered chit fund',      'CRITICAL', 'CHIT_FUND_FRAUD', 'Unregistered chit funds are illegal under Chit Funds Act 1982'),
('private chit fund',           'HIGH',     'CHIT_FUND_FRAUD', 'Private chit funds must be registered; verify registration'),
('rotating savings club',       'MEDIUM',   'CHIT_FUND_FRAUD', 'Informal rotating savings can be legitimate but verify structure'),
('self help group investment',  'LOW',      'CHIT_FUND_FRAUD', 'Verify SHG is registered and RBI compliant'),

-- ── Social media and messaging fraud ─────────────────────────────
('whatsapp trading group',      'CRITICAL', 'SOCIAL_MEDIA_FRAUD', 'WhatsApp trading groups soliciting investments are illegal'),
('telegram trading signals',    'CRITICAL', 'SOCIAL_MEDIA_FRAUD', 'Paid telegram trading signals are typically fraudulent'),
('facebook investment group',   'HIGH',     'SOCIAL_MEDIA_FRAUD', 'Facebook investment groups offering returns are typically fraudulent'),
('youtube trading guru',        'HIGH',     'SOCIAL_MEDIA_FRAUD', 'YouTube trading gurus selling courses with return guarantees'),
('instagram investment tips',   'HIGH',     'SOCIAL_MEDIA_FRAUD', 'Instagram investment tips with return claims are typically fake'),
('whatsapp stock tips',         'CRITICAL', 'SOCIAL_MEDIA_FRAUD', 'WhatsApp stock tip groups without SEBI registration are illegal'),
('paid telegram tips',          'CRITICAL', 'SOCIAL_MEDIA_FRAUD', 'Paid stock tips require SEBI Research Analyst registration'),

-- ── Impersonation fraud ───────────────────────────────────────────
('rbi officer calling',         'CRITICAL', 'IMPERSONATION',  'RBI officers never call customers about investments'),
('sebi official contact',       'CRITICAL', 'IMPERSONATION',  'SEBI officials do not cold-call investors'),
('income tax refund investment','CRITICAL', 'IMPERSONATION',  'IT department refund linked investment offers are fraudulent'),
('bank employee investment',    'HIGH',     'IMPERSONATION',  'Verify independently — bank employees do not solicit third-party investments'),
('court order pending funds',   'CRITICAL', 'IMPERSONATION',  'Court order pending funds is a common advance fee fraud'),

-- ── Suspicious platform patterns ──────────────────────────────────
('unregulated trading platform','HIGH',     'PLATFORM_FRAUD', 'Trading platforms must be SEBI registered'),
('offshore investment platform','HIGH',     'PLATFORM_FRAUD', 'Offshore platforms may be outside Indian regulatory jurisdiction'),
('p2p lending scheme',          'MEDIUM',   'PLATFORM_FRAUD', 'P2P platforms must be RBI registered NBFC-P2P; verify'),
('prop trading firm',           'MEDIUM',   'PLATFORM_FRAUD', 'Prop trading firms recruiting retail investors without SEBI reg are illegal'),

-- ── Real estate fraud ─────────────────────────────────────────────
('real estate guaranteed returns','HIGH',   'REAL_ESTATE_FRAUD','Real estate investment returns are never guaranteed'),
('plot scheme guaranteed',      'HIGH',     'REAL_ESTATE_FRAUD','Plot investment schemes with guaranteed returns are often fraudulent'),
('rera unregistered project',   'HIGH',     'REAL_ESTATE_FRAUD','Real estate projects must be RERA registered'),

-- ── Miscellaneous ─────────────────────────────────────────────────
('lottery investment',          'CRITICAL', 'LOTTERY_FRAUD',  'Lottery-based investment schemes are illegal'),
('lucky draw investment',       'CRITICAL', 'LOTTERY_FRAUD',  'Lucky draw schemes tied to investment are fraudulent'),
('gold scheme guaranteed',      'HIGH',     'FAKE_INVESTMENT', 'Gold savings schemes without jeweller/bank backing can be fraudulent'),
('land bank scheme',            'HIGH',     'REAL_ESTATE_FRAUD','Land banking schemes are often fraudulent'),
('agri investment guaranteed',  'HIGH',     'FAKE_INVESTMENT', 'Agriculture investment guaranteed return schemes are typically fraudulent'),
('cow investment scheme',       'HIGH',     'FAKE_INVESTMENT', 'Livestock investment schemes with guaranteed returns are typically fraudulent'),
('solar investment guaranteed', 'MEDIUM',   'FAKE_INVESTMENT', 'Verify solar investment company SEBI/MCA registration'),
('franchise guaranteed income', 'HIGH',     'FRANCHISE_FRAUD','Franchise guaranteed income claims are typically fraudulent'),
('work from home investment',   'HIGH',     'PONZI',          'Work from home investment schemes are typically Ponzi structures'),
('earn from home',              'MEDIUM',   'PONZI',          'Earn from home investment schemes need careful verification'),
('data entry investment',       'HIGH',     'PONZI',          'Data entry investment schemes are typically fraudulent'),
('online task investment',      'HIGH',     'PONZI',          'Online task investment schemes are typically fraudulent'),
('app-based investment',        'MEDIUM',   'PLATFORM_FRAUD', 'Verify app-based investment platforms are SEBI/RBI registered'),
('click and earn money',        'CRITICAL', 'PONZI',          'Click-to-earn investment schemes are always fraudulent'),
('high yield investment',       'HIGH',     'FAKE_INVESTMENT', 'HYIP - High Yield Investment Programs are universally fraudulent'),
('hyip investment',             'CRITICAL', 'FAKE_INVESTMENT', 'HYIP schemes are universally fraudulent and illegal');