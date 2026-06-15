import java.util.Properties

val localProperties = Properties().apply {
    val file = File(rootDir, "local.properties")
    if (file.exists()) load(file.inputStream())
}

rootProject.name = "Finioapp"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        maven {
            url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        }
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/dgbarreto/*")
            credentials {
                username = localProperties["github.actor"] as String?
                    ?: System.getenv("GITHUB_ACTOR") ?: ""
                password = localProperties["github.token"] as String?
                    ?: System.getenv("GITHUB_TOKEN") ?: ""
            }
            content {
                includeGroup("dev.finio")
            }
        }
    }
}

include(":androidApp")
include(":sharedLogic")
include(":sharedUI")

if (localProperties["useLocalTransactions"] == "true") {
    includeBuild("../finio-transaction") {
        dependencySubstitution {
            substitute(module("dev.finio:transactions-kmp")).using(project(":transactions"))
        }
    }
    includeBuild("../finio-budget"){
        dependencySubstitution {
            substitute(module("dev.finio:budget-kmp")).using(project(":budget"))
        }
    }
    includeBuild("../finio-insights"){
        dependencySubstitution {
            substitute(module("dev.finio:insights-kmp")).using(project(":insights"))
        }
    }
}
