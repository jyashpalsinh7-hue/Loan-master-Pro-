package com.loanmaster.pro.feature.emi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.loanmaster.pro.HistoryRepository
import com.loanmaster.pro.domain.calculator.EmiCalculator
import com.loanmaster.pro.CalculationHistory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Standard ViewModel for LoanMaster Pro calculators.
 * Uses single UiState + Domain layer for clean architecture.
 */
class EmiCalculatorViewModel(
    private val historyRepository: HistoryRepository? = null
) : ViewModel() {

    // ==================== UI STATE ====================
    private val _uiState = MutableStateFlow(EmiUiState())
    val uiState: StateFlow<EmiUiState> = _uiState.asStateFlow()

    // ==================== DOMAIN ====================
    private val emiCalculator = EmiCalculator()

    // ==================== PUBLIC METHODS ====================
    fun updateInputs(
        loanAmount: String? = null,
        interestRate: String? = null,
        tenureYears: String? = null
    ) {
        _uiState.update { currentState ->
            val newState = currentState.copy(
                loanAmountText = loanAmount ?: currentState.loanAmountText,
                interestRateText = interestRate ?: currentState.interestRateText,
                tenureYearsText = tenureYears ?: currentState.tenureYearsText
            )
            calculateAndUpdateState(newState)
        }
    }

    fun saveToHistory() {
        val state = _uiState.value
        if (!state.hasValidInput || historyRepository == null) return

        val history = CalculationHistory(
            calculatorType = "EMI",
            title = "EMI Calculation",
            param1 = state.loanAmountText,
            param2 = state.interestRateText,
            param3 = state.tenureYearsText,
            result1 = state.monthlyEmi,
            result2 = state.totalInterest,
            timestamp = System.currentTimeMillis()
        )

        viewModelScope.launch {
            historyRepository.saveHistory(history)
        }
    }

    fun loadFromHistory(history: CalculationHistory) {
        _uiState.update { currentState ->
            val newState = currentState.copy(
                loanAmountText = history.param1 ?: "",
                interestRateText = history.param2 ?: "",
                tenureYearsText = history.param3 ?: ""
            )
            calculateAndUpdateState(newState)
        }
    }

    // ==================== PRIVATE HELPERS ====================
    private fun calculateAndUpdateState(currentState: EmiUiState): EmiUiState {
        val result = emiCalculator.calculate(
            loanAmount = currentState.loanAmountText,
            interestRate = currentState.interestRateText,
            tenureYears = currentState.tenureYearsText
        )

        return currentState.copy(
            monthlyEmi = result.monthlyEmi,
            totalInterest = result.totalInterest,
            totalPayment = result.totalPayment,
            hasValidInput = result.isValid
        )
    }
}
