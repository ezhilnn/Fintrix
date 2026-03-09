# ============================================================
# fintrix - React TypeScript Frontend Scaffold
# Run from your workspace root: .\scaffold-frontend.ps1
# Assumes you already ran: npx create-react-app fintrix-web --template typescript
# OR: npm create vite@latest fintrix-web -- --template react-ts
# ============================================================

$src = "fintrix-web\src"

# ── Types (TypeScript interfaces) ────────────────────────────
$types = "$src\types"
New-Item -ItemType Directory -Force -Path $types
New-Item -ItemType File -Force -Path "$types\user.types.ts"
New-Item -ItemType File -Force -Path "$types\financialProfile.types.ts"
New-Item -ItemType File -Force -Path "$types\loan.types.ts"
New-Item -ItemType File -Force -Path "$types\creditCard.types.ts"
New-Item -ItemType File -Force -Path "$types\dashboard.types.ts"
New-Item -ItemType File -Force -Path "$types\fraud.types.ts"
New-Item -ItemType File -Force -Path "$types\api.types.ts"

# ── Services (BFF API calls) ──────────────────────────────────
$services = "$src\services"
New-Item -ItemType Directory -Force -Path $services
New-Item -ItemType File -Force -Path "$services\api.client.ts"
New-Item -ItemType File -Force -Path "$services\auth.service.ts"
New-Item -ItemType File -Force -Path "$services\user.service.ts"
New-Item -ItemType File -Force -Path "$services\financialProfile.service.ts"
New-Item -ItemType File -Force -Path "$services\loan.service.ts"
New-Item -ItemType File -Force -Path "$services\creditCard.service.ts"
New-Item -ItemType File -Force -Path "$services\dashboard.service.ts"
New-Item -ItemType File -Force -Path "$services\fraud.service.ts"

# ── Store (Zustand state management) ─────────────────────────
$store = "$src\store"
New-Item -ItemType Directory -Force -Path $store
New-Item -ItemType File -Force -Path "$store\authStore.ts"
New-Item -ItemType File -Force -Path "$store\userStore.ts"
New-Item -ItemType File -Force -Path "$store\financialProfileStore.ts"
New-Item -ItemType File -Force -Path "$store\dashboardStore.ts"

# ── Hooks (Reusable logic) ────────────────────────────────────
$hooks = "$src\hooks"
New-Item -ItemType Directory -Force -Path $hooks
New-Item -ItemType File -Force -Path "$hooks\useAuth.ts"
New-Item -ItemType File -Force -Path "$hooks\useFinancialProfile.ts"
New-Item -ItemType File -Force -Path "$hooks\useLoanEligibility.ts"
New-Item -ItemType File -Force -Path "$hooks\useCreditCardRecommendation.ts"
New-Item -ItemType File -Force -Path "$hooks\useFinancialHealth.ts"
New-Item -ItemType File -Force -Path "$hooks\useFraudCheck.ts"

# ── Utils ─────────────────────────────────────────────────────
$utils = "$src\utils"
New-Item -ItemType Directory -Force -Path $utils
New-Item -ItemType File -Force -Path "$utils\formatters.ts"
New-Item -ItemType File -Force -Path "$utils\validators.ts"
New-Item -ItemType File -Force -Path "$utils\scoreHelpers.ts"
New-Item -ItemType File -Force -Path "$utils\constants.ts"

# ── Components (Reusable UI) ──────────────────────────────────
$comp = "$src\components"

New-Item -ItemType Directory -Force -Path "$comp\common"
New-Item -ItemType File -Force -Path "$comp\common\Navbar.tsx"
New-Item -ItemType File -Force -Path "$comp\common\Sidebar.tsx"
New-Item -ItemType File -Force -Path "$comp\common\LoadingSpinner.tsx"
New-Item -ItemType File -Force -Path "$comp\common\ErrorBoundary.tsx"
New-Item -ItemType File -Force -Path "$comp\common\ProtectedRoute.tsx"
New-Item -ItemType File -Force -Path "$comp\common\ApiResponseAlert.tsx"

New-Item -ItemType Directory -Force -Path "$comp\dashboard"
New-Item -ItemType File -Force -Path "$comp\dashboard\HealthScoreCard.tsx"
New-Item -ItemType File -Force -Path "$comp\dashboard\RiskWarningPanel.tsx"
New-Item -ItemType File -Force -Path "$comp\dashboard\ImprovementSuggestions.tsx"
New-Item -ItemType File -Force -Path "$comp\dashboard\EmiReminderWidget.tsx"

New-Item -ItemType Directory -Force -Path "$comp\loan"
New-Item -ItemType File -Force -Path "$comp\loan\LoanEligibilityForm.tsx"
New-Item -ItemType File -Force -Path "$comp\loan\LoanResultCard.tsx"
New-Item -ItemType File -Force -Path "$comp\loan\LenderComparisonTable.tsx"

New-Item -ItemType Directory -Force -Path "$comp\creditcard"
New-Item -ItemType File -Force -Path "$comp\creditcard\CardRecommendationForm.tsx"
New-Item -ItemType File -Force -Path "$comp\creditcard\CardResultCard.tsx"

New-Item -ItemType Directory -Force -Path "$comp\fraud"
New-Item -ItemType File -Force -Path "$comp\fraud\FraudCheckForm.tsx"
New-Item -ItemType File -Force -Path "$comp\fraud\FraudAlertBanner.tsx"

New-Item -ItemType Directory -Force -Path "$comp\charts"
New-Item -ItemType File -Force -Path "$comp\charts\HealthScoreGauge.tsx"
New-Item -ItemType File -Force -Path "$comp\charts\SpendingBreakdownChart.tsx"
New-Item -ItemType File -Force -Path "$comp\charts\DebtTrendChart.tsx"

# ── Pages (Screens) ───────────────────────────────────────────
$pages = "$src\pages"
New-Item -ItemType Directory -Force -Path $pages
New-Item -ItemType File -Force -Path "$pages\LoginPage.tsx"
New-Item -ItemType File -Force -Path "$pages\OAuthCallbackPage.tsx"
New-Item -ItemType File -Force -Path "$pages\DashboardPage.tsx"
New-Item -ItemType File -Force -Path "$pages\UserProfilePage.tsx"
New-Item -ItemType File -Force -Path "$pages\FinancialProfilePage.tsx"
New-Item -ItemType File -Force -Path "$pages\LoanEligibilityPage.tsx"
New-Item -ItemType File -Force -Path "$pages\CreditCardPage.tsx"
New-Item -ItemType File -Force -Path "$pages\FraudCheckPage.tsx"
New-Item -ItemType File -Force -Path "$pages\NotFoundPage.tsx"

# ── Router & App entry ────────────────────────────────────────
New-Item -ItemType File -Force -Path "$src\router\AppRouter.tsx"
New-Item -ItemType Directory -Force -Path "$src\router"
New-Item -ItemType File -Force -Path "$src\router\AppRouter.tsx"

# ── Env files ─────────────────────────────────────────────────
New-Item -ItemType File -Force -Path "fintrix-web\.env.development"
New-Item -ItemType File -Force -Path "fintrix-web\.env.production"
New-Item -ItemType File -Force -Path "fintrix-web\.gitignore"

Write-Host ""
Write-Host "Frontend scaffold complete!" -ForegroundColor Green
Write-Host "Open 'fintrix-web' in VS Code" -ForegroundColor Cyan
Write-Host "Next: cd fintrix-web && npm install" -ForegroundColor Yellow
Write-Host "Install deps: npm install axios zustand react-router-dom recharts" -ForegroundColor Yellow