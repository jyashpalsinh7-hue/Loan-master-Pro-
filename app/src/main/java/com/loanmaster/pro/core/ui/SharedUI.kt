package com.loanmaster.pro.core.ui

import com.loanmaster.pro.domain.model.*
import com.loanmaster.pro.feature.gst.*
import com.loanmaster.pro.feature.sip.*
import com.loanmaster.pro.feature.history.*
import com.loanmaster.pro.core.theme.*
import com.loanmaster.pro.data.datastore.*
import com.loanmaster.pro.feature.settings.*
import com.loanmaster.pro.feature.rd.*
import com.loanmaster.pro.domain.calculator.*
import com.loanmaster.pro.data.local.entity.*
import com.loanmaster.pro.core.utils.*
import com.loanmaster.pro.data.local.dao.*
import com.loanmaster.pro.data.local.room.*
import com.loanmaster.pro.feature.emi.*
import com.loanmaster.pro.feature.loansummary.*
import com.loanmaster.pro.feature.prepayment.*
import com.loanmaster.pro.core.formatter.*
import com.loanmaster.pro.feature.fd.*
import com.loanmaster.pro.data.repository.*
import com.loanmaster.pro.feature.currency.*
import com.loanmaster.pro.core.navigation.*
import com.loanmaster.pro.feature.compare.*
import com.loanmaster.pro.feature.loaneligibility.*
import com.loanmaster.pro.core.responsive.*
import com.loanmaster.pro.feature.home.*

import androidx.window.core.layout.WindowWidthSizeClass


import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.*
import androidx.compose.material.icons.automirrored.rounded.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.heightIn



private 



@Composable
fun AutoSizeText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    minTextSize: TextUnit = LoanMasterTheme.typography.label.fontSize,
    maxTextSize: TextUnit = androidx.compose.ui.unit.TextUnit.Unspecified,
    fontWeight: FontWeight? = null,
    maxLines: Int = 1,
    style: TextStyle = TextStyle.Default
) {
    val actualMaxTextSize = if (maxTextSize == androidx.compose.ui.unit.TextUnit.Unspecified) LoanMasterTheme.typography.body.fontSize else maxTextSize
    var textStyle by remember(text, actualMaxTextSize, fontWeight, color, style) { mutableStateOf(style.copy(fontSize = actualMaxTextSize, fontWeight = fontWeight, color = color)) }
    var readyToDraw by remember(text) { mutableStateOf(false) }

    Text(
        text = text,
        modifier = modifier.drawWithContent {
            if (readyToDraw) drawContent()
        },
        style = textStyle,
        color = color,
        maxLines = maxLines,
        softWrap = false,
        overflow = TextOverflow.Visible,
        onTextLayout = { textLayoutResult ->
            if (!readyToDraw) {
                if (textLayoutResult.hasVisualOverflow && textStyle.fontSize > minTextSize) {
                    val nextSize = (textStyle.fontSize.value * 0.9f).sp
                    if (nextSize >= minTextSize) {
                        textStyle = textStyle.copy(fontSize = nextSize)
                    } else {
                        textStyle = textStyle.copy(fontSize = minTextSize)
                        readyToDraw = true
                    }
                } else {
                    readyToDraw = true
                }
            }
        }
    )
}

@Composable
fun AutoResizedText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = androidx.compose.ui.unit.TextUnit.Unspecified,
    fontWeight: FontWeight? = null,
    maxLines: Int = 1,
    style: TextStyle = TextStyle.Default
) {
    AutoSizeText(
        text = text,
        modifier = modifier,
        color = color,
        minTextSize = LoanMasterTheme.typography.label.fontSize,
        maxTextSize = fontSize,
        fontWeight = fontWeight,
        maxLines = maxLines,
        style = style
    )
}

@Composable
fun ScrollingTitleText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = androidx.compose.ui.unit.TextUnit.Unspecified,
    fontWeight: FontWeight? = null,
) {
    AutoResizedText(
        text = text,
        modifier = modifier,
        color = color,
        fontSize = fontSize,
        fontWeight = fontWeight,
        maxLines = 1
    )
}

@Composable
fun PremiumInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    trailingIcon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    suffix: String = "",    readOnly: Boolean = false,
    onClick: (() -> Unit)? = null,
    infoText: String? = null,
    errorMessage: String? = null,
    isNumeric: Boolean = true,
    modifier: Modifier = Modifier
) {
    var showInfoDialog by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf(false) }
    val colors = androidx.compose.material3.MaterialTheme.colorScheme

    if (showInfoDialog && infoText != null) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showInfoDialog = false },
            title = { Text(label, color = colors.onSurface) },
            text = { Text(infoText, color = colors.onSurfaceVariant) },
            confirmButton = {
                androidx.compose.material3.TextButton(onClick = { showInfoDialog = false }) {
                    Text("Got it", color = colors.primary)
                }
            },
            containerColor = colors.surface
        )
    }

    val internalError = remember(value) {
        if (isNumeric && value.isNotEmpty() && value != "." && value.toDoubleOrNull() == null) {
            "Invalid numeric entry"
        } else {
            null
        }
    }
    val finalErrorMessage = errorMessage ?: internalError

    val handleValueChange: (String) -> Unit = remember(onValueChange) {
        { newValue ->
            val sanitized = newValue.replace("-", "").replace(",", "")
            onValueChange(sanitized)
        }
    }

    var isFocused by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf(false) }

    val hasError = finalErrorMessage != null
    val targetStrokeColor = if (hasError) colors.error else if (isFocused) colors.primary else colors.outlineVariant
    val textColor = if (hasError) colors.error else colors.onSurface
    val targetIconColor = if (hasError) colors.error else if (isFocused) colors.primary else iconTint
    val cursorColor = if (hasError) colors.error else colors.primary
    val targetBorderWidth = if (isFocused) 2.dp else 1.dp

    val strokeColor by androidx.compose.animation.animateColorAsState(targetValue = targetStrokeColor, label = "strokeColor")
    val iconColor by androidx.compose.animation.animateColorAsState(targetValue = targetIconColor, label = "iconColor")
    val borderWidth by androidx.compose.animation.core.animateDpAsState(targetValue = targetBorderWidth, label = "borderWidth")

    androidx.compose.foundation.layout.Column(modifier = modifier) {
        androidx.compose.foundation.layout.Row(verticalAlignment = Alignment.CenterVertically) {
            Text(label, color = if (isFocused) colors.primary else colors.onSurfaceVariant, fontSize = LoanMasterTheme.typography.body.fontSize.value.sp * 0.85f)
            if (infoText != null) {
                androidx.compose.foundation.layout.Spacer(Modifier.widthIn(min = LoanMasterTheme.spacing.xs))
                androidx.compose.material3.Icon(
                    imageVector = Icons.Rounded.Info,
                    contentDescription = "Info about $label",
                    tint = colors.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier
                        .size(LoanMasterTheme.spacing.md)
                        .clickable(
                            indication = null,
                            interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                        ) { showInfoDialog = true }
                )
            }
        }
        androidx.compose.foundation.layout.Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.sm))
        androidx.compose.material3.Surface(
            shape = RoundedCornerShape(LoanMasterTheme.spacing.md),
            color = colors.surface,
            border = androidx.compose.foundation.BorderStroke(borderWidth, strokeColor),
            modifier = Modifier.fillMaxWidth()
        ) {
            androidx.compose.foundation.layout.Box(modifier = Modifier.fillMaxWidth()) {
                androidx.compose.foundation.layout.Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = LoanMasterTheme.spacing.md, vertical = LoanMasterTheme.spacing.gridGutter),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    androidx.compose.material3.Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(LoanMasterTheme.components.iconMedium.value.dp * 0.8f))
                    androidx.compose.foundation.layout.Spacer(Modifier.widthIn(min = LoanMasterTheme.spacing.gridGutter))
                    androidx.compose.foundation.text.BasicTextField(
                        value = value,
                        onValueChange = handleValueChange,
                        readOnly = readOnly,
                        enabled = onClick == null,
                        textStyle = TextStyle(color = textColor, fontSize = LoanMasterTheme.typography.body.fontSize),
                        cursorBrush = androidx.compose.ui.graphics.SolidColor(cursorColor),
                        modifier = Modifier.weight(1f).onFocusChanged { isFocused = it.isFocused },
                        singleLine = true,
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
                    ) { innerTextField ->
                        androidx.compose.foundation.layout.Box(modifier = Modifier.fillMaxWidth().padding(vertical = LoanMasterTheme.spacing.xs)) {
                            if (value.isEmpty()) {
                                Text("0", color = colors.onSurfaceVariant, fontSize = LoanMasterTheme.typography.body.fontSize)
                            }
                            innerTextField()
                        }
                    }
                    if (suffix.isNotEmpty()) {
                        Text(suffix, color = colors.onSurfaceVariant, fontSize = LoanMasterTheme.typography.body.fontSize.value.sp * 0.9f)
                    }
                    if (trailingContent != null) {
                        trailingContent()
                    } else {
                        trailingIcon?.let {
                            androidx.compose.material3.Icon(imageVector = it, contentDescription = null, tint = colors.onSurfaceVariant, modifier = Modifier.size(LoanMasterTheme.components.iconMedium.value.dp * 0.8f))
                        }
                    }
                }
                if (onClick != null) {
                    androidx.compose.foundation.layout.Spacer(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable { onClick() }
                    )
                }
            }
        }
        if (finalErrorMessage != null) {
            androidx.compose.foundation.layout.Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.xs))
            Text(finalErrorMessage, color = colors.error, fontSize = LoanMasterTheme.typography.label.fontSize, modifier = Modifier.padding(start = LoanMasterTheme.spacing.xs))
        }
    }
}

@OptIn(androidx.compose.animation.ExperimentalAnimationApi::class)
@Composable
fun CalculatorScreenLayout(
    widthSizeClass: WindowWidthSizeClass,
    headerSection: @Composable () -> Unit,
    inputControlsSection: @Composable () -> Unit,
    resultsSection: @Composable () -> Unit,
    animationTriggerState: Any? = null
) {
    val scrollState = androidx.compose.foundation.rememberScrollState()
    
    val animatedResults: @Composable () -> Unit = {
        if (animationTriggerState != null) {
            androidx.compose.animation.AnimatedContent(
                targetState = animationTriggerState,
                transitionSpec = {
                    androidx.compose.animation.fadeIn() togetherWith androidx.compose.animation.fadeOut()
                },
                label = "resultsAnimation"
            ) { _ ->
                resultsSection()
            }
        } else {
            resultsSection()
        }
    }

    val globalHeader: @Composable () -> Unit = {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.foundation.layout.Box(modifier = Modifier.weight(1f)) {
                headerSection()
            }
        }
    }

    androidx.compose.foundation.layout.Box(
        modifier = Modifier.fillMaxSize().safeDrawingPadding(),
        contentAlignment = Alignment.TopCenter
    ) {
        androidx.compose.foundation.layout.Column(
            modifier = Modifier
                .widthIn(max = 840.dp)
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(horizontal = LoanMasterTheme.spacing.screenPadding, vertical = LoanMasterTheme.spacing.xs)
        ) {
            globalHeader()

            if (widthSizeClass == WindowWidthSizeClass.EXPANDED) {
                androidx.compose.foundation.layout.Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.sm))
                androidx.compose.foundation.layout.Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(LoanMasterTheme.spacing.md)
                ) {
                    androidx.compose.foundation.layout.Column(modifier = Modifier.weight(1f)) {
                        inputControlsSection()
                    }
                    androidx.compose.foundation.layout.Column(modifier = Modifier.weight(1f)) {
                        animatedResults()
                    }
                }
            } else {
                androidx.compose.foundation.layout.Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.xs))
                inputControlsSection()
                androidx.compose.foundation.layout.Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.sm))
                animatedResults()
            }
        }
    }
}

@Composable
fun ResponsiveCard(
    modifier: Modifier = Modifier,
    minWidth: Dp = Dp.Unspecified,
    bgColor: Color = androidx.compose.material3.MaterialTheme.colorScheme.surface,
    borderColor: Color = androidx.compose.material3.MaterialTheme.colorScheme.outlineVariant,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    var m = modifier
        .wrapContentHeight()
        .clip(RoundedCornerShape(LoanMasterTheme.spacing.md))
        .background(bgColor)
        .border(1.dp, borderColor, RoundedCornerShape(LoanMasterTheme.spacing.md))
        
    if (minWidth != Dp.Unspecified) {
        m = m.defaultMinSize(minWidth = minWidth)
    }
        
    if (onClick != null) {
        m = m.clickable { onClick() }
    }
    
    Box(modifier = m.padding(LoanMasterTheme.spacing.md)) {
        content()
    }
}

fun String.safeToDouble(): Double {
    return this.replace(",", "").trim().toDoubleOrNull() ?: 0.0
}

// FIX: Added Financial Disclaimer
@Composable
fun FinancialDisclaimer(modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier) {
    androidx.compose.material3.Text(
        text = "⚠ For informational purposes only. Not financial advice. Consult a qualified advisor before making financial decisions.",
        color = com.loanmaster.pro.core.theme.TextSecondary.copy(alpha = 0.7f),
        fontSize = 10.sp,
        lineHeight = 14.sp,
        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}
