package com.example.loanmasterpro

import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Centralized responsive utilities for consistent adaptive layouts across the app.
 */
object ResponsiveUtils {

    // ==================== WIDTH-BASED RESPONSIVE VALUES ====================

    fun horizontalPadding(widthSizeClass: WindowWidthSizeClass): Dp = when (widthSizeClass) {
        WindowWidthSizeClass.Compact -> 12.dp
        WindowWidthSizeClass.Medium -> 16.dp
        WindowWidthSizeClass.Expanded -> 24.dp
        else -> 16.dp
    }

    fun verticalPadding(widthSizeClass: WindowWidthSizeClass): Dp = when (widthSizeClass) {
        WindowWidthSizeClass.Compact -> 12.dp
        WindowWidthSizeClass.Medium -> 16.dp
        WindowWidthSizeClass.Expanded -> 20.dp
        else -> 16.dp
    }

    fun iconSize(widthSizeClass: WindowWidthSizeClass): Dp = when (widthSizeClass) {
        WindowWidthSizeClass.Compact -> 20.dp
        WindowWidthSizeClass.Medium -> 24.dp
        WindowWidthSizeClass.Expanded -> 28.dp
        else -> 24.dp
    }

    fun titleFontSize(widthSizeClass: WindowWidthSizeClass): Dp = when (widthSizeClass) {
        WindowWidthSizeClass.Compact -> 16.dp
        WindowWidthSizeClass.Medium -> 18.dp
        WindowWidthSizeClass.Expanded -> 20.dp
        else -> 18.dp
    }

    fun bodyFontSize(widthSizeClass: WindowWidthSizeClass): Dp = when (widthSizeClass) {
        WindowWidthSizeClass.Compact -> 13.dp
        WindowWidthSizeClass.Medium -> 14.dp
        WindowWidthSizeClass.Expanded -> 15.dp
        else -> 14.dp
    }

    fun cardSpacing(widthSizeClass: WindowWidthSizeClass): Dp = when (widthSizeClass) {
        WindowWidthSizeClass.Compact -> 10.dp
        WindowWidthSizeClass.Medium -> 12.dp
        WindowWidthSizeClass.Expanded -> 16.dp
        else -> 12.dp
    }

    // ==================== HEIGHT-BASED HELPERS ====================

    fun isCompactHeight(heightSizeClass: WindowHeightSizeClass): Boolean =
        heightSizeClass == WindowHeightSizeClass.Compact

    fun isMediumHeight(heightSizeClass: WindowHeightSizeClass): Boolean =
        heightSizeClass == WindowHeightSizeClass.Medium

    fun isExpandedHeight(heightSizeClass: WindowHeightSizeClass): Boolean =
        heightSizeClass == WindowHeightSizeClass.Expanded
}
