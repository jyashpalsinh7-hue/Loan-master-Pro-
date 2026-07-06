package com.loanmaster.pro

import com.loanmaster.pro.ui.theme.*

import com.loanmaster.pro.model.*

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlin.math.pow



data class LoanEligibilityUiState(
    val selectedLoanProfile: String = "Home Loan",
    val monthlyIncomeText: String = "",
    val existingEMIsText: String = "",
    val isCoBorrowerEnabled: Boolean = false,
    val coBorrowerIncomeText: String = "",
    val coBorrowerEMIsText: String = "",
    val tenureYearsText: String = "20",
    val interestRateText: String = "8.5",
    val isSalaried: Boolean = true,
    val creditScoreRange: String = "Excellent",

    // Calculated fields from LoanEligibilityScreen
    val totalIncome: Double = 0.0,
    val totalExistingEmi: Double = 0.0,
    val foirLimit: Double = 0.0,
    val maxAllowedEmi: Double = 0.0,
    val availableEmi: Double = 0.0,
    val eligibleLoanAmount: Double = 0.0,
    val recommendedLoanAmount: Double = 0.0,
    val currentFoir: Double = 0.0,

    // Eligibility Scoring & Verdict logic
    val burdenScore: Int = 0,
    val rateScore: Int = 0,
    val tenureScore: Int = 0,
    val eligibilityScore: Int = 0,
    val verdictGrade: String = "",
    val verdictTitle: String = "",
    val verdictDesc: String = "",

    val alerts: List<SmartAlert> = emptyList(),
    val opportunities: List<SmartOpportunity> = emptyList()
)

sealed class LoanEligibilityEvent {
    data class ProfileChanged(val profile: String, val defaultTenure: String, val defaultRate: String) : LoanEligibilityEvent()
    data class MonthlyIncomeChanged(val income: String) : LoanEligibilityEvent()
    data class ExistingEMIsChanged(val emi: String) : LoanEligibilityEvent()
    data class CoBorrowerToggled(val enabled: Boolean) : LoanEligibilityEvent()
    data class CoBorrowerIncomeChanged(val income: String) : LoanEligibilityEvent()
    data class CoBorrowerEMIsChanged(val emi: String) : LoanEligibilityEvent()
    data class TenureChanged(val tenure: String) : LoanEligibilityEvent()
    data class InterestRateChanged(val rate: String) : LoanEligibilityEvent()
    data class EmploymentTypeChanged(val isSalaried: Boolean) : LoanEligibilityEvent()
    data class CreditScoreChanged(val scoreRange: String, val defaultRate: String) : LoanEligibilityEvent()
    data class AdjustIncome(val amount: Int) : LoanEligibilityEvent()
    data class AdjustEmi(val amount: Int) : LoanEligibilityEvent()
    data class AdjustTenure(val years: Int) : LoanEligibilityEvent()
}

class LoanEligibilityViewModel : ViewModel() {

    private val _selectedLoanProfile = MutableStateFlow("Home Loan")
    private val _monthlyIncomeText = MutableStateFlow("")
    private val _existingEMIsText = MutableStateFlow("")
    private val _isCoBorrowerEnabled = MutableStateFlow(false)
    private val _coBorrowerIncomeText = MutableStateFlow("")
    private val _coBorrowerEMIsText = MutableStateFlow("")
    private val _tenureYearsText = MutableStateFlow("20")
    private val _interestRateText = MutableStateFlow("8.5")
    private val _isSalaried = MutableStateFlow(true)
    private val _creditScoreRange = MutableStateFlow("Excellent")

    val uiState: StateFlow<LoanEligibilityUiState> = combine(
        _selectedLoanProfile, _monthlyIncomeText, _existingEMIsText, _isCoBorrowerEnabled,
        _coBorrowerIncomeText, _coBorrowerEMIsText, _tenureYearsText, _interestRateText,
        _isSalaried, _creditScoreRange
    ) { params ->
        val profileName = params[0] as String
        val incomeStr = params[1] as String
        val emiStr = params[2] as String
        val isCoBorrower = params[3] as Boolean
        val coIncomeStr = params[4] as String
        val coEmiStr = params[5] as String
        val tenureStr = params[6] as String
        val rateStr = params[7] as String
        val isSal = params[8] as Boolean
        val creditScore = params[9] as String

        val profile = loanProfiles.find { it.name == profileName } ?: loanProfiles[0]

        val income1 = incomeStr.safeToDouble()
        val emi1 = emiStr.safeToDouble()
        val income2 = if (isCoBorrower) coIncomeStr.safeToDouble() else 0.0
        val emi2 = if (isCoBorrower) coEmiStr.safeToDouble() else 0.0

        val totalIncome = income1 + income2
        val totalExistingEmi = emi1 + emi2

        val foirLimit = if (isSal) profile.baseFoir else (profile.baseFoir - 0.05)
        val maxAllowedEmi = totalIncome * foirLimit
        val availableEmi = maxAllowedEmi - totalExistingEmi

        val r = rateStr.safeToDouble() / 100 / 12
        val n = tenureStr.safeToDouble().coerceIn(0.0, 100.0) * 12

        val eligibleLoanAmount = if (availableEmi > 0 && r > 0 && n > 0.0) {
            availableEmi * ((Math.pow(1 + r, n) - 1) / (r * Math.pow(1 + r, n)))
        } else 0.0

        val recommendedLoanAmount = eligibleLoanAmount * 0.80
        val currentFoir = if (totalIncome > 0) (totalExistingEmi / totalIncome) * 100 else 0.0
        
        // Emulate the scoring logic the user asked for
        // We'll calculate some basic scores
        val interestBurdenRatio = if (eligibleLoanAmount > 0) {
            val totalPayment = availableEmi * n
            val totalInterest = totalPayment - eligibleLoanAmount
            totalInterest / eligibleLoanAmount
        } else 0.0
        
        val burdenScore = if (interestBurdenRatio < 0.5) 50 else if (interestBurdenRatio < 0.8) 40 else 30
        val rateScore = if (rateStr.safeToDouble() < 10) 30 else 20
        val tenureScore = if (tenureStr.safeToDouble() < 20) 20 else 10
        val totalScore = burdenScore + rateScore + tenureScore

        val grade = if (totalScore >= 80) "A" else if (totalScore >= 60) "B" else "C"
        
        LoanEligibilityUiState(
            selectedLoanProfile = profileName,
            monthlyIncomeText = incomeStr,
            existingEMIsText = emiStr,
            isCoBorrowerEnabled = isCoBorrower,
            coBorrowerIncomeText = coIncomeStr,
            coBorrowerEMIsText = coEmiStr,
            tenureYearsText = tenureStr,
            interestRateText = rateStr,
            isSalaried = isSal,
            creditScoreRange = creditScore,

            totalIncome = totalIncome,
            totalExistingEmi = totalExistingEmi,
            foirLimit = foirLimit,
            maxAllowedEmi = maxAllowedEmi,
            availableEmi = availableEmi,
            eligibleLoanAmount = eligibleLoanAmount,
            recommendedLoanAmount = recommendedLoanAmount,
            currentFoir = currentFoir,
            
            burdenScore = burdenScore,
            rateScore = rateScore,
            tenureScore = tenureScore,
            eligibilityScore = totalScore,
            verdictGrade = grade,
            verdictTitle = if (grade == "A") "Excellent" else if (grade == "B") "Good" else "Fair",
            verdictDesc = "Based on your income and requested details.",
            opportunities = emptyList()
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = LoanEligibilityUiState()
    )

    fun onEvent(event: LoanEligibilityEvent) {
        when (event) {
            is LoanEligibilityEvent.ProfileChanged -> {
                _selectedLoanProfile.value = event.profile
                _tenureYearsText.value = event.defaultTenure
                updateInterestRate(event.defaultRate, _creditScoreRange.value)
            }
            is LoanEligibilityEvent.MonthlyIncomeChanged -> _monthlyIncomeText.value = event.income
            is LoanEligibilityEvent.ExistingEMIsChanged -> _existingEMIsText.value = event.emi
            is LoanEligibilityEvent.CoBorrowerToggled -> _isCoBorrowerEnabled.value = event.enabled
            is LoanEligibilityEvent.CoBorrowerIncomeChanged -> _coBorrowerIncomeText.value = event.income
            is LoanEligibilityEvent.CoBorrowerEMIsChanged -> _coBorrowerEMIsText.value = event.emi
            is LoanEligibilityEvent.TenureChanged -> _tenureYearsText.value = event.tenure
            is LoanEligibilityEvent.InterestRateChanged -> _interestRateText.value = event.rate
            is LoanEligibilityEvent.EmploymentTypeChanged -> _isSalaried.value = event.isSalaried
            is LoanEligibilityEvent.CreditScoreChanged -> {
                _creditScoreRange.value = event.scoreRange
                updateInterestRate(event.defaultRate, event.scoreRange)
            }
            is LoanEligibilityEvent.AdjustIncome -> {
                val current = _monthlyIncomeText.value.safeToDouble()
                _monthlyIncomeText.value = (current + event.amount).toInt().coerceAtLeast(0).toString()
            }
            is LoanEligibilityEvent.AdjustEmi -> {
                val current = _existingEMIsText.value.safeToDouble()
                _existingEMIsText.value = (current + event.amount).toInt().coerceAtLeast(0).toString()
            }
            is LoanEligibilityEvent.AdjustTenure -> {
                val current = _tenureYearsText.value.toIntOrNull() ?: 0
                _tenureYearsText.value = (current + event.years).coerceAtLeast(1).toString()
            }
        }
    }

    private fun updateInterestRate(defaultRateStr: String, creditScore: String) {
        val baseRate = defaultRateStr.safeToDouble()
        val additionalRate = when (creditScore) {
            "Excellent" -> 0.0
            "Good" -> 1.0
            else -> 2.0
        }
        _interestRateText.value = (baseRate + additionalRate).toString()
    }
}
