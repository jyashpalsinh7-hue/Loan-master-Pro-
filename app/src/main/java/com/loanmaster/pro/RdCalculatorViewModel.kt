package com.loanmaster.pro

import com.loanmaster.pro.ui.theme.*

import com.loanmaster.pro.model.*

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlin.math.pow

data class RdUiState(
    val selectedTab: String = "Standard",
    val monthlyDepositText: String = "",
    val interestRatePaText: String = "",
    val tenureYearsText: String = "",
    val compoundingFrequency: String = "Quarterly",
    val targetAmountText: String = "",
    val currentHistoryId: Int = 0,
    val maturityValue: Double = 0.0,
    val calculatedMonthlyDeposit: Double = 0.0,
    val totalInvested: Double = 0.0,
    val totalReturns: Double = 0.0,
    val wealthGain: Double = 0.0,
    val hasValidInput: Boolean = false
)

sealed class RdEvent {
    data class TabChanged(val tab: String) : RdEvent()
    data class MonthlyDepositChanged(val amount: String) : RdEvent()
    data class InterestRateChanged(val rate: String) : RdEvent()
    data class TenureChanged(val years: String) : RdEvent()
    data class CompoundingFrequencyChanged(val freq: String) : RdEvent()
    data class TargetAmountChanged(val amount: String) : RdEvent()
    data class HistoryIdUpdated(val id: Int) : RdEvent()
    data class InitializeFromHistory(val history: CalculationHistory) : RdEvent()
}

class RdCalculatorViewModel : ViewModel() {
    private val _selectedTab = MutableStateFlow("Standard")
    private val _monthlyDepositText = MutableStateFlow("")
    private val _interestRatePaText = MutableStateFlow("")
    private val _tenureYearsText = MutableStateFlow("")
    private val _compoundingFrequency = MutableStateFlow("Quarterly")
    private val _targetAmountText = MutableStateFlow("")
    private val _currentHistoryId = MutableStateFlow(0)

    val uiState: StateFlow<RdUiState> = combine(
        _selectedTab, _monthlyDepositText, _interestRatePaText, _tenureYearsText,
        _compoundingFrequency, _targetAmountText, _currentHistoryId
    ) { params ->
        val tab = params[0] as String
        val depositStr = params[1] as String
        val rateStr = params[2] as String
        val tenureStr = params[3] as String
        val compFreq = params[4] as String
        val targetStr = params[5] as String
        val historyId = params[6] as Int

        val deposit = depositStr.toDoubleOrNull() ?: 0.0
        val target = targetStr.toDoubleOrNull() ?: 0.0
        val rate = rateStr.toDoubleOrNull() ?: 0.0
        val years = tenureStr.toDoubleOrNull()?.coerceIn(0.0, 100.0) ?: 0.0
        
        val annualRate = rate / 100
        val months = (years * 12).toInt()
        
        val n = when (compFreq) {
            "Yearly" -> 1.0
            "Half-Yearly" -> 2.0
            "Quarterly" -> 4.0
            "Monthly" -> 12.0
            else -> 4.0
        }

        var maturityValue = 0.0
        var calculatedMonthlyDeposit = 0.0
        
        val isValid = if (tab == "Standard") {
            deposit > 0 && rate > 0 && years > 0
        } else {
            target > 0 && rate > 0 && years > 0
        }

        if (tab == "Standard") {
            calculatedMonthlyDeposit = deposit
            if (annualRate > 0 && months > 0) {
                for (i in 1..months) {
                    val remainingTimeYears = (months - i + 1) / 12.0
                    maturityValue += deposit * (1 + annualRate / n).pow(n * remainingTimeYears)
                }
            } else {
                maturityValue = deposit * months
            }
        } else {
            maturityValue = target
            if (annualRate > 0 && months > 0) {
                var sumFactors = 0.0
                for (i in 1..months) {
                    val remainingTimeYears = (months - i + 1) / 12.0
                    sumFactors += (1 + annualRate / n).pow(n * remainingTimeYears)
                }
                if (sumFactors > 0) {
                    calculatedMonthlyDeposit = target / sumFactors
                }
            } else if (months > 0) {
                calculatedMonthlyDeposit = target / months.toDouble()
            }
        }

        val totalInvested = calculatedMonthlyDeposit * months
        val totalReturns = if (maturityValue > totalInvested) maturityValue - totalInvested else 0.0
        val wealthGain = if (totalInvested > 0) (totalReturns / totalInvested) * 100 else 0.0

        RdUiState(
            selectedTab = tab,
            monthlyDepositText = depositStr,
            interestRatePaText = rateStr,
            tenureYearsText = tenureStr,
            compoundingFrequency = compFreq,
            targetAmountText = targetStr,
            currentHistoryId = historyId,
            maturityValue = maturityValue,
            calculatedMonthlyDeposit = calculatedMonthlyDeposit,
            totalInvested = totalInvested,
            totalReturns = totalReturns,
            wealthGain = wealthGain,
            hasValidInput = isValid
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = RdUiState()
    )

    fun onEvent(event: RdEvent) {
        when (event) {
            is RdEvent.TabChanged -> _selectedTab.value = event.tab
            is RdEvent.MonthlyDepositChanged -> _monthlyDepositText.value = event.amount
            is RdEvent.InterestRateChanged -> _interestRatePaText.value = event.rate
            is RdEvent.TenureChanged -> _tenureYearsText.value = event.years
            is RdEvent.CompoundingFrequencyChanged -> _compoundingFrequency.value = event.freq
            is RdEvent.TargetAmountChanged -> _targetAmountText.value = event.amount
            is RdEvent.HistoryIdUpdated -> _currentHistoryId.value = event.id
            is RdEvent.InitializeFromHistory -> {
                _selectedTab.value = if (event.history.param5 == "Target") "Goal Based" else "Standard"
                _monthlyDepositText.value = event.history.param1 ?: ""
                _interestRatePaText.value = event.history.param2 ?: ""
                _tenureYearsText.value = event.history.param3 ?: ""
                _compoundingFrequency.value = event.history.param4?.takeIf { it.isNotEmpty() } ?: "Quarterly"
                _currentHistoryId.value = event.history.id
            }
        }
    }
}
