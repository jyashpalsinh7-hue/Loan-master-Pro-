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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.loanmaster.pro.domain.calculator.CompoundingFrequency
import com.loanmaster.pro.domain.calculator.FdCalculator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FdViewModel(
    private val historyRepository: HistoryRepository? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(FdUiState())
    val uiState: StateFlow<FdUiState> = _uiState.asStateFlow()

    private val fdCalculator = FdCalculator()

    fun updateInputs(
        depositAmount: String? = null,
        interestRatePa: String? = null,
        tenureYears: String? = null,
        compoundingFreq: String? = null
    ) {
        _uiState.update { current ->
            val newCompounding = compoundingFreq?.let { freqString ->
                CompoundingFrequency.values().find { it.displayName == freqString }
            } ?: current.compounding

            val updated = current.copy(
                depositText = depositAmount ?: current.depositText,
                rateText = interestRatePa ?: current.rateText,
                tenureText = tenureYears ?: current.tenureText,
                compounding = newCompounding
            )
            calculateResults(updated)
        }
    }

    fun saveToHistory() {
        val state = _uiState.value
        if (!state.hasValidInput || historyRepository == null) return

        val history = CalculationHistory(
            id = if (state.currentHistoryId > 0) state.currentHistoryId else 0,
            calculatorType = "FD",
            title = "FD Calculation",
            param1 = state.depositText,
            param2 = state.rateText,
            param3 = state.tenureText,
            param4 = state.compounding.displayName,
            result1 = state.totalInvested,
            result2 = state.maturityValue,
            timestamp = System.currentTimeMillis()
        )

        viewModelScope.launch {
            val id = historyRepository.saveHistory(history)
            _uiState.update { it.copy(currentHistoryId = id.toInt(), isSavedSuccessfully = true) }
        }
    }

    fun loadFromHistory(history: CalculationHistory) {
        _uiState.update { current ->
            val comp = CompoundingFrequency.values().find { it.displayName == history.param4 } ?: CompoundingFrequency.QUARTERLY
            val updated = current.copy(
                currentHistoryId = history.id,
                depositText = history.param1 ?: "",
                rateText = history.param2 ?: "",
                tenureText = history.param3 ?: "",
                compounding = comp
            )
            calculateResults(updated)
        }
    }
    
    fun updateHistoryId(id: Int) {
        _uiState.update { it.copy(currentHistoryId = id) }
    }

    private fun calculateResults(state: FdUiState): FdUiState {
        val deposit = state.depositText.toDoubleOrNull() ?: 0.0
        val rate = state.rateText.toDoubleOrNull() ?: 0.0
        val tenure = state.tenureText.toDoubleOrNull() ?: 0.0
        
        var error: String? = null
        if (state.depositText.isNotEmpty() && deposit <= 0) error = "Deposit must be > 0"
        else if (state.rateText.isNotEmpty() && rate <= 0) error = "Invalid interest rate"
        else if (state.tenureText.isNotEmpty() && tenure <= 0) error = "Invalid tenure"
        else if (tenure > 100) error = "Tenure cannot exceed 100 years"

        val result = fdCalculator.calculate(
            deposit = state.depositText,
            rate = state.rateText,
            tenureYears = state.tenureText,
            frequency = state.compounding
        )

        return state.copy(
            maturityValue = result.maturityValue,
            totalInvested = result.totalInvested,
            totalReturns = result.totalReturns,
            wealthGain = result.wealthGain,
            breakdown = result.breakdown,
            hasValidInput = result.isValid,
            validationError = error
        )
    }
}

class FdCalculatorViewModelFactory(
    private val historyRepository: HistoryRepository?
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FdViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FdViewModel(historyRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
