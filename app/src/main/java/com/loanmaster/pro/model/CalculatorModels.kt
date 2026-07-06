package com.loanmaster.pro.model

import com.loanmaster.pro.ui.theme.*

enum class AlertType {
    CRITICAL, WARNING, POSITIVE
}

data class SmartAlert(
    val type: AlertType,
    val message: String
)

data class SmartOpportunity(
    val title: String,
    val subtitle1: String,
    val subtitle2: String
)

data class YearBreakdown(
    val year: Int,
    val emi: Double,
    val principalPaid: Double,
    val interestPaid: Double,
    val remainingBalance: Double
)

data class MonthlyAmortization(
    val month: Int,
    val emi: Double,
    val principalPaid: Double,
    val interestPaid: Double,
    val remainingBalance: Double
)

data class FdYearBreakdown(
    val year: Int,
    val openingBalance: Double,
    val interestEarned: Double,
    val closingBalance: Double
)

data class SipYearBreakdown(
    val year: Int,
    val openingBalance: Double,
    val amountInvested: Double,
    val interestEarned: Double,
    val closingBalance: Double
)

data class RdYearBreakdown(
    val year: Int,
    val openingBalance: Double,
    val amountInvested: Double,
    val interestEarned: Double,
    val closingBalance: Double
)

data class ChartData(
    val points: List<Double>,
    val minVal: Double,
    val maxVal: Double,
    val trendPercent: Double
)

data class SmartRecommendation(
    val id: String,
    val title: String,
    val description: String,
    val currentEmi: Double,
    val targetEmi: Double,
    val currentTotalInterest: Double,
    val targetTotalInterest: Double,
    val currentTenureMonths: Int,
    val targetTenureMonths: Int,
    val isRecommended: Boolean = false
)
