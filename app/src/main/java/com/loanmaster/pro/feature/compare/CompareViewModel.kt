package com.loanmaster.pro.feature.compare

import com.loanmaster.pro.domain.model.*
import com.loanmaster.pro.feature.gst.*
import com.loanmaster.pro.feature.sip.*
import com.loanmaster.pro.core.ui.*
import com.loanmaster.pro.feature.history.*
import com.loanmaster.pro.core.theme.*
import com.loanmaster.pro.data.datastore.*
import com.loanmaster.pro.feature.settings.*
import com.loanmaster.pro.feature.rd.*
import com.loanmaster.pro.domain.calculator.*
import com.loanmaster.pro.data.local.entity.*
import com.loanmaster.pro.core.utils.*
import com.loanmaster.pro.data.local.dao.*
import com.loanmaster.pro.data.local.room.*
import com.loanmaster.pro.feature.emi.*
import com.loanmaster.pro.feature.loansummary.*
import com.loanmaster.pro.feature.prepayment.*
import com.loanmaster.pro.core.formatter.*
import com.loanmaster.pro.feature.fd.*
import com.loanmaster.pro.data.repository.*
import com.loanmaster.pro.feature.currency.*
import com.loanmaster.pro.core.navigation.*
import com.loanmaster.pro.feature.loaneligibility.*
import com.loanmaster.pro.core.responsive.*
import com.loanmaster.pro.feature.home.*


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import com.loanmaster.pro.domain.calculator.LoanComparisonCalculator
import com.loanmaster.pro.domain.model.LoanOffer
import kotlin.math.pow


class CompareViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CompareUiState())
    val uiState: StateFlow<CompareUiState> = _uiState.asStateFlow()

    private val calculator = LoanComparisonCalculator()

    init {
        updateCalculations()
    }

    fun onEvent(event: LoanComparisonEvent) {
        when (event) {
            is LoanComparisonEvent.UpdateLoanA -> {
                _uiState.update { 
                    it.copy(
                        loanA = it.loanA.copy(
                            amountText = event.amount ?: it.loanA.amountText,
                            interestText = event.interest ?: it.loanA.interestText,
                            yearsText = event.years ?: it.loanA.yearsText,
                            monthsText = event.months ?: it.loanA.monthsText
                        ),
                        showResults = false
                    ) 
                }
                updateCalculations()
            }
            is LoanComparisonEvent.UpdateLoanB -> {
                _uiState.update { 
                    it.copy(
                        loanB = it.loanB.copy(
                            amountText = event.amount ?: it.loanB.amountText,
                            interestText = event.interest ?: it.loanB.interestText,
                            yearsText = event.years ?: it.loanB.yearsText,
                            monthsText = event.months ?: it.loanB.monthsText
                        ),
                        showResults = false
                    ) 
                }
                updateCalculations()
            }
            LoanComparisonEvent.ShowResults -> {
                _uiState.update { it.copy(showResults = true) }
            }
            LoanComparisonEvent.Reset -> {
                _uiState.update {
                    it.copy(
                        loanA = LoanOptionState("A", "Loan A", "", "", "", ""),
                        loanB = LoanOptionState("B", "Loan B", "", "", "", ""),
                        showResults = false
                    )
                }
                updateCalculations()
            }
        }
    }

    private fun updateCalculations() {
        val currentState = _uiState.value
        val loanA = currentState.loanA
        val loanB = currentState.loanB
        
        val offerA = LoanOffer(loanA.id, loanA.bankName, loanA.interestRate, loanA.tenureYears, loanA.tenureMonths, loanA.loanAmount, 0.0, 0.0)
        val offerB = LoanOffer(loanB.id, loanB.bankName, loanB.interestRate, loanB.tenureYears, loanB.tenureMonths, loanB.loanAmount, 0.0, 0.0)
        
        val result = calculator.calculate(offerA, offerB)
        
        _uiState.update {
            it.copy(
                processedLoans = result.processedLoans,
                hasValidInput = result.hasValidInput
            )
        }
    }
}
