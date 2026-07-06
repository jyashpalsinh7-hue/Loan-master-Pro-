package com.loanmaster.pro.core.ui

import com.loanmaster.pro.domain.model.*
import com.loanmaster.pro.feature.gst.*
import com.loanmaster.pro.feature.sip.*
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



import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material.icons.automirrored.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.window.core.layout.WindowWidthSizeClass
import kotlin.math.pow

@Composable
fun LoanIntelligenceCard(
    loanType: String,
    loanAmount: Double,
    interestRate: Double,
    tenureYears: Int,
    monthlyEmi: Double,
    totalInterest: Double,
    totalPayment: Double,
    alerts: List<SmartAlert>,
    opportunities: List<SmartOpportunity>,    modifier: Modifier = Modifier
) {
    val interestBurdenRatio = if (loanAmount > 0) totalInterest / loanAmount else 0.0

    val (verdictGrade, verdictTitle, verdictDesc, verdictColor) = remember(loanType, interestBurdenRatio, interestRate, tenureYears) {
        // 1. Interest Burden Ratio (Max 50 points)
        val burdenScore = when (loanType) {
            "Home Loan" -> when {
                interestBurdenRatio < 0.50 -> 50
                interestBurdenRatio < 0.80 -> 40
                interestBurdenRatio < 1.20 -> 30
                interestBurdenRatio < 1.60 -> 15
                else -> 0
            }
            "Car Loan" -> when {
                interestBurdenRatio < 0.15 -> 50
                interestBurdenRatio < 0.25 -> 40
                interestBurdenRatio < 0.40 -> 30
                interestBurdenRatio < 0.60 -> 15
                else -> 0
            }
            "Personal Loan", "Business Loan" -> when {
                interestBurdenRatio < 0.15 -> 50
                interestBurdenRatio < 0.25 -> 40
                interestBurdenRatio < 0.45 -> 30
                interestBurdenRatio < 0.60 -> 15
                else -> 0
            }
            else -> when { // Education etc
                interestBurdenRatio < 0.30 -> 50
                interestBurdenRatio < 0.50 -> 40
                interestBurdenRatio < 0.80 -> 30
                interestBurdenRatio < 1.20 -> 15
                else -> 0
            }
        }
        
        // 2. Interest Rate (Max 30 points)
        val rateScore = when (loanType) {
            "Home Loan" -> if (interestRate < 8.5) 30 else if (interestRate < 9.5) 20 else if (interestRate < 11.0) 10 else 0
            "Car Loan" -> if (interestRate < 9.0) 30 else if (interestRate < 11.0) 20 else if (interestRate < 14.0) 10 else 0
            "Personal Loan" -> if (interestRate < 12.0) 30 else if (interestRate < 15.0) 20 else if (interestRate < 20.0) 10 else 0
            "Business Loan" -> if (interestRate < 12.0) 30 else if (interestRate < 16.0) 20 else if (interestRate < 22.0) 10 else 0
            "Education Loan" -> if (interestRate < 10.0) 30 else if (interestRate < 12.0) 20 else if (interestRate < 15.0) 10 else 0
            else -> if (interestRate < 10.0) 30 else if (interestRate < 15.0) 15 else 0
        }
        
        // 3. Tenure (Max 20 points)
        val tenureScore = when (loanType) {
            "Home Loan" -> if (tenureYears <= 15) 20 else if (tenureYears <= 20) 15 else if (tenureYears <= 25) 10 else 0
            "Car Loan" -> if (tenureYears <= 4) 20 else if (tenureYears <= 5) 15 else if (tenureYears <= 7) 5 else 0
            "Personal Loan" -> if (tenureYears <= 3) 20 else if (tenureYears <= 4) 15 else if (tenureYears <= 5) 5 else 0
            "Business Loan" -> if (tenureYears <= 5) 20 else if (tenureYears <= 7) 15 else if (tenureYears <= 10) 5 else 0
            "Education Loan" -> if (tenureYears <= 7) 20 else if (tenureYears <= 10) 15 else if (tenureYears <= 15) 5 else 0
            else -> if (tenureYears <= 5) 20 else if (tenureYears <= 10) 10 else 0
        }
        
        val totalScore = burdenScore + rateScore + tenureScore
        
        val grade: String
        val title: String
        val desc: String
        val color: Color
        
        when {
            totalScore >= 85 -> {
                grade = "A+"
                title = "Excellent Deal"
                desc = "Optimal interest rate and tenure. Highly recommended."
                color = Color(0xFF4CAF50)
            }
            totalScore >= 70 -> {
                grade = "A"
                title = "Good Deal"
                desc = "Fair terms. Overall interest burden is reasonable."
                color = Color(0xFF8BC34A)
            }
            totalScore >= 50 -> {
                grade = "B"
                title = "Acceptable"
                desc = if (rateScore <= 10) "Interest rate is slightly high. Prepayments will help."
                       else if (tenureScore <= 5) "Long tenure increases cost. Try to reduce tenure."
                       else "Standard terms, but borrowing costs are noticeable."
                color = Color(0xFFFFC107)
            }
            totalScore >= 30 -> {
                grade = "C"
                title = "Expensive"
                desc = if (rateScore == 0) "Interest rate is very high for a $loanType."
                       else if (tenureScore == 0) "Unusually long tenure makes this loan very expensive."
                       else "High borrowing cost. Consider alternatives or prepayments."
                color = Color(0xFFFF9800)
            }
            else -> {
                grade = "D"
                title = "High Cost"
                desc = "Terms are highly unfavorable. Extremely high interest burden."
                color = Color(0xFFF44336)
            }
        }
        
        listOf(grade, title, desc, color)
    }



    val interestPercentage = if (totalPayment > 0) (totalInterest / totalPayment * 100).toInt() else 0
    val principalPercentage = if (totalPayment > 0) 100 - interestPercentage else 0

    val primaryCard = Color(0xFF061633)
    val borderColor = Color(0xFF183C8A)
    val primaryText = Color(0xFFFFFFFF)
    val secondaryText = Color(0xFFA8B3D1)
    val blueAccent = Color(0xFF2D7DFF)
    val goldAccent = Color(0xFFFFC328)
    val accentGreen = Color(0xFF22C55E)
    val accentPurple = Color(0xFF7C4DFF)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.screenPadding)
    ) {
        // LOAN VERDICT
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(LoanMasterTheme.spacing.md),
            colors = CardDefaults.cardColors(containerColor = primaryCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
        ) {
            Column(
                modifier = Modifier.padding(
                    horizontal = LoanMasterTheme.spacing.screenPadding,
                    vertical = LoanMasterTheme.spacing.lg
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.EmojiEvents, contentDescription = null, tint = verdictColor as Color, modifier = Modifier.size(LoanMasterTheme.spacing.xl))
                    Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.md))
                    Column {
                        Text(
                            text = "🏆 Loan Verdict",
                            fontSize = LoanMasterTheme.typography.body.fontSize,
                            color = secondaryText
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = verdictGrade as String,
                                fontSize = LoanMasterTheme.typography.title.fontSize,
                                fontWeight = FontWeight.Bold,
                                color = verdictColor
                            )
                            Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.sm))
                            Text(
                                text = verdictTitle as String,
                                fontSize = LoanMasterTheme.typography.body.fontSize * 1.1f,
                                fontWeight = FontWeight.Bold,
                                color = primaryText
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.sm))
                Text(
                    text = verdictDesc as String,
                    fontSize = LoanMasterTheme.typography.body.fontSize * 0.9f,
                    color = secondaryText
                )
            }
        }

        // COST BREAKDOWN
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(LoanMasterTheme.spacing.md),
            colors = CardDefaults.cardColors(containerColor = primaryCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
        ) {
            Column(
                modifier = Modifier.padding(
                    horizontal = LoanMasterTheme.spacing.screenPadding,
                    vertical = LoanMasterTheme.spacing.lg
                )
            ) {
                Text(
                    text = "Cost Breakdown",
                    fontSize = LoanMasterTheme.typography.title.fontSize,
                    fontWeight = FontWeight.Bold,
                    color = primaryText
                )
                Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.md))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    BreakdownItem("Borrowed Amount", formatMoney(loanAmount), blueAccent)
                    BreakdownItem("Interest Cost", formatMoney(totalInterest), goldAccent)
                    BreakdownItem("Total Repayment", formatMoney(totalPayment), primaryText)
                }

                Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.md))

                Row(modifier = Modifier.fillMaxWidth().heightIn(min = LoanMasterTheme.spacing.md).clip(RoundedCornerShape(LoanMasterTheme.spacing.sm))) {
                    Box(modifier = Modifier.weight(principalPercentage.toFloat() + 0.1f).fillMaxHeight().background(blueAccent))
                    Box(modifier = Modifier.weight(interestPercentage.toFloat() + 0.1f).fillMaxHeight().background(goldAccent))
                }
                Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.sm))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Principal Share: $principalPercentage%", fontSize = LoanMasterTheme.typography.body.fontSize * 0.8f, color = blueAccent)
                    Text("Interest Share: $interestPercentage%", fontSize = LoanMasterTheme.typography.body.fontSize * 0.8f, color = goldAccent)
                }
            }
        }

        // SMART ALERTS
        if (alerts.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(LoanMasterTheme.spacing.md),
                colors = CardDefaults.cardColors(containerColor = primaryCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
            ) {
                Column(
                    modifier = Modifier.padding(
                        horizontal = LoanMasterTheme.spacing.screenPadding,
                        vertical = LoanMasterTheme.spacing.lg
                    ),
                    verticalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.md)
                ) {
                    Text(
                        text = "Smart Alerts",
                        fontSize = LoanMasterTheme.typography.title.fontSize,
                        fontWeight = FontWeight.Bold,
                        color = primaryText
                    )
                    
                    alerts.forEach { alert ->
                        val type = alert.type.name
                        val msg = alert.message
                        val color = when(alert.type) { AlertType.CRITICAL -> Color(0xFFF44336); AlertType.WARNING -> Color(0xFFFF9800); AlertType.POSITIVE -> Color(0xFF4CAF50); else -> Color.Gray }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val icon = when (type) {
                                "Critical" -> Icons.Rounded.Warning
                                "Warning" -> Icons.Rounded.Info
                                else -> Icons.Rounded.CheckCircle
                            }
                            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(LoanMasterTheme.components.iconSmall))
                            Spacer(Modifier.widthIn(min = LoanMasterTheme.spacing.sm))
                            Text(msg, fontSize = LoanMasterTheme.typography.body.fontSize * 0.9f, color = secondaryText)
                        }
                    }
                }
            }
        }

        // SMART OPPORTUNITIES
        if (opportunities.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(LoanMasterTheme.spacing.md),
                colors = CardDefaults.cardColors(containerColor = primaryCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
            ) {
                Column(
                    modifier = Modifier.padding(
                        horizontal = LoanMasterTheme.spacing.screenPadding,
                        vertical = LoanMasterTheme.spacing.lg
                    ),
                    verticalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.md)
                ) {
                    Text(
                        text = "Smart Opportunities",
                        fontSize = LoanMasterTheme.typography.title.fontSize,
                        fontWeight = FontWeight.Bold,
                        color = primaryText
                    )

                    opportunities.forEach { opp ->
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(Icons.Rounded.Lightbulb, contentDescription = null, tint = goldAccent, modifier = Modifier.size(LoanMasterTheme.components.iconSmall).padding(top = LoanMasterTheme.spacing.xs))
                            Spacer(Modifier.widthIn(min = LoanMasterTheme.spacing.sm))
                            Column {
                                Text(opp.title, fontSize = LoanMasterTheme.typography.body.fontSize, fontWeight = FontWeight.Bold, color = primaryText)
                                Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.xs))
                                Text(opp.subtitle1, fontSize = LoanMasterTheme.typography.body.fontSize * 0.9f, color = accentGreen)
                                Text(opp.subtitle2, fontSize = LoanMasterTheme.typography.body.fontSize * 0.9f, color = blueAccent)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BreakdownItem(title: String, amount: String, color: Color) {
    Column {
        Text(title, fontSize = LoanMasterTheme.typography.label.fontSize, color = color.copy(alpha = 0.8f))
        Text(amount, fontWeight = FontWeight.Bold, fontSize = LoanMasterTheme.typography.body.fontSize, color = color)
    }
}
