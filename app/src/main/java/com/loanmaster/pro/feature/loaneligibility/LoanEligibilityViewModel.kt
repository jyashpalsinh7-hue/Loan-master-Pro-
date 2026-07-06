package com.loanmaster.pro.feature.loaneligibility

import com.loanmaster.pro.domain.model.*
import com.loanmaster.pro.domain.calculator.LoanEligibilityCalculator
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
import com.loanmaster.pro.feature.compare.*
import com.loanmaster.pro.core.responsive.*
import com.loanmaster.pro.feature.home.*


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlin.math.pow




class LoanEligibilityViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(LoanEligibilityUiState())
    val uiState: StateFlow<LoanEligibilityUiState> = _uiState.asStateFlow()

    private val calculator = LoanEligibilityCalculator()

        fun updateInputs(
        profile: String? = null,
        defaultTenure: String? = null,
        defaultRate: String? = null,
        income: String? = null,
        emi: String? = null,
        isCoBorrowerEnabled: Boolean? = null,
        coIncome: String? = null,
        coEmi: String? = null,
        tenure: String? = null,
        rate: String? = null,
        isSalaried: Boolean? = null,
        creditScoreRange: String? = null,
        adjustIncomeAmount: Double? = null,
        adjustEmiAmount: Double? = null,
        adjustTenureYears: Double? = null
    ) {
        updateState { current ->
            var next = current
            if (profile != null && defaultTenure != null && defaultRate != null) {
                next = next.copy(selectedLoanProfile = profile, tenureYearsText = defaultTenure, interestRateText = defaultRate)
            }
            if (income != null) next = next.copy(monthlyIncomeText = income)
            if (emi != null) next = next.copy(existingEMIsText = emi)
            if (isCoBorrowerEnabled != null) next = next.copy(isCoBorrowerEnabled = isCoBorrowerEnabled)
            if (coIncome != null) next = next.copy(coBorrowerIncomeText = coIncome)
            if (coEmi != null) next = next.copy(coBorrowerEMIsText = coEmi)
            if (tenure != null) next = next.copy(tenureYearsText = tenure)
            if (rate != null) next = next.copy(interestRateText = rate)
            if (isSalaried != null) next = next.copy(isSalaried = isSalaried)
            if (creditScoreRange != null) next = next.copy(creditScoreRange = creditScoreRange)
            
            if (adjustIncomeAmount != null) {
                next = next.copy(monthlyIncomeText = ((next.monthlyIncomeText.toDoubleOrNull() ?: 0.0) + adjustIncomeAmount).coerceAtLeast(0.0).toString())
            }
            if (adjustEmiAmount != null) {
                next = next.copy(existingEMIsText = ((next.existingEMIsText.toDoubleOrNull() ?: 0.0) + adjustEmiAmount).coerceAtLeast(0.0).toString())
            }
            if (adjustTenureYears != null) {
                next = next.copy(tenureYearsText = ((next.tenureYearsText.toDoubleOrNull() ?: 0.0) + adjustTenureYears).coerceAtLeast(1.0).toString())
            }
            next
        }
    }

    private fun updateState(update: (LoanEligibilityUiState) -> LoanEligibilityUiState) {
        _uiState.update { current ->
            val newState = update(current)
            val result = calculator.calculate(
                profileName = newState.selectedLoanProfile,
                incomeStr = newState.monthlyIncomeText,
                emiStr = newState.existingEMIsText,
                isCoBorrower = newState.isCoBorrowerEnabled,
                coIncomeStr = newState.coBorrowerIncomeText,
                coEmiStr = newState.coBorrowerEMIsText,
                tenureStr = newState.tenureYearsText,
                rateStr = newState.interestRateText,
                isSal = newState.isSalaried,
                creditScore = newState.creditScoreRange
            )
            newState.copy(
                totalIncome = result.totalIncome,
                totalExistingEmi = result.totalExistingEmi,
                foirLimit = result.foirLimit,
                maxAllowedEmi = result.maxAllowedEmi,
                availableEmi = result.availableEmi,
                eligibleLoanAmount = result.eligibleLoanAmount,
                recommendedLoanAmount = result.recommendedLoanAmount,
                currentFoir = result.currentFoir,
                burdenScore = result.burdenScore,
                rateScore = result.rateScore,
                tenureScore = result.tenureScore,
                eligibilityScore = result.eligibilityScore,
                verdictGrade = result.verdictGrade,
                verdictTitle = result.verdictTitle,
                verdictDesc = result.verdictDesc,
                alerts = result.alerts,
                opportunities = result.opportunities
            )
        }
    }
}