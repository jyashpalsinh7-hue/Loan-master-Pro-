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
fun LoadingState(
    isCalculating: Boolean
) {
    val surfaceColor = SurfaceDark
    val brightBlue = AccentBlue
    val textSecondary = TextSecondary
    val textColor = TextPrimary

                    AnimatedVisibility(
                        visible = isCalculating,
                        enter = fadeIn(animationSpec = tween(400)),
                        exit = fadeOut(animationSpec = tween(400))
                    ) {
                        val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
                        val shimmerAlpha by infiniteTransition.animateFloat(
                            initialValue = 0.2f,
                            targetValue = 0.6f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(800, easing = LinearEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "shimmerAlpha"
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(24.dp), modifier = Modifier.fillMaxWidth()) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = surfaceColor.copy(alpha = 0.4f)),
                                shape = RoundedCornerShape(24.dp),
                                modifier = Modifier.fillMaxWidth().height(LoanMasterTheme.components.chartHeight)
                            ) {
                                Box(modifier = Modifier.fillMaxSize().background(Color.White.copy(alpha = shimmerAlpha * 0.1f)))
                            }
                            Card(
                                colors = CardDefaults.cardColors(containerColor = surfaceColor.copy(alpha = 0.25f)),
                                shape = RoundedCornerShape(24.dp),
                                modifier = Modifier.fillMaxWidth().height(LoanMasterTheme.components.bannerHeight)
                            ) {
                                Box(modifier = Modifier.fillMaxSize().background(Color.White.copy(alpha = shimmerAlpha * 0.1f)))
                            }
                        }
                    }
                }


