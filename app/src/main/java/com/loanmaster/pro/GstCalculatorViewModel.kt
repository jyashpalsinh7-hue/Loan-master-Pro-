package com.loanmaster.pro

import com.loanmaster.pro.ui.theme.*

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*

data class GstCalculatorUiState(
    val mode: GstMode = GstMode.ADD,
    val amountText: String = "",
    val selectedRate: Double = 18.0,
    val showAdvanced: Boolean = false,
    val cessRateText: String = "",
    val isIntrastate: Boolean = true,
    val currentHistoryId: Int = 0,
    
    // Calculated fields
    val baseAmount: Double = 0.0,
    val totalAmount: Double = 0.0,
    val totalGst: Double = 0.0,
    val totalCess: Double = 0.0,
    val cgst: Double = 0.0,
    val sgst: Double = 0.0,
    val igst: Double = 0.0
)

sealed class GstCalculatorEvent {
    data class ModeChanged(val mode: GstMode) : GstCalculatorEvent()
    data class AmountChanged(val amount: String) : GstCalculatorEvent()
    data class RateChanged(val rate: Double) : GstCalculatorEvent()
    data class ShowAdvancedToggled(val show: Boolean) : GstCalculatorEvent()
    data class CessRateChanged(val rate: String) : GstCalculatorEvent()
    data class IntrastateToggled(val isIntrastate: Boolean) : GstCalculatorEvent()
    data class InitializeFromHistory(val history: CalculationHistory) : GstCalculatorEvent()
    data class HistoryIdUpdated(val id: Int) : GstCalculatorEvent()
    object Reset : GstCalculatorEvent()
}

class GstCalculatorViewModel : ViewModel() {
    private val _mode = MutableStateFlow(GstMode.ADD)
    private val _amountText = MutableStateFlow("")
    private val _selectedRate = MutableStateFlow(18.0)
    private val _showAdvanced = MutableStateFlow(false)
    private val _cessRateText = MutableStateFlow("")
    private val _isIntrastate = MutableStateFlow(true)
    private val _currentHistoryId = MutableStateFlow(0)
    
    val uiState: StateFlow<GstCalculatorUiState> = combine(
        _mode, _amountText, _selectedRate, _showAdvanced,
        _cessRateText, _isIntrastate, _currentHistoryId
    ) { params ->
        val mode = params[0] as GstMode
        val amountText = params[1] as String
        val selectedRate = params[2] as Double
        val showAdvanced = params[3] as Boolean
        val cessRateText = params[4] as String
        val isIntrastate = params[5] as Boolean
        val currentHistoryId = params[6] as Int
        
        val amount = amountText.toDoubleOrNull() ?: 0.0
        val actualRate = selectedRate
        val cessRate = cessRateText.toDoubleOrNull() ?: 0.0
        
        val baseAmount: Double
        val totalAmount: Double
        if (mode == GstMode.ADD) {
            baseAmount = amount
            totalAmount = amount * (1 + (actualRate + cessRate) / 100.0)
        } else {
            totalAmount = amount
            baseAmount = amount / (1 + (actualRate + cessRate) / 100.0)
        }
        val totalGst = baseAmount * (actualRate / 100.0)
        val totalCess = baseAmount * (cessRate / 100.0)
        val cgst = if (isIntrastate) totalGst / 2 else 0.0
        val sgst = if (isIntrastate) totalGst / 2 else 0.0
        val igst = if (!isIntrastate) totalGst else 0.0
        
        GstCalculatorUiState(
            mode = mode,
            amountText = amountText,
            selectedRate = selectedRate,
            showAdvanced = showAdvanced,
            cessRateText = cessRateText,
            isIntrastate = isIntrastate,
            currentHistoryId = currentHistoryId,
            baseAmount = baseAmount,
            totalAmount = totalAmount,
            totalGst = totalGst,
            totalCess = totalCess,
            cgst = cgst,
            sgst = sgst,
            igst = igst
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = GstCalculatorUiState()
    )
    
    fun onEvent(event: GstCalculatorEvent) {
        when (event) {
            is GstCalculatorEvent.ModeChanged -> _mode.value = event.mode
            is GstCalculatorEvent.AmountChanged -> _amountText.value = event.amount
            is GstCalculatorEvent.RateChanged -> _selectedRate.value = event.rate
            is GstCalculatorEvent.ShowAdvancedToggled -> _showAdvanced.value = event.show
            is GstCalculatorEvent.CessRateChanged -> _cessRateText.value = event.rate
            is GstCalculatorEvent.IntrastateToggled -> _isIntrastate.value = event.isIntrastate
            is GstCalculatorEvent.HistoryIdUpdated -> _currentHistoryId.value = event.id
            is GstCalculatorEvent.InitializeFromHistory -> {
                _mode.value = if (event.history.param1 == "REMOVE") GstMode.REMOVE else GstMode.ADD
                _amountText.value = event.history.param2 ?: ""
                _selectedRate.value = event.history.param3?.toDoubleOrNull() ?: 18.0
                _currentHistoryId.value = event.history.id
            }
            GstCalculatorEvent.Reset -> {
                _amountText.value = ""
                _cessRateText.value = ""
                _mode.value = GstMode.ADD
                _selectedRate.value = 18.0
            }
        }
    }
}
