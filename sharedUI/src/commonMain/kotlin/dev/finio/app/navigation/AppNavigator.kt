package dev.finio.app.navigation

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import dev.finio.app.ui.auth.LoginScreen
import dev.finio.app.ui.home.HomeScreen

@Composable
fun AppNavigator(isLoggedIn: Boolean){
    val startScreen = if(isLoggedIn) HomeScreen else LoginScreen

    Navigator(startScreen){ navigator ->
        navigator.lastItem
    }
}