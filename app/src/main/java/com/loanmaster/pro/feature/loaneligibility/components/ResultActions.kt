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

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        
        // Compare Banks Destination Card
        Card(
            modifier = Modifier.fillMaxWidth().clickable { onCompareClick() }.height(76.dp),
            colors = CardDefaults.cardColors(containerColor = surfaceColor.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
        ) {
            Row(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(44.dp).clip(CircleShape).background(brightBlue.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.AccountBalance, contentDescription = null, tint = brightBlue, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Compare Banks", color = textColor, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Text("Compare offers from multiple lenders", color = textSecondary, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = textSecondary)
            }
        }

        // Secondary Actions Grid - Compact action cards
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
        modifier = modifier.clickable { onClick() }.height(72.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark.copy(alpha = 0.3f)),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(text, color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
        }
    }
}
