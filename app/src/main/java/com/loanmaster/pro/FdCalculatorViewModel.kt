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

data class FdUiState(
    val depositText: String = "",
    val rateText: String = "",
    val tenureText: String = "",
    val compounding: CompoundingFrequency = CompoundingFrequency.QUARTERLY,
    val currentHistoryId: Int = 0,
    val maturityValue: Double = 0.0,
    val totalInvested: Double = 0.0,
    val totalReturns: Double = 0.0,
    val wealthGain: Double = 0.0,
    val breakdown: List<FdYearBreakdown> = emptyList(),
    val hasValidInput: Boolean = false,
    val validationError: String? = null
)

private fun String.toMoney() = toDoubleOrNull() ?: 0.0

class FdCalculatorViewModel : ViewModel() {

    private val _depositText = MutableStateFlow("")
    private val _rateText = MutableStateFlow("")
    private val _tenureText = MutableStateFlow("")
    private val _compounding = MutableStateFlow(CompoundingFrequency.QUARTERLY)
    private val _currentHistoryId = MutableStateFlow(0)

    val uiState: StateFlow<FdUiState> = combine(
        _depositText, _rateText, _tenureText, _compounding, _currentHistoryId
    ) { deposit, rate, tenure, compounding, historyId ->
        val p = deposit.toMoney()
        val r = rate.toMoney()
        val t = tenure.toMoney()

        var error: String? = null
        if (deposit.isNotEmpty() && p <= 0) error = "Deposit must be > 0"
        else if (rate.isNotEmpty() && r <= 0) error = "Invalid interest rate"
        else if (tenure.isNotEmpty() && t <= 0) error = "Invalid tenure"
        else if (t > 100) error = "Tenure cannot exceed 100 years"

        val isValid = p > 0 && r > 0 && t > 0 && t <= 100
        val result = if (isValid) {
            FdCalculator.calculate(p, r, t, compounding)
        } else {
            FdResult(0.0, 0.0, 0.0, 0.0, emptyList())
        }

        FdUiState(
            depositText = deposit,
            rateText = rate,
            tenureText = tenure,
            compounding = compounding,
            currentHistoryId = historyId,
            maturityValue = result.maturityValue,
            totalInvested = result.totalInvested,
            totalReturns = result.totalReturns,
            wealthGain = result.wealthGain,
            breakdown = result.breakdown,
            hasValidInput = isValid,
            validationError = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FdUiState()
    )

    fun updateInputs(
        depositAmount: String? = null,
        interestRatePa: String? = null,
        tenureYears: String? = null,
        compoundingFreq: String? = null
    ) {
        depositAmount?.let { _depositText.value = it }
        interestRatePa?.let { _rateText.value = it }
        tenureYears?.let { _tenureText.value = it }
        compoundingFreq?.let { freqString ->
            CompoundingFrequency.values().find { it.displayName == freqString }?.let {
                _compounding.value = it
            }
        }
    }

    fun updateHistoryId(id: Int) {
        _currentHistoryId.value = id
    }

    fun initializeFromHistory(history: CalculationHistory?) {
        if (history != null) {
            _depositText.value = history.param1 ?: ""
            _rateText.value = history.param2 ?: ""
            _tenureText.value = history.param3 ?: ""
            _compounding.value = CompoundingFrequency.values().find { it.displayName == history.param4 } ?: CompoundingFrequency.QUARTERLY
            _currentHistoryId.value = history.id
        }
    }
}
