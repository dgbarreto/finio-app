package dev.finio.app.observability

actual object FinioObservability {
    actual fun captureError(message: String) {
        // TODO: integrate sentry-cocoa
    }

    actual fun addBreadcrumb(screen: String) {
        // TODO: integrate sentry-cocoa
    }
}