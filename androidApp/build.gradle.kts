import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) load(file.inputStream())
}

fun sentryDsn(): String =
    localProperties["SENTRY_DSN"] as String?
        ?: System.getenv("SENTRY_DSN")
        ?: ""

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.googleServices)
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_11
    }
}
dependencies {
    implementation(libs.androidx.material3)
    implementation(projects.sharedUI)
    implementation(libs.androidx.activity.compose)
    implementation(libs.koin.android)
    implementation(libs.voyager.navigator)
    implementation(libs.compose.uiToolingPreview)
    implementation(libs.sentry.android)
    implementation(libs.sentry.compose)
    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
    implementation(libs.firebase.messaging)

    debugImplementation(libs.compose.uiTooling)
}

android {
    namespace = "dev.finio.app"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "dev.finio.app"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildFeatures{
        buildConfig = true
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }

        debug {
            buildConfigField("String", "SENTRY_DSN", "\"${sentryDsn() ?: ""}\"")
            buildConfigField("String", "BUILD_TYPE", "\"debug\"")
        }
        release {
            buildConfigField("String", "SENTRY_DSN", "\"${sentryDsn() ?: ""}\"")
            buildConfigField("String", "BUILD_TYPE", "\"release\"")
        }

    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}