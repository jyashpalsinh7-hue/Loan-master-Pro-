package com.example

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
import com.example.ui.theme.*

import androidx.compose.runtime.compositionLocalOf

val LocalLanguage = compositionLocalOf { "English" }
val LocalCurrency = compositionLocalOf { "INR (₹)" }
val LocalNotificationsEnabled = compositionLocalOf { true }
val LocalKeepHistoryEnabled = compositionLocalOf { true }

@Volatile
private var APP_DATABASE_INSTANCE: AppDatabase? = null

fun getDatabase(context: android.content.Context): AppDatabase {
    return APP_DATABASE_INSTANCE ?: synchronized(Any()) {
        val instance = androidx.room.Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "loan_master_database"
        ).build()
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

            globalCurrencySymbol = extractCurrencySymbol(currency)

            androidx.compose.runtime.CompositionLocalProvider(
                LocalLanguage provides language,
                LocalCurrency provides currency,
                LocalNotificationsEnabled provides notificationsEnabled,
                LocalKeepHistoryEnabled provides keepHistoryEnabled
            ) {
                MyApplicationTheme {
                    val context = androidx.compose.ui.platform.LocalContext.current
                    val database = getDatabase(context)
                    val repository = HistoryRepository(database.historyDao())
                    val settingsRepository = SettingsRepository(context)
                    val historyViewModel: HistoryViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                        factory = HistoryViewModelFactory(repository, settingsRepository)
                    )
                    val navController = rememberNavController()
                    val mainViewModel: MainViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
                    val activeRoute by mainViewModel.activeBottomNavItem.collectAsStateWithLifecycle()
                    val selectedHistory by mainViewModel.selectedHistory.collectAsStateWithLifecycle()
                    
                NavHost(navController = navController, startDestination = "home") {
                    composable("home") { 
                        val historyList by historyViewModel.uiState.collectAsStateWithLifecycle()
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
                            historyCount = historyList.size,
                            viewModel = mainViewModel
                        ) 
                    }
                    composable("emi") { 
                        EmiCalculatorScreen(
                            onNavigateBack = { navController.popBackStack() }, 
                            historyViewModel = historyViewModel,
                            initialHistory = selectedHistory?.takeIf { it.calculatorType == "EMI" },
                            onHistoryConsumed = { mainViewModel.clearSelectedHistory() }
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
                    composable("compare") { LoanComparisonScreen(onNavigateBack = { navController.popBackStack() }) }
                    composable("sip") { 
                        SipCalculatorScreen(
                            onNavigateBack = { navController.popBackStack() },
                            historyViewModel = historyViewModel,
                            initialHistory = selectedHistory?.takeIf { it.calculatorType == "SIP" },
                            onHistoryConsumed = { mainViewModel.clearSelectedHistory() }
                        ) 
                    }
                    composable("rd") { 
                        RdCalculatorScreen(
                            onNavigateBack = { navController.popBackStack() },
                            historyViewModel = historyViewModel,
                            initialHistory = selectedHistory?.takeIf { it.calculatorType == "RD" },
                            onHistoryConsumed = { mainViewModel.clearSelectedHistory() }
                        ) 
                    }
                    composable("fd") { 
                        FdCalculatorScreen(
                            onNavigateBack = { navController.popBackStack() },
                            historyViewModel = historyViewModel,
                            initialHistory = selectedHistory?.takeIf { it.calculatorType == "FD" },
                            onHistoryConsumed = { mainViewModel.clearSelectedHistory() }
                        ) 
                    }
                    composable("gst") { 
                        GstCalculatorScreen(
                            onNavigateBack = { navController.popBackStack() },
                            historyViewModel = historyViewModel,
                            initialHistory = selectedHistory?.takeIf { it.calculatorType == "GST" },
                            onHistoryConsumed = { mainViewModel.clearSelectedHistory() }
                        ) 
                    }
                    composable("currency") { CurrencyConverterScreen(onNavigateBack = { navController.popBackStack() }) }
                    composable("eligibility") { LoanEligibilityScreen() }
                    composable("prepayment") { 
                        PrepaymentCalculatorScreen(
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
        }
    }
}

@Composable
fun HomeScreen(onNavigateToEmi: () -> Unit, onNavigateToCompare: () -> Unit, onNavigateToSip: () -> Unit, onNavigateToGst: () -> Unit, onNavigateToRd: () -> Unit, onNavigateToFd: () -> Unit, onNavigateToCurrency: () -> Unit, onNavigateToEligibility: () -> Unit, onNavigateToPrepayment: () -> Unit = {}, onNavigateToSettings: () -> Unit = {}, onNavigateBottomNav: (String) -> Unit = {}, onNavigateToHistory: () -> Unit = {}, historyCount: Int = 0, viewModel: MainViewModel) {
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val activeBottomNavItem by viewModel.activeBottomNavItem.collectAsStateWithLifecycle()
    val isQuickToolsExpanded by viewModel.isQuickToolsExpanded.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { AppTopBar(onNavigateToSettings) },
        bottomBar = { AppBottomBar(selectedRoute = activeBottomNavItem, onNavClick = onNavigateBottomNav) },
        containerColor = BackgroundDark
    ) { innerPadding ->
        ResponsiveScreenWrapper(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundDark)
                .padding(innerPadding)
        ) {
            val columns = LoanMasterTheme.grids.calculatorColumns

            androidx.compose.foundation.lazy.grid.LazyVerticalGrid(
                columns = androidx.compose.foundation.lazy.grid.GridCells.Adaptive(minSize = 150.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = LoanMasterTheme.spacing.screenPadding, vertical = LoanMasterTheme.spacing.sm)
                    .imePadding(),
                verticalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.sm),
                horizontalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.sm)
            ) {
                item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) {
                    SearchAndPremiumRow(searchQuery, onSearchQueryChange = { viewModel.updateSearchQuery(it) })
                }
                item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) {
                    HeroBanner()
                }
                
                // Calculators Header
                item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) {
                    CalculatorsSectionHeader()
                }
                
                // Calculator Cards
                item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) {
                    EmiCalculatorCard(onNavigateToEmi)
                }
                item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(1) }) {
                    StandardCalculatorCard("Loan Compare", "Compare 2-4 loans", Icons.Rounded.Balance, Color(0xFF8E24AA), badge = "New", onClick = onNavigateToCompare)
                }
                item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(1) }) {
                    StandardCalculatorCard("SIP Calculator", "Plan your SIP & grow wealth", Icons.Rounded.TrendingUp, Color(0xFF43A047), onClick = onNavigateToSip)
                }
                item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(1) }) {
                    StandardCalculatorCard("GST Calculator", "Add or remove GST easily", Icons.Rounded.Receipt, Color(0xFFE53935), onClick = onNavigateToGst)
                }
                item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(1) }) {
                    StandardCalculatorCard("RD Calculator", "Calculate Recurring Deposit", Icons.Rounded.CalendarToday, Color(0xFFFF9800), onClick = onNavigateToRd)
                }
                item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(1) }) {
                    StandardCalculatorCard("Currency Converter", "Live rates & conversion", Icons.Rounded.CurrencyExchange, Color(0xFF00ACC1), onClick = onNavigateToCurrency)
                }
                item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(1) }) {
                    StandardCalculatorCard("Loan Eligibility", "Check eligibility quickly", Icons.Rounded.PersonSearch, Color(0xFF1E88E5), badge = "Premium", onClick = onNavigateToEligibility)
                }
                item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(1) }) {
                    StandardCalculatorCard("FD Calculator", "Calculate FD returns", Icons.Rounded.Savings, Color(0xFFD81B60), onClick = onNavigateToFd)
                }
                item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(1) }) {
                    StandardCalculatorCard("Loan Prepayment", "Check interest saved", Icons.Rounded.EditNote, Color(0xFF5E35B1), onClick = onNavigateToPrepayment)
                }
                item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(1) }) {
                    StandardCalculatorCard("Loan Summary", "View active loans", Icons.Rounded.Summarize, Color(0xFF1E88E5))
                }
                item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(1) }) {
                    StandardCalculatorCard("History", "Recent calculations", Icons.Rounded.History, Color(0xFF607D8B), onClick = onNavigateToHistory)
                }

                item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) {
                    RecentCalculationsBanner(historyCount, onNavigateToHistory)
                }
                item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) {
                    QuickToolsSection(isQuickToolsExpanded, onToggleExpand = { viewModel.toggleQuickToolsExpanded() })
                }
                item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) {
                    Spacer(modifier = Modifier.height(LoanMasterTheme.spacing.xl))
                }
            }
        }
    }
}

@Composable
fun AppTopBar(onNavigateToSettings: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = LoanMasterTheme.spacing.screenPadding, vertical = LoanMasterTheme.spacing.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Rounded.Menu,
            contentDescription = "Menu",
            tint = TextPrimary,
            modifier = Modifier
                .size(LoanMasterTheme.components.iconMedium)
                .testTag("menu_icon")
                .clickable { onNavigateToSettings() }
        )
        Spacer(modifier = Modifier.width(LoanMasterTheme.spacing.md))
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(LoanMasterTheme.components.buttonHeight)
                    .clip(CircleShape)
                    .border(2.dp, AccentYellow, CircleShape)
                    .background(BackgroundDark),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.CurrencyRupee,
                    contentDescription = null,
                    tint = AccentBlue,
                    modifier = Modifier.size(LoanMasterTheme.components.iconMedium)
                )
            }
            Spacer(modifier = Modifier.width(LoanMasterTheme.spacing.sm))
            Column {
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = LoanMasterTheme.typography.title.fontSize)) {
                            append("LoanMaster ")
                        }
                        withStyle(style = SpanStyle(color = AccentYellow, fontWeight = FontWeight.Bold, fontSize = LoanMasterTheme.typography.title.fontSize)) {
                            append("Pro")
                        }
                    }
                )
                Text(
                    text = "Smart Finance Calculator",
                    color = TextSecondary,
                    style = LoanMasterTheme.typography.label
                )
            }
        }
    }
}

@Composable
fun SearchAndPremiumRow(searchQuery: String, onSearchQueryChange: (String) -> Unit) {
    var isFocused by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
    val borderColor by androidx.compose.animation.animateColorAsState(targetValue = if (isFocused) AccentBlue else CardStroke)
    val glowAlpha by androidx.compose.animation.core.animateFloatAsState(targetValue = if (isFocused) 0.5f else 0f)
    
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = LoanMasterTheme.spacing.md),
        horizontalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Search Bar
        Row(
            modifier = Modifier
                .weight(1f)
                .height(LoanMasterTheme.components.topAppBarHeight)
                .shadow(if (isFocused) 8.dp else 0.dp, RoundedCornerShape(LoanMasterTheme.components.cardRadius), spotColor = AccentBlue.copy(alpha = glowAlpha))
                .clip(RoundedCornerShape(LoanMasterTheme.components.cardRadius))
                .background(SurfaceDark)
                .border(1.dp, borderColor, RoundedCornerShape(LoanMasterTheme.components.cardRadius))
                .padding(horizontal = LoanMasterTheme.spacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Rounded.Search,
                contentDescription = "Search",
                tint = if (isFocused) AccentBlue else TextSecondary,
                modifier = Modifier.size(LoanMasterTheme.components.iconMedium)
            )
            Spacer(modifier = Modifier.width(LoanMasterTheme.spacing.sm))
            androidx.compose.foundation.text.BasicTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                textStyle = LoanMasterTheme.typography.body.copy(color = TextPrimary),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .wrapContentHeight(Alignment.CenterVertically)
                    .onFocusChanged { isFocused = it.isFocused },
                decorationBox = { innerTextField ->
                    if (searchQuery.isEmpty()) {
                        Text(
                            text = "Search calculators...",
                            color = TextSecondary,
                            style = LoanMasterTheme.typography.body
                        )
                    }
                    innerTextField()
                },
                cursorBrush = Brush.verticalGradient(listOf(AccentBlue, AccentBlue))
            )
        }

        // Premium Button
        OutlinedButton(
            onClick = { },
            modifier = Modifier
                .height(LoanMasterTheme.components.topAppBarHeight)
                .testTag("premium_button"),
            shape = RoundedCornerShape(LoanMasterTheme.components.cardRadius),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = AccentYellow.copy(alpha = 0.1f),
                contentColor = AccentYellow
            ),
            border = androidx.compose.foundation.BorderStroke(1.dp, AccentYellow),
            contentPadding = PaddingValues(horizontal = LoanMasterTheme.spacing.md)
        ) {
            Icon(
                imageVector = Icons.Rounded.WorkspacePremium,
                contentDescription = null,
                modifier = Modifier.size(LoanMasterTheme.components.iconSmall)
            )
            Spacer(modifier = Modifier.width(LoanMasterTheme.spacing.xs))
            Text(text = "Premium", style = LoanMasterTheme.typography.label, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun HeroBanner() {
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    
    val pagerState = androidx.compose.foundation.pager.rememberPagerState(pageCount = { 3 })
    
    androidx.compose.runtime.LaunchedEffect(pagerState) {
        while (true) {
            kotlinx.coroutines.delay(3000)
            val nextPage = (pagerState.currentPage + 1) % 3
            pagerState.animateScrollToPage(nextPage)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(screenHeight * 0.22f)
            .clip(RoundedCornerShape(LoanMasterTheme.components.cardRadius))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF0B1736),
                        Color(0xFF133282)
                    )
                )
            )
            .border(1.dp, AccentBlue.copy(alpha = 0.5f), RoundedCornerShape(LoanMasterTheme.components.cardRadius))
    ) {
        androidx.compose.foundation.pager.HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .weight(0.6f)
                        .padding(LoanMasterTheme.spacing.lg)
                ) {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = LoanMasterTheme.typography.title.fontSize)) {
                                append(if (page == 0) "Plan Smart, " else if (page == 1) "Zero Ads, " else "Pro Tools, ")
                            }
                            withStyle(style = SpanStyle(color = AccentYellow, fontWeight = FontWeight.Bold, fontSize = LoanMasterTheme.typography.title.fontSize)) {
                                append(if (page == 0) "Save More!" else if (page == 1) "Pure Speed" else "Better Insight")
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(LoanMasterTheme.spacing.sm))
                    Text(
                        text = if (page == 0) "All-in-One Finance Calculator\nfor a Better Financial Future" else if (page == 1) "Upgrade to Premium for an\nuninterrupted ad-free experience" else "Unlock advanced financial\nanalytics and reporting tools",
                        color = TextSecondary,
                        style = LoanMasterTheme.typography.body,
                        lineHeight = LoanMasterTheme.typography.title.fontSize
                    )
                    Spacer(modifier = Modifier.height(LoanMasterTheme.spacing.md))
                    Button(
                        onClick = { },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentYellow, contentColor = BackgroundDark),
                        shape = RoundedCornerShape(LoanMasterTheme.components.cardRadius),
                        contentPadding = PaddingValues(horizontal = LoanMasterTheme.spacing.md, vertical = LoanMasterTheme.spacing.sm),
                        modifier = Modifier.height(LoanMasterTheme.components.buttonHeight).testTag("explore_premium_btn_$page")
                    ) {
                        Text("Explore Premium", style = LoanMasterTheme.typography.label, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(LoanMasterTheme.spacing.xs))
                        Icon(imageVector = Icons.Rounded.WorkspacePremium, contentDescription = null, modifier = Modifier.size(LoanMasterTheme.components.iconSmall))
                    }
                }
                Box(
                    modifier = Modifier
                        .weight(0.4f)
                        .fillMaxHeight()
                        .padding(LoanMasterTheme.spacing.md),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (page == 0) Icons.Rounded.Savings else if (page == 1) Icons.Rounded.Speed else Icons.Rounded.Analytics,
                        contentDescription = null,
                        tint = AccentYellow.copy(alpha = 0.5f),
                        modifier = Modifier.size(LoanMasterTheme.components.topAppBarHeight)
                    )
                }
            }
        }
        
        // Pager dots
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = LoanMasterTheme.spacing.sm),
            horizontalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.xs)
        ) {
            for (i in 0 until 3) {
                Box(
                    modifier = Modifier
                        .size(LoanMasterTheme.spacing.sm)
                        .clip(CircleShape)
                        .background(if (pagerState.currentPage == i) AccentYellow else TextSecondary.copy(alpha = 0.5f))
                )
            }
        }
    }
}

@Composable
fun CalculatorsSectionHeader() {
    Text(
        text = "Calculators",
        color = TextPrimary,
        style = LoanMasterTheme.typography.title,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.fillMaxWidth().padding(top = LoanMasterTheme.spacing.lg, bottom = LoanMasterTheme.spacing.md)
    )
}

@Composable
fun EmiCalculatorCard(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(LoanMasterTheme.components.featuredCardHeight)
            .shadow(elevation = 12.dp, shape = RoundedCornerShape(LoanMasterTheme.components.cardRadius), spotColor = AccentYellow.copy(alpha = 0.4f))
            .clip(RoundedCornerShape(LoanMasterTheme.components.cardRadius))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF1E3A8A), // Blue 900
                        Color(0xFF312E81), // Indigo 900
                        Color(0xFF0F172A)  // Slate 900
                    )
                )
            )
            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(LoanMasterTheme.components.cardRadius))
            .clickable { onClick() }
            .testTag("emi_calculator_card")
    ) {
        // Decorative background elements
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 40.dp, y = (-40).dp)
                .size(120.dp)
                .clip(CircleShape)
                .background(Brush.radialGradient(listOf(Color.White.copy(alpha = 0.1f), Color.Transparent)))
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = (-30).dp, y = 30.dp)
                .size(100.dp)
                .clip(CircleShape)
                .background(Brush.radialGradient(listOf(AccentBlue.copy(alpha = 0.2f), Color.Transparent)))
        )
        
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(LoanMasterTheme.spacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Brush.linearGradient(listOf(Color(0xFF60A5FA), Color(0xFF2563EB))))
                        .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Rounded.Calculate, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                }
                Spacer(modifier = Modifier.height(LoanMasterTheme.spacing.sm))
                Text(
                    text = "EMI Calculator", 
                    color = Color.White, 
                    fontSize = 20.sp, 
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Calculate EMI for Home, Car, Personal & more",
                    color = Color.White.copy(alpha = 0.7f),
                    style = LoanMasterTheme.typography.label,
                    lineHeight = LoanMasterTheme.typography.label.fontSize,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.1f))
                    .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.ChevronRight,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        
        // Popular Tag
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = LoanMasterTheme.spacing.md, end = LoanMasterTheme.spacing.md)
                .clip(RoundedCornerShape(6.dp))
                .background(Brush.linearGradient(listOf(Color(0xFFF59E0B), Color(0xFFD97706))))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = Icons.Rounded.Star, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("POPULAR", color = Color.White, style = LoanMasterTheme.typography.label, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun StandardCalculatorCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: Color,
    badge: String? = null,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(LoanMasterTheme.components.calculatorCardHeight)
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(LoanMasterTheme.components.cardRadius))
            .clip(RoundedCornerShape(LoanMasterTheme.components.cardRadius))
            .background(SurfaceDark)
            .border(1.dp, CardStroke, RoundedCornerShape(LoanMasterTheme.components.cardRadius))
            .clickable { onClick() }
            .testTag("std_card_${title.replace(" ", "_").lowercase()}")
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(LoanMasterTheme.spacing.md)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(iconColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(LoanMasterTheme.spacing.sm))
            ScrollingTitleText(
                title, color = TextPrimary, fontSize = LoanMasterTheme.typography.body.fontSize, fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                color = TextSecondary,
                style = LoanMasterTheme.typography.label,
                lineHeight = LoanMasterTheme.typography.label.fontSize,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(end = LoanMasterTheme.spacing.md)
            )
        }
        
        if (badge != null) {
            val badgeBg = if (badge == "Premium") Brush.linearGradient(listOf(Color(0xFFF59E0B), Color(0xFFD97706))) else Brush.linearGradient(listOf(BackgroundDark.copy(alpha = 0.8f), BackgroundDark.copy(alpha = 0.8f)))
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = LoanMasterTheme.spacing.md, end = LoanMasterTheme.spacing.md)
                    .clip(RoundedCornerShape(6.dp))
                    .background(badgeBg)
                    .border(if (badge != "Premium") 1.dp else 0.dp, CardStroke, RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (badge == "Premium") {
                    Icon(imageVector = Icons.Rounded.WorkspacePremium, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(badge.uppercase(), color = Color.White, style = LoanMasterTheme.typography.label, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                } else {
                    Text(badge.uppercase(), color = TextPrimary, style = LoanMasterTheme.typography.label, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        
        Icon(
            imageVector = Icons.Rounded.ChevronRight,
            contentDescription = null,
            tint = TextSecondary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = LoanMasterTheme.spacing.md, end = LoanMasterTheme.spacing.md)
                .size(LoanMasterTheme.components.iconMedium)
        )
    }
}

@Composable
fun RecentCalculationsBanner(historyCount: Int, onNavigateToHistory: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(LoanMasterTheme.components.cardRadius))
            .background(SurfaceDark)
            .border(1.dp, CardStroke, RoundedCornerShape(LoanMasterTheme.components.cardRadius))
            .clickable { onNavigateToHistory() }
            .padding(LoanMasterTheme.spacing.md)
            .testTag("recent_calc_banner"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Rounded.AccessTime,
            contentDescription = null,
            tint = AccentBlue,
            modifier = Modifier.size(LoanMasterTheme.components.iconLarge)
        )
        Spacer(modifier = Modifier.width(LoanMasterTheme.spacing.md))
        Column(modifier = Modifier.weight(1f, fill=false).padding(end=LoanMasterTheme.spacing.xs)) {
            ScrollingTitleText(
                "Recent Calculations", color = TextPrimary, fontSize = LoanMasterTheme.typography.body.fontSize, fontWeight = FontWeight.Bold
            )
            ScrollingTitleText(
                "View and manage your previous calculations", color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize
            )
        }
        
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(LoanMasterTheme.components.cardRadius))
                .background(BackgroundDark)
                .border(1.dp, AccentYellow, RoundedCornerShape(LoanMasterTheme.components.cardRadius))
                .padding(horizontal = LoanMasterTheme.spacing.sm, vertical = LoanMasterTheme.spacing.xs),
            contentAlignment = Alignment.Center
        ) {
            Text(text = historyCount.toString(), color = AccentYellow, style = LoanMasterTheme.typography.label, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.width(LoanMasterTheme.spacing.sm))
        Icon(
            imageVector = Icons.Rounded.ChevronRight,
            contentDescription = null,
            tint = TextSecondary,
            modifier = Modifier.size(LoanMasterTheme.components.iconMedium)
        )
    }
}

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
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(80.dp).clickable { }.testTag("quick_tool_${title.replace("\n", "_").lowercase()}")
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(iconColor.copy(alpha = 0.15f))
                .border(1.dp, iconColor.copy(alpha = 0.3f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(LoanMasterTheme.spacing.sm))
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

@Composable
fun AppBottomBar(selectedRoute: String = "home", onNavClick: (String) -> Unit = {}) {
    NavigationBar(
        containerColor = NavBackground,
        contentColor = TextSecondary,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = selectedRoute == "home",
            onClick = { onNavClick("home") },
            icon = { Icon(Icons.Rounded.Home, contentDescription = "Home") },
            label = { Text("Home", maxLines = 1) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AccentYellow,
                selectedTextColor = AccentYellow,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary,
                indicatorColor = Color.Transparent
            ),
            modifier = Modifier.testTag("nav_home")
        )
        NavigationBarItem(
            selected = selectedRoute == "emi",
            onClick = { onNavClick("emi") },
            icon = { Icon(Icons.Rounded.Calculate, contentDescription = "EMI") },
            label = { Text("EMI", maxLines = 1) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AccentYellow,
                selectedTextColor = AccentYellow,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary,
                indicatorColor = Color.Transparent
            ),
            modifier = Modifier.testTag("nav_emi")
        )
        NavigationBarItem(
            selected = selectedRoute == "sip",
            onClick = { onNavClick("sip") },
            icon = { Icon(Icons.Rounded.TrendingUp, contentDescription = "SIP") },
            label = { Text("SIP", maxLines = 1) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AccentYellow,
                selectedTextColor = AccentYellow,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary,
                indicatorColor = Color.Transparent
            ),
            modifier = Modifier.testTag("nav_sip")
        )
        NavigationBarItem(
            selected = selectedRoute == "gst",
            onClick = { onNavClick("gst") },
            icon = { Icon(Icons.Rounded.ReceiptLong, contentDescription = "GST") },
            label = { Text("GST", maxLines = 1) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AccentYellow,
                selectedTextColor = AccentYellow,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary,
                indicatorColor = Color.Transparent
            ),
            modifier = Modifier.testTag("nav_gst")
        )
        NavigationBarItem(
            selected = selectedRoute == "history",
            onClick = { onNavClick("history") },
            icon = { Icon(Icons.Rounded.History, contentDescription = "History") },
            label = { Text("History", maxLines = 1) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AccentYellow,
                selectedTextColor = AccentYellow,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary,
                indicatorColor = Color.Transparent
            ),
            modifier = Modifier.testTag("nav_history")
        )
        NavigationBarItem(
            selected = selectedRoute == "compare",
            onClick = { onNavClick("compare") },
            icon = { Icon(Icons.Rounded.CompareArrows, contentDescription = "Compare") },
            label = { Text("Compare", maxLines = 1) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AccentYellow,
                selectedTextColor = AccentYellow,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary,
                indicatorColor = Color.Transparent
            ),
            modifier = Modifier.testTag("nav_compare")
        )
    }
}
