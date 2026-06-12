package dev.finio.app.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home

object HomeTab: Tab{
    override val options: TabOptions
        @Composable
        get() = TabOptions(
            index = 0u,
            title = "Home",
            icon = rememberVectorPainter(Icons.Filled.Home)
        )

    @Composable
    override fun Content(){
        HomeScreenContent()
    }
}