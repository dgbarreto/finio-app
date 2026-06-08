package dev.finio.app

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform