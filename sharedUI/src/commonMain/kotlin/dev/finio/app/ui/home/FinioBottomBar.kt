package dev.finio.app.ui.home

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import dev.finio.app.ui.auth.ProfileTab
import dev.finio.app.ui.budget.BudgetTab
import dev.finio.app.ui.insights.InsightsTab
import dev.finio.app.ui.transactions.TransactionsTab

@Composable
fun FinioBottomBar(){
    val tabNavigator = LocalTabNavigator.current

    NavigationBar {
        listOf(HomeTab, TransactionsTab, BudgetTab, InsightsTab, ProfileTab).forEach { tab ->
            NavigationBarItem(
                selected = tabNavigator.current == tab,
                onClick = { tabNavigator.current = tab },
                icon = { Icon(painter = tab.options.icon!!, contentDescription = tab.options.title) },
                label = { Text(tab.options.title) }
            )
        }
    }
}