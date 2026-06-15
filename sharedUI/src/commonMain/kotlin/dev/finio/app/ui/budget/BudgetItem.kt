package dev.finio.app.ui.budget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.unit.dp
import dev.finio.budget.domain.model.Budget

@Composable
fun BudgetItem(budget: Budget, onDelete: () -> Unit){
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)){
        Column(modifier = Modifier.padding(16.dp)){
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                Text(budget.category)
                IconButton(onClick = onDelete){
                    Icon(Icons.Filled.Delete, contentDescription = "Delete Budget")
                }
            }

            Text("Limit: ${budget.limit}")
            Text("Spent: ${budget.spent} (${budget.percentage}%)")
            Text("Remaining: ${budget.remaining}")

            val progressColor = if(budget.exceeded) Color(0xFFC62828) else Color(0xFF2E7D32)

            LinearProgressIndicator(
                progress = { (budget.percentage / 100f).coerceIn(0f, 1f) },
                color = progressColor,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )
        }
    }
}