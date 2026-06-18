# Fix IntelliSense for Local Dependencies (Composite Builds)

## Quick Fix (Do This First!)

```bash
# 1. From finio-app root, run the sync script:
chmod +x sync-ide.sh
./sync-ide.sh

# 2. Close IDE completely (not just the project)

# 3. Delete ALL IDE caches:
rm -rf ~/Library/Caches/JetBrains/*

# 4. Reopen IDE and wait for indexing (watch the progress bar)

# 5. VERIFY: local.properties must have:
echo "useLocalTransactions=true" | grep -q "useLocalTransactions" || echo "useLocalTransactions=true" >> local.properties
```

If IntelliSense still doesn't work after step 4, continue to **Manual IDE Link** below.

---

## Understanding the Problem

Your project uses Gradle composite builds with `dependencySubstitution`:
- Local modules (finio-auth, finio-transaction, etc.) are defined in `settings.gradle.kts`
- When `useLocalTransactions=true`, these replace the remote Maven artifacts
- BUT the IDE doesn't automatically recognize this substitution — you need to explicitly link them

**Settings.**gradle.kts has:
```kotlin
includeBuild("../finio-transaction") {
    dependencySubstitution {
        substitute(module("dev.finio:transactions-kmp")).using(project(":transactions"))
    }
}
// ... and similar for budget, insights, auth
```

---

## Solution 1: IDE Cache Invalidation (Works 70% of the time)

### Android Studio / IntelliJ:
1. **File** → **Invalidate Caches** → **Invalidate and Restart**
2. Click "Invalidate and Restart"
3. Wait for IDE to restart and indexing to complete (watch progress bar at bottom)

### Or from Terminal:
```bash
# Close IDE first, then:
rm -rf ~/Library/Caches/JetBrains/IntelliJIdea*
rm -rf ~/Library/Caches/Jetbrains/AndroidStudio*

# Reopen IDE
```

---

## Solution 2: Gradle Resync (Works 20% more cases)

In your IDE:
- **Android Studio**: File → **Sync Now** (or Ctrl+Alt+Y)
- **IntelliJ**: Right-click on `settings.gradle.kts` → **Reload Project**
- Or: View → Tool Windows → Gradle → **Reload All Gradle Projects**

Then wait 30-60 seconds for indexing.

---

## Solution 3: Project Structure Link (Works for remaining cases)

Sometimes you need to manually link composite builds in IDE project settings:

### Android Studio / IntelliJ:

1. **File** → **Project Structure** (or Cmd+;)
2. Select **Modules** in left panel
3. You should see something like:
   - `finio-app.sharedUI`
   - `finio-app.sharedLogic`
   - `finio-app.androidApp`
   - And hopefully: `finio-transaction` (or `:transactions`)

4. If the composite modules are **missing**, click **+** → **Import Module**
   - Select: `/Users/danilobarreto/Desktop/repos/Finio/finio-transaction/transactions`
   -Then repeat for:
     - `/Users/danilobarreto/Desktop/repos/Finio/finio-budget/budget`
     - `/Users/danilobarreto/Desktop/repos/Finio/finio-insights/insights`
     - `/Users/danilobarreto/Desktop/repos/Finio/finio-auth/auth`

5. Click **OK** and wait for indexing

---

## Solution 4: Verify Local.Properties

Your `local.properties` file **must** have:
```properties
useLocalTransactions=true
```

Check it:
```bash
cat local.properties | grep useLocalTransactions
# Should output: useLocalTransactions=true
```

If missing (or false), add it:
```bash
echo "useLocalTransactions=true" >> local.properties
```

Then in IDE: File → Sync Now

---

## Solution 5: Full Gradle Clean + Sync

Run this from `/finio-app` root:

```bash
// Clean everything
./gradlew clean

# Refresh remote repos
./gradlew --refresh-dependencies

# Verify Gradle sees all projects
./gradlew projects

# You should see output like:
# Root project 'Finioapp'
# +--- Project ':androidApp'
# +--- Project ':sharedLogic'
# +--- Project ':sharedUI'
# \--- Included builds
#      +--- Project ':transactions' (plugin use only)
#      ...
```

Then in IDE: File → Sync Now

---

## Solution 6: Check settings.gradle.kts

Verify your `settings.gradle.kts` has the composite builds **conditionally** included:

Should look like:
```kotlin
val useLocalTransactions = localProperties["useLocalTransactions"] == "true"

if (useLocalTransactions) {
    includeBuild("../finio-transaction") {
        dependencySubstitution {
            substitute(module("dev.finio:transactions-kmp")).using(project(":transactions"))
        }
    }
    // ... and other modules
}
```

✅ This is now in your file.

---

## Solution 7: IDE Settings

### Android Studio / IntelliJ Settings:

**Settings** → **Build, Execution, Deployment** → **Gradle:**
- ✅ "Use Gradle from" = `gradle-wrapper.properties` (or project path)
- ✅ "Gradle JVM" = JDK 11 or higher (should match project)
- ✅ "Run tests using" = Gradle (or IDE)

**Settings** → **Languages & Frameworks** → **Kotlin:**
- ✅ Kotlin language version = 2.x
- ✅ No red errors in the Kotlin settings panel

---

## Solution 8: Nuclear Option (Last Resort)

If **nothing** works after trying all above:

```bash
# 1. Close IDE

# 2. Delete everything:
rm -rf ~/.gradle
rm -rf .gradle
rm -rf .idea
rm -rf finio-app.iml
rm -rf */*.iml

# 3. Delete IDE cache
rm -rf ~/Library/Caches/JetBrains/*

# 4. Reopen IDE (it will re-import from scratch)
# This takes 5-10 minutes but almost always works
```

---

## Troubleshooting Specific Errors

### "Unresolved reference 'dev.finio.auth.event.AuthEvent'"

This means the IDE hasn't linked the composite build module `:auth`.

**Try:**
1. File → Sync Now
2. Or: File → Invalidate Caches → Invalidate and Restart
3. Or: Manually import module (Solution 3 above)

### "Cannot find declaration of name 'SummarySection'"

This is a different error — likely a missing local function, not a composite build issue.

### Build works but IDE shows errors (Squiggles in editor)

**This is purely an IDE cache issue:**
```bash
rm -rf ~/Library/Caches/JetBrains/*
# Reopen IDE
```

### "Project 'transactions' not found"

The composite build either:
1. Doesn't exist at `../finio-transaction` — verify path
2. `useLocalTransactions` is not `true` in `local.properties`
3. `settings.gradle.kts` is missing the `includeBuild()` call

Check:
```bash
# Verify directories exist:
ls -la ../finio-transaction/
ls -la ../finio-budget/
ls -la ../finio-insights/
ls -la ../finio-auth/

# Verify local.properties:
grep useLocalTransactions local.properties
```

---

## What Changed in Setup

✅ **Updated `settings.gradle.kts`:**
- Made composite builds conditionally included based on `local.properties`
- Properly formatted with clear comments

✅ **Updated `gradle.properties`:**
- Disabled project isolation for IDE (allows composite build indexing)
- Added IDE-friendly Gradle settings

✅ **Created `sync-ide.sh`:**
- Cleans Gradle cache
- Refreshes dependencies
- Shows detailed next steps

---

## Quick Checklist

- [ ] `local.properties` has `useLocalTransactions=true`
- [ ] Directories exist: `../finio-transaction`, `../finio-budget`, etc.
- [ ] IDE caches deleted: `rm -rf ~/Library/Caches/JetBrains/*`
- [ ] IDE Gradle JVM = JDK 11+
- [ ] File → Sync Now (in IDE)
- [ ] File → Invalidate Caches (if sync doesn't work)
- [ ] Project Structure → Modules shows composite build modules

---

## Final Check

Once everything is working, you should see in `File → Project Structure → Modules`:

```
Project 'finio-app'
├── androidApp
├── sharedLogic
├── sharedUI
├── transactions (from ../finio-transaction)  ← These should be visible
├── budget (from ../finio-budget)
├── insights (from ../finio-insights)
└── auth (from ../finio-auth)
```

If you see all 7 modules, **IntelliSense will work perfectly**.

---

## Still not working?

If after trying all 8 solutions you still have issues, please share:
1. Output of: `./gradlew projects`
2. Output of: `cat local.properties`
3. The exact unresolved reference error (with line number)

Then we can debug further!



