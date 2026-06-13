package com.example

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
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
