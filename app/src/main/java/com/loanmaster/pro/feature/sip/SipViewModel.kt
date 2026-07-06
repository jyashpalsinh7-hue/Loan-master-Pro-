package com.loanmaster.pro.feature.sip

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.loanmaster.pro.domain.calculator.SipCalculator
import com.loanmaster.pro.data.local.entity.CalculationHistory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


class SipViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SipUiState())
    val uiState: StateFlow<SipUiState> = _uiState.asStateFlow()

    private val calculator = SipCalculator()

        fun updateInputs(
        amount: String? = null,
        rate: String? = null,
        years: String? = null,
        stepUp: String? = null,
        historyId: Int? = null,
        history: CalculationHistory? = null
    ) {
        if (history != null) {
            updateState { 
                it.copy(
                    amountText = history.param1 ?: "",
                    returnRateText = history.param2 ?: "",
                    yearsText = history.param3 ?: "",
                    stepUpText = history.param4 ?: "",
                    currentHistoryId = history.id
                )
            }
            return
        }
        
        updateState { current ->
            current.copy(
                amountText = amount ?: current.amountText,
                returnRateText = rate ?: current.returnRateText,
                yearsText = years ?: current.yearsText,
                stepUpText = stepUp ?: current.stepUpText,
                currentHistoryId = historyId ?: current.currentHistoryId
            )
        }
    }

    private fun updateState(update: (SipUiState) -> SipUiState) {
        _uiState.update { current ->
            val newState = update(current)
            val result = calculator.calculate(
                amount = newState.amountText,
                rate = newState.returnRateText,
                years = newState.yearsText,
                stepUp = newState.stepUpText
            )
            newState.copy(
                totalInvested = result.totalInvested,
                totalGain = result.totalGain,
                maturityValue = result.maturityValue,
                yearlyDataList = result.yearlyDataList,
                hasValidInput = result.isValid,
                inflationAdjustedValue = result.inflationAdjustedValue
            )
        }
    }
}
