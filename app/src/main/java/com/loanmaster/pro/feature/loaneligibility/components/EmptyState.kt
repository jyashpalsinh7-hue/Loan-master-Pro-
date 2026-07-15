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
fun EmptyState(
    isResultVisible: Boolean,
    isCalculating: Boolean,
    isInputExpanded: Boolean
) {
    val surfaceColor = SurfaceDark
    val brightBlue = AccentBlue
    val textSecondary = TextSecondary
    val textColor = TextPrimary

                    AnimatedVisibility(
                        visible = !isResultVisible && !isCalculating && isInputExpanded,
                        enter = fadeIn(animationSpec = tween(500)),
                        exit = fadeOut(animationSpec = tween(300))
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(top = 24.dp, bottom = 32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier.size(88.dp).background(brightBlue.copy(alpha = 0.1f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Rounded.AccountBalance, contentDescription = null, tint = brightBlue, modifier = Modifier.size(44.dp))
                            }
                            Spacer(modifier = Modifier.height(LoanMasterTheme.spacing.lg))
                            Text("Check Your Loan Eligibility", color = textColor, fontSize = 22.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                            Spacer(modifier = Modifier.height(LoanMasterTheme.spacing.md))
                            Text("Enter your income and loan details to calculate the maximum amount you can borrow instantly.", color = textSecondary, fontSize = 14.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 24.dp), fontWeight = FontWeight.Normal)
                        }
                    }
                }


