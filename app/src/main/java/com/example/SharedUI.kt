package com.example

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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

@Composable
fun AutoResizedText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontWeight: FontWeight? = null,
    maxLines: Int = 1,
) {
    var textStyle by remember(text, fontSize, fontWeight, color) { mutableStateOf(TextStyle(fontSize = fontSize, fontWeight = fontWeight, color = color)) }
    var readyToDraw by remember(text) { mutableStateOf(false) }

    Text(
        text = text,
        modifier = modifier.drawWithContent {
            if (readyToDraw) drawContent()
        },
        style = textStyle,
        color = color,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
        onTextLayout = { textLayoutResult ->
            if (!readyToDraw) {
                if ((textLayoutResult.didOverflowWidth || textLayoutResult.didOverflowHeight) && textStyle.fontSize > 10.sp) {
                    val nextSize = (textStyle.fontSize.value * 0.9f).sp
                    if (nextSize >= 10.sp) {
                        textStyle = textStyle.copy(fontSize = nextSize)
                    } else {
                        textStyle = textStyle.copy(fontSize = 10.sp)
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
    suffix: String = "",
    sizeClass: com.example.WindowWidthSizeClass = com.example.WindowWidthSizeClass.Compact,
    modifier: Modifier = Modifier
) {
    androidx.compose.foundation.layout.Column(modifier = modifier) {
        Text(label, color = com.example.ui.theme.TextSecondary, fontSize = com.example.ResponsiveUtils.bodyFontSize(sizeClass).value.sp * 0.85f)
        androidx.compose.foundation.layout.Spacer(Modifier.height(6.dp))
        androidx.compose.material3.Surface(
            shape = RoundedCornerShape(12.dp),
            color = com.example.ui.theme.SurfaceDark,
            border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.CardStroke)
        ) {
            androidx.compose.foundation.layout.Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                androidx.compose.material3.Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(com.example.ResponsiveUtils.iconSize(sizeClass).value.dp * 0.8f))
                androidx.compose.foundation.layout.Spacer(Modifier.width(10.dp))
                androidx.compose.foundation.text.BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    textStyle = TextStyle(color = Color.White, fontSize = com.example.ResponsiveUtils.bodyFontSize(sizeClass)),
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
                )
                if (suffix.isNotEmpty()) {
                    Text(suffix, color = com.example.ui.theme.TextSecondary, fontSize = com.example.ResponsiveUtils.bodyFontSize(sizeClass).value.sp * 0.9f)
                }
                trailingIcon?.let {
                    androidx.compose.material3.Icon(imageVector = it, contentDescription = null, tint = com.example.ui.theme.TextSecondary, modifier = Modifier.size(com.example.ResponsiveUtils.iconSize(sizeClass).value.dp * 0.8f))
                }
            }
        }
    }
}

@Composable
fun ResponsiveCard(
    modifier: Modifier = Modifier,
    minWidth: Dp = Dp.Unspecified,
    bgColor: Color = SurfaceDark,
    borderColor: Color = CardStroke,
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
