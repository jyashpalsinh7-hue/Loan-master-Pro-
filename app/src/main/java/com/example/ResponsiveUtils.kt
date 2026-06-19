package com.example

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class WindowWidthSizeClass { Compact, Medium, Expanded }

object ResponsiveUtils {
    fun horizontalPadding(widthSizeClass: WindowWidthSizeClass): Dp {
        return when (widthSizeClass) {
            WindowWidthSizeClass.Compact -> 16.dp
            WindowWidthSizeClass.Medium -> 32.dp
            WindowWidthSizeClass.Expanded -> 64.dp
        }
    }

    fun verticalPadding(widthSizeClass: WindowWidthSizeClass): Dp {
        return when (widthSizeClass) {
            WindowWidthSizeClass.Compact -> 16.dp
            WindowWidthSizeClass.Medium -> 24.dp
            WindowWidthSizeClass.Expanded -> 32.dp
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
            WindowWidthSizeClass.Compact -> 20.sp
            WindowWidthSizeClass.Medium -> 24.sp
            WindowWidthSizeClass.Expanded -> 28.sp
        }
    }

    fun bodyFontSize(widthSizeClass: WindowWidthSizeClass): TextUnit {
        return when (widthSizeClass) {
            WindowWidthSizeClass.Compact -> 14.sp
            WindowWidthSizeClass.Medium -> 16.sp
            WindowWidthSizeClass.Expanded -> 18.sp
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
