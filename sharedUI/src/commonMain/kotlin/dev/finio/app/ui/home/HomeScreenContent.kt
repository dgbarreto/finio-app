package dev.finio.app.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.finio.transactions.presentation.TransactionViewModel
import org.koin.compose.koinInject

@Composable
fun HomeScreenContent(){
    val transactionViewModel: TransactionViewModel = koinInject()

    LaunchedEffect(Unit){
        transactionViewModel.loadSummary()
    }

    val summary by transactionViewModel.summary.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ){
        Text("Welcome to Finio")

        summary?.let {
            Text("Income: ${it.totalIncome}")
            Text("Expenses: ${it.totalExpenses}")
            Text("Balance: ${it.balance}")
        }
    }
}