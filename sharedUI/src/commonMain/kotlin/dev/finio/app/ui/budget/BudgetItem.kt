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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.unit.dp
import dev.finio.budget.domain.model.Budget
import dev.finio.budget.domain.repository.BudgetRepositoryImpl
import dev.finio.designsystem.component.FinioButton
import dev.finio.designsystem.component.FinioButtonVariant
import dev.finio.designsystem.component.FinioCard
import dev.finio.designsystem.component.FinioHeadline
import dev.finio.designsystem.component.FinioLabel
import dev.finio.designsystem.theme.FinioColors
import dev.finio.designsystem.theme.FinioSpacing
import dev.finio.designsystem.util.FinioFormat

@Composable
fun BudgetItem(budget: Budget, onDelete: () -> Unit, onChange: ((Budget) -> Unit)?){
    FinioCard(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    ){
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Column(modifier = Modifier.weight(1f)){
                FinioHeadline(budget.category)
                FinioLabel("${budget.spent} of ${budget.limit}")
                FinioLabel("${FinioFormat.date(budget.startDate)} → ${FinioFormat.date(budget.endDate)}")
            }
            Row {
                FinioButton(
                    text = "Edit",
                    onClick = { onChange?.invoke(budget) },
                    variant = FinioButtonVariant.Ghost
                )
                FinioButton(
                    text = "Delete",
                    onClick = onDelete,
                    variant = FinioButtonVariant.Ghost
                )
            }
        }
        LinearProgressIndicator(
            progress = { (budget.spent / budget.limit).toFloat().coerceIn(0f, 1f) },
            modifier = Modifier.fillMaxWidth().padding(top = FinioSpacing.xs),
            color = if(budget.exceeded) FinioColors.error else FinioColors.primary,
            trackColor = FinioColors.surfaceVariant
        )
    }
}