package com.example

import androidx.compose.foundation.layout.widthIn
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape

enum class WindowWidthSizeClass { Compact, Medium, Expanded }

object ResponsiveUtils {
    
    // Fintech Dark Theme Colors for easy access inside components
    val BgColor = Color(0xFF020B1F)
    val SurfaceColor = Color(0xFF061633)
    val PrimaryAccent = Color(0xFFFFC328)
    val SecondaryAccent = Color(0xFF2D7DFF)
    val CardStroke = Color(0xFF183C8A)
    val TextPrimary = Color(0xFFFFFFFF)
    val TextSecondary = Color(0xFFA8B3D1)
    
    val CardShape = RoundedCornerShape(16.dp)
    val ButtonShape = RoundedCornerShape(12.dp)

    @JvmStatic
    fun horizontalPadding(sizeClass: WindowWidthSizeClass): Dp = when (sizeClass) {
        WindowWidthSizeClass.Compact -> 16.dp
        WindowWidthSizeClass.Medium -> 48.dp
        WindowWidthSizeClass.Expanded -> 120.dp
    }

    @JvmStatic
    fun verticalPadding(sizeClass: WindowWidthSizeClass): Dp = when (sizeClass) {
        WindowWidthSizeClass.Compact -> 24.dp
        WindowWidthSizeClass.Medium -> 32.dp
        WindowWidthSizeClass.Expanded -> 48.dp
    }

    @JvmStatic
    fun iconSize(sizeClass: WindowWidthSizeClass): Dp = when (sizeClass) {
        WindowWidthSizeClass.Compact -> 24.dp
        WindowWidthSizeClass.Medium -> 28.dp
        WindowWidthSizeClass.Expanded -> 32.dp
    }

    @JvmStatic
    fun titleFontSize(sizeClass: WindowWidthSizeClass): TextUnit = when (sizeClass) {
        WindowWidthSizeClass.Compact -> 22.sp
        WindowWidthSizeClass.Medium -> 26.sp
        WindowWidthSizeClass.Expanded -> 32.sp
    }
    
    @JvmStatic
    fun subtitleFontSize(sizeClass: WindowWidthSizeClass): TextUnit = when (sizeClass) {
        WindowWidthSizeClass.Compact -> 16.sp
        WindowWidthSizeClass.Medium -> 18.sp
        WindowWidthSizeClass.Expanded -> 20.sp
    }

    @JvmStatic
    fun bodyFontSize(sizeClass: WindowWidthSizeClass): TextUnit = when (sizeClass) {
        WindowWidthSizeClass.Compact -> 15.sp
        WindowWidthSizeClass.Medium -> 17.sp
        WindowWidthSizeClass.Expanded -> 18.sp
    }
    
    @JvmStatic
    fun labelFontSize(sizeClass: WindowWidthSizeClass): TextUnit = when (sizeClass) {
        WindowWidthSizeClass.Compact -> 13.sp
        WindowWidthSizeClass.Medium -> 14.sp
        WindowWidthSizeClass.Expanded -> 15.sp
    }

    @JvmStatic
    fun heroValueFontSize(sizeClass: WindowWidthSizeClass): TextUnit = when (sizeClass) {
        WindowWidthSizeClass.Compact -> 34.sp
        WindowWidthSizeClass.Medium -> 38.sp
        WindowWidthSizeClass.Expanded -> 42.sp
    }

    @JvmStatic
    fun cardSpacing(sizeClass: WindowWidthSizeClass): Dp = when (sizeClass) {
        WindowWidthSizeClass.Compact -> 16.dp
        WindowWidthSizeClass.Medium -> 24.dp
        WindowWidthSizeClass.Expanded -> 32.dp
    }
    
    // RESPONSIVE: Capping content width for tablet readability
    fun Modifier.optimalContentWidth(): Modifier {
        return this.widthIn(max = 840.dp)
    }
}
