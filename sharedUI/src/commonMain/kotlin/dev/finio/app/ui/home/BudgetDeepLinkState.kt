package dev.finio.app.ui.home

import androidx.compose.runtime.mutableStateOf

object BudgetDeepLinkState {
    val shouldOpenBudget = mutableStateOf(false)

    fun trigger(){
        shouldOpenBudget.value = true
    }

    fun consume(){
        shouldOpenBudget.value = false
    }
}