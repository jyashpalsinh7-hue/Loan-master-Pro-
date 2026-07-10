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
import com.loanmaster.pro.core.responsive.*
import com.loanmaster.pro.feature.loaneligibility.util.*
import com.loanmaster.pro.domain.model.LoanProfile


@Composable
fun SummaryCard(
    isInputExpanded: Boolean,
    isResultVisible: Boolean,
    selectedLoanProfile: String,
    tenureYears: String,
    interestRate: String,
    totalIncome: Double,
    totalExistingEmi: Double,
    onEditClick: () -> Unit
) {
    val surfaceColor = SurfaceDark
    val brightBlue = AccentBlue
    val textSecondary = TextSecondary
    val textColor = TextPrimary

    AnimatedVisibility(
        visible = !isInputExpanded && isResultVisible,
        enter = expandVertically(animationSpec = tween(300)) + fadeIn(animationSpec = tween(300)),
        exit = shrinkVertically(animationSpec = tween(300)) + fadeOut(animationSpec = tween(300))
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = surfaceColor.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
            modifier = Modifier.fillMaxWidth().heightIn(max = 120.dp).clickable { onEditClick() }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(selectedLoanProfile, color = textColor, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Text(" • ", color = textSecondary)
                        Text("$tenureYears Yrs", color = textSecondary, fontSize = 12.sp)
                        Text(" • ", color = textSecondary)
                        Text("$interestRate%", color = textSecondary, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Income: ", color = textSecondary, fontSize = 12.sp)
                        Text(formatMoney(totalIncome), color = textColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("EMI: ", color = textSecondary, fontSize = 12.sp)
                        Text(formatMoney(totalExistingEmi), color = textColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
                
                Surface(
                    color = brightBlue.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(50),
                ) {
                    Box(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), contentAlignment = Alignment.Center) {
                        Text("Edit", color = brightBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
