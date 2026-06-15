package dev.finio.app.ui.budget

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.finio.budget.domain.model.BudgetPeriod
import dev.finio.designsystem.component.FinioButton
import dev.finio.designsystem.component.FinioTextField

@Composable
fun CreateBudgetDialog(
    onDismiss: () -> Unit,
    onCreate: (category: String, limit: Double, period: BudgetPeriod, startDate: String, endDate: String) -> Unit
){
    var category by remember { mutableStateOf("") }
    var limit by remember { mutableStateOf("") }
    var period by remember { mutableStateOf(BudgetPeriod.MONTHLY) }
    var periodExpanded by remember { mutableStateOf(false) }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Budget") },
        text = {
            Column{
                FinioTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = "Category",
                    modifier = Modifier.fillMaxWidth()
                )

                FinioTextField(
                    value = limit,
                    onValueChange = { limit = it },
                    label = "Limit",
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                )

                TextButton(
                    onClick = { periodExpanded = true }
                ){
                    Text("Period: ${period.name}")
                }

                DropdownMenu(
                    expanded = periodExpanded,
                    onDismissRequest = { periodExpanded = false }
                ){
                    BudgetPeriod.entries.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option.name) },
                            onClick = {
                                period = option
                                periodExpanded = false
                            }
                        )
                    }
                }

                FinioTextField(
                    value = startDate,
                    onValueChange = { startDate = it },
                    label = "Start Date (YYYY-MM-DD)",
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                )

                FinioTextField(
                    value = endDate,
                    onValueChange = { endDate = it },
                    label = "End Date (YYYY-MM-DD)",
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                )
            }
        },
        confirmButton = {
            FinioButton(
                text = "Create",
                onClick = {
                    val limitValue = limit.toDoubleOrNull() ?: 0.0
                    onCreate(category, limitValue, period, startDate, endDate)
                }
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss){
                Text("Cancel")
            }
        }
    )
}