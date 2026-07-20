package com.loanmaster.pro.feature.loaneligibility.components

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.loanmaster.pro.core.theme.*
import com.loanmaster.pro.core.formatter.formatMoney

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
    val textColor = TextPrimary
    val textSecondary = TextSecondary

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Card(
            colors = CardDefaults.cardColors(containerColor = surfaceColor.copy(alpha = 0.3f)),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(LoanMasterTheme.spacing.md)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.MonetizationOn, contentDescription = null, tint = textSecondary, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.widthIn(min = 4.dp))
                        Text("Eligible Loan Amount", color = textSecondary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
                    
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Updated just now", color = textSecondary.copy(alpha = 0.6f), fontSize = 10.sp)
                        Surface(
                            color = stateColor.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(50),
                        ) {
                            Row(modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                                val icon = when(riskState) {
                                    "Safe" -> Icons.Rounded.VerifiedUser
                                    "Critical" -> Icons.Rounded.Error
                                    else -> Icons.Rounded.Warning
                                }
                                Icon(icon, contentDescription = null, tint = stateColor, modifier = Modifier.size(10.dp))
                                Spacer(modifier = Modifier.widthIn(min = 4.dp))
                                AnimatedContent(targetState = riskState, label = "badgeState") { state ->
                                    Text(state, color = stateColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.heightIn(min = 8.dp))
                
                Text(
                    text = formatMoney(animatedAmount.toDouble()),
                    color = stateColor,
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.heightIn(min = 8.dp))
                HorizontalDivider(color = Color.White.copy(alpha = 0.05f), thickness = 1.dp)
                Spacer(modifier = Modifier.heightIn(min = 8.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.AccountBalanceWallet, contentDescription = null, tint = textSecondary, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.widthIn(min = 4.dp))
                            Text("Monthly EMI Capacity", color = textSecondary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                        }
                        Spacer(modifier = Modifier.heightIn(min = 4.dp))
                        Text(formatMoney(animatedAvailableEmi.toDouble()), color = textColor, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Savings, contentDescription = null, tint = textSecondary, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.widthIn(min = 4.dp))
                            Text("Max Affordable EMI", color = textSecondary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                        }
                        Spacer(modifier = Modifier.heightIn(min = 4.dp))
                        Text(formatMoney(animatedMaxAllowedEmi.toDouble()), color = textColor, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
                
                Spacer(modifier = Modifier.heightIn(min = 8.dp))
                
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Payments, contentDescription = null, tint = textSecondary, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.widthIn(min = 4.dp))
                            Text("Income", color = textSecondary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                        }
                        Spacer(modifier = Modifier.heightIn(min = 4.dp))
                        Text(formatMoney(animatedTotalIncome.toDouble()), color = textColor, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.CreditCard, contentDescription = null, tint = textSecondary, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.widthIn(min = 4.dp))
                            Text("Existing EMI", color = textSecondary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                        }
                        Spacer(modifier = Modifier.heightIn(min = 4.dp))
                        Text(formatMoney(animatedTotalExistingEmi.toDouble()), color = textColor, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}
