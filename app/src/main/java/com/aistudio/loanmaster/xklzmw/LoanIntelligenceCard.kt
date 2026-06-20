package com.aistudio.loanmaster.xklzmw

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
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
import com.example.CurrencyFormatter
import com.example.ResponsiveUtils
import com.example.WindowWidthSizeClass
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
    sizeClass: WindowWidthSizeClass = WindowWidthSizeClass.Compact,
    modifier: Modifier = Modifier
) {
    val interestBurdenRatio = if (loanAmount > 0) totalInterest / loanAmount else 0.0

    val (verdictGrade, verdictTitle, verdictDesc, verdictColor) = remember(loanType, interestBurdenRatio) {
        val thresholds = when (loanType) {
            "Home Loan" -> listOf(0.40, 0.80, 1.20, 1.60) // Home loans naturally have high interest burden over long tenure
            "Education Loan" -> listOf(0.30, 0.60, 0.90, 1.20)
            "Car Loan" -> listOf(0.15, 0.30, 0.50, 0.70)
            "Personal Loan", "Business Loan" -> listOf(0.15, 0.30, 0.45, 0.60)
            else -> listOf(0.30, 0.50, 0.80, 1.20)
        }
        val (t1, t2, t3, t4) = thresholds
        when {
            interestBurdenRatio < t1 -> listOf("A+", "Excellent Deal", "Interest burden is exceptionally low for this loan type.", Color(0xFF4CAF50))
            interestBurdenRatio < t2 -> listOf("A", "Good Deal", "Total interest remains reasonable for the selected tenure.", Color(0xFF8BC34A))
            interestBurdenRatio < t3 -> listOf("B", "Acceptable", "Standard interest cost, but prepayments could help.", Color(0xFFFFC107))
            interestBurdenRatio < t4 -> listOf("C", "Expensive", "Long tenure significantly increases borrowing cost.", Color(0xFFFF9800))
            else -> listOf("D", "High Cost", "Interest significantly exceeds the norm. Highly expensive.", Color(0xFFF44336))
        }
    }

    val alerts = remember(loanType, loanAmount, interestRate, tenureYears, totalInterest, totalPayment) {
        val list = mutableListOf<Triple<String, String, Color>>()
        
        // Critical
        val criticalRatio = when (loanType) {
            "Home Loan" -> 1.5
            "Education Loan" -> 1.2
            "Car Loan" -> 0.6
            else -> 0.5
        }
        if (interestBurdenRatio > criticalRatio) list.add(Triple("Critical", "Interest burden is critically high for this loan type.", Color(0xFFF44336)))
        if (totalInterest > 1_000_000 && loanType != "Home Loan" && loanType != "Business Loan") list.add(Triple("Critical", "Total interest exceeds ₹10 lakh.", Color(0xFFF44336)))
        
        // Warning based on Loan Type
        when (loanType) {
            "Home Loan" -> {
                if (interestRate > 10.0) list.add(Triple("Warning", "Home loan rates are typically below 10%.", Color(0xFFFF9800)))
                if (tenureYears > 30) list.add(Triple("Warning", "Tenure is unusually long (>30 years).", Color(0xFFFF9800)))
            }
            "Car Loan" -> {
                if (interestRate > 12.0) list.add(Triple("Warning", "Car loan rates are typically below 12%.", Color(0xFFFF9800)))
                if (tenureYears > 7) list.add(Triple("Warning", "Tenure over 7 years depreciates the car faster than the loan.", Color(0xFFFF9800)))
            }
            "Personal Loan" -> {
                if (interestRate > 20.0) list.add(Triple("Warning", "Personal loan interest rate is considered very high.", Color(0xFFFF9800)))
                if (tenureYears > 5) list.add(Triple("Warning", "Personal loans normally have a max tenure of 5 years.", Color(0xFFFF9800)))
            }
            "Education Loan" -> {
                if (interestRate > 15.0) list.add(Triple("Warning", "Education loan rates shouldn't typically exceed 15%.", Color(0xFFFF9800)))
                if (tenureYears > 15) list.add(Triple("Warning", "Education loans usually don't exceed 15 years.", Color(0xFFFF9800)))
            }
            "Business Loan" -> {
                if (interestRate > 18.0) list.add(Triple("Warning", "Business loan rates are generally 12%-18%.", Color(0xFFFF9800)))
                if (tenureYears > 10) list.add(Triple("Warning", "Business loan tenure normally within 10 years.", Color(0xFFFF9800)))
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
            list.add(Triple("Warning", "Interest burden is high for this type of loan.", Color(0xFFFF9800)))
        }
        if (tenureYears > 15 && totalInterest > loanAmount * 0.5 && loanType != "Home Loan") list.add(Triple("Warning", "Long tenure is increasing total borrowing cost.", Color(0xFFFF9800)))
        if (totalInterest > 500_000 && totalInterest <= 1_000_000 && loanType != "Home Loan" && loanType != "Business Loan") list.add(Triple("Warning", "Interest exceeds ₹5 lakh.", Color(0xFFFF9800)))
        
        // Positive
        val positiveRatio = when (loanType) {
            "Home Loan" -> 0.4
            "Education Loan" -> 0.3
            "Car Loan" -> 0.15
            else -> 0.15
        }
        if (interestBurdenRatio <= positiveRatio && loanAmount > 0) list.add(Triple("Positive", "Interest cost is exceptionally low for this loan type.", Color(0xFF4CAF50)))
        else if (interestBurdenRatio <= positiveRatio + 0.15 && loanAmount > 0) list.add(Triple("Positive", "Loan structure is cost efficient.", Color(0xFF4CAF50)))
        
        val optimalTenureEnd = when (loanType) {
            "Home Loan" -> 20
            "Education Loan" -> 10
            "Car Loan" -> 5
            else -> 3
        }
        if (tenureYears in 2..optimalTenureEnd) list.add(Triple("Positive", "Tenure is well balanced.", Color(0xFF4CAF50)))
        
        list.take(4) // Max 4 alerts
    }

    val opportunities = remember(loanAmount, interestRate, tenureYears, monthlyEmi) {
        val list = mutableListOf<String>()
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
            if (totalInterest - int1k > 0) {
                val yearsSaved = (totalMonths - month1k) / 12
                if (yearsSaved > 0) {
                    list.add("Increasing EMI by ₹1,000/month could save ${CurrencyFormatter.formatMoney(totalInterest - int1k)} and close the loan $yearsSaved years earlier.")
                }
            }

            val (int2k, month2k) = simulate(2000.0)
            if (totalInterest - int2k > 0) {
                val yearsSaved = (totalMonths - month2k) / 12
                if (yearsSaved > 0) {
                    list.add("Increasing EMI by ₹2,000/month could save ${CurrencyFormatter.formatMoney(totalInterest - int2k)} and close the loan $yearsSaved years earlier.")
                }
            }

            if (interestRate > 1.0) {
                val newRate = interestRate - 1.0
                val newR = (newRate / 12) / 100
                val newDenom = (1 + newR).pow(totalMonths) - 1
                val newEmi = loanAmount * newR * (1 + newR).pow(totalMonths) / newDenom
                val newTotalInt = (newEmi * totalMonths) - loanAmount
                if (totalInterest - newTotalInt > 0) {
                    list.add("A 1% lower interest rate could save approximately ${CurrencyFormatter.formatMoney(totalInterest - newTotalInt)}.")
                }
            }
        }
        list.take(3)
    }

    val interestPercentage = if (totalPayment > 0) (totalInterest / totalPayment * 100).toInt() else 0
    val principalPercentage = if (totalPayment > 0) 100 - interestPercentage else 0

    val primaryCard = Color(0xFF061633)
    val borderColor = Color(0xFF183C8A)
    val primaryText = Color(0xFFFFFFFF)
    val secondaryText = Color(0xFFA8B3D1)
    val blueAccent = Color(0xFF2D7DFF)
    val goldAccent = Color(0xFFFFC328)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(ResponsiveUtils.cardSpacing(sizeClass))
    ) {
        // LOAN VERDICT
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = primaryCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
        ) {
            Column(
                modifier = Modifier.padding(
                    horizontal = ResponsiveUtils.horizontalPadding(sizeClass),
                    vertical = ResponsiveUtils.verticalPadding(sizeClass)
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.EmojiEvents, contentDescription = null, tint = verdictColor as Color, modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "🏆 Loan Verdict",
                            fontSize = ResponsiveUtils.bodyFontSize(sizeClass),
                            color = secondaryText
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = verdictGrade as String,
                                fontSize = ResponsiveUtils.titleFontSize(sizeClass),
                                fontWeight = FontWeight.Bold,
                                color = verdictColor
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = verdictTitle as String,
                                fontSize = ResponsiveUtils.bodyFontSize(sizeClass) * 1.1f,
                                fontWeight = FontWeight.Bold,
                                color = primaryText
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = verdictDesc as String,
                    fontSize = ResponsiveUtils.bodyFontSize(sizeClass) * 0.9f,
                    color = secondaryText
                )
            }
        }

        // COST BREAKDOWN
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = primaryCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
        ) {
            Column(
                modifier = Modifier.padding(
                    horizontal = ResponsiveUtils.horizontalPadding(sizeClass),
                    vertical = ResponsiveUtils.verticalPadding(sizeClass)
                )
            ) {
                Text(
                    text = "Cost Breakdown",
                    fontSize = ResponsiveUtils.titleFontSize(sizeClass),
                    fontWeight = FontWeight.Bold,
                    color = primaryText
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    BreakdownItem("Borrowed Amount", CurrencyFormatter.formatMoney(loanAmount), blueAccent)
                    BreakdownItem("Interest Cost", CurrencyFormatter.formatMoney(totalInterest), goldAccent)
                    BreakdownItem("Total Repayment", CurrencyFormatter.formatMoney(totalPayment), primaryText)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth().height(12.dp).clip(RoundedCornerShape(6.dp))) {
                    Box(modifier = Modifier.weight(principalPercentage.toFloat() + 0.1f).fillMaxHeight().background(blueAccent))
                    Box(modifier = Modifier.weight(interestPercentage.toFloat() + 0.1f).fillMaxHeight().background(goldAccent))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Principal Share: $principalPercentage%", fontSize = ResponsiveUtils.bodyFontSize(sizeClass) * 0.8f, color = blueAccent)
                    Text("Interest Share: $interestPercentage%", fontSize = ResponsiveUtils.bodyFontSize(sizeClass) * 0.8f, color = goldAccent)
                }
            }
        }

        // SMART ALERTS
        if (alerts.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = primaryCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
            ) {
                Column(
                    modifier = Modifier.padding(
                        horizontal = ResponsiveUtils.horizontalPadding(sizeClass),
                        vertical = ResponsiveUtils.verticalPadding(sizeClass)
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Smart Alerts",
                        fontSize = ResponsiveUtils.titleFontSize(sizeClass),
                        fontWeight = FontWeight.Bold,
                        color = primaryText
                    )
                    
                    alerts.forEach { (type, msg, color) ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val icon = when (type) {
                                "Critical" -> Icons.Rounded.Warning
                                "Warning" -> Icons.Rounded.Info
                                else -> Icons.Rounded.CheckCircle
                            }
                            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(msg, fontSize = ResponsiveUtils.bodyFontSize(sizeClass) * 0.9f, color = secondaryText)
                        }
                    }
                }
            }
        }

        // SMART OPPORTUNITIES
        if (opportunities.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = primaryCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
            ) {
                Column(
                    modifier = Modifier.padding(
                        horizontal = ResponsiveUtils.horizontalPadding(sizeClass),
                        vertical = ResponsiveUtils.verticalPadding(sizeClass)
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Smart Opportunities",
                        fontSize = ResponsiveUtils.titleFontSize(sizeClass),
                        fontWeight = FontWeight.Bold,
                        color = primaryText
                    )

                    opportunities.forEach { opp ->
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(Icons.Rounded.Lightbulb, contentDescription = null, tint = goldAccent, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(opp, fontSize = ResponsiveUtils.bodyFontSize(sizeClass) * 0.9f, color = secondaryText)
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
        Text(title, fontSize = 12.sp, color = color.copy(alpha = 0.8f))
        Text(amount, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = color)
    }
}
