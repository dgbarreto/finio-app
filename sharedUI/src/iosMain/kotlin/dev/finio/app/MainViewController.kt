package dev.finio.app

import androidx.compose.ui.window.ComposeUIViewController
import dev.finio.app.di.appModules
import dev.finio.app.di.createDriverFactory
import dev.finio.app.di.sharedLogicModule
import org.koin.core.context.startKoin
import platform.UIKit.UIViewController

fun MainViewController() = ComposeUIViewController { App() }

fun initKoin(baseUrl: String) {
    startKoin {
        modules(
            listOf(sharedLogicModule) + appModules(
                baseUrl = baseUrl,
                driverFactory = createDriverFactory()
            )
        )
    }
}

