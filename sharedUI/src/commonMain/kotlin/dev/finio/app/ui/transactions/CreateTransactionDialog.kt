package dev.finio.app.ui.transactions

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
import dev.finio.designsystem.component.FinioButton
import dev.finio.designsystem.component.FinioTextField
import dev.finio.transactions.domain.model.TransactionCategory
import dev.finio.transactions.domain.model.TransactionType

@Composable
fun CreateTransactionDialog(
    onDismiss: () -> Unit,
    onCreate: (title: String, amount: Double, type: TransactionType, category: TransactionCategory) -> Unit
){
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(TransactionType.EXPENSE) }
    var category by remember { mutableStateOf(TransactionCategory.OTHER) }
    var typeExpanded by remember { mutableStateOf(false) }
    var categoryExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New transaction") },
        text = {
            Column{
                FinioTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = "Title",
                    modifier = Modifier.fillMaxWidth()
                )

                FinioTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = "Amount",
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                )

                TextButton(onClick = { typeExpanded = true }){
                    Text("Type: ${type.name}")
                }
                DropdownMenu(expanded = typeExpanded, onDismissRequest = { typeExpanded = false }){
                    TransactionType.entries.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option.name) },
                            onClick = {
                                type = option
                                typeExpanded = false
                            }
                        )
                    }
                }

                TextButton(onClick = { categoryExpanded = true }){
                    Text("Category: ${category.name}")
                }
                DropdownMenu(expanded = categoryExpanded, onDismissRequest = { categoryExpanded = false }){
                    TransactionCategory.entries.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option.name) },
                            onClick = {
                                category = option
                                categoryExpanded = false
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            FinioButton(
                text = "Create",
                onClick = {
                    val parsedAmount = amount.toDoubleOrNull() ?: 0.0
                    onCreate(title, parsedAmount, type, category)
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