package com.loanmaster.pro

import com.loanmaster.pro.ui.theme.*

import com.loanmaster.pro.model.*

import com.loanmaster.pro.ui.theme.LoanMasterTheme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material.icons.automirrored.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun RecommendationBottomSheet(
    recommendation: SmartRecommendation,
    isExpandedWidth: Boolean,
    isMediumWidth: Boolean,
    screenWidth: androidx.compose.ui.unit.Dp,
    onDismissRequest: () -> Unit
) {
    val modalBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val bgColor = BackgroundDark
    val cardColor = SurfaceDark
    val inputBg = SurfaceDark
    val borderColor = CardStroke
    val primaryText = TextPrimary
    val secondaryText = TextSecondary
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
                .padding(horizontal = if (isExpandedWidth) LoanMasterTheme.spacing.xl else LoanMasterTheme.spacing.md)
                .verticalScroll(rememberScrollState())
                .windowInsetsPadding(WindowInsets.safeDrawing),
            verticalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.md)
        ) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = when(recommendation.id) { "best_savings" -> Color(0xFF22C55E); "fast_track" -> Color(0xFF3B82F6); "interest_minimizer" -> Color(0xFFA855F7); "custom" -> Color(0xFFF59E0B); else -> Color(0xFF9E9E9E) }.copy(alpha = 0.2f),
                    modifier = Modifier.size(LoanMasterTheme.components.iconLarge)
                ) {
                    Icon(
                        imageVector = when(recommendation.id) { "best_savings" -> Icons.Rounded.Savings; "fast_track" -> Icons.Rounded.Speed; "interest_minimizer" -> Icons.AutoMirrored.Rounded.TrendingDown; "custom" -> Icons.Rounded.AutoAwesome; else -> Icons.Rounded.Info },
                        contentDescription = null,
                        tint = when(recommendation.id) { "best_savings" -> Color(0xFF22C55E); "fast_track" -> Color(0xFF3B82F6); "interest_minimizer" -> Color(0xFFA855F7); "custom" -> Color(0xFFF59E0B); else -> Color(0xFF9E9E9E) },
                        modifier = Modifier
                            .padding(LoanMasterTheme.spacing.md)
                            .fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.md))
                Column {
                    Text(recommendation.title, color = primaryText, fontSize = LoanMasterTheme.typography.title.fontSize, fontWeight = FontWeight.Bold)
                    Text(recommendation.description, color = secondaryText, fontSize = LoanMasterTheme.typography.body.fontSize)
                }
            }

            Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.sm))
            Text("Plan Comparison", color = primaryText, fontSize = LoanMasterTheme.typography.title.fontSize, fontWeight = FontWeight.SemiBold)
            
            // Plan Comparison
            Row(
                horizontalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.md),
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
                    cardColor = when(recommendation.id) { "best_savings" -> Color(0xFF22C55E); "fast_track" -> Color(0xFF3B82F6); "interest_minimizer" -> Color(0xFFA855F7); "custom" -> Color(0xFFF59E0B); else -> Color(0xFF9E9E9E) }.copy(alpha = 0.1f),
                    borderColor = when(recommendation.id) { "best_savings" -> Color(0xFF22C55E); "fast_track" -> Color(0xFF3B82F6); "interest_minimizer" -> Color(0xFFA855F7); "custom" -> Color(0xFFF59E0B); else -> Color(0xFF9E9E9E) }.copy(alpha = 0.5f),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.sm))
            Text("Impact Summary", color = primaryText, fontSize = LoanMasterTheme.typography.title.fontSize, fontWeight = FontWeight.SemiBold)

            // Impact Summary
            Column(verticalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.md)) {
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
                        icon = Icons.AutoMirrored.Rounded.TrendingUp,
                        title = "Extra EMI Required",
                        value = "₹" + formatMoney(extraEmi).replace("₹", "") + " /mo",
                        accentColor = Color(0xFFFFC328),
                        bgColor = cardColor,
                        borderColor = borderColor
                    )
                } else if (extraEmi < 0) {
                    ImpactCard(
                        icon = Icons.AutoMirrored.Rounded.TrendingDown,
                        title = "EMI Reduced By",
                        value = "₹" + formatMoney(-extraEmi).replace("₹", "") + " /mo",
                        accentColor = greenAccent,
                        bgColor = cardColor,
                        borderColor = borderColor
                    )
                }
            }

            Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.sm))
            StrategyInsightCard(recommendation.id, cardColor, borderColor)
            
            Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.xl))
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
        shape = RoundedCornerShape(LoanMasterTheme.spacing.md),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(modifier = Modifier.padding(LoanMasterTheme.spacing.md), verticalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.md)) {
            Text(title, color = Color.White, fontSize = LoanMasterTheme.typography.body.fontSize, fontWeight = FontWeight.SemiBold)
            
            Column {
                Text("EMI", color = Color(0xFFA8B3D1), fontSize = LoanMasterTheme.typography.label.fontSize)
                Text(formatMoney(emi), color = Color.White, fontSize = LoanMasterTheme.typography.body.fontSize, fontWeight = FontWeight.Bold)
            }
            
            Column {
                Text("Tenure", color = Color(0xFFA8B3D1), fontSize = LoanMasterTheme.typography.label.fontSize)
                Text("${tenureMonths / 12} Yrs ${if (tenureMonths % 12 > 0) "${tenureMonths % 12} Mo" else ""}", color = Color.White, fontSize = LoanMasterTheme.typography.body.fontSize)
            }
            
            Column {
                Text("Total Interest", color = Color(0xFFA8B3D1), fontSize = LoanMasterTheme.typography.label.fontSize)
                Text(formatMoney(totalInterest), color = Color.White, fontSize = LoanMasterTheme.typography.body.fontSize)
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
        shape = RoundedCornerShape(LoanMasterTheme.spacing.md),
        border = BorderStroke(1.dp, borderColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(LoanMasterTheme.spacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(LoanMasterTheme.spacing.lg)
            )
            Spacer(Modifier.widthIn(min = LoanMasterTheme.spacing.md))
            Column {
                Text(title, color = Color(0xFFA8B3D1), fontSize = LoanMasterTheme.typography.label.fontSize)
                Text(value, color = accentColor, fontSize = LoanMasterTheme.typography.body.fontSize, fontWeight = FontWeight.Bold)
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
        shape = RoundedCornerShape(LoanMasterTheme.spacing.md),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(modifier = Modifier.padding(LoanMasterTheme.spacing.md)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Lightbulb, contentDescription = null, tint = Color(0xFFFFC328), modifier = Modifier.size(LoanMasterTheme.components.iconSmall))
                Spacer(Modifier.widthIn(min = LoanMasterTheme.spacing.sm))
                Text("Why this works", color = Color.White, fontSize = LoanMasterTheme.typography.body.fontSize, fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.sm))
            Text(insight, color = Color(0xFFA8B3D1), fontSize = LoanMasterTheme.typography.body.fontSize, lineHeight = LoanMasterTheme.typography.title.fontSize)
        }
    }
}
