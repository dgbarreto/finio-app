package dev.finio.app.ui.auth

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions

object ProfileTab: Tab{
    override val options: TabOptions
        @Composable
        get() = TabOptions(
            index = 4u,
            title = "Profile",
            icon = rememberVectorPainter(Icons.Filled.VerifiedUser)
        )

    @Composable
    override fun Content() {
        ProfileScreenContent()
    }

}