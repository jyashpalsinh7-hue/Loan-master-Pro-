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
fun CalculateButton(
    isInputExpanded: Boolean,
    isCalculating: Boolean,
    onCalculateClick: () -> Unit
) {
    val surfaceColor = SurfaceDark
    val brightBlue = AccentBlue
    val textSecondary = TextSecondary
    val textColor = TextPrimary
    val coroutineScope = rememberCoroutineScope()

                    
                    
                    val buttonInteractionSource = remember { MutableInteractionSource() }
                    val isPressed by buttonInteractionSource.collectIsPressedAsState()
                    val btnScale by animateFloatAsState(targetValue = if (isPressed) 0.97f else 1f, animationSpec = tween(150), label = "")
                    val elevation by animateDpAsState(if (isPressed) 2.dp else 8.dp, label = "")
                    val buttonGradient = Brush.linearGradient(listOf(brightBlue, HighlightBlue))
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 56.dp)
                            .graphicsLayer { scaleX = btnScale; scaleY = btnScale }
                            .shadow(elevation, RoundedCornerShape(24.dp), spotColor = brightBlue)
                            .clip(RoundedCornerShape(24.dp))
                            .background(buttonGradient)
                            .clickable(interactionSource = buttonInteractionSource, indication = LocalIndication.current) {
onCalculateClick()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isCalculating) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(if (isInputExpanded) Icons.Rounded.Calculate else Icons.Rounded.Update, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.widthIn(min = 8.dp))
                                Text(
                                    text = if (isInputExpanded) "Calculate Eligibility" else "Update Calculation",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    
                    
                }


