package dev.finio.app.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.finio.app.observability.FinioObservability
import dev.finio.budget.presentation.BudgetState
import dev.finio.budget.presentation.BudgetViewModel
import dev.finio.designsystem.component.FinioBody
import dev.finio.designsystem.component.FinioButton
import dev.finio.designsystem.component.FinioButtonVariant
import dev.finio.designsystem.component.FinioCard
import dev.finio.designsystem.component.FinioCardTransaction
import dev.finio.designsystem.component.FinioEmptyState
import dev.finio.designsystem.component.FinioErrorState
import dev.finio.designsystem.component.FinioHeadline
import dev.finio.designsystem.component.FinioLabel
import dev.finio.designsystem.component.FinioTransactionType
import dev.finio.designsystem.icon.FinioIcons
import dev.finio.designsystem.theme.FinioColors
import dev.finio.designsystem.theme.FinioSpacing
import dev.finio.designsystem.theme.FinioTypography
import dev.finio.designsystem.util.FinioFormat
import dev.finio.designsystem.util.FinioFormat.currency
import dev.finio.transactions.domain.model.TransactionType
import dev.finio.transactions.presentation.TransactionState
import dev.finio.transactions.presentation.TransactionViewModel
import org.koin.compose.koinInject

@Composable
fun HomeScreenContent(
    transactionViewModel: TransactionViewModel = koinInject(),
    budgetViewModel: BudgetViewModel = koinInject(),
    onSeeAllTransactions: () -> Unit = {},
    onSeeAllBudgets: () -> Unit = {}
){
    val transactionState by transactionViewModel.state.collectAsState()
    val summary by transactionViewModel.summary.collectAsState()
    val budgetState by budgetViewModel.state.collectAsState()

    var isRefreshing by remember { mutableStateOf(false) }

    LaunchedEffect(Unit){
        transactionViewModel.loadSummary()
    }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = {
            isRefreshing = true
            transactionViewModel.sync()
            transactionViewModel.loadSummary()
            budgetViewModel.load()
        },
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(FinioSpacing.md),
            verticalArrangement = Arrangement.spacedBy(FinioSpacing.lg)
        ) {
            FinioHeadline("Hello 👋")
            FinioBody("Here's your financial summary")

            FinioCard(modifier = Modifier.fillMaxWidth()) {
                summary?.let {
                    Column(verticalArrangement = Arrangement.spacedBy(FinioSpacing.md)) {
                        Text(
                            text = FinioFormat.currency(it.balance),
                            style = FinioTypography.displayMedium,
                            color = if (it.balance >= 0) FinioColors.success else FinioColors.error
                        )
                        FinioLabel("Current balance")

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = FinioSpacing.xs),
                            color = FinioColors.divider
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                FinioLabel("Income")
                                Text(
                                    FinioFormat.currency(it.totalIncome),
                                    style = FinioTypography.titleSmall,
                                    color = FinioColors.success
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                FinioLabel("Expenses")
                                Text(
                                    FinioFormat.currency(it.totalExpenses),
                                    style = FinioTypography.titleSmall,
                                    color = FinioColors.error
                                )
                            }
                        }
                    }
                } ?: Box(
                    modifier = Modifier.fillMaxWidth().height(FinioSpacing.xxl),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator(color = FinioColors.primary) }
            }

            SectionHeader(title = "Recent Transactions", onSeeAll = onSeeAllTransactions)

            when (val s = transactionState) {
                is TransactionState.Loading -> Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator(color = FinioColors.primary) }

                is TransactionState.Error -> {
                    FinioObservability.captureError(s.message)

                    FinioErrorState(
                        message = s.message,
                        onRetry = { transactionViewModel.sync() }
                    )
                }

                is TransactionState.Success -> {
                    if (s.transactions.isEmpty()) {
                        FinioEmptyState(
                            icon = FinioIcons.emptyTransactions,
                            title = "No transactions yet",
                            message = "Add your first transaction in the Transactions tab."
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(FinioSpacing.xs)) {
                            s.transactions.take(5).forEach { transaction ->
                                FinioCardTransaction(
                                    description = transaction.title,
                                    category = transaction.category.name,
                                    amount = FinioFormat.currency(transaction.amount),
                                    date = transaction.date ?: "",
                                    type = if (transaction.type == TransactionType.INCOME)
                                        FinioTransactionType.Income
                                    else
                                        FinioTransactionType.Expense
                                )
                            }
                        }
                    }
                }
            }

            SectionHeader(title = "Budget Overview", onSeeAll = onSeeAllBudgets)

            when (val current = budgetState) {
                is BudgetState.Loading -> Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator(color = FinioColors.primary) }

                is BudgetState.Error -> {
                    FinioObservability.captureError(current.message)

                    FinioErrorState(
                        message = current.message,
                        onRetry = { budgetViewModel.load() }
                    )
                }

                is BudgetState.Success -> {
                    if (current.budgets.isEmpty()) {
                        FinioEmptyState(
                            icon = FinioIcons.emptyBudgets,
                            title = "No budgets yet",
                            message = "Create a budget in the Budget tab."
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(FinioSpacing.xs)) {
                            current.budgets.take(3).forEach {
                                FinioCard {
                                    Column(verticalArrangement = Arrangement.spacedBy(FinioSpacing.xxs)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            FinioBody(it.category)
                                            FinioLabel(
                                                "${FinioFormat.currency(it.spent)} / ${FinioFormat.currency(it.limit)}"
                                            )
                                        }
                                        LinearProgressIndicator(
                                            progress = {
                                                (it.spent / it.limit).toFloat().coerceIn(0f, 1f)
                                            },
                                            modifier = Modifier.fillMaxWidth(),
                                            color = if (it.spent > it.limit) FinioColors.error else FinioColors.primary,
                                            trackColor = FinioColors.surfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(transactionState, budgetState){
        if(transactionState !is TransactionState.Loading && budgetState !is BudgetState.Loading){
            isRefreshing = false
        }
    }
}

@Composable
private fun SectionHeader(title: String, onSeeAll: () -> Unit){
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        Text(title, style = FinioTypography.titleMedium, color = FinioColors.onBackground)
        Text(
            "See all",
            style = FinioTypography.labelMedium,
            color = FinioColors.primary,
            modifier = Modifier.clickable { onSeeAll() }
        )
    }
}
