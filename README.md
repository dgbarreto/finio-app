# finio-insights

Kotlin Multiplatform insights module for the Finio platform. Encapsulates all financial analytics logic — spending by category, monthly evolution, and period summary — published to GitHub Packages (Maven) for consumption by `finio-app`.

## Stack

- **Language**: Kotlin Multiplatform
- **HTTP**: Ktor Client
- **Serialization**: kotlinx.serialization
- **Coroutines**: kotlinx.coroutines
- **DI**: Koin
- **CI/CD**: Bitrise
- **Publication**: GitHub Packages (Maven)

## Targets

| Target | Status |
|--------|--------|
| Android | ✅ |
| iOS Arm64 | ✅ |
| iOS Simulator Arm64 | ✅ |

## Module structure
insights/src/
commonMain/kotlin/dev/finio/insights/
data/
dto/                              ← API request/response DTOs
mapper/                           ← DTO → domain model mappers
remote/
InsightsRemoteDataSource.kt     ← Ktor API calls
repository/
InsightsRepositoryImpl.kt       ← repository implementation
di/
InsightsModule.kt                 ← Koin module (insightsModule(baseUrl))
domain/
model/
SpendingByCategory.kt           ← category, total, percentage
MonthlyEvolution.kt             ← year, month, income, expenses, balance
InsightsSummary.kt              ← totalIncome, totalExpenses, balance, topCategory
repository/
InsightsRepository.kt           ← interface
presentation/
InsightsViewModel.kt              ← StateFlow<InsightsUiState>
InsightsUiState.kt

## Domain models

```kotlin
data class SpendingByCategory(
    val category: String,
    val total: Double,
    val percentage: Int
)

data class MonthlyEvolution(
    val year: Int,
    val month: Int,
    val income: Double,
    val expenses: Double,
    val balance: Double
)

data class InsightsSummary(
    val totalIncome: Double,
    val totalExpenses: Double,
    val balance: Double,
    val topCategory: String?
)

data class InsightsUiState(
    val spendingByCategory: List<SpendingByCategory> = emptyList(),
    val monthlyEvolution: List<MonthlyEvolution> = emptyList(),
    val summary: InsightsSummary? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
```

## API endpoints

All endpoints served by `finio-api` deployed on Railway.

| Method | Route | Description | Auth |
|--------|-------|-------------|------|
| POST | `/insights/spending-by-category` | Spending by category in period | ✓ |
| GET | `/insights/monthly-evolution` | Monthly evolution (`months` query param) | ✓ |
| POST | `/insights/summary` | Financial summary for period | ✓ |

> `POST` is used for endpoints that require `startDate`/`endDate` in the request body.

## ViewModel

```kotlin
class InsightsViewModel(repository: InsightsRepository) {
    val state: StateFlow<InsightsUiState>

    fun loadAll(startDate: String, endDate: String, months: Int = 6)
    // Fetches spendingByCategory, monthlyEvolution and summary in parallel
    // startDate/endDate format: ISO-8601 UTC (e.g. "2026-07-01T00:00:00.000Z")
}
```

## Koin setup

```kotlin
startKoin {
    modules(
        insightsModule(baseUrl = "https://finio-api-production.up.railway.app")
    )
}
```

Resolve and use:

```kotlin
val viewModel: InsightsViewModel = get()
viewModel.loadAll(
    startDate = "2026-07-01T00:00:00.000Z",
    endDate = "2026-07-31T00:00:00.000Z",
    months = 6
)
```

## Published artifacts

| Artifact | Description |
|----------|-------------|
| `dev.finio:insights-android` | Android AAR |
| `dev.finio:insights-iosarm64` | iOS Arm64 framework |
| `dev.finio:insights-iossimulatorarm64` | iOS Simulator framework |
| `dev.finio:insights-kmp` | KMP metadata |

## Publishing

```bash
git tag 1.0.0
git push origin 1.0.0
```

Local publish:
```properties
# local.properties
version=1.0.0
github.actor=your_username
github.token=your_token
```
Commit:
git add README.md
git commit -m "docs: update README with complete domain models and ViewModel API

- Add complete domain model definitions (SpendingByCategory, MonthlyEvolution, InsightsSummary)
- Add InsightsUiState definition
- Document loadAll() signature with ISO-8601 date format requirement
- Fix Koin setup with correct module name and usage example
- Update published artifact group IDs"
git push origin main
Último — finio-app?https://github.com/dgbarreto/finio-appVou buscar o conteúdo:Fetched: GitHub - dgbarreto/finio-appFetched: GitHub - dgbarreto/finio-appO README atual está desatualizado — versões antigas, seção "pending" que já foi resolvida, e não cobre nada do que foi implementado (screens, navigation, FCM, Sentry, iOS, Maestro). Aqui está o README completo e atualizado:
markdown# finio-app

Compose Multiplatform shell app for the Finio personal finance platform. Consumes all KMP modules via GitHub Packages (Maven) and delivers a shared UI experience for Android and iOS.

## Stack

- **Language**: Kotlin Multiplatform
- **UI**: Compose Multiplatform 1.11.0
- **Navigation**: Voyager 1.0.0
- **DI**: Koin 4.0.0
- **Push Notifications**: Firebase Cloud Messaging (Android)
- **Observability**: Sentry (Android)
- **UI Testing**: Maestro
- **Modules**: finio-auth, finio-transaction, finio-budget, finio-insights, finio-design-system

## Platforms

| Platform | Status |
|----------|--------|
| Android | ✅ |
| iOS | ✅ |

## Project structure
finio-app/
androidApp/           ← Android application entry point (FinioApplication, MainActivity)
iosApp/               ← iOS Xcode project (iOSApp.swift, ComposeView)
sharedLogic/          ← KMP module: DI setup, DeepLinkEventBus, FinioObservability (expect/actual)
sharedUI/             ← KMP module: all Compose Multiplatform screens and navigation
maestro/
flows/              ← Maestro UI test flows (Android + iOS)
settings.gradle.kts   ← GitHub Packages repository configuration
local.properties      ← credentials and secrets (not committed)

## Module responsibilities

### `sharedLogic`

Declares all Finio KMP modules as `api` dependencies. Also contains:
- `AppModule.kt` — Koin module wiring all dependencies
- `DeepLinkEventBus` — `SharedFlow` for deep link events (e.g. push notification → Budget tab)
- `FinioObservability` — `expect/actual` for error tracking (Android: Sentry, iOS: no-op)
- `sharedLogicModule` — Koin singleton registrations for cross-cutting concerns

### `sharedUI`

All Compose Multiplatform screens, navigation and UI logic:
- `AppNavigator` — Voyager `Navigator` with auth state and deep link observation
- `HomeScreen` — summary, recent transactions, budget overview with pull-to-refresh
- `TransactionsScreen` — offline-first list, local search + category/type filters, create/edit/delete
- `BudgetScreen` — list with spending progress, create/edit/delete
- `InsightsScreen` — period selector, donut chart, bar chart (native Canvas)
- `ProfileScreen` — user info, logout with confirmation dialog
- `LoginScreen` / `RegisterScreen` — auth flows with typed error messages

### `androidApp`

- `FinioApplication` — Koin init, Sentry init
- `MainActivity` — FCM token registration, deep link intent handling, push notification permission
- `FinioMessagingService` — FCM data-only payload handling, push notification display

### `iosApp`

- `iOSApp.swift` — Koin init via `doInitKoin()`, `ComposeView` embedding the KMP framework
- Xcode Build Phase script to copy Compose Resources from Gradle cache to app bundle

## Screens

| Screen | Features |
|--------|----------|
| Login | Email/password, typed error messages (InvalidCredentials / NetworkError) |
| Register | Name/email/password |
| Home | Balance summary, last 5 transactions, top 3 budgets, pull-to-refresh, "See all" navigation |
| Transactions | Offline-first list, local search by title, type/category filter chips, create/edit/delete, pull-to-refresh |
| Budget | List with `LinearProgressIndicator`, create/edit/delete with `FinioDateField`, pull-to-refresh |
| Insights | Period selector (This Month/Last 3/6 months/This Year), spending donut chart, monthly bar chart |
| Profile | Avatar with initials, name/email, logout with confirmation |

## KMP modules consumed

| Module | Artifact | Version |
|--------|----------|---------|
| finio-design-system | `dev.finio:design-system-kmp` | 1.8.0 |
| finio-auth | `dev.finio:auth-kmp` | 1.8.0 |
| finio-transaction | `dev.finio:transactions-kmp` | 1.4.0 |
| finio-budget | `dev.finio:budget-kmp` | 1.4.0 |
| finio-insights | `dev.finio:insights-kmp` | 1.0.0 |

## Local setup

Add to `local.properties` (never commit this file):

```properties
github.actor=YOUR_GITHUB_USERNAME
github.token=YOUR_PERSONAL_ACCESS_TOKEN   # requires read:packages
SENTRY_DSN=https://...@sentry.io/...
```

For Firebase push notifications, add `androidApp/google-services.json` (download from Firebase Console).

## Build

```bash
# Android
./gradlew :androidApp:assembleDebug

# iOS framework (for Xcode)
./gradlew :sharedUI:linkDebugFrameworkIosSimulatorArm64

# All modules
./gradlew :sharedLogic:assemble :sharedUI:assemble
```

## iOS setup

1. Open `iosApp/iosApp.xcodeproj` in Xcode
2. In **Build Settings → Framework Search Paths**, add:
   `$(SRCROOT)/../sharedUI/build/bin/iosSimulatorArm64/debugFramework`
3. In **Build Settings → Other Linker Flags**, add:
   `-Wl,-U,_OBJC_CLASS_$_UIViewLayoutRegion`
4. In **Build Phases → Link Binary With Libraries**, add `libsqlite3.tbd`
5. Add a **Run Script Build Phase** to copy Compose Resources:

```bash
CACHE_BASE="${HOME}/.gradle/caches/9.1.0/transforms"
RESOURCES_SRC=$(find "$CACHE_BASE" -path "*/designsystem/assets/composeResources" -type d 2>/dev/null | head -1)
DEST="${BUILT_PRODUCTS_DIR}/${PRODUCT_NAME}.app/compose-resources/composeResources"
if [ -n "$RESOURCES_SRC" ]; then
    mkdir -p "$DEST"
    cp -r "$RESOURCES_SRC/." "$DEST/"
fi
```

## Push Notifications (Android)

FCM is configured with **data-only payloads** (no `notification` block) to ensure `FinioMessagingService.onMessageReceived` is always called regardless of app state. Tapping a budget alert notification deep-links directly to the Budget tab via `DeepLinkEventBus`.

iOS push notifications require APNs (Apple Developer paid account) — planned for a future phase.

## UI Tests (Maestro)

```bash
# Install Maestro
curl -Ls "https://get.maestro.mobile.dev" | bash

# Run all flows (Android)
maestro test --env FINIO_EMAIL=your@email.com --env FINIO_PASSWORD=yourpassword maestro/flows/

# Run a single flow
maestro test maestro/flows/login.yaml
```

Available flows: `login.yaml`, `home_navigation.yaml`, `create_transaction.yaml`, `create_budget.yaml`

## Observability

- **Android**: Sentry initialized in `FinioApplication` via `BuildConfig.SENTRY_DSN`. `FinioObservability.captureError()` used across all screens. Navigation breadcrumbs added in `AppNavigator`.
- **iOS**: `FinioObservability` is a no-op stub. Sentry iOS (`sentry-cocoa`) integration planned for a future phase.

## Key versions
kotlin = "2.3.21"
agp = "9.0.1"
composeMultiplatform = "1.11.0"
koin = "4.0.0"
voyager = "1.0.0"
sentry-android = "7.14.0"
firebase-bom = "33.7.0"
maestro = "2.6.1"

## Known backlog

- `finio-auth` module internal name is still `shared` instead of `auth` (issue #2)
- `finio-transaction` Android artifact ID: `finio-transaction-android` vs `transaction-android` (issue #3)
- `TransactionViewModel`, `BudgetViewModel`, `InsightsViewModel` registered as `factory {}` in Koin — evaluate if `single {}` is needed for shared state
- Budget alert throttling: push notification fires on every budget list load (tracked in `finio-api`)
- iOS push notifications via APNs (requires Apple Developer paid account)
- Sentry iOS integration via `sentry-cocoa`
- `FinioDatePicker` uses deprecated `kotlinx.datetime.Instant` (suppressed with `@file:Suppress("DEPRECATION")`)
