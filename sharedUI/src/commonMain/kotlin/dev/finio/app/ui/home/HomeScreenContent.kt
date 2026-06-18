package dev.finio.app.ui.home

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.finio.budget.presentation.BudgetState
import dev.finio.budget.presentation.BudgetViewModel
import dev.finio.designsystem.component.FinioBody
import dev.finio.designsystem.component.FinioCard
import dev.finio.designsystem.component.FinioEmptyState
import dev.finio.designsystem.component.FinioErrorState
import dev.finio.designsystem.component.FinioHeadline
import dev.finio.designsystem.component.FinioLabel
import dev.finio.designsystem.theme.FinioColors
import dev.finio.designsystem.theme.FinioSpacing
import dev.finio.designsystem.theme.FinioTypography
import dev.finio.transactions.presentation.TransactionViewModel
import org.koin.compose.koinInject

@Composable
fun HomeScreenContent(
    transactionViewModel: TransactionViewModel = koinInject(),
    budgetViewModel: BudgetViewModel = koinInject()
){
    val transactionState by transactionViewModel.state.collectAsState()
    val summary by transactionViewModel.summary.collectAsState()
    val budgetState by budgetViewModel.state.collectAsState()

    LaunchedEffect(Unit){
        transactionViewModel.loadSummary()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(FinioSpacing.md),
        verticalArrangement = Arrangement.spacedBy(FinioSpacing.lg)
    ){
        FinioHeadline("Hello 👋")
        FinioBody("Here's your financial summary")

        FinioCard(modifier = Modifier.fillMaxWidth()){
            summary?.let {
                Column(verticalArrangement = Arrangement.spacedBy(FinioSpacing.md)){
                    Text(
                        text = formatCurrency(it.balance),
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
                    ){
                        Column {
                            FinioLabel("Income")
                            Text(
                                formatCurrency(it.totalIncome),
                                style = FinioTypography.titleSmall,
                                color = FinioColors.success
                            )
                        }
                        Column(horizontalAlignment = Alignment.End){
                            FinioLabel("Expenses")
                            Text(
                                formatCurrency(it.totalExpenses),
                                style = FinioTypography.titleSmall,
                                color = FinioColors.error
                            )
                        }
                    }
                }
            } ?: Box(
                modifier = Modifier.fillMaxWidth().height(FinioSpacing.xxl),
                contentAlignment = Alignment.Center
            ){ CircularProgressIndicator(color = FinioColors.primary) }
        }

        SectionHeader(title = "Budget Overview")

        when(val current = budgetState){
            is BudgetState.Loading -> Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ){ CircularProgressIndicator( color = FinioColors.primary) }

            is BudgetState.Error -> FinioErrorState(
                message = current.message,
                onRetry = { budgetViewModel.load() }
            )

            is BudgetState.Success -> {
                if(current.budgets.isEmpty()){
                    FinioEmptyState(
                        icon = "🎯",
                        title = "No budgets yet",
                        message = "Create a budget in the Budget tab."
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(FinioSpacing.xs)){
                        current.budgets.take(3).forEach {
                            FinioCard{
                                Column(verticalArrangement = Arrangement.spacedBy(FinioSpacing.xxs)){
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ){
                                        FinioBody(it.category)
                                        FinioLabel(
                                            "${formatCurrency(it.spent)} / ${formatCurrency(it.limit)}"
                                        )
                                    }
                                    LinearProgressIndicator(
                                        progress = {
                                            (it.spent / it.limit).toFloat().coerceIn(0f, 1f)
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        color = if(it.spent > it.limit) FinioColors.error else FinioColors.primary,
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

@Composable
private fun SectionHeader(title: String){
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        Text(title, style = FinioTypography.titleMedium, color = FinioColors.onBackground)
    }
}

private fun formatCurrency(value: Double): String {
    val sign = if (value < 0) "-" else ""
    val abs = kotlin.math.abs(value)
    val rounded = kotlin.math.round(abs * 100) / 100
    val intPart = rounded.toLong()
    val decPart = kotlin.math.round((rounded - intPart) * 100).toInt()
    return "$sign$$intPart.${decPart.toString().padStart(2, '0')}"
}