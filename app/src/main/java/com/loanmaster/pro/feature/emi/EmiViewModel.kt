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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.loanmaster.pro.domain.calculator.EmiCalculator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.loanmaster.pro.feature.loaneligibility.util.loanProfiles


class EmiViewModel(
    private val historyRepository: HistoryRepository? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmiUiState())
    val uiState: StateFlow<EmiUiState> = _uiState.asStateFlow()

    private val emiCalculator = EmiCalculator()

    fun updateInputs(
        loanAmount: String? = null,
        interestRate: String? = null,
        tenureInput: String? = null,
        isTenureMonths: Boolean? = null,
        type: String? = null
    ) {
        _uiState.update { current ->
            var newInterest = interestRate ?: current.interestRateText
            var newTenure = tenureInput ?: current.tenureInputText
            var newIsMonths = isTenureMonths ?: current.isTenureInMonths
            
            // Auto-fill default interest and tenure if type changes and current inputs are empty,
            // or if we just want to reset them to standard when changing type
            if (type != null && type != current.loanType) {
                val profile = loanProfiles.find { it.name == type }
                if (profile != null) {
                    newInterest = profile.defaultRate
                    newTenure = profile.defaultTenure
                    newIsMonths = false // default tenure is in years
                }
            }

            val next = current.copy(
                loanAmountText = loanAmount ?: current.loanAmountText,
                interestRateText = newInterest,
                tenureInputText = newTenure,
                isTenureInMonths = newIsMonths,
                loanType = type ?: current.loanType
            )
            calculateAndUpdateState(next)
        }
    }

    fun saveCurrentCalculation() {
        val state = _uiState.value
        if (!state.hasValidInput || historyRepository == null) return

        val history = CalculationHistory(
            id = if (state.currentHistoryId > 0) state.currentHistoryId else 0,
            calculatorType = "EMI",
            title = state.loanType,
            param1 = state.loanAmountText,
            param2 = state.interestRateText,
            param3 = state.tenureInputText,
            result1 = state.monthlyEmi,
            result2 = state.totalInterest,
            timestamp = System.currentTimeMillis()
        )

        viewModelScope.launch {
            val id = historyRepository.saveHistory(history)
            _uiState.update { it.copy(currentHistoryId = id.toInt(), isSavedSuccessfully = true) }
        }
    }
    
    fun updateHistoryId(id: Int) {
        _uiState.update { it.copy(currentHistoryId = id) }
    }

    fun loadFromHistory(history: CalculationHistory) {
        _uiState.update { current ->
            val next = current.copy(
                currentHistoryId = history.id,
                loanType = history.title ?: "Home Loan",
                loanAmountText = history.param1 ?: "",
                interestRateText = history.param2 ?: "",
                tenureInputText = history.param3 ?: "",
                isTenureInMonths = false
            )
            calculateAndUpdateState(next)
        }
    }

    private fun calculateAndUpdateState(state: EmiUiState): EmiUiState {
        val loanAmt = state.loanAmountText.toDoubleOrNull()
        val loanAmountError = if (state.loanAmountText.isNotEmpty()) {
            if (loanAmt == null) "Invalid numeric entry"
            else if (loanAmt <= 0) "Must be greater than 0"
            else null
        } else null

        val interestRate = state.interestRateText.toDoubleOrNull()
        val interestRateError = if (state.interestRateText.isNotEmpty()) {
            if (interestRate == null) "Invalid numeric entry"
            else if (interestRate <= 0) "Must be greater than 0"
            else if (interestRate > 100) "Max rate is 100%"
            else null
        } else null

        val tenureVal = state.tenureInputText.toIntOrNull()
        val tenureError = if (state.tenureInputText.isNotEmpty()) {
            if (tenureVal == null) "Invalid numeric entry"
            else if (tenureVal <= 0) "Must be greater than 0"
            else if (state.isTenureInMonths && tenureVal > 1200) "Max tenure is 1200 months"
            else if (!state.isTenureInMonths && tenureVal > 100) "Max tenure is 100 years"
            else null
        } else null

        val result = emiCalculator.calculateFull(
            loanAmount = state.loanAmountText,
            interestRate = state.interestRateText,
            tenureInput = state.tenureInputText,
            isTenureInMonths = state.isTenureInMonths,
            loanType = state.loanType
        )

        return state.copy(
            loanAmountError = loanAmountError,
            interestRateError = interestRateError,
            tenureError = tenureError,
            parsedLoanAmount = result.parsedLoanAmount,
            parsedInterestRate = result.parsedInterestRate,
            parsedTenureYears = result.parsedTenureYears,
            totalMonths = result.totalMonths,
            hasValidInput = result.hasValidInput,
            monthlyEmi = result.monthlyEmi,
            totalInterest = result.totalInterest,
            totalPayment = result.totalPayment,
            principalPercentage = result.principalPercentage,
            interestPercentage = result.interestPercentage,
            monthlySchedule = result.monthlySchedule,
            yearBreakdown = result.yearBreakdown,
            recommendations = result.recommendations,
            alerts = result.alerts,
            opportunities = result.opportunities
        )
    }
}

class EmiCalculatorViewModelFactory(
    private val historyRepository: HistoryRepository?
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EmiViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EmiViewModel(historyRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
