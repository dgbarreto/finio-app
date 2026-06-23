package dev.finio.app.ui.home

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import dev.finio.app.ui.auth.ProfileTab
import dev.finio.app.ui.budget.BudgetTab
import dev.finio.app.ui.insights.InsightsTab
import dev.finio.app.ui.transactions.TransactionsTab
import dev.finio.designsystem.icon.FinioIcons
import dev.finio.designsystem.theme.FinioColors
import dev.finio.designsystem.theme.FinioTypography
import org.jetbrains.compose.resources.painterResource

@Composable
fun FinioBottomBar(){
    val tabNavigator = LocalTabNavigator.current

    val tabIcons = mapOf(
        HomeTab to FinioIcons.homeFill,
        TransactionsTab to FinioIcons.transactionsFill,
        BudgetTab to FinioIcons.budgetFill,
        InsightsTab to FinioIcons.insightsFill,
        ProfileTab to FinioIcons.profileFill
    )

    NavigationBar {
        tabIcons.forEach { (tab, icon) ->
            val selected = tabNavigator.current == tab
            val iconPainter = if(selected){
                painterResource(icon)
            } else {
                tab.options.icon!!
            }

            NavigationBarItem(
                selected = tabNavigator.current == tab,
                onClick = { tabNavigator.current = tab },
                icon = {
                    Icon(
                        painter = iconPainter,
                        contentDescription = tab.options.title,
                        tint = if(selected) FinioColors.primary else FinioColors.subtle)
               },
                label = {
                    Text(
                        tab.options.title,
                        style = FinioTypography.labelSmall,
                        color = if(selected) FinioColors.primary else FinioColors.subtle
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = FinioColors.surfaceVariant
                )
            )
        }
    }
}