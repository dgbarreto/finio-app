package dev.finio.app.ui.insights

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import dev.finio.app.ui.home.InsightsScreen
import dev.finio.designsystem.icon.FinioIcons
import org.jetbrains.compose.resources.painterResource

object InsightsTab: Tab{
    override val options: TabOptions
        @Composable
        get() = TabOptions(
            index = 3u,
            title = "Insights",
            icon = painterResource(FinioIcons.insights)
        )

    @Composable
    override fun Content() {
        InsightsScreenContent()
    }
}