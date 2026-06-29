package dev.finio.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import cafe.adriel.voyager.navigator.Navigator
import dev.finio.app.deeplink.DeepLinkEvent
import dev.finio.app.deeplink.DeepLinkEventBus
import dev.finio.app.observability.FinioObservability
import dev.finio.app.ui.auth.LoginScreen
import dev.finio.app.ui.home.BudgetDeepLinkState
import dev.finio.app.ui.home.HomeScreen
import dev.finio.auth.event.AuthEvent
import dev.finio.auth.event.AuthEventBus
import org.koin.compose.koinInject

@Composable
fun AppNavigator(
    isLoggedIn: Boolean,
    authEventBus: AuthEventBus = koinInject(),
    deepLinkEventBus: DeepLinkEventBus = koinInject()
){
    val startScreen = if(isLoggedIn) HomeScreen else LoginScreen

    Navigator(startScreen){ navigator ->
        LaunchedEffect(navigator.lastItem){
            FinioObservability.addBreadcrumb(
                navigator.lastItem::class.simpleName ?: "Unknown"
            )
        }

        LaunchedEffect(Unit){
            authEventBus.events.collect { event ->
                when(event){
                    is AuthEvent.SessionExpired -> navigator.replaceAll(LoginScreen)
                }
            }
        }

        LaunchedEffect(Unit){
            deepLinkEventBus.events.collect {event ->
                when(event){
                    is DeepLinkEvent.OpenBudget -> {
                        if(navigator.lastItem !is HomeScreen){
                            navigator.replaceAll(HomeScreen)
                        }
                        BudgetDeepLinkState.trigger()
                    }
                }
            }
        }

        navigator.lastItem.Content()
    }
}