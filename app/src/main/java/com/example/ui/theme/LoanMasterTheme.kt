package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import com.example.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Immutable
data class AppSpacing(
    val xs: Dp = 4.dp,
    val sm: Dp = 8.dp,
    val md: Dp = 16.dp,
    val lg: Dp = 24.dp,
    val xl: Dp = 32.dp,
    val screenPadding: Dp = 16.dp,
    val gridGutter: Dp = 16.dp
)

@Immutable
data class AppComponents(
    val buttonHeight: Dp = 56.dp,
    val cardRadius: Dp = 16.dp,
    val iconSmall: Dp = 20.dp,
    val iconMedium: Dp = 24.dp,
    val iconLarge: Dp = 32.dp,
    val topAppBarHeight: Dp = 64.dp,
    val bottomNavHeight: Dp = 80.dp,
    val dialogMaxWidth: Dp = 400.dp,
    val calculatorCardHeight: Dp = 135.dp,
    val featuredCardHeight: Dp = 158.dp
)

@Immutable
data class AppTypographyTokens(
    val display: TextStyle = TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Bold, lineHeight = 40.sp),
    val title: TextStyle = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.SemiBold, lineHeight = 28.sp),
    val body: TextStyle = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Normal, lineHeight = 24.sp),
    val label: TextStyle = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Medium, lineHeight = 16.sp)
)

@Immutable
data class AppGrids(
    val calculatorColumns: Int = 1,
    val scheduleColumns: Int = 1
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

@Composable
fun LoanMasterTheme(
    windowSizeClass: WindowWidthSizeClass = WindowWidthSizeClass.Compact,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val isExpanded = windowSizeClass == WindowWidthSizeClass.Expanded
    val isMedium = windowSizeClass == WindowWidthSizeClass.Medium

    val spacing = if (isExpanded) {
        AppSpacing(
            screenPadding = 32.dp,
            gridGutter = 24.dp
        )
    } else if (isMedium) {
        AppSpacing(
            screenPadding = 24.dp,
            gridGutter = 20.dp
        )
    } else {
        AppSpacing()
    }
    
    val grids = if (isExpanded) {
        AppGrids(
            calculatorColumns = 4,
            scheduleColumns = 2
        )
    } else if (isMedium) {
        AppGrids(
            calculatorColumns = 3,
            scheduleColumns = 1
        )
    } else {
        AppGrids(
            calculatorColumns = 2,
            scheduleColumns = 1
        )
    }

    CompositionLocalProvider(
        LocalAppSpacing provides spacing,
        LocalAppComponents provides AppComponents(),
        LocalAppTypographyTokens provides AppTypographyTokens(),
        LocalAppGrids provides grids
    ) {
        MyApplicationTheme(
            content = content
        )
    }
}
