package com.loanmaster.pro.core.theme

import com.loanmaster.pro.domain.model.*
import com.loanmaster.pro.feature.gst.*
import com.loanmaster.pro.feature.sip.*
import com.loanmaster.pro.core.ui.*
import com.loanmaster.pro.feature.history.*
import com.loanmaster.pro.data.datastore.*
import com.loanmaster.pro.feature.settings.*
import com.loanmaster.pro.feature.rd.*
import com.loanmaster.pro.domain.calculator.*
import com.loanmaster.pro.data.local.entity.*
import com.loanmaster.pro.core.utils.*
import com.loanmaster.pro.data.local.dao.*
import com.loanmaster.pro.data.local.room.*
import com.loanmaster.pro.feature.emi.*
import com.loanmaster.pro.feature.loansummary.*
import com.loanmaster.pro.feature.prepayment.*
import com.loanmaster.pro.core.formatter.*
import com.loanmaster.pro.feature.fd.*
import com.loanmaster.pro.data.repository.*
import com.loanmaster.pro.feature.currency.*
import com.loanmaster.pro.core.navigation.*
import com.loanmaster.pro.feature.compare.*
import com.loanmaster.pro.feature.loaneligibility.*
import com.loanmaster.pro.core.responsive.*
import com.loanmaster.pro.feature.home.*

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.window.core.layout.WindowWidthSizeClass
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
    val gridGutter: Dp = 10.dp
)

@Immutable
data class AppComponents(
    val buttonHeight: Dp = 48.dp,
    val cardRadius: Dp = 16.dp,
    val iconSmall: Dp = 20.dp,
    val iconMedium: Dp = 24.dp,
    val iconLarge: Dp = 32.dp,
    val topAppBarHeight: Dp = 56.dp,
    val bottomNavHeight: Dp = 72.dp,
    val dialogMaxWidth: Dp = 400.dp,
    val calculatorCardHeight: Dp = 100.dp,
    val featuredCardHeight: Dp = 120.dp,
    val bannerHeight: Dp = 140.dp,
    val heroHeight: Dp = 200.dp,
    val chartHeight: Dp = 220.dp,
    val logoSize: Dp = 120.dp,
    val cardPadding: Dp = 16.dp
)

@Immutable
data class AppTypographyTokens(
    val display: TextStyle = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.Bold, lineHeight = 36.sp),
    val title: TextStyle = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.SemiBold, lineHeight = 24.sp),
    val body: TextStyle = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal, lineHeight = 20.sp),
    val label: TextStyle = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Medium, lineHeight = 14.sp)
)

@Immutable
data class AppGrids(
    val calculatorColumns: Int = 2,
    val inputColumns: Int = 1,
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
    windowSizeClass: WindowWidthSizeClass = WindowWidthSizeClass.COMPACT,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val isExpanded = windowSizeClass == WindowWidthSizeClass.EXPANDED
    val isMedium = windowSizeClass == WindowWidthSizeClass.MEDIUM

    val spacing = if (isExpanded) {
        AppSpacing(
            screenPadding = 32.dp,
            gridGutter = 20.dp,
            sm = 12.dp,
            md = 24.dp,
            lg = 32.dp
        )
    } else if (isMedium) {
        AppSpacing(
            screenPadding = 24.dp,
            gridGutter = 16.dp,
            sm = 10.dp,
            md = 20.dp,
            lg = 28.dp
        )
    } else {
        AppSpacing(
            screenPadding = 16.dp,
            gridGutter = 10.dp,
            sm = 8.dp,
            md = 14.dp,
            lg = 20.dp
        )
    }

    val components = if (isExpanded) {
        AppComponents(
            iconSmall = 24.dp, iconMedium = 32.dp, iconLarge = 48.dp,
            calculatorCardHeight = 150.dp, featuredCardHeight = 180.dp,
            bannerHeight = 200.dp, heroHeight = 280.dp, chartHeight = 320.dp, logoSize = 180.dp, cardPadding = 24.dp
        )
    } else if (isMedium) {
        AppComponents(
            iconSmall = 22.dp, iconMedium = 28.dp, iconLarge = 40.dp,
            calculatorCardHeight = 135.dp, featuredCardHeight = 160.dp,
            bannerHeight = 160.dp, heroHeight = 240.dp, chartHeight = 280.dp, logoSize = 150.dp, cardPadding = 20.dp
        )
    } else {
        AppComponents(
            iconSmall = 18.dp, iconMedium = 24.dp, iconLarge = 32.dp,
            calculatorCardHeight = 100.dp, featuredCardHeight = 120.dp,
            bannerHeight = 140.dp, heroHeight = 200.dp, chartHeight = 220.dp, logoSize = 120.dp, cardPadding = 16.dp
        )
    }

    val typography = if (isExpanded) {
        AppTypographyTokens(
            display = TextStyle(fontSize = 40.sp, fontWeight = FontWeight.Bold, lineHeight = 48.sp),
            title = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.SemiBold, lineHeight = 32.sp),
            body = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Normal, lineHeight = 26.sp),
            label = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, lineHeight = 20.sp)
        )
    } else if (isMedium) {
        AppTypographyTokens(
            display = TextStyle(fontSize = 36.sp, fontWeight = FontWeight.Bold, lineHeight = 44.sp),
            title = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.SemiBold, lineHeight = 30.sp),
            body = TextStyle(fontSize = 17.sp, fontWeight = FontWeight.Normal, lineHeight = 25.sp),
            label = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Medium, lineHeight = 18.sp)
        )
    } else {
        AppTypographyTokens(
            display = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.Bold, lineHeight = 36.sp),
            title = TextStyle(fontSize = 17.sp, fontWeight = FontWeight.SemiBold, lineHeight = 24.sp),
            body = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal, lineHeight = 20.sp),
            label = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Medium, lineHeight = 14.sp)
        )
    }
    
    val grids = if (isExpanded) {
        AppGrids(
            calculatorColumns = 4,
            inputColumns = 3,
            scheduleColumns = 2
        )
    } else if (isMedium) {
        AppGrids(
            calculatorColumns = 3,
            inputColumns = 2,
            scheduleColumns = 1
        )
    } else {
        AppGrids(
            calculatorColumns = 2,
            inputColumns = 1,
            scheduleColumns = 1
        )
    }

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
