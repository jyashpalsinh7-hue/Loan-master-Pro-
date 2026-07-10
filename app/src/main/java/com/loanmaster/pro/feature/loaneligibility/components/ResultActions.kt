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


                                Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                                // Primary Action
                                val buttonGradient = Brush.linearGradient(listOf(brightBlue, HighlightBlue))
                                val compareInteractionSource = remember { MutableInteractionSource() }
                                val comparePressed by compareInteractionSource.collectIsPressedAsState()
                                val compareScale by animateFloatAsState(targetValue = if (comparePressed) 0.97f else 1f, animationSpec = tween(150), label = "")
                                val compareElevation by animateDpAsState(if (comparePressed) 4.dp else 16.dp, label = "")
                                
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(68.dp)
                                        .graphicsLayer {
                                            scaleX = compareScale
                                            scaleY = compareScale
                                        }
                                        .shadow(compareElevation, RoundedCornerShape(24.dp), spotColor = brightBlue.copy(alpha = 0.7f), ambientColor = brightBlue.copy(alpha = 0.3f))
                                        .clip(RoundedCornerShape(24.dp))
                                        .background(buttonGradient)
                                        .clickable(interactionSource = compareInteractionSource, indication = LocalIndication.current) { },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Rounded.AccountBalance, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text("Compare Banks", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 0.5.sp)
                                    }
                                }
                                
                                // Secondary Actions Grid
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                    ActionButton(
                                        text = "Detailed Report",
                                        icon = Icons.Rounded.Description,
                                        color = surfaceColor.copy(alpha = 0.4f),
                                        contentColor = brightBlue,
                                        modifier = Modifier.weight(1f),
                                        onClick = {}
                                    )
                                    ActionButton(
                                        text = "Save Calc",
                                        icon = Icons.Rounded.Bookmark,
                                        color = surfaceColor.copy(alpha = 0.4f),
                                        contentColor = textColor,
                                        modifier = Modifier.weight(1f),
                                        onClick = {}
                                    )
                                    ActionButton(
                                        text = "Export PDF",
                                        icon = Icons.Rounded.PictureAsPdf,
                                        color = surfaceColor.copy(alpha = 0.4f),
                                        contentColor = textColor,
                                        modifier = Modifier.weight(1f),
                                        onClick = {}
                                    )

}

}
}