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


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SummaryCard(
    isInputExpanded: Boolean,
    isResultVisible: Boolean,
    selectedLoanProfile: String,
    tenureYears: String,
    interestRate: String,
    totalIncome: Double,
    totalExistingEmi: Double,
    onEditClick: () -> Unit
) {
    val surfaceColor = SurfaceDark
    val brightBlue = AccentBlue
    val textSecondary = TextSecondary
    val textColor = TextPrimary

                    AnimatedVisibility(
                        visible = !isInputExpanded && isResultVisible,
                        enter = expandVertically(animationSpec = tween(300)) + fadeIn(animationSpec = tween(300)),
                        exit = shrinkVertically(animationSpec = tween(300)) + fadeOut(animationSpec = tween(300))
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = surfaceColor.copy(alpha = 0.25f)),
                            shape = RoundedCornerShape(24.dp),
                            border = BorderStroke(1.dp, brightBlue.copy(alpha = 0.3f)),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier.fillMaxWidth().clickable { onEditClick() }
                        ) {
                            Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                                        
                                        // Loan Type Chip
                                        Surface(color = surfaceColor.copy(alpha = 0.8f), shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, surfaceColor)) {
                                            Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Rounded.Home, contentDescription = null, tint = textSecondary, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(selectedLoanProfile, color = textColor, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                            }
                                        }
                                        
                                        // Tenure Chip
                                        Surface(color = surfaceColor.copy(alpha = 0.8f), shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, surfaceColor)) {
                                            Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Rounded.Event, contentDescription = null, tint = textSecondary, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("$tenureYears Yrs", color = textColor, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                            }
                                        }
                                        
                                        // Interest Rate Chip
                                        Surface(color = surfaceColor.copy(alpha = 0.8f), shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, surfaceColor)) {
                                            Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Rounded.TrendingUp, contentDescription = null, tint = textSecondary, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("$interestRate%", color = textColor, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                            }
                                        }
                                    }
                                    Surface(
                                        color = brightBlue.copy(alpha = 0.1f),
                                        shape = RoundedCornerShape(12.dp),
                                        border = BorderStroke(1.dp, brightBlue.copy(alpha = 0.3f))
                                    ) {
                                        Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Rounded.Edit, contentDescription = "Edit", tint = brightBlue, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Edit", color = brightBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Income", color = textSecondary, fontSize = 12.sp, fontWeight = FontWeight.Normal)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(formatMoney(totalIncome), color = textColor, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                                        Text("Existing EMI", color = textSecondary, fontSize = 12.sp, fontWeight = FontWeight.Normal)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(formatMoney(totalExistingEmi), color = textColor, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

}
