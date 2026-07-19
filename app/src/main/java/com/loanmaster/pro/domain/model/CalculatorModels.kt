package com.loanmaster.pro.domain.model

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
import com.loanmaster.pro.feature.loaneligibility.*
import com.loanmaster.pro.core.responsive.*
import com.loanmaster.pro.feature.home.*

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
    val year: Double,
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

data class YearlyData(
    val year: Int,
    val investedForYear: Double,
    val totalInvested: Double,
    val returns: Double,
    val maturity: Double
)

enum class GstMode { ADD, REMOVE }

data class LoanProfile(val name: String, val baseFoir: Double, val defaultRate: String, val defaultTenure: String)

data class LoanOffer(
    val id: String,
    val bankName: String,
    val interestRate: Double,
    val tenureYears: Int,
    val tenureMonths: Int = 0,
    val loanAmount: Double,
    val processingFee: Double = 0.0,
    val prepaymentCharges: Double = 0.0,
    val isBest: Boolean = false
) {
    val totalMonths: Int get() = tenureYears * 12 + tenureMonths
}
