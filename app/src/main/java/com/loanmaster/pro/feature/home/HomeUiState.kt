package com.loanmaster.pro.feature.home
import com.loanmaster.pro.data.local.entity.*
import com.loanmaster.pro.domain.model.*
import com.loanmaster.pro.data.local.entity.CalculationHistory

data class HomeUiState(
    val searchQuery: String = "",
    val activeBottomNavItem: String = "home",
    val isQuickToolsExpanded: Boolean = false,
    val selectedHistory: CalculationHistory? = null
)
