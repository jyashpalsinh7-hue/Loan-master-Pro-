package com.loanmaster.pro.feature.gst
import com.loanmaster.pro.data.local.entity.*
import com.loanmaster.pro.domain.model.*

import com.loanmaster.pro.domain.model.GstMode

data class GstUiState(
    val mode: GstMode = GstMode.ADD,
    val amountText: String = "",
    val selectedRate: Double = 18.0,
    val showAdvanced: Boolean = false,
    val cessRateText: String = "",
    val isIntrastate: Boolean = true,
    val currentHistoryId: Int = 0,
    val baseAmount: Double = 0.0,
    val totalAmount: Double = 0.0,
    val totalGst: Double = 0.0,
    val totalCess: Double = 0.0,
    val cgst: Double = 0.0,
    val sgst: Double = 0.0,
    val igst: Double = 0.0
)
