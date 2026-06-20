package com.example

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

    fun horizontalPadding(widthSizeClass: WindowWidthSizeClass): Dp {
        return when (widthSizeClass) {
            WindowWidthSizeClass.Compact -> 16.dp
            WindowWidthSizeClass.Medium -> 48.dp
            WindowWidthSizeClass.Expanded -> 120.dp
        }
    }

    fun verticalPadding(widthSizeClass: WindowWidthSizeClass): Dp {
        return when (widthSizeClass) {
            WindowWidthSizeClass.Compact -> 24.dp
            WindowWidthSizeClass.Medium -> 32.dp
            WindowWidthSizeClass.Expanded -> 48.dp
        }
    }

    fun iconSize(widthSizeClass: WindowWidthSizeClass): Dp {
        return when (widthSizeClass) {
            WindowWidthSizeClass.Compact -> 24.dp
            WindowWidthSizeClass.Medium -> 28.dp
            WindowWidthSizeClass.Expanded -> 32.dp
        }
    }

    fun titleFontSize(widthSizeClass: WindowWidthSizeClass): TextUnit {
        return when (widthSizeClass) {
            WindowWidthSizeClass.Compact -> 22.sp
            WindowWidthSizeClass.Medium -> 26.sp
            WindowWidthSizeClass.Expanded -> 32.sp
        }
    }
    
    fun subtitleFontSize(widthSizeClass: WindowWidthSizeClass): TextUnit {
        return when (widthSizeClass) {
            WindowWidthSizeClass.Compact -> 16.sp
            WindowWidthSizeClass.Medium -> 18.sp
            WindowWidthSizeClass.Expanded -> 20.sp
        }
    }

    fun bodyFontSize(widthSizeClass: WindowWidthSizeClass): TextUnit {
        return when (widthSizeClass) {
            WindowWidthSizeClass.Compact -> 15.sp
            WindowWidthSizeClass.Medium -> 17.sp
            WindowWidthSizeClass.Expanded -> 18.sp
        }
    }
    
    fun labelFontSize(widthSizeClass: WindowWidthSizeClass): TextUnit {
        return when (widthSizeClass) {
            WindowWidthSizeClass.Compact -> 13.sp
            WindowWidthSizeClass.Medium -> 14.sp
            WindowWidthSizeClass.Expanded -> 15.sp
        }
    }

    fun cardSpacing(widthSizeClass: WindowWidthSizeClass): Dp {
        return when (widthSizeClass) {
            WindowWidthSizeClass.Compact -> 16.dp
            WindowWidthSizeClass.Medium -> 24.dp
            WindowWidthSizeClass.Expanded -> 32.dp
        }
    }
}
