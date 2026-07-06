package com.loanmaster.pro.feature.rd

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.loanmaster.pro.domain.calculator.RdCalculator
import com.loanmaster.pro.data.local.entity.CalculationHistory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


class RdViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(RdUiState())
    val uiState: StateFlow<RdUiState> = _uiState.asStateFlow()

    private val calculator = RdCalculator()

        fun updateInputs(
        tab: Int? = null,
        monthlyDeposit: String? = null,
        interestRate: String? = null,
        tenureYears: String? = null,
        frequency: String? = null,
        targetAmount: String? = null,
        historyId: Int? = null,
        history: CalculationHistory? = null
    ) {
        if (history != null) {
            updateState { 
                it.copy(
                    selectedTab = if (history.param5 == "Target") 1 else 0,
                    monthlyDepositText = history.param1 ?: "",
                    interestRatePaText = history.param2 ?: "",
                    tenureYearsText = history.param3 ?: "",
                    compoundingFrequency = history.param4 ?: "Quarterly",
                    targetAmountText = if (history.param5 == "Target") history.param1 ?: "" else "",
                    currentHistoryId = history.id
                )
            }
            return
        }
        
        updateState { current ->
            current.copy(
                selectedTab = tab ?: current.selectedTab,
                monthlyDepositText = monthlyDeposit ?: current.monthlyDepositText,
                interestRatePaText = interestRate ?: current.interestRatePaText,
                tenureYearsText = tenureYears ?: current.tenureYearsText,
                compoundingFrequency = frequency ?: current.compoundingFrequency,
                targetAmountText = targetAmount ?: current.targetAmountText,
                currentHistoryId = historyId ?: current.currentHistoryId
            )
        }
    }

    private fun updateState(update: (RdUiState) -> RdUiState) {
        _uiState.update { current ->
            val newState = update(current)
            val result = calculator.calculate(
                tab = newState.selectedTab,
                depositStr = newState.monthlyDepositText,
                rateStr = newState.interestRatePaText,
                tenureStr = newState.tenureYearsText,
                compFreq = newState.compoundingFrequency,
                targetStr = newState.targetAmountText
            )
            newState.copy(
                maturityValue = result.maturityValue,
                calculatedMonthlyDeposit = result.calculatedMonthlyDeposit,
                totalInvested = result.totalInvested,
                totalReturns = result.totalReturns,
                wealthGain = result.wealthGain,
                hasValidInput = result.isValid
            )
        }
    }
}
