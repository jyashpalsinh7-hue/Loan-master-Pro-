package com.loanmaster.pro.feature.loanintelligence.components
import androidx.compose.animation.animateContentSize

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.loanmaster.pro.core.theme.*
import com.loanmaster.pro.feature.loanintelligence.model.LoanIntelligenceState

@Composable
fun LoanIntelligenceCard(
    state: LoanIntelligenceState,
    onWatchAdClick: () -> Unit,
    onPremiumClick: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth().animateContentSize()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF6C63FF).copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.Lightbulb, contentDescription = null, tint = Color(0xFF6C63FF))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    "Loan Intelligence",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Personalized insights",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            }
        }

        AnimatedContent(
            targetState = state.isUnlocked,
            transitionSpec = {
                fadeIn(animationSpec = tween(500)) togetherWith fadeOut(animationSpec = tween(500))
            },
            label = "IntelligenceUnlockAnim"
        ) { isUnlocked ->
            if (!isUnlocked) {
                PremiumLockCard(
                    isLoading = state.isLoading,
                    onWatchAdClick = onWatchAdClick,
                    onPremiumClick = onPremiumClick
                )
            } else {
                if (state.suggestions.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = AccentGreen.copy(alpha=0.1f)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = AccentGreen, modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text("Everything looks excellent!", color = AccentGreen, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text("You're already near the maximum loan eligibility.", color = TextPrimary.copy(alpha=0.8f), fontSize = 13.sp)
                            }
                        }
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        if (isExpanded) {
                            state.suggestions.forEach { suggestion ->
                                IntelligenceSuggestionCard(suggestion = suggestion)
                            }
                        } else {
                            IntelligenceSuggestionCard(suggestion = state.suggestions.first())
                        }
                        
                        if (state.suggestions.size > 1) {
                            TextButton(
                                onClick = { isExpanded = !isExpanded },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    if (isExpanded) "Show Less" else "View all insights (${state.suggestions.size})",
                                    color = AccentBlue,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
