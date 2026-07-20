package com.loanmaster.pro.feature.loanintelligence.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.loanmaster.pro.core.theme.*

@Composable
fun PremiumLockCard(
    isLoading: Boolean,
    onWatchAdClick: () -> Unit,
    onPremiumClick: () -> Unit
) {
    val gradientBrush = Brush.linearGradient(
        colors = listOf(AccentBlue.copy(alpha = 0.15f), Color.Transparent)
    )
    
    Card(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).border(1.dp, Color.White.copy(alpha=0.05f), RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark.copy(alpha = 0.6f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().background(gradientBrush).padding(LoanMasterTheme.spacing.md)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(36.dp).clip(CircleShape).background(AccentBlue.copy(alpha=0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.Lock, contentDescription = "Locked", tint = AccentBlue, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.widthIn(min = 12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Unlock Loan Intelligence", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text("Personalized recommendations to improve approval.", color = TextSecondary, fontSize = 11.sp)
                }
            }
            Spacer(modifier = Modifier.height(LoanMasterTheme.spacing.lg))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = onWatchAdClick,
                    modifier = Modifier.weight(1f).heightIn(min = 40.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceDark.copy(alpha=0.8f), contentColor = TextPrimary),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha=0.05f)),
                    enabled = !isLoading,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = AccentBlue, strokeWidth = 2.dp)
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.PlayCircleOutline, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.widthIn(min = 6.dp))
                            Text("Watch Ad", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        }
                    }
                }
                
                Box(modifier = Modifier.weight(1f)) {
                    Button(
                        onClick = onPremiumClick,
                        modifier = Modifier.fillMaxWidth().heightIn(min = 40.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentBlue.copy(alpha=0.15f), contentColor = AccentBlue),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Star, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.widthIn(min = 6.dp))
                            Text("Go Premium", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 6.dp, y = (-6).dp)
                            .background(AccentBlue, RoundedCornerShape(4.dp))
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text("PRO", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
        }
    }
}
