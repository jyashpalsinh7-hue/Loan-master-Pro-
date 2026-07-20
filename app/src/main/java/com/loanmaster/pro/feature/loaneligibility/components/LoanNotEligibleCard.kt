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
import com.loanmaster.pro.core.responsive.AdaptiveRowCol
import com.loanmaster.pro.core.responsive.ResponsiveScreenWrapper

import com.loanmaster.pro.feature.loaneligibility.util.*
import com.loanmaster.pro.feature.loaneligibility.components.*
import com.loanmaster.pro.domain.model.LoanProfile



@Composable
fun LoanNotEligibleCard(
    totalIncome: Double,
    currentFoir: Double,
    foirLimit: Double,
    totalExistingEmi: Double,
    tenureYears: String,
    creditScoreRange: String,
    onModifyInputsClick: () -> Unit
) {
    val surfaceColor = SurfaceDark
    val brightBlue = AccentBlue
    val neonGreen = AccentGreen
    val dangerRed = Color(0xFFF44336)
    val textColor = TextPrimary
    val textSecondary = TextSecondary

    Card(
                                    colors = CardDefaults.cardColors(containerColor = surfaceColor.copy(alpha = 0.25f)),
                                    shape = RoundedCornerShape(24.dp),
                                    border = BorderStroke(1.dp, dangerRed.copy(alpha = 0.5f)),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                                    modifier = Modifier.fillMaxWidth().shadow(8.dp, RoundedCornerShape(24.dp), spotColor = dangerRed)
                                ) {
                                    Column(modifier = Modifier.fillMaxWidth().padding(LoanMasterTheme.spacing.lg)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Rounded.Warning, contentDescription = null, tint = dangerRed, modifier = Modifier.size(24.dp))
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Text("Loan Not Eligible", color = textColor, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Spacer(modifier = Modifier.height(LoanMasterTheme.spacing.lg))
                                        val reasons = mutableListOf<String>()
                                        val dynamicSuggestions = mutableSetOf<String>()
                                        if (totalIncome < 15000.0) {
                                            reasons.add("Income is too low.")
                                            dynamicSuggestions.add("Increase monthly income")
                                            dynamicSuggestions.add("Add co-borrower")
                                        }
                                        if (currentFoir >= foirLimit) {
                                            reasons.add("FOIR is above the permitted threshold.")
                                            dynamicSuggestions.add("Reduce existing EMI")
                                            dynamicSuggestions.add("Reduce requested loan amount")
                                        }
                                        if (totalExistingEmi >= totalIncome * 0.5) {
                                            reasons.add("Existing EMI exceeds bank limits.")
                                            dynamicSuggestions.add("Reduce existing EMI")
                                        }
                                        val tYrs = tenureYears.toDoubleOrNull() ?: 0.0
                                        if (tYrs < 2.0) {
                                            reasons.add("Loan tenure is insufficient.")
                                            dynamicSuggestions.add("Select longer tenure")
                                        }
                                        if (creditScoreRange == "Below 600" || creditScoreRange == "600 - 650") {
                                            reasons.add("Credit profile is weak.")
                                            dynamicSuggestions.add("Improve credit score")
                                        }
                                        if (reasons.isEmpty()) {
                                            reasons.add("Does not meet minimum requirements.")
                                        }
                                        
                                        Text("Reasons", color = textSecondary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        reasons.forEach { rsn ->
                                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 6.dp)) {
                                                Icon(Icons.Rounded.Circle, contentDescription = null, tint = dangerRed, modifier = Modifier.size(6.dp))
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(rsn, color = textColor, fontSize = 14.sp)
                                            }
                                        }
                                        
                                        if (dynamicSuggestions.isNotEmpty()) {
                                            Spacer(modifier = Modifier.height(LoanMasterTheme.spacing.lg))
                                            Text("Suggestions", color = textSecondary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                            Spacer(modifier = Modifier.height(8.dp))
                                            dynamicSuggestions.forEach { sug ->
                                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 6.dp)) {
                                                    Icon(Icons.Rounded.Check, contentDescription = null, tint = neonGreen, modifier = Modifier.size(16.dp))
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(sug, color = textColor, fontSize = 14.sp)
                                                }
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(LoanMasterTheme.spacing.lg))
                                        
                                        val btnSource = remember { MutableInteractionSource() }
                                        val isBtnPressed by btnSource.collectIsPressedAsState()
                                        val modBtnScale by animateFloatAsState(targetValue = if (isBtnPressed) 0.97f else 1f, animationSpec = tween(150), label = "")
                                        val btnElevation by animateDpAsState(if (isBtnPressed) 2.dp else 8.dp, label = "")
                                        val btnGradient = Brush.horizontalGradient(listOf(brightBlue, brightBlue.copy(alpha = 0.8f)))
                                        
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(64.dp)
                                                .graphicsLayer { scaleX = modBtnScale; scaleY = modBtnScale }
                                                .shadow(btnElevation, RoundedCornerShape(24.dp), spotColor = brightBlue)
                                                .clip(RoundedCornerShape(24.dp))
                                                .background(btnGradient)
                                                .clickable(interactionSource = btnSource, indication = LocalIndication.current) {
                                                    onModifyInputsClick()
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("Modify Inputs", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

}
