package com.loanmaster.pro.feature.loansummary
import com.loanmaster.pro.data.local.entity.*
import com.loanmaster.pro.domain.model.*
import com.loanmaster.pro.data.local.entity.ActiveLoan

data class LoanSummaryUiState(
    val activeLoans: List<ActiveLoan> = emptyList()
)
