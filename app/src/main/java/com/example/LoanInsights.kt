package com.example.loanmasterpro

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ==================== DATA MODELS ====================

enum class LoanType {
    HOME_LOAN, PERSONAL_LOAN, CAR_LOAN, EDUCATION_LOAN, BUSINESS_LOAN, GOLD_LOAN, OTHER
}

enum class EfficiencyLevel { EXCELLENT, GOOD, AVERAGE, EXPENSIVE }
enum class AffordabilityLevel { COMFORTABLE, MODERATE, AGGRESSIVE }

data class InterestBurden(val percentage: Double, val description: String)
data class LoanEfficiency(val level: EfficiencyLevel, val reason: String)
data class AffordabilityAssessment(val level: AffordabilityLevel, val description: String)
data class TenureAnalysis(val currentYears: Int, val recommendedYears: Int, val yearsSaved: Int)
data class TransparencyCheck(
    val advertisedRate: Double, val effectiveRate: Double,
    val difference: Double, val assessment: String, val isTransparent: Boolean
)
data class LoanHealthScore(val score: Int, val reasons: List<String>)

data class LoanInsights(
    val interestBurden: InterestBurden,
    val loanEfficiency: LoanEfficiency,
    val affordability: AffordabilityAssessment,
    val tenureAnalysis: TenureAnalysis,
    val transparencyCheck: TransparencyCheck,
    val loanHealthScore: LoanHealthScore
)

// ==================== CALCULATION ENGINE ====================

object LoanInsightsEngine {

    fun calculateInterestBurden(loanAmount: Double, totalInterest: Double): InterestBurden {
        if (loanAmount <= 0) return InterestBurden(0.0, "Invalid loan amount")
        val burden = (totalInterest / loanAmount) * 100
        return InterestBurden(burden, "You will pay ₹${formatMoney(totalInterest)} interest for every ₹${formatMoney(loanAmount)} borrowed.")
    }

    fun calculateLoanEfficiency(loanType: LoanType, interestBurden: Double): LoanEfficiency {
        val (excellent, good, average) = when (loanType) {
            LoanType.HOME_LOAN -> Triple(50.0, 100.0, 150.0)
            LoanType.PERSONAL_LOAN -> Triple(20.0, 40.0, 70.0)
            LoanType.CAR_LOAN -> Triple(15.0, 35.0, 60.0)
            LoanType.EDUCATION_LOAN -> Triple(25.0, 50.0, 80.0)
            LoanType.BUSINESS_LOAN -> Triple(30.0, 60.0, 100.0)
            LoanType.GOLD_LOAN -> Triple(10.0, 25.0, 45.0)
            else -> Triple(30.0, 60.0, 100.0)
        }
        return when {
            interestBurden < excellent -> LoanEfficiency(EfficiencyLevel.EXCELLENT, "Interest burden is significantly lower than typical for this loan type.")
            interestBurden < good -> LoanEfficiency(EfficiencyLevel.GOOD, "Interest burden is reasonable for this loan type.")
            interestBurden < average -> LoanEfficiency(EfficiencyLevel.AVERAGE, "Interest burden is higher than average for this loan type.")
            else -> LoanEfficiency(EfficiencyLevel.EXPENSIVE, "Interest burden is very high for this type of loan.")
        }
    }

    fun calculateAffordability(monthlyEmi: Double, loanAmount: Double, tenureYears: Int): AffordabilityAssessment {
        if (monthlyEmi <= 0 || loanAmount <= 0) return AffordabilityAssessment(AffordabilityLevel.MODERATE, "Insufficient data")
        val ratio = (monthlyEmi * 12) / loanAmount
        return when {
            ratio < 0.08 -> AffordabilityAssessment(AffordabilityLevel.COMFORTABLE, "Your EMI is very comfortable relative to the loan size.")
            ratio < 0.12 -> AffordabilityAssessment(AffordabilityLevel.MODERATE, "EMI is manageable but leaves limited room for other expenses.")
            else -> AffordabilityAssessment(AffordabilityLevel.AGGRESSIVE, "EMI is quite high relative to the loan amount.")
        }
    }

    fun calculateTenureAnalysis(currentYears: Int, interestRate: Double): TenureAnalysis {
        val recommended = when {
            interestRate > 12 -> (currentYears * 0.7).toInt().coerceAtLeast(5)
            interestRate > 9 -> (currentYears * 0.8).toInt().coerceAtLeast(7)
            else -> (currentYears * 0.85).toInt().coerceAtLeast(8)
        }
        return TenureAnalysis(currentYears, recommended, currentYears - recommended)
    }

    fun calculateTransparencyCheck(advertisedRate: Double): TransparencyCheck {
        val effective = advertisedRate * 1.8
        val diff = effective - advertisedRate
        val transparent = diff < 1.5
        return TransparencyCheck(
            advertisedRate, effective, diff,
            if (transparent) "✓ Transparent rate structure" else "⚠ Possible flat-rate structure detected",
            transparent
        )
    }

    fun calculateLoanHealthScore(
        interestBurden: Double,
        efficiency: EfficiencyLevel,
        affordability: AffordabilityLevel,
        isTransparent: Boolean
    ): LoanHealthScore {
        var score = 50
        score += when {
            interestBurden < 30 -> 25; interestBurden < 60 -> 15; interestBurden < 100 -> 5; else -> -10
        }
        score += when (efficiency) {
            EfficiencyLevel.EXCELLENT -> 15; EfficiencyLevel.GOOD -> 10; EfficiencyLevel.AVERAGE -> 0; else -> -10
        }
        score += when (affordability) {
            AffordabilityLevel.COMFORTABLE -> 10; AffordabilityLevel.MODERATE -> 5; else -> -5
        }
        if (isTransparent) score += 10

        val final = score.coerceIn(0, 100)
        val reasons = mutableListOf<String>()
        if (interestBurden < 60) reasons += "✓ Good interest efficiency"
        if (affordability != AffordabilityLevel.AGGRESSIVE) reasons += "✓ Reasonable EMI burden"
        if (isTransparent) reasons += "✓ Transparent rate structure"
        if (efficiency == EfficiencyLevel.EXCELLENT || efficiency == EfficiencyLevel.GOOD) reasons += "✓ Efficient loan for its type"

        return LoanHealthScore(final, reasons.ifEmpty { listOf("Loan profile is average") })
    }

    private fun formatMoney(amount: Double) = String.format("%,.0f", amount)
}

// ==================== UI COMPOSABLES ====================

@Composable
fun LoanInsightsCard(insights: LoanInsights) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF061633)),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(Modifier.padding(20.dp)) {
            Text("Loan Insights", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(16.dp))

            InsightCard(Icons.Rounded.AttachMoney, "Interest Burden", "${"%.1f".format(insights.interestBurden.percentage)}%", insights.interestBurden.description, Color(0xFF7C4DFF))
            Spacer(Modifier.height(10.dp))

            val effColor = when (insights.loanEfficiency.level) {
                EfficiencyLevel.EXCELLENT -> Color(0xFF22C55E)
                EfficiencyLevel.GOOD -> Color(0xFF3B82F6)
                EfficiencyLevel.AVERAGE -> Color(0xFFF59E0B)
                else -> Color(0xFFEF4444)
            }
            InsightCard(Icons.Rounded.Assessment, "Loan Efficiency", insights.loanEfficiency.level.name, insights.loanEfficiency.reason, effColor)
            Spacer(Modifier.height(10.dp))

            val affColor = when (insights.affordability.level) {
                AffordabilityLevel.COMFORTABLE -> Color(0xFF22C55E)
                AffordabilityLevel.MODERATE -> Color(0xFF3B82F6)
                else -> Color(0xFFEF4444)
            }
            InsightCard(Icons.Rounded.AccountBalanceWallet, "Affordability", insights.affordability.level.name, insights.affordability.description, affColor)
            Spacer(Modifier.height(10.dp))

            InsightCard(Icons.Rounded.Schedule, "Tenure Analysis", "${insights.tenureAnalysis.currentYears} Years",
                "Recommended: ${insights.tenureAnalysis.recommendedYears} years (${insights.tenureAnalysis.yearsSaved} years earlier)", Color(0xFF3B82F6))
            Spacer(Modifier.height(10.dp))

            val transColor = if (insights.transparencyCheck.isTransparent) Color(0xFF22C55E) else Color(0xFFF59E0B)
            InsightCard(Icons.Rounded.Verified, "Transparency", if (insights.transparencyCheck.isTransparent) "Transparent" else "Review Needed",
                insights.transparencyCheck.assessment, transColor)
            Spacer(Modifier.height(16.dp))

            LoanHealthScoreCard(insights.loanHealthScore)
        }
    }
}

@Composable
fun LoanHealthScoreCard(health: LoanHealthScore) {
    val color = when {
        health.score >= 80 -> Color(0xFF22C55E)
        health.score >= 60 -> Color(0xFF3B82F6)
        health.score >= 40 -> Color(0xFFF59E0B)
        else -> Color(0xFFEF4444)
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F2744)),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.HealthAndSafety, null, tint = color, modifier = Modifier.size(22.dp))
                Spacer(Modifier.width(8.dp))
                Text("Loan Health Score", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(8.dp))
            Text("${health.score}/100", color = color, fontSize = 26.sp, fontWeight = FontWeight.Bold)
            health.reasons.forEach {
                Text(it, color = Color(0xFFA8B3D1), fontSize = 13.sp, modifier = Modifier.padding(top = 4.dp))
            }
        }
    }
}

@Composable
fun InsightCard(icon: ImageVector, title: String, value: String, description: String, color: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F2744)),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = color, modifier = Modifier.size(26.dp))
            Spacer(Modifier.width(12.dp))
            Column {
                Text(title, color = Color(0xFFA8B3D1), fontSize = 13.sp)
                Text(value, color = color, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                Text(description, color = Color(0xFFA8B3D1), fontSize = 12.sp)
            }
        }
    }
}