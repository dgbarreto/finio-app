package dev.finio.app

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import dev.finio.app.navigation.AppNavigator
import dev.finio.auth.domain.model.AuthState
import dev.finio.auth.presentation.AuthViewModel
import org.jetbrains.compose.resources.painterResource

import org.koin.compose.koinInject

@Composable
fun App() {
    val authViewModel: AuthViewModel = koinInject()
    val authState by authViewModel.state.collectAsState()

    val isLoggedIn = authState is AuthState.Authenticated

    AppNavigator(isLoggedIn = isLoggedIn)
}