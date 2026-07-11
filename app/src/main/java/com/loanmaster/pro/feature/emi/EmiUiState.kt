package com.loanmaster.pro.feature.emi

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
import com.loanmaster.pro.feature.loansummary.*
import com.loanmaster.pro.feature.prepayment.*
import com.loanmaster.pro.core.formatter.*
import com.loanmaster.pro.feature.fd.*
import com.loanmaster.pro.data.repository.*
import com.loanmaster.pro.feature.currency.*
import com.loanmaster.pro.core.navigation.*
import com.loanmaster.pro.feature.compare.*
import com.loanmaster.pro.feature.loaneligibility.*
import com.loanmaster.pro.core.responsive.*
import com.loanmaster.pro.feature.home.*

data class EmiUiState(
    val loanAmountText: String = "",
    val interestRateText: String = "",
    val tenureInputText: String = "",
    val isTenureInMonths: Boolean = false,
    val loanType: String = "Home Loan",
    
    val loanAmountError: String? = null,
    val interestRateError: String? = null,
    val tenureError: String? = null,
    
    val parsedLoanAmount: Double = 0.0,
    val parsedInterestRate: Double = 0.0,
    val parsedTenureYears: Int = 0,
    val totalMonths: Int = 0,
    val hasValidInput: Boolean = false,
    
    val monthlyEmi: Double = 0.0,
    val totalInterest: Double = 0.0,
    val totalPayment: Double = 0.0,
    val principalPercentage: Double = 0.0,
    val interestPercentage: Double = 0.0,
    
    val monthlySchedule: List<MonthlyAmortization> = emptyList(),
    val yearBreakdown: List<YearBreakdown> = emptyList(),
    
    val recommendations: List<SmartRecommendation> = emptyList(),
    val alerts: List<SmartAlert> = emptyList(),
    val opportunities: List<SmartOpportunity> = emptyList(),
    
    val currentHistoryId: Int = 0,
    val isSavedSuccessfully: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
