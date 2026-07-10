package com.loanmaster.pro.feature.loanintelligence.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.style.TextAlign
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
        colors = listOf(Color(0xFF6C63FF).copy(alpha = 0.2f), Color(0xFF03DAC5).copy(alpha = 0.1f))
    )
    
    Card(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).border(1.dp, Color.White.copy(alpha=0.1f), RoundedCornerShape(24.dp)),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().background(gradientBrush).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Rounded.Lock,
                contentDescription = "Locked",
                tint = Color(0xFFFFD700),
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Unlock Loan Intelligence",
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Get AI-powered recommendations to maximize your loan approval and loan amount.",
                color = TextSecondary,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = onWatchAdClick,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SurfaceDark.copy(alpha=0.5f), contentColor = TextPrimary),
                border = borderStroke(),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = AccentBlue)
                } else {
                    Icon(Icons.Rounded.PlayCircleOutline, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Watch Ad & Unlock", fontWeight = FontWeight.Bold)
                        Text("For this calculation only", fontSize = 10.sp, color = TextSecondary)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = onPremiumClick,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700).copy(alpha=0.2f), contentColor = Color(0xFFFFD700))
            ) {
                Icon(Icons.Rounded.Star, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Go Premium", fontWeight = FontWeight.Bold)
                    Text("Unlimited Loan Intelligence forever", fontSize = 10.sp, color = Color(0xFFFFD700).copy(alpha=0.7f))
                }
            }
        }
    }
}

@Composable
private fun borderStroke() = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha=0.1f))
