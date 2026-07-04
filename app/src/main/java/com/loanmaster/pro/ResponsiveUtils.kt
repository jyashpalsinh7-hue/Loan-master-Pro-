package com.loanmaster.pro

import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import com.loanmaster.pro.ui.theme.LoanMasterTheme

enum class WindowWidthSizeClass { Compact, Medium, Expanded }

/**
 * Single source of truth for "how big is the window". Standard Material 3
 * breakpoints (600dp / 840dp). Previously this exact calculation was
 * copy-pasted independently in 9+ files; everything should call this instead.
 */
@Composable
fun rememberWindowWidthSizeClass(): WindowWidthSizeClass {
    val screenWidthDp = LocalConfiguration.current.screenWidthDp
    return when {
        screenWidthDp < 600 -> WindowWidthSizeClass.Compact
        screenWidthDp < 840 -> WindowWidthSizeClass.Medium
        else -> WindowWidthSizeClass.Expanded
    }
}

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

    // --- Below: thin @Composable wrappers around LoanMasterTheme's tokens. ---
    // These used to be a second, independent hardcoded scale that disagreed
    // with LoanMasterTheme at every breakpoint (e.g. horizontal padding used
    // to jump 16 -> 48 -> 120dp here vs. 16 -> 24 -> 32dp in LoanMasterTheme).
    // They now all read from the same CompositionLocal-backed tokens, so the
    // ~70 existing call sites across the app keep compiling unchanged but get
    // consistent numbers everywhere. The `sizeClass` parameter is kept for
    // source compatibility but is no longer used to branch on: LoanMasterTheme
    // already reacts to the real window size via its own CompositionLocal.

    @Composable
    fun horizontalPadding(sizeClass: WindowWidthSizeClass): Dp = LoanMasterTheme.spacing.screenPadding

    @Composable
    fun verticalPadding(sizeClass: WindowWidthSizeClass): Dp = LoanMasterTheme.spacing.lg

    @Composable
    fun iconSize(sizeClass: WindowWidthSizeClass): Dp = LoanMasterTheme.components.iconMedium

    @Composable
    fun titleFontSize(sizeClass: WindowWidthSizeClass): TextUnit = LoanMasterTheme.typography.title.fontSize

    @Composable
    fun subtitleFontSize(sizeClass: WindowWidthSizeClass): TextUnit = LoanMasterTheme.typography.title.fontSize

    @Composable
    fun bodyFontSize(sizeClass: WindowWidthSizeClass): TextUnit = LoanMasterTheme.typography.body.fontSize

    @Composable
    fun labelFontSize(sizeClass: WindowWidthSizeClass): TextUnit = LoanMasterTheme.typography.label.fontSize

    @Composable
    fun heroValueFontSize(sizeClass: WindowWidthSizeClass): TextUnit = LoanMasterTheme.typography.display.fontSize

    @Composable
    fun cardSpacing(sizeClass: WindowWidthSizeClass): Dp = LoanMasterTheme.spacing.screenPadding

    // RESPONSIVE: Capping content width for tablet readability
    fun Modifier.optimalContentWidth(): Modifier {
        return this.widthIn(max = 840.dp)
    }
}
