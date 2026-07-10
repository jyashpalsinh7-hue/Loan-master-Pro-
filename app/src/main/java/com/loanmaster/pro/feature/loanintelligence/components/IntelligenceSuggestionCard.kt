package com.loanmaster.pro.feature.loanintelligence.components
import com.loanmaster.pro.core.theme.AccentGreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.loanmaster.pro.feature.loanintelligence.model.IntelligenceSuggestion
import com.loanmaster.pro.core.theme.SurfaceDark
import com.loanmaster.pro.core.theme.TextPrimary
import com.loanmaster.pro.core.theme.TextSecondary

@Composable
fun IntelligenceSuggestionCard(suggestion: IntelligenceSuggestion) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(40.dp).clip(CircleShape).background(suggestion.color.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = suggestion.icon, contentDescription = null, tint = suggestion.color, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = suggestion.title, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = suggestion.description, color = TextSecondary, fontSize = 12.sp, lineHeight = 16.sp)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Impact metrics
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MetricBox(label = "Approval", value = suggestion.estimatedApprovalIncrease, modifier = Modifier.weight(1f))
                MetricBox(label = "Amount", value = suggestion.estimatedImpact, modifier = Modifier.weight(1f))
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Badges
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                BadgeLabel(label = "Difficulty", value = suggestion.difficulty)
                BadgeLabel(label = "Confidence", value = suggestion.confidence)
                val prio = when(suggestion.priority) { 1 -> "High"; 2 -> "Medium"; else -> "Low" }
                BadgeLabel(label = "Priority", value = prio)
            }
        }
    }
}

@Composable
fun MetricBox(label: String, value: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.background(Color.White.copy(alpha = 0.03f), RoundedCornerShape(8.dp)).padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, color = AccentGreen, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(label, color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun BadgeLabel(label: String, value: String) {
    val color = when(value) {
        "High", "Hard" -> Color(0xFFF44336)
        "Medium" -> Color(0xFFFBBF24)
        "Low", "Easy" -> AccentGreen
        else -> TextSecondary
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("$label:", color = TextSecondary, fontSize = 10.sp)
        Spacer(modifier = Modifier.width(4.dp))
        Text(value, color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}
