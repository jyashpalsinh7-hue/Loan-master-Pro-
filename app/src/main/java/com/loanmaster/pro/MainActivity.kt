package com.loanmaster.pro

import com.loanmaster.pro.ui.theme.LoanMasterTheme

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
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

val LocalLanguage = compositionLocalOf { "English" }
val LocalCurrency = compositionLocalOf { "INR (₹)" }
val LocalNotificationsEnabled = compositionLocalOf { true }
val LocalKeepHistoryEnabled = compositionLocalOf { true }

@Volatile
private var APP_DATABASE_INSTANCE: LoanMasterDatabase? = null

fun getDatabase(context: android.content.Context): LoanMasterDatabase {
    return APP_DATABASE_INSTANCE ?: synchronized(Any()) {
        val instance = androidx.room.Room.databaseBuilder(
            context.applicationContext,
            LoanMasterDatabase::class.java,
            "loan_master_database"
        ).fallbackToDestructiveMigration().build()
        APP_DATABASE_INSTANCE = instance
        instance
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settingsViewModel: SettingsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
            val language by settingsViewModel.language.collectAsStateWithLifecycle()
            val currency by settingsViewModel.currency.collectAsStateWithLifecycle()
            val notificationsEnabled by settingsViewModel.notificationsEnabled.collectAsStateWithLifecycle()
            val keepHistoryEnabled by settingsViewModel.keepHistoryEnabled.collectAsStateWithLifecycle()


            val configuration = androidx.compose.ui.platform.LocalConfiguration.current
            val windowSizeClass = when {
                configuration.screenWidthDp < 600 -> com.loanmaster.pro.WindowWidthSizeClass.Compact
                configuration.screenWidthDp < 840 -> com.loanmaster.pro.WindowWidthSizeClass.Medium
                else -> com.loanmaster.pro.WindowWidthSizeClass.Expanded
            }

            androidx.compose.runtime.CompositionLocalProvider(
                LocalLanguage provides language,
                LocalCurrency provides currency,
                LocalNotificationsEnabled provides notificationsEnabled,
                LocalKeepHistoryEnabled provides keepHistoryEnabled
            ) {
                LoanMasterTheme(windowSizeClass = windowSizeClass) {
                    val context = androidx.compose.ui.platform.LocalContext.current
                    val database = getDatabase(context)
                    val repository = HistoryRepository(database.historyDao())
                    val activeLoanRepository = ActiveLoanRepository(database.activeLoanDao())
                    val settingsRepository = SettingsRepository(context)

                    val historyViewModel: HistoryViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                        factory = HistoryViewModelFactory(repository, settingsRepository)
                    )
                    
                    // Prepopulate database with 5 items per calculator if empty
                    LaunchedEffect(Unit) {
                        DatabasePrepopulator.prepopulateIfEmpty(context, repository)
                    }

                    val loanSummaryViewModel: LoanSummaryViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                        factory = LoanSummaryViewModelFactory(activeLoanRepository)
                    )
                    val navController = rememberNavController()
                    val mainViewModel: MainViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
                    val activeRoute by mainViewModel.activeBottomNavItem.collectAsStateWithLifecycle()
                    val selectedHistory by mainViewModel.selectedHistory.collectAsStateWithLifecycle()
                    
                    val adaptiveInfo = currentWindowAdaptiveInfo()
                    

                    
                    AppNavigation(
                        historyViewModel = historyViewModel,
                        loanSummaryViewModel = loanSummaryViewModel,
                        mainViewModel = mainViewModel,
                        settingsViewModel = settingsViewModel,
                        navController = navController
                    )
                }
            }
        }
    }
}
