package dev.finio.app.ui.transactions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.finio.designsystem.component.FinioCardTransaction
import dev.finio.designsystem.component.FinioTransactionType
import dev.finio.transactions.domain.model.Transaction
import dev.finio.transactions.domain.model.TransactionType

@Composable
fun TransactionList(transactions: List<Transaction>, onTransactionSelected: ((transaction: Transaction) -> Unit)? = null){
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(transactions){ transaction ->
            TransactionItem(transaction, onTransactionSelected)
        }
    }
}

@Composable
fun TransactionItem(transaction: Transaction, onTransactionSelected: ((transaction: Transaction) -> Unit)? = null){
    FinioCardTransaction(
        description = transaction.title,
        category = transaction.category.name,
        amount = transaction.amount.toString(),
        date = transaction.date,
        type = if(transaction.type == TransactionType.INCOME)
            FinioTransactionType.Income else FinioTransactionType.Expense,
        onClick = {
            onTransactionSelected?.invoke(transaction)
        }
    )
}