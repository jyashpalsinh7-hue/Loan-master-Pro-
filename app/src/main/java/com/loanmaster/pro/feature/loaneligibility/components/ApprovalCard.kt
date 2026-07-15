package com.loanmaster.pro.feature.loaneligibility.components
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*


import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import com.loanmaster.pro.core.theme.*

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
    val neonGreen = AccentGreen
    val warningYellow = Color(0xFFFBBF24)
    val dangerRed = Color(0xFFF44336)
    val textColor = TextPrimary
    val textSecondary = TextSecondary

    var animationPlayed by rememberSaveable { mutableStateOf(false) }
    val animatedApproval by animateFloatAsState(
        targetValue = if (animationPlayed) approvalProb else 0f,
        animationSpec = tween(1500, easing = FastOutSlowInEasing),
        label = "ApprovalAnim"
    )

    LaunchedEffect(Unit) {
        animationPlayed = true
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = surfaceColor.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(LoanMasterTheme.spacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Gauge (reduced to 42dp)
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(42.dp)) {
                CircularProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier.size(42.dp),
                    color = bgColor,
                    strokeWidth = 3.5.dp,
                    strokeCap = StrokeCap.Round
                )
                CircularProgressIndicator(
                    progress = { animatedApproval },
                    modifier = Modifier.size(42.dp),
                    color = stateColor,
                    strokeWidth = 3.5.dp,
                    strokeCap = StrokeCap.Round
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("${(animatedApproval * 100).toInt()}%", color = textColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.width(20.dp))
            
            // Right: Details (Perfectly aligned)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.DataUsage, contentDescription = null, tint = textSecondary, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("FOIR", color = textSecondary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
                    val foirColor = if (currentFoir < 0.4) neonGreen else if (currentFoir < 0.6) warningYellow else dangerRed
                    Text("${(currentFoir * 100).toInt()}%", color = foirColor, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Insights, contentDescription = null, tint = textSecondary, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Confidence", color = textSecondary, fontSize = 12.sp, fontWeight = FontWeight.Medium, minLines = 1, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    }
                    Text(conf, color = stateColor, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Grade, contentDescription = null, tint = textSecondary, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Credit Grade", color = textSecondary, fontSize = 12.sp, fontWeight = FontWeight.Medium, minLines = 1, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    }
                    Text(gradeRaw, color = gradeColor, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}
