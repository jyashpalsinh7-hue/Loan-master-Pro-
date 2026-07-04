package com.loanmaster.pro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*

data class PrepaymentCalculatorUiState(
    val loanAmountText: String = "",
    val interestRateText: String = "",
    val tenureYearsText: String = "",
    val prepaymentAmountText: String = "",
    val strategy: String = "Tenure", // "Tenure" or "EMI"
    val monthlyPrepaymentText: String = "0",
    val annualPrepaymentText: String = "0",
    val currentHistoryId: Int = 0,
    
    // Calculated
    val originalEmi: Double = 0.0,
    val originalTotalPayment: Double = 0.0,
    val originalTotalInterest: Double = 0.0,
    
    val newEmi: Double = 0.0,
    val newTenureMonths: Double = 0.0,
    val newTotalInterest: Double = 0.0,
    
    val interestSaved: Double = 0.0,
    val tenureReducedMonths: Double = 0.0,
    val emiReduced: Double = 0.0,
    
    val hasValidInput: Boolean = false
)

sealed class PrepaymentEvent {
    data class LoanAmountChanged(val amount: String) : PrepaymentEvent()
    data class InterestRateChanged(val rate: String) : PrepaymentEvent()
    data class TenureChanged(val years: String) : PrepaymentEvent()
    data class PrepaymentAmountChanged(val amount: String) : PrepaymentEvent()
    data class StrategyChanged(val strategy: String) : PrepaymentEvent()
    data class MonthlyPrepaymentChanged(val amount: String) : PrepaymentEvent()
    data class AnnualPrepaymentChanged(val amount: String) : PrepaymentEvent()
    data class InitializeFromHistory(val history: CalculationHistory) : PrepaymentEvent()
    data class HistoryIdUpdated(val id: Int) : PrepaymentEvent()
}

class PrepaymentCalculatorViewModel : ViewModel() {
    private val _loanAmountText = MutableStateFlow("")
    private val _interestRateText = MutableStateFlow("")
    private val _tenureYearsText = MutableStateFlow("")
    private val _prepaymentAmountText = MutableStateFlow("")
    private val _strategy = MutableStateFlow("Tenure")
    private val _monthlyPrepaymentText = MutableStateFlow("0")
    private val _annualPrepaymentText = MutableStateFlow("0")
    private val _currentHistoryId = MutableStateFlow(0)
    
    val uiState: StateFlow<PrepaymentCalculatorUiState> = combine(
        _loanAmountText, _interestRateText, _tenureYearsText, _prepaymentAmountText,
        _strategy, _monthlyPrepaymentText, _annualPrepaymentText, _currentHistoryId
    ) { params ->
        val loanAmount = params[0] as String
        val rateStr = params[1] as String
        val tenureStr = params[2] as String
        val prepayStr = params[3] as String
        val strategy = params[4] as String
        val monthlyStr = params[5] as String
        val annualStr = params[6] as String
        val historyId = params[7] as Int
        
        val p = loanAmount.safeToDouble()
        val rate = rateStr.safeToDouble()
        val terms = tenureStr.safeToDouble().coerceIn(0.0, 100.0)
        val prePay = prepayStr.safeToDouble()
        val monthlyPrepay = monthlyStr.safeToDouble()
        val annualPrepay = annualStr.safeToDouble()
        
        val r = if (rate > 0) (rate / 12) / 100 else 0.0
        val n = terms * 12
        val emi = if (p > 0 && r > 0 && n > 0) p * (r * Math.pow(1 + r, n)) / (Math.pow(1 + r, n) - 1) else 0.0
        
        val originalTotalPayment = emi * n
        val originalTotalInterest = originalTotalPayment - p
        
        val newPrincipal = p - prePay
        var newEmi = emi
        var newTenureMonths = n
        var newTotalInterest = 0.0
        
        if (newPrincipal <= 0) {
            newEmi = 0.0
            newTenureMonths = 0.0
            newTotalInterest = 0.0
        } else {
            if (strategy == "EMI") {
                newEmi = if (r > 0 && n > 0) newPrincipal * (r * Math.pow(1 + r, n)) / (Math.pow(1 + r, n) - 1) else newPrincipal / n
                newTenureMonths = n
                newTotalInterest = (newEmi * n) - newPrincipal
            } else {
                newEmi = emi + monthlyPrepay
                if (annualPrepay == 0.0) {
                    if (r > 0 && newEmi > newPrincipal * r) {
                        newTenureMonths = Math.log(newEmi / (newEmi - newPrincipal * r)) / Math.log(1 + r)
                    } else if (r == 0.0 && newEmi > 0) {
                        newTenureMonths = newPrincipal / newEmi
                    } else {
                        newTenureMonths = n
                    }
                    newTotalInterest = (newEmi * newTenureMonths) - newPrincipal
                } else {
                    var bal = newPrincipal
                    var months = 0
                    var totInt = 0.0
                    while (bal > 0.01 && months <= n * 2) {
                        val interestForMonth = bal * r
                        totInt += interestForMonth
                        var principalForMonth = newEmi - interestForMonth
                        
                        if (bal < principalForMonth) {
                            principalForMonth = bal
                        }
                        bal -= principalForMonth
                        
                        if ((months + 1) % 12 == 0 && bal > 0.01) {
                            val extra = if (bal < annualPrepay) bal else annualPrepay
                            bal -= extra
                        }
                        months++
                    }
                    newTenureMonths = months.toDouble()
                    newTotalInterest = totInt
                }
            }
        }
        
        val interestSaved = if (originalTotalInterest > newTotalInterest) originalTotalInterest - newTotalInterest else 0.0
        val tenureReducedMonths = if (n > newTenureMonths) n - newTenureMonths else 0.0
        val emiReduced = if (emi > newEmi) emi - newEmi else 0.0
        
        val isValid = p > 0 && rate > 0 && terms > 0
        
        PrepaymentCalculatorUiState(
            loanAmountText = loanAmount,
            interestRateText = rateStr,
            tenureYearsText = tenureStr,
            prepaymentAmountText = prepayStr,
            strategy = strategy,
            monthlyPrepaymentText = monthlyStr,
            annualPrepaymentText = annualStr,
            currentHistoryId = historyId,
            originalEmi = emi,
            originalTotalPayment = originalTotalPayment,
            originalTotalInterest = originalTotalInterest,
            newEmi = newEmi,
            newTenureMonths = newTenureMonths,
            newTotalInterest = newTotalInterest,
            interestSaved = interestSaved,
            tenureReducedMonths = tenureReducedMonths,
            emiReduced = emiReduced,
            hasValidInput = isValid
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PrepaymentCalculatorUiState()
    )
    
    fun onEvent(event: PrepaymentEvent) {
        when (event) {
            is PrepaymentEvent.LoanAmountChanged -> _loanAmountText.value = event.amount
            is PrepaymentEvent.InterestRateChanged -> _interestRateText.value = event.rate
            is PrepaymentEvent.TenureChanged -> _tenureYearsText.value = event.years
            is PrepaymentEvent.PrepaymentAmountChanged -> _prepaymentAmountText.value = event.amount
            is PrepaymentEvent.StrategyChanged -> _strategy.value = event.strategy
            is PrepaymentEvent.MonthlyPrepaymentChanged -> _monthlyPrepaymentText.value = event.amount
            is PrepaymentEvent.AnnualPrepaymentChanged -> _annualPrepaymentText.value = event.amount
            is PrepaymentEvent.HistoryIdUpdated -> _currentHistoryId.value = event.id
            is PrepaymentEvent.InitializeFromHistory -> {
                _loanAmountText.value = event.history.param1 ?: ""
                _interestRateText.value = event.history.param2 ?: ""
                _tenureYearsText.value = event.history.param3 ?: ""
                _prepaymentAmountText.value = event.history.param4 ?: ""
                _strategy.value = event.history.param5 ?: "Tenure"
                _currentHistoryId.value = event.history.id
            }
        }
    }
}
