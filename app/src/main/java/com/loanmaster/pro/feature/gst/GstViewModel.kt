package com.loanmaster.pro.feature.gst

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.loanmaster.pro.domain.calculator.GstCalculator
import com.loanmaster.pro.data.local.entity.CalculationHistory
import com.loanmaster.pro.domain.model.GstMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


class GstViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(GstUiState())
    val uiState: StateFlow<GstUiState> = _uiState.asStateFlow()

    private val calculator = GstCalculator()

    fun updateInputs(
        mode: GstMode? = null,
        amount: String? = null,
        selectedRate: Double? = null,
        showAdvanced: Boolean? = null,
        cessRateText: String? = null,
        isIntrastate: Boolean? = null,
        currentHistoryId: Int? = null,
        reset: Boolean = false,
        history: CalculationHistory? = null
    ) {
        if (reset) {
            _uiState.update { GstUiState() }
            return
        }
        
        if (history != null) {
            updateState { current ->
                val nextMode = try { GstMode.valueOf(history.param1 ?: "ADD") } catch(e: Exception) { GstMode.ADD }
                val nextAmount = history.param2 ?: ""
                val nextRate = history.param3?.toDoubleOrNull() ?: 18.0
                
                current.copy(
                    mode = nextMode,
                    amountText = nextAmount,
                    selectedRate = nextRate,
                    currentHistoryId = history.id
                )
            }
            return
        }

        updateState { current ->
            current.copy(
                mode = mode ?: current.mode,
                amountText = amount ?: current.amountText,
                selectedRate = selectedRate ?: current.selectedRate,
                showAdvanced = showAdvanced ?: current.showAdvanced,
                cessRateText = cessRateText ?: current.cessRateText,
                isIntrastate = isIntrastate ?: current.isIntrastate,
                currentHistoryId = currentHistoryId ?: current.currentHistoryId
            )
        }
    }

    private fun updateState(update: (GstUiState) -> GstUiState) {
        _uiState.update { current ->
            val newState = update(current)
            val result = calculator.calculate(
                mode = newState.mode,
                amountText = newState.amountText,
                selectedRate = newState.selectedRate,
                cessRateText = newState.cessRateText,
                isIntrastate = newState.isIntrastate
            )
            newState.copy(
                baseAmount = result.baseAmount,
                totalAmount = result.totalAmount,
                totalGst = result.totalGst,
                totalCess = result.totalCess,
                cgst = result.cgst,
                sgst = result.sgst,
                igst = result.igst
            )
        }
    }
}
