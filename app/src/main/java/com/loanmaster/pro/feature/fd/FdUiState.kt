package com.loanmaster.pro.feature.fd

import com.loanmaster.pro.domain.model.*
import com.loanmaster.pro.feature.gst.*
import com.loanmaster.pro.feature.sip.*
import com.loanmaster.pro.core.ui.*
import com.loanmaster.pro.feature.history.*
import com.loanmaster.pro.core.theme.*
import com.loanmaster.pro.data.datastore.*
import com.loanmaster.pro.feature.settings.*
import com.loanmaster.pro.feature.rd.*
import com.loanmaster.pro.domain.calculator.*
import com.loanmaster.pro.data.local.entity.*
import com.loanmaster.pro.core.utils.*
import com.loanmaster.pro.data.local.dao.*
import com.loanmaster.pro.data.local.room.*
import com.loanmaster.pro.feature.emi.*
import com.loanmaster.pro.feature.loansummary.*
import com.loanmaster.pro.feature.prepayment.*
import com.loanmaster.pro.core.formatter.*
import com.loanmaster.pro.data.repository.*
import com.loanmaster.pro.feature.currency.*
import com.loanmaster.pro.core.navigation.*
import com.loanmaster.pro.feature.compare.*
import com.loanmaster.pro.feature.loaneligibility.*
import com.loanmaster.pro.core.responsive.*
import com.loanmaster.pro.feature.home.*
import com.loanmaster.pro.domain.calculator.CompoundingFrequency

data class FdUiState(
    val depositText: String = "",
    val rateText: String = "",
    val tenureText: String = "",
    val compounding: CompoundingFrequency = CompoundingFrequency.QUARTERLY,
    
    val maturityValue: Double = 0.0,
    val totalInvested: Double = 0.0,
    val totalInterest: Double = 0.0,
    val wealthGain: Double = 0.0,
    val breakdown: List<FdYearBreakdown> = emptyList(),
    
    val hasValidInput: Boolean = false,
    val validationError: String? = null,
    val currentHistoryId: Int = 0,
    val isSavedSuccessfully: Boolean = false
)
