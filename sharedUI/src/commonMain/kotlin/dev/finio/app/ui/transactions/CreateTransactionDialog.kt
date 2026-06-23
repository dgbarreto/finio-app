package dev.finio.app.ui.transactions

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
import androidx.compose.material3.MenuAnchorType
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
import app.cash.sqldelight.Transacter
import dev.finio.designsystem.component.FinioBottomSheet
import dev.finio.designsystem.component.FinioButton
import dev.finio.designsystem.component.FinioDialog
import dev.finio.designsystem.component.FinioTextField
import dev.finio.designsystem.theme.FinioColors
import dev.finio.designsystem.theme.FinioShape
import dev.finio.designsystem.theme.FinioSpacing
import dev.finio.designsystem.theme.FinioTypography
import dev.finio.transactions.domain.model.Transaction
import dev.finio.transactions.domain.model.TransactionCategory
import dev.finio.transactions.domain.model.TransactionType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTransactionDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, amount: Double, type: TransactionType, category: TransactionCategory) -> Unit,
    editingTransaction: Transaction? = null
){
    val isEditing = editingTransaction != null
    var title by remember { mutableStateOf(editingTransaction?.title ?: "") }
    var amount by remember { mutableStateOf(editingTransaction?.amount?.toString() ?: "") }
    var type by remember { mutableStateOf(editingTransaction?.type ?: TransactionType.EXPENSE) }
    var category by remember { mutableStateOf(editingTransaction?.category ?: TransactionCategory.OTHER) }
    var typeExpanded by remember { mutableStateOf(false) }
    var categoryExpanded by remember { mutableStateOf(false) }

    var titleError by remember { mutableStateOf<String?>(null) }
    var amountError by remember { mutableStateOf<String?>(null) }

    fun validate(): Boolean{
        titleError = if(title.isBlank()) "Title is required" else null
        amountError = when{
            amount.isBlank() -> "Amount is required"
            amount.toDoubleOrNull() == null -> "Amount must be a valid number"
            amount.toDouble() <= 0 -> "Amount must be greater than zero"
            else -> null
        }

        return titleError == null && amountError == null
    }

    FinioBottomSheet(
        onDismiss = onDismiss,
        title = if(isEditing) "Edit Transaction" else "New Transaction"
    ){
        Column{
            FinioTextField(
                value = title,
                onValueChange = {
                    title = it
                    titleError = null
                },
                label = "Title",
                errorText = titleError,
                modifier = Modifier.fillMaxWidth()
            )

            FinioTextField(
                value = amount,
                onValueChange = {
                    amount = it
                    amountError = null
                },
                errorText = amountError,
                label = "Amount",
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )


            ExposedDropdownMenuBox(expanded = typeExpanded, onExpandedChange = { typeExpanded = it }){
                OutlinedTextField(
                    value = type.name.lowercase(),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Type", style = FinioTypography.labelSmall) },
                    textStyle = FinioTypography.bodyMedium,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
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
                    expanded = typeExpanded,
                    onDismissRequest = { typeExpanded = false },
                    containerColor = FinioColors.surface
                ){
                    TransactionType.entries.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option.name.lowercase(), style = FinioTypography.bodyMedium, color = FinioColors.onBackground) },
                            onClick = { type = option; typeExpanded = false }
                        )
                    }
                }
            }

            ExposedDropdownMenuBox(expanded = categoryExpanded, onExpandedChange = { categoryExpanded = it }){
                OutlinedTextField(
                    value = category.name.lowercase(),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Type", style = FinioTypography.labelSmall) },
                    textStyle = FinioTypography.bodyMedium,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
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
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false },
                    containerColor = FinioColors.surface
                ){
                    TransactionCategory.entries.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option.name.lowercase(), style = FinioTypography.bodyMedium, color = FinioColors.onBackground) },
                            onClick = { category = option; typeExpanded = false }
                        )
                    }
                }
            }

            Spacer(Modifier.height(FinioSpacing.sm))

            FinioButton(
                text = if (isEditing) "Save" else "Add",
                onClick = {
                    if(validate()){
                        val parsedAmount = amount.toDoubleOrNull() ?: 0.0
                        onConfirm(title, parsedAmount, type, category)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}