package dev.finio.app.di

import dev.finio.app.deeplink.DeepLinkEventBus
import org.koin.dsl.module

val sharedLogicModule = module {
    single { DeepLinkEventBus() }
}