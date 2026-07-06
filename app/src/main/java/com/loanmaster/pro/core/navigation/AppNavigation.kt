package com.loanmaster.pro.core.navigation

import com.loanmaster.pro.getDatabase

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
import com.loanmaster.pro.feature.compare.*
import com.loanmaster.pro.feature.loaneligibility.*
import com.loanmaster.pro.core.responsive.*
import com.loanmaster.pro.feature.home.*
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
import androidx.compose.runtime.getValue
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
import androidx.compose.runtime.remember
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.compositionLocalOf


@Composable
fun AppNavigation(
    historyViewModel: HistoryViewModel,
    loanSummaryViewModel: LoanSummaryViewModel,
    mainViewModel: HomeViewModel,
    settingsViewModel: SettingsViewModel,
    navController: androidx.navigation.NavHostController = rememberNavController()
) {

    val context = androidx.compose.ui.platform.LocalContext.current
    val database = getDatabase(context)
    val historyRepository = remember { HistoryRepository(database.historyDao()) }

    val mainUiState by mainViewModel.uiState.collectAsStateWithLifecycle()
    val activeRoute = mainUiState.activeBottomNavItem
    val selectedHistory = mainUiState.selectedHistory
    
    NavigationSuiteScaffold(
        navigationSuiteItems = {
                            item(
                                icon = { Icon(Icons.Rounded.Home, contentDescription = "Home") },
                                label = { Text("Home", maxLines = 1) },
                                selected = activeRoute == "home",
                                onClick = { 
                                    mainViewModel.updateActiveBottomNavItem("home")
                                    navController.navigate("home") {
                                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                            item(
                                icon = { Icon(Icons.Rounded.Calculate, contentDescription = "EMI") },
                                label = { Text("EMI", maxLines = 1) },
                                selected = activeRoute == "emi",
                                onClick = { 
                                    mainViewModel.updateActiveBottomNavItem("emi")
                                    navController.navigate("emi") {
                                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                            item(
                                icon = { Icon(Icons.AutoMirrored.Rounded.TrendingUp, contentDescription = "SIP") },
                                label = { Text("SIP", maxLines = 1) },
                                selected = activeRoute == "sip",
                                onClick = { 
                                    mainViewModel.updateActiveBottomNavItem("sip")
                                    navController.navigate("sip") {
                                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                            item(
                                icon = { Icon(Icons.AutoMirrored.Rounded.ReceiptLong, contentDescription = "GST") },
                                label = { Text("GST", maxLines = 1) },
                                selected = activeRoute == "gst",
                                onClick = { 
                                    mainViewModel.updateActiveBottomNavItem("gst")
                                    navController.navigate("gst") {
                                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                            item(
                                icon = { Icon(Icons.Rounded.History, contentDescription = "History") },
                                label = { Text("History", maxLines = 1) },
                                selected = activeRoute == "history",
                                onClick = { 
                                    mainViewModel.updateActiveBottomNavItem("history")
                                    navController.navigate("history") {
                                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        },
                        containerColor = NavBackground
                    ) {
                        NavHost(navController = navController, startDestination = "home") {
                    composable("home") { 
                        val historyUiState by historyViewModel.uiState.collectAsStateWithLifecycle()
                        val historyList = historyUiState.historyList
                        val summaryUiState by loanSummaryViewModel.uiState.collectAsStateWithLifecycle()
                        val activeLoans = summaryUiState.activeLoans
                        HomeScreen(
                            onNavigateToEmi = { navController.navigate("emi") }, 
                            onNavigateToCompare = { navController.navigate("compare") }, 
                            onNavigateToSip = { navController.navigate("sip") }, 
                            onNavigateToGst = { navController.navigate("gst") }, 
                            onNavigateToRd = { navController.navigate("rd") }, 
                            onNavigateToFd = { navController.navigate("fd") }, 
                            onNavigateToCurrency = { navController.navigate("currency") }, 
                            onNavigateToEligibility = { navController.navigate("eligibility") }, 
                            onNavigateToPrepayment = { navController.navigate("prepayment") }, 
                            onNavigateToSettings = { navController.navigate("settings") },
                            onNavigateBottomNav = { route ->
                                mainViewModel.updateActiveBottomNavItem(route)
                                navController.navigate(route) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            onNavigateToHistory = {
                                mainViewModel.updateActiveBottomNavItem("history")
                                navController.navigate("history") {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            onNavigateToLoanSummary = { navController.navigate("loan_summary") },
                            onNavigateToCalculator = { history -> 
                                mainViewModel.setSelectedHistory(history)
                                navController.navigate(history.calculatorType.lowercase())
                            },

                            historyItems = historyList,
                            viewModel = mainViewModel,
                            activeLoans = activeLoans
                        ) 
                    }
                    composable("loan_summary") { 
                        LoanSummaryScreen(
                            viewModel = loanSummaryViewModel, 
                            onBack = { navController.popBackStack() }
                        ) 
                    }
                    composable("emi") {
                        val emiViewModel: EmiViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                            factory = EmiCalculatorViewModelFactory(historyRepository)
                        )
                        EmiScreen(
                            onNavigateBack = { navController.popBackStack() }, 
                            historyViewModel = historyViewModel,
                            initialHistory = selectedHistory?.takeIf { it.calculatorType == "EMI" },
                            onHistoryConsumed = { mainViewModel.clearSelectedHistory() },
                            viewModel = emiViewModel
                        )
                    }
                    composable("history") { 
                        HistoryScreen(
                            viewModel = historyViewModel, 
                            onNavigateBack = { navController.popBackStack() },
                            onNavigateToCalculator = { history -> 
                                mainViewModel.setSelectedHistory(history)
                                navController.navigate(history.calculatorType.lowercase())
                            },
                            onNavigateBottomNav = { route ->
                                mainViewModel.updateActiveBottomNavItem(route)
                                navController.navigate(route) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            activeBottomNavItem = activeRoute
                        ) 
                    }
                    composable("compare") { CompareScreen(onNavigateBack = { navController.popBackStack() }) }
                    composable("sip") { 
                        SipScreen(
                            onNavigateBack = { navController.popBackStack() },
                            historyViewModel = historyViewModel,
                            initialHistory = selectedHistory?.takeIf { it.calculatorType == "SIP" },
                            onHistoryConsumed = { mainViewModel.clearSelectedHistory() }
                        ) 
                    }
                    composable("rd") { 
                        RdScreen(
                            onNavigateBack = { navController.popBackStack() },
                            historyViewModel = historyViewModel,
                            initialHistory = selectedHistory?.takeIf { it.calculatorType == "RD" },
                            onHistoryConsumed = { mainViewModel.clearSelectedHistory() }
                        ) 
                    }
                    composable("fd") {
                        val fdViewModel: FdViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                            factory = FdCalculatorViewModelFactory(historyRepository)
                        )
                        FdScreen(
                            onNavigateBack = { navController.popBackStack() },
                            historyViewModel = historyViewModel,
                            initialHistory = selectedHistory?.takeIf { it.calculatorType == "FD" },
                            onHistoryConsumed = { mainViewModel.clearSelectedHistory() },
                            viewModel = fdViewModel
                        )
                    }
                    composable("gst") { 
                        GstScreen(
                            onNavigateBack = { navController.popBackStack() },
                            historyViewModel = historyViewModel,
                            initialHistory = selectedHistory?.takeIf { it.calculatorType == "GST" },
                            onHistoryConsumed = { mainViewModel.clearSelectedHistory() }
                        ) 
                    }
                    composable("currency") { CurrencyScreen(onNavigateBack = { navController.popBackStack() }) }
                    composable("eligibility") { LoanEligibilityScreen() }
                    composable("prepayment") { 
                        PrepaymentScreen(
                            onNavigateBack = { navController.popBackStack() },
                            historyViewModel = historyViewModel,
                            initialHistory = selectedHistory?.takeIf { it.calculatorType == "Prepayment" },
                            onHistoryConsumed = { mainViewModel.clearSelectedHistory() }
                        ) 
                    }
                    composable("settings") { 
                        SettingsScreen(
                            onNavigateBack = { navController.popBackStack() }, 
                            viewModel = settingsViewModel,
                            onClearHistory = { historyViewModel.clearAll() },
                            onNavigateBottomNav = { route ->
                                mainViewModel.updateActiveBottomNavItem(route)
                                navController.navigate(route) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            activeBottomNavItem = activeRoute
                        ) 
                    }
                }

}
}
