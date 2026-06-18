#!/bin/bash

echo "🧹 Cleaning Gradle caches..."
rm -rf ~/.gradle/caches/modules-2
rm -rf ~/.gradle/caches/build-cache-1
rm -rf .gradle

echo "🗑️  Cleaning build outputs..."
./gradlew clean

echo "🔄 Refreshing dependencies with composite builds..."
./gradlew --refresh-dependencies

echo "📋 Linking composite build projects..."
./gradlew projects

echo "✅ Sync complete!"
echo ""
echo "NEXT STEPS FOR IDE:"
echo "=================="
echo ""
echo "1️⃣  CLOSE your IDE completely (not just the project)"
echo ""
echo "2️⃣  DELETE IDE caches:"
echo "   rm -rf ~/Library/Caches/JetBrains/IntelliJIdea*"
echo "   rm -rf ~/Library/Caches/JetBrains/AndroidStudio*"
echo ""
echo "3️⃣  VERIFY local.properties has:"
echo "   useLocalTransactions=true"
echo ""
echo "4️⃣  REOPEN the IDE"
echo ""
echo "5️⃣  WAIT for indexing to complete (watch progress bar at bottom)"
echo ""
echo "6️⃣  If still no IntelliSense:"
echo "   - File > Invalidate Caches > Invalidate and Restart"
echo "   - Or (Android Studio): File > Sync Now"
echo ""
echo "7️⃣  Check File > Project Structure > Modules"
echo "   You should see composite build modules:"
echo "   - transactions (from ../finio-transaction)"
echo "   - budget (from ../finio-budget)"
echo "   - insights (from ../finio-insights)"
echo "   - auth (from ../finio-auth)"



