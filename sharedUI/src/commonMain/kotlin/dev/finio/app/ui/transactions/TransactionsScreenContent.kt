package dev.finio.app.ui.transactions

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.LineHeightStyle
import dev.finio.designsystem.component.FinioEmptyState
import dev.finio.designsystem.component.FinioErrorState
import dev.finio.transactions.domain.model.Transaction
import dev.finio.transactions.presentation.TransactionState
import dev.finio.transactions.presentation.TransactionViewModel
import org.koin.compose.koinInject
import kotlin.time.Clock

@Composable
fun TransactionsScreenContent(){
    val viewModel: TransactionViewModel = koinInject()
    val state by viewModel.state.collectAsState()
    var showCreatedDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit){
        viewModel.sync()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreatedDialog = true }){
                Icon(Icons.Filled.Add, contentDescription = "Add Transaction")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ){
            when(val current = state){
                is TransactionState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                        CircularProgressIndicator()
                    }
                }
                is TransactionState.Error -> {
                    FinioErrorState(
                        message = current.message,
                        onRetry = { viewModel.sync() }
                    )
                }
                is TransactionState.Success -> {
                    if(current.transactions.isEmpty()){
                        FinioEmptyState(
                            icon = "💸",
                            title = "No transactions yet",
                            message = "Tap the + button to add your first transaction"
                        )
                    } else {
                        TransactionList(transactions = current.transactions)
                    }
                }
            }
        }

        if(showCreatedDialog){
            CreateTransactionDialog(
                onDismiss = { showCreatedDialog = false },
                onCreate = { title, amount, type, category ->
                    viewModel.createTransaction(
                        title = title,
                        amount = amount,
                        type = type,
                        category = category,
                        date = Clock.System.now().toString()
                    )
                    showCreatedDialog = false
                }
            )
        }
    }
}
