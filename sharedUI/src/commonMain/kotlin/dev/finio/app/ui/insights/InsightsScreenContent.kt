package dev.finio.app.ui.insights

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.LineHeightStyle

@Composable
fun InsightsScreenContent(){
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center){
        Text("Insights")
    }
}
