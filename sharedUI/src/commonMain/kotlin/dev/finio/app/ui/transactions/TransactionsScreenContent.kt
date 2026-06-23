package dev.finio.app.ui.transactions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.layout.BeyondBoundsLayout
import androidx.compose.ui.modifier.modifierLocalOf
import androidx.compose.ui.text.style.LineHeightStyle
import dev.finio.designsystem.component.FinioEmptyState
import dev.finio.designsystem.component.FinioErrorState
import dev.finio.designsystem.component.FinioTextField
import dev.finio.designsystem.icon.FinioIcons
import dev.finio.designsystem.theme.FinioColors
import dev.finio.designsystem.theme.FinioShape
import dev.finio.designsystem.theme.FinioSpacing
import dev.finio.designsystem.theme.FinioTypography
import dev.finio.transactions.domain.model.Transaction
import dev.finio.transactions.domain.model.TransactionCategory
import dev.finio.transactions.domain.model.TransactionType
import dev.finio.transactions.presentation.TransactionState
import dev.finio.transactions.presentation.TransactionViewModel
import org.koin.compose.koinInject
import kotlin.time.Clock

@Composable
fun TransactionsScreenContent(){
    val viewModel: TransactionViewModel = koinInject()
    val state by viewModel.state.collectAsState()
    var showCreatedDialog by remember { mutableStateOf(false) }
    var editingTransaction by remember { mutableStateOf<Transaction?>(null) }
    var isRefreshing by remember { mutableStateOf(false) }

    var searchQuery by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf<TransactionType?>(null) }
    var selectedCategory by remember { mutableStateOf<TransactionCategory?>(null) }

    LaunchedEffect(Unit){
        viewModel.sync()
    }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = {
            isRefreshing = true
            viewModel.sync()
        },
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = { showCreatedDialog = true }) {
                    Icon(Icons.Filled.Add, contentDescription = "Add Transaction")
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                FinioTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = "Search",
                    placeholder = "Search by title",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = FinioSpacing.md, vertical = FinioSpacing.xs)
                )

                FilterRow(
                    selectedType = selectedType,
                    onTypeSelected = { selectedType = it },
                    selectedCategory = selectedCategory,
                    onCategorySelected = { selectedCategory = it }
                )

                when (val current = state) {
                    is TransactionState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    is TransactionState.Error -> {
                        FinioErrorState(
                            message = current.message,
                            onRetry = { viewModel.sync() },
                            icon = FinioIcons.error
                        )
                    }

                    is TransactionState.Success -> {
                        val filtered = current.transactions.filter {
                            val matchesQuery = searchQuery.isBlank() ||
                                    it.title.contains(searchQuery, ignoreCase = true)
                            val matchesType = selectedType == null || it.type == selectedType
                            val matchesCategory = selectedCategory == null || it.category == selectedCategory

                            matchesQuery && matchesType && matchesCategory
                        }

                        if (filtered.isEmpty()) {
                            FinioEmptyState(
                                icon = FinioIcons.emptyTransactions,
                                title = "No transactions yet",
                                message = "Tap the + button to add your first transaction"
                            )
                        } else {
                            TransactionList(
                                transactions = filtered,
                                onTransactionSelected = {
                                    editingTransaction = it
                                })
                        }
                    }
                }
            }

            if (showCreatedDialog) {
                CreateTransactionDialog(
                    onDismiss = { showCreatedDialog = false },
                    onConfirm = { title, amount, type, category ->
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

            editingTransaction?.let {
                CreateTransactionDialog(
                    editingTransaction = editingTransaction,
                    onDismiss = { editingTransaction = null },
                    onConfirm = { title, amount, type, category ->
                        viewModel.updateTransaction(
                            id = it.id,
                            title = title,
                            amount = amount,
                            type = type,
                            category = category,
                        )
                        editingTransaction = null
                    }
                )
            }
        }
    }

    LaunchedEffect(state){
        if (state !is TransactionState.Loading) isRefreshing = false
    }
}

@Composable
private fun FilterRow(
    selectedType: TransactionType?,
    onTypeSelected: (TransactionType?) -> Unit,
    selectedCategory: TransactionCategory?,
    onCategorySelected: (TransactionCategory?) -> Unit
){
    Column(
        modifier = Modifier.padding(horizontal = FinioSpacing.md)
    ){
        LazyRow(horizontalArrangement = Arrangement.spacedBy(FinioSpacing.xxs)){
            item{
                FilterChipItem(
                    label = "All",
                    selected = selectedType == null,
                    onClick = { onTypeSelected(null) }
                )
            }
            items(TransactionType.entries){
                FilterChipItem(
                    label = it.name,
                    selected = selectedType == it,
                    onClick = { onTypeSelected(if (selectedType == it) null else it) }
                )
            }
        }

        LazyRow(horizontalArrangement = Arrangement.spacedBy(FinioSpacing.xxs)){
            item{
                FilterChipItem(
                    label = "All categories",
                    selected = selectedCategory == null,
                    onClick = { onCategorySelected(null) }
                )
            }
            items(TransactionCategory.entries) {
                FilterChipItem(
                    label = it.name,
                    selected = selectedCategory == it,
                    onClick = { onCategorySelected(if (selectedCategory == it) null else it) }
                )
            }
        }
    }
}

@Composable
private fun FilterChipItem(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
){
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label, style = FinioTypography.labelSmall) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = FinioColors.primary,
            selectedLabelColor = FinioColors.onPrimary,
            containerColor = FinioColors.surfaceVariant,
            labelColor = FinioColors.onSurfaceVariant
        ),
        shape = FinioShape.full
    )
}