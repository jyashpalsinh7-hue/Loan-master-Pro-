package com.loanmaster.pro.feature.history
import com.loanmaster.pro.data.local.entity.*
import com.loanmaster.pro.domain.model.*

data class HistoryUiState(
    val historyList: List<CalculationHistory> = emptyList()
)
