package com.example

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class SmartRecommendation(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val accentColor: Color,
    val currentEmi: Double,
    val targetEmi: Double,
    val currentTotalInterest: Double,
    val targetTotalInterest: Double,
    val currentTenureMonths: Int,
    val targetTenureMonths: Int,
    val isRecommended: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecommendationBottomSheet(
    recommendation: SmartRecommendation,
    isExpandedWidth: Boolean,
    isMediumWidth: Boolean,
    screenWidth: androidx.compose.ui.unit.Dp,
    onDismissRequest: () -> Unit
) {
    val modalBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val bgColor = Color(0xFF020B1F)
    val cardColor = Color(0xFF061633)
    val inputBg = Color(0xFF071833)
    val borderColor = Color(0xFF183C8A)
    val primaryText = Color.White
    val secondaryText = Color(0xFFA8B3D1)
    val greenAccent = Color(0xFF22C55E)
    val redAccent = Color(0xFFFF5252)

    val interestSaved = recommendation.currentTotalInterest - recommendation.targetTotalInterest
    val extraEmi = recommendation.targetEmi - recommendation.currentEmi
    val tenureSaved = recommendation.currentTenureMonths - recommendation.targetTenureMonths

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = modalBottomSheetState,
        containerColor = bgColor,
        dragHandle = { BottomSheetDefaults.DragHandle(color = secondaryText.copy(alpha = 0.5f)) },
        modifier = Modifier.fillMaxHeight(0.9f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = if (isExpandedWidth) 32.dp else 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = recommendation.accentColor.copy(alpha = 0.2f),
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = recommendation.icon,
                        contentDescription = null,
                        tint = recommendation.accentColor,
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(recommendation.title, color = primaryText, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Text(recommendation.description, color = secondaryText, fontSize = 14.sp)
                }
            }

            Spacer(Modifier.height(8.dp))
            Text("Plan Comparison", color = primaryText, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            
            // Plan Comparison
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Current Plan
                RecommendationComparisonCard(
                    title = "Current Plan",
                    emi = recommendation.currentEmi,
                    tenureMonths = recommendation.currentTenureMonths,
                    totalInterest = recommendation.currentTotalInterest,
                    cardColor = cardColor,
                    borderColor = borderColor,
                    modifier = Modifier.weight(1f)
                )

                // Recommended Plan
                RecommendationComparisonCard(
                    title = if(recommendation.isRecommended) "AI Recommended" else "New Plan",
                    emi = recommendation.targetEmi,
                    tenureMonths = recommendation.targetTenureMonths,
                    totalInterest = recommendation.targetTotalInterest,
                    cardColor = recommendation.accentColor.copy(alpha = 0.1f),
                    borderColor = recommendation.accentColor.copy(alpha = 0.5f),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(8.dp))
            Text("Impact Summary", color = primaryText, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)

            // Impact Summary
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (interestSaved > 0) {
                    ImpactCard(
                        icon = Icons.Rounded.Savings,
                        title = "Interest Saved",
                        value = "₹" + formatMoney(interestSaved).replace("₹", ""),
                        accentColor = greenAccent,
                        bgColor = cardColor,
                        borderColor = borderColor
                    )
                } else if (interestSaved < 0) {
                    ImpactCard(
                        icon = Icons.Rounded.MoneyOff,
                        title = "Additional Interest Cost",
                        value = "₹" + formatMoney(-interestSaved).replace("₹", ""),
                        accentColor = redAccent,
                        bgColor = cardColor,
                        borderColor = borderColor
                    )
                }

                if (tenureSaved > 0) {
                    ImpactCard(
                        icon = Icons.Rounded.AccessTime,
                        title = "Loan Closed Earlier By",
                        value = "${tenureSaved / 12} years ${if (tenureSaved % 12 > 0) "and ${tenureSaved % 12} months" else ""}",
                        accentColor = greenAccent,
                        bgColor = cardColor,
                        borderColor = borderColor
                    )
                } else if (tenureSaved < 0) {
                    ImpactCard(
                        icon = Icons.Rounded.AccessTime,
                        title = "Loan Tenure Extended By",
                        value = "${(-tenureSaved) / 12} years",
                        accentColor = redAccent,
                        bgColor = cardColor,
                        borderColor = borderColor
                    )
                }

                if (extraEmi > 0) {
                    ImpactCard(
                        icon = Icons.Rounded.TrendingUp,
                        title = "Extra EMI Required",
                        value = "₹" + formatMoney(extraEmi).replace("₹", "") + " /mo",
                        accentColor = Color(0xFFFFC328),
                        bgColor = cardColor,
                        borderColor = borderColor
                    )
                } else if (extraEmi < 0) {
                    ImpactCard(
                        icon = Icons.Rounded.TrendingDown,
                        title = "EMI Reduced By",
                        value = "₹" + formatMoney(-extraEmi).replace("₹", "") + " /mo",
                        accentColor = greenAccent,
                        bgColor = cardColor,
                        borderColor = borderColor
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
            StrategyInsightCard(recommendation.id, cardColor, borderColor)
            
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
fun RecommendationComparisonCard(
    title: String,
    emi: Double,
    tenureMonths: Int,
    totalInterest: Double,
    cardColor: Color,
    borderColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            
            Column {
                Text("EMI", color = Color(0xFFA8B3D1), fontSize = 11.sp)
                Text(formatMoney(emi), color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
            
            Column {
                Text("Tenure", color = Color(0xFFA8B3D1), fontSize = 11.sp)
                Text("${tenureMonths / 12} Yrs ${if (tenureMonths % 12 > 0) "${tenureMonths % 12} Mo" else ""}", color = Color.White, fontSize = 14.sp)
            }
            
            Column {
                Text("Total Interest", color = Color(0xFFA8B3D1), fontSize = 11.sp)
                Text(formatMoney(totalInterest), color = Color.White, fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun ImpactCard(
    icon: ImageVector,
    title: String,
    value: String,
    accentColor: Color,
    bgColor: Color,
    borderColor: Color
) {
    Surface(
        color = bgColor,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, borderColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(16.dp))
            Column {
                Text(title, color = Color(0xFFA8B3D1), fontSize = 13.sp)
                Text(value, color = accentColor, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun StrategyInsightCard(strategyId: String, bgColor: Color, borderColor: Color) {
    val insight = when (strategyId) {
        "best_savings" -> "By slightly increasing your monthly EMI by 15%, more of your payment goes toward the principal early on. This drastically cuts down the compounding interest over the years."
        "fastest_closure" -> "Increasing your EMI by 25% accelerates principal repayment aggressively, helping you become debt-free years earlier than planned."
        "lowest_emi" -> "Extending the tenure lowers your immediate monthly burden, effectively preserving your short-term cash flow, though at the expense of higher overall combined interest."
        "ai_recommended" -> "This AI-balanced plan combines moderate EMI increases with strategic pre-payments to ensure solid interest savings without heavy monthly strain."
        else -> "This strategy is calculated to optimize your loan parameters based on your stated preference."
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Lightbulb, contentDescription = null, tint = Color(0xFFFFC328), modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("Why this works", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(8.dp))
            Text(insight, color = Color(0xFFA8B3D1), fontSize = 14.sp, lineHeight = 20.sp)
        }
    }
}
