package com.loanmaster.pro.feature.emi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.loanmaster.pro.HistoryRepository
import com.loanmaster.pro.CalculationHistory
import com.loanmaster.pro.domain.calculator.EmiCalculator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EmiCalculatorViewModel(
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
            val next = current.copy(
                loanAmountText = loanAmount ?: current.loanAmountText,
                interestRateText = interestRate ?: current.interestRateText,
                tenureInputText = tenureInput ?: current.tenureInputText,
                isTenureInMonths = isTenureMonths ?: current.isTenureInMonths,
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
        val result = emiCalculator.calculateFull(
            loanAmount = state.loanAmountText,
            interestRate = state.interestRateText,
            tenureInput = state.tenureInputText,
            isTenureInMonths = state.isTenureInMonths,
            loanType = state.loanType
        )

        return state.copy(
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
        if (modelClass.isAssignableFrom(EmiCalculatorViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EmiCalculatorViewModel(historyRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
