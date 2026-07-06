package com.loanmaster.pro.feature.loaneligibility
import com.loanmaster.pro.data.local.entity.*
import com.loanmaster.pro.domain.model.*

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
