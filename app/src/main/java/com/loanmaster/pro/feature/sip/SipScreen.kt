package com.loanmaster.pro.feature.sip

import com.loanmaster.pro.domain.model.*
import com.loanmaster.pro.feature.gst.*
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


import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.Lock
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
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.draw.blur
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.focus.onFocusChanged

// --- Colors ---
private val NavyBg = Color(0xFF070D1B) // Very dark blue/black
private val CardBg = Color(0xFF0F172A) // Slate slate-900
private val BluePrimary = Color(0xFF2563EB) // Blue-600
private val GoldAccent = Color(0xFFEAB308) // Yellow-500
private val GreenSuccess = Color(0xFF22C55E) // Green-500
private val TextSec = Color(0xFF94A3B8) // Slate-400
private val StrokeNavy = Color(0xFF1E293B) // Slate-800
private val BadgeBg = Color(0xFF1E3A8A)


@Composable
fun SipScreen(
    onNavigateBack: () -> Unit,
    historyViewModel: HistoryViewModel? = null,
    initialHistory: CalculationHistory? = null,
    onHistoryConsumed: () -> Unit = {}
,
    viewModel: SipViewModel = viewModel()
) {
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    val configuration = LocalConfiguration.current
    val isWide = configuration.screenWidthDp > 600

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val dummyCurrency = com.loanmaster.pro.LocalCurrency.current
    var showPremiumDialog by rememberSaveable { mutableStateOf(false) }
    
    val amountText = uiState.amountText
    val returnRateText = uiState.returnRateText
    val yearsText = uiState.yearsText
    val stepUpText = uiState.stepUpText
    val currentHistoryId = uiState.currentHistoryId
    
    val totalInvested = uiState.totalInvested
    val totalGain = uiState.totalGain
    val maturityValue = uiState.maturityValue
    val yearlyDataList = uiState.yearlyDataList
    val hasValidInput = uiState.hasValidInput
    
    val returnRate = returnRateText.toDoubleOrNull() ?: 0.0
    val years = yearsText.toIntOrNull() ?: 0

    LaunchedEffect(initialHistory) {
        if (initialHistory != null) {
            viewModel.updateInputs(history = initialHistory)
            onHistoryConsumed()
        }
    }
    
    val currencySymbol = com.loanmaster.pro.LocalCurrencySymbol.current
    LaunchedEffect(amountText, currencySymbol, returnRateText, yearsText, stepUpText) {
        kotlinx.coroutines.delay(2000)
        if (historyViewModel != null && hasValidInput) {
            val history = CalculationHistory(
                id = currentHistoryId,
                calculatorType = "SIP",
                title = "${currencySymbol}$amountText for $yearsText Yrs at $returnRateText%",
                param1 = amountText,
                param2 = returnRateText,
                param3 = yearsText,
                param4 = stepUpText
            )
            historyViewModel.insert(history) { id ->
                viewModel.updateInputs(historyId = id)
            }
        }
    }

    val context = androidx.compose.ui.platform.LocalContext.current
    Scaffold(
        containerColor = NavyBg,
        topBar = {
            SipTopBar(
                onNavigateBack = onNavigateBack,
                onExportClick = {
                    ExportUtils.exportToPdf(
                        context,
                        "SIP Calculator Report",
                        listOf(
                            "Monthly Investment" to amountText,
                            "Expected Return Rate" to "$returnRateText%",
                            "Time Period" to "$yearsText Years",
                            "Annual Step-Up" to "$stepUpText%",
                            "" to "",
                            "Total Invested" to com.loanmaster.pro.core.formatter.formatMoney(totalInvested),
                            "Est. Returns" to com.loanmaster.pro.core.formatter.formatMoney(totalGain),
                            "Total Value" to com.loanmaster.pro.core.formatter.formatMoney(maturityValue)
                        )
                    )
                }
            )
        },
        modifier = Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() }, indication = null
        ) { focusManager.clearFocus() }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = if (isWide) LoanMasterTheme.spacing.xl else LoanMasterTheme.spacing.md, vertical = LoanMasterTheme.spacing.lg),
            verticalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.lg)
        ) {
            InputsSection(
                uiState = uiState, 
                updateInputs = { amount, rate, years, stepUp ->
                    viewModel.updateInputs(amount = amount, rate = rate, years = years, stepUp = stepUp)
                }, 
                isWide = isWide
            )
            
            if (hasValidInput) {
                HeroCard(totalInvested, totalGain, maturityValue, returnRate, years, isWide, uiState)
                
                if (isWide) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.lg)) {
                        GrowthVisualizationCard(yearlyDataList, modifier = Modifier.weight(1.5f))
                        Box(modifier = Modifier.weight(1f)) { InflationAdjustedCard(maturityValue, years, uiState, onUnlockPremium = { showPremiumDialog = true }) }
                    }
                } else {
                    GrowthVisualizationCard(yearlyDataList, modifier = Modifier.fillMaxWidth())
                    InflationAdjustedCard(maturityValue, years, uiState, onUnlockPremium = { showPremiumDialog = true })
                }
                
                LifestyleFundsSection(isWide, maturityValue, years)
                
                WealthOpportunityCard(maturityValue, uiState, onUnlockPremium = { showPremiumDialog = true })
                
                SipScheduleCard(yearlyDataList, uiState, onUnlockPremium = { showPremiumDialog = true })
            } else {
                EmptyStateUi()
            }
            
            if (showPremiumDialog) {
                val dialogContext = androidx.compose.ui.platform.LocalContext.current
        com.loanmaster.pro.core.ui.PremiumUnlockDialog(
                    onDismiss = { showPremiumDialog = false },
                    onUnlockSuccessful = { viewModel.unlockPremium() }
                )
            }
            Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.xl))

        }
    }
}



@Composable
private fun SipTopBar(onNavigateBack: () -> Unit, onExportClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(NavyBg)
            .statusBarsPadding()
            .padding(horizontal = LoanMasterTheme.spacing.md, vertical = LoanMasterTheme.spacing.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier.size(LoanMasterTheme.components.iconLarge).clip(CircleShape).background(Color.Transparent).border(1.dp, StrokeNavy, CircleShape)
        ) {
            Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back", tint = Color.White)
        }
        Spacer(Modifier.widthIn(min = LoanMasterTheme.spacing.md))
        Column(modifier = Modifier.weight(1f)) {
            Text("SIP Calculator", color = Color.White, fontSize = LoanMasterTheme.typography.title.fontSize, fontWeight = FontWeight.Bold, lineHeight = LoanMasterTheme.typography.display.fontSize)
            Text("Plan your investments", color = TextSec, fontSize = LoanMasterTheme.typography.label.fontSize, lineHeight = LoanMasterTheme.typography.title.fontSize)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.md), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onExportClick, modifier = Modifier.size(LoanMasterTheme.components.iconLarge).clip(CircleShape).border(1.dp, StrokeNavy, CircleShape)) {
                Icon(Icons.Rounded.PictureAsPdf, contentDescription = "Export PDF", tint = Color.White, modifier = Modifier.size(LoanMasterTheme.spacing.lg))
            }
        }
    }
}



@Composable
private fun InputsSection(
    uiState: SipUiState,
    updateInputs: (amount: String?, rate: String?, years: String?, stepUp: String?) -> Unit,
    isWide: Boolean
) {
    val amount = uiState.amountText
    val returnRate = uiState.returnRateText
    val years = uiState.yearsText
    val stepUp = uiState.stepUpText
    val onAmount: (String) -> Unit = { updateInputs(it, null, null, null) }
    val onRate: (String) -> Unit = { updateInputs(null, it, null, null) }
    val onYears: (String) -> Unit = { updateInputs(null, null, it, null) }
    val onStepUp: (String) -> Unit = { updateInputs(null, null, null, it) }

    if (isWide) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.lg)) {
            Box(Modifier.weight(1f)) { CustomInput("Monthly SIP", amount, onAmount, Icons.Rounded.Edit, prefix = com.loanmaster.pro.core.formatter.currentCurrencySymbol) }
            Box(Modifier.weight(1f)) { CustomInput("Expected Return", returnRate, onRate, Icons.Rounded.Edit, suffix = "%") }
            Box(Modifier.weight(1f)) { CustomInput("Period", years, onYears, Icons.Rounded.KeyboardArrowDown, suffix = " Yr") }
            Box(Modifier.weight(1f)) { CustomInput("Step-Up", stepUp, onStepUp, Icons.Rounded.Edit, suffix = "%") }
        }
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.md)) {
            com.loanmaster.pro.core.responsive.AdaptiveRowCol(
                modifier = Modifier.fillMaxWidth(),
                content1 = { mod -> Box(mod) { CustomInput("Monthly SIP", amount, onAmount, Icons.Rounded.Edit, prefix = com.loanmaster.pro.core.formatter.currentCurrencySymbol) } },
                content2 = { mod -> Box(mod) { CustomInput("Return Rate", returnRate, onRate, Icons.Rounded.Edit, suffix = "%") } }
            )
            com.loanmaster.pro.core.responsive.AdaptiveRowCol(
                modifier = Modifier.fillMaxWidth(),
                content1 = { mod -> Box(mod) { CustomInput("Period", years, onYears, Icons.Rounded.KeyboardArrowDown, suffix = " Yr") } },
                content2 = { mod -> Box(mod) { CustomInput("Step-Up", stepUp, onStepUp, Icons.Rounded.Edit, suffix = "%") } }
            )
        }
    }
}

@Composable
private fun CustomInput(label: String, value: String, onValueChange: (String) -> Unit, icon: androidx.compose.ui.graphics.vector.ImageVector, prefix: String = "", suffix: String = "") {
    var isFocused by remember { mutableStateOf(false) }
    
    val targetBorderColor = if (isFocused) AccentGreen else StrokeNavy
    val borderColor by androidx.compose.animation.animateColorAsState(targetValue = targetBorderColor, label = "borderColor")
    val targetBorderWidth = if (isFocused) 2.dp else 1.dp
    val borderWidth by androidx.compose.animation.core.animateDpAsState(targetValue = targetBorderWidth, label = "borderWidth")

    Column {
        Text(label, color = if (isFocused) AccentGreen else TextSec, fontSize = LoanMasterTheme.typography.label.fontSize, lineHeight = LoanMasterTheme.typography.body.fontSize, minLines = 1, maxLines = 2, overflow = TextOverflow.Ellipsis)
        Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.xs))
        Box(
            modifier = Modifier.fillMaxWidth().heightIn(min = LoanMasterTheme.components.buttonHeight).background(Color.Transparent).border(borderWidth, borderColor, RoundedCornerShape(LoanMasterTheme.spacing.sm)),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = LoanMasterTheme.spacing.md), verticalAlignment = Alignment.CenterVertically) {
                if (prefix.isNotEmpty()) {
                    Text(prefix, color = Color.White, fontSize = LoanMasterTheme.typography.body.fontSize, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.widthIn(min = LoanMasterTheme.spacing.sm))
                }
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(color = Color.White, fontSize = LoanMasterTheme.typography.body.fontSize, fontWeight = FontWeight.Bold),
                    modifier = Modifier.weight(1f).fillMaxHeight().wrapContentHeight(Alignment.CenterVertically).onFocusChanged { isFocused = it.isFocused }
                )
                if (suffix.isNotEmpty()) {
                    Text(suffix, color = Color.White, fontSize = LoanMasterTheme.typography.body.fontSize, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.widthIn(min = LoanMasterTheme.spacing.md))
                Icon(icon, contentDescription = null, tint = TextSec, modifier = Modifier.size(LoanMasterTheme.components.iconSmall))
            }
        }
    }
}

@Composable
private fun HeroStat(label: String, value: String, color: Color = Color.White, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.Top) {
            Text(label, color = TextSec, fontSize = LoanMasterTheme.typography.label.fontSize, lineHeight = LoanMasterTheme.typography.body.fontSize, minLines = 1, maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
            Spacer(Modifier.widthIn(min = LoanMasterTheme.spacing.xs))
            Icon(Icons.Rounded.Info, contentDescription = null, tint = TextSec, modifier = Modifier.size(LoanMasterTheme.spacing.md).padding(top = LoanMasterTheme.spacing.xs))
        }
        Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.xs))
        AutoSizeText(if (label.contains("Multiplier") || label.contains("Return") && !label.contains("Total")) value else value, color = color, maxTextSize = LoanMasterTheme.typography.body.fontSize, lineHeight = LoanMasterTheme.typography.title.fontSize, fontWeight = FontWeight.Bold, maxLines = 1)
    }
}

private val exactMoneyFormatter = java.text.NumberFormat.getNumberInstance(java.util.Locale.Builder().setLanguage("en").setRegion("IN").build()).apply {
    maximumFractionDigits = 0
}



@Composable
private fun HeroCard(invested: Double, gain: Double, maturity: Double, ret: Double, years: Int, isWide: Boolean, uiState: SipUiState) {
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val heroFontSize = when {
        screenWidth >= 480 -> LoanMasterTheme.typography.display.fontSize
        screenWidth >= 393 -> 38.sp
        else -> 34.sp
    }
    val heroLineHeight = when {
        screenWidth >= 480 -> 48.sp
        screenWidth >= 393 -> 44.sp
        else -> LoanMasterTheme.typography.display.fontSize
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(LoanMasterTheme.spacing.md))
            .background(Color(0xFF0C162C))
            .border(1.dp, Color(0xFF1E3A8A).copy(alpha=0.5f), RoundedCornerShape(LoanMasterTheme.spacing.md))
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(LoanMasterTheme.spacing.lg)) {
            // Top Section
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Estimated Maturity Value", color = TextSec, fontSize = LoanMasterTheme.typography.body.fontSize, minLines = 1, maxLines = 2, overflow = TextOverflow.Ellipsis)
                        Spacer(Modifier.widthIn(min = LoanMasterTheme.spacing.sm))
                        Icon(Icons.Rounded.Info, contentDescription = null, tint = TextSec, modifier = Modifier.size(LoanMasterTheme.spacing.md))
                    }
                    Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.sm))
                    AutoSizeText(
                        com.loanmaster.pro.core.formatter.formatMoney(maturity), 
                        color = GoldAccent, 
                        minTextSize = LoanMasterTheme.typography.title.fontSize,
                        maxTextSize = heroFontSize, 
                        lineHeight = heroLineHeight,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1
                    )
                    Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.xs))
                    val adjustedValue = uiState.inflationAdjustedValue
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clip(RoundedCornerShape(LoanMasterTheme.spacing.xs)).background(Color.White.copy(alpha=0.05f)).padding(horizontal = LoanMasterTheme.spacing.sm, vertical = LoanMasterTheme.spacing.xs)) {
                        Text("Real Value Today: ", color = TextSec, fontSize = LoanMasterTheme.typography.label.fontSize, maxLines = 1)
                        Text(com.loanmaster.pro.core.formatter.formatMoney(adjustedValue), color = Color.White, fontSize = LoanMasterTheme.typography.label.fontSize, fontWeight = FontWeight.SemiBold, maxLines = 1)
                    }
                }
                
                // Piggy bank icon with glow
                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(start = LoanMasterTheme.spacing.md)) {
                    Box(modifier = Modifier.size(LoanMasterTheme.components.iconLarge).blur(LoanMasterTheme.spacing.lg).background(BluePrimary.copy(alpha = 0.6f), CircleShape))
                    Icon(
                        Icons.Rounded.Savings, 
                        contentDescription = "Savings", 
                        tint = BluePrimary, 
                        modifier = Modifier.size(LoanMasterTheme.components.topAppBarHeight)
                    )
                }
            }
            
            Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.lg))
            HorizontalDivider(color = Color(0xFF1E3A8A).copy(alpha=0.5f))
            Spacer(Modifier.heightIn(min = LoanMasterTheme.components.iconSmall))
            
            // Stats Row
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                HeroStat("Total Invested", com.loanmaster.pro.core.formatter.formatMoney(invested), modifier = Modifier.weight(1f))
                HeroStat("Total Returns", com.loanmaster.pro.core.formatter.formatMoney(gain), color = GreenSuccess, modifier = Modifier.weight(1f).padding(start = LoanMasterTheme.spacing.sm))
                if (isWide) {
                    HeroStat("Wealth Multiplier", "${String.format("%.2f", maturity/invested)}x", color = GreenSuccess, modifier = Modifier.weight(1f).padding(start = LoanMasterTheme.spacing.sm))
                    HeroStat("Avg. Return", "${ret.toInt()}%", modifier = Modifier.weight(1f).padding(start = LoanMasterTheme.spacing.sm))
                } else {
                    HeroStat("Multiplier", "${String.format("%.2f", maturity/invested)}x", color = GreenSuccess, modifier = Modifier.weight(0.7f).padding(start = LoanMasterTheme.spacing.sm))
                }
            }
        }
    }
}

@Composable
private fun GrowthVisualizationCard(yearlyDataList: List<YearlyData>, modifier: Modifier = Modifier) {
    Column(modifier = modifier.heightIn(min = LoanMasterTheme.components.chartHeight).clip(RoundedCornerShape(LoanMasterTheme.spacing.md)).background(Color(0xFF0C162C)).border(1.dp, Color(0xFF1E3A8A).copy(alpha=0.5f), RoundedCornerShape(LoanMasterTheme.spacing.md)).padding(LoanMasterTheme.spacing.md)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Investment Growth", color = Color.White, fontSize = LoanMasterTheme.typography.body.fontSize, fontWeight = FontWeight.Bold)
            Icon(Icons.Rounded.MoreVert, contentDescription = null, tint = TextSec, modifier = Modifier.size(LoanMasterTheme.spacing.md))
        }
        Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.md))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.sm)) {
            Column(modifier = Modifier.weight(1f)) {
                LegendDot("Maturity Value", BluePrimary)
            }
            Column(modifier = Modifier.weight(1f)) {
                LegendDot("Invested", GreenSuccess)
            }
        }
        Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.md))
        
        val maxMaturity = yearlyDataList.maxOfOrNull { it.maturity } ?: 1.0
        val maxYValue = maxMaturity * 1.1 // Add 10% padding to top
        val maxYears = yearlyDataList.maxOfOrNull { it.year } ?: 1
        
        Column(modifier = Modifier.weight(1f).fillMaxWidth()) {
            Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                // Y-Axis
                Column(modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.SpaceBetween) {
                    Text(com.loanmaster.pro.core.formatter.formatMoney(maxYValue), color = TextSec, fontSize = LoanMasterTheme.typography.label.fontSize)
                    Text(com.loanmaster.pro.core.formatter.formatMoney(maxYValue * 0.66), color = TextSec, fontSize = LoanMasterTheme.typography.label.fontSize)
                    Text(com.loanmaster.pro.core.formatter.formatMoney(maxYValue * 0.33), color = TextSec, fontSize = LoanMasterTheme.typography.label.fontSize)
                    Text("0", color = TextSec, fontSize = LoanMasterTheme.typography.label.fontSize)
                }
                Spacer(Modifier.widthIn(min = LoanMasterTheme.spacing.sm))
                // Chart
                Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height
                        
                        // Grid lines
                        for(i in 1..4) {
                            val x = w * (i / 4f)
                            drawLine(Color.White.copy(alpha=0.05f), Offset(x, 0f), Offset(x, h), 1f)
                        }
                        for(i in 0..3) {
                            val y = h * (i / 3f)
                            drawLine(Color.White.copy(alpha=0.05f), Offset(0f, y), Offset(w, y), 1f)
                        }
                        
                        if (yearlyDataList.isEmpty()) return@Canvas
                        
                        // Curve 1 (Portfolio)
                        val path1 = Path()
                        path1.moveTo(0f, h)
                        yearlyDataList.forEach { data ->
                            val x = (data.year.toFloat() / maxYears) * w
                            val y = h - ((data.maturity / maxYValue).toFloat() * h)
                            path1.lineTo(x, y)
                        }
                        drawPath(path1, BluePrimary, style = Stroke(6f, cap = StrokeCap.Round, join = StrokeJoin.Round))
                        
                        // Curve 2 (Invested)
                        val path2 = Path()
                        path2.moveTo(0f, h)
                        yearlyDataList.forEach { data ->
                            val x = (data.year.toFloat() / maxYears) * w
                            val y = h - ((data.totalInvested / maxYValue).toFloat() * h)
                            path2.lineTo(x, y)
                        }
                        drawPath(path2, GreenSuccess, style = Stroke(6f, cap = StrokeCap.Round, join = StrokeJoin.Round))
                        
                        // Markers
                        val step = (yearlyDataList.size / 4).coerceAtLeast(1)
                        for (i in step..yearlyDataList.size step step) {
                            val data = yearlyDataList.getOrNull(i - 1) ?: continue
                            val x = (data.year.toFloat() / maxYears) * w
                            val y1 = h - ((data.maturity / maxYValue).toFloat() * h)
                            val y2 = h - ((data.totalInvested / maxYValue).toFloat() * h)
                            
                            drawCircle(BluePrimary, 8f, Offset(x, y1))
                            drawCircle(Color.White, 4f, Offset(x, y1))
                            
                            drawCircle(GreenSuccess, 8f, Offset(x, y2))
                            drawCircle(Color.White, 4f, Offset(x, y2))
                        }
                    }
                }
            }
            Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.sm))
            Row(modifier = Modifier.fillMaxWidth().padding(start = LoanMasterTheme.spacing.xl), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("0Y", color = Color.Transparent, fontSize = LoanMasterTheme.typography.label.fontSize)
                Text("${maxYears / 4}Y", color = TextSec, fontSize = LoanMasterTheme.typography.label.fontSize)
                Text("${maxYears / 2}Y", color = TextSec, fontSize = LoanMasterTheme.typography.label.fontSize)
                Text("${(maxYears * 3) / 4}Y", color = TextSec, fontSize = LoanMasterTheme.typography.label.fontSize)
                Text("${maxYears}Y", color = TextSec, fontSize = LoanMasterTheme.typography.label.fontSize)
            }
        }
    }
}

@Composable
private fun LegendDot(lbl: String, col: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(LoanMasterTheme.spacing.gridGutter).clip(CircleShape).background(col))
        Spacer(Modifier.widthIn(min = LoanMasterTheme.spacing.sm))
        Text(lbl, color = TextSec, fontSize = LoanMasterTheme.typography.label.fontSize)
    }
}

@Composable
private fun LifestyleFundsSection(isWide: Boolean, maturityValue: Double, years: Int) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("With this corpus, you can afford:", color = Color.White, fontSize = LoanMasterTheme.typography.title.fontSize, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.md))
        
        data class FutureGoal(val title: String, val target: Double, val icon: androidx.compose.ui.graphics.vector.ImageVector)
        val items = mutableListOf<FutureGoal>()
        if (maturityValue > 5000000) {
            items.add(FutureGoal("Premium Apartment", 15000000.0, Icons.Rounded.Home))
            items.add(FutureGoal("Luxury SUV", 7500000.0, Icons.Rounded.DirectionsCar))
            items.add(FutureGoal("Emergency Fund", 2500000.0, Icons.Rounded.AccountBalanceWallet))
            items.add(FutureGoal("Child Education", 5000000.0, Icons.Rounded.School))
        } else if (maturityValue > 1500000) {
            items.add(FutureGoal("Home Downpayment", 2500000.0, Icons.Rounded.Home))
            items.add(FutureGoal("Mahindra XUV700", 2200000.0, Icons.Rounded.DirectionsCar))
            items.add(FutureGoal("Emergency Fund", 1000000.0, Icons.Rounded.AccountBalanceWallet))
            items.add(FutureGoal("Child Education", 2000000.0, Icons.Rounded.School))
        } else {
            items.add(FutureGoal("Honda City", 1200000.0, Icons.Rounded.DirectionsCar))
            items.add(FutureGoal("International Travel", 600000.0, Icons.Rounded.FlightTakeoff))
            items.add(FutureGoal("Emergency Fund", 500000.0, Icons.Rounded.AccountBalanceWallet))
            items.add(FutureGoal("Child Education", 1000000.0, Icons.Rounded.School))
        }
        
        val columns = if (isWide) 4 else 2
        val chunked = items.chunked(columns)
        
        Column(verticalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.md)) {
            chunked.forEach { rowItems ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.md)) {
                    rowItems.forEach { item ->
                        LifeCard(
                            title = item.title,
                            targetAmount = item.target,
                            maturityValue = maturityValue,
                            years = years,
                            icon = item.icon,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (rowItems.size < columns) {
                        repeat(columns - rowItems.size) {
                            Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LifeCard(title: String, targetAmount: Double, maturityValue: Double, years: Int, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier = Modifier) {
    val percent = ((maturityValue / targetAmount) * 100).toInt()
    val progress = (maturityValue / targetAmount).coerceAtMost(1.0)
    val shortAmount = (targetAmount - maturityValue).coerceAtLeast(0.0)

    Column(
        modifier = modifier.clip(RoundedCornerShape(LoanMasterTheme.spacing.md))
            .background(Brush.linearGradient(listOf(Color(0xFF1E293B), Color(0xFF0F172A))))
            .border(1.dp, Color.White.copy(alpha=0.1f), RoundedCornerShape(LoanMasterTheme.spacing.md))
            .padding(LoanMasterTheme.spacing.md),
        horizontalAlignment = Alignment.Start
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = BluePrimary, modifier = Modifier.size(LoanMasterTheme.spacing.md))
            Spacer(Modifier.widthIn(min = LoanMasterTheme.spacing.sm))
            Text(title, color = Color.White, fontSize = LoanMasterTheme.typography.body.fontSize, fontWeight = FontWeight.Bold, minLines = 1, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
        Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.md))
        
        Text("$percent% Achieved", color = if (percent >= 100) GreenSuccess else GoldAccent, fontSize = LoanMasterTheme.typography.label.fontSize, fontWeight = FontWeight.SemiBold, minLines = 1, maxLines = 2, overflow = TextOverflow.Ellipsis)
        Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.sm))
        Box(modifier = Modifier.fillMaxWidth().heightIn(min = LoanMasterTheme.spacing.xs).clip(CircleShape).background(Color.White.copy(alpha=0.1f))) {
            Box(modifier = Modifier.fillMaxWidth(progress.toFloat()).heightIn(min = LoanMasterTheme.spacing.xs).clip(CircleShape).background(if (percent >= 100) GreenSuccess else BluePrimary))
        }
        
        Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.sm))
        if (shortAmount > 0) {
            Text("${com.loanmaster.pro.core.formatter.formatMoney(shortAmount)} Short", color = TextSec, fontSize = LoanMasterTheme.typography.label.fontSize, minLines = 1, maxLines = 2, overflow = TextOverflow.Ellipsis)
        } else {
            Text("Goal Reached", color = GreenSuccess, fontSize = LoanMasterTheme.typography.label.fontSize, fontWeight = FontWeight.SemiBold, minLines = 1, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun InflationAdjustedCard(maturityValue: Double, years: Int, uiState: SipUiState, onUnlockPremium: () -> Unit) {
    val adjustedValue = uiState.inflationAdjustedValue
    val valueLost = maturityValue - adjustedValue

    Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(LoanMasterTheme.spacing.md)).background(CardBg).border(1.dp, StrokeNavy, RoundedCornerShape(LoanMasterTheme.spacing.md)).padding(LoanMasterTheme.spacing.lg)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Inflation Impact", color = Color.White, fontSize = LoanMasterTheme.typography.title.fontSize, fontWeight = FontWeight.Bold)
            Spacer(Modifier.widthIn(min = LoanMasterTheme.spacing.sm))
            Icon(Icons.Rounded.Info, contentDescription = null, tint = TextSec, modifier = Modifier.size(LoanMasterTheme.components.iconSmall))
        }
        Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.lg))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Future Corpus", color = TextSec, fontSize = LoanMasterTheme.typography.body.fontSize, modifier = Modifier.weight(1f), minLines = 1, maxLines = 2, overflow = TextOverflow.Ellipsis)
            AutoSizeText(com.loanmaster.pro.core.formatter.formatMoney(maturityValue), color = Color.White, maxTextSize = LoanMasterTheme.typography.body.fontSize, fontWeight = FontWeight.SemiBold, maxLines = 1)
        }
        Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.md))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Inflation Adjusted", color = TextSec, fontSize = LoanMasterTheme.typography.body.fontSize, modifier = Modifier.weight(1f), minLines = 1, maxLines = 2, overflow = TextOverflow.Ellipsis)
            AutoSizeText(com.loanmaster.pro.core.formatter.formatMoney(adjustedValue), color = GoldAccent, maxTextSize = LoanMasterTheme.typography.body.fontSize, fontWeight = FontWeight.SemiBold, maxLines = 1)
        }
        Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.md))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Value Lost", color = TextSec, fontSize = LoanMasterTheme.typography.body.fontSize, modifier = Modifier.weight(1f), minLines = 1, maxLines = 2, overflow = TextOverflow.Ellipsis)
            AutoSizeText("- " + com.loanmaster.pro.core.formatter.formatMoney(valueLost), color = Color(0xFFF87171), maxTextSize = LoanMasterTheme.typography.body.fontSize, fontWeight = FontWeight.SemiBold, maxLines = 1)
        }
        
        Spacer(Modifier.heightIn(min = LoanMasterTheme.components.iconSmall))
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(LoanMasterTheme.spacing.sm)).background(Color.White.copy(alpha=0.05f)).padding(LoanMasterTheme.spacing.md)) {
            Icon(Icons.AutoMirrored.Rounded.TrendingDown, contentDescription = null, tint = Color(0xFFF87171), modifier = Modifier.size(LoanMasterTheme.spacing.lg))
            Spacer(Modifier.widthIn(min = LoanMasterTheme.spacing.md))
            Column {
                Text("Purchasing Power Reduced", color = Color.White, fontSize = LoanMasterTheme.typography.body.fontSize, fontWeight = FontWeight.SemiBold)
                Text("Inflation eats into your returns.", color = TextSec, fontSize = LoanMasterTheme.typography.label.fontSize)
            }
        }
    }
}

@Composable
private fun WealthOpportunityCard(maturityValue: Double, uiState: SipUiState, onUnlockPremium: () -> Unit) {
    val potentialCorpus = maturityValue * 1.35
    val potentialGain = potentialCorpus - maturityValue

    Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(LoanMasterTheme.spacing.md)).background(Brush.horizontalGradient(listOf(Color(0xFF2C240E), CardBg))).border(1.dp, GoldAccent.copy(alpha=0.6f), RoundedCornerShape(LoanMasterTheme.spacing.md)).padding(LoanMasterTheme.spacing.lg)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Rounded.AutoAwesome, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(LoanMasterTheme.spacing.lg))
            Spacer(Modifier.widthIn(min = LoanMasterTheme.spacing.md))
            Text("Wealth Opportunity Found", color = GoldAccent, fontSize = LoanMasterTheme.typography.title.fontSize, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.sm))
        Text(
            text = buildAnnotatedString {
                append("AI analyzed your SIP strategy and ")
                withStyle(SpanStyle(color = GoldAccent)) {
                    append("found multiple opportunities")
                }
                append(" that may increase your final corpus.")
            },
            color = Color.White.copy(alpha=0.9f), fontSize = LoanMasterTheme.typography.body.fontSize, lineHeight = LoanMasterTheme.typography.title.fontSize
        )
        
        Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.lg))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Current Corpus", color = TextSec, fontSize = LoanMasterTheme.typography.label.fontSize, lineHeight = LoanMasterTheme.typography.body.fontSize, minLines = 1, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.xs))
                AutoSizeText(com.loanmaster.pro.core.formatter.formatMoney(maturityValue), color = Color.White, maxTextSize = LoanMasterTheme.typography.body.fontSize, fontWeight = FontWeight.Bold, maxLines = 1)
            }
            Box(modifier = Modifier.widthIn(min = 1.dp).heightIn(min = LoanMasterTheme.spacing.xl).background(StrokeNavy))
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1.3f)) {
                Text("Potential Corpus", color = TextSec, fontSize = LoanMasterTheme.typography.label.fontSize, lineHeight = LoanMasterTheme.typography.body.fontSize, minLines = 1, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.xs))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!uiState.isPremiumUnlocked) Icon(Icons.Rounded.Lock, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(LoanMasterTheme.spacing.md))
                    Spacer(Modifier.widthIn(min = LoanMasterTheme.spacing.xs))
                    val formatted = com.loanmaster.pro.core.formatter.formatMoney(potentialCorpus)
                    if (uiState.isPremiumUnlocked) {
                        AutoSizeText(formatted, color = GoldAccent, maxTextSize = LoanMasterTheme.typography.body.fontSize, fontWeight = FontWeight.Bold, maxLines = 1)
                    } else {
                        val blurredText = if (formatted.length > 5) formatted.take(2) + ",XX,XXX" else "XX,XXX"
                        AutoSizeText(blurredText, color = GoldAccent, maxTextSize = LoanMasterTheme.typography.body.fontSize, fontWeight = FontWeight.Bold, modifier = Modifier.blur(LoanMasterTheme.spacing.xs), maxLines = 1)
                    }
                }
            }
            Box(modifier = Modifier.widthIn(min = 1.dp).heightIn(min = LoanMasterTheme.spacing.xl).background(StrokeNavy))
            Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1f)) {
                Text("Potential Gain", color = TextSec, fontSize = LoanMasterTheme.typography.label.fontSize, lineHeight = LoanMasterTheme.typography.body.fontSize, minLines = 1, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.xs))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!uiState.isPremiumUnlocked) Icon(Icons.Rounded.Lock, contentDescription = null, tint = GreenSuccess, modifier = Modifier.size(LoanMasterTheme.spacing.md))
                    Spacer(Modifier.widthIn(min = LoanMasterTheme.spacing.xs))
                    val formattedGain = com.loanmaster.pro.core.formatter.formatMoney(potentialGain)
                    if (uiState.isPremiumUnlocked) {
                        AutoSizeText("+" + formattedGain, color = GreenSuccess, maxTextSize = LoanMasterTheme.typography.body.fontSize, fontWeight = FontWeight.Bold, maxLines = 1)
                    } else {
                        val blurredGain = if (formattedGain.length > 5) formattedGain.take(1) + ",XX,XXX" else "X,XXX"
                        AutoSizeText("+" + blurredGain, color = GreenSuccess, maxTextSize = LoanMasterTheme.typography.body.fontSize, fontWeight = FontWeight.Bold, modifier = Modifier.blur(LoanMasterTheme.spacing.xs), maxLines = 1)
                    }
                }
            }
        }
        
        Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.lg))
        val insights = if (uiState.isPremiumUnlocked) {
            uiState.premiumInsights.ifEmpty { 
                listOf(
                    "Increase annual step-up by 5% to combat inflation",
                    "Extend tenure by 3 years to maximize compounding",
                    "Rebalance 20% to mid-cap funds for better returns"
                )
            }
        } else {
            listOf("Better Step-Up Strategy", "Faster Wealth Growth", "Goal Achievement Optimization")
        }

        if (uiState.isPremiumUnlocked) {
            Text(
                "To achieve this potential corpus, our AI recommends the following strategic adjustments:",
                color = GoldAccent,
                fontSize = LoanMasterTheme.typography.label.fontSize,
                modifier = Modifier.padding(bottom = LoanMasterTheme.spacing.md)
            )
        }

        insights.forEach { text ->
           Row(modifier = Modifier.fillMaxWidth().padding(bottom = LoanMasterTheme.spacing.md), verticalAlignment = Alignment.CenterVertically) {
               Icon(if (uiState.isPremiumUnlocked) Icons.Rounded.CheckCircle else Icons.Rounded.AutoAwesome, contentDescription = null, tint = if (uiState.isPremiumUnlocked) GreenSuccess else GoldAccent, modifier = Modifier.size(LoanMasterTheme.spacing.md))
               Spacer(Modifier.widthIn(min = LoanMasterTheme.spacing.md))
               Text(text, color = Color.White, fontSize = LoanMasterTheme.typography.body.fontSize)
               Spacer(Modifier.weight(1f))
               if (!uiState.isPremiumUnlocked) {
                   Box(modifier = Modifier.widthIn(min = LoanMasterTheme.components.calculatorCardHeight).heightIn(min = LoanMasterTheme.spacing.sm).clip(CircleShape).background(Color.White.copy(alpha=0.1f)).blur(LoanMasterTheme.spacing.xs))
                   Spacer(Modifier.widthIn(min = LoanMasterTheme.spacing.md))
                   Icon(Icons.Rounded.Lock, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(LoanMasterTheme.spacing.md))
               }
           }
        }
        
        Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.md))
        if (!uiState.isPremiumUnlocked) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.md)) {
                Button(
                    onClick = { onUnlockPremium() }, modifier = Modifier.weight(1f).heightIn(min = LoanMasterTheme.components.buttonHeight),
                    colors = ButtonDefaults.buttonColors(containerColor = NavyBg.copy(alpha=0.6f), contentColor = Color.White),
                    shape = RoundedCornerShape(LoanMasterTheme.spacing.md), border = BorderStroke(1.dp, StrokeNavy),
                    contentPadding = PaddingValues(horizontal = LoanMasterTheme.spacing.sm)
                ) {
                    Icon(Icons.Rounded.PlayCircleOutline, contentDescription = null, modifier = Modifier.size(LoanMasterTheme.spacing.md))
                    Spacer(Modifier.widthIn(min = LoanMasterTheme.spacing.xs))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                       Text("Watch Ad", fontSize = LoanMasterTheme.typography.label.fontSize, fontWeight = FontWeight.Bold, minLines = 1, maxLines = 2, overflow = TextOverflow.Ellipsis)
                       Text("Unlock AI Insight", fontSize = 9.sp, color = TextSec, minLines = 1, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    }
                }
                Button(
                    onClick = { onUnlockPremium() }, modifier = Modifier.weight(1f).heightIn(min = LoanMasterTheme.components.buttonHeight),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = NavyBg),
                    shape = RoundedCornerShape(LoanMasterTheme.spacing.md),
                    contentPadding = PaddingValues(horizontal = LoanMasterTheme.spacing.sm)
                ) {
                    Icon(Icons.Rounded.WorkspacePremium, contentDescription = null, modifier = Modifier.size(LoanMasterTheme.spacing.md))
                    Spacer(Modifier.widthIn(min = LoanMasterTheme.spacing.xs))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                       Text("Premium", fontSize = LoanMasterTheme.typography.label.fontSize, fontWeight = FontWeight.Bold, minLines = 1, maxLines = 2, overflow = TextOverflow.Ellipsis)
                       Text("Unlimited AI Insights", fontSize = 9.sp, color = NavyBg.copy(alpha=0.8f), minLines = 1, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
        }
    }
}

@Composable
private fun SipScheduleCard(yearlyDataList: List<YearlyData>, uiState: SipUiState, onUnlockPremium: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(LoanMasterTheme.spacing.md)).background(CardBg).border(1.dp, StrokeNavy, RoundedCornerShape(LoanMasterTheme.spacing.md)).padding(vertical = LoanMasterTheme.spacing.lg)) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = LoanMasterTheme.spacing.lg)) {
            Text("SIP Schedule (Year-wise)", color = Color.White, fontSize = LoanMasterTheme.typography.title.fontSize, fontWeight = FontWeight.Bold)
            Spacer(Modifier.widthIn(min = LoanMasterTheme.spacing.sm))
            Icon(Icons.Rounded.Info, contentDescription = null, tint = TextSec, modifier = Modifier.size(LoanMasterTheme.components.iconSmall))
        }
        Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.lg))
        Row(modifier = Modifier.fillMaxWidth().padding(start = LoanMasterTheme.spacing.lg, end = LoanMasterTheme.spacing.lg, bottom = LoanMasterTheme.spacing.md)) {
            Text("Yr", color = TextSec, fontSize = LoanMasterTheme.typography.label.fontSize, lineHeight = LoanMasterTheme.typography.body.fontSize, modifier = Modifier.weight(0.5f), minLines = 1, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Text("Invested", color = TextSec, fontSize = LoanMasterTheme.typography.label.fontSize, lineHeight = LoanMasterTheme.typography.body.fontSize, modifier = Modifier.weight(1.3f), minLines = 1, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Text("Total", color = TextSec, fontSize = LoanMasterTheme.typography.label.fontSize, lineHeight = LoanMasterTheme.typography.body.fontSize, modifier = Modifier.weight(1.2f), minLines = 1, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Text("Returns", color = TextSec, fontSize = LoanMasterTheme.typography.label.fontSize, lineHeight = LoanMasterTheme.typography.body.fontSize, modifier = Modifier.weight(1.2f), minLines = 1, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Text("Corpus", color = BluePrimary, fontSize = LoanMasterTheme.typography.label.fontSize, lineHeight = LoanMasterTheme.typography.body.fontSize, modifier = Modifier.weight(1.2f), textAlign = TextAlign.End, minLines = 1, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
        
        val rowsToDisplay = if (uiState.isPremiumUnlocked) {
            yearlyDataList
        } else {
            listOf(1, 5, 10).mapNotNull { targetYear ->
                yearlyDataList.find { it.year == targetYear }
            }
        }
        rowsToDisplay.forEach { data ->
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = LoanMasterTheme.spacing.lg, vertical = LoanMasterTheme.spacing.md)) {
                Text(data.year.toString(), color = Color.White, fontSize = LoanMasterTheme.typography.label.fontSize, lineHeight = LoanMasterTheme.typography.body.fontSize, modifier = Modifier.weight(0.5f), minLines = 1, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text(com.loanmaster.pro.core.formatter.formatMoney(data.investedForYear), color = Color.White, fontSize = LoanMasterTheme.typography.label.fontSize, lineHeight = LoanMasterTheme.typography.body.fontSize, modifier = Modifier.weight(1.3f), minLines = 1, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text(com.loanmaster.pro.core.formatter.formatMoney(data.totalInvested), color = Color.White, fontSize = LoanMasterTheme.typography.label.fontSize, lineHeight = LoanMasterTheme.typography.body.fontSize, modifier = Modifier.weight(1.2f), minLines = 1, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text(com.loanmaster.pro.core.formatter.formatMoney(data.returns), color = GreenSuccess, fontSize = LoanMasterTheme.typography.label.fontSize, lineHeight = LoanMasterTheme.typography.body.fontSize, modifier = Modifier.weight(1.2f), minLines = 1, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text(com.loanmaster.pro.core.formatter.formatMoney(data.maturity), color = GoldAccent, fontSize = LoanMasterTheme.typography.label.fontSize, lineHeight = LoanMasterTheme.typography.body.fontSize, modifier = Modifier.weight(1.2f), textAlign = TextAlign.End, minLines = 1, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
            HorizontalDivider(color = StrokeNavy)
        }
        
        if (!uiState.isPremiumUnlocked && yearlyDataList.size > 5) {
            val remainingYears = yearlyDataList.size - 5
            Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.md))
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = LoanMasterTheme.spacing.lg).clip(RoundedCornerShape(LoanMasterTheme.spacing.sm)).background(Color.White.copy(alpha=0.05f)).padding(LoanMasterTheme.spacing.md), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                Icon(Icons.Rounded.Lock, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(LoanMasterTheme.spacing.md))
                Spacer(Modifier.widthIn(min = LoanMasterTheme.spacing.sm))
                Text("$remainingYears More Hidden", color = Color.White, fontSize = LoanMasterTheme.typography.label.fontSize, fontWeight = FontWeight.SemiBold, minLines = 1, maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                Spacer(Modifier.widthIn(min = LoanMasterTheme.spacing.sm))
                androidx.compose.material3.Button(onClick = { onUnlockPremium() }, colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = GoldAccent), contentPadding = PaddingValues(horizontal = LoanMasterTheme.spacing.md, vertical = 0.dp), modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.xl)) {
                    Text("Unlock", color = Color.Black, fontSize = LoanMasterTheme.typography.label.fontSize, fontWeight = FontWeight.Bold, maxLines = 1)
                }
            }
        }
    }
}

@Composable
fun AutoSizeText(
    text: String,
    color: Color = Color.White,
    minTextSize: TextUnit = LoanMasterTheme.typography.label.fontSize,
    maxTextSize: TextUnit = LoanMasterTheme.typography.body.fontSize,
    fontWeight: FontWeight = FontWeight.Normal,
    lineHeight: TextUnit = TextUnit.Unspecified,
    modifier: Modifier = Modifier,
    maxLines: Int = 1
) {
    var scaledFontSize by androidx.compose.runtime.remember(text, maxTextSize) { androidx.compose.runtime.mutableStateOf(maxTextSize) }
    var scaledLineHeight by androidx.compose.runtime.remember(lineHeight) { androidx.compose.runtime.mutableStateOf(lineHeight) }
    var readyToDraw by androidx.compose.runtime.remember(text) { androidx.compose.runtime.mutableStateOf(false) }

    Text(
        text = text,
        color = color,
        fontSize = scaledFontSize,
        fontWeight = fontWeight,
        lineHeight = scaledLineHeight,
        maxLines = maxLines,
        softWrap = false,
        overflow = TextOverflow.Visible,
        modifier = modifier.drawWithContent {
            if (readyToDraw) drawContent()
        },
        onTextLayout = { textLayoutResult ->
            if (textLayoutResult.hasVisualOverflow && scaledFontSize > minTextSize) {
                val nextSize = scaledFontSize * 0.9f
                if (nextSize >= minTextSize) {
                    scaledFontSize = nextSize
                    if (scaledLineHeight != TextUnit.Unspecified) {
                        scaledLineHeight *= 0.9f
                    }
                } else {
                    scaledFontSize = minTextSize
                    readyToDraw = true
                }
            } else {
                readyToDraw = true
            }
        }
    )
}

@Composable
private fun EmptyStateUi() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = LoanMasterTheme.spacing.xl, horizontal = LoanMasterTheme.spacing.md),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(Color.White.copy(alpha = 0.05f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Analytics,
                contentDescription = null,
                tint = TextSec.copy(alpha = 0.5f),
                modifier = Modifier.size(40.dp)
            )
        }
        Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.lg))
        Text(
            "Enter details to see projection",
            color = Color.White,
            fontSize = LoanMasterTheme.typography.title.fontSize,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.sm))
        Text(
            "Fill in your monthly SIP amount, expected return, and period to calculate your wealth growth.",
            color = TextSec,
            fontSize = LoanMasterTheme.typography.body.fontSize,
            textAlign = TextAlign.Center,
            lineHeight = LoanMasterTheme.typography.title.fontSize
        )
    }
}
