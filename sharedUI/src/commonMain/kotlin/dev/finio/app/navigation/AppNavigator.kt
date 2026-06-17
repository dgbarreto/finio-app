package dev.finio.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import cafe.adriel.voyager.navigator.Navigator
import dev.finio.app.ui.auth.LoginScreen
import dev.finio.app.ui.home.HomeScreen
import dev.finio.auth.event.AuthEvent
import dev.finio.auth.event.AuthEventBus
import org.koin.compose.koinInject

@Composable
fun AppNavigator(
    isLoggedIn: Boolean,
    authEventBus: AuthEventBus = koinInject()
){
    val startScreen = if(isLoggedIn) HomeScreen else LoginScreen

    Navigator(startScreen){ navigator ->
        LaunchedEffect(Unit){
            authEventBus.events.collect { event ->
                when(event){
                    is AuthEvent.SessionExpired -> navigator.replaceAll(LoginScreen)
                }
            }
        }
        navigator.lastItem.Content()
    }
}