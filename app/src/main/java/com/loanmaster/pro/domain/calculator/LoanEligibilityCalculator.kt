package com.loanmaster.pro.domain.calculator

import com.loanmaster.pro.domain.model.*
import kotlin.math.pow

data class LoanEligibilityResult(
    val totalIncome: Double = 0.0,
    val totalExistingEmi: Double = 0.0,
    val foirLimit: Double = 0.0,
    val maxAllowedEmi: Double = 0.0,
    val availableEmi: Double = 0.0,
    val eligibleLoanAmount: Double = 0.0,
    val recommendedLoanAmount: Double = 0.0,
    val currentFoir: Double = 0.0,
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

class LoanEligibilityCalculator {
    private val loanProfiles = listOf(
        LoanProfile("Home Loan", 0.60, "8.5", "20"),
        LoanProfile("Personal Loan", 0.50, "12.0", "5"),
        LoanProfile("Car Loan", 0.55, "9.5", "7"),
        LoanProfile("Education Loan", 0.40, "10.5", "10")
    )

    private fun String.safeToDouble(): Double = toDoubleOrNull() ?: 0.0
    private fun String.safeToInt(): Int = toIntOrNull() ?: 0

    fun calculate(
        profileName: String,
        incomeStr: String,
        emiStr: String,
        isCoBorrower: Boolean,
        coIncomeStr: String,
        coEmiStr: String,
        tenureStr: String,
        rateStr: String,
        isSal: Boolean,
        creditScore: String
    ): LoanEligibilityResult {
        val profile = loanProfiles.find { it.name == profileName } ?: loanProfiles[0]

        val income1 = incomeStr.safeToDouble()
        val emi1 = emiStr.safeToDouble()
        val income2 = if (isCoBorrower) coIncomeStr.safeToDouble() else 0.0
        val emi2 = if (isCoBorrower) coEmiStr.safeToDouble() else 0.0

        val totalIncome = income1 + income2
        val totalExistingEmi = emi1 + emi2

        val foirLimit = if (isSal) profile.baseFoir else (profile.baseFoir - 0.05)
        val maxAllowedEmi = totalIncome * foirLimit

        val availableEmi = if (maxAllowedEmi > totalExistingEmi) maxAllowedEmi - totalExistingEmi else 0.0
        val currentFoir = if (totalIncome > 0) totalExistingEmi / totalIncome else 0.0

        val tenureYrs = tenureStr.safeToDouble()
        val rate = rateStr.safeToDouble()
        var eligibleLoanAmount = 0.0

        if (rate > 0 && tenureYrs > 0 && availableEmi > 0) {
            val r = (rate / 100.0) / 12.0
            val n = tenureYrs * 12
            eligibleLoanAmount = availableEmi * ((1 + r).pow(n) - 1) / (r * (1 + r).pow(n))
        }
        
        val recommendedLoanAmount = eligibleLoanAmount * 0.85

        val burdenScore = when {
            currentFoir < 0.20 -> 100
            currentFoir < 0.40 -> 80
            currentFoir < 0.50 -> 50
            else -> 10
        }

        val rateScore = when {
            rate < profile.defaultRate.safeToDouble() -> 90
            rate == profile.defaultRate.safeToDouble() -> 70
            rate < profile.defaultRate.safeToDouble() + 2 -> 50
            else -> 20
        }

        val tenureScore = when {
            tenureYrs <= profile.defaultTenure.safeToDouble() -> 80
            tenureYrs <= profile.defaultTenure.safeToDouble() + 5 -> 50
            else -> 20
        }

        val creditMultiplier = when (creditScore) {
            "Excellent" -> 1.1
            "Good" -> 1.0
            "Fair" -> 0.8
            else -> 0.5
        }

        var eligibilityScore = ((burdenScore * 0.5 + rateScore * 0.3 + tenureScore * 0.2) * creditMultiplier).toInt().coerceIn(0, 100)
        
        if (eligibleLoanAmount <= 0) {
            eligibilityScore = 0
        }

        val (grade, title, desc) = when {
            eligibilityScore >= 80 -> Triple("A+", "Excellent Approval Odds", "You have strong financials. Banks will likely offer you the lowest interest rates.")
            eligibilityScore >= 60 -> Triple("B", "Good Approval Odds", "You meet standard criteria. You can easily secure a loan with standard rates.")
            eligibilityScore >= 40 -> Triple("C", "Fair Approval Odds", "Approval is possible but you may face higher interest rates or require a guarantor.")
            else -> Triple("D", "Low Approval Odds", "Your current debt burden is too high relative to your income, reducing borrowing capacity.")
        }

        val alerts = mutableListOf<SmartAlert>()
        if (currentFoir > 0.5) {
            alerts.add(SmartAlert(AlertType.CRITICAL, "Your existing EMIs consume over 50% of your income. Consider clearing small debts before applying."))
        }
        if (creditScore == "Poor") {
            alerts.add(SmartAlert(AlertType.WARNING, "A poor credit score drastically reduces approval chances and increases interest rates."))
        }
        if (!isSal) {
            alerts.add(SmartAlert(AlertType.WARNING, "Banks usually require 2-3 years of ITRs for self-employed individuals. Ensure your tax filings are up to date."))
        }

        val opportunities = mutableListOf<SmartOpportunity>()
        if (!isCoBorrower && currentFoir > 0.4) {
            opportunities.add(SmartOpportunity("Add a Co-borrower", "Adding a working spouse or parent can increase your eligible loan amount significantly.", ""))
        }
        if (tenureYrs < 20 && profileName == "Home Loan" && availableEmi < 20000) {
            opportunities.add(SmartOpportunity("Increase Tenure", "Extending the loan tenure will lower the EMI requirement and increase your eligible amount.", ""))
        }

        return LoanEligibilityResult(
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
            eligibilityScore = eligibilityScore,
            verdictGrade = grade,
            verdictTitle = title,
            verdictDesc = desc,
            alerts = alerts,
            opportunities = opportunities
        )
    }
}
