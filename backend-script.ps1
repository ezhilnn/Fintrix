# ============================================================
# fintrix - Spring Boot Backend Scaffold
# Run from your workspace root: .\scaffold-backend.ps1
# ============================================================

$base = "fintrix-api\src\main\java\com\fintrix"
$res  = "fintrix-api\src\main\resources"
$test = "fintrix-api\src\test\java\com\fintrix"

# ── Root Maven wrapper files ─────────────────────────────────
New-Item -ItemType Directory -Force -Path "fintrix-api"
New-Item -ItemType File -Force -Path "fintrix-api\pom.xml"
New-Item -ItemType File -Force -Path "fintrix-api\.gitignore"
New-Item -ItemType File -Force -Path "fintrix-api\README.md"

# ── Resources ────────────────────────────────────────────────
New-Item -ItemType Directory -Force -Path $res
New-Item -ItemType File -Force -Path "$res\application.yml"
New-Item -ItemType File -Force -Path "$res\application-dev.yml"
New-Item -ItemType File -Force -Path "$res\application-prod.yml"

# ── Main entry point ─────────────────────────────────────────
New-Item -ItemType Directory -Force -Path $base
New-Item -ItemType File -Force -Path "$base\fintrixApplication.java"

# ── Config ───────────────────────────────────────────────────
$config = "$base\config"
New-Item -ItemType Directory -Force -Path $config
New-Item -ItemType File -Force -Path "$config\DatabaseConfig.java"
New-Item -ItemType File -Force -Path "$config\RedisConfig.java"
New-Item -ItemType File -Force -Path "$config\SecurityConfig.java"
New-Item -ItemType File -Force -Path "$config\CorsConfig.java"
New-Item -ItemType File -Force -Path "$config\SchedulerConfig.java"

# ── Security (OAuth2 + JWT) ───────────────────────────────────
$sec = "$base\security"
New-Item -ItemType Directory -Force -Path $sec
New-Item -ItemType File -Force -Path "$sec\JwtTokenProvider.java"
New-Item -ItemType File -Force -Path "$sec\JwtAuthenticationFilter.java"
New-Item -ItemType File -Force -Path "$sec\OAuth2UserService.java"
New-Item -ItemType File -Force -Path "$sec\OAuth2AuthenticationSuccessHandler.java"
New-Item -ItemType File -Force -Path "$sec\UserPrincipal.java"

# ── Shared / Common ───────────────────────────────────────────
$common = "$base\common"
New-Item -ItemType Directory -Force -Path "$common\exception"
New-Item -ItemType Directory -Force -Path "$common\response"
New-Item -ItemType Directory -Force -Path "$common\utils"
New-Item -ItemType File -Force -Path "$common\exception\GlobalExceptionHandler.java"
New-Item -ItemType File -Force -Path "$common\exception\ResourceNotFoundException.java"
New-Item -ItemType File -Force -Path "$common\exception\ValidationException.java"
New-Item -ItemType File -Force -Path "$common\response\ApiResponse.java"
New-Item -ItemType File -Force -Path "$common\response\PagedResponse.java"
New-Item -ItemType File -Force -Path "$common\utils\DateUtils.java"
New-Item -ItemType File -Force -Path "$common\utils\MathUtils.java"

# ── Infrastructure ────────────────────────────────────────────
$infra = "$base\infrastructure"
New-Item -ItemType Directory -Force -Path "$infra\cache"
New-Item -ItemType Directory -Force -Path "$infra\persistence"
New-Item -ItemType File -Force -Path "$infra\cache\CacheService.java"
New-Item -ItemType File -Force -Path "$infra\cache\CacheKeys.java"
New-Item -ItemType File -Force -Path "$infra\persistence\AuditableEntity.java"

# ── Background Jobs ───────────────────────────────────────────
$jobs = "$base\jobs"
New-Item -ItemType Directory -Force -Path $jobs
New-Item -ItemType File -Force -Path "$jobs\EmiReminderJob.java"
New-Item -ItemType File -Force -Path "$jobs\CreditScoreRefreshJob.java"
New-Item -ItemType File -Force -Path "$jobs\FinancialHealthRecalcJob.java"

# ── MODULE: User ──────────────────────────────────────────────
$user = "$base\modules\user"
New-Item -ItemType Directory -Force -Path "$user\controller"
New-Item -ItemType Directory -Force -Path "$user\service"
New-Item -ItemType Directory -Force -Path "$user\domain"
New-Item -ItemType Directory -Force -Path "$user\repository"
New-Item -ItemType Directory -Force -Path "$user\dto"
New-Item -ItemType File -Force -Path "$user\controller\UserController.java"
New-Item -ItemType File -Force -Path "$user\controller\AuthController.java"
New-Item -ItemType File -Force -Path "$user\service\UserService.java"
New-Item -ItemType File -Force -Path "$user\service\UserServiceImpl.java"
New-Item -ItemType File -Force -Path "$user\domain\User.java"
New-Item -ItemType File -Force -Path "$user\domain\UserRole.java"
New-Item -ItemType File -Force -Path "$user\repository\UserRepository.java"
New-Item -ItemType File -Force -Path "$user\dto\UserProfileRequest.java"
New-Item -ItemType File -Force -Path "$user\dto\UserProfileResponse.java"
New-Item -ItemType File -Force -Path "$user\dto\AuthResponse.java"

# ── MODULE: Financial Profile ─────────────────────────────────
$fp = "$base\modules\financialprofile"
New-Item -ItemType Directory -Force -Path "$fp\controller"
New-Item -ItemType Directory -Force -Path "$fp\service"
New-Item -ItemType Directory -Force -Path "$fp\domain"
New-Item -ItemType Directory -Force -Path "$fp\repository"
New-Item -ItemType Directory -Force -Path "$fp\dto"
New-Item -ItemType File -Force -Path "$fp\controller\FinancialProfileController.java"
New-Item -ItemType File -Force -Path "$fp\service\FinancialProfileService.java"
New-Item -ItemType File -Force -Path "$fp\service\FinancialProfileServiceImpl.java"
New-Item -ItemType File -Force -Path "$fp\domain\FinancialProfile.java"
New-Item -ItemType File -Force -Path "$fp\domain\EmploymentType.java"
New-Item -ItemType File -Force -Path "$fp\repository\FinancialProfileRepository.java"
New-Item -ItemType File -Force -Path "$fp\dto\FinancialProfileRequest.java"
New-Item -ItemType File -Force -Path "$fp\dto\FinancialProfileResponse.java"

# ── MODULE: Loan ──────────────────────────────────────────────
$loan = "$base\modules\loan"
New-Item -ItemType Directory -Force -Path "$loan\controller"
New-Item -ItemType Directory -Force -Path "$loan\service"
New-Item -ItemType Directory -Force -Path "$loan\domain"
New-Item -ItemType Directory -Force -Path "$loan\repository"
New-Item -ItemType Directory -Force -Path "$loan\dto"
New-Item -ItemType Directory -Force -Path "$loan\rules"
New-Item -ItemType File -Force -Path "$loan\controller\LoanController.java"
New-Item -ItemType File -Force -Path "$loan\service\LoanEligibilityService.java"
New-Item -ItemType File -Force -Path "$loan\service\LoanEligibilityServiceImpl.java"
New-Item -ItemType File -Force -Path "$loan\domain\Loan.java"
New-Item -ItemType File -Force -Path "$loan\domain\LoanType.java"
New-Item -ItemType File -Force -Path "$loan\domain\Lender.java"
New-Item -ItemType File -Force -Path "$loan\repository\LoanRepository.java"
New-Item -ItemType File -Force -Path "$loan\repository\LenderRepository.java"
New-Item -ItemType File -Force -Path "$loan\dto\LoanEligibilityRequest.java"
New-Item -ItemType File -Force -Path "$loan\dto\LoanEligibilityResponse.java"
New-Item -ItemType File -Force -Path "$loan\rules\LoanEligibilityRule.java"
New-Item -ItemType File -Force -Path "$loan\rules\FoirRule.java"
New-Item -ItemType File -Force -Path "$loan\rules\CreditScoreRule.java"
New-Item -ItemType File -Force -Path "$loan\rules\LoanRuleEngine.java"

# ── MODULE: Credit Card ───────────────────────────────────────
$cc = "$base\modules\creditcard"
New-Item -ItemType Directory -Force -Path "$cc\controller"
New-Item -ItemType Directory -Force -Path "$cc\service"
New-Item -ItemType Directory -Force -Path "$cc\domain"
New-Item -ItemType Directory -Force -Path "$cc\repository"
New-Item -ItemType Directory -Force -Path "$cc\dto"
New-Item -ItemType Directory -Force -Path "$cc\rules"
New-Item -ItemType File -Force -Path "$cc\controller\CreditCardController.java"
New-Item -ItemType File -Force -Path "$cc\service\CreditCardRecommendationService.java"
New-Item -ItemType File -Force -Path "$cc\service\CreditCardRecommendationServiceImpl.java"
New-Item -ItemType File -Force -Path "$cc\domain\CreditCard.java"
New-Item -ItemType File -Force -Path "$cc\domain\CardCategory.java"
New-Item -ItemType File -Force -Path "$cc\domain\RewardType.java"
New-Item -ItemType File -Force -Path "$cc\repository\CreditCardRepository.java"
New-Item -ItemType File -Force -Path "$cc\dto\CardRecommendationRequest.java"
New-Item -ItemType File -Force -Path "$cc\dto\CardRecommendationResponse.java"
New-Item -ItemType File -Force -Path "$cc\rules\CardEligibilityRule.java"
New-Item -ItemType File -Force -Path "$cc\rules\CardRecommendationEngine.java"

# ── MODULE: Decision Engine ───────────────────────────────────
$de = "$base\modules\decisionengine"
New-Item -ItemType Directory -Force -Path "$de\controller"
New-Item -ItemType Directory -Force -Path "$de\service"
New-Item -ItemType Directory -Force -Path "$de\domain"
New-Item -ItemType Directory -Force -Path "$de\scoring"
New-Item -ItemType File -Force -Path "$de\controller\FinancialHealthController.java"
New-Item -ItemType File -Force -Path "$de\service\FinancialHealthService.java"
New-Item -ItemType File -Force -Path "$de\service\FinancialHealthServiceImpl.java"
New-Item -ItemType File -Force -Path "$de\domain\FinancialHealthScore.java"
New-Item -ItemType File -Force -Path "$de\domain\RiskLevel.java"
New-Item -ItemType File -Force -Path "$de\scoring\ScoreCalculator.java"
New-Item -ItemType File -Force -Path "$de\scoring\DebtToIncomeAnalyzer.java"
New-Item -ItemType File -Force -Path "$de\scoring\CreditUtilizationAnalyzer.java"
New-Item -ItemType File -Force -Path "$de\scoring\SavingsRateAnalyzer.java"

# ── MODULE: Fraud Detection ───────────────────────────────────
$fraud = "$base\modules\fraud"
New-Item -ItemType Directory -Force -Path "$fraud\controller"
New-Item -ItemType Directory -Force -Path "$fraud\service"
New-Item -ItemType Directory -Force -Path "$fraud\domain"
New-Item -ItemType Directory -Force -Path "$fraud\rules"
New-Item -ItemType File -Force -Path "$fraud\controller\FraudCheckController.java"
New-Item -ItemType File -Force -Path "$fraud\service\FraudDetectionService.java"
New-Item -ItemType File -Force -Path "$fraud\service\FraudDetectionServiceImpl.java"
New-Item -ItemType File -Force -Path "$fraud\domain\FraudAlert.java"
New-Item -ItemType File -Force -Path "$fraud\domain\AlertSeverity.java"
New-Item -ItemType File -Force -Path "$fraud\rules\SebiRegistrationRule.java"
New-Item -ItemType File -Force -Path "$fraud\rules\RbiNbfcRule.java"
New-Item -ItemType File -Force -Path "$fraud\rules\FraudRuleEngine.java"

# ── MODULE: BFF (Backend for Frontend) ───────────────────────
$bff = "$base\modules\bff"
New-Item -ItemType Directory -Force -Path "$bff\controller"
New-Item -ItemType Directory -Force -Path "$bff\service"
New-Item -ItemType Directory -Force -Path "$bff\dto"
New-Item -ItemType File -Force -Path "$bff\controller\DashboardController.java"
New-Item -ItemType File -Force -Path "$bff\service\DashboardAggregatorService.java"
New-Item -ItemType File -Force -Path "$bff\dto\DashboardResponse.java"

# ── DB Migrations (Flyway) ────────────────────────────────────
$fly = "fintrix-api\src\main\resources\db\migration"
New-Item -ItemType Directory -Force -Path $fly
New-Item -ItemType File -Force -Path "$fly\V1__create_users_table.sql"
New-Item -ItemType File -Force -Path "$fly\V2__create_financial_profiles_table.sql"
New-Item -ItemType File -Force -Path "$fly\V3__create_loans_lenders_table.sql"
New-Item -ItemType File -Force -Path "$fly\V4__create_credit_cards_table.sql"
New-Item -ItemType File -Force -Path "$fly\V5__create_financial_health_scores_table.sql"
New-Item -ItemType File -Force -Path "$fly\V6__create_fraud_alerts_table.sql"
New-Item -ItemType File -Force -Path "$fly\V7__seed_lenders_data.sql"
New-Item -ItemType File -Force -Path "$fly\V8__seed_credit_cards_data.sql"

# ── Tests ─────────────────────────────────────────────────────
New-Item -ItemType Directory -Force -Path "$test\modules\user"
New-Item -ItemType Directory -Force -Path "$test\modules\loan"
New-Item -ItemType Directory -Force -Path "$test\modules\decisionengine"
New-Item -ItemType File -Force -Path "$test\modules\user\UserServiceTest.java"
New-Item -ItemType File -Force -Path "$test\modules\loan\LoanEligibilityServiceTest.java"
New-Item -ItemType File -Force -Path "$test\modules\decisionengine\ScoreCalculatorTest.java"

Write-Host ""
Write-Host "Backend scaffold complete!" -ForegroundColor Green
Write-Host "Open 'fintrix-api' in IntelliJ IDEA or VS Code" -ForegroundColor Cyan
Write-Host "Next: paste pom.xml content, then run mvn clean install" -ForegroundColor Yellow