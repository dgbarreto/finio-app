package dev.finio.app

import android.app.Application
import dev.finio.app.di.appModules
import dev.finio.app.di.createDriverFactory
import dev.finio.auth.storage.AndroidTokenStorage
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class FinioApplication: Application(){
    override fun onCreate() {
        super.onCreate()

        AndroidTokenStorage.init(this)

        startKoin {
            androidContext(this@FinioApplication)

            modules(
                appModules(
                    baseUrl = "https://finio-api-production.up.railway.app",
                    driverFactory = createDriverFactory(this@FinioApplication)
                )
            )
        }
    }
}