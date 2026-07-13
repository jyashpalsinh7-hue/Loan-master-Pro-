package com.loanmaster.pro.feature.rd

import com.loanmaster.pro.domain.model.*
import com.loanmaster.pro.feature.gst.*
import com.loanmaster.pro.feature.sip.*
import com.loanmaster.pro.core.ui.*
import com.loanmaster.pro.feature.history.*
import com.loanmaster.pro.core.theme.*
import com.loanmaster.pro.data.datastore.*
import com.loanmaster.pro.feature.settings.*
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



import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material.icons.automirrored.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.blur
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.pow

@Composable
fun RdScreen(
    onNavigateBack: () -> Unit,
    historyViewModel: HistoryViewModel? = null,
    initialHistory: CalculationHistory? = null,
    onHistoryConsumed: () -> Unit = {}
,
    viewModel: RdViewModel = viewModel()
) {
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val sizeClass = when {
        configuration.screenWidthDp < 600 -> WindowWidthSizeClass.COMPACT
        configuration.screenWidthDp < 840 -> WindowWidthSizeClass.MEDIUM
        else -> WindowWidthSizeClass.EXPANDED
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val dummyCurrency = com.loanmaster.pro.LocalCurrency.current
    
    val selectedTab = uiState.selectedTab
    val monthlyDepositText = uiState.monthlyDepositText
    val interestRatePaText = uiState.interestRatePaText
    val tenureYearsText = uiState.tenureYearsText
    val compoundingFrequency = uiState.compoundingFrequency
    val targetAmountText = uiState.targetAmountText
    val currentHistoryId = uiState.currentHistoryId
    
    val maturityValue = uiState.maturityValue
    val calculatedMonthlyDeposit = uiState.calculatedMonthlyDeposit
    val totalInvested = uiState.totalInvested
    val totalReturns = uiState.totalReturns
    val wealthGain = uiState.wealthGain
    val hasValidInput = uiState.hasValidInput

    var showCompoundingDropdown by rememberSaveable { mutableStateOf(false) }
    var isPremiumUnlocked by rememberSaveable { mutableStateOf(false) }
    var showUnlockDialog by rememberSaveable { mutableStateOf(false) }
    
    LaunchedEffect(initialHistory) {
        if (initialHistory != null) {
            viewModel.updateInputs(history = initialHistory)
            onHistoryConsumed()
        }
    }
    
    val formatInr = { value: Double ->
        com.loanmaster.pro.core.formatter.formatMoney(value)
    }
    

    val formatDec = { value: Double ->
        val s = String.format(Locale.US, "%.2f", value)
        if (s.endsWith(".00")) s.substring(0, s.length - 3) else s
    }
    
    val n = when (compoundingFrequency) {
        "Yearly" -> 1.0
        "Half-Yearly" -> 2.0
        "Quarterly" -> 4.0
        "Monthly" -> 12.0
        else -> 4.0
    }

    
    val annualRate = interestRatePaText.safeToDouble() / 100
    val t = tenureYearsText.safeToDouble().coerceIn(0.0, 100.0)
    
    LaunchedEffect(selectedTab, monthlyDepositText, interestRatePaText, tenureYearsText, compoundingFrequency, targetAmountText) {
        kotlinx.coroutines.delay(2000)
        if (historyViewModel != null && hasValidInput) {
            val history = CalculationHistory(
                id = currentHistoryId,
                calculatorType = "RD",
                title = "${com.loanmaster.pro.core.formatter.currentCurrencySymbol}$monthlyDepositText at $interestRatePaText%",
                param1 = monthlyDepositText,
                param2 = interestRatePaText,
                param3 = tenureYearsText,
                param4 = compoundingFrequency,
                param5 = if(selectedTab == "Goal Based") "Target" else "Standard"
            )
            historyViewModel.insert(history) { id ->
                viewModel.updateInputs(historyId = id)
            }
        }
    }



    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(BackgroundDark).statusBarsPadding()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = LoanMasterTheme.spacing.md, vertical = LoanMasterTheme.spacing.md),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary,
                        modifier = Modifier.size(LoanMasterTheme.spacing.lg).clickable { onNavigateBack() }
                    )
                    Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.md))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("RD Calculator", color = TextPrimary, fontSize = LoanMasterTheme.typography.title.fontSize, fontWeight = FontWeight.Bold)
                        Text("Plan your Recurring Deposit", color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize)
                    }
                    val context = androidx.compose.ui.platform.LocalContext.current
                    Icon(imageVector = Icons.Rounded.PictureAsPdf, contentDescription = "Export PDF", tint = TextPrimary, modifier = Modifier.size(LoanMasterTheme.spacing.lg).clickable {
                        ExportUtils.exportToPdf(
                            context,
                            "RD Calculator Report",
                            listOf(
                                "Monthly Deposit" to formatInr(calculatedMonthlyDeposit),
                                "Interest Rate" to "$interestRatePaText%",
                                "Time Period" to "$tenureYearsText Years",
                                "" to "",
                                "Total Invested" to formatInr(totalInvested),
                                "Total Returns" to formatInr(totalReturns),
                                "Maturity Value" to formatInr(maturityValue)
                            )
                        )
                    })
                }
            }
        },
        containerColor = BackgroundDark
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            CalculatorScreenLayout(
                widthSizeClass = sizeClass,
                animationTriggerState = maturityValue,
                headerSection = { },
                inputControlsSection = {
                    Column(verticalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.screenPadding), modifier = Modifier.fillMaxWidth()) {
                        
                        TabRow(
                            selectedTabIndex = if (selectedTab == "Standard") 0 else 1,
                            containerColor = Color.Transparent,
                            contentColor = AccentBlue,
                            indicator = { tabPositions ->
                                TabRowDefaults.SecondaryIndicator(
                                    modifier = Modifier.tabIndicatorOffset(tabPositions[if (selectedTab == "Standard") 0 else 1]),
                                    color = AccentBlue
                                )
                            },
                            divider = { HorizontalDivider(color = CardStroke) }
                        ) {
                            Tab(
                                selected = selectedTab == "Standard",
                                onClick = { viewModel.updateInputs(tab = "Standard") },
                                text = { Text("Standard RD", color = if (selectedTab == "Standard") AccentBlue else TextSecondary) }
                            )
                            Tab(
                                selected = selectedTab == "Goal Based",
                                onClick = { viewModel.updateInputs(tab = "Goal Based") },
                                text = { 
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("Goal Based", color = if (selectedTab == "Goal Based") AccentBlue else TextSecondary)
                                        if (!isPremiumUnlocked) {
                                            Spacer(Modifier.widthIn(min = LoanMasterTheme.spacing.xs))
                                            Icon(Icons.Rounded.Lock, contentDescription = "Premium", tint = AccentYellow, modifier = Modifier.size(LoanMasterTheme.spacing.md))
                                        }
                                    }
                                }
                            )
                        }
                        
                        if (sizeClass == WindowWidthSizeClass.COMPACT) {
                            if (selectedTab == "Standard") {
                                PremiumInputField(
                                    label = "Monthly Deposit", value = monthlyDepositText, onValueChange = { viewModel.updateInputs(monthlyDeposit = it) },
                                    icon = Icons.Rounded.AccountBalanceWallet, iconTint = AccentBlue, modifier = Modifier.fillMaxWidth()
                                )
                            } else {
                                PremiumInputField(
                                    label = "Target Amount", value = targetAmountText, onValueChange = { viewModel.updateInputs(targetAmount = it) },
                                    icon = Icons.Rounded.Flag, iconTint = AccentBlue, modifier = Modifier.fillMaxWidth()
                                )
                            }
                            PremiumInputField(
                                label = "Interest Rate (p.a.)", value = interestRatePaText, onValueChange = { viewModel.updateInputs(interestRate = it) },
                                icon = Icons.Rounded.Percent, iconTint = Color(0xFF7C4DFF), modifier = Modifier.fillMaxWidth()
                            )
                            PremiumInputField(
                                label = "Tenure", value = tenureYearsText, onValueChange = { viewModel.updateInputs(tenureYears = it) },
                                icon = Icons.Rounded.DateRange, iconTint = AccentGreen, trailingIcon = Icons.Rounded.KeyboardArrowDown, suffix = " Yrs", modifier = Modifier.fillMaxWidth()
                            )
                    Box(modifier = Modifier.fillMaxWidth()) {
                        PremiumInputField(
                            isNumeric = false,
                            label = "Compounding", value = compoundingFrequency, onValueChange = {}, readOnly = true, onClick = { showCompoundingDropdown = true },
                            icon = Icons.Rounded.BarChart, iconTint = AccentBlue, trailingIcon = Icons.Rounded.KeyboardArrowDown, modifier = Modifier.fillMaxWidth(),
                            infoText = "How often interest is calculated and added to your principal. Banks usually compound RD interest quarterly."
                        )
                        DropdownMenu(
                            expanded = showCompoundingDropdown,
                            onDismissRequest = { showCompoundingDropdown = false },
                            modifier = Modifier.background(SurfaceDark).fillMaxWidth(0.9f)
                        ) {
                            listOf("Yearly", "Half-Yearly", "Quarterly", "Monthly").forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option, color = TextPrimary) },
                                    onClick = {
                                        viewModel.updateInputs(frequency = option)
                                        showCompoundingDropdown = false
                                    }
                                )
                            }
                        }
                    }
                } else {
                    Row(horizontalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.md), modifier = Modifier.fillMaxWidth()) {
                        Box(modifier = Modifier.weight(1f)) {
                            if (selectedTab == "Standard") {
                                PremiumInputField(
                                    label = "Monthly Deposit", value = monthlyDepositText, onValueChange = { viewModel.updateInputs(monthlyDeposit = it) },
                                    icon = Icons.Rounded.AccountBalanceWallet, iconTint = AccentBlue
                                )
                            } else {
                                PremiumInputField(
                                    label = "Target Amount", value = targetAmountText, onValueChange = { viewModel.updateInputs(targetAmount = it) },
                                    icon = Icons.Rounded.Flag, iconTint = AccentBlue
                                )
                            }
                        }
                    // FIX: Added Financial Disclaimer
                    FinancialDisclaimer()
                        Box(modifier = Modifier.weight(1f)) {
                            PremiumInputField(
                                label = "Interest Rate (p.a.)", value = interestRatePaText, onValueChange = { viewModel.updateInputs(interestRate = it) },
                                icon = Icons.Rounded.Percent, iconTint = Color(0xFF7C4DFF)
                            )
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.md), modifier = Modifier.fillMaxWidth()) {
                        Box(modifier = Modifier.weight(1f)) {
                            PremiumInputField(
                                label = "Tenure", value = tenureYearsText, onValueChange = { viewModel.updateInputs(tenureYears = it) },
                                icon = Icons.Rounded.DateRange, iconTint = AccentGreen, trailingIcon = Icons.Rounded.KeyboardArrowDown, suffix = " Yrs"
                            )
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            PremiumInputField(
                                isNumeric = false,
                                label = "Compounding", value = compoundingFrequency, onValueChange = {}, readOnly = true, onClick = { showCompoundingDropdown = true },
                                icon = Icons.Rounded.BarChart, iconTint = AccentBlue, trailingIcon = Icons.Rounded.KeyboardArrowDown,
                                infoText = "How often interest is calculated and added to your principal. Banks usually compound RD interest quarterly."
                            )
                            DropdownMenu(
                                expanded = showCompoundingDropdown,
                                onDismissRequest = { showCompoundingDropdown = false },
                                modifier = Modifier.background(SurfaceDark).fillMaxWidth(0.9f)
                            ) {
                                listOf("Yearly", "Half-Yearly", "Quarterly", "Monthly").forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option, color = TextPrimary) },
                                        onClick = {
                                            viewModel.updateInputs(frequency = option)
                                            showCompoundingDropdown = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        resultsSection = {
            Box(modifier = Modifier.fillMaxWidth()) {
                val isLocked = selectedTab == "Goal Based" && !isPremiumUnlocked
                Column(
                    verticalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.screenPadding),
                    modifier = if (isLocked) Modifier.fillMaxWidth().blur(LoanMasterTheme.spacing.md) else Modifier.fillMaxWidth()
                ) {
                    // Hero Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .clip(RoundedCornerShape(LoanMasterTheme.spacing.md))
                        .background(SurfaceDark)
                    .border(1.dp, CardStroke, RoundedCornerShape(LoanMasterTheme.spacing.md))
                    .padding(LoanMasterTheme.spacing.md)
            ) {
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(modifier = Modifier.weight(0.65f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(if (selectedTab == "Standard") "Estimated Maturity Value" else "Required Monthly Deposit", color = TextSecondary, fontSize = LoanMasterTheme.typography.body.fontSize, modifier = Modifier.padding(horizontal = LoanMasterTheme.spacing.xs))
                                Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.sm))
                                Icon(Icons.Rounded.Info, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(LoanMasterTheme.spacing.md))
                            }
                            Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.xs))
                            val formattedHeroValue = if (selectedTab == "Standard") formatInr(maturityValue) else formatInr(calculatedMonthlyDeposit)
                            AutoResizedText(
                                text = formattedHeroValue,
                                color = AccentYellow,
                                fontSize = LoanMasterTheme.typography.display.fontSize,
                                fontWeight = FontWeight.ExtraBold,
                                maxLines = 1,
                                modifier = Modifier.padding(horizontal = LoanMasterTheme.spacing.xs)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .weight(0.35f)
                                .heightIn(min = 80.dp)
                                .clip(RoundedCornerShape(LoanMasterTheme.spacing.md))
                                .background(Brush.radialGradient(colors = listOf(AccentBlue.copy(alpha = 0.2f), Color.Transparent))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Savings,
                                contentDescription = null,
                                tint = AccentBlue.copy(alpha = 0.8f),
                                modifier = Modifier.size(LoanMasterTheme.components.topAppBarHeight)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.components.iconSmall))
                    HorizontalDivider(color = CardStroke)
                    Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.md))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.padding(end = LoanMasterTheme.spacing.lg)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Total Invested", color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize, modifier = Modifier.padding(horizontal = LoanMasterTheme.spacing.xs))
                                Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.xs))
                                Icon(Icons.Rounded.Info, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(LoanMasterTheme.spacing.md))
                            }
                            Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.xs))
                            Text(formatInr(totalInvested), color = TextPrimary, fontSize = LoanMasterTheme.typography.title.fontSize, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = LoanMasterTheme.spacing.xs))
                        }
                        Column(modifier = Modifier.padding(end = LoanMasterTheme.spacing.lg)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Total Returns", color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize, modifier = Modifier.padding(horizontal = LoanMasterTheme.spacing.xs))
                                Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.xs))
                                Icon(Icons.Rounded.Info, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(LoanMasterTheme.spacing.md))
                            }
                            Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.xs))
                            Text(formatInr(totalReturns), color = AccentGreen, fontSize = LoanMasterTheme.typography.title.fontSize, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = LoanMasterTheme.spacing.xs))
                        }
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Wealth Gain", color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize, modifier = Modifier.padding(horizontal = LoanMasterTheme.spacing.xs))
                                Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.xs))
                                Icon(Icons.Rounded.Info, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(LoanMasterTheme.spacing.md))
                            }
                            Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.xs))
                            Text("${formatDec(wealthGain)}%", color = AccentGreen, fontSize = LoanMasterTheme.typography.title.fontSize, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = LoanMasterTheme.spacing.xs))
                        }
                    }
                }
            }

            // Visuals
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.md)) {
                // Growth Chart
                Column(
                    modifier = Modifier.weight(1f).clip(RoundedCornerShape(LoanMasterTheme.spacing.md)).background(SurfaceDark).border(1.dp, CardStroke, RoundedCornerShape(LoanMasterTheme.spacing.md)).padding(LoanMasterTheme.spacing.md)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        ScrollingTitleText("Investment Growth", color = TextPrimary, fontSize = LoanMasterTheme.typography.label.fontSize, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.xs))
                        Icon(Icons.Rounded.Info, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(LoanMasterTheme.spacing.md))
                    }
                    Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.md))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.md)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(LoanMasterTheme.spacing.sm).clip(CircleShape).background(AccentYellow))
                            Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.xs))
                            Text("Maturity Value", color = TextSecondary, fontSize = 9.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(LoanMasterTheme.spacing.sm).clip(CircleShape).background(AccentBlue))
                            Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.xs))
                            Text("Total Invested", color = TextSecondary, fontSize = 9.sp)
                        }
                    }
                    Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.md))
                    Row(modifier = Modifier.fillMaxWidth().heightIn(min = LoanMasterTheme.components.featuredCardHeight)) {
                        Column(modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.SpaceBetween) {
                            Text("120L", color = TextSecondary, fontSize = 9.sp)
                            Text("90L", color = TextSecondary, fontSize = 9.sp)
                            Text("60L", color = TextSecondary, fontSize = 9.sp)
                            Text("30L", color = TextSecondary, fontSize = 9.sp)
                            Text("0", color = TextSecondary, fontSize = 9.sp)
                        }
                        Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.sm))
                        Column(modifier = Modifier.weight(1f)) {
                            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                                Canvas(modifier = Modifier.fillMaxSize().padding(vertical = LoanMasterTheme.spacing.xs)) {
                                    val h = size.height
                                    val w = size.width
                                    val investedPath = Path().apply {
                                        moveTo(0f, h)
                                        lineTo(w, h * 0.75f)
                                    }
                                    val maturityPath = Path().apply {
                                        moveTo(0f, h)
                                        cubicTo(w * 0.4f, h, w * 0.7f, h * 0.6f, w, 0f)
                                    }
                                    drawPath(investedPath, color = AccentBlue, style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round))
                                    drawPath(maturityPath, color = AccentYellow, style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round))
                                }
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("0", color = TextSecondary, fontSize = 9.sp)
                                Text("5Y", color = TextSecondary, fontSize = 9.sp)
                                Text("10Y", color = TextSecondary, fontSize = 9.sp)
                                Text("15Y", color = TextSecondary, fontSize = 9.sp)
                                Text("20Y", color = TextSecondary, fontSize = 9.sp)
                            }
                        }
                    }
                }
                
                // Breakup Chart
                Column(
                    modifier = Modifier.weight(1f).clip(RoundedCornerShape(LoanMasterTheme.spacing.md)).background(SurfaceDark).border(1.dp, CardStroke, RoundedCornerShape(LoanMasterTheme.spacing.md)).padding(LoanMasterTheme.spacing.md)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        ScrollingTitleText("Breakup at Maturity", color = TextPrimary, fontSize = LoanMasterTheme.typography.label.fontSize, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.xs))
                        Icon(Icons.Rounded.Info, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(LoanMasterTheme.spacing.md))
                    }
                    Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.components.iconSmall))
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Box(modifier = Modifier.size(LoanMasterTheme.components.calculatorCardHeight), contentAlignment = Alignment.Center) {
                            val invPct = if (maturityValue > 0) (totalInvested / maturityValue).toFloat() else 1f
                            Canvas(modifier = Modifier.fillMaxSize().padding(LoanMasterTheme.spacing.md)) {
                                val strokeWidth = 24.dp.toPx()
                                drawArc(
                                    color = AccentBlue,
                                    startAngle = 90f,
                                    sweepAngle = 360f * invPct,
                                    useCenter = false,
                                    style = Stroke(strokeWidth, cap = StrokeCap.Butt)
                                )
                                drawArc(
                                    color = AccentGreen,
                                    startAngle = 90f + (360f * invPct),
                                    sweepAngle = 360f * (1 - invPct),
                                    useCenter = false,
                                    style = Stroke(strokeWidth, cap = StrokeCap.Butt)
                                )
                            }
                        }
                        Column(modifier = Modifier.weight(1f).padding(start = LoanMasterTheme.spacing.md)) {
                            val invPctStr = String.format(Locale.US, "%.1f%%", if (maturityValue > 0) (totalInvested/maturityValue)*100 else 100f)
                            val retPctStr = String.format(Locale.US, "%.1f%%", if (maturityValue > 0) (totalReturns/maturityValue)*100 else 0f)
                            RdLegend("Total Invested", formatInr(totalInvested), "($invPctStr)", AccentBlue, Modifier.padding(end = LoanMasterTheme.spacing.sm))
                            Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.md))
                            RdLegend("Total Returns", formatInr(totalReturns), "($retPctStr)", AccentGreen, Modifier.padding(end = LoanMasterTheme.spacing.sm))
                        }
                    }
                }
            }

            // What if Banner
            Row(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(LoanMasterTheme.spacing.sm)).background(SurfaceDark).border(1.dp, CardStroke, RoundedCornerShape(LoanMasterTheme.spacing.sm)).padding(LoanMasterTheme.spacing.md),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Rounded.EmojiObjects, contentDescription = null, tint = Color(0xFF7C4DFF), modifier = Modifier.size(LoanMasterTheme.spacing.lg))
                Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.md))
                Column(modifier = Modifier.weight(1f)) {
                    Text("What if?", color = TextPrimary, fontSize = LoanMasterTheme.typography.body.fontSize, fontWeight = FontWeight.Bold)
                    ScrollingTitleText("Adjust values and see how your wealth changes", color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize)
                }
                Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.md))
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(LoanMasterTheme.spacing.sm)).border(1.dp, CardStroke, RoundedCornerShape(LoanMasterTheme.spacing.sm)).clickable { if (!isPremiumUnlocked) showUnlockDialog = true }.padding(horizontal = LoanMasterTheme.spacing.md, vertical = LoanMasterTheme.spacing.sm)
                ) {
                    Text("Explore Scenarios ->", color = TextPrimary, fontSize = LoanMasterTheme.typography.label.fontSize)
                }
            }

            // Projection Summary
            Column(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(LoanMasterTheme.spacing.md)).background(SurfaceDark).border(1.dp, CardStroke, RoundedCornerShape(LoanMasterTheme.spacing.md)).padding(top = LoanMasterTheme.spacing.md, bottom = LoanMasterTheme.spacing.sm)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(start = LoanMasterTheme.spacing.md, end = LoanMasterTheme.spacing.md, bottom = LoanMasterTheme.spacing.md),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("RD Projection Summary", color = TextPrimary, fontSize = LoanMasterTheme.typography.body.fontSize, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.xs))
                        Icon(Icons.Rounded.Info, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(LoanMasterTheme.spacing.md))
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { if (!isPremiumUnlocked) showUnlockDialog = true }) {
                        Text("View Full Yearly Report", color = AccentBlue, fontSize = LoanMasterTheme.typography.label.fontSize)
                        Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = AccentBlue, modifier = Modifier.size(LoanMasterTheme.spacing.md))
                    }
                }
                
                Column(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
                    Row(modifier = Modifier.defaultMinSize(minWidth = 380.dp).fillMaxWidth().background(CardStroke.copy(alpha = 0.5f)).padding(vertical = LoanMasterTheme.spacing.gridGutter, horizontal = LoanMasterTheme.spacing.md)) {
                        Text("Year", color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize, modifier = Modifier.weight(1f).padding(horizontal = LoanMasterTheme.spacing.xs))
                        Text("Total Invested", color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize, modifier = Modifier.weight(1.5f).padding(horizontal = LoanMasterTheme.spacing.xs), textAlign = TextAlign.Center)
                        Text("Est. Returns", color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize, modifier = Modifier.weight(1.5f).padding(horizontal = LoanMasterTheme.spacing.xs), textAlign = TextAlign.Center)
                        Text("Maturity Value", color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize, modifier = Modifier.weight(1.5f).padding(horizontal = LoanMasterTheme.spacing.xs), textAlign = TextAlign.End)
                    }
                    
                    val yearsList = listOf(1.0, 3.0, 5.0, 10.0)
                    yearsList.forEachIndexed { index, y ->
                        val tMonths = (y * 12).toInt()
                        val tInvested = calculatedMonthlyDeposit * tMonths
                        var tMat = 0.0
                        if (annualRate > 0 && tMonths > 0) {
                            for (i in 1..tMonths) {
                                val remainingTimeYears = (tMonths - i + 1) / 12.0
                                tMat += calculatedMonthlyDeposit * (1 + annualRate / n).pow(n * remainingTimeYears)
                            }
                        } else {
                            tMat = calculatedMonthlyDeposit * tMonths
                        }
                        val tRet = tMat - tInvested
                        val isLast = index == yearsList.size - 1
                        val color = if (isLast) AccentGreen else TextPrimary
                        
                        Row(modifier = Modifier.defaultMinSize(minWidth = 380.dp).fillMaxWidth().padding(vertical = LoanMasterTheme.spacing.md, horizontal = LoanMasterTheme.spacing.md)) {
                            Text("${y.toInt()} Years", color = TextPrimary, fontSize = LoanMasterTheme.typography.label.fontSize, modifier = Modifier.weight(1f).padding(horizontal = LoanMasterTheme.spacing.xs))
                            Text(formatInr(tInvested), color = TextPrimary, fontSize = LoanMasterTheme.typography.label.fontSize, modifier = Modifier.weight(1.5f).padding(horizontal = LoanMasterTheme.spacing.xs), textAlign = TextAlign.Center)
                            Text(formatInr(tRet), color = TextPrimary, fontSize = LoanMasterTheme.typography.label.fontSize, modifier = Modifier.weight(1.5f).padding(horizontal = LoanMasterTheme.spacing.xs), textAlign = TextAlign.Center)
                            Text(formatInr(tMat), color = color, fontSize = LoanMasterTheme.typography.label.fontSize, fontWeight = if(isLast) FontWeight.Bold else FontWeight.Normal, modifier = Modifier.weight(1.5f).padding(horizontal = LoanMasterTheme.spacing.xs), textAlign = TextAlign.End)
                        }
                        if (!isLast) HorizontalDivider(color = CardStroke)
                    }
                }
                Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.xs))
                Text("* Values are rounded off", color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize, modifier = Modifier.padding(horizontal = LoanMasterTheme.spacing.md))
            }

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.gridGutter)
            ) {
                RdActionButton("RD Schedule", "Monthly Breakdown", Icons.Rounded.DateRange, AccentBlue) {
                    if (!isPremiumUnlocked) showUnlockDialog = true
                }
                RdActionButton("Charts", "Visual Analysis", Icons.Rounded.PieChart, Color(0xFF7C4DFF)) {
                    if (!isPremiumUnlocked) showUnlockDialog = true
                }
                RdActionButton("Download Report", "Save as PDF", Icons.Rounded.Download, AccentGreen) {
                    if (!isPremiumUnlocked) showUnlockDialog = true
                }
                val context = androidx.compose.ui.platform.LocalContext.current
                RdActionButton("Export PDF", "Save Report", Icons.Rounded.PictureAsPdf, AccentYellow) {
                    ExportUtils.exportToPdf(
                        context,
                        "RD Calculator Report",
                        listOf(
                            "Monthly Deposit" to formatInr(calculatedMonthlyDeposit),
                            "Interest Rate" to "$interestRatePaText%",
                            "Time Period" to "$tenureYearsText Years",
                            "" to "",
                            "Total Invested" to formatInr(totalInvested),
                            "Total Returns" to formatInr(totalReturns),
                            "Maturity Value" to formatInr(maturityValue)
                        )
                    )
                }
            } // Closes Row for action buttons
            } // Closes Column
            
            if (isLocked) {
                Box(modifier = Modifier.matchParentSize().background(BackgroundDark.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clip(RoundedCornerShape(LoanMasterTheme.spacing.md)).background(SurfaceDark).border(1.dp, CardStroke, RoundedCornerShape(LoanMasterTheme.spacing.md)).padding(LoanMasterTheme.spacing.lg).clickable { showUnlockDialog = true }
                    ) {
                        Box(modifier = Modifier.size(LoanMasterTheme.components.buttonHeight).clip(CircleShape).background(AccentYellow.copy(alpha = 0.2f)), contentAlignment = Alignment.Center) {
                            Icon(Icons.Rounded.Lock, contentDescription = "Premium", tint = AccentYellow, modifier = Modifier.size(LoanMasterTheme.components.iconMedium))
                        }
                        Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.md))
                        Text("Goal-Based RD is Premium", color = TextPrimary, fontSize = LoanMasterTheme.typography.body.fontSize, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.sm))
                        Text("Calculate exactly how much to invest.", color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize)
                        Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.md))
                        Button(
                            onClick = { showUnlockDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentYellow, contentColor = Color(0xFF020B1F))
                        ) {
                            Text("Unlock Premium", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
          }
        }
    )
    
    if (showUnlockDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showUnlockDialog = false },
            containerColor = Color(0xFF0D1B36),
            titleContentColor = Color.White,
            textContentColor = TextSecondary,
            title = {
                Text("Unlock Premium Features", fontWeight = FontWeight.Bold)
            },
            text = {
                Text("Goal-Based RD calculations, detailed schedules, charts, and downloadable PDF reports are premium features. Watch a short ad or upgrade to Premium to unlock unlimited access!")
            },
            confirmButton = {
                androidx.compose.material3.Button(
                    onClick = {
                        isPremiumUnlocked = true
                        showUnlockDialog = false
                    },
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = AccentYellow, contentColor = Color(0xFF020B1F))
                ) {
                    Text("Watch Ad to Unlock", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(
                    onClick = {
                        isPremiumUnlocked = true
                        showUnlockDialog = false
                    }
                ) {
                    Text("Buy Premium", color = AccentBlue)
                }
            }
        )
    }
  }
 }
}

@Composable
fun RdInputCard(title: String, value: String, subValue: String?, icon: ImageVector, iconColor: Color, onClick: () -> Unit = {}) {
    ResponsiveCard(
        minWidth = LoanMasterTheme.components.featuredCardHeight,
        onClick = onClick,
        modifier = Modifier.wrapContentWidth()
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(LoanMasterTheme.spacing.lg).clip(CircleShape).background(iconColor.copy(alpha = 0.2f)), contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(LoanMasterTheme.spacing.md))
                }
                Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.sm))
                ScrollingTitleText(title, color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize)
            }
            Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.md))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.padding(end = LoanMasterTheme.spacing.sm)) {
                    AutoResizedText(value, color = TextPrimary, fontSize = LoanMasterTheme.typography.body.fontSize, fontWeight = FontWeight.Bold)
                    if (subValue != null) {
                        ScrollingTitleText(subValue, color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize)
                    }
                }
                Icon(Icons.Rounded.Edit, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(LoanMasterTheme.spacing.md))
            }
        }
    }
}

@Composable
fun RdLegend(label: String, value: String, percentage: String, color: Color, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(LoanMasterTheme.spacing.sm).clip(CircleShape).background(color))
            Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.sm))
            ScrollingTitleText(label, color = TextPrimary, fontSize = LoanMasterTheme.typography.label.fontSize)
        }
        AutoResizedText(value, color = TextPrimary, fontSize = LoanMasterTheme.typography.body.fontSize, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = LoanMasterTheme.spacing.md))
        ScrollingTitleText(percentage, color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize, modifier = Modifier.padding(start = LoanMasterTheme.spacing.md))
    }
}

@Composable
fun RdActionButton(title: String, subtitle: String, icon: ImageVector, color: Color, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier.defaultMinSize(minWidth = LoanMasterTheme.components.featuredCardHeight).clip(RoundedCornerShape(LoanMasterTheme.spacing.sm)).background(SurfaceDark).border(1.dp, CardStroke, RoundedCornerShape(LoanMasterTheme.spacing.sm)).clickable(onClick = onClick).padding(LoanMasterTheme.spacing.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(LoanMasterTheme.spacing.xl).clip(CircleShape).background(color.copy(alpha = 0.2f)), contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(LoanMasterTheme.components.iconSmall))
        }
        Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.gridGutter))
        Column(modifier = Modifier.weight(1f, fill = false)) {
            ScrollingTitleText(title, color = TextPrimary, fontSize = LoanMasterTheme.typography.label.fontSize, fontWeight = FontWeight.Bold)
            ScrollingTitleText(subtitle, color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize)
        }
    }
}
