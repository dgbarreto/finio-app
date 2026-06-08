# finio-app

Shell app for the Finio personal finance platform. Consumes all KMP modules via Maven and delivers a shared UI experience for Android and iOS using Compose Multiplatform.

## Stack

- **Language**: Kotlin Multiplatform
- **UI**: Compose Multiplatform 1.11.0
- **Navigation**: Voyager 1.0.0
- **DI**: Koin 4.0.0
- **Modules**: finio-auth, finio-transaction, finio-budget, finio-insights, finio-design-system

## Project structure

```
finio-app/
  androidApp/          ← Android application entry point
  iosApp/              ← iOS Xcode project
  sharedLogic/         ← KMP module: imports and re-exports all Finio Maven modules
  sharedUI/            ← KMP module: Compose Multiplatform screens and navigation
  settings.gradle.kts  ← GitHub Packages repository configuration
  local.properties     ← github.actor and github.token (not committed)
```

## Module responsibilities

### sharedLogic
Declares all Finio KMP modules as `api` dependencies so their types are available throughout the app without re-declaring each dependency.

```kotlin
commonMain.dependencies {
    api(libs.finio.auth)
    api(libs.finio.transaction)
    api(libs.finio.budget)
    api(libs.finio.insights)
    api(libs.finio.design.system)
    api(libs.koin.core)
}
```

### sharedUI
Contains all Compose Multiplatform screens, navigation setup with Voyager, and ViewModels. Depends on `sharedLogic`.

### androidApp
Android application module. Entry point for the Android target.

### iosApp
Xcode project. Embeds the KMP framework produced by `sharedUI`.

## KMP modules consumed

| Module | Artifact | Version |
|--------|----------|---------|
| finio-auth | `dev.finio:auth-kmp` | 0.1.0 |
| finio-transaction | `dev.finio:finio-transaction-kmp` | 0.1.0 |
| finio-budget | `dev.finio:budget-kmp` | 0.1.0 |
| finio-insights | `dev.finio:insights-kmp` | 0.1.0 |
| finio-design-system | `dev.finio:finio-design-system-kmp` | 0.1.0 |

All modules are published to GitHub Packages under `dev.finio`.

## Local setup

Add to `local.properties` (never commit this file):

```properties
github.actor=YOUR_GITHUB_USERNAME
github.token=YOUR_PERSONAL_ACCESS_TOKEN
```

The token needs `read:packages` permission to resolve dependencies and `write:packages` to publish.

## Build

```bash
# Android
./gradlew :androidApp:assembleDebug

# Verify all modules resolve
./gradlew :sharedLogic:assemble
./gradlew :sharedUI:assemble
```

## Key versions

```toml
kotlin = "2.3.21"
agp = "9.0.1"
composeMultiplatform = "1.11.0"
koin = "4.0.0"
voyager = "1.0.0"
```

## Pending before full implementation

- Contract review between finio-api schemas and KMP DTOs (finio-transaction, finio-budget)
- Rename finio-auth internal module from `shared` to `auth` to fix iOS artifact ID generation
- Confirm Android artifact ID for finio-transaction (`finio-transaction-android` vs `transaction-android`)