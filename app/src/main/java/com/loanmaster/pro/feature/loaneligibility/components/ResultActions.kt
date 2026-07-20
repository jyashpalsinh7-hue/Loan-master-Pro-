package com.loanmaster.pro.feature.loaneligibility.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.loanmaster.pro.core.theme.*

@Composable
fun ResultActions(
    onCompareClick: () -> Unit,
    onDetailedReportClick: () -> Unit,
    onSaveCalcClick: () -> Unit,
    onExportPdfClick: () -> Unit
) {
    val surfaceColor = SurfaceDark
    val brightBlue = AccentBlue
    val textSecondary = TextSecondary
    val textColor = TextPrimary

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        
        // Compare Banks Destination Card (60dp)
        Card(
            modifier = Modifier.fillMaxWidth().height(60.dp).clickable { onCompareClick() },
            colors = CardDefaults.cardColors(containerColor = surfaceColor.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
        ) {
            Row(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(40.dp).clip(CircleShape).background(brightBlue.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.AccountBalance, contentDescription = null, tint = brightBlue, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
                    Text("Compare Banks", color = textColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("Compare offers from multiple lenders", color = textSecondary.copy(alpha = 0.7f), fontSize = 11.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = textSecondary, modifier = Modifier.size(18.dp))
            }
        }

        // Secondary Actions Grid - Compact action cards (68dp)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ActionCard(
                text = "Report",
                icon = Icons.Rounded.Description,
                modifier = Modifier.weight(1f),
                onClick = onDetailedReportClick
            )
            ActionCard(
                text = "Save",
                icon = Icons.Rounded.BookmarkBorder,
                modifier = Modifier.weight(1f),
                onClick = onSaveCalcClick
            )
            ActionCard(
                text = "Export PDF",
                icon = Icons.Rounded.PictureAsPdf,
                modifier = Modifier.weight(1f),
                onClick = onExportPdfClick
            )
        }
    }
}

@Composable
fun ActionCard(text: String, icon: ImageVector, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier.height(68.dp).clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(text, color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Medium)
        }
    }
}
