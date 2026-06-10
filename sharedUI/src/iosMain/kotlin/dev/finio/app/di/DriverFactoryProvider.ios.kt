package dev.finio.app.di

import dev.finio.transactions.db.DatabaseDriverFactory

actual fun createDriverFactory(): DatabaseDriverFactory = DatabaseDriverFactory()