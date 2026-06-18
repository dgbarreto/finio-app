package dev.finio.app.ui.budget

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
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
import androidx.compose.ui.unit.dp
import dev.finio.budget.domain.model.Budget
import dev.finio.budget.presentation.BudgetState
import dev.finio.budget.presentation.BudgetViewModel
import dev.finio.designsystem.component.FinioEmptyState
import dev.finio.designsystem.component.FinioErrorState
import org.koin.compose.koinInject

@Composable
fun BudgetScreenContent(){
    val viewModel: BudgetViewModel = koinInject()
    val state by viewModel.state.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }
    var editingBudget by remember { mutableStateOf<Budget?>(null) }
    var isRefreshing by remember { mutableStateOf(false) }

    LaunchedEffect(Unit){
        viewModel.load()
    }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = {
            isRefreshing = true
            viewModel.load()
        },
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = { showCreateDialog = true }) {
                    Icon(Icons.Filled.Add, contentDescription = "Add Budget")
                }
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                when (val current = state) {
                    is BudgetState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    is BudgetState.Error -> {
                        FinioErrorState(
                            message = current.message,
                            onRetry = { viewModel.load() }
                        )
                    }

                    is BudgetState.Success -> {
                        if (current.budgets.isEmpty()) {
                            FinioEmptyState(
                                icon = "🎯",
                                title = "No budgets yet",
                                message = "Created a budget to start tracking your spending limits."
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(16.dp)
                            ) {
                                items(current.budgets) { budget ->
                                    BudgetItem(
                                        budget = budget,
                                        onDelete = { viewModel.deleteBudget(budget.id) },
                                        onChange = {
                                            editingBudget = it
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (showCreateDialog) {
                CreateBudgetDialog(
                    onDismiss = { showCreateDialog = false },
                    onConfirm = { category, limit, period, startDate, endDate ->
                        viewModel.createBudget(
                            category = category.lowercase(),
                            limit = limit,
                            period = period,
                            startDate = startDate,
                            endDate = endDate
                        )
                        showCreateDialog = false
                    }
                )
            }

            editingBudget?.let {
                CreateBudgetDialog(
                    onDismiss = { editingBudget = null },
                    onConfirm = { category, limit, period, startDate, endDate ->
                        viewModel.updateBudget(
                            id = it.id,
                            category = category.lowercase(),
                            limit = limit,
                            period = period,
                            startDate = startDate,
                            endDate = endDate
                        )
                        editingBudget = null
                    },
                    editingBudget = it
                )
            }
        }
    }

    LaunchedEffect(state){
        if(state !is BudgetState.Loading) isRefreshing = false
    }
}