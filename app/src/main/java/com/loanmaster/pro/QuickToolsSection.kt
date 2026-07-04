package com.loanmaster.pro
import com.loanmaster.pro.ui.theme.LoanMasterTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.material.icons.rounded.*
import androidx.compose.material.icons.automirrored.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.focus.onFocusChanged
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.loanmaster.pro.ui.theme.*
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.window.core.layout.WindowWidthSizeClass as WindowWidthSizeClassCore
import androidx.compose.runtime.compositionLocalOf

@Composable
fun QuickToolsSection(isExpanded: Boolean, onToggleExpand: () -> Unit) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = LoanMasterTheme.spacing.md),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Quick Tools",
                color = TextPrimary,
                style = LoanMasterTheme.typography.title,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (isExpanded) "Show Less" else "View All >",
                color = AccentBlue,
                style = LoanMasterTheme.typography.body,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable { onToggleExpand() }.padding(LoanMasterTheme.spacing.xs)
            )
        }

        if (isExpanded) {
            val columns = LoanMasterTheme.grids.calculatorColumns
            val items = listOf<@Composable () -> Unit>(
                { QuickToolItem("Inflation\nCalculator", Icons.AutoMirrored.Rounded.TrendingUp, Color(0xFF4CAF50)) },
                { QuickToolItem("Retirement\nCalculator", Icons.Rounded.Chair, Color(0xFF9C27B0)) },
                { QuickToolItem("Net Worth\nTracker", Icons.Rounded.PieChart, Color(0xFFFF9800)) },
                { QuickToolItem("EMI Schedule\nGenerator", Icons.Rounded.CalendarMonth, Color(0xFF2196F3)) },
                { QuickToolItem("Interest Rate\nTrends", Icons.Rounded.SsidChart, Color(0xFF00BCD4)) },
                { QuickToolItem("Tax\nCalculator", Icons.Rounded.AccountBalance, Color(0xFFE91E63)) }
            )

            val chunkedItems = items.chunked(columns)
            
            Column(verticalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.lg)) {
                for (rowItems in chunkedItems) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.gridGutter)
                    ) {
                        for (item in rowItems) {
                            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                item()
                            }
                        }
                        for (i in 0 until (columns - rowItems.size)) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.lg)
            ) {
                QuickToolItem("Inflation\nCalculator", Icons.AutoMirrored.Rounded.TrendingUp, Color(0xFF4CAF50))
                QuickToolItem("Retirement\nCalculator", Icons.Rounded.Chair, Color(0xFF9C27B0))
                QuickToolItem("Net Worth\nTracker", Icons.Rounded.PieChart, Color(0xFFFF9800))
                QuickToolItem("EMI Schedule\nGenerator", Icons.Rounded.CalendarMonth, Color(0xFF2196F3))
                QuickToolItem("Interest Rate\nTrends", Icons.Rounded.SsidChart, Color(0xFF00BCD4))
            }
        }
    }
}

@Composable
fun QuickToolItem(title: String, icon: ImageVector, iconColor: Color) {
    val width = if (LoanMasterTheme.components.iconLarge > LoanMasterTheme.spacing.xl) LoanMasterTheme.components.calculatorCardHeight else 80.dp
    val circleSize = LoanMasterTheme.components.iconLarge + LoanMasterTheme.spacing.gridGutter
    val context = androidx.compose.ui.platform.LocalContext.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.widthIn(min = width).clickable { 
            android.widget.Toast.makeText(context, "$title is Coming Soon!", android.widget.Toast.LENGTH_SHORT).show()
        }.testTag("quick_tool_${title.replace("\n", "_").lowercase()}")
    ) {
        Box(
            modifier = Modifier
                .size(circleSize)
                .clip(CircleShape)
                .background(iconColor.copy(alpha = 0.15f))
                .border(1.dp, iconColor.copy(alpha = 0.3f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(LoanMasterTheme.components.iconMedium)
            )
        }
        Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.sm))
        Text(
            text = title,
            color = TextSecondary,
            style = LoanMasterTheme.typography.label,
            textAlign = TextAlign.Center,
            lineHeight = LoanMasterTheme.typography.label.fontSize,
            maxLines = 2
        )
    }
}

