package dev.finio.app.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import dev.finio.auth.domain.model.AuthState
import dev.finio.auth.presentation.AuthViewModel
import dev.finio.designsystem.component.FinioBody
import dev.finio.designsystem.component.FinioButton
import dev.finio.designsystem.component.FinioButtonVariant
import dev.finio.designsystem.component.FinioCard
import dev.finio.designsystem.component.FinioDialog
import dev.finio.designsystem.component.FinioHeadline
import dev.finio.designsystem.component.FinioLabel
import dev.finio.designsystem.theme.FinioColors
import dev.finio.designsystem.theme.FinioColors.divider
import dev.finio.designsystem.theme.FinioSpacing
import dev.finio.designsystem.theme.FinioTypography
import org.koin.compose.koinInject

@Composable
fun ProfileScreenContent(){
    val viewModel: AuthViewModel = koinInject()
    val state by viewModel.state.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit){
        viewModel.loadProfile()
    }

    if(showLogoutDialog){
        FinioDialog(
            title = "Log out",
            message = "Are you sure you want to log out?",
            confirmText = "Log out",
            onConfirm = {
                showLogoutDialog = false
                viewModel.logout()
            },
            onDismiss = {
                showLogoutDialog = false
            },
            dismissText = "Cancel",
            isDestructive = true
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(FinioSpacing.md),
        verticalArrangement = Arrangement.spacedBy(FinioSpacing.lg)
    ){
        when(val s = state){
            is AuthState.Loading -> Box(
                modifier = Modifier.fillMaxWidth().padding(vertical = FinioSpacing.xl),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator(color = FinioColors.primary) }

            is AuthState.Error -> FinioBody(s.message)

            is AuthState.Authenticated -> {
                val user = s.user

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(FinioSpacing.sm)
                ){
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(FinioColors.primary),
                        contentAlignment = Alignment.Center
                    ){
                        Text(
                            text = user.name
                                .split(" ")
                                .take(2)
                                .joinToString(" ") { it.first().uppercaseChar().toString()},
                            style = FinioTypography.headlineSmall,
                            color = FinioColors.onPrimary
                        )
                    }

                    FinioHeadline(user.name)
                    FinioLabel(user.email)
                }

                HorizontalDivider(color = divider)

                FinioCard {
                    ProfileRow(label = "Name", value = user.name)
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = FinioSpacing.xs),
                    )
                    ProfileRow(label = "Email", value = user.email)
                }

                Spacer(Modifier.weight(1f))

                FinioButton(
                    text = "Log out",
                    onClick = { showLogoutDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    variant = FinioButtonVariant.Destructive
                )
            }

            else -> Unit
        }
    }
}

@Composable
private fun ProfileRow(label: String, value: String){
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        FinioLabel(label)
        FinioBody(value)
    }
}