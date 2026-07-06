package com.loanmaster.pro.feature.prepayment

import com.loanmaster.pro.domain.model.*
import com.loanmaster.pro.domain.calculator.PrepaymentCalculator
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
import com.loanmaster.pro.core.formatter.*
import com.loanmaster.pro.feature.fd.*
import com.loanmaster.pro.data.repository.*
import com.loanmaster.pro.feature.currency.*
import com.loanmaster.pro.core.navigation.*
import com.loanmaster.pro.feature.compare.*
import com.loanmaster.pro.feature.loaneligibility.*
import com.loanmaster.pro.core.responsive.*
import com.loanmaster.pro.feature.home.*

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*


class PrepaymentViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(PrepaymentCalculatorUiState())
    val uiState: StateFlow<PrepaymentCalculatorUiState> = _uiState.asStateFlow()

    private val calculator = PrepaymentCalculator()

        fun updateInputs(
        loanAmount: String? = null,
        interestRate: String? = null,
        tenureYears: String? = null,
        prepaymentAmount: String? = null,
        strategy: String? = null,
        monthlyPrepayment: String? = null,
        annualPrepayment: String? = null,
        historyId: Int? = null,
        history: CalculationHistory? = null
    ) {
        if (history != null) {
            updateState { 
                it.copy(
                    loanAmountText = history.param1 ?: "",
                    interestRateText = history.param2 ?: "",
                    tenureYearsText = history.param3 ?: "",
                    prepaymentAmountText = history.param4 ?: "",
                    strategy = history.param5?.takeIf { it.isNotEmpty() } ?: "Tenure",
                    currentHistoryId = history.id
                )
            }
            return
        }
        
        updateState { current ->
            current.copy(
                loanAmountText = loanAmount ?: current.loanAmountText,
                interestRateText = interestRate ?: current.interestRateText,
                tenureYearsText = tenureYears ?: current.tenureYearsText,
                prepaymentAmountText = prepaymentAmount ?: current.prepaymentAmountText,
                strategy = strategy ?: current.strategy,
                monthlyPrepaymentText = monthlyPrepayment ?: current.monthlyPrepaymentText,
                annualPrepaymentText = annualPrepayment ?: current.annualPrepaymentText,
                currentHistoryId = historyId ?: current.currentHistoryId
            )
        }
    }

    private fun updateState(update: (PrepaymentCalculatorUiState) -> PrepaymentCalculatorUiState) {
        _uiState.update { current ->
            val newState = update(current)
            val result = calculator.calculate(
                loanAmount = newState.loanAmountText,
                rateStr = newState.interestRateText,
                tenureStr = newState.tenureYearsText,
                prepayStr = newState.prepaymentAmountText,
                strategy = newState.strategy,
                monthlyStr = newState.monthlyPrepaymentText,
                annualStr = newState.annualPrepaymentText
            )
            newState.copy(
                originalEmi = result.originalEmi,
                originalTotalPayment = result.originalTotalPayment,
                originalTotalInterest = result.originalTotalInterest,
                newEmi = result.newEmi,
                newTenureMonths = result.newTenureMonths,
                newTotalInterest = result.newTotalInterest,
                interestSaved = result.interestSaved,
                tenureReducedMonths = result.tenureReducedMonths,
                emiReduced = result.emiReduced,
                hasValidInput = result.isValid,
                standardSchedule = result.standardSchedule,
                prepaySchedule = result.prepaySchedule
            )
        }
    }
}
