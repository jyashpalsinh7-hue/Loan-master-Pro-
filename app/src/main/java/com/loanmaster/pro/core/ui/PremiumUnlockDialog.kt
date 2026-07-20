package com.loanmaster.pro.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material.icons.rounded.WorkspacePremium
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.loanmaster.pro.core.theme.AccentGreen
import com.loanmaster.pro.core.theme.BackgroundDark
import com.loanmaster.pro.core.theme.CardStroke
import com.loanmaster.pro.core.theme.LoanMasterTheme
import com.loanmaster.pro.core.theme.SurfaceDark
import com.loanmaster.pro.core.theme.TextSecondary

@Composable
fun PremiumUnlockDialog(
    onDismiss: () -> Unit,
    onUnlockSuccessful: () -> Unit
) {
    val context = LocalContext.current
    var isAdLoading by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .clip(RoundedCornerShape(LoanMasterTheme.spacing.lg))
                .background(BackgroundDark)
                .border(1.dp, CardStroke, RoundedCornerShape(LoanMasterTheme.spacing.lg))
                .padding(LoanMasterTheme.spacing.lg)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.md)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Unlock Premium",
                        color = Color.White,
                        fontSize = LoanMasterTheme.typography.title.fontSize,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Rounded.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }
                
                Text(
                    text = "Get access to advanced insights, schedules, and more features.",
                    color = TextSecondary,
                    fontSize = LoanMasterTheme.typography.body.fontSize,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = LoanMasterTheme.spacing.sm)
                )

                // Option 1: Buy Premium
                Button(
                    onClick = {
                        onUnlockSuccessful()
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentGreen,
                        contentColor = BackgroundDark
                    ),
                    shape = RoundedCornerShape(LoanMasterTheme.spacing.md)
                ) {
                    Icon(Icons.Rounded.WorkspacePremium, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.widthIn(min = 8.dp))
                    Text("Buy Premium", fontWeight = FontWeight.Bold)
                }

                Text(
                    text = "OR",
                    color = TextSecondary,
                    fontSize = LoanMasterTheme.typography.label.fontSize,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = LoanMasterTheme.spacing.xs)
                )

                // Option 2: Watch Ad
                Button(
                    onClick = {
                        val activity = context as? android.app.Activity
                        if (activity != null) {
                            isAdLoading = true
                            com.loanmaster.pro.core.ads.RewardedAdManager.showAd(activity) {
                                isAdLoading = false
                                onUnlockSuccessful()
                                onDismiss()
                            }
                        }
                    },
                    enabled = !isAdLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SurfaceDark,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(LoanMasterTheme.spacing.md)
                ) {
                    if (isAdLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = AccentGreen, strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Rounded.PlayCircle, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.widthIn(min = 8.dp))
                        Text("Watch Ad to Unlock", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
