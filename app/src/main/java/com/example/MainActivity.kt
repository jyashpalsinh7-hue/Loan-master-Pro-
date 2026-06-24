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
                    val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "home") {
                    composable("home") { HomeScreen(onNavigateToEmi = { navController.navigate("emi") }, onNavigateToCompare = { navController.navigate("compare") }, onNavigateToSip = { navController.navigate("sip") }, onNavigateToGst = { navController.navigate("gst") }, onNavigateToRd = { navController.navigate("rd") }, onNavigateToFd = { navController.navigate("fd") }, onNavigateToCurrency = { navController.navigate("currency") }, onNavigateToEligibility = { navController.navigate("eligibility") }, onNavigateToPrepayment = { navController.navigate("prepayment") }, onNavigateToSettings = { navController.navigate("settings") }) }
                    composable("emi") { EmiCalculatorScreen(onNavigateBack = { navController.popBackStack() }) }
                    composable("compare") { LoanComparisonScreen(onNavigateBack = { navController.popBackStack() }) }
                    composable("sip") { SipCalculatorScreen(onNavigateBack = { navController.popBackStack() }) }
                    composable("rd") { RdCalculatorScreen(onNavigateBack = { navController.popBackStack() }) }
                    composable("fd") { FdCalculatorScreen(onNavigateBack = { navController.popBackStack() }) }
                    composable("gst") { GstCalculatorScreen(onNavigateBack = { navController.popBackStack() }) }
                    composable("currency") { CurrencyConverterScreen(onNavigateBack = { navController.popBackStack() }) }
                    composable("eligibility") { LoanEligibilityScreen() }
                    composable("prepayment") { PrepaymentCalculatorScreen() }
                    composable("settings") { SettingsScreen(onNavigateBack = { navController.popBackStack() }, viewModel = settingsViewModel) }
                }
            }
            }
        }
    }
}

@Composable
fun HomeScreen(onNavigateToEmi: () -> Unit, onNavigateToCompare: () -> Unit, onNavigateToSip: () -> Unit, onNavigateToGst: () -> Unit, onNavigateToRd: () -> Unit, onNavigateToFd: () -> Unit, onNavigateToCurrency: () -> Unit, onNavigateToEligibility: () -> Unit, onNavigateToPrepayment: () -> Unit = {}, onNavigateToSettings: () -> Unit = {}) {
    Scaffold(
        topBar = { AppTopBar(onNavigateToSettings) },
        bottomBar = { AppBottomBar() },
        containerColor = BackgroundDark
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            SearchAndPremiumRow()
            HeroBanner()
            CalculatorsSection(onNavigateToEmi, onNavigateToCompare, onNavigateToSip, onNavigateToGst, onNavigateToRd, onNavigateToFd, onNavigateToCurrency, onNavigateToEligibility, onNavigateToPrepayment)
            RecentCalculationsBanner()
            QuickToolsSection()
        }
    }
}

@Composable
fun AppTopBar(onNavigateToSettings: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Rounded.Menu,
            contentDescription = "Menu",
            tint = TextPrimary,
            modifier = Modifier.size(28.dp).testTag("menu_icon").clickable { onNavigateToSettings() }
        )
        Spacer(modifier = Modifier.width(16.dp))
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .border(2.dp, AccentYellow, CircleShape)
                    .background(BackgroundDark),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.CurrencyRupee,
                    contentDescription = null,
                    tint = AccentBlue,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 20.sp)) {
                            append("LoanMaster ")
                        }
                        withStyle(style = SpanStyle(color = AccentYellow, fontWeight = FontWeight.Bold, fontSize = 20.sp)) {
                            append("Pro")
                        }
                    }
                )
                Text(
                    text = "Smart Finance Calculator",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun SearchAndPremiumRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Search Bar
        Row(
            modifier = Modifier
                .weight(0.7f)
                .height(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(SurfaceDark)
                .border(1.dp, CardStroke, RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Rounded.Search,
                contentDescription = "Search",
                tint = TextSecondary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Search calculators...",
                color = TextSecondary,
                fontSize = 14.sp
            )
        }

        // Premium Button
        OutlinedButton(
            onClick = { },
            modifier = Modifier
                .weight(0.3f)
                .height(48.dp)
                .testTag("premium_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = SurfaceDark,
                contentColor = AccentYellow
            ),
            border = androidx.compose.foundation.BorderStroke(1.dp, AccentYellow),
            contentPadding = PaddingValues(0.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.WorkspacePremium,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "Premium", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun HeroBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF0B1736),
                        Color(0xFF133282)
                    )
                )
            )
            .border(1.dp, AccentBlue.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .weight(0.6f)
                    .padding(20.dp)
            ) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 22.sp)) {
                            append("Plan Smart, ")
                        }
                        withStyle(style = SpanStyle(color = AccentYellow, fontWeight = FontWeight.Bold, fontSize = 22.sp)) {
                            append("Save More!")
                        }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "All-in-One Finance Calculator\nfor a Better Financial Future",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentYellow, contentColor = BackgroundDark),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    modifier = Modifier.height(36.dp).testTag("explore_premium_btn")
                ) {
                    Text("Explore Premium", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(imageVector = Icons.Rounded.WorkspacePremium, contentDescription = null, modifier = Modifier.size(14.dp))
                }
            }
            Box(
                modifier = Modifier
                    .weight(0.4f)
                    .fillMaxHeight()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                // Placeholder for custom illustration
                Icon(
                    imageVector = Icons.Rounded.Savings,
                    contentDescription = null,
                    tint = AccentYellow.copy(alpha = 0.5f),
                    modifier = Modifier.size(72.dp)
                )
            }
        }
        
        // Pager dots
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(AccentYellow))
            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(TextSecondary.copy(alpha = 0.5f)))
            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(TextSecondary.copy(alpha = 0.5f)))
        }
    }
}

@Composable
fun CalculatorsSection(onNavigateToEmi: () -> Unit, onNavigateToCompare: () -> Unit, onNavigateToSip: () -> Unit, onNavigateToGst: () -> Unit, onNavigateToRd: () -> Unit, onNavigateToFd: () -> Unit, onNavigateToCurrency: () -> Unit, onNavigateToEligibility: () -> Unit, onNavigateToPrepayment: () -> Unit = {}) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Calculators",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            OutlinedButton(
                onClick = { },
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardStroke),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                modifier = Modifier.height(32.dp).testTag("edit_calculators_btn")
            ) {
                Icon(imageVector = Icons.Rounded.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Edit", fontSize = 12.sp)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Left Column
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                EmiCalculatorCard(onNavigateToEmi)
                StandardCalculatorCard("GST Calculator", "Add or remove GST easily for any amount", Icons.Rounded.Receipt, Color(0xFFE53935), onNavigateToGst)
                StandardCalculatorCard("RD Calculator", "Calculate Recurring Deposit returns", Icons.Rounded.CalendarToday, Color(0xFFFF9800), onNavigateToRd)
                StandardCalculatorCard("Loan Eligibility Checker", "Check your loan eligibility in seconds", Icons.Rounded.PersonSearch, Color(0xFF1E88E5), onNavigateToEligibility)
            }
            // Right Column
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StandardCalculatorCard("Loan Compare", "Compare 2-4 loans and find the best option", Icons.Rounded.Balance, Color(0xFF8E24AA), onNavigateToCompare)
                StandardCalculatorCard("SIP Calculator", "Plan your SIP and grow your wealth", Icons.Rounded.TrendingUp, Color(0xFF43A047), onNavigateToSip)
                StandardCalculatorCard("Currency Converter", "Live currency rates & easy conversion", Icons.Rounded.CurrencyExchange, Color(0xFF00ACC1), onNavigateToCurrency)
                StandardCalculatorCard("FD Calculator", "Calculate returns on Fixed Deposits", Icons.Rounded.Savings, Color(0xFFD81B60), onNavigateToFd)
                StandardCalculatorCard("Loan Prepayment", "Check interest saved by prepaying your loan", Icons.Rounded.EditNote, Color(0xFF5E35B1), onNavigateToPrepayment)
            }
        }
    }
}

@Composable
fun EmiCalculatorCard(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(HighlightBlue)
            .clickable { onClick() }
            .padding(16.dp)
            .testTag("emi_calculator_card")
    ) {
        Column {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Rounded.Home, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(32.dp))
                 Icon(imageVector = Icons.Rounded.CurrencyRupee, contentDescription = null, tint = HighlightBlue, modifier = Modifier.size(16.dp).align(Alignment.BottomEnd).padding(bottom = 12.dp, end = 12.dp))
            }
            Spacer(modifier = Modifier.height(24.dp))
            ScrollingTitleText(
                "EMI Calculator", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Calculate EMI for Home, Car, Personal & more",
                color = TextPrimary.copy(alpha = 0.8f),
                fontSize = 12.sp,
                lineHeight = 16.sp,
                maxLines = 2
            )
        }
        
        // Popular Tag
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .clip(RoundedCornerShape(8.dp))
                .background(BackgroundDark.copy(alpha = 0.6f))
                .padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = Icons.Rounded.Star, contentDescription = null, tint = AccentYellow, modifier = Modifier.size(10.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Popular", color = TextPrimary, fontSize = 10.sp)
        }
        
        Icon(
            imageVector = Icons.Rounded.ChevronRight,
            contentDescription = null,
            tint = TextPrimary,
            modifier = Modifier.align(Alignment.BottomEnd)
        )
    }
}

@Composable
fun StandardCalculatorCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: Color,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceDark)
            .border(1.dp, CardStroke, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(16.dp)
            .testTag("std_card_${title.replace(" ", "_").lowercase()}")
    ) {
        Column {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(iconColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
            ScrollingTitleText(
                title, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                color = TextSecondary,
                fontSize = 11.sp,
                lineHeight = 15.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(end = 12.dp)
            )
        }
        
        Icon(
            imageVector = Icons.Rounded.ChevronRight,
            contentDescription = null,
            tint = TextSecondary,
            modifier = Modifier.align(Alignment.CenterEnd).size(20.dp)
        )
    }
}

@Composable
fun RecentCalculationsBanner() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceDark)
            .border(1.dp, CardStroke, RoundedCornerShape(12.dp))
            .clickable { }
            .padding(16.dp)
            .testTag("recent_calc_banner"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Rounded.AccessTime,
            contentDescription = null,
            tint = AccentBlue,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f, fill=false).padding(end=4.dp)) {
            ScrollingTitleText(
                "Recent Calculations", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold
            )
            ScrollingTitleText(
                "View and manage your previous calculations", color = TextSecondary, fontSize = 11.sp
            )
        }
        
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(BackgroundDark)
                .border(1.dp, AccentYellow, RoundedCornerShape(16.dp))
                .padding(horizontal = 10.dp, vertical = 2.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "27", color = AccentYellow, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
            imageVector = Icons.Rounded.ChevronRight,
            contentDescription = null,
            tint = TextSecondary,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun QuickToolsSection() {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Quick Tools",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "View All >",
                color = AccentBlue,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable { }.padding(4.dp)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            QuickToolItem("Inflation\nCalculator", Icons.AutoMirrored.Rounded.TrendingUp, Color(0xFF4CAF50))
            QuickToolItem("Retirement\nCalculator", Icons.Rounded.Chair, Color(0xFF9C27B0))
            QuickToolItem("Net Worth\nTracker", Icons.Rounded.PieChart, Color(0xFFFF9800))
            QuickToolItem("EMI Schedule\nGenerator", Icons.Rounded.CalendarMonth, Color(0xFF2196F3))
            QuickToolItem("Interest Rate\nTrends", Icons.Rounded.SsidChart, Color(0xFF00BCD4))
        }
    }
}

@Composable
fun QuickToolItem(title: String, icon: ImageVector, iconColor: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(72.dp).clickable { }.testTag("quick_tool_${title.replace("\n", "_").lowercase()}")
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = title,
            color = TextSecondary,
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
            lineHeight = 14.sp,
            maxLines = 2
        )
    }
}

@Composable
fun AppBottomBar(selectedRoute: String = "home") {
    NavigationBar(
        containerColor = NavBackground,
        contentColor = TextSecondary,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = selectedRoute == "home",
            onClick = { },
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
            onClick = { },
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
            onClick = { },
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
            onClick = { },
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
            selected = selectedRoute == "currency",
            onClick = { },
            icon = { Icon(Icons.Rounded.AttachMoney, contentDescription = "Currency") },
            label = { Text("Currency", maxLines = 1) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AccentYellow,
                selectedTextColor = AccentYellow,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary,
                indicatorColor = Color.Transparent
            ),
            modifier = Modifier.testTag("nav_currency")
        )
        NavigationBarItem(
            selected = selectedRoute == "more",
            onClick = { },
            icon = { Icon(Icons.Rounded.GridView, contentDescription = "More") },
            label = { Text("More", maxLines = 1) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AccentYellow,
                selectedTextColor = AccentYellow,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary,
                indicatorColor = Color.Transparent
            ),
            modifier = Modifier.testTag("nav_more")
        )
    }
}
