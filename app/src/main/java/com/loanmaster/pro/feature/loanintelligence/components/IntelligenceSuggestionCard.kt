package com.loanmaster.pro.feature.loanintelligence.components

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
import com.loanmaster.pro.core.theme.AccentGreen
import com.loanmaster.pro.core.theme.SurfaceDark
import com.loanmaster.pro.core.theme.TextPrimary
import com.loanmaster.pro.core.theme.TextSecondary
import com.loanmaster.pro.feature.loanintelligence.model.IntelligenceSuggestion

@Composable
fun IntelligenceSuggestionCard(suggestion: IntelligenceSuggestion) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Box(
                    modifier = Modifier.size(36.dp).clip(CircleShape).background(suggestion.color.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = suggestion.icon, contentDescription = null, tint = suggestion.color, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = suggestion.title, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = suggestion.description, color = TextSecondary, fontSize = 13.sp, lineHeight = 18.sp)
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Impact Chip
            Box(
                modifier = Modifier.background(suggestion.color.copy(alpha = 0.1f), RoundedCornerShape(8.dp)).padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Column(horizontalAlignment = Alignment.Start) {
                    Text("Impact", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(suggestion.estimatedImpact, color = suggestion.color, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Badges
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                BadgeLabel(label = "Difficulty", value = getDifficultyDots(suggestion.difficulty))
                BadgeLabel(label = "Confidence", value = suggestion.confidence)
                val prio = when(suggestion.priority) { 1 -> "High"; 2 -> "Medium"; else -> "Low" }
                BadgeLabel(label = "Priority", value = prio)
            }
        }
    }
}

fun getDifficultyDots(difficulty: String): String {
    return when (difficulty) {
        "Easy" -> "●○○"
        "Medium" -> "●●○"
        "Hard" -> "●●●"
        else -> "●●○"
    }
}

@Composable
fun BadgeLabel(label: String, value: String) {
    val color = when(value) {
        "High", "●●●", "Hard" -> Color(0xFFF44336)
        "Medium", "●●○" -> Color(0xFFFBBF24)
        "Low", "●○○", "Easy" -> AccentGreen
        else -> TextPrimary
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("$label", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.width(4.dp))
        Text(value, color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}
