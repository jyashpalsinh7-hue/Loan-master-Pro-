package com.loanmaster.pro.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import com.loanmaster.pro.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * RESPONSIVE: Spacing tokens scale across breakpoints
 * Compact (< 600dp): mobile phones
 * Medium (600-840dp): tablets
 * Expanded (> 840dp): large tablets/desktops
 */
@Immutable
data class AppSpacing(
    val xs: Dp = 4.dp,           // Extra small - dividers, minimal gaps
    val sm: Dp = 8.dp,           // Small spacing
    val md: Dp = 16.dp,          // Medium spacing (default card padding)
    val lg: Dp = 24.dp,          // Large spacing (between sections)
    val xl: Dp = 32.dp,          // Extra large spacing
    val screenPadding: Dp = 16.dp,  // Edge padding on screen
    val gridGutter: Dp = 10.dp   // Gap between grid columns
)

/**
 * RESPONSIVE: Component sizes that adapt to screen size
 */
@Immutable
data class AppComponents(
    val buttonHeight: Dp = 48.dp,           // Touch target minimum height
    val cardRadius: Dp = 16.dp,             // Border radius for cards
    val borderRadiusSm: Dp = 8.dp,          // Small border radius
    val borderRadiusMd: Dp = 16.dp,         // Medium border radius
    val borderRadiusLg: Dp = 24.dp,         // Large border radius
    val borderThick: Dp = 1.dp,             // Standard border thickness
    val borderThin: Dp = 0.5.dp,            // Thin border (dividers)
    val iconSmall: Dp = 20.dp,              // Small icons (16-20dp)
    val iconMedium: Dp = 24.dp,             // Medium icons (24-28dp)
    val iconLarge: Dp = 32.dp,              // Large icons (32-40dp)
    val topAppBarHeight: Dp = 56.dp,        // Header height
    val bottomNavHeight: Dp = 72.dp,        // Bottom navigation
    val dialogMaxWidth: Dp = 400.dp,        // Dialog max width (tablets)
    val calculatorCardHeight: Dp = 100.dp,  // Calculator result card height
    val featuredCardHeight: Dp = 120.dp     // Featured/hero card height
)

/**
 * RESPONSIVE: Typography scales across breakpoints
 * Maintains proper hierarchy and readability at all sizes
 */
@Immutable
data class AppTypographyTokens(
    val display: TextStyle = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.Bold, lineHeight = 36.sp),
    val title: TextStyle = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.SemiBold, lineHeight = 24.sp),
    val body: TextStyle = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal, lineHeight = 20.sp),
    val label: TextStyle = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Medium, lineHeight = 14.sp)
)

/**
 * RESPONSIVE: Grid column counts adapt to available space
 */
@Immutable
data class AppGrids(
    val calculatorColumns: Int = 1,     // Calculator grid columns
    val scheduleColumns: Int = 1        // Schedule/amortization grid columns
)

val LocalAppSpacing = staticCompositionLocalOf { AppSpacing() }
val LocalAppComponents = staticCompositionLocalOf { AppComponents() }
val LocalAppTypographyTokens = staticCompositionLocalOf { AppTypographyTokens() }
val LocalAppGrids = staticCompositionLocalOf { AppGrids() }

object LoanMasterTheme {
    val spacing: AppSpacing
        @Composable
        get() = LocalAppSpacing.current
    
    val components: AppComponents
        @Composable
        get() = LocalAppComponents.current
        
    val typography: AppTypographyTokens
        @Composable
        get() = LocalAppTypographyTokens.current
        
    val grids: AppGrids
        @Composable
        get() = LocalAppGrids.current
}

/**
 * Main theme composable that provides responsive tokens based on window size
 * Updates all spacing, components, typography, and grids automatically
 */
@Composable
fun LoanMasterTheme(
    windowSizeClass: WindowWidthSizeClass = WindowWidthSizeClass.Compact,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val isExpanded = windowSizeClass == WindowWidthSizeClass.Expanded
    val isMedium = windowSizeClass == WindowWidthSizeClass.Medium

    // RESPONSIVE: Spacing adjusts for each breakpoint
    val spacing = if (isExpanded) {
        AppSpacing(
            xs = 6.dp,                  // ← Responsive
            sm = 12.dp,
            md = 24.dp,
            lg = 32.dp,
            xl = 40.dp,
            screenPadding = 32.dp,
            gridGutter = 20.dp
        )
    } else if (isMedium) {
        AppSpacing(
            xs = 5.dp,                  // ← Responsive
            sm = 10.dp,
            md = 20.dp,
            lg = 28.dp,
            xl = 36.dp,
            screenPadding = 24.dp,
            gridGutter = 16.dp
        )
    } else {
        // Compact (mobile)
        AppSpacing(
            xs = 4.dp,
            sm = 8.dp,
            md = 16.dp,
            lg = 24.dp,
            xl = 32.dp,
            screenPadding = 16.dp,
            gridGutter = 10.dp
        )
    }

    // RESPONSIVE: Component sizes scale appropriately
    val components = if (isExpanded) {
        AppComponents(
            // Touch targets stay above 48dp minimum
            buttonHeight = 56.dp,
            cardRadius = 20.dp,
            borderRadiusSm = 12.dp,
            borderRadiusMd = 20.dp,
            borderRadiusLg = 28.dp,
            // Border remains consistent across sizes (thin/thick)
            borderThick = 1.2.dp,
            borderThin = 0.5.dp,
            // Icons scale up on larger screens
            iconSmall = 28.dp,
            iconMedium = 36.dp,
            iconLarge = 48.dp,
            // App bars and nav scale up
            topAppBarHeight = 72.dp,
            bottomNavHeight = 88.dp,
            // Dialog width capped for readability
            dialogMaxWidth = 600.dp,
            // Cards are taller on expanded screens
            calculatorCardHeight = 160.dp,
            featuredCardHeight = 200.dp
        )
    } else if (isMedium) {
        AppComponents(
            buttonHeight = 52.dp,
            cardRadius = 18.dp,
            borderRadiusSm = 10.dp,
            borderRadiusMd = 18.dp,
            borderRadiusLg = 26.dp,
            borderThick = 1.dp,
            borderThin = 0.5.dp,
            // Medium screen icons
            iconSmall = 24.dp,
            iconMedium = 32.dp,
            iconLarge = 40.dp,
            topAppBarHeight = 64.dp,
            bottomNavHeight = 80.dp,
            dialogMaxWidth = 500.dp,
            calculatorCardHeight = 140.dp,
            featuredCardHeight = 170.dp
        )
    } else {
        // Compact (mobile) - default sizing
        AppComponents(
            buttonHeight = 48.dp,
            cardRadius = 16.dp,
            borderRadiusSm = 8.dp,
            borderRadiusMd = 16.dp,
            borderRadiusLg = 24.dp,
            borderThick = 1.dp,
            borderThin = 0.5.dp,
            iconSmall = 20.dp,
            iconMedium = 24.dp,
            iconLarge = 32.dp,
            topAppBarHeight = 56.dp,
            bottomNavHeight = 72.dp,
            dialogMaxWidth = 400.dp,
            calculatorCardHeight = 120.dp,
            featuredCardHeight = 150.dp
        )
    }

    // RESPONSIVE: Typography scales for optimal readability at each breakpoint
    val typography = if (isExpanded) {
        AppTypographyTokens(
            display = TextStyle(fontSize = 40.sp, fontWeight = FontWeight.Bold, lineHeight = 48.sp),
            title = TextStyle(fontSize = 26.sp, fontWeight = FontWeight.SemiBold, lineHeight = 34.sp),
            body = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Normal, lineHeight = 28.sp),
            label = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, lineHeight = 20.sp)
        )
    } else if (isMedium) {
        AppTypographyTokens(
            display = TextStyle(fontSize = 36.sp, fontWeight = FontWeight.Bold, lineHeight = 44.sp),
            title = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.SemiBold, lineHeight = 30.sp),
            body = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Normal, lineHeight = 24.sp),
            label = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Medium, lineHeight = 18.sp)
        )
    } else {
        // Compact (mobile)
        AppTypographyTokens(
            display = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.Bold, lineHeight = 36.sp),
            title = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.SemiBold, lineHeight = 26.sp),
            body = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal, lineHeight = 20.sp),
            label = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Medium, lineHeight = 14.sp)
        )
    }
    
    // RESPONSIVE: Grid columns adapt to screen width
    val grids = if (isExpanded) {
        AppGrids(
            calculatorColumns = 4,  // 4-column grid on large tablets
            scheduleColumns = 2     // 2-column amortization on large tablets
        )
    } else if (isMedium) {
        AppGrids(
            calculatorColumns = 2,  // 2-column grid on tablets
            scheduleColumns = 1     // 1-column on medium tablets
        )
    } else {
        AppGrids(
            calculatorColumns = 1,  // 1-column grid on mobile
            scheduleColumns = 1     // 1-column on mobile
        )
    }

    // Provide all tokens to composables via CompositionLocal
    CompositionLocalProvider(
        LocalAppSpacing provides spacing,
        LocalAppComponents provides components,
        LocalAppTypographyTokens provides typography,
        LocalAppGrids provides grids
    ) {
        MyApplicationTheme(
            content = content
        )
    }
}
