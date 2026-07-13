package com.loanmaster.pro

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

import com.loanmaster.pro.core.responsive.*

import com.loanmaster.pro.feature.home.*

import androidx.window.core.layout.WindowWidthSizeClass

import android.os.Bundle

import androidx.activity.ComponentActivity

import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

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

import androidx.navigation.compose.composable

import androidx.navigation.compose.rememberNavController

import androidx.lifecycle.compose.collectAsStateWithLifecycle

import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold

import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType

import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults

import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo

import androidx.compose.runtime.compositionLocalOf

val LocalLanguage = compositionLocalOf { "English" }

val LocalCurrency = compositionLocalOf { "INR (₹)" }


val LocalCurrencySymbol = compositionLocalOf { "₹" }

val LocalNotificationsEnabled = compositionLocalOf { true }

val LocalKeepHistoryEnabled = compositionLocalOf { true }
@Volatile
private var APP_DATABASE_INSTANCE: LoanMasterDatabase? = null

fun getDatabase(context: android.content.Context): LoanMasterDatabase {
    return APP_DATABASE_INSTANCE ?: synchronized(LoanMasterDatabase::class.java) {
        APP_DATABASE_INSTANCE ?: androidx.room.Room.databaseBuilder(
            context.applicationContext,
            LoanMasterDatabase::class.java,
            "loan_master_database"
        ).fallbackToDestructiveMigration().addMigrations(com.loanmaster.pro.data.local.room.MIGRATION_3_4, com.loanmaster.pro.data.local.room.MIGRATION_4_5).build().also { APP_DATABASE_INSTANCE = it }
    }
}

class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        
        super.onCreate(savedInstanceState)
        
        
        enableEdgeToEdge()
        
        setContent {
            val settingsViewModel: SettingsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
            val uiState by settingsViewModel.uiState.collectAsStateWithLifecycle()
            val language = uiState.language
            val currency = uiState.currency
            
            // Extract the symbol from format "USD ($)" and update globally
            val symbol = currency.substringAfter("(").substringBefore(")")
            com.loanmaster.pro.core.formatter.CurrencyHelper.currencySymbol = symbol
            
            val notificationsEnabled = uiState.notificationsEnabled
            val keepHistoryEnabled = uiState.keepHistoryEnabled
            val configuration = androidx.compose.ui.platform.LocalConfiguration.current
            
            val windowSizeClass = when {
                configuration.screenWidthDp < 600 -> androidx.window.core.layout.WindowWidthSizeClass.COMPACT
                configuration.screenWidthDp < 840 -> androidx.window.core.layout.WindowWidthSizeClass.MEDIUM
                else -> androidx.window.core.layout.WindowWidthSizeClass.EXPANDED
            }
            
            androidx.compose.runtime.CompositionLocalProvider(
                LocalLanguage provides language,
                LocalCurrency provides currency,
                LocalCurrencySymbol provides symbol,
                LocalNotificationsEnabled provides notificationsEnabled,
                LocalKeepHistoryEnabled provides keepHistoryEnabled
            ) {
                
                LoanMasterTheme(windowSizeClass = windowSizeClass) {
                    // FIX: Removed key(currency) wrapper
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
                    
                    val mainViewModel: HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
                                                            
                    
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
}// Force refresh
// Reinstall trigger
// trigger re-build
