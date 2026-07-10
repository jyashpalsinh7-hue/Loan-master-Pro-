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
fun ResultCard(
    riskState: String,
    stateColor: Color,
    animatedAmount: Float,
    animatedAvailableEmi: Float,
    animatedMaxAllowedEmi: Float,
    animatedTotalIncome: Float,
    animatedTotalExistingEmi: Float
) {
    val surfaceColor = SurfaceDark
    val brightBlue = AccentBlue
    val neonGreen = AccentGreen
    val warningYellow = Color(0xFFFBBF24)
    val dangerRed = Color(0xFFF44336)
    val textColor = TextPrimary
    val textSecondary = TextSecondary

                                val heroGradient = Brush.radialGradient(
                                    colors = listOf(stateColor.copy(alpha = 0.2f), brightBlue.copy(alpha = 0.05f), Color.Transparent),
                                    radius = 800f
                                )
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = surfaceColor.copy(alpha = 0.4f)),
                                    shape = RoundedCornerShape(24.dp),
                                    border = BorderStroke(1.dp, stateColor.copy(alpha = 0.3f)),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                                    modifier = Modifier.fillMaxWidth().shadow(16.dp, RoundedCornerShape(24.dp), spotColor = stateColor.copy(alpha = 0.5f), ambientColor = stateColor.copy(alpha = 0.1f))
                                ) {
                                    Box(modifier = Modifier.fillMaxWidth().background(heroGradient)) {
                                        Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                                Text("Eligible Loan Amount", color = textSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                                                Surface(
                                                    color = stateColor.copy(alpha = 0.1f),
                                                    shape = RoundedCornerShape(50),
                                                    border = BorderStroke(1.dp, stateColor.copy(alpha = 0.4f))
                                                ) {
                                                    Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                                                        val icon = when(riskState) {
                                                            "Safe" -> Icons.Rounded.VerifiedUser
                                                            "Critical" -> Icons.Rounded.Error
                                                            else -> Icons.Rounded.Warning
                                                        }
                                                        Icon(icon, contentDescription = null, tint = stateColor, modifier = Modifier.size(14.dp))
                                                        Spacer(modifier = Modifier.width(6.dp))
                                                        AnimatedContent(targetState = riskState, label = "badgeState") { state ->
                                                            Text("$state Zone", color = stateColor, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 0.5.sp)
                                                        }
                                                    }
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(16.dp))
                                            AutoResizedText(
                                                text = formatMoney(animatedAmount.toDouble()),
                                                color = stateColor,
                                                fontSize = 54.sp,
                                                fontWeight = FontWeight.Black,
                                                modifier = Modifier.fillMaxWidth(),
                                                style = androidx.compose.ui.text.TextStyle(
                                                    shadow = androidx.compose.ui.graphics.Shadow(
                                                        color = stateColor.copy(alpha = 0.5f),
                                                        blurRadius = 16f
                                                    )
                                                )
                                            )
                                            
                                            Spacer(modifier = Modifier.height(32.dp))
                                            HorizontalDivider(color = brightBlue.copy(alpha = 0.15f), thickness = 1.dp)
                                            Spacer(modifier = Modifier.height(24.dp))
                                            
                                            Row(modifier = Modifier.fillMaxWidth()) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text("Monthly EMI Capacity", color = textSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.5.sp)
                                                    Spacer(modifier = Modifier.height(8.dp))
                                                    Text(formatMoney(animatedAvailableEmi.toDouble()), color = neonGreen, fontSize = 18.sp, fontWeight = FontWeight.Black)
                                                }
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text("Max Affordable EMI", color = textSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.5.sp)
                                                    Spacer(modifier = Modifier.height(8.dp))
                                                    Text(formatMoney(animatedMaxAllowedEmi.toDouble()), color = textColor, fontSize = 18.sp, fontWeight = FontWeight.Black)
                                                }
                                            }
                                            
                                            Spacer(modifier = Modifier.height(24.dp))
                                            Row(modifier = Modifier.fillMaxWidth()) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text("Income", color = textSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.5.sp)
                                                    Spacer(modifier = Modifier.height(8.dp))
                                                    Text(formatMoney(animatedTotalIncome.toDouble()), color = textColor, fontSize = 18.sp, fontWeight = FontWeight.Black)
                                                }
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text("Existing EMI", color = textSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.5.sp)
                                                    Spacer(modifier = Modifier.height(8.dp))
                                                    Text(formatMoney(animatedTotalExistingEmi.toDouble()), color = warningYellow, fontSize = 18.sp, fontWeight = FontWeight.Black)
                                                }
                                            }
                                        }
                                    }
                                }

}
