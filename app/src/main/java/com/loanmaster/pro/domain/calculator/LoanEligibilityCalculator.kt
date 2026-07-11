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
        LoanProfile("Home Loan", 0.65, "8.5", "20"),
        LoanProfile("Personal Loan", 0.50, "11.0", "5"),
        LoanProfile("Car Loan", 0.55, "9.0", "7"),
        LoanProfile("Education Loan", 0.50, "10.0", "10"),
        LoanProfile("Business Loan", 0.50, "12.0", "5"),
        LoanProfile("Gold Loan", 0.75, "9.5", "2"),
        LoanProfile("Medical Loan", 0.45, "11.5", "4"),
        LoanProfile("Travel Loan", 0.40, "12.5", "2"),
        LoanProfile("Two Wheeler Loan", 0.50, "10.5", "3")
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

        val baseFoirAdjusted = if (isSal) profile.baseFoir else (profile.baseFoir - 0.05)
        
        // FOIR limit logic based on income size for certain loans (e.g. Home Loan)
        val foirLimit = if (profileName == "Home Loan" && totalIncome > 100000) {
            baseFoirAdjusted + 0.05 // Higher income = higher capacity
        } else {
            baseFoirAdjusted
        }

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

        val creditMultiplier = when (profileName) {
            "Gold Loan" -> when (creditScore) {
                "Excellent" -> 1.1
                "Good" -> 1.05
                "Fair" -> 0.95
                else -> 0.85
            }
            "Travel Loan", "Personal Loan" -> when (creditScore) {
                "Excellent" -> 1.1
                "Good" -> 0.9
                "Fair" -> 0.6
                else -> 0.2
            }
            else -> when (creditScore) {
                "Excellent" -> 1.1
                "Good" -> 1.0
                "Fair" -> 0.8
                else -> 0.5
            }
        }

        var eligibilityScore = ((burdenScore * 0.5 + rateScore * 0.3 + tenureScore * 0.2) * creditMultiplier).toInt().coerceIn(0, 100)
        
        if (profileName == "Gold Loan") {
            // Gold loans are heavily collateral-based
            eligibilityScore = (eligibilityScore + 20).coerceIn(0, 100)
        }

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
        
        if (currentFoir > 0.5 && profileName != "Gold Loan") {
            alerts.add(SmartAlert(AlertType.CRITICAL, "Your existing EMIs consume over 50% of your income. Consider clearing small debts before applying."))
        }
        
        if (creditScore == "Poor" && profileName != "Gold Loan") {
            alerts.add(SmartAlert(AlertType.WARNING, "A poor credit score drastically reduces approval chances for unsecured loans and increases interest rates."))
        }
        
        if (!isSal && (profileName == "Personal Loan" || profileName == "Home Loan")) {
            alerts.add(SmartAlert(AlertType.WARNING, "Banks usually require 2-3 years of ITRs for self-employed individuals. Ensure your tax filings are up to date."))
        }
        
        if (profileName == "Business Loan" && isSal) {
            alerts.add(SmartAlert(AlertType.WARNING, "Business loans are generally intended for self-employed individuals or business entities. You may be offered a Personal Loan instead."))
        }

        val opportunities = mutableListOf<SmartOpportunity>()
        
        if (!isCoBorrower && currentFoir > 0.4 && profileName != "Gold Loan") {
            opportunities.add(SmartOpportunity("Add a Co-borrower", "Adding a working spouse or parent can increase your eligible loan amount significantly.", ""))
        }
        
        if (profileName == "Education Loan" && !isCoBorrower) {
            opportunities.add(SmartOpportunity("Add a Parent as Co-borrower", "Education loans over certain limits usually require a parent as a financial co-applicant.", ""))
        }
        
        if (profileName == "Gold Loan" && eligibilityScore < 80) {
            opportunities.add(SmartOpportunity("Leverage Gold Value", "Since this is a fully secured loan, approval is heavily based on the value of pledged gold rather than income.", ""))
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
            rateScore = rateScore,            tenureScore = tenureScore,            eligibilityScore = eligibilityScore,            verdictGrade = grade,            verdictTitle = title,            verdictDesc = desc,            alerts = alerts,            opportunities = opportunities        )    }}