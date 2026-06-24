package dev.finio.app.observability

expect object FinioObservability {
    fun captureError(message: String)
    fun addBreadcrumb(screen: String)
}