package com.loanmaster.pro.feature.sip
import com.loanmaster.pro.data.local.entity.*
import com.loanmaster.pro.domain.model.*

import com.loanmaster.pro.domain.model.YearlyData

data class SipUiState(
    val amountText: String = "",
    val returnRateText: String = "",
    val yearsText: String = "",
    val stepUpText: String = "",
    val currentHistoryId: Int = 0,
    val totalInvested: Double = 0.0,
    val totalGain: Double = 0.0,
    val maturityValue: Double = 0.0,
    val yearlyDataList: List<YearlyData> = emptyList(),
    val hasValidInput: Boolean = false,
    val inflationAdjustedValue: Double = 0.0,
    val isPremiumUnlocked: Boolean = false,
    val premiumInsights: List<String> = emptyList()
)
