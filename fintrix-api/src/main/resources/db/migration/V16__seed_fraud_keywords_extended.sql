-- ================================================================
-- V16__seed_fraud_keywords_extended.sql
-- 120+ additional fraud keywords covering major gaps:
-- Task-based scams, digital arrest, OTP fraud, job scams,
-- Hindi phrases, KBC/lottery, trading tips, KYC phishing
-- These are the most reported scam types in India 2024-2025
-- ================================================================

-- INSERT INTO fraud_keywords (keyword, risk_level, fraud_type, description) VALUES
INSERT INTO fraud_keywords (keyword, risk_level, fraud_type, description) VALUES
-- ══════════════════════════════════════════════════════════════════
-- TASK-BASED SCAMS (biggest growing fraud category in India 2024)
-- User asked to like YouTube videos, follow Instagram, write reviews
-- Gets paid small amounts initially then asked to "invest" to unlock
-- ══════════════════════════════════════════════════════════════════
('part time job earn money',     'CRITICAL', 'TASK_FRAUD',   'Task-based job scams start with small payments then ask for large "deposits"'),
('work from home job offer',     'HIGH',     'TASK_FRAUD',   'Unsolicited WFH job offers are almost universally scams'),
('youtube like karo paise pao',  'CRITICAL', 'TASK_FRAUD',   'YouTube like/subscribe tasks are a known scam entry point'),
('youtube like earn money',      'CRITICAL', 'TASK_FRAUD',   'Paid YouTube like tasks are fraudulent — YouTube does not pay for likes'),
('video like task',              'CRITICAL', 'TASK_FRAUD',   'Video like tasks are the #1 entry point for task-based investment scams'),
('task complete earn',           'CRITICAL', 'TASK_FRAUD',   'Task completion income schemes always escalate to investment fraud'),
('rate and review earn',         'HIGH',     'TASK_FRAUD',   'Paid review tasks often lead to advance fee fraud'),
('app download earn money',      'HIGH',     'TASK_FRAUD',   'Paid app download tasks are typically fraudulent'),
('survey earn money daily',      'HIGH',     'TASK_FRAUD',   'Daily paid survey schemes are typically fraudulent'),
('data entry work from home',    'HIGH',     'TASK_FRAUD',   'Data entry WFH jobs often require upfront registration fee — illegal'),
('freelance task daily income',  'HIGH',     'TASK_FRAUD',   'Unsolicited freelance task offers with guaranteed income are scams'),
('online task investment',       'CRITICAL', 'TASK_FRAUD',   'Tasks that require you to "invest" to unlock earnings are Ponzi schemes'),
('prepaid task',                 'CRITICAL', 'TASK_FRAUD',   'Any task that requires prepayment is a scam'),
('boost task',                   'HIGH',     'TASK_FRAUD',   'Boost/boost task schemes are fraudulent'),

-- ══════════════════════════════════════════════════════════════════
-- DIGITAL ARREST SCAM (fastest growing 2024 — MHA issued warnings)
-- Fake police/CBI/narcotics officers threaten arrest via video call
-- ══════════════════════════════════════════════════════════════════
('digital arrest',               'CRITICAL', 'IMPERSONATION', 'Digital arrest is a scam — no Indian law enforcement makes video call arrests'),
('cyber crime arrest',           'CRITICAL', 'IMPERSONATION', 'Real cybercrime police do not call and demand immediate payment'),
('narcotics control bureau',     'CRITICAL', 'IMPERSONATION', 'Fake NCB calls demanding payment are a well-documented scam'),
('narcotics parcel',             'CRITICAL', 'IMPERSONATION', 'Fake parcel containing drugs threat is a common scare tactic scam'),
('custom department parcel',     'CRITICAL', 'IMPERSONATION', 'Fake customs calls about illegal parcels are impersonation scams'),
('ed enforcement directorate',   'CRITICAL', 'IMPERSONATION', 'Fake ED calls demanding money are a well-documented scam'),
('cbi officer calling',          'CRITICAL', 'IMPERSONATION', 'CBI does not call citizens for payment — this is always a scam'),
('police arrest video call',     'CRITICAL', 'IMPERSONATION', 'Police do not conduct arrests via video call — always a scam'),
('money laundering case',        'CRITICAL', 'IMPERSONATION', 'Fake money laundering threats to extract payment are a major scam'),
('sim card illegal activity',    'CRITICAL', 'IMPERSONATION', 'Telecom companies do not call to demand payment to avoid arrest'),
('bank account frozen arrest',   'CRITICAL', 'IMPERSONATION', 'Banks do not arrest account holders via phone — this is fraud'),
('aadhar illegal use',           'CRITICAL', 'IMPERSONATION', 'Fake UIDAI calls about Aadhaar misuse demanding payment are scams'),

-- ══════════════════════════════════════════════════════════════════
-- TELEGRAM SCAMS
-- ══════════════════════════════════════════════════════════════════
('join telegram channel invest', 'CRITICAL', 'SOCIAL_MEDIA_FRAUD', 'Investment channels on Telegram are not regulated — almost all are scams'),
('telegram trading channel',     'CRITICAL', 'SOCIAL_MEDIA_FRAUD', 'Telegram trading channels without SEBI registration are illegal'),
('telegram investment group',    'CRITICAL', 'SOCIAL_MEDIA_FRAUD', 'Telegram investment groups soliciting money are illegal'),
('telegram stock tips',          'CRITICAL', 'SOCIAL_MEDIA_FRAUD', 'Paid stock tips on Telegram require SEBI Research Analyst registration'),
('whatsapp investment group',    'CRITICAL', 'SOCIAL_MEDIA_FRAUD', 'WhatsApp investment groups are illegal without SEBI registration'),
('whatsapp stock tips',          'CRITICAL', 'SOCIAL_MEDIA_FRAUD', 'Stock tips on WhatsApp without SEBI RA registration are illegal'),
('signal trading tips',          'CRITICAL', 'SOCIAL_MEDIA_FRAUD', 'Trading signal groups on Signal app are typically unregistered and illegal'),

-- ══════════════════════════════════════════════════════════════════
-- OTP AND PHISHING SCAMS
-- ══════════════════════════════════════════════════════════════════
('otp share karo',               'CRITICAL', 'PHISHING',      'No legitimate organisation ever asks you to share OTP — always a scam'),
('otp batao',                    'CRITICAL', 'PHISHING',      'Requests for OTP sharing are always fraudulent'),
('share your otp',               'CRITICAL', 'PHISHING',      'Never share OTP with anyone — banks, RBI, no one ever asks for it'),
('kyc update link',              'CRITICAL', 'PHISHING',      'KYC update links sent via SMS/WhatsApp are phishing attempts'),
('kyc expire',                   'CRITICAL', 'PHISHING',      'KYC expiry threats via message are phishing — banks send official letters'),
('account will be blocked kyc',  'CRITICAL', 'PHISHING',      'Threats to block account without KYC update via message are phishing'),
('bank account verify link',     'CRITICAL', 'PHISHING',      'Bank verification links via SMS/WhatsApp are phishing — use official app'),
('net banking credentials',      'CRITICAL', 'PHISHING',      'Never share net banking username/password — banks never ask for this'),
('debit card details',           'CRITICAL', 'PHISHING',      'Sharing card number+CVV+expiry over phone/message is always fraud'),
('credit card otp',              'CRITICAL', 'PHISHING',      'Card OTPs should never be shared — banks never ask for them'),
('upi pin share',                'CRITICAL', 'PHISHING',      'UPI PIN must never be shared — no legitimate party will ask for it'),
('google pay verification',      'CRITICAL', 'PHISHING',      'Google Pay does not call to verify accounts — this is always a scam'),
('paytm kyc update',             'CRITICAL', 'PHISHING',      'Paytm KYC update requests via call/SMS are phishing attempts'),

-- ══════════════════════════════════════════════════════════════════
-- LOTTERY AND PRIZE SCAMS
-- ══════════════════════════════════════════════════════════════════
('kbc winner',                   'CRITICAL', 'LOTTERY_FRAUD',  'KBC winner calls/messages are fake — KBC contacts only through official channels'),
('kaun banega crorepati winner', 'CRITICAL', 'LOTTERY_FRAUD',  'Fake KBC winner notifications demanding processing fee are a well-known scam'),
('lottery winner congratulations','CRITICAL','LOTTERY_FRAUD',  'Unsolicited lottery winner notifications are always scams'),
('prize money claim',            'CRITICAL', 'LOTTERY_FRAUD',  'Prize money claims requiring fee payment are advance fee fraud'),
('lucky draw winner',            'CRITICAL', 'LOTTERY_FRAUD',  'Unsolicited lucky draw winner notifications are always scams'),
('you have won',                 'HIGH',     'LOTTERY_FRAUD',  'Unsolicited "you have won" messages are typically scams'),
('scratch card winner',          'HIGH',     'LOTTERY_FRAUD',  'Scratch card winner scams require upfront payment to claim fake prize'),
('free iphone winner',           'CRITICAL', 'LOTTERY_FRAUD',  'Free iPhone/gadget winner notifications are advance fee scams'),
('free laptop offer',            'HIGH',     'FAKE_INVESTMENT','Free laptop/gift offers typically require investment or fee payment'),
('government lottery',           'CRITICAL', 'LOTTERY_FRAUD',  'There is no government lottery that contacts winners via phone/WhatsApp'),

-- ══════════════════════════════════════════════════════════════════
-- JOB AND VISA SCAMS
-- ══════════════════════════════════════════════════════════════════
('foreign job offer',            'HIGH',     'JOB_FRAUD',     'Unsolicited foreign job offers often involve human trafficking or advance fee fraud'),
('abroad job opportunity',       'HIGH',     'JOB_FRAUD',     'Verify abroad job offers through official embassy channels'),
('visa job offer',               'HIGH',     'JOB_FRAUD',     'Job offers promising visa without interview are typically fraudulent'),
('work permit fee',              'CRITICAL', 'JOB_FRAUD',     'Work permits are obtained through official channels — not by paying agents'),
('job placement fee',            'CRITICAL', 'JOB_FRAUD',     'Legitimate job placements do not charge the candidate — always a scam if they do'),
('registration fee job',         'CRITICAL', 'JOB_FRAUD',     'Job registration fees are illegal in India under Employment Exchanges Act'),
('dubai job easy salary',        'HIGH',     'JOB_FRAUD',     'Easy high-salary Dubai jobs without proper verification are often trafficking scams'),
('call centre job abroad',       'HIGH',     'JOB_FRAUD',     'Abroad call centre job scams have led to human trafficking — verify carefully'),

-- ══════════════════════════════════════════════════════════════════
-- HINDI / HINGLISH SCAM PHRASES (very common in India)
-- ══════════════════════════════════════════════════════════════════
('paisa double karo',            'CRITICAL', 'PONZI',         'Paisa double schemes are classic Ponzi fraud'),
('paise double',                 'CRITICAL', 'PONZI',         'Money doubling schemes are illegal'),
('invest karo kamao',            'CRITICAL', 'FAKE_INVESTMENT','Unsolicited invest-and-earn offers are typically fraudulent'),
('ghar baithe paise kamao',      'CRITICAL', 'TASK_FRAUD',    'Earn from home without any skill offers are always scams'),
('roz 1000 kamao',               'CRITICAL', 'PONZI',         'Guaranteed daily income schemes are Ponzi fraud'),
('1000 lagao 10000 pao',         'CRITICAL', 'PONZI',         '10x returns schemes are classic Ponzi fraud'),
('refer karo unlimited kamao',   'CRITICAL', 'MLM_FRAUD',     'Unlimited refer-and-earn schemes are pyramid/MLM fraud'),
('group join karo earning shuru','CRITICAL', 'TASK_FRAUD',    'Earn-by-joining-group offers are scam entry points'),

-- ══════════════════════════════════════════════════════════════════
-- TRADING TIPS SCAMS (SEBI has repeatedly warned about these)
-- ══════════════════════════════════════════════════════════════════
('trading tips guaranteed profit','CRITICAL','SOCIAL_MEDIA_FRAUD','Guaranteed profit trading tips are illegal — requires SEBI RA registration'),
('intraday tips free',           'HIGH',     'SOCIAL_MEDIA_FRAUD','Free intraday tips without SEBI RA registration are illegal'),
('options trading tips',         'HIGH',     'SOCIAL_MEDIA_FRAUD','Options tips without SEBI Research Analyst registration are illegal'),
('sebi registered tips provider','HIGH',     'SOCIAL_MEDIA_FRAUD','Verify SEBI RA registration at sebi.gov.in before paying for tips'),
('stock market tips paid',       'HIGH',     'SOCIAL_MEDIA_FRAUD','Paid stock tips without SEBI registration are illegal — verify first'),
('futures tips',                 'HIGH',     'SOCIAL_MEDIA_FRAUD','F&O tips providers must be SEBI registered Research Analysts'),
('100% accurate tips',           'CRITICAL', 'SOCIAL_MEDIA_FRAUD','100% accurate stock tips are impossible and indicate fraud'),
('sure shot tips',               'CRITICAL', 'SOCIAL_MEDIA_FRAUD','Sure shot / sure profit tips are deceptive and illegal'),
('jackpot call',                 'CRITICAL', 'SOCIAL_MEDIA_FRAUD','Jackpot trading calls are a well-documented SEBI-warned scam category'),
('operator call',                'CRITICAL', 'SOCIAL_MEDIA_FRAUD','Operator calls for stock manipulation are illegal under SEBI'),

-- ══════════════════════════════════════════════════════════════════
-- VIP / INVESTMENT PACKAGE SCAMS
-- ══════════════════════════════════════════════════════════════════
('vip investment plan',          'CRITICAL', 'FAKE_INVESTMENT','VIP investment plans with guaranteed returns are fraudulent'),
('premium investment package',   'CRITICAL', 'FAKE_INVESTMENT','Premium investment packages promising returns are fraudulent'),
('gold member investment',       'CRITICAL', 'FAKE_INVESTMENT','Gold/silver member investment tiers are typical Ponzi structure'),
('upgrade to earn more',         'CRITICAL', 'PONZI',         'Upgrade to higher tier to earn more is classic Ponzi structure'),
('vip group profit',             'CRITICAL', 'FAKE_INVESTMENT','VIP profit groups without SEBI registration are illegal'),

-- ══════════════════════════════════════════════════════════════════
-- FAKE GOVERNMENT SCHEME SCAMS
-- ══════════════════════════════════════════════════════════════════
('modi free scheme',             'CRITICAL', 'FAKE_REGULATORY','Fake Modi government scheme offers are impersonation fraud'),
('government subsidy apply',     'HIGH',     'FAKE_REGULATORY','Verify government subsidies only at official .gov.in websites'),
('pm kisan extra money',         'CRITICAL', 'FAKE_REGULATORY','Fake PM Kisan scheme payments are advance fee fraud'),
('ayushman card money',          'HIGH',     'FAKE_REGULATORY','Ayushman Bharat money release scams are well-documented'),
('free solar panel apply',       'HIGH',     'FAKE_REGULATORY','Free solar panel scheme requiring fee is fraud — PM Surya Ghar is official'),
('ration card bonus',            'HIGH',     'FAKE_REGULATORY','Ration card bonus payment scams require advance payment — illegal'),
('jan dhan account bonus',       'CRITICAL', 'FAKE_REGULATORY','Jan Dhan bonus scams ask for OTP to release fake funds'),

-- ══════════════════════════════════════════════════════════════════
-- SIM CARD AND TELECOM SCAMS
-- ══════════════════════════════════════════════════════════════════
('sim card block',               'CRITICAL', 'IMPERSONATION', 'Telecom companies do not call to block SIM and demand payment'),
('trai sim block',               'CRITICAL', 'IMPERSONATION', 'TRAI does not call individual users about SIM blocking'),
('mobile number disconnect',     'CRITICAL', 'IMPERSONATION', 'Fake mobile disconnection threats to extract personal info are scams'),
('sim upgrade fee',              'CRITICAL', 'IMPERSONATION', 'SIM upgrade fees demanded via call are always fraud'),

-- ══════════════════════════════════════════════════════════════════
-- FAKE LOAN APP SCAMS (major RBI concern 2023-2025)
-- ══════════════════════════════════════════════════════════════════
('instant loan app',             'HIGH',     'LOAN_FRAUD',    'Verify instant loan apps are RBI registered — many are illegal'),
('5 minute loan approval',       'CRITICAL', 'LOAN_FRAUD',    'No-document instant loans often have predatory recovery practices'),
('loan without income proof',    'HIGH',     'LOAN_FRAUD',    'No-income-proof loans often carry illegal interest rates'),
('personal loan low cibil',      'MEDIUM',   'LOAN_FRAUD',    'Very low CIBIL score loan offers often have predatory interest rates'),
('loan app recovery harassment', 'HIGH',     'LOAN_FRAUD',    'Report illegal recovery harassment to RBI Ombudsman'),
('processing charge before loan','CRITICAL', 'LOAN_FRAUD',    'Any upfront charge before loan disbursement is illegal per RBI'),

-- ══════════════════════════════════════════════════════════════════
-- INSURANCE AND HEALTH SCAMS
-- ══════════════════════════════════════════════════════════════════
('insurance maturity bonus',     'HIGH',     'FAKE_INVESTMENT','Fake insurance maturity calls asking for fee to release maturity amount are scams'),
('policy surrender bonus',       'HIGH',     'IMPERSONATION', 'Calls about surprise policy surrender bonuses requiring fee are fraudulent'),
('health insurance cashback',    'HIGH',     'FAKE_INVESTMENT','Unsolicited health insurance cashback offers requiring payment are scams'),

-- ══════════════════════════════════════════════════════════════════
-- ROMANCE AND SOCIAL ENGINEERING SCAMS
-- ══════════════════════════════════════════════════════════════════
('online friendship invest',     'CRITICAL', 'SOCIAL_ENGINEERING','Strangers met online who recommend investments are a well-known scam pattern'),
('dating site investment',       'CRITICAL', 'SOCIAL_ENGINEERING','Investment recommendations from online romantic interests are pig butchering scams'),
('foreign friend send money',    'CRITICAL', 'SOCIAL_ENGINEERING','Foreign friends asking you to invest or send money are typically scams')
ON CONFLICT (keyword) DO NOTHING;;