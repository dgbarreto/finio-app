package dev.finio.app.ui.budget

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import dev.finio.designsystem.icon.FinioIcons
import org.jetbrains.compose.resources.painterResource

object BudgetTab: Tab{
    override val options: TabOptions
        @Composable
        get() = TabOptions(
            index = 2u,
            title = "Budget",
            icon = painterResource(FinioIcons.budget)
        )

    @Composable
    override fun Content() {
        BudgetScreenContent()
    }
}