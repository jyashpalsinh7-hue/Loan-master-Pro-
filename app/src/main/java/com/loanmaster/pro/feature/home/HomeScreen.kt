package com.loanmaster.pro.feature.home
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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

@Composable
fun HomeScreen(onNavigateToEmi: () -> Unit, onNavigateToCompare: () -> Unit, onNavigateToSip: () -> Unit, onNavigateToGst: () -> Unit, onNavigateToRd: () -> Unit, onNavigateToFd: () -> Unit, onNavigateToCurrency: () -> Unit, onNavigateToEligibility: () -> Unit, onNavigateToPrepayment: () -> Unit = {}, onNavigateToSettings: () -> Unit = {}, onNavigateBottomNav: (String) -> Unit = {}, onNavigateToHistory: () -> Unit = {},
    onNavigateToCalculator: (CalculationHistory) -> Unit = {}, onNavigateToLoanSummary: () -> Unit = {}, historyItems: List<CalculationHistory> = emptyList(), viewModel: HomeViewModel, activeLoans: List<ActiveLoan> = emptyList()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val dummyCurrency = com.loanmaster.pro.LocalCurrency.current
    var showUnlockDialog by rememberSaveable { mutableStateOf(false) }
    val premiumStateContext = androidx.compose.ui.platform.LocalContext.current
    val premiumStateManager = remember { com.loanmaster.pro.core.managers.PremiumManager(premiumStateContext.applicationContext) }
    val isPremiumUnlocked by premiumStateManager.isPremium.collectAsStateWithLifecycle(initialValue = false)
    val searchQuery = uiState.searchQuery
    val isQuickToolsExpanded = uiState.isQuickToolsExpanded
    

    if (showUnlockDialog) {
        val context = androidx.compose.ui.platform.LocalContext.current
        com.loanmaster.pro.core.ui.PremiumUnlockDialog(
            onDismiss = { showUnlockDialog = false },
            onUnlockSuccessful = {
                premiumStateManager.unlockPermanent()
            }
        )
    }
    Scaffold(
        topBar = { AppTopBar(onNavigateToSettings) },
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
                columns = androidx.compose.foundation.lazy.grid.GridCells.Fixed(columns),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = LoanMasterTheme.spacing.screenPadding, vertical = LoanMasterTheme.spacing.sm)
                    .imePadding(),
                verticalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.sm),
                horizontalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.gridGutter)
            ) {
                item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) {
                    SearchAndPremiumRow(searchQuery, onSearchQueryChange = { viewModel.updateSearchQuery(it) }, onPremiumClick = { if (!isPremiumUnlocked) showUnlockDialog = true }, isPremiumUnlocked = isPremiumUnlocked)
                }
                if (searchQuery.isBlank() && !isPremiumUnlocked) {
                    item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) {
                        HeroBanner(onPremiumClick = { if (!isPremiumUnlocked) showUnlockDialog = true })
                    }
                }
                
                // Calculators Header
                item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) {
                    val headerText = if (searchQuery.isBlank()) "Calculators" else "Search Results"
                    Text(
                        text = headerText,
                        color = TextPrimary,
                        style = LoanMasterTheme.typography.title,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth().padding(top = LoanMasterTheme.spacing.lg, bottom = LoanMasterTheme.spacing.md)
                    )
                }
                
                val summaryTitle = if (activeLoans.isNotEmpty()) "Active Loans (${activeLoans.size})" else "Loan Summary"
                val summaryDesc = if (activeLoans.isNotEmpty()) "Total: ${com.loanmaster.pro.core.formatter.formatMoney(activeLoans.sumOf { it.principalAmount })}" else "View active loans"

                class CardItem(val title: String, val subtitle: String, val content: @Composable () -> Unit)
                val allCards = listOf(
                    CardItem("Loan Compare", "Compare 2-4 loans") { StandardCalculatorCard("Loan Compare", "Compare 2-4 loans", Icons.Rounded.Balance, Color(0xFF8E24AA), badge = "New", onClick = onNavigateToCompare) },
                    CardItem("SIP Calculator", "Plan your SIP & grow wealth") { StandardCalculatorCard("SIP Calculator", "Plan your SIP & grow wealth", Icons.AutoMirrored.Rounded.TrendingUp, Color(0xFF43A047), onClick = onNavigateToSip) },
                    CardItem("GST Calculator", "Add or remove GST easily") { StandardCalculatorCard("GST Calculator", "Add or remove GST easily", Icons.Rounded.Receipt, Color(0xFFE53935), onClick = onNavigateToGst) },
                    CardItem("RD Calculator", "Calculate Recurring Deposit") { StandardCalculatorCard("RD Calculator", "Calculate Recurring Deposit", Icons.Rounded.CalendarToday, Color(0xFFFF9800), onClick = onNavigateToRd) },
                    CardItem("Currency Converter", "Live rates & conversion") { StandardCalculatorCard("Currency Converter", "Live rates & conversion", Icons.Rounded.CurrencyExchange, Color(0xFF00ACC1), onClick = onNavigateToCurrency) },
                    CardItem("Loan Eligibility", "Check eligibility quickly") { StandardCalculatorCard("Loan Eligibility", "Check eligibility quickly", Icons.Rounded.PersonSearch, Color(0xFF1E88E5), badge = "Premium", onClick = onNavigateToEligibility) },
                    CardItem("FD Calculator", "Calculate FD returns") { StandardCalculatorCard("FD Calculator", "Calculate FD returns", Icons.Rounded.Savings, Color(0xFFD81B60), onClick = onNavigateToFd) },
                    CardItem("Loan Prepayment", "Check interest saved") { StandardCalculatorCard("Loan Prepayment", "Check interest saved", Icons.Rounded.EditNote, Color(0xFF5E35B1), onClick = onNavigateToPrepayment) },
                    CardItem(summaryTitle, summaryDesc) { StandardCalculatorCard(summaryTitle, summaryDesc, Icons.Rounded.Summarize, Color(0xFF1E88E5), onClick = onNavigateToLoanSummary) },
                    CardItem("History", "Recent calculations") { StandardCalculatorCard("History", "Recent calculations", Icons.Rounded.History, Color(0xFF607D8B), onClick = onNavigateToHistory) }
                )

                val filteredCards = allCards.filter {
                    it.title.contains(searchQuery, ignoreCase = true) || it.subtitle.contains(searchQuery, ignoreCase = true)
                }

                if ("EMI Calculator".contains(searchQuery, ignoreCase = true) || "Calculate monthly EMI".contains(searchQuery, ignoreCase = true)) {
                    item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) {
                        EmiCalculatorCard(onNavigateToEmi)
                    }
                }

                filteredCards.forEach { cardInfo ->
                    item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(1) }) {
                        cardInfo.content()
                    }
                }

                if (searchQuery.isBlank()) {
                    item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) {
                        RecentCalculationsBanner(historyItems, onNavigateToHistory, onNavigateToCalculator)
                    }
                    item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) {
                        QuickToolsSection(isQuickToolsExpanded, onToggleExpand = { viewModel.toggleQuickToolsExpanded() })
                    }
                }
                
                item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) {
                    Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.xl))
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
            imageVector = Icons.Rounded.Settings,
            contentDescription = "Settings",
            tint = TextPrimary,
            modifier = Modifier
                .size(LoanMasterTheme.components.iconMedium)
                .testTag("menu_icon")
                .clickable { onNavigateToSettings() }
        )
        Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.md))
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(LoanMasterTheme.components.buttonHeight)
                    .clip(CircleShape)
                    .border(LoanMasterTheme.spacing.xs, AccentYellow, CircleShape)
                    .background(BackgroundDark),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.AccountBalanceWallet,
                    contentDescription = null,
                    tint = AccentBlue,
                    modifier = Modifier.size(LoanMasterTheme.components.iconMedium)
                )
            }
            Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.sm))
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
fun SearchAndPremiumRow(searchQuery: String, onSearchQueryChange: (String) -> Unit, onPremiumClick: () -> Unit = {}, isPremiumUnlocked: Boolean = false) {
    var isFocused by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
    val borderColor by androidx.compose.animation.animateColorAsState(targetValue = if (isFocused) AccentBlue else CardStroke)
    val borderWidth by androidx.compose.animation.core.animateDpAsState(targetValue = if (isFocused) 2.dp else 1.dp)
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
                .heightIn(min = LoanMasterTheme.components.buttonHeight)
                .shadow(if (isFocused) LoanMasterTheme.spacing.sm else 0.dp, RoundedCornerShape(LoanMasterTheme.components.cardRadius), spotColor = AccentBlue.copy(alpha = glowAlpha))
                .clip(RoundedCornerShape(LoanMasterTheme.components.cardRadius))
                .background(SurfaceDark)
                .border(borderWidth, borderColor, RoundedCornerShape(LoanMasterTheme.components.cardRadius))
                .padding(horizontal = LoanMasterTheme.spacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Rounded.Search,
                contentDescription = "Search",
                tint = if (isFocused) AccentBlue else TextSecondary,
                modifier = Modifier.size(LoanMasterTheme.components.iconMedium)
            )
            Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.sm))
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

        val context = androidx.compose.ui.platform.LocalContext.current
        // Premium Button
        if (isPremiumUnlocked) {
            Row(
                modifier = Modifier
                    .heightIn(min = LoanMasterTheme.components.buttonHeight)
                    .clip(RoundedCornerShape(LoanMasterTheme.components.cardRadius))
                    .background(androidx.compose.ui.graphics.Brush.horizontalGradient(listOf(Color(0xFFF59E0B), Color(0xFFD97706))))
                    .padding(horizontal = LoanMasterTheme.spacing.md),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.WorkspacePremium,
                    contentDescription = "Premium User",
                    tint = Color.White,
                    modifier = Modifier.size(LoanMasterTheme.components.iconSmall)
                )
                Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.xs))
                Text(text = "Premium User", style = LoanMasterTheme.typography.label, fontWeight = FontWeight.Bold, color = Color.White)
            }
        } else {
            OutlinedButton(
                onClick = onPremiumClick,
                modifier = Modifier
                    .heightIn(min = LoanMasterTheme.components.buttonHeight)
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
                Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.xs))
                Text(text = "Premium", style = LoanMasterTheme.typography.label, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun HeroBanner(onPremiumClick: () -> Unit = {}) {
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
            .heightIn(min = screenHeight * 0.24f)
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {
                Column(
                    modifier = Modifier
                        .weight(0.6f)
                        .fillMaxHeight()
                        .padding(LoanMasterTheme.spacing.lg),
                    verticalArrangement = Arrangement.Center
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
                    Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.sm))
                    Text(
                        text = if (page == 0) "All-in-One Finance Calculator\nfor a Better Financial Future" else if (page == 1) "Upgrade to Premium for an\nuninterrupted ad-free experience" else "Unlock advanced financial\nanalytics and reporting tools",
                        color = TextSecondary,
                        style = LoanMasterTheme.typography.body,
                        lineHeight = LoanMasterTheme.typography.body.fontSize * 1.3f
                    )
                    Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.md))
                    val context = androidx.compose.ui.platform.LocalContext.current
                    Button(
                        onClick = onPremiumClick,
                        colors = ButtonDefaults.buttonColors(containerColor = AccentYellow, contentColor = BackgroundDark),
                        shape = RoundedCornerShape(LoanMasterTheme.components.cardRadius),
                        contentPadding = PaddingValues(horizontal = LoanMasterTheme.spacing.md, vertical = LoanMasterTheme.spacing.sm),
                        modifier = Modifier.heightIn(min = LoanMasterTheme.components.buttonHeight).testTag("explore_premium_btn_$page")
                    ) {
                        Text("Explore Premium", style = LoanMasterTheme.typography.label, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.xs))
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
            .aspectRatio(2.8f)
            .shadow(elevation = LoanMasterTheme.spacing.md, shape = RoundedCornerShape(LoanMasterTheme.components.cardRadius), spotColor = AccentYellow.copy(alpha = 0.4f))
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
                .offset(x = LoanMasterTheme.spacing.xl, y = (-40).dp)
                .size(LoanMasterTheme.components.calculatorCardHeight)
                .clip(CircleShape)
                .background(Brush.radialGradient(listOf(Color.White.copy(alpha = 0.1f), Color.Transparent)))
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = (-30).dp, y = LoanMasterTheme.spacing.xl)
                .size(LoanMasterTheme.components.calculatorCardHeight)
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
                        .size(LoanMasterTheme.components.iconLarge)
                        .clip(RoundedCornerShape(LoanMasterTheme.spacing.md))
                        .background(Brush.linearGradient(listOf(Color(0xFF60A5FA), Color(0xFF2563EB))))
                        .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(LoanMasterTheme.spacing.md)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Rounded.Calculate, contentDescription = null, tint = Color.White, modifier = Modifier.size(LoanMasterTheme.components.iconSmall))
                }
                Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.sm))
                Text(
                    text = "EMI Calculator", 
                    color = Color.White, 
                    fontSize = LoanMasterTheme.typography.title.fontSize, 
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.xs))
                Text(
                    text = "Calculate EMI for Home, Car, Personal & more",
                    color = Color.White.copy(alpha = 0.7f),
                    style = LoanMasterTheme.typography.label,
                    lineHeight = LoanMasterTheme.typography.label.fontSize * 1.3f,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            Box(
                modifier = Modifier
                    .size(LoanMasterTheme.components.iconLarge)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.1f))
                    .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.ChevronRight,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(LoanMasterTheme.components.iconMedium)
                )
            }
        }
        
        // Popular Tag
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = LoanMasterTheme.spacing.md, end = LoanMasterTheme.spacing.md)
                .clip(RoundedCornerShape(LoanMasterTheme.spacing.sm))
                .background(Brush.linearGradient(listOf(Color(0xFFF59E0B), Color(0xFFD97706))))
                .padding(horizontal = LoanMasterTheme.spacing.sm, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = Icons.Rounded.Star, contentDescription = null, tint = Color.White, modifier = Modifier.size(LoanMasterTheme.spacing.md))
            Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.xs))
            Text("POPULAR", color = Color.White, style = LoanMasterTheme.typography.label, fontSize = LoanMasterTheme.typography.label.fontSize, fontWeight = FontWeight.Bold)
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
            .aspectRatio(1.6f)
            .shadow(elevation = LoanMasterTheme.spacing.xs, shape = RoundedCornerShape(LoanMasterTheme.components.cardRadius))
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
                    .size(LoanMasterTheme.components.iconLarge)
                    .clip(CircleShape)
                    .background(iconColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(LoanMasterTheme.components.iconSmall))
            }
            Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.sm))
            ScrollingTitleText(
                title, color = TextPrimary, fontSize = LoanMasterTheme.typography.body.fontSize, fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.xs))
            Text(
                text = subtitle,
                color = TextSecondary,
                style = LoanMasterTheme.typography.label,
                lineHeight = LoanMasterTheme.typography.label.fontSize,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        
        if (badge != null) {
            val badgeBg = if (badge == "Premium") Brush.linearGradient(listOf(Color(0xFFF59E0B), Color(0xFFD97706))) else Brush.linearGradient(listOf(BackgroundDark.copy(alpha = 0.8f), BackgroundDark.copy(alpha = 0.8f)))
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = LoanMasterTheme.spacing.md, end = LoanMasterTheme.spacing.md)
                    .clip(RoundedCornerShape(LoanMasterTheme.spacing.sm))
                    .background(badgeBg)
                    .border(if (badge != "Premium") 1.dp else 0.dp, CardStroke, RoundedCornerShape(LoanMasterTheme.spacing.sm))
                    .padding(horizontal = LoanMasterTheme.spacing.sm, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (badge == "Premium") {
                    Icon(imageVector = Icons.Rounded.WorkspacePremium, contentDescription = null, tint = Color.White, modifier = Modifier.size(LoanMasterTheme.spacing.md))
                    Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.xs))
                    Text(badge.uppercase(), color = Color.White, style = LoanMasterTheme.typography.label, fontSize = LoanMasterTheme.typography.label.fontSize, fontWeight = FontWeight.Bold)
                } else {
                    Text(badge.uppercase(), color = TextPrimary, style = LoanMasterTheme.typography.label, fontSize = LoanMasterTheme.typography.label.fontSize, fontWeight = FontWeight.Bold)
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
                .size(LoanMasterTheme.components.iconSmall)
        )
    }
}

@Composable
fun RecentCalculationsBanner(
    historyItems: List<CalculationHistory>, 
    onNavigateToHistory: () -> Unit,
    onNavigateToCalculator: (CalculationHistory) -> Unit
) {
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
        Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.md))
        Column(modifier = Modifier.weight(1f, fill=false).padding(end=LoanMasterTheme.spacing.xs)) {
            ScrollingTitleText(
                "Recent Calculations", color = TextPrimary, fontSize = LoanMasterTheme.typography.body.fontSize, fontWeight = FontWeight.Bold
            )
            ScrollingTitleText(
                if (historyItems.isEmpty()) "No history available yet" else "${historyItems.size} calculations saved", 
                color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize
            )
        }
        Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.sm))
        Icon(
            imageVector = Icons.Rounded.ChevronRight,
            contentDescription = null,
            tint = TextSecondary,
            modifier = Modifier.size(LoanMasterTheme.components.iconMedium)
        )
    }
}

