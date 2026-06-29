package dev.finio.app.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import dev.finio.app.ui.budget.BudgetTab
import dev.finio.designsystem.theme.FinioTheme

object HomeScreen: Screen{
    @Composable
    override fun Content() {
        FinioTheme {
            TabNavigator(HomeTab){
                LaunchedEffect(BudgetDeepLinkState.shouldOpenBudget.value){
                    if(BudgetDeepLinkState.shouldOpenBudget.value){
                        it.current = BudgetTab
                        BudgetDeepLinkState.consume()
                    }
                }

                Scaffold(
                    bottomBar = { FinioBottomBar() },
                    content = { padding ->
                        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                            CurrentTab()
                        }
                    }
                )
            }
        }
    }
}