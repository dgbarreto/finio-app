package dev.finio.app.di

import dev.finio.transactions.db.DatabaseDriverFactory

expect fun createDriverFactory(): DatabaseDriverFactory