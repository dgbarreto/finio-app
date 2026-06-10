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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dev.finio.app.navigation.AppNavigator
import dev.finio.auth.presentation.AuthViewModel
import org.jetbrains.compose.resources.painterResource

import finioapp.sharedui.generated.resources.Res
import finioapp.sharedui.generated.resources.compose_multiplatform
import org.koin.compose.koinInject

@Composable
fun App() {
    val authViewModel: AuthViewModel = koinInject()
    val authState by authViewModel.state.collectAsState()

    val isLoggedIn = authState is dev.finio.auth.domain.model.AuthState.Authenticated

    AppNavigator(isLoggedIn = isLoggedIn)
}