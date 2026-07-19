package com.loanmaster.pro.feature.sip

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.loanmaster.pro.domain.calculator.SipCalculator
import com.loanmaster.pro.data.local.entity.CalculationHistory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class SipViewModel(application: Application) : AndroidViewModel(application) {
    private val premiumManager = com.loanmaster.pro.core.managers.PremiumManager(application.applicationContext)
    
    private val _uiState = MutableStateFlow(SipUiState())
    val uiState: StateFlow<SipUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            premiumManager.isPremium.collect { isPremium ->
                _uiState.update { it.copy(isPremiumUnlocked = isPremium) }
            }
        }
    }

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

    fun unlockPremium() {
        premiumManager.unlockPermanent()
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
            val amountVal = newState.amountText.toDoubleOrNull() ?: 0.0
            val rateVal = newState.returnRateText.toDoubleOrNull() ?: 0.0
            val yearsVal = newState.yearsText.toIntOrNull() ?: 0
            val stepUpVal = newState.stepUpText.toDoubleOrNull() ?: 0.0

            val insights = mutableListOf<String>()
            if (result.isValid) {
                // 1. Tenure Strategy
                if (yearsVal < 5) {
                    insights.add("Increase tenure to at least 7-10 years to ride out market volatility and benefit from the power of compounding.")
                } else if (yearsVal < 10) {
                    insights.add("Extending your SIP by just 3 years could increase your corpus by over 40% due to compounding power.")
                } else {
                    insights.add("Excellent long-term focus! Since compounding works best late in the tenure, consider extending by 2-5 more years for exponential wealth growth.")
                }

                // 2. Return Rate & Asset Allocation Strategy
                if (rateVal < 10) {
                    insights.add("A ${rateVal}% return is relatively low and barely beats inflation. Consider allocating a portion to Index or Mid-cap equity funds for 12-15% potential returns.")
                } else if (rateVal > 15) {
                    insights.add("Your expected return (${rateVal}%) is highly aggressive. Ensure your portfolio is well-diversified across large and mid-cap funds to manage high market risk.")
                } else {
                    insights.add("To sustain a healthy ${rateVal}% return over ${yearsVal} years, periodically review and rebalance your portfolio annually to maintain your target asset allocation.")
                }

                // 3. Step-Up & Amount Strategy
                if (stepUpVal == 0.0) {
                    insights.add("Introduce a 5-10% annual Step-Up. Increasing your investment slightly each year combats inflation and can nearly double your final corpus effortlessly.")
                } else if (stepUpVal < 10.0) {
                    insights.add("You have a ${stepUpVal}% step-up. Increasing it to 10-15% in line with your annual salary hikes will massively boost wealth without straining your monthly budget.")
                } else {
                    insights.add("Great step-up strategy! A ${stepUpVal}% annual increase is excellent and will keep you well ahead of inflation and growing lifestyle expenses.")
                }
                
                // 4. Bonus Insight (Lump sum vs Amount)
                if (amountVal > 20000) {
                    insights.add("Consider splitting your large ₹${amountVal.toInt()} SIP into 3 or 4 smaller weekly/fortnightly SIPs to average out market volatility better.")
                } else {
                    insights.add("Whenever you receive an annual bonus or tax refund, make a lump-sum top-up in the same folio to drastically accelerate your corpus growth.")
                }
                
                // Shuffle somewhat or just take the best 3. Let's just take the first 3 which are dynamically constructed.
                // Or we can take 1, 2, and either 3 or 4. Let's just pass all and display 3 in the UI.
            }

            newState.copy(
                totalInvested = result.totalInvested,
                totalGain = result.totalGain,
                maturityValue = result.maturityValue,
                yearlyDataList = result.yearlyDataList,
                hasValidInput = result.isValid,
                inflationAdjustedValue = result.inflationAdjustedValue,
                premiumInsights = insights.take(4)
            )
        }
    }
}
