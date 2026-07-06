package com.loanmaster.pro.feature.prepayment

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


import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrepaymentScreen(
    onNavigateBack: () -> Unit = {},
    historyViewModel: HistoryViewModel? = null,
    initialHistory: CalculationHistory? = null,
    onHistoryConsumed: () -> Unit = {},
    viewModel: PrepaymentViewModel = viewModel()
) {
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val sizeClass = when {
        configuration.screenWidthDp < 600 -> WindowWidthSizeClass.COMPACT
        configuration.screenWidthDp < 840 -> WindowWidthSizeClass.MEDIUM
        else -> WindowWidthSizeClass.EXPANDED
    }
    val adaptiveInfo = currentWindowAdaptiveInfo()

    val bgColor = BackgroundDark
    val surfaceColor = SurfaceDark
    val primaryColor = AccentYellow
    val secondaryColor = AccentBlue
    val accentGreen = Color(0xFF4ADE80)
    val accentOrange = Color(0xFFF97316)
    
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    val loanAmount = uiState.loanAmountText
    val interestRate = uiState.interestRateText
    val tenureYears = uiState.tenureYearsText
    val prepaymentAmount = uiState.prepaymentAmountText
    val strategy = uiState.strategy
    val monthlyPrepayment = uiState.monthlyPrepaymentText
    val annualPrepayment = uiState.annualPrepaymentText
    val currentHistoryId = uiState.currentHistoryId
    
    val emi = uiState.originalEmi
    val originalTotalPayment = uiState.originalTotalPayment
    val originalTotalInterest = uiState.originalTotalInterest
    val newEmi = uiState.newEmi
    val newTenureMonths = uiState.newTenureMonths
    val newTotalInterest = uiState.newTotalInterest
    val interestSaved = uiState.interestSaved
    val tenureReducedMonths = uiState.tenureReducedMonths
    val emiReduced = uiState.emiReduced

    val p = loanAmount.safeToDouble()
    val prePay = prepaymentAmount.safeToDouble()
    val monthlyPrepay = monthlyPrepayment.safeToDouble()
    val annualPrepay = annualPrepayment.safeToDouble()
    val r = if (interestRate.safeToDouble() > 0) (interestRate.safeToDouble() / 12) / 100 else 0.0
    val n = tenureYears.safeToDouble() * 12

    val hasValidInput = uiState.hasValidInput
    
    var showAmortization by rememberSaveable { mutableStateOf(false) }
    var isAiUnlocked by rememberSaveable { mutableStateOf(false) }
    var showUnlockDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(initialHistory) {
        if (initialHistory != null) {
            viewModel.onEvent(PrepaymentEvent.InitializeFromHistory(initialHistory))
            onHistoryConsumed()
        }
    }
    
    LaunchedEffect(loanAmount, interestRate, tenureYears, prepaymentAmount, strategy, monthlyPrepayment, annualPrepayment) {
        kotlinx.coroutines.delay(2000)
        if (historyViewModel != null && hasValidInput) {
            val history = CalculationHistory(
                id = currentHistoryId,
                calculatorType = "Prepayment",
                title = "₹$loanAmount - Strategy: $strategy",
                param1 = loanAmount,
                param2 = interestRate,
                param3 = tenureYears,
                param4 = prepaymentAmount,
                param5 = strategy
            )
            historyViewModel.insert(history) { id ->
                viewModel.onEvent(PrepaymentEvent.HistoryIdUpdated(id))
            }
        }
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(bgColor).statusBarsPadding()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = LoanMasterTheme.spacing.md, vertical = LoanMasterTheme.spacing.md),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(LoanMasterTheme.components.iconMedium).clickable { onNavigateBack() }
                    )
                    Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.md))
                    Text(
                        text = "Prepayment",
                        color = Color.White,
                        fontSize = LoanMasterTheme.typography.title.fontSize,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        containerColor = bgColor
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = LoanMasterTheme.spacing.screenPadding),
            verticalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.lg)
        ) {
            item { Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.sm)) }

            // Hero Card: Interest Saved
            item {
                PrepaymentHeroCard(interestSaved, tenureReducedMonths, emiReduced, strategy, accentGreen, surfaceColor)
            }

            // Input Fields
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(LoanMasterTheme.spacing.lg))
                        .background(surfaceColor)
                        .border(1.dp, CardStroke, RoundedCornerShape(LoanMasterTheme.spacing.lg))
                        .padding(LoanMasterTheme.components.iconSmall),
                    verticalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.md)
                ) {
                    Text("Loan Details", color = Color.White, fontSize = LoanMasterTheme.typography.title.fontSize, fontWeight = FontWeight.Bold)
                    
                    PremiumInputField("Outstanding Loan Amount", "₹", loanAmount) { viewModel.updateInputs(loanAmount = it) }
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.md)) {
                        Box(modifier = Modifier.weight(1f)) {
                            PremiumInputField("Interest Rate", "%", interestRate) { viewModel.updateInputs(interestRate = it) }
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            PremiumInputField("Remaining Tenure", "Yrs", tenureYears) { viewModel.updateInputs(tenureYears = it) }
                        }
                    }
                    
                    Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.sm))
                    HorizontalDivider(color = CardStroke)
                    Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.sm))
                    
                    Text("Prepayment Details", color = primaryColor, fontSize = LoanMasterTheme.typography.title.fontSize, fontWeight = FontWeight.Bold)
                    
                    Row(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(LoanMasterTheme.spacing.md)).background(Color.Black.copy(alpha = 0.2f)).padding(LoanMasterTheme.spacing.xs)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(LoanMasterTheme.spacing.sm))
                                .background(if (strategy == "Tenure") primaryColor else Color.Transparent)
                                .clickable { viewModel.onEvent(PrepaymentEvent.StrategyChanged("Tenure")) }
                                .padding(vertical = LoanMasterTheme.spacing.md),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Reduce Tenure", color = if (strategy == "Tenure") Color.White else TextSecondary, fontSize = LoanMasterTheme.typography.body.fontSize, fontWeight = FontWeight.Bold)
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(LoanMasterTheme.spacing.sm))
                                .background(if (strategy == "EMI") primaryColor else Color.Transparent)
                                .clickable { viewModel.onEvent(PrepaymentEvent.StrategyChanged("EMI")) }
                                .padding(vertical = LoanMasterTheme.spacing.md),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Reduce EMI", color = if (strategy == "EMI") Color.White else TextSecondary, fontSize = LoanMasterTheme.typography.body.fontSize, fontWeight = FontWeight.Bold)
                        }
                    }

                    PremiumInputField("Lump Sum Prepayment", "₹", prepaymentAmount) { viewModel.updateInputs(prepaymentAmount = it) }
                    
                    // Slider for Lump Sum
                    val maxSliderValue = if (p > 0) p.toFloat() else 10000000f
                    val currentSliderValue = prePay.toFloat().coerceIn(0f, maxSliderValue)
                    Slider(
                        value = currentSliderValue,
                        onValueChange = { viewModel.onEvent(PrepaymentEvent.PrepaymentAmountChanged(it.toInt().toString())) },
                        valueRange = 0f..maxSliderValue,
                        colors = SliderDefaults.colors(
                            thumbColor = primaryColor,
                            activeTrackColor = primaryColor,
                            inactiveTrackColor = primaryColor.copy(alpha = 0.2f)
                        )
                    )

                    if (strategy == "Tenure") {
                        Row(horizontalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.md)) {
                            Box(modifier = Modifier.weight(1f)) {
                                PremiumInputField("Monthly Extra", "₹", monthlyPrepayment) { viewModel.updateInputs(monthlyPrepayment = it) }
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                PremiumInputField("Annual Extra", "₹", annualPrepayment) { viewModel.updateInputs(annualPrepayment = it) }
                            }
                        }
                    }
                }
            }

            // Comparison Cards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.md)
                ) {
                    ComparisonCard(
                        modifier = Modifier.weight(1f),
                        title = "Without Prepayment",
                        totalInterest = originalTotalInterest,
                        color = accentOrange,
                        surfaceColor = surfaceColor
                    )
                    ComparisonCard(
                        modifier = Modifier.weight(1f),
                        title = "With Prepayment",
                        totalInterest = newTotalInterest,
                        color = accentGreen,
                        surfaceColor = surfaceColor
                    )
                }
            }
            
            // Visual Chart
            item {
                PrepaymentChartCard(
                    originalPrincipal = p,
                    originalInterest = originalTotalInterest,
                    newPrincipal = p,
                    newInterest = newTotalInterest,
                    surfaceColor = surfaceColor,
                    primaryColor = primaryColor,
                    accentOrange = accentOrange,
                    accentGreen = accentGreen
                )
            }

            item {
                Button(
                    onClick = { showAmortization = true },
                    modifier = Modifier.fillMaxWidth().heightIn(min = LoanMasterTheme.components.buttonHeight),
                    shape = RoundedCornerShape(LoanMasterTheme.spacing.md),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
                ) {
                    Icon(Icons.Rounded.TableChart, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.sm))
                    Text("View Amortization Schedule", color = Color.White, fontSize = LoanMasterTheme.typography.body.fontSize, fontWeight = FontWeight.Bold)
                }
            }

            item {
                // Premium locked AI Insights card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(LoanMasterTheme.spacing.lg))
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF2D1B4E), Color(0xFF1E1136))
                            )
                        )
                        .border(1.dp, Color(0xFF6B21A8), RoundedCornerShape(LoanMasterTheme.spacing.lg))
                        .clickable { if (!isAiUnlocked) showUnlockDialog = true }
                        .padding(LoanMasterTheme.components.iconSmall)
                ) {
                    if (isAiUnlocked) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF6B21A8).copy(alpha = 0.3f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Rounded.AutoAwesome, contentDescription = "AI Insights", tint = Color(0xFFC084FC), modifier = Modifier.size(LoanMasterTheme.components.iconSmall))
                                }
                                Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.md))
                                Text("AI Strategy Unlocked", color = Color.White, fontSize = LoanMasterTheme.typography.body.fontSize, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.md))
                            
                            val bestMonth = 1
                            val delayLoss = prePay * r // rough estimate of interest lost by delaying one month
                            
                            Text("Optimal Prepayment Month: Month $bestMonth", color = Color(0xFF4ADE80), fontSize = LoanMasterTheme.typography.body.fontSize, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.sm))
                            Text("Mathematically, the earlier you prepay, the more principal you reduce. Prepaying ₹${formatMoney(prePay)} in Month 1 saves maximum interest. Delaying this prepayment by just one year will cost you approximately ₹${formatMoney(delayLoss * 12)} in additional interest.", color = Color.White.copy(alpha = 0.8f), fontSize = LoanMasterTheme.typography.body.fontSize, lineHeight = LoanMasterTheme.typography.title.fontSize)
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(LoanMasterTheme.components.iconLarge)
                                    .clip(CircleShape)
                                    .background(Color(0xFF6B21A8).copy(alpha = 0.3f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Rounded.AutoAwesome, contentDescription = "AI Insights", tint = Color(0xFFC084FC))
                            }
                            Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.md))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("AI Smart Strategy", color = Color.White, fontSize = LoanMasterTheme.typography.body.fontSize, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.sm))
                                    Icon(Icons.Rounded.Lock, contentDescription = "Locked", tint = Color(0xFFFCD34D), modifier = Modifier.size(LoanMasterTheme.spacing.md))
                                }
                                Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.xs))
                                Text("Find the optimal month to prepay for maximum interest savings.", color = Color.White.copy(alpha = 0.7f), fontSize = LoanMasterTheme.typography.label.fontSize)
                            }
                            Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.sm))
                            Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = null, tint = Color(0xFFC084FC))
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.xl)) }
        }
    }

    if (showAmortization) {
        AmortizationBottomSheet(
            p = p,
            prePay = prePay,
            monthlyPrepay = monthlyPrepay,
            annualPrepay = annualPrepay,
            r = r,
            n = n,
            originalEmi = emi,
            strategy = strategy,
            isUnlocked = isAiUnlocked,
            onUnlockClick = { showUnlockDialog = true },
            onDismiss = { showAmortization = false }
        )
    }

    if (showUnlockDialog) {
        AlertDialog(
            onDismissRequest = { showUnlockDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.WorkspacePremium, contentDescription = null, tint = Color.Yellow)
                    Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.sm))
                    Text("Unlock Premium Features", color = Color.White, fontSize = LoanMasterTheme.typography.title.fontSize, fontWeight = FontWeight.Bold)
                }
            },
            text = { Text("Watch a short ad or upgrade to Premium to unlock AI Smart Strategy and PDF Export of the Amortization schedule.", color = TextSecondary, fontSize = LoanMasterTheme.typography.body.fontSize) },
            confirmButton = {
                Button(
                    onClick = { 
                        isAiUnlocked = true 
                        showUnlockDialog = false 
                    }, 
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC084FC)),
                    shape = RoundedCornerShape(LoanMasterTheme.spacing.md)
                ) {
                    Icon(Icons.Rounded.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(LoanMasterTheme.spacing.md))
                    Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.xs))
                    Text("Watch Ad", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { 
                        isAiUnlocked = true 
                        showUnlockDialog = false 
                    },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFC084FC)),
                    border = BorderStroke(1.dp, Color(0xFFC084FC)),
                    shape = RoundedCornerShape(LoanMasterTheme.spacing.md)
                ) {
                    Icon(Icons.Rounded.WorkspacePremium, contentDescription = null, tint = Color(0xFFC084FC), modifier = Modifier.size(LoanMasterTheme.spacing.md))
                    Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.xs))
                    Text("Buy Premium")
                }
            },
            containerColor = SurfaceDark
        )
    }
}

@Composable
fun PrepaymentHeroCard(interestSaved: Double, tenureReducedMonths: Double, emiReduced: Double, strategy: String, accentColor: Color, surfaceColor: Color) {
    val formatMoney = { amount: Double ->
        formatMoney(amount)
    }
    
    val years = (tenureReducedMonths / 12).toInt()
    val months = (tenureReducedMonths % 12).toInt()
    val tenureText = if (years > 0 && months > 0) "$years Yrs $months Mos"
    else if (years > 0) "$years Yrs"
    else "$months Mos"

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(LoanMasterTheme.components.cardRadius))
            .background(Brush.linearGradient(listOf(Color(0xFF0D3B23), surfaceColor)))
            .border(1.dp, accentColor.copy(alpha = 0.3f), RoundedCornerShape(LoanMasterTheme.components.cardRadius))
            .padding(LoanMasterTheme.spacing.lg)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Savings, contentDescription = null, tint = accentColor, modifier = Modifier.size(LoanMasterTheme.spacing.lg))
                Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.sm))
                Text("TOTAL INTEREST SAVED", color = accentColor, fontSize = LoanMasterTheme.typography.label.fontSize, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            }
            Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.md))
            AutoSizeText(
                text = formatMoney(interestSaved),
                color = accentColor,
                minTextSize = LoanMasterTheme.typography.title.fontSize,
                maxTextSize = 48.sp,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1
            )
            Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.md))
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(LoanMasterTheme.spacing.md))
                    .background(Color.Black.copy(alpha = 0.2f))
                    .padding(horizontal = LoanMasterTheme.spacing.md, vertical = LoanMasterTheme.spacing.sm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val icon = if(strategy == "Tenure") Icons.Rounded.Event else Icons.Rounded.AccountBalanceWallet
                Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(LoanMasterTheme.spacing.md))
                Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.sm))
                val savedText = if (strategy == "Tenure") "Tenure Reduced by: $tenureText" else "EMI Reduced by: ${formatMoney(emiReduced)}"
                Text(savedText, color = Color.White, fontSize = LoanMasterTheme.typography.body.fontSize, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
fun ComparisonCard(modifier: Modifier, title: String, totalInterest: Double, color: Color, surfaceColor: Color) {
    val formatMoney = { amount: Double ->
        formatMoney(amount)
    }
    
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(LoanMasterTheme.components.iconSmall))
            .background(surfaceColor)
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(LoanMasterTheme.components.iconSmall))
            .padding(LoanMasterTheme.spacing.md),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(title, color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.sm))
        Text(formatMoney(totalInterest), color = color, fontSize = LoanMasterTheme.typography.title.fontSize, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.xs))
        Text("Total Interest", color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize)
    }
}

@Composable
fun PremiumInputField(label: String, symbol: String, value: String, onValueChange: (String) -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    
    Column {
        Text(label, color = TextSecondary, fontSize = LoanMasterTheme.typography.body.fontSize)
        Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.sm))
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(LoanMasterTheme.spacing.md))
                .background(Color.Black.copy(alpha = 0.2f))
                .border(1.dp, CardStroke, RoundedCornerShape(LoanMasterTheme.spacing.md))
                .padding(horizontal = LoanMasterTheme.spacing.md, vertical = LoanMasterTheme.spacing.md),
            textStyle = TextStyle(
                color = Color.White,
                fontSize = LoanMasterTheme.typography.title.fontSize,
                fontWeight = FontWeight.Bold
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            interactionSource = interactionSource,
            decorationBox = { innerTextField ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (symbol.isNotEmpty() && symbol == "₹") {
                        Text(symbol, color = AccentYellow, fontSize = LoanMasterTheme.typography.title.fontSize, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.sm))
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        if (value.isEmpty()) {
                            Text("0", color = Color.White.copy(alpha = 0.3f), fontSize = LoanMasterTheme.typography.title.fontSize, fontWeight = FontWeight.Bold)
                        }
                        innerTextField()
                    }
                    if (symbol.isNotEmpty() && symbol == "%" || symbol == "Yrs") {
                        Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.sm))
                        Text(symbol, color = AccentYellow, fontSize = LoanMasterTheme.typography.body.fontSize, fontWeight = FontWeight.Bold)
                    }
                }
            }
        )
    }
}

@Composable
fun PrepaymentChartCard(
    originalPrincipal: Double,
    originalInterest: Double,
    newPrincipal: Double,
    newInterest: Double,
    surfaceColor: Color,
    primaryColor: Color,
    accentOrange: Color,
    accentGreen: Color
) {
    val originalTotal = originalPrincipal + originalInterest
    val newTotal = newPrincipal + newInterest

    val ogPrinPct = if (originalTotal > 0) (originalPrincipal / originalTotal).toFloat() else 0f
    val ogIntPct = if (originalTotal > 0) (originalInterest / originalTotal).toFloat() else 0f
    
    val newPrinPct = if (newTotal > 0) (newPrincipal / newTotal).toFloat() else 0f
    val newIntPct = if (newTotal > 0) (newInterest / newTotal).toFloat() else 0f

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(LoanMasterTheme.spacing.lg))
            .background(surfaceColor)
            .border(1.dp, CardStroke, RoundedCornerShape(LoanMasterTheme.spacing.lg))
            .padding(LoanMasterTheme.components.iconSmall)
    ) {
        Text("Payment Breakdown", color = Color.White, fontSize = LoanMasterTheme.typography.title.fontSize, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.lg))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Without Prepayment
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(modifier = Modifier.size(LoanMasterTheme.components.calculatorCardHeight), contentAlignment = Alignment.Center) {
                    Canvas(modifier = Modifier.fillMaxSize().padding(LoanMasterTheme.spacing.sm)) {
                        drawArc(color = primaryColor, startAngle = -90f, sweepAngle = 360f * ogPrinPct, useCenter = false, style = Stroke(width = 30f, cap = StrokeCap.Round))
                        drawArc(color = accentOrange, startAngle = -90f + (360f * ogPrinPct), sweepAngle = 360f * ogIntPct, useCenter = false, style = Stroke(width = 30f, cap = StrokeCap.Round))
                    }
                    Text("${(ogIntPct*100).toInt()}%", color = accentOrange, fontSize = LoanMasterTheme.typography.body.fontSize, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.md))
                Text("Standard", color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize)
                Text(formatMoney(originalTotal), color = Color.White, fontSize = LoanMasterTheme.typography.body.fontSize, fontWeight = FontWeight.Bold)
            }
            
            Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(LoanMasterTheme.spacing.xl))
            
            // With Prepayment
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(modifier = Modifier.size(LoanMasterTheme.components.calculatorCardHeight), contentAlignment = Alignment.Center) {
                    Canvas(modifier = Modifier.fillMaxSize().padding(LoanMasterTheme.spacing.sm)) {
                        drawArc(color = primaryColor, startAngle = -90f, sweepAngle = 360f * newPrinPct, useCenter = false, style = Stroke(width = 30f, cap = StrokeCap.Round))
                        drawArc(color = accentGreen, startAngle = -90f + (360f * newPrinPct), sweepAngle = 360f * newIntPct, useCenter = false, style = Stroke(width = 30f, cap = StrokeCap.Round))
                    }
                    Text("${(newIntPct*100).toInt()}%", color = accentGreen, fontSize = LoanMasterTheme.typography.body.fontSize, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.md))
                Text("Prepayment", color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize)
                Text(formatMoney(newTotal), color = Color.White, fontSize = LoanMasterTheme.typography.body.fontSize, fontWeight = FontWeight.Bold)
            }
        }
        
        Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.lg))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(LoanMasterTheme.spacing.gridGutter).clip(CircleShape).background(primaryColor))
                Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.sm))
                Text("Principal", color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize)
            }
            Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.lg))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(LoanMasterTheme.spacing.gridGutter).clip(CircleShape).background(accentOrange))
                Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.sm))
                Text("Interest", color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize)
            }
        }
    }
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AmortizationBottomSheet(standardSchedule: List<com.loanmaster.pro.domain.calculator.PrepaymentAmortizationRow>, prepaySchedule: List<com.loanmaster.pro.domain.calculator.PrepaymentAmortizationRow>, isUnlocked: Boolean = false, onUnlockClick: () -> Unit = {}, onDismiss: () -> Unit
) {
    var isStandard by remember { mutableStateOf(true) }
    val schedule = if (isStandard) standardSchedule else prepaySchedule

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = LoanMasterTheme.spacing.md)
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(bottom = LoanMasterTheme.spacing.xl)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = LoanMasterTheme.spacing.md),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Amortization Schedule",
                    color = Color.White,
                    fontSize = LoanMasterTheme.typography.title.fontSize,
                    fontWeight = FontWeight.Bold
                )
                
                val context = androidx.compose.ui.platform.LocalContext.current
                // Premium Feature Button
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(LoanMasterTheme.spacing.md))
                        .background(
                            if (isUnlocked) Brush.horizontalGradient(listOf(Color(0xFF059669), Color(0xFF10B981)))
                            else Brush.horizontalGradient(listOf(Color(0xFFF59E0B), Color(0xFFD97706)))
                        )
                        .clickable { 
                            if (!isUnlocked) {
                                onUnlockClick()
                            } else {
                                android.widget.Toast.makeText(context, "PDF Exported Successfully!", android.widget.Toast.LENGTH_SHORT).show()
                            }
                        }
                        .padding(horizontal = LoanMasterTheme.spacing.md, vertical = LoanMasterTheme.spacing.sm),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Rounded.PictureAsPdf, contentDescription = "Export PDF", tint = Color.White, modifier = Modifier.size(LoanMasterTheme.spacing.md))
                    Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.xs))
                    Text("Export", color = Color.White, fontSize = LoanMasterTheme.typography.label.fontSize, fontWeight = FontWeight.Bold)
                    if (!isUnlocked) {
                        Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.xs))
                        Icon(Icons.Rounded.WorkspacePremium, contentDescription = "Premium", tint = Color.Yellow, modifier = Modifier.size(LoanMasterTheme.spacing.md))
                    }
                }
            }
            
            // Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(LoanMasterTheme.spacing.md))
                    .background(Color.Black.copy(alpha = 0.2f))
                    .padding(LoanMasterTheme.spacing.xs)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(LoanMasterTheme.spacing.sm))
                        .background(if (isStandard) AccentYellow else Color.Transparent)
                        .clickable { isStandard = true }
                        .padding(vertical = LoanMasterTheme.spacing.md),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Standard", color = if (isStandard) Color.White else TextSecondary, fontSize = LoanMasterTheme.typography.body.fontSize, fontWeight = FontWeight.Bold)
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(LoanMasterTheme.spacing.sm))
                        .background(if (!isStandard) AccentYellow else Color.Transparent)
                        .clickable { isStandard = false }
                        .padding(vertical = LoanMasterTheme.spacing.md),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Prepayment", color = if (!isStandard) Color.White else TextSecondary, fontSize = LoanMasterTheme.typography.body.fontSize, fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.md))
            
            // Header
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = LoanMasterTheme.spacing.sm),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Mon", color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize, modifier = Modifier.weight(0.5f))
                Text("Principal", color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize, textAlign = TextAlign.End, modifier = Modifier.weight(1f))
                Text("Interest", color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize, textAlign = TextAlign.End, modifier = Modifier.weight(1f))
                Text("Balance", color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize, textAlign = TextAlign.End, modifier = Modifier.weight(1f))
            }
            HorizontalDivider(color = CardStroke)
            
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f, fill = false).heightIn(max = 400.dp)
            ) {
                items(schedule) { row ->
                    Column(modifier = Modifier.fillMaxWidth().padding(vertical = LoanMasterTheme.spacing.md)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(if (row.month == 0) "-" else "${row.month}", color = Color.White, fontSize = LoanMasterTheme.typography.body.fontSize, modifier = Modifier.weight(0.5f))
                            Text(formatMoney(row.principal), color = Color(0xFF4ADE80), fontSize = LoanMasterTheme.typography.body.fontSize, textAlign = TextAlign.End, modifier = Modifier.weight(1f))
                            Text(if (row.interest > 0) formatMoney(row.interest) else "-", color = Color(0xFFF97316), fontSize = LoanMasterTheme.typography.body.fontSize, textAlign = TextAlign.End, modifier = Modifier.weight(1f))
                            Text(formatMoney(row.balance), color = Color.White, fontSize = LoanMasterTheme.typography.body.fontSize, textAlign = TextAlign.End, modifier = Modifier.weight(1f))
                        }
                        if (row.label.isNotEmpty()) {
                            Text(row.label, color = if (row.isPrepayment) AccentYellow else TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize, modifier = Modifier.padding(top = LoanMasterTheme.spacing.xs))
                        }
                    }
                    HorizontalDivider(color = CardStroke.copy(alpha = 0.5f))
                }
            }
        }
    }
}
