package com.loanmaster.pro

import com.loanmaster.pro.model.*

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class SipUiState(
    val amountText: String = "",
    val returnRateText: String = "",
    val yearsText: String = "",
    val stepUpText: String = "",
    val currentHistoryId: Int = 0,
    val totalInvested: Double = 0.0,
    val totalGain: Double = 0.0,
    val maturityValue: Double = 0.0,
    val yearlyDataList: List<YearlyData> = emptyList(),
    val hasValidInput: Boolean = false
)

sealed class SipEvent {
    data class AmountChanged(val amount: String) : SipEvent()
    data class ReturnRateChanged(val rate: String) : SipEvent()
    data class YearsChanged(val years: String) : SipEvent()
    data class StepUpChanged(val stepUp: String) : SipEvent()
    data class HistoryIdUpdated(val id: Int) : SipEvent()
    data class InitializeFromHistory(val history: CalculationHistory) : SipEvent()
}

private fun String.toDoubleOrZero() = toDoubleOrNull() ?: 0.0
private fun String.toIntOrZero() = toIntOrNull() ?: 0

class SipCalculatorViewModel : ViewModel() {
    private val _amountText = MutableStateFlow("")
    private val _returnRateText = MutableStateFlow("")
    private val _yearsText = MutableStateFlow("")
    private val _stepUpText = MutableStateFlow("")
    private val _currentHistoryId = MutableStateFlow(0)

    val uiState: StateFlow<SipUiState> = combine(
        _amountText, _returnRateText, _yearsText, _stepUpText, _currentHistoryId
    ) { amount, rate, years, stepUp, historyId ->
        val p = amount.toDoubleOrZero()
        val r = rate.toDoubleOrZero()
        val y = years.toIntOrZero()
        val s = stepUp.toDoubleOrZero()

        val isValid = p > 0 && r > 0 && y > 0

        var totalInvested = 0.0
        var maturityValue = 0.0
        var currentMonthlySip = p
        val monthlyReturnRate = (r / 100.0) / 12.0
        val totalMonths = y * 12
        val stepUpFraction = s / 100.0
        
        val yearlyDataList = mutableListOf<YearlyData>()
        var investedThisYear = 0.0
        
        if (isValid) {
            for (m in 1..totalMonths) {
                totalInvested += currentMonthlySip
                investedThisYear += currentMonthlySip
                maturityValue = (maturityValue + currentMonthlySip) * (1 + monthlyReturnRate)
                if (m % 12 == 0) {
                    val year = m / 12
                    yearlyDataList.add(
                        YearlyData(
                            year = year,
                            investedForYear = investedThisYear,
                            totalInvested = totalInvested,
                            returns = maturityValue - totalInvested,
                            maturity = maturityValue
                        )
                    )
                    investedThisYear = 0.0
                    currentMonthlySip += currentMonthlySip * stepUpFraction
                }
            }
        }
        
        val totalGain = maturityValue - totalInvested

        SipUiState(
            amountText = amount,
            returnRateText = rate,
            yearsText = years,
            stepUpText = stepUp,
            currentHistoryId = historyId,
            totalInvested = totalInvested,
            totalGain = totalGain,
            maturityValue = maturityValue,
            yearlyDataList = yearlyDataList,
            hasValidInput = isValid
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SipUiState()
    )

    fun onEvent(event: SipEvent) {
        when (event) {
            is SipEvent.AmountChanged -> _amountText.value = event.amount
            is SipEvent.ReturnRateChanged -> _returnRateText.value = event.rate
            is SipEvent.YearsChanged -> _yearsText.value = event.years
            is SipEvent.StepUpChanged -> _stepUpText.value = event.stepUp
            is SipEvent.HistoryIdUpdated -> _currentHistoryId.value = event.id
            is SipEvent.InitializeFromHistory -> {
                _amountText.value = event.history.param1 ?: ""
                _returnRateText.value = event.history.param2 ?: ""
                _yearsText.value = event.history.param3 ?: ""
                _stepUpText.value = event.history.param4 ?: ""
                _currentHistoryId.value = event.history.id
            }
        }
    }
}
