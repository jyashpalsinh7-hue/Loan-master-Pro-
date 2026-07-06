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
        amount: String? = null,
        rate: String? = null,
        type: String? = null,
        historyId: Int? = null,
        history: CalculationHistory? = null
    ) {
        if (history != null) {
            updateState { 
                it.copy(
                    amountText = history.param1 ?: "",
                    gstRate = history.param2 ?: "",
                    gstType = history.param3 ?: "Exclusive",
                    currentHistoryId = history.id
                )
            }
            return
        }
        
        updateState { current ->
            current.copy(
                amountText = amount ?: current.amountText,
                gstRate = rate ?: current.gstRate,
                gstType = type ?: current.gstType,
                currentHistoryId = historyId ?: current.currentHistoryId
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
