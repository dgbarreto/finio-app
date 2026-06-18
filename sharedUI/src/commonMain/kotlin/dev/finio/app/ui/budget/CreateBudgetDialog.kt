package dev.finio.app.ui.budget

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.finio.budget.domain.model.Budget
import dev.finio.budget.domain.model.BudgetPeriod
import dev.finio.designsystem.component.FinioBottomSheet
import dev.finio.designsystem.component.FinioButton
import dev.finio.designsystem.component.FinioTextField
import dev.finio.designsystem.theme.FinioColors
import dev.finio.designsystem.theme.FinioShape
import dev.finio.designsystem.theme.FinioSpacing
import dev.finio.designsystem.theme.FinioTypography
import dev.finio.transactions.domain.model.TransactionCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateBudgetDialog(
    onDismiss: () -> Unit,
    onConfirm: (category: String, limit: Double, period: BudgetPeriod, startDate: String, endDate: String) -> Unit,
    editingBudget: Budget? = null
){
    val isEditing = editingBudget != null
    var category by remember { mutableStateOf(editingBudget?.category ?: "") }
    var limit by remember { mutableStateOf(editingBudget?.limit?.toString() ?: "") }
    var period by remember { mutableStateOf(editingBudget?.period ?: BudgetPeriod.MONTHLY) }
    var startDate by remember { mutableStateOf(editingBudget?.startDate ?: "") }
    var endDate by remember { mutableStateOf(editingBudget?.endDate ?: "") }
    var periodExpanded by remember { mutableStateOf(false) }
    var cateogoryExpanded by remember { mutableStateOf(false) }

    FinioBottomSheet(
        onDismiss = onDismiss,
        title = if(isEditing) "Edit Budget" else "New Budget"
    ){
        Column{
            ExposedDropdownMenuBox(expanded = cateogoryExpanded, onExpandedChange = { cateogoryExpanded = it }){
                OutlinedTextField(
                    value = category,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category", style = FinioTypography.labelSmall) },
                    textStyle = FinioTypography.bodyMedium,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = cateogoryExpanded) },
                    shape = FinioShape.sm,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FinioColors.primary,
                        unfocusedBorderColor = FinioColors.divider,
                        focusedTextColor = FinioColors.onBackground,
                        unfocusedTextColor = FinioColors.onBackground
                    ),
                    modifier = Modifier
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable)
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = cateogoryExpanded,
                    onDismissRequest = { cateogoryExpanded = false },
                    containerColor = FinioColors.surface
                ){
                    TransactionCategory.entries.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option.name.lowercase(), style = FinioTypography.bodyMedium, color = FinioColors.onBackground) },
                            onClick = { category = option.name; cateogoryExpanded = false }
                        )
                    }
                }
            }

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

            ExposedDropdownMenuBox(expanded = periodExpanded, onExpandedChange = { periodExpanded = it }){
                OutlinedTextField(
                    value = period.name.lowercase(),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Type", style = FinioTypography.labelSmall) },
                    textStyle = FinioTypography.bodyMedium,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = periodExpanded) },
                    shape = FinioShape.sm,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FinioColors.primary,
                        unfocusedBorderColor = FinioColors.divider,
                        focusedTextColor = FinioColors.onBackground,
                        unfocusedTextColor = FinioColors.onBackground
                    ),
                    modifier = Modifier
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable)
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = periodExpanded,
                    onDismissRequest = { periodExpanded = false },
                    containerColor = FinioColors.surface
                ){
                    BudgetPeriod.entries.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option.name.lowercase(), style = FinioTypography.bodyMedium, color = FinioColors.onBackground) },
                            onClick = { period = option; periodExpanded = false }
                        )
                    }
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

            Spacer(Modifier.height(FinioSpacing.sm))

            FinioButton(
                text = if (isEditing) "Save" else "Add",
                onClick = {
                    val limitValue = limit.toDoubleOrNull() ?: 0.0
                    onConfirm(category, limitValue, period, startDate, endDate)
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}