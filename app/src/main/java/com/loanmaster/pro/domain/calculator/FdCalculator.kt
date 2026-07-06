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
import kotlin.math.ceil
import kotlin.math.pow

enum class CompoundingFrequency(val displayName: String, val periods: Int) {
    YEARLY("Yearly", 1),
    HALF_YEARLY("Half-Yearly", 2),
    QUARTERLY("Quarterly", 4),
    MONTHLY("Monthly", 12)
}

data class FdResult(
    val maturityValue: Double = 0.0,
    val totalInvested: Double = 0.0,
    val totalReturns: Double = 0.0,
    val wealthGain: Double = 0.0,
    val breakdown: List<FdYearBreakdown> = emptyList(),
    val isValid: Boolean = false
)

class FdCalculator {
    fun calculate(
        deposit: String,
        rate: String,
        tenureYears: String,
        frequency: CompoundingFrequency
    ): FdResult {
        val p = deposit.toDoubleOrNull() ?: 0.0
        val r = rate.toDoubleOrNull() ?: 0.0
        val t = tenureYears.toDoubleOrNull() ?: 0.0

        if (p <= 0 || r <= 0 || t <= 0) {
            return FdResult(isValid = false)
        }

        val ratePerPeriod = r / 100
        val n = frequency.periods.toDouble()
        val maturity = p * (1 + ratePerPeriod / n).pow(n * t)
        val returns = maturity - p
        val wealthGain = if (p > 0) (returns / p) * 100 else 0.0

        val maxYears = ceil(t).toInt()
        val breakdown = (1..maxYears).map { y ->
            val tMat = p * (1 + ratePerPeriod / n).pow(n * y.toDouble())
            FdYearBreakdown(
                year = y,
                openingBalance = p,
                interestEarned = tMat - p,
                closingBalance = tMat
            )
        }

        return FdResult(
            maturityValue = maturity,
            totalInvested = p,
            totalReturns = returns,
            wealthGain = wealthGain,
            breakdown = breakdown,
            isValid = true
        )
    }
}
