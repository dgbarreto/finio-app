package dev.finio.app

import android.app.Application
import dev.finio.app.di.appModules
import dev.finio.app.di.createDriverFactory
import dev.finio.app.di.sharedLogicModule
import dev.finio.auth.storage.AndroidTokenStorage
import io.sentry.android.core.SentryAndroid
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class FinioApplication: Application(){
    override fun onCreate() {
        super.onCreate()

        SentryAndroid.init(this){
            it.dsn = BuildConfig.SENTRY_DSN.ifBlank { null }
            it.environment = BuildConfig.BUILD_TYPE
            it.tracesSampleRate = 1.0
            it.isEnableUserInteractionTracing = true
            it.isAttachScreenshot = false
        }

        AndroidTokenStorage.init(this)

        startKoin {
            androidContext(this@FinioApplication)

            modules(
                listOf(sharedLogicModule) + appModules(
                    baseUrl = "https://finio-api-production.up.railway.app",
                    driverFactory = createDriverFactory(this@FinioApplication)
                )
            )
        }
    }
}