package dev.finio.app.observability

import io.sentry.Breadcrumb
import io.sentry.Sentry
import io.sentry.SentryLevel

actual object FinioObservability {
    actual fun captureError(message: String) {
        Sentry.captureMessage(message, SentryLevel.ERROR)
    }

    actual fun addBreadcrumb(screen: String) {
        val breadcrumb = Breadcrumb.navigation("previous", screen)
        Sentry.addBreadcrumb(breadcrumb)
    }
}