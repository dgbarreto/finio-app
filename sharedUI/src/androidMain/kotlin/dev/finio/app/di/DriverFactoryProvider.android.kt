package dev.finio.app.di

import android.content.Context
import dev.finio.transactions.db.DatabaseDriverFactory

actual fun createDriverFactory(): DatabaseDriverFactory {
    error("Use createDriverFactory(context) on Android")
}
fun createDriverFactory(context: Context): DatabaseDriverFactory = DatabaseDriverFactory(context)