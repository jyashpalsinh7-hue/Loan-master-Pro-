package com.loanmaster.pro.feature.loaneligibility.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import kotlinx.coroutines.launch
import com.loanmaster.pro.core.theme.*
import com.loanmaster.pro.core.ui.*
import com.loanmaster.pro.core.formatter.formatMoney
import com.loanmaster.pro.core.responsive.*
import com.loanmaster.pro.feature.loaneligibility.util.*
import com.loanmaster.pro.domain.model.LoanProfile

@Composable
fun ResultsSection(
    isResultVisible: Boolean,
    isInputExpanded: Boolean,
    isCalculating: Boolean,
    currentFoir: Double,
    foirLimit: Double,
    uiStateVerdictGrade: String,
    eligibleLoanAmount: Double,
    availableEmi: Double,
    maxAllowedEmi: Double,
    totalIncome: Double,
    totalExistingEmi: Double,
    tenureYears: String,
    creditScoreRange: String,
    onModifyInputsClick: () -> Unit,
    intelligenceState: com.loanmaster.pro.feature.loanintelligence.model.LoanIntelligenceState,
    onWatchAdClick: () -> Unit,
    onPremiumClick: () -> Unit
) {
    val surfaceColor = SurfaceDark
    val brightBlue = AccentBlue
    val neonGreen = AccentGreen
    val warningYellow = Color(0xFFFBBF24)
    val riskOrange = Color(0xFFFF9800)
    val dangerRed = Color(0xFFF44336)
    val textColor = TextPrimary
    val textSecondary = TextSecondary
    val bgColor = BackgroundDark

                    AnimatedVisibility(
                        visible = isResultVisible && !isInputExpanded && !isCalculating,
                        enter = slideInVertically(initialOffsetY = { 150 }, animationSpec = tween(800, easing = FastOutSlowInEasing)) + fadeIn(animationSpec = tween(800)),
                        exit = slideOutVertically(targetOffsetY = { 150 }, animationSpec = tween(500)) + fadeOut(animationSpec = tween(500))
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            
                            val targetApprovalProb = (1.0f - (currentFoir.toFloat() / (foirLimit.toFloat().coerceAtLeast(0.01f)))).coerceIn(0f, 1f)
                            val approvalProb by animateFloatAsState(targetValue = if (isResultVisible && !isInputExpanded) targetApprovalProb else 0f, animationSpec = tween(1500, easing = FastOutSlowInEasing), label = "probAnim")
                            
                            val riskState = when {
                                targetApprovalProb > 0.7f -> "Safe"
                                targetApprovalProb > 0.5f -> "Moderate"
                                targetApprovalProb > 0.3f -> "Risk"
                                else -> "Critical"
                            }
                            
                            val targetStateColor = when(riskState) {
                                "Safe" -> neonGreen
                                "Moderate" -> warningYellow
                                "Risk" -> riskOrange
                                else -> dangerRed
                            }
                            val stateColor by animateColorAsState(targetValue = targetStateColor, label = "stateColor")
                            
                            val conf = when(riskState) {
                                "Safe" -> "High"
                                "Moderate" -> "Medium"
                                "Risk" -> "Low"
                                else -> "Very Low"
                            }
                            
                            val gradeRaw = uiStateVerdictGrade.ifEmpty { 
                                when(riskState) {
                                    "Safe" -> "A+"
                                    "Moderate" -> "B"
                                    "Risk" -> "C"
                                    else -> "D"
                                }
                            }
                            
                            val gradeColor = when {
                                gradeRaw.startsWith("A") -> neonGreen
                                gradeRaw.startsWith("B") -> warningYellow
                                gradeRaw.startsWith("C") -> riskOrange
                                else -> dangerRed
                            }

                            val animatedAmount by animateFloatAsState(
                                targetValue = if (isResultVisible && !isInputExpanded) eligibleLoanAmount.toFloat() else 0f,
                                animationSpec = tween(1500, easing = FastOutSlowInEasing), label = "amountAnim"
                            )
                            val animatedAvailableEmi by animateFloatAsState(targetValue = if (isResultVisible && !isInputExpanded) availableEmi.toFloat().coerceAtLeast(0f) else 0f, animationSpec = tween(1500, easing = FastOutSlowInEasing), label = "")
                            val animatedMaxAllowedEmi by animateFloatAsState(targetValue = if (isResultVisible && !isInputExpanded) maxAllowedEmi.toFloat() else 0f, animationSpec = tween(1500, easing = FastOutSlowInEasing), label = "")
                            val animatedTotalIncome by animateFloatAsState(targetValue = if (isResultVisible && !isInputExpanded) totalIncome.toFloat() else 0f, animationSpec = tween(1500, easing = FastOutSlowInEasing), label = "")
                            val animatedTotalExistingEmi by animateFloatAsState(targetValue = if (isResultVisible && !isInputExpanded) totalExistingEmi.toFloat() else 0f, animationSpec = tween(1500, easing = FastOutSlowInEasing), label = "")

                            AnimatedContent(
                                targetState = eligibleLoanAmount <= 0.0,
                                transitionSpec = { fadeIn(animationSpec = tween(500)) togetherWith fadeOut(animationSpec = tween(500)) },
                                label = "eligibilityTransition"
                            ) { isNotEligible ->
                                if (isNotEligible) {

                                    LoanNotEligibleCard(
                                        totalIncome = totalIncome,
                                        currentFoir = currentFoir,
                                        foirLimit = foirLimit,
                                        totalExistingEmi = totalExistingEmi,
                                        tenureYears = tenureYears,
                                        creditScoreRange = creditScoreRange,
                                        onModifyInputsClick = onModifyInputsClick
                                    )
                                } else {
                                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                        ResultCard(
                                            riskState = riskState,
                                            stateColor = stateColor,
                                            animatedAmount = animatedAmount,
                                            animatedAvailableEmi = animatedAvailableEmi,
                                            animatedMaxAllowedEmi = animatedMaxAllowedEmi,
                                            animatedTotalIncome = animatedTotalIncome,
                                            animatedTotalExistingEmi = animatedTotalExistingEmi
                                        )
                                        

                                        ApprovalCard(
                                            approvalProb = approvalProb,
                                            currentFoir = currentFoir,
                                            conf = conf,
                                            gradeRaw = gradeRaw,
                                            gradeColor = gradeColor,
                                            stateColor = stateColor,
                                            bgColor = bgColor
                                        )
                                        
                                        com.loanmaster.pro.feature.loanintelligence.components.LoanIntelligenceCard(
                                            state = intelligenceState,
                                            onWatchAdClick = onWatchAdClick,
                                            onPremiumClick = onPremiumClick
                                        )

                                        ResultActions(
                                            onCompareClick = { },
                                            onDetailedReportClick = { },
                                            onSaveCalcClick = { },
                                            onExportPdfClick = { }
                                        )
                                    }
                                }
                            }
                        }
                    }
}
