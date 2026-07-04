
package com.loanmaster.pro

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay


import com.loanmaster.pro.model.*

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


import kotlin.math.pow


// Top-level function moved from EmiCalculatorScreen.kt
fun calculateEMI(principal: Double, annualRate: Double, months: Int): Double {
    if (principal <= 0 || annualRate <= 0 || months <= 0) return 0.0
    val r = annualRate / 12 / 100
    return principal * r * (1 + r).pow(months) / ((1 + r).pow(months) - 1)
}

fun getMonthlyAmortizationSchedule(principal: Double, annualRate: Double, totalMonths: Int): List<MonthlyAmortization> {
    if (principal <= 0 || annualRate <= 0 || totalMonths <= 0) return emptyList()

    val monthlyRate = annualRate / 12 / 100
    val emi = calculateEMI(principal, annualRate, totalMonths)
    val list = mutableListOf<MonthlyAmortization>()
    var balance = principal

    for (month in 1..totalMonths) {
        val interest = balance * monthlyRate
        val principalPaid = emi - interest
        balance = (balance - principalPaid).coerceAtLeast(0.0)

        list.add(
            MonthlyAmortization(
                month = month,
                emi = emi,
                principalPaid = principalPaid,
                interestPaid = interest,
                remainingBalance = balance
            )
        )
    }
    return list
}

fun getYearWiseBreakdown(
    principal: Double,
    annualRate: Double,
    totalMonths: Int
): List<YearBreakdown> {
    if (principal <= 0 || annualRate <= 0 || totalMonths <= 0) return emptyList()

    val monthlyRate = annualRate / 12 / 100
    val emi = calculateEMI(principal, annualRate, totalMonths)
    val breakdown = mutableListOf<YearBreakdown>()
    var balance = principal
    val fullYears = totalMonths / 12

    for (year in 1..fullYears) {
        var yearlyPrincipal = 0.0
        var yearlyInterest = 0.0

        repeat(12) {
            val interest = balance * monthlyRate
            val principalPaid = emi - interest
            yearlyInterest += interest
            yearlyPrincipal += principalPaid
            balance -= principalPaid
        }

        breakdown.add(
            YearBreakdown(
                year = year,
                emi = emi,
                principalPaid = yearlyPrincipal,
                interestPaid = yearlyInterest,
                remainingBalance = balance.coerceAtLeast(0.0)
            )
        )
    }

    val remainingMonths = totalMonths % 12
    if (remainingMonths > 0) {
        var yearlyPrincipal = 0.0
        var yearlyInterest = 0.0

        repeat(remainingMonths) {
            val interest = balance * monthlyRate
            val principalPaid = emi - interest
            yearlyInterest += interest
            yearlyPrincipal += principalPaid
            balance -= principalPaid
        }

        breakdown.add(
            YearBreakdown(
                year = fullYears + 1,
                emi = emi,
                principalPaid = yearlyPrincipal,
                interestPaid = yearlyInterest,
                remainingBalance = balance.coerceAtLeast(0.0)
            )
        )
    }

    return breakdown
}



class EmiCalculatorViewModel(
    private val historyRepository: HistoryRepository
) : ViewModel() {

    private val _loanAmountText = MutableStateFlow("")
    val loanAmountText: StateFlow<String> = _loanAmountText.asStateFlow()

    private val _interestRateText = MutableStateFlow("")
    val interestRateText: StateFlow<String> = _interestRateText.asStateFlow()

    private val _tenureInputText = MutableStateFlow("")
    val tenureInputText: StateFlow<String> = _tenureInputText.asStateFlow()

    private val _isTenureInMonths = MutableStateFlow(false)
    val isTenureInMonths: StateFlow<Boolean> = _isTenureInMonths.asStateFlow()

    private val _loanType = MutableStateFlow("Home Loan")
    val loanType: StateFlow<String> = _loanType.asStateFlow()
    
    private val _currentHistoryId = MutableStateFlow(0)
    val currentHistoryId: StateFlow<Int> = _currentHistoryId.asStateFlow()

    // Calculated Results
    private val _monthlyEmi = MutableStateFlow(0.0)
    val monthlyEmi: StateFlow<Double> = _monthlyEmi.asStateFlow()

    private val _totalInterest = MutableStateFlow(0.0)
    val totalInterest: StateFlow<Double> = _totalInterest.asStateFlow()

    private val _totalPayment = MutableStateFlow(0.0)
    val totalPayment: StateFlow<Double> = _totalPayment.asStateFlow()
    
    private val _principalPercentage = MutableStateFlow(0.0)
    val principalPercentage: StateFlow<Double> = _principalPercentage.asStateFlow()

    private val _interestPercentage = MutableStateFlow(0.0)
    val interestPercentage: StateFlow<Double> = _interestPercentage.asStateFlow()
    
    private val _parsedLoanAmount = MutableStateFlow(0.0)
    val parsedLoanAmount: StateFlow<Double> = _parsedLoanAmount.asStateFlow()
    
    private val _parsedInterestRate = MutableStateFlow(0.0)
    val parsedInterestRate: StateFlow<Double> = _parsedInterestRate.asStateFlow()
    
    private val _parsedTenureYears = MutableStateFlow(0)
    val parsedTenureYears: StateFlow<Int> = _parsedTenureYears.asStateFlow()
    
    private val _hasValidInput = MutableStateFlow(false)
    val hasValidInput: StateFlow<Boolean> = _hasValidInput.asStateFlow()
    
    private val _totalMonths = MutableStateFlow(0)
    val totalMonths: StateFlow<Int> = _totalMonths.asStateFlow()

    private val _monthlySchedule = MutableStateFlow<List<MonthlyAmortization>>(emptyList())
    val monthlySchedule: StateFlow<List<MonthlyAmortization>> = _monthlySchedule.asStateFlow()

    private val _yearBreakdown = MutableStateFlow<List<YearBreakdown>>(emptyList())
    val yearBreakdown: StateFlow<List<YearBreakdown>> = _yearBreakdown.asStateFlow()


    private val _recommendations = MutableStateFlow<List<SmartRecommendation>>(emptyList())
    val recommendations: StateFlow<List<SmartRecommendation>> = _recommendations.asStateFlow()

    private val _alerts = MutableStateFlow<List<SmartAlert>>(emptyList())
    val alerts: StateFlow<List<SmartAlert>> = _alerts.asStateFlow()

    private val _opportunities = MutableStateFlow<List<SmartOpportunity>>(emptyList())
    val opportunities: StateFlow<List<SmartOpportunity>> = _opportunities.asStateFlow()

    private val _isSavedSuccessfully = MutableStateFlow(false)
    val isSavedSuccessfully: StateFlow<Boolean> = _isSavedSuccessfully.asStateFlow()

    fun updateHistoryId(id: Int) {
        _currentHistoryId.value = id
    }
    
    fun loadFromHistory(history: CalculationHistory) {
        _loanAmountText.value = history.param1 ?: ""
        _interestRateText.value = history.param2 ?: ""
        _tenureInputText.value = history.param3 ?: ""
        _isTenureInMonths.value = history.param4 == "true"
        _loanType.value = history.param5 ?: "Home Loan"
        _currentHistoryId.value = history.id
        calculateResults()
    }

    fun saveCurrentCalculation() {
        val amount = _parsedLoanAmount.value
        val rate = _parsedInterestRate.value
        val tenure = _parsedTenureYears.value
        
        if (amount <= 0 || rate <= 0 || tenure <= 0) return

        val history = CalculationHistory(
            id = _currentHistoryId.value,
            calculatorType = "EMI",
            title = "${_loanType.value} - ${formatMoney(amount)}",
            param1 = _loanAmountText.value,
            param2 = _interestRateText.value,
            param3 = _tenureInputText.value,
            param4 = _isTenureInMonths.value.toString(),
            param5 = _loanType.value,
            result1 = _monthlyEmi.value,
            result2 = _totalInterest.value,
            result3 = _totalPayment.value
        )
        
        viewModelScope.launch {
            val id = historyRepository.saveHistory(history)
            if (id > 0) {
                _currentHistoryId.value = id.toInt()
                _isSavedSuccessfully.value = true
                delay(2000)
                _isSavedSuccessfully.value = false
            }
        }
    }


    fun updateInputs(
        loanAmount: String? = null,
        interestRate: String? = null,
        tenureInput: String? = null,
        isTenureMonths: Boolean? = null,
        type: String? = null
    ) {
        loanAmount?.let { _loanAmountText.value = it }
        interestRate?.let { _interestRateText.value = it }
        tenureInput?.let { _tenureInputText.value = it }
        isTenureMonths?.let { _isTenureInMonths.value = it }
        type?.let { _loanType.value = it }

        calculateResults()
    }

    private fun calculateResults() {
        val loanAmount = _loanAmountText.value.toDoubleOrNull() ?: 0.0
        val interestRate = _interestRateText.value.toDoubleOrNull() ?: 0.0
        val tenureValue = _tenureInputText.value.toDoubleOrNull()?.toInt()?.coerceIn(0, 1200) ?: 0
        
        val months = if (_isTenureInMonths.value) tenureValue else tenureValue * 12
        _totalMonths.value = months

        val valid = loanAmount > 0 && interestRate > 0 && months > 0
        _hasValidInput.value = valid

        if (valid) {
            val emi = calculateEMI(loanAmount, interestRate, months)
            _monthlyEmi.value = emi
            
            val totalPay = emi * months
            _totalPayment.value = totalPay
            _totalInterest.value = totalPay - loanAmount
            
            _principalPercentage.value = if (totalPay > 0) (loanAmount / totalPay) * 100 else 0.0
            _interestPercentage.value = if (totalPay > 0) ((totalPay - loanAmount) / totalPay) * 100 else 0.0
            _parsedLoanAmount.value = loanAmount
            _parsedInterestRate.value = interestRate
            _parsedTenureYears.value = if (_isTenureInMonths.value) tenureValue / 12 else tenureValue
            
            _monthlySchedule.value = getMonthlyAmortizationSchedule(loanAmount, interestRate, months)
            _yearBreakdown.value = getYearWiseBreakdown(loanAmount, interestRate, months)
            _recommendations.value = generateRecommendations(loanAmount, interestRate, months, emi, totalPay - loanAmount)
            _alerts.value = generateSmartAlerts(_loanType.value, loanAmount, interestRate, if (_isTenureInMonths.value) tenureValue / 12 else tenureValue, totalPay - loanAmount, totalPay)
            _opportunities.value = generateSmartOpportunities(loanAmount, interestRate, if (_isTenureInMonths.value) tenureValue / 12 else tenureValue, emi, totalPay - loanAmount)

        } else {
            _parsedLoanAmount.value = 0.0
            _parsedInterestRate.value = 0.0
            _parsedTenureYears.value = 0
            _principalPercentage.value = 0.0
            _interestPercentage.value = 0.0
            _monthlyEmi.value = 0.0
            _totalPayment.value = 0.0
            _totalInterest.value = 0.0
            _monthlySchedule.value = emptyList()
            _yearBreakdown.value = emptyList()
            _recommendations.value = emptyList()
            _alerts.value = emptyList()
            _opportunities.value = emptyList()

        }
    }
}

fun generateRecommendations(
    principal: Double,
    annualRate: Double,
    totalMonths: Int,
    baseEmi: Double,
    baseInterest: Double
): List<SmartRecommendation> {
    if (principal <= 0 || annualRate <= 0 || totalMonths <= 0) return emptyList()

    val r = annualRate / 12 / 100

    fun calc(emi: Double): Pair<Int, Double> {
        var bal = principal
        var m = 0
        var totInt = 0.0
        while (bal > 0 && m < totalMonths * 3) {
            m++
            val int = bal * r
            val prin = emi - int
            if (prin <= 0) return Pair(totalMonths * 2, Double.MAX_VALUE)
            totInt += int
            bal -= prin
        }
        return Pair(m, totInt)
    }

    val emi1 = baseEmi * 1.15
    val (m1, int1) = calc(emi1)

    val emi2 = baseEmi * 1.25
    val (m2, int2) = calc(emi2)

    val m3 = totalMonths + 60
    val emi3 = calculateEMI(principal, annualRate, m3)
    val (_, int3) = calc(emi3)

    val emi4 = baseEmi * 1.10
    val (m4, int4) = calc(emi4)

    return listOf(
        SmartRecommendation(
            id = "best_savings",
            title = "Best Savings",
            description = "Save ${formatMoney(baseInterest - int1)}",
            currentEmi = baseEmi,
            targetEmi = emi1,
            currentTotalInterest = baseInterest,
            targetTotalInterest = int1,
            currentTenureMonths = totalMonths,
            targetTenureMonths = m1,
            isRecommended = false
        ),
        SmartRecommendation(
            id = "fastest_closure",
            title = "Fast Closure",
            description = "Finish ${(totalMonths - m2) / 12} Years Early",
            currentEmi = baseEmi,
            targetEmi = emi2,
            currentTotalInterest = baseInterest,
            targetTotalInterest = int2,
            currentTenureMonths = totalMonths,
            targetTenureMonths = m2,
            isRecommended = false
        ),
        SmartRecommendation(
            id = "lowest_emi",
            title = "Low EMI",
            description = "Reduce EMI to ${formatMoney(emi3)}",
            currentEmi = baseEmi,
            targetEmi = emi3,
            currentTotalInterest = baseInterest,
            targetTotalInterest = int3,
            currentTenureMonths = totalMonths,
            targetTenureMonths = m3,
            isRecommended = false
        ),
        SmartRecommendation(
            id = "ai_recommended",
            title = "AI Peak Plan",
            description = "Save ${formatMoney(baseInterest - int4)}\n& Close Faster",
            currentEmi = baseEmi,
            targetEmi = emi4,
            currentTotalInterest = baseInterest,
            targetTotalInterest = int4,
            currentTenureMonths = totalMonths,
            targetTenureMonths = m4,
            isRecommended = true
        )
    )
}

fun generateSmartAlerts(loanType: String, loanAmount: Double, interestRate: Double, tenureYears: Int, totalInterest: Double, totalPayment: Double): List<SmartAlert> {
    val list = mutableListOf<SmartAlert>()
    val interestBurdenRatio = if (loanAmount > 0) totalInterest / loanAmount else 0.0

    // Critical
    val criticalRatio = when (loanType) {
        "Home Loan" -> 1.5
        "Education Loan" -> 1.2
        "Car Loan" -> 0.6
        else -> 0.5
    }
    if (interestBurdenRatio > criticalRatio) list.add(SmartAlert(AlertType.CRITICAL, "Interest burden is critically high for this loan type."))
    if (totalInterest > 1_000_000 && loanType != "Home Loan" && loanType != "Business Loan") list.add(SmartAlert(AlertType.CRITICAL, "Total interest exceeds ₹10 lakh."))

    // Warning based on Loan Type
    when (loanType) {
        "Home Loan" -> {
            if (interestRate > 10.0) list.add(SmartAlert(AlertType.WARNING, "Home loan rates are typically below 10%."))
            if (tenureYears > 30) list.add(SmartAlert(AlertType.WARNING, "Tenure is unusually long (>30 years)."))
        }
        "Car Loan" -> {
            if (interestRate > 12.0) list.add(SmartAlert(AlertType.WARNING, "Car loan rates are typically below 12%."))
            if (tenureYears > 7) list.add(SmartAlert(AlertType.WARNING, "Tenure over 7 years depreciates the car faster than the loan."))
        }
        "Personal Loan" -> {
            if (interestRate > 20.0) list.add(SmartAlert(AlertType.WARNING, "Personal loan interest rate is considered very high."))
            if (tenureYears > 5) list.add(SmartAlert(AlertType.WARNING, "Personal loans normally have a max tenure of 5 years."))
        }
        "Education Loan" -> {
            if (interestRate > 15.0) list.add(SmartAlert(AlertType.WARNING, "Education loan rates shouldn't typically exceed 15%."))
            if (tenureYears > 15) list.add(SmartAlert(AlertType.WARNING, "Education loans usually don't exceed 15 years."))
        }
        "Business Loan" -> {
            if (interestRate > 18.0) list.add(SmartAlert(AlertType.WARNING, "Business loan rates are generally 12%-18%."))
            if (tenureYears > 10) list.add(SmartAlert(AlertType.WARNING, "Business loan tenure normally within 10 years."))
        }
    }

    // General Warnings
    val warningRatio = when (loanType) {
        "Home Loan" -> 1.0
        "Education Loan" -> 0.8
        "Car Loan" -> 0.4
        else -> 0.3
    }
    if (interestBurdenRatio > warningRatio && interestBurdenRatio <= criticalRatio) {
        list.add(SmartAlert(AlertType.WARNING, "Interest burden is high for this type of loan."))
    }
    if (tenureYears > 15 && totalInterest > loanAmount * 0.5 && loanType != "Home Loan") list.add(SmartAlert(AlertType.WARNING, "Long tenure is increasing total borrowing cost."))
    if (totalInterest > 500_000 && totalInterest <= 1_000_000 && loanType != "Home Loan" && loanType != "Business Loan") list.add(SmartAlert(AlertType.WARNING, "Interest exceeds ₹5 lakh."))

    // Positive
    val positiveRatio = when (loanType) {
        "Home Loan" -> 0.4
        "Education Loan" -> 0.3
        "Car Loan" -> 0.15
        else -> 0.15
    }
    if (interestBurdenRatio <= positiveRatio && loanAmount > 0) list.add(SmartAlert(AlertType.POSITIVE, "Interest cost is exceptionally low for this loan type."))
    else if (interestBurdenRatio <= positiveRatio + 0.15 && loanAmount > 0) list.add(SmartAlert(AlertType.POSITIVE, "Loan structure is cost efficient."))

    val optimalTenureEnd = when (loanType) {
        "Home Loan" -> 20
        "Education Loan" -> 10
        "Car Loan" -> 5
        else -> 3
    }
    if (tenureYears in 2..optimalTenureEnd) list.add(SmartAlert(AlertType.POSITIVE, "Tenure is well balanced."))

    return list.take(4)
}

fun generateSmartOpportunities(loanAmount: Double, interestRate: Double, tenureYears: Int, monthlyEmi: Double, totalInterest: Double): List<SmartOpportunity> {
    val list = mutableListOf<SmartOpportunity>()
    val totalMonths = tenureYears * 12
    val r = (interestRate / 12) / 100

    fun simulate(extraEmi: Double): Pair<Double, Int> {
        val emi = monthlyEmi + extraEmi
        var balance = loanAmount
        var months = 0
        var newTotalInterest = 0.0
        while (balance > 0 && months < 1200) {
            val interestForMonth = balance * r
            newTotalInterest += interestForMonth
            val principalForMonth = emi - interestForMonth
            balance -= principalForMonth
            months++
        }
        return Pair(newTotalInterest, months)
    }

    if (monthlyEmi > 0 && loanAmount > 0) {
        val (int1k, month1k) = simulate(1000.0)
        if (totalInterest - int1k > 0.0) {
            val monthsSaved = totalMonths - month1k
            if (monthsSaved > 0) {
                list.add(SmartOpportunity(
                    "Increase EMI by ₹1,000",
                    "Potential Saving: ${formatMoney(totalInterest - int1k)}",
                    "Potential Closure: $monthsSaved Months Earlier"
                ))
            }
        }

        val (int2k, month2k) = simulate(2000.0)
        if (totalInterest - int2k > 0.0) {
            val monthsSaved = totalMonths - month2k
            if (monthsSaved > 0) {
                list.add(SmartOpportunity(
                    "Increase EMI by ₹2,000",
                    "Potential Saving: ${formatMoney(totalInterest - int2k)}",
                    "Potential Closure: $monthsSaved Months Earlier"
                ))
            }
        }

        if (interestRate > 1.0) {
            val newRate = interestRate - 1.0
            val newR = (newRate / 12) / 100
            val newDenom = (1 + newR).pow(totalMonths) - 1
            val newEmi = loanAmount * newR * (1 + newR).pow(totalMonths) / newDenom
            val newTotalInt = (newEmi * totalMonths) - loanAmount
            if (totalInterest - newTotalInt > 0.0) {
                list.add(SmartOpportunity(
                    "Refinance at ${String.format("%.1f", newRate)}%",
                    "Potential Saving: ${formatMoney(totalInterest - newTotalInt)}",
                    "Lower EMI by ${formatMoney(monthlyEmi - newEmi)}"
                ))
            }
        }
    }
    return list
}

class EmiCalculatorViewModelFactory(
    private val historyRepository: HistoryRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EmiCalculatorViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EmiCalculatorViewModel(historyRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
