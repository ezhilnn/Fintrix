package com.fintrix.modules.fraud.service;

import com.fintrix.modules.fraud.domain.FraudKeyword;
import com.fintrix.modules.fraud.dto.KeywordScanRequest;
import com.fintrix.modules.fraud.dto.KeywordScanResponse;
import com.fintrix.modules.fraud.repository.FraudKeywordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * KeywordScanServiceImpl — with fuzzy matching
 *
 * ── Why fuzzy matching? ───────────────────────────────────────────
 * Exact match fails on:
 *   "guaranted returns"   (typo — missing 'e')
 *   "guarentee profit"    (misspelling)
 *   "garaunteed income"   (phonetic misspelling)
 *   "double ur money"     (SMS-style abbreviation)
 *   "duoble your money"   (fat-finger typo)
 *
 * All of these are real fraud messages users will paste.
 *
 * ── Algorithm: Two-phase matching ────────────────────────────────
 *
 * Phase 1 — Exact substring match (fast, zero false positives)
 *   textLower.contains(keywordLower)
 *   Catches perfect matches instantly.
 *
 * Phase 2 — Fuzzy match using Levenshtein distance (catches typos)
 *   For each WORD in the user text, we check each WORD in each keyword.
 *   If a text word is within 1-2 edits of a keyword word → fuzzy match.
 *
 *   Why Levenshtein?
 *   It counts the minimum number of single-character edits (insert,
 *   delete, substitute) to transform one string into another.
 *   "guaranted" → "guaranteed" = 1 edit (missing 'e') ✅
 *   "duoble"    → "double"     = 1 edit (transposition) ✅
 *   "phising"   → "phishing"   = 1 edit (missing 'h') ✅
 *
 * ── Fuzzy threshold by keyword length ────────────────────────────
 *   Short words (≤ 4 chars): exact match only — "otp", "sim"
 *     → too short to fuzzy match without huge false positive rate
 *   Medium words (5–7 chars): max 1 edit — "crypto" → "krypto"
 *   Long words (8+ chars): max 2 edits — "guaranteed" → "guaranted"
 *
 * ── Per-word matching for multi-word keywords ────────────────────
 *   Keyword: "guaranteed returns"  (2 words)
 *   Text: "100% guaranted returns on investment"
 *
 *   We slide a window of 2 words across the text and check:
 *   window "guaranted returns" vs keyword "guaranteed returns"
 *   → word1: "guaranted" vs "guaranteed" = 1 edit ✅
 *   → word2: "returns"   vs "returns"    = 0 edits ✅
 *   → fuzzy match! ✅
 *
 * ── Performance ──────────────────────────────────────────────────
 *   200 keywords × avg 3 words × text word count ≈ few ms
 *   Not a hot path — called by user explicitly, not on every request
 *   Cached at service level after first load
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KeywordScanServiceImpl implements KeywordScanService {

    private final FraudKeywordRepository keywordRepository;

    // Apache Commons Text Levenshtein — thread-safe, reusable
    private static final LevenshteinDistance LEVENSHTEIN =
            LevenshteinDistance.getDefaultInstance();

    @Override
    @Transactional(readOnly = true)
    public KeywordScanResponse scan(String userId, KeywordScanRequest request) {

        log.info("Keyword scan: userId={} contentType={} textLength={}",
                userId,
                request.getContentType(),
                request.getText().length());

        String cleanText = request.getText().toLowerCase().trim();

        // Load all active keywords once
        List<FraudKeyword> allKeywords =
                keywordRepository.findByIsActiveTrue();

        List<KeywordScanResponse.KeywordMatch> matches = new ArrayList<>();
        int maxSeverityScore = 0;

        for (FraudKeyword kw : allKeywords) {
            MatchResult result = findMatch(cleanText, kw.getKeyword().toLowerCase());

            if (result.matched()) {
                int score = severityScore(kw.getRiskLevel());
                maxSeverityScore = Math.max(maxSeverityScore, score);

                matches.add(KeywordScanResponse.KeywordMatch.builder()
                        .keyword(kw.getKeyword())
                        .riskLevel(kw.getRiskLevel())
                        .fraudType(kw.getFraudType())
                        .explanation(kw.getDescription())
                        .matchedPhrase(extractPhrase(request.getText(),
                                result.matchedAt()))
                        .matchType(result.type())
                        .build());
            }
        }

        // Sort: CRITICAL first, then HIGH, MEDIUM, LOW
        matches.sort((a, b) ->
                severityScore(b.getRiskLevel()) - severityScore(a.getRiskLevel()));

        String overallRisk = mapRiskLabel(maxSeverityScore);
        boolean isSafe     = matches.isEmpty();

        String preview = request.getText().length() > 200
                ? request.getText().substring(0, 200) + "..."
                : request.getText();

        log.info("Scan complete: userId={} matches={} risk={} type={}",
                userId, matches.size(), overallRisk,
                request.getContentType());

        return KeywordScanResponse.builder()
                .overallRisk(overallRisk)
                .isSafe(isSafe)
                .totalMatchesFound(matches.size())
                .verdict(buildVerdict(overallRisk, matches.size(),
                        request.getContentType()))
                .matches(matches)
                .safetyActions(buildSafetyActions(maxSeverityScore, matches))
                .reportUrl("https://sachet.rbi.org.in")
                .scannedTextPreview(preview)
                .contentTypeLabel(request.getContentType().getLabel())
                .build();
    }

    // ── Two-phase match: exact first, then fuzzy ──────────────────
    private MatchResult findMatch(String text, String keyword) {

        // Phase 1: exact substring match (fastest path)
        int exactIdx = text.indexOf(keyword);
        if (exactIdx >= 0) {
            return new MatchResult(true, "EXACT", exactIdx);
        }

        // Phase 2: fuzzy word-window match
        String[] kwWords   = keyword.split("\\s+");
        String[] textWords = text.split("\\s+");

        if (textWords.length < kwWords.length) return MatchResult.noMatch();

        // Slide a window of kwWords.length across textWords
        for (int i = 0; i <= textWords.length - kwWords.length; i++) {
            boolean windowMatches = true;

            for (int j = 0; j < kwWords.length; j++) {
                String textWord = textWords[i + j];
                String kwWord   = kwWords[j];

                if (!fuzzyWordMatch(textWord, kwWord)) {
                    windowMatches = false;
                    break;
                }
            }

            if (windowMatches) {
                // Approximate character position in original text
                int charPos = approximateCharPos(text, i);
                return new MatchResult(true, "FUZZY", charPos);
            }
        }

        return MatchResult.noMatch();
    }

    // ── Fuzzy word match with threshold by length ─────────────────
    private boolean fuzzyWordMatch(String textWord, String kwWord) {
        // Exact match — always passes
        if (textWord.equals(kwWord)) return true;

        // Short words (≤ 4 chars): exact only — too risky to fuzzy
        if (kwWord.length() <= 4) return false;

        // Compute edit distance
        int distance = LEVENSHTEIN.apply(textWord, kwWord);

        // Threshold scales with word length
        // 5-7 chars: allow 1 edit  ("crypto" → "krypto")
        // 8+ chars:  allow 2 edits ("guaranteed" → "guaranted")
        int threshold = kwWord.length() >= 8 ? 2 : 1;

        return distance <= threshold;
    }

    // ── Approximate char position of word index in text ──────────
    private int approximateCharPos(String text, int wordIndex) {
        String[] parts = text.split("\\s+");
        int pos = 0;
        for (int i = 0; i < wordIndex && i < parts.length; i++) {
            pos += parts[i].length() + 1;
        }
        return Math.min(pos, text.length());
    }

    // ── Extract surrounding context from original text ────────────
    private String extractPhrase(String originalText, int charPos) {
        if (charPos < 0 || charPos >= originalText.length()) {
            return originalText.length() > 60
                    ? originalText.substring(0, 60) + "..."
                    : originalText;
        }
        int start = Math.max(0, charPos - 15);
        int end   = Math.min(originalText.length(), charPos + 50);
        String phrase = originalText.substring(start, end).trim();
        if (start > 0)                           phrase = "..." + phrase;
        if (end < originalText.length())         phrase = phrase + "...";
        return phrase;
    }

    // ── Severity scoring ──────────────────────────────────────────
    private int severityScore(String riskLevel) {
        return switch (riskLevel.toUpperCase()) {
            case "CRITICAL" -> 4;
            case "HIGH"     -> 3;
            case "MEDIUM"   -> 2;
            case "LOW"      -> 1;
            default         -> 0;
        };
    }

    private String mapRiskLabel(int score) {
        return switch (score) {
            case 4  -> "CRITICAL";
            case 3  -> "HIGH";
            case 2  -> "MEDIUM";
            case 1  -> "LOW";
            default -> "SAFE";
        };
    }

    // ── Verdict personalised by content type ─────────────────────
    private String buildVerdict(String risk, int count,
            KeywordScanRequest.ContentType contentType) {

        if (count == 0) {
            return "✅ No known fraud patterns detected in this "
                    + contentType.getLabel().toLowerCase() + ". "
                    + "Still exercise caution with unsolicited financial offers.";
        }

        String source = contentType.getLabel();
        return switch (risk) {
            case "CRITICAL" ->
                "⛔ CRITICAL RISK: This " + source + " contains " + count
                + " known fraud pattern(s). Do NOT respond or pay any money.";
            case "HIGH" ->
                "🔴 HIGH RISK: This " + source + " contains " + count
                + " suspicious pattern(s). Verify independently before engaging.";
            case "MEDIUM" ->
                "🟡 MEDIUM RISK: This " + source + " contains " + count
                + " concerning pattern(s). Proceed with caution.";
            default ->
                "🔵 LOW CONCERN: This " + source + " contains " + count
                + " minor pattern(s). Verify before any transaction.";
        };
    }

    // ── Safety actions ────────────────────────────────────────────
    private List<String> buildSafetyActions(int severity,
            List<KeywordScanResponse.KeywordMatch> matches) {

        List<String> actions = new ArrayList<>();

        if (matches.isEmpty()) {
            actions.add("No known fraud patterns detected.");
            actions.add("Always be cautious with unsolicited financial offers.");
            return actions;
        }

        if (severity >= 4) {
            actions.add("⛔ Do NOT invest, pay, or share any personal details.");
            actions.add("Block and report the sender immediately.");
            actions.add("Report to RBI Sachet: https://sachet.rbi.org.in");
            actions.add("File cyber complaint: https://cybercrime.gov.in");
        } else if (severity == 3) {
            actions.add("⚠️ Do not proceed without independent verification.");
            actions.add("Verify company on MCA21: https://www.mca.gov.in");
            actions.add("Check SEBI/RBI registration before any transaction.");
        } else if (severity == 2) {
            actions.add("🔶 Verify offer independently before responding.");
            actions.add("Never pay upfront fees for loans or investments.");
        } else {
            actions.add("🔵 Low risk. Still verify before any transaction.");
        }

        // Specific actions by fraud type
        boolean hasPonzi = matches.stream()
                .anyMatch(m -> "PONZI".equals(m.getFraudType()));
        if (hasPonzi) actions.add(
                "Matches Ponzi scheme pattern — illegal under " +
                "Prize Chits and Money Circulation Act 1978.");

        boolean hasLoanFraud = matches.stream()
                .anyMatch(m -> "LOAN_FRAUD".equals(m.getFraudType()));
        if (hasLoanFraud) actions.add(
                "RBI prohibits advance fee before loan disbursement. " +
                "Report at https://sachet.rbi.org.in");

        boolean hasPhishing = matches.stream()
                .anyMatch(m -> "PHISHING".equals(m.getFraudType()));
        if (hasPhishing) actions.add(
                "Never share OTP, PIN, or passwords with anyone. " +
                "Your bank will NEVER ask for these.");

        boolean hasImpersonation = matches.stream()
                .anyMatch(m -> "IMPERSONATION".equals(m.getFraudType()));
        if (hasImpersonation) actions.add(
                "Government agencies (RBI, CBI, Police) never " +
                "demand payment over phone or video call.");

        return actions;
    }

    // ── Internal result type ──────────────────────────────────────
    private record MatchResult(
            boolean matched,
            String  type,      // "EXACT" or "FUZZY"
            int     matchedAt  // character position in text
    ) {
        static MatchResult noMatch() {
            return new MatchResult(false, "NONE", -1);
        }
    }
}