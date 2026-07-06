package com.loanmaster.pro.domain.calculator

import com.loanmaster.pro.domain.model.*
import com.loanmaster.pro.feature.gst.*
import com.loanmaster.pro.feature.sip.*
import com.loanmaster.pro.core.ui.*
import com.loanmaster.pro.feature.history.*
import com.loanmaster.pro.core.theme.*
import com.loanmaster.pro.data.datastore.*
import com.loanmaster.pro.feature.settings.*
import com.loanmaster.pro.feature.rd.*
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
import com.loanmaster.pro.feature.loaneligibility.*
import com.loanmaster.pro.core.responsive.*
import com.loanmaster.pro.feature.home.*
import kotlin.math.pow

class EmiCalculator {

    data class FullResult(
        val parsedLoanAmount: Double,
        val parsedInterestRate: Double,
        val parsedTenureYears: Int,
        val totalMonths: Int,
        val hasValidInput: Boolean,
        val monthlyEmi: Double,
        val totalInterest: Double,
        val totalPayment: Double,
        val principalPercentage: Double,
        val interestPercentage: Double,
        val monthlySchedule: List<MonthlyAmortization>,
        val yearBreakdown: List<YearBreakdown>,
        val recommendations: List<SmartRecommendation>,
        val alerts: List<SmartAlert>,
        val opportunities: List<SmartOpportunity>
    )

    fun calculateFull(
        loanAmount: String,
        interestRate: String,
        tenureInput: String,
        isTenureInMonths: Boolean,
        loanType: String
    ): FullResult {
        val p = loanAmount.toDoubleOrNull() ?: 0.0
        val r = interestRate.toDoubleOrNull() ?: 0.0
        val t = tenureInput.toDoubleOrNull()?.toInt()?.coerceIn(0, 1200) ?: 0
        
        val months = if (isTenureInMonths) t else t * 12
        val years = if (isTenureInMonths) t / 12 else t
        
        val valid = p > 0 && r > 0 && months > 0
        
        if (!valid) {
            return FullResult(p, r, years, months, false, 0.0, 0.0, 0.0, 0.0, 0.0, emptyList(), emptyList(), emptyList(), emptyList(), emptyList())
        }

        val emi = calculateEMI(p, r, months)
        val totalPayment = emi * months
        val totalInterest = totalPayment - p
        
        val pp = (p / totalPayment) * 100
        val ip = (totalInterest / totalPayment) * 100

        val schedule = getMonthlyAmortizationSchedule(p, r, months)
        val yearly = getYearWiseBreakdown(p, r, months)
        
        val recommendations = generateRecommendations(p, r, months, emi, totalInterest)
        val alerts = generateSmartAlerts(loanType, p, r, years, totalInterest, totalPayment)
        val opportunities = generateSmartOpportunities(p, r, years, emi, totalInterest)

        return FullResult(
            p, r, years, months, true, emi, totalInterest, totalPayment, pp, ip,
            schedule, yearly, recommendations, alerts, opportunities
        )
    }

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
}
