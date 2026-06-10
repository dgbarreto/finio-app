package dev.finio.app

import androidx.compose.ui.window.ComposeUIViewController
import dev.finio.app.di.appModules
import dev.finio.app.di.createDriverFactory
import org.koin.core.context.startKoin

fun MainViewController() = ComposeUIViewController { App() }

fun initKoin(baseUrl: String) {
    startKoin {
        modules(
            appModules(
                baseUrl = baseUrl,
                driverFactory = createDriverFactory()
            )
        )
    }
}

