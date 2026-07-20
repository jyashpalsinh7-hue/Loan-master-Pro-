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
import androidx.compose.ui.zIndex
import androidx.compose.foundation.background
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
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.TopCenter) {
        BoxWithConstraints(modifier = modifier.widthIn(max = 840.dp)) {
            content()
            if (showDiagnostics) {
                GridDiagnosticOverlay()
            }
        }
    }
}

@Composable
fun AdaptiveRowCol(
    modifier: Modifier = Modifier,
    columns: Int = LoanMasterTheme.grids.inputColumns,
    content1: @Composable (Modifier) -> Unit,
    content2: @Composable (Modifier) -> Unit
) {
    BoxWithConstraints(modifier = modifier) {
        // Calculate the actual minimum width one field needs to render without
        // clipping, based on real design tokens rather than a guessed constant.
        // This makes the 2-column decision correct regardless of how much padding
        // a given screen applies upstream before reaching this composable.
        //
        // Components summed:
        //   - leading icon width (LoanMasterTheme.components.iconSmall)
        //   - spacing between icon and label text (LoanMasterTheme.spacing.sm)
        //   - estimated rendered width of the longest known field label
        //     ("Monthly Income", 15 characters), approximated at roughly 0.55x
        //     the font size per character for this font family/weight — a
        //     reasonable estimate for Latin proportional text, not a precise
        //     text measurement, but far more grounded than an arbitrary dp guess
        //   - internal OutlinedTextField horizontal content padding (~24dp,
        //     Material 3's default start+end content inset)
        //   - a small safety buffer so text isn't touching the field's edge
        val density = LocalDensity.current
        val longestLabelCharCount = 15 // "Monthly Income"
        val estimatedCharWidth = with(density) {
            (LoanMasterTheme.typography.body.fontSize.toPx() * 0.55f).toDp()
        }
        val estimatedLabelWidth = estimatedCharWidth * longestLabelCharCount
        val fieldInternalPadding = 24.dp
        val safetyBuffer = 12.dp

        val minWidthPerField = LoanMasterTheme.components.iconSmall +
            LoanMasterTheme.spacing.sm +
            estimatedLabelWidth +
            fieldInternalPadding +
            safetyBuffer

        // Two fields side by side also need the gutter between them, plus this
        // composable's own horizontal space is shared 50/50 by weight(1f), so
        // the true minimum LOCAL width needed for 2-column is roughly double
        // one field's minimum, plus the gutter.
        val minWidthForTwoColumns = (minWidthPerField * 2) + LoanMasterTheme.spacing.gridGutter

        val effectiveColumns = if (maxWidth >= minWidthForTwoColumns) 2 else 1

        if (effectiveColumns == 1) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.md)
            ) {
                content1(Modifier.fillMaxWidth())
                content2(Modifier.fillMaxWidth())
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.gridGutter)
            ) {
                content1(Modifier.weight(1f))
                content2(Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun AdaptiveRowCol3(
    modifier: Modifier = Modifier,
    columns: Int = LoanMasterTheme.grids.inputColumns,
    content1: @Composable (Modifier) -> Unit,
    content2: @Composable (Modifier) -> Unit,
    content3: @Composable (Modifier) -> Unit
) {
    BoxWithConstraints(modifier = modifier) {
        val minWidthForThreeColumns = 560.dp
        val effectiveColumns = if (maxWidth >= minWidthForThreeColumns) 3 else columns

        if (effectiveColumns < 3) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
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
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.gridGutter)
            ) {
                content1(Modifier.weight(1f))
                content2(Modifier.weight(1f))
                content3(Modifier.weight(1f))
            }
        }
    }
}
