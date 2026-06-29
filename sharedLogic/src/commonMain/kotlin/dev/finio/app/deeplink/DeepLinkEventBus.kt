package dev.finio.app.deeplink

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

sealed class DeepLinkEvent{
    object OpenBudget: DeepLinkEvent()
}

class DeepLinkEventBus{
    private val _events = MutableSharedFlow<DeepLinkEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<DeepLinkEvent> = _events.asSharedFlow()

    fun emit(event: DeepLinkEvent){
        _events.tryEmit(event)
    }
}