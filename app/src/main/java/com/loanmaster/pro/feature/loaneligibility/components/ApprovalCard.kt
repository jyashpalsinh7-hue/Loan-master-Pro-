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
fun ApprovalCard(
    approvalProb: Float,
    currentFoir: Double,
    conf: String,
    gradeRaw: String,
    gradeColor: Color,
    stateColor: Color,
    bgColor: Color
) {
    val surfaceColor = SurfaceDark
    val brightBlue = AccentBlue
    val neonGreen = AccentGreen
    val warningYellow = Color(0xFFFBBF24)
    val dangerRed = Color(0xFFF44336)
    val textColor = TextPrimary
    val textSecondary = TextSecondary

                                Card(
                                    colors = CardDefaults.cardColors(containerColor = surfaceColor.copy(alpha = 0.25f)),
                                    shape = RoundedCornerShape(24.dp),
                                    border = BorderStroke(1.dp, brightBlue.copy(alpha = 0.3f)),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Left: Gauge
                                        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(104.dp)) {
                                            CircularProgressIndicator(
                                                progress = { 1f },
                                                modifier = Modifier.size(104.dp),
                                                color = bgColor,
                                                strokeWidth = 12.dp,
                                                strokeCap = StrokeCap.Round
                                            )
                                            CircularProgressIndicator(
                                                progress = { approvalProb },
                                                modifier = Modifier.size(104.dp),
                                                color = stateColor,
                                                strokeWidth = 12.dp,
                                                strokeCap = StrokeCap.Round
                                            )
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text("${(approvalProb * 100).toInt()}%", color = textColor, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                                                Text("Approval", color = textSecondary, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(24.dp))
                                        
                                        // Right: Details
                                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                                Text("FOIR", color = textSecondary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                                val foirColor = if (currentFoir < 0.4) neonGreen else if (currentFoir < 0.6) warningYellow else dangerRed
                                                Text("${(currentFoir * 100).toInt()}%", color = foirColor, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                            }
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                                Text("Bank Confidence", color = textSecondary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                                Text(conf, color = stateColor, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                            }
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                                Text("Credit Grade", color = textSecondary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                                Text(gradeRaw, color = gradeColor, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                                            }
                                        }
                                    }

}

}