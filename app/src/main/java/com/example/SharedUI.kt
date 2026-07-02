package com.example

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
import com.example.ui.theme.CardStroke
import com.example.ui.theme.SurfaceDark

import java.text.NumberFormat
import java.util.Locale

var globalCurrencySymbol: String = "₹"

fun extractCurrencySymbol(currencyString: String): String {
    return currencyString.substringAfter("(", "₹").removeSuffix(")")
}

private val moneyFormat = NumberFormat.getCurrencyInstance(Locale.Builder().setLanguage("en").setRegion("US").build()).apply {
    maximumFractionDigits = 0
}

fun formatMoney(amt: Double, currencySym: String = globalCurrencySymbol): String {
    if (amt <= 0) return "${currencySym}0"
    // Replace the default symbol with our custom one
    return moneyFormat.format(amt).replace("$", currencySym)
}

@Composable
fun AutoSizeText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    minTextSize: TextUnit = 12.sp,
    maxTextSize: TextUnit = TextUnit.Unspecified,
    fontWeight: FontWeight? = null,
    maxLines: Int = 1,
) {
    var textStyle by remember(text, maxTextSize, fontWeight, color) { mutableStateOf(TextStyle(fontSize = maxTextSize, fontWeight = fontWeight, color = color)) }
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
    fontSize: TextUnit = TextUnit.Unspecified,
    fontWeight: FontWeight? = null,
    maxLines: Int = 1,
) {
    AutoSizeText(
        text = text,
        modifier = modifier,
        color = color,
        minTextSize = 10.sp,
        maxTextSize = fontSize,
        fontWeight = fontWeight,
        maxLines = maxLines
    )
}

@Composable
fun ScrollingTitleText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
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
    suffix: String = "",
    sizeClass: WindowWidthSizeClass = WindowWidthSizeClass.Compact,
    readOnly: Boolean = false,
    onClick: (() -> Unit)? = null,
    infoText: String? = null,
    errorMessage: String? = null,
    modifier: Modifier = Modifier
) {
    var showInfoDialog by remember { mutableStateOf(false) }
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
        if (value.isNotEmpty() && value != "." && value.toDoubleOrNull() == null) {
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

    val hasError = finalErrorMessage != null
    val strokeColor = if (hasError) colors.error else colors.outlineVariant
    val textColor = if (hasError) colors.error else colors.onSurface
    val iconColor = if (hasError) colors.error else iconTint
    val cursorColor = if (hasError) colors.error else colors.primary

    androidx.compose.foundation.layout.Column(modifier = modifier) {
        androidx.compose.foundation.layout.Row(verticalAlignment = Alignment.CenterVertically) {
            Text(label, color = colors.onSurfaceVariant, fontSize = ResponsiveUtils.bodyFontSize(sizeClass).value.sp * 0.85f)
            if (infoText != null) {
                androidx.compose.foundation.layout.Spacer(Modifier.width(4.dp))
                androidx.compose.material3.Icon(
                    imageVector = Icons.Rounded.Info,
                    contentDescription = "Info about $label",
                    tint = colors.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier
                        .size(14.dp)
                        .clickable(
                            indication = null,
                            interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                        ) { showInfoDialog = true }
                )
            }
        }
        androidx.compose.foundation.layout.Spacer(Modifier.height(6.dp))
        androidx.compose.material3.Surface(
            shape = RoundedCornerShape(12.dp),
            color = colors.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, strokeColor),
            modifier = Modifier.fillMaxWidth()
        ) {
            androidx.compose.foundation.layout.Box(modifier = Modifier.fillMaxWidth()) {
                androidx.compose.foundation.layout.Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    androidx.compose.material3.Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(ResponsiveUtils.iconSize(sizeClass).value.dp * 0.8f))
                    androidx.compose.foundation.layout.Spacer(Modifier.width(10.dp))
                    androidx.compose.foundation.text.BasicTextField(
                        value = value,
                        onValueChange = handleValueChange,
                        readOnly = readOnly,
                        enabled = onClick == null,
                        textStyle = TextStyle(color = textColor, fontSize = ResponsiveUtils.bodyFontSize(sizeClass)),
                        cursorBrush = androidx.compose.ui.graphics.SolidColor(cursorColor),
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
                    ) { innerTextField ->
                        androidx.compose.foundation.layout.Box(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            if (value.isEmpty()) {
                                Text("0", color = colors.onSurfaceVariant, fontSize = ResponsiveUtils.bodyFontSize(sizeClass))
                            }
                            innerTextField()
                        }
                    }
                    if (suffix.isNotEmpty()) {
                        Text(suffix, color = colors.onSurfaceVariant, fontSize = ResponsiveUtils.bodyFontSize(sizeClass).value.sp * 0.9f)
                    }
                    if (trailingContent != null) {
                        trailingContent()
                    } else {
                        trailingIcon?.let {
                            androidx.compose.material3.Icon(imageVector = it, contentDescription = null, tint = colors.onSurfaceVariant, modifier = Modifier.size(ResponsiveUtils.iconSize(sizeClass).value.dp * 0.8f))
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
            androidx.compose.foundation.layout.Spacer(Modifier.height(4.dp))
            Text(finalErrorMessage, color = colors.error, fontSize = 12.sp, modifier = Modifier.padding(start = 4.dp))
        }
    }
}

@OptIn(androidx.compose.animation.ExperimentalAnimationApi::class)
@Composable
fun ResponsiveScreenWrapper(
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

    androidx.compose.foundation.layout.Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
            .padding(horizontal = ResponsiveUtils.horizontalPadding(widthSizeClass), vertical = 16.dp)
    ) {
        globalHeader()
        if (widthSizeClass == WindowWidthSizeClass.Expanded) {
            androidx.compose.foundation.layout.Spacer(Modifier.height(24.dp))
            androidx.compose.foundation.layout.Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(24.dp)
            ) {
                androidx.compose.foundation.layout.Column(modifier = Modifier.weight(1f)) {
                    inputControlsSection()
                }
                androidx.compose.foundation.layout.Column(modifier = Modifier.weight(1f)) {
                    animatedResults()
                }
            }
        } else {
            androidx.compose.foundation.layout.Spacer(Modifier.height(16.dp))
            inputControlsSection()
            androidx.compose.foundation.layout.Spacer(Modifier.height(24.dp))
            animatedResults()
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
        .clip(RoundedCornerShape(12.dp))
        .background(bgColor)
        .border(1.dp, borderColor, RoundedCornerShape(12.dp))
        
    if (minWidth != Dp.Unspecified) {
        m = m.defaultMinSize(minWidth = minWidth)
    }
        
    if (onClick != null) {
        m = m.clickable { onClick() }
    }
    
    Box(modifier = m.padding(12.dp)) {
        content()
    }
}

fun String.safeToDouble(): Double {
    return this.replace(",", "").trim().toDoubleOrNull() ?: 0.0
}
