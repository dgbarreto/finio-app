import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    val iosDeploymentTarget = libs.versions.ios.deploymentTarget.get()

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        val platform = if (iosTarget.name.contains("Simulator")) "ios-simulator" else "ios"
        iosTarget.binaries.all {
            linkerOpts += listOf(
                "-Wl,-platform_version,$platform,$iosDeploymentTarget,$iosDeploymentTarget",
                "-Wl,-U,_OBJC_CLASS_\$_UIViewLayoutRegion"
            )
        }
        iosTarget.binaries.framework {
            baseName = "SharedUI"
            isStatic = true
        }
    }

    androidLibrary {
       namespace = "dev.finio.app.sharedUI"
       compileSdk = libs.versions.android.compileSdk.get().toInt()
       minSdk = libs.versions.android.minSdk.get().toInt()

       compilerOptions {
           jvmTarget = JvmTarget.JVM_11
       }
       androidResources {
           enable = true
       }
       withHostTest {
           isIncludeAndroidResources = true
       }
    }

    iosArm64()
    iosSimulatorArm64()
    
    sourceSets {
        commonMain.dependencies {
            api(projects.sharedLogic)
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(libs.androidx.lifecycle.viewmodel.compose)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.kotlinx.datetime)
            implementation(libs.voyager.navigator)
            implementation(libs.voyager.tab.navigator)
            implementation(libs.voyager.screenmodel)
            implementation(libs.voyager.koin)
            implementation(libs.koin.compose)
        }
        androidMain.dependencies {
            implementation(compose.uiTooling)
        }
    }
}

dependencies {
    androidRuntimeClasspath(compose.uiTooling)
}

compose.resources {
    publicResClass = true
    packageOfResClass = "dev.finio.app.sharedui.generated.resources"
    generateResClass = auto
}