package com.loanmaster.pro.core.responsive

import com.loanmaster.pro.domain.model.*
import com.loanmaster.pro.feature.gst.*
import com.loanmaster.pro.feature.sip.*
import com.loanmaster.pro.core.ui.*
import com.loanmaster.pro.feature.history.*
import com.loanmaster.pro.core.theme.*
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
import com.loanmaster.pro.feature.home.*

import androidx.compose.foundation.layout.*
import androidx.window.core.layout.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.LayoutDirection

val LocalWindowSizeClass = staticCompositionLocalOf<WindowWidthSizeClass> { WindowWidthSizeClass.COMPACT }

@Composable
fun ProvideResponsive(
    windowSizeClass: WindowWidthSizeClass = WindowWidthSizeClass.COMPACT,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalWindowSizeClass provides windowSizeClass) {
        content()
    }
}

val Number.sw: Dp
    @Composable
    get() {
        val config = LocalConfiguration.current
        val insets = WindowInsets.safeDrawing.asPaddingValues()
        val width = config.screenWidthDp.dp - insets.calculateLeftPadding(LayoutDirection.Ltr) - insets.calculateRightPadding(LayoutDirection.Ltr)
        return width * (this.toDouble() / 100.0).toFloat()
    }

val Number.sh: Dp
    @Composable
    get() {
        val config = LocalConfiguration.current
        val insets = WindowInsets.safeDrawing.asPaddingValues()
        val height = config.screenHeightDp.dp - insets.calculateTopPadding() - insets.calculateBottomPadding()
        return height * (this.toDouble() / 100.0).toFloat()
    }

@Composable
fun ResponsiveScreenWrapper(
    modifier: Modifier = Modifier,
    showDiagnostics: Boolean = false,
    content: @Composable BoxWithConstraintsScope.() -> Unit
) {
    BoxWithConstraints(modifier = modifier) {
        content()
        if (showDiagnostics) {
            GridDiagnosticOverlay()
        }
    }
}

@Composable
fun AdaptiveRowCol(
    modifier: Modifier = Modifier,
    columns: Int = LoanMasterTheme.grids.calculatorColumns,
    content1: @Composable (Modifier) -> Unit,
    content2: @Composable (Modifier) -> Unit
) {
    if (columns == 1) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.md)
        ) {
            content1(Modifier.fillMaxWidth())
            content2(Modifier.fillMaxWidth())
        }
    } else {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.gridGutter)
        ) {
            content1(Modifier.weight(1f))
            content2(Modifier.weight(1f))
        }
    }
}

@Composable
fun AdaptiveRowCol3(
    modifier: Modifier = Modifier,
    columns: Int = LoanMasterTheme.grids.calculatorColumns,
    content1: @Composable (Modifier) -> Unit,
    content2: @Composable (Modifier) -> Unit,
    content3: @Composable (Modifier) -> Unit
) {
    if (columns < 3) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.md)
        ) {
            content1(Modifier.fillMaxWidth())
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.gridGutter)
            ) {
                content2(Modifier.weight(1f))
                content3(Modifier.weight(1f))
            }
        }
    } else {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.gridGutter)
        ) {
            content1(Modifier.weight(1f))
            content2(Modifier.weight(1f))
            content3(Modifier.weight(1f))
        }
    }
}
