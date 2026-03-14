-- ================================================================
-- V3__seed_credit_cards_full.sql
-- 50+ real Indian credit cards with complete production data
-- Replaces the 10-card seed from V1
-- ================================================================

TRUNCATE TABLE credit_cards RESTART IDENTITY CASCADE;

INSERT INTO credit_cards (
    bank_name, card_name, card_network, card_category, reward_type,
    min_credit_score, min_monthly_income, min_age, max_age,
    allowed_employment_types, joining_fee, annual_fee,
    annual_fee_waiver_condition, interest_rate, reward_rate,
    welcome_benefit, key_benefits,
    fuel_surcharge_waiver, international_usage, lounge_access, is_active
) VALUES

-- ══════════════════════════════════════════════════════════════════
-- HDFC BANK (7 cards)
-- ══════════════════════════════════════════════════════════════════
('HDFC Bank', 'Millennia Credit Card', 'Mastercard', 'CASHBACK', 'CASHBACK',
 700, 25000, 21, 60, 'SALARIED,SELF_EMPLOYED,GOVERNMENT',
 1000, 1000, 'Spend ₹1,00,000 in a year', 42.00,
 '5% cashback on Amazon/Flipkart/Myntra; 2.5% on EMI; 1% all else',
 '₹1,000 welcome gift voucher',
 '["5% cashback on top online merchants","2 complimentary lounge visits per quarter","Zomato/Swiggy/Uber 5% cashback","BookMyShow discount","Milestone rewards"]',
 TRUE, TRUE, '2 per quarter domestic', TRUE),

('HDFC Bank', 'Regalia Credit Card', 'Visa Signature', 'TRAVEL', 'REWARD_POINTS',
 750, 100000, 21, 60, 'SALARIED,SELF_EMPLOYED',
 2500, 2500, 'Spend ₹3,00,000 in a year', 40.80,
 '4 Reward Points per ₹150; 5X on travel portals',
 '2,500 welcome reward points worth ₹625',
 '["6 international + 12 domestic lounge visits/yr","Comprehensive travel insurance up to ₹1 Cr","Golf program access","Concierge service","Priority Pass membership"]',
 TRUE, TRUE, '6 international + 12 domestic per year', TRUE),

('HDFC Bank', 'Infinia Credit Card Metal Edition', 'Visa Infinite', 'PREMIUM', 'REWARD_POINTS',
 800, 300000, 21, 65, 'SALARIED,SELF_EMPLOYED',
 12500, 12500, 'Spend ₹10,00,000 in a year', 40.80,
 '5 Reward Points per ₹150; 10X on SmartBuy',
 '12,500 reward points worth ₹3,125 as welcome gift',
 '["Unlimited Priority Pass lounge access","Complimentary Club Marriott membership","Golf program","Concierge 24/7","Insurance cover ₹3.5 Cr","Annual fee waived on ₹10L spend"]',
 TRUE, TRUE, 'Unlimited domestic + international Priority Pass', TRUE),

('HDFC Bank', 'MoneyBack+ Credit Card', 'Mastercard', 'ENTRY_LEVEL', 'CASHBACK',
 650, 15000, 21, 60, 'SALARIED,SELF_EMPLOYED',
 500, 500, 'Spend ₹50,000 in a year', 42.00,
 '2 CashPoints per ₹150; 10X on SmartBuy',
 '₹500 gift voucher on card activation',
 '["Good starter card","CashPoints redeemable vs statement","Fuel surcharge waiver","Easy upgrade path to premium cards"]',
 TRUE, TRUE, 'None', TRUE),

('HDFC Bank', 'Tata Neu Infinity HDFC Card', 'Visa', 'CASHBACK', 'CASHBACK',
 700, 50000, 21, 60, 'SALARIED,SELF_EMPLOYED',
 1499, 1499, 'Spend ₹3,00,000 in a year', 42.00,
 '5% NeuCoins on Tata brands; 1.5% on others',
 '1,499 NeuCoins as welcome benefit',
 '["5% NeuCoins on Tata brands (BigBasket, Croma, Air Asia, etc.)","Complimentary Tata Neu Plus membership","Lounge access","Insurance benefits"]',
 TRUE, TRUE, '2 per quarter domestic', TRUE),

('HDFC Bank', 'Diners Club Privilege Card', 'Diners Club', 'TRAVEL', 'REWARD_POINTS',
 750, 75000, 21, 65, 'SALARIED,SELF_EMPLOYED',
 2500, 2500, 'Spend ₹3,00,000 in a year', 40.80,
 '4 Reward Points per ₹150',
 'Welcome gift voucher worth ₹2,500',
 '["Unlimited domestic lounge access","6 international lounge visits","Golf access","Dining privileges","Milestone upgrade option"]',
 TRUE, TRUE, 'Unlimited domestic + 6 international', TRUE),

('HDFC Bank', 'Pixel Play Credit Card', 'Visa', 'CASHBACK', 'CASHBACK',
 680, 25000, 21, 60, 'SALARIED,SELF_EMPLOYED',
 0, 0, 'Lifetime free', 42.00,
 '5% cashback on selected merchant category of choice',
 'Choose your own 5% cashback category',
 '["Choose your own reward category","Lifetime free card","Good for millennials","Digital-first card"]',
 TRUE, TRUE, 'None', TRUE),

-- ══════════════════════════════════════════════════════════════════
-- ICICI BANK (7 cards)
-- ══════════════════════════════════════════════════════════════════
('ICICI Bank', 'Amazon Pay ICICI Credit Card', 'Visa', 'CASHBACK', 'CASHBACK',
 700, 25000, 21, 58, 'SALARIED,SELF_EMPLOYED',
 0, 0, 'Lifetime free', 42.00,
 '5% on Amazon for Prime; 3% non-Prime; 2% on others',
 'Instant approval + Amazon Pay cashback credited next day',
 '["Lifetime free - no annual fee ever","5% on Amazon Prime orders","2% on all non-Amazon spends","No minimum redemption","Instant online approval"]',
 TRUE, TRUE, 'None', TRUE),

('ICICI Bank', 'Coral Credit Card', 'Visa', 'ENTRY_LEVEL', 'REWARD_POINTS',
 680, 20000, 23, 58, 'SALARIED,SELF_EMPLOYED',
 500, 500, 'Spend ₹1,50,000 in a year', 42.00,
 '2 PAYBACK points per ₹100; 4X on utilities',
 '₹500 welcome voucher on first spend',
 '["Good entry-level card","2 domestic lounge per quarter","Movie discounts via BookMyShow","Fuel surcharge waiver","PAYBACK points program"]',
 TRUE, TRUE, '2 per quarter domestic', TRUE),

('ICICI Bank', 'Sapphiro Credit Card', 'American Express', 'PREMIUM', 'REWARD_POINTS',
 760, 125000, 21, 60, 'SALARIED,SELF_EMPLOYED',
 6500, 3500, 'Spend ₹6,00,000 in a year', 40.80,
 '4 PAYBACK points per ₹100',
 '₹5,000 joining voucher',
 '["Complimentary golf rounds","Airport lounge access unlimited domestic","Travel insurance","Concierge service","Priority Pass"]',
 TRUE, TRUE, 'Unlimited domestic + 6 international per year', TRUE),

('ICICI Bank', 'HPCL Super Saver Credit Card', 'Visa', 'FUEL', 'FUEL_SURCHARGE_WAIVER',
 680, 20000, 21, 58, 'SALARIED,SELF_EMPLOYED,GOVERNMENT',
 500, 500, 'Spend ₹1,00,000 in a year', 42.00,
 '4% cashback on HPCL fuel; 1.5% on groceries',
 '500 bonus reward points on first fuel transaction',
 '["4% value back on HPCL fuel","1% fuel surcharge waiver","Cashback on groceries and utilities","Good for regular drivers"]',
 TRUE, FALSE, 'None', TRUE),

('ICICI Bank', 'MMT ICICI Bank Platinum Credit Card', 'Mastercard', 'TRAVEL', 'AIRLINE_MILES',
 720, 50000, 21, 58, 'SALARIED,SELF_EMPLOYED',
 1500, 1500, 'Spend ₹2,00,000 in a year', 42.00,
 '6 My Cash per ₹100 on MakeMyTrip; 1 My Cash elsewhere',
 '₹1,500 MakeMyTrip voucher',
 '["6X rewards on MakeMyTrip","Complimentary domestic lounge visits","Travel insurance","Hotel discounts","Good for frequent travelers"]',
 TRUE, TRUE, '2 per quarter domestic', TRUE),

('ICICI Bank', 'Emeralde Private Metal Credit Card', 'Visa Infinite', 'PREMIUM', 'REWARD_POINTS',
 820, 500000, 21, 65, 'SALARIED,SELF_EMPLOYED',
 12000, 12000, 'Spend ₹15,00,000 in a year', 40.80,
 '6 PAYBACK points per ₹100',
 '12,000 reward points + complimentary golf sessions',
 '["Unlimited lounge access globally","Dedicated relationship manager","Golf membership","Luxury hotel upgrades","Highest tier ICICI card"]',
 TRUE, TRUE, 'Unlimited domestic + international Priority Pass', TRUE),

('ICICI Bank', 'Rubyx Credit Card', 'Visa', 'TRAVEL', 'REWARD_POINTS',
 720, 60000, 21, 58, 'SALARIED,SELF_EMPLOYED',
 3000, 2000, 'Spend ₹3,50,000 in a year', 40.80,
 '2 PAYBACK points per ₹100; 5X on travel',
 '₹2,000 travel voucher',
 '["Comprehensive travel insurance","Airport lounge access","Golf benefits","Movie discounts","Good mid-tier travel card"]',
 TRUE, TRUE, '4 per quarter domestic + 2 international', TRUE),

-- ══════════════════════════════════════════════════════════════════
-- AXIS BANK (6 cards)
-- ══════════════════════════════════════════════════════════════════
('Axis Bank', 'Flipkart Axis Bank Credit Card', 'Visa', 'CASHBACK', 'CASHBACK',
 700, 15000, 21, 60, 'SALARIED,SELF_EMPLOYED',
 500, 500, 'Spend ₹2,00,000 in a year', 52.86,
 '5% on Flipkart; 4% on Myntra/2GUD; 1.5% on others',
 '₹500 Flipkart voucher on first transaction',
 '["5% on Flipkart always","No cost EMI on Flipkart","Cleartrip cashback","Good for Flipkart shoppers","Fuel surcharge waiver"]',
 TRUE, TRUE, 'None', TRUE),

('Axis Bank', 'Magnus Credit Card', 'Mastercard World Elite', 'PREMIUM', 'AIRLINE_MILES',
 780, 150000, 18, 70, 'SALARIED,SELF_EMPLOYED',
 12500, 12500, 'Spend ₹15,00,000 in a year', 40.80,
 '12 EDGE Miles per ₹200; 35 EDGE Miles on travel partners',
 'Magnus membership kit + TATA CLiQ voucher ₹25,000',
 '["Unlimited domestic + international lounge access","35 EDGE Miles per ₹200 on Axis Travel EDGE","Complimentary golf","Annual fee waived on ₹15L spend","Best-in-class travel card"]',
 TRUE, TRUE, 'Unlimited domestic + Priority Pass international', TRUE),

('Axis Bank', 'Ace Credit Card', 'Visa', 'CASHBACK', 'CASHBACK',
 720, 15000, 18, 70, 'SALARIED,SELF_EMPLOYED',
 499, 499, 'Spend ₹2,00,000 in a year', 52.86,
 '5% on utility bill payments via Google Pay; 4% on Swiggy/Zomato/Ola; 2% on others',
 'None — low fee card',
 '["5% cashback on utility bills","Best cashback on everyday spending","Low joining fee","Good for monthly expenses","Google Pay integration"]',
 TRUE, TRUE, 'None', TRUE),

('Axis Bank', 'Vistara Credit Card', 'Visa', 'TRAVEL', 'AIRLINE_MILES',
 740, 75000, 21, 65, 'SALARIED,SELF_EMPLOYED',
 1500, 1500, 'Spend ₹2,00,000 in a year', 52.86,
 '3 CV Points per ₹200; 6 CV Points on Vistara tickets',
 '1 Vistara complimentary ticket in economy class',
 '["Complimentary Vistara flights on spend milestones","Club Vistara Silver status","Lounge access","Travel insurance","Good for Vistara flyers"]',
 TRUE, TRUE, '2 per quarter domestic', TRUE),

('Axis Bank', 'Select Credit Card', 'Mastercard', 'TRAVEL', 'REWARD_POINTS',
 730, 50000, 21, 65, 'SALARIED,SELF_EMPLOYED',
 3000, 3000, 'Spend ₹6,00,000 in a year', 52.86,
 '10 EDGE Reward Points per ₹200',
 '5,000 bonus EDGE points on joining',
 '["10 EDGE points per ₹200","Complimentary airport lounge","Golf access","Comprehensive insurance","Mid-tier premium card"]',
 TRUE, TRUE, '4 per quarter domestic + 2 international', TRUE),

('Axis Bank', 'My Zone Credit Card', 'Visa', 'ENTRY_LEVEL', 'CASHBACK',
 650, 15000, 18, 65, 'SALARIED,SELF_EMPLOYED,STUDENT',
 500, 500, 'Lifetime free after first year waiver', 52.86,
 '1.5% cashback on all online spends',
 '₹500 Paytm cashback on first online transaction',
 '["Good entry-level card","Online shopping cashback","Movie discount offers","Low income threshold","Easy approval"]',
 TRUE, TRUE, 'None', TRUE),

-- ══════════════════════════════════════════════════════════════════
-- SBI CARD (6 cards)
-- ══════════════════════════════════════════════════════════════════
('SBI Card', 'SimplyCLICK SBI Card', 'Visa', 'CASHBACK', 'REWARD_POINTS',
 700, 20000, 21, 65, 'SALARIED,GOVERNMENT,PSU',
 499, 499, 'Spend ₹1,00,000 in a year', 42.00,
 '10X points on Amazon/BookMyShow/Cleartrip/Lenskart; 5X on Foodpanda',
 'Amazon.in gift card worth ₹500',
 '["10X reward points on partner merchants","E-voucher on ₹1L annual spend","Fuel surcharge waiver","Good for online shopping"]',
 TRUE, TRUE, 'None', TRUE),

('SBI Card', 'BPCL Octane SBI Credit Card', 'Visa', 'FUEL', 'FUEL_SURCHARGE_WAIVER',
 680, 20000, 21, 65, 'SALARIED,GOVERNMENT',
 1499, 1499, 'Spend ₹50,000 in a year', 42.00,
 '7.25% value back on BPCL fuel; 25X reward points on BPCL',
 '6,000 bonus reward points worth ₹1,500',
 '["7.25% value back on BPCL fuel","Best fuel credit card in India","1% surcharge waiver","4 domestic lounge visits","Good for BPCL users"]',
 TRUE, FALSE, '4 per year domestic', TRUE),

('SBI Card', 'SBI Card PRIME', 'Visa Signature', 'TRAVEL', 'REWARD_POINTS',
 750, 50000, 21, 65, 'SALARIED,GOVERNMENT,PSU',
 2999, 2999, 'Spend ₹3,00,000 in a year', 42.00,
 '10X points on dining/groceries/movies; 2X on others',
 '₹3,000 e-gift voucher choice',
 '["Complimentary Club Vistara Silver status","Priority Pass membership","Renewal ₹3,000 e-voucher","Comprehensive insurance","Good premium government employee card"]',
 TRUE, TRUE, '8 domestic + 2 international per year', TRUE),

('SBI Card', 'SBI Card ELITE', 'Mastercard World', 'PREMIUM', 'REWARD_POINTS',
 770, 100000, 21, 65, 'SALARIED,GOVERNMENT,PSU',
 4999, 4999, 'Spend ₹10,00,000 in a year', 40.80,
 '5 reward points per ₹100; 10X on dining/entertainment',
 '₹5,000 e-gift voucher + welome kit',
 '["Unlimited lounge access","Comprehensive travel insurance ₹1Cr","Golf access","Concierge service","Top SBI premium card"]',
 TRUE, TRUE, 'Unlimited domestic + 6 international Priority Pass', TRUE),

('SBI Card', 'Cashback SBI Card', 'Visa', 'CASHBACK', 'CASHBACK',
 700, 20000, 21, 65, 'SALARIED,GOVERNMENT,PSU,SELF_EMPLOYED',
 999, 999, 'Spend ₹2,00,000 in a year', 42.00,
 '5% cashback on all online transactions; 1% on offline',
 '₹1,000 cashback on first statement',
 '["Flat 5% cashback on ALL online transactions","No merchant restrictions","Easiest cashback card","Fuel surcharge waiver","Good for high online spenders"]',
 TRUE, TRUE, 'None', TRUE),

('SBI Card', 'SimplySAVE SBI Card', 'Visa', 'ENTRY_LEVEL', 'REWARD_POINTS',
 650, 10000, 21, 65, 'SALARIED,GOVERNMENT,PSU',
 499, 499, 'Spend ₹1,00,000 in a year', 42.00,
 '10X points on dining/movies/groceries; 1X on others',
 '2,000 bonus reward points on first spend',
 '["Low income threshold","10X on everyday spending","Good for entry level","Fuel surcharge waiver","Easy upgrade to PRIME"]',
 TRUE, TRUE, 'None', TRUE),

-- ══════════════════════════════════════════════════════════════════
-- KOTAK MAHINDRA BANK (4 cards)
-- ══════════════════════════════════════════════════════════════════
('Kotak Bank', 'Kotak 811 #DreamDifferent Card', 'Mastercard', 'ENTRY_LEVEL', 'CASHBACK',
 600, 0, 18, 75, 'SALARIED,SELF_EMPLOYED,STUDENT,UNEMPLOYED',
 0, 0, 'Lifetime free', 42.00,
 '2% cashback on online; 1% on offline',
 'Instant digital card issuance',
 '["Lifetime free - zero annual fee","Best card for beginners and students","No minimum income requirement","Instant approval","Works on RuPay network"]',
 TRUE, TRUE, 'None', TRUE),

('Kotak Bank', 'Kotak League Platinum Card', 'Visa', 'CASHBACK', 'CASHBACK',
 680, 25000, 21, 65, 'SALARIED,SELF_EMPLOYED',
 499, 499, 'Spend ₹1,00,000 in a year', 42.00,
 '8 reward points per ₹150 on weekends; 4 on weekdays',
 '500 bonus points on activation',
 '["2X rewards on weekends","Domestic lounge access","Fuel surcharge waiver","Movie discounts","Good weekend spender card"]',
 TRUE, TRUE, '2 per quarter domestic', TRUE),

('Kotak Bank', 'Kotak Zen Signature Credit Card', 'Visa Signature', 'TRAVEL', 'REWARD_POINTS',
 740, 75000, 21, 65, 'SALARIED,SELF_EMPLOYED',
 1500, 1500, 'Spend ₹1,50,000 in a year', 40.80,
 '10 reward points per ₹150; 15X on international',
 '5,000 bonus reward points',
 '["15X points on international spends","Airport lounge access","Travel insurance","Zero foreign currency markup on select plans","Good international card"]',
 TRUE, TRUE, '4 per quarter domestic + 2 international', TRUE),

('Kotak Bank', 'Kotak Royale Signature Credit Card', 'Visa Signature', 'PREMIUM', 'REWARD_POINTS',
 760, 100000, 21, 65, 'SALARIED,SELF_EMPLOYED',
 2500, 2500, 'Spend ₹5,00,000 in a year', 40.80,
 '4 reward points per ₹150',
 '₹2,500 gift voucher on joining',
 '["Unlimited domestic lounge access","International lounge visits","Concierge service","Golf access","Premium Kotak card"]',
 TRUE, TRUE, 'Unlimited domestic + 4 international per year', TRUE),

-- ══════════════════════════════════════════════════════════════════
-- INDUSIND BANK (4 cards)
-- ══════════════════════════════════════════════════════════════════
('IndusInd Bank', 'Indulge Credit Card', 'Visa Infinite', 'PREMIUM', 'REWARD_POINTS',
 780, 200000, 21, 65, 'SALARIED,SELF_EMPLOYED',
 50000, 0, 'Annual fee waived permanently after joining', 40.80,
 '1.5 reward points per ₹100',
 'Exclusive welcome gift worth ₹25,000',
 '["Metal card","Unlimited lounge access globally","Dedicated concierge","Luxury hotel benefits","High joining fee - ultra premium"]',
 TRUE, TRUE, 'Unlimited Priority Pass worldwide', TRUE),

('IndusInd Bank', 'EazyDiner IndusInd Bank Platinum Credit Card', 'Mastercard', 'CASHBACK', 'CASHBACK',
 700, 30000, 21, 60, 'SALARIED,SELF_EMPLOYED',
 1999, 1999, 'Spend ₹3,00,000 in a year', 42.00,
 '25% off at 2000+ restaurants via EazyDiner; 2X on dining',
 'EazyDiner Prime membership worth ₹2,500',
 '["Best dining credit card in India","25% off at partner restaurants","EazyDiner Prime membership","Complimentary dining experiences","Good for foodies"]',
 TRUE, TRUE, '2 per quarter domestic', TRUE),

('IndusInd Bank', 'Legend Credit Card', 'Visa Signature', 'TRAVEL', 'REWARD_POINTS',
 740, 50000, 21, 65, 'SALARIED,SELF_EMPLOYED',
 9999, 9999, 'Spend ₹10,00,000 in a year', 40.80,
 '3 reward points per ₹100',
 '₹5,000 gift voucher on activation',
 '["Unlimited domestic lounge","Priority Pass","Golf access","Travel insurance","Relationship manager"]',
 TRUE, TRUE, 'Unlimited domestic + 4 international Priority Pass', TRUE),

('IndusInd Bank', 'Duo Card', 'Visa', 'CASHBACK', 'CASHBACK',
 660, 20000, 21, 60, 'SALARIED,SELF_EMPLOYED',
 0, 0, 'Lifetime free', 42.00,
 '1.5% cashback on all spends; 5% on selected partners',
 'Physical + virtual card combo',
 '["Lifetime free","Comes with virtual + physical card","5% on Swiggy/Zomato","Good everyday card"]',
 TRUE, TRUE, 'None', TRUE),

-- ══════════════════════════════════════════════════════════════════
-- YES BANK (3 cards)
-- ══════════════════════════════════════════════════════════════════
('Yes Bank', 'Yes Preferred Credit Card', 'Mastercard', 'CASHBACK', 'REWARD_POINTS',
 700, 25000, 21, 60, 'SALARIED,SELF_EMPLOYED',
 999, 999, 'Spend ₹2,50,000 in a year', 40.80,
 '12 Yes PayPoints per ₹200; 24 PayPoints on travel/dining',
 '12,000 bonus Yes PayPoints on joining',
 '["Good reward rate on travel and dining","Domestic lounge access","Golf access","Milestone bonuses","Yes Bank relationship benefits"]',
 TRUE, TRUE, '2 per quarter domestic', TRUE),

('Yes Bank', 'Yes Prosperity Reward Plus Credit Card', 'Mastercard', 'ENTRY_LEVEL', 'REWARD_POINTS',
 660, 15000, 21, 60, 'SALARIED,SELF_EMPLOYED',
 399, 399, 'Spend ₹75,000 in a year', 42.00,
 '8 Yes PayPoints per ₹200',
 '5,000 bonus Yes PayPoints',
 '["Low annual fee","Good points earn rate","Fuel surcharge waiver","Movie benefits","Entry level Yes Bank card"]',
 TRUE, TRUE, 'None', TRUE),

('Yes Bank', 'Yes First Exclusive Credit Card', 'Visa Infinite', 'PREMIUM', 'REWARD_POINTS',
 780, 200000, 21, 65, 'SALARIED,SELF_EMPLOYED',
 9999, 9999, 'Spend ₹12,00,000 in a year', 40.80,
 '18 Yes PayPoints per ₹200; 54 PayPoints on international',
 '18,000 bonus PayPoints + exclusive welcome kit',
 '["Unlimited Priority Pass globally","54X points on international","Dedicated RM","Luxury hotel benefits","Top Yes Bank card"]',
 TRUE, TRUE, 'Unlimited Priority Pass worldwide', TRUE),

-- ══════════════════════════════════════════════════════════════════
-- STANDARD CHARTERED (3 cards)
-- ══════════════════════════════════════════════════════════════════
('Standard Chartered', 'Smart Credit Card', 'Mastercard', 'CASHBACK', 'CASHBACK',
 700, 25000, 21, 65, 'SALARIED,SELF_EMPLOYED',
 499, 499, 'Spend ₹1,00,000 in a year', 40.50,
 '2% cashback on online; 1% offline; up to 5% on partner',
 'None — low fee card',
 '["Good online cashback rate","No minimum redemption threshold","Monthly cashback credit","Good for digital spenders"]',
 TRUE, TRUE, 'None', TRUE),

('Standard Chartered', 'Rewards Credit Card', 'Visa', 'CASHBACK', 'REWARD_POINTS',
 720, 30000, 21, 65, 'SALARIED,SELF_EMPLOYED',
 1000, 1000, 'Spend ₹1,50,000 in a year', 40.50,
 '3 reward points per ₹150; 10X on partner merchants',
 '1,000 bonus reward points',
 '["10X on partner merchants","Domestic lounge access","Fuel surcharge waiver","Dining discounts","Good everyday card"]',
 TRUE, TRUE, '2 per quarter domestic', TRUE),

('Standard Chartered', 'Ultimate Credit Card', 'Mastercard World', 'PREMIUM', 'REWARD_POINTS',
 780, 150000, 21, 65, 'SALARIED,SELF_EMPLOYED',
 5000, 5000, 'Spend ₹5,00,000 in a year', 40.50,
 '5 reward points per ₹150; 10X on travel partners',
 '5,000 bonus reward points worth ₹2,500',
 '["Priority Pass membership","Travel insurance ₹1Cr","Golf privileges","Concierge service","Good premium SC card"]',
 TRUE, TRUE, 'Unlimited domestic + 4 international Priority Pass', TRUE),

-- ══════════════════════════════════════════════════════════════════
-- AMERICAN EXPRESS (3 cards)
-- ══════════════════════════════════════════════════════════════════
('American Express', 'SmartEarn Credit Card', 'American Express', 'CASHBACK', 'REWARD_POINTS',
 700, 25000, 18, 65, 'SALARIED,SELF_EMPLOYED',
 495, 495, 'Spend ₹40,000 in a year', 42.00,
 '10X Membership Rewards on Flipkart/Amazon; 5X on Paytm',
 '₹500 cashback on ₹5,000 first spend',
 '["10X on Amazon and Flipkart","Good for online shopping","Amex customer service","Points transfer to airline miles","Good entry Amex card"]',
 TRUE, TRUE, 'None', TRUE),

('American Express', 'Gold Card', 'American Express', 'TRAVEL', 'REWARD_POINTS',
 750, 60000, 18, 65, 'SALARIED,SELF_EMPLOYED',
 1000, 4500, 'Spend ₹5,00,000 in a year', 42.00,
 '5 Membership Rewards per ₹50 on partner merchants',
 '2,000 bonus Membership Rewards Points',
 '["Best-in-class customer service","Membership Rewards points","Travel partner transfers","Dining programs","Amex global acceptance"]',
 TRUE, TRUE, '4 per year domestic', TRUE),

('American Express', 'Platinum Card', 'American Express', 'PREMIUM', 'REWARD_POINTS',
 820, 400000, 18, 65, 'SALARIED,SELF_EMPLOYED',
 60000, 60000, 'Non-waivable premium card', 42.00,
 '5 Membership Rewards per ₹50',
 'Extensive welcome gift package worth ₹30,000+',
 '["Ultimate luxury card","Unlimited access to 1300+ airport lounges globally","Fine Dining program","Amex FHR hotel benefits","Dedicated 24/7 concierge","Annual fee non-waivable - true ultra premium"]',
 TRUE, TRUE, 'Unlimited worldwide including Amex Centurion lounges', TRUE),

-- ══════════════════════════════════════════════════════════════════
-- OTHER BANKS (remaining to reach 50+)
-- ══════════════════════════════════════════════════════════════════
('RBL Bank', 'Shoprite Credit Card', 'Mastercard', 'CASHBACK', 'CASHBACK',
 680, 15000, 21, 65, 'SALARIED,SELF_EMPLOYED',
 0, 0, 'Lifetime free', 42.00,
 '5% cashback at grocery stores; 2% on others',
 'None — lifetime free',
 '["Lifetime free","5% on groceries","Good for families","Low income threshold","Easy approval"]',
 TRUE, TRUE, 'None', TRUE),

('RBL Bank', 'World Safari Credit Card', 'Mastercard World', 'TRAVEL', 'AIRLINE_MILES',
 740, 50000, 21, 65, 'SALARIED,SELF_EMPLOYED',
 3000, 3000, 'Spend ₹3,50,000 in a year', 42.00,
 '5 Travel Points per ₹100; 10X on foreign currency',
 '5,000 bonus travel points',
 '["10X on international spends","No foreign currency markup","Airport lounge access","Travel insurance","Good international card"]',
 TRUE, TRUE, '4 per quarter domestic + 2 international Priority Pass', TRUE),

('AU Small Finance Bank', 'Altura Credit Card', 'Mastercard', 'CASHBACK', 'CASHBACK',
 650, 15000, 21, 60, 'SALARIED,SELF_EMPLOYED',
 0, 0, 'Lifetime free', 42.00,
 '2% cashback on utility bills; 1.5% on others',
 'None — lifetime free',
 '["Lifetime free","Good for utility bill payments","Low income threshold","Easy approval for AU SFB customers"]',
 TRUE, TRUE, 'None', TRUE),

('Federal Bank', 'Federal Bank Scapia Credit Card', 'Visa', 'TRAVEL', 'REWARD_POINTS',
 700, 25000, 21, 60, 'SALARIED,SELF_EMPLOYED',
 0, 0, 'Lifetime free', 42.00,
 '10% off on all travel bookings via Scapia app',
 'Welcome travel vouchers',
 '["Lifetime free travel card","10% off all flights via Scapia","Unlimited lounge access","Zero forex markup","Best value travel card under ₹0 fee"]',
 TRUE, TRUE, 'Unlimited domestic lounge', TRUE),

('IDFC First Bank', 'IDFC First Wealth Credit Card', 'Visa', 'PREMIUM', 'REWARD_POINTS',
 730, 100000, 21, 65, 'SALARIED,SELF_EMPLOYED',
 0, 0, 'Lifetime free - premium card with no fee', 42.00,
 '6X points on online; 3X on offline; 10X on travel',
 '₹500 cashback on first transaction',
 '["Lifetime FREE premium card - unique in India","6X on online spends","10X on travel bookings","Airport lounge access","Best value premium card in India"]',
 TRUE, TRUE, '4 per quarter domestic', TRUE),

('IDFC First Bank', 'IDFC First Select Credit Card', 'Visa', 'CASHBACK', 'REWARD_POINTS',
 700, 25000, 21, 65, 'SALARIED,SELF_EMPLOYED',
 0, 0, 'Lifetime free', 42.00,
 '3X on online; 1.5X offline; 10X on partner merchants',
 '₹250 cashback on first spend',
 '["Lifetime free","Zero forex markup","Good reward rate","Easy approval","Good everyday card"]',
 TRUE, TRUE, '2 per quarter domestic', TRUE),

('OneCard', 'OneCard Metal Credit Card', 'Visa', 'CASHBACK', 'REWARD_POINTS',
 700, 25000, 21, 60, 'SALARIED,SELF_EMPLOYED',
 0, 0, 'Lifetime free', 42.00,
 '5X on top 2 spend categories each month',
 'Stainless steel metal card',
 '["Metal card at zero fee","5X on your top 2 categories","App-first card management","Good for tech-savvy users","No forex markup"]',
 TRUE, TRUE, 'None', TRUE),

('Citi Bank', 'Citi Cashback Credit Card', 'Mastercard', 'CASHBACK', 'CASHBACK',
 720, 25000, 23, 65, 'SALARIED,SELF_EMPLOYED',
 500, 500, 'Spend ₹30,000 per month', 42.00,
 '5% on movie tickets; 5% on utility bills; 0.5% on others',
 '₹500 cashback voucher',
 '["5% on movies and utilities","Monthly cashback credit","Auto-redemption","Good for recurring bill payments"]',
 TRUE, TRUE, 'None', TRUE),

('HSBC Bank', 'HSBC Cashback Credit Card', 'Visa', 'CASHBACK', 'CASHBACK',
 720, 25000, 21, 65, 'SALARIED,SELF_EMPLOYED',
 750, 750, 'Spend ₹2,00,000 in a year', 42.00,
 '10% cashback on Swiggy/Zomato/BigBasket; 1.5% on others',
 '₹750 Swiggy voucher on first transaction',
 '["10% on food delivery apps","Good for online food ordering","International acceptance","HSBC banking relationship benefits"]',
 TRUE, TRUE, 'None', TRUE),

('Bank of Baroda', 'BoB Premier Credit Card', 'Visa Signature', 'TRAVEL', 'REWARD_POINTS',
 730, 60000, 21, 65, 'SALARIED,GOVERNMENT,PSU',
 2500, 2500, 'Spend ₹3,00,000 in a year', 42.00,
 '4 reward points per ₹100; 10X on international',
 '2,500 bonus reward points',
 '["Good for government employees","Priority Pass access","Travel insurance","Dining discounts","BoB relationship benefits"]',
 TRUE, TRUE, '4 per quarter domestic + 2 international', TRUE);

-- Final count check
-- SELECT COUNT(*) FROM credit_cards;   -- should be 52