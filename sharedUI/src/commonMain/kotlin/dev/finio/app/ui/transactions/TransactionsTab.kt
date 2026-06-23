package dev.finio.app.ui.transactions

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import dev.finio.designsystem.icon.FinioIcons
import org.jetbrains.compose.resources.painterResource

object TransactionsTab: Tab{
    override val options: TabOptions
        @Composable
        get() = TabOptions(
            index = 1u,
            title = "Transactions",
            icon = painterResource(FinioIcons.transactions)
        )

    @Composable
    override fun Content() {
        TransactionsScreenContent()
    }
}