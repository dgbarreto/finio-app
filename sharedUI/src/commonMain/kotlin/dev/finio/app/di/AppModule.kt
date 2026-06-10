package dev.finio.app.di

import dev.finio.auth.di.authModule
import dev.finio.auth.storage.createTokenStorage
import dev.finio.budget.di.budgetModule
import dev.finio.insights.di.insightsModule
import dev.finio.transactions.db.DatabaseDriverFactory
import dev.finio.transactions.di.transactionModule
import org.koin.core.module.Module

fun appModules(
    baseUrl: String,
    driverFactory: DatabaseDriverFactory
): List<Module> {
    val tokenStorage = createTokenStorage()

    return listOf(
        authModule(baseUrl = baseUrl),
        transactionModule(
            baseUrl = baseUrl,
            driverFactory = driverFactory,
            tokenProvider = { tokenStorage.getToken() }
        ),
        budgetModule(
            baseUrl = baseUrl,
            tokenProvider = { tokenStorage.getToken() }
        ),
        insightsModule(
            baseUrl = baseUrl,
            tokenProvider = { tokenStorage.getToken() }
        )
    )
}