package dev.finio.app.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import dev.finio.designsystem.theme.FinioTheme

object HomeScreen: Screen{
    @Composable
    override fun Content() {
        FinioTheme {
            TabNavigator(HomeTab){
                Scaffold(
                    bottomBar = { FinioBottomBar() },
                    content = { padding ->
                        CurrentTab()
                    }
                )
            }
        }
    }
}