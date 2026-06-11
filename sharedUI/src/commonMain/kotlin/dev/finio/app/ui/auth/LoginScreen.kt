package dev.finio.app.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.finio.app.ui.home.HomeScreen
import dev.finio.auth.domain.model.AuthState
import dev.finio.auth.presentation.AuthViewModel
import dev.finio.designsystem.component.FinioButton
import dev.finio.designsystem.component.FinioPasswordField
import dev.finio.designsystem.component.FinioTextField
import dev.finio.designsystem.theme.FinioTheme
import org.koin.compose.koinInject

object LoginScreen: Screen{
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val authViewModel: AuthViewModel = koinInject()
        val authState by authViewModel.state.collectAsState()

        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

        if(authState is AuthState.Authenticated){
            navigator.replace(HomeScreen)
        }

        FinioTheme {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Text(
                    text = "Finio",
                    style = androidx.compose.material3.MaterialTheme.typography.headlineLarge
                )

                Spacer(modifier = Modifier.height(48.dp))

                FinioTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Email",
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                FinioPasswordField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Password",
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                if(authState is AuthState.Error){
                    Text(
                        text = (authState as AuthState.Error).message,
                        color = androidx.compose.ui.graphics.Color.Red
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Spacer(modifier = Modifier.height(24.dp))

                FinioButton(
                    text = if(authState is AuthState.Loading) "Loading..." else "Login",
                    onClick = { authViewModel.login(email, password) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = authState !is AuthState.Loading
                )

                Spacer(modifier = Modifier.height(16.dp))

                FinioButton(
                    text = "Create account",
                    onClick = { navigator.push(RegisterScreen) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}