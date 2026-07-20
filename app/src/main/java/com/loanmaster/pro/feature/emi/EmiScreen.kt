package com.loanmaster.pro.feature.emi

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
import com.loanmaster.pro.feature.loansummary.*
import com.loanmaster.pro.feature.prepayment.*
import com.loanmaster.pro.core.formatter.*
import com.loanmaster.pro.feature.loaneligibility.util.*

import com.loanmaster.pro.feature.fd.*
import com.loanmaster.pro.data.repository.*
import com.loanmaster.pro.feature.currency.*
import com.loanmaster.pro.core.navigation.*
import com.loanmaster.pro.feature.compare.*
import com.loanmaster.pro.feature.loaneligibility.*
import com.loanmaster.pro.core.responsive.*
import com.loanmaster.pro.feature.home.*
import com.loanmaster.pro.feature.emi.*

import androidx.window.core.layout.WindowWidthSizeClass


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material.icons.automirrored.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.log
import kotlin.math.pow

// ==================== DATA CLASSES ====================


// ==================== HELPER FUNCTIONS ====================
// ==================== TENURE INPUT FIELD ====================
@Composable
fun TenureInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isMonths: Boolean,
    onToggleIsMonths: (Boolean) -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    inputBg: Color,
    borderColor: Color,
    secondaryText: Color,
    errorMessage: String? = null,
    modifier: Modifier = Modifier
) {
    val haptic = androidx.compose.ui.platform.LocalHapticFeedback.current
    PremiumInputField(
        label = label,
        value = value,
        onValueChange = onValueChange,
        icon = icon,
        iconTint = iconTint,
        errorMessage = errorMessage,
        modifier = modifier,
        trailingContent = {
            // Compact Toggle Button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(LoanMasterTheme.spacing.sm))
                    .background(Color(0xFF2D7DFF).copy(alpha = 0.15f))
                    .clickable { 
                        haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                        onToggleIsMonths(!isMonths) 
                    }
                    .padding(horizontal = LoanMasterTheme.spacing.md, vertical = LoanMasterTheme.spacing.sm),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isMonths) "Mo" else "Yrs",
                    color = Color(0xFF2D7DFF),
                    fontSize = LoanMasterTheme.typography.body.fontSize.value.sp * 0.9f,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    )
}

// ==================== LOAN TYPE SELECTOR ====================
@Composable
fun LoanTypeSelector(
    selectedType: String,
    onTypeSelected: (String) -> Unit,
    inputBg: Color,
    borderColor: Color,
    secondaryText: Color,
    modifier: Modifier = Modifier
) {
    var isDropdownExpanded by rememberSaveable { mutableStateOf(false) }
    val haptic = androidx.compose.ui.platform.LocalHapticFeedback.current

    Column(modifier = modifier) {
        Text("Loan Type", color = secondaryText, fontSize = LoanMasterTheme.typography.body.fontSize.value.sp * 0.85f)
        Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.sm))
        Surface(
            shape = RoundedCornerShape(LoanMasterTheme.spacing.md),
            color = inputBg,
            border = BorderStroke(1.dp, borderColor),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { 
                    haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                    isDropdownExpanded = true 
                }
        ) {
            Box {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = LoanMasterTheme.spacing.md, vertical = LoanMasterTheme.spacing.gridGutter),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = getLoanTypeIcon(selectedType),
                        contentDescription = null,
                        tint = Color(0xFF22C55E),
                        modifier = Modifier.size(LoanMasterTheme.components.iconMedium.value.dp * 0.8f)
                    )
                    Spacer(Modifier.widthIn(min = LoanMasterTheme.spacing.gridGutter))
                    Text(selectedType, color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface, fontSize = LoanMasterTheme.typography.body.fontSize, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                    Icon(
                        imageVector = Icons.Rounded.KeyboardArrowDown,
                        contentDescription = null,
                        tint = secondaryText,
                        modifier = Modifier.size(LoanMasterTheme.components.iconMedium.value.dp * 0.8f)
                    )
                }
                androidx.compose.material3.DropdownMenu(
                    expanded = isDropdownExpanded,
                    onDismissRequest = { isDropdownExpanded = false },
                    modifier = Modifier.background(inputBg).border(1.dp, borderColor, RoundedCornerShape(LoanMasterTheme.spacing.sm))
                ) {
                    loanProfiles.forEach { profile ->
                        androidx.compose.material3.DropdownMenuItem(
                            text = { Text(profile.name, color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface) },
                            leadingIcon = {
                                Icon(getLoanTypeIcon(profile.name), contentDescription = null, tint = secondaryText, modifier = Modifier.size(LoanMasterTheme.components.iconMedium.value.dp * 0.8f))
                            },
                            onClick = {
                                haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                                onTypeSelected(profile.name)
                                isDropdownExpanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

// ==================== FULL AMORTIZATION DIALOG ====================
@Composable
fun FullAmortizationDialog(
    emi: Double,
    schedule: List<MonthlyAmortization>,
    onDismiss: () -> Unit
) {
    val haptic = androidx.compose.ui.platform.LocalHapticFeedback.current

    Dialog(onDismissRequest = onDismiss, properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)) {
        val bgColor = Color(0xFF061633)
        val headerColor = Color(0xFF0A2150)
        val primaryText = Color.White
        val secondaryText = Color(0xFFA8B3D1)
        val blueAccent = Color(0xFF2D7DFF)
        val greenAccent = Color(0xFF22C55E)
        val goldAccent = Color(0xFFFFC328)
        val borderColor = Color(0xFF183C8A)

        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(LoanMasterTheme.components.cardRadius),
            colors = CardDefaults.cardColors(containerColor = bgColor)
        ) {
            Column(modifier = Modifier.padding(LoanMasterTheme.spacing.md)) {
                Text(
                    "Full Monthly Amortization Schedule",
                    color = primaryText,
                    style = LoanMasterTheme.typography.title,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "${schedule.size} months • EMI: ${com.loanmaster.pro.core.formatter.formatMoney(emi)}",
                    color = secondaryText,
                    style = LoanMasterTheme.typography.body
                )

                Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.md))

                // Column Headers
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(headerColor, RoundedCornerShape(LoanMasterTheme.spacing.sm))
                        .padding(horizontal = LoanMasterTheme.spacing.sm, vertical = LoanMasterTheme.spacing.gridGutter),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Mo.", color = secondaryText, style = LoanMasterTheme.typography.label, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(0.9f))
                    Text("EMI", color = secondaryText, style = LoanMasterTheme.typography.label, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1.1f), textAlign = TextAlign.End)
                    Text("Prin", color = secondaryText, style = LoanMasterTheme.typography.label, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1.2f), textAlign = TextAlign.End)
                    Text("Int", color = secondaryText, style = LoanMasterTheme.typography.label, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1.1f), textAlign = TextAlign.End)
                    Text("Bal", color = secondaryText, style = LoanMasterTheme.typography.label, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1.2f), textAlign = TextAlign.End)
                }

                Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.xs))

                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(items = schedule, key = { it.month }) { row ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = LoanMasterTheme.spacing.xs, vertical = 7.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("M${row.month}", color = secondaryText, style = LoanMasterTheme.typography.label, modifier = Modifier.weight(0.9f))
                            AutoResizedText(com.loanmaster.pro.core.formatter.formatMoney(row.emi), color = primaryText, fontSize = LoanMasterTheme.typography.label.fontSize, modifier = Modifier.weight(1.1f).wrapContentWidth(Alignment.End))
                            AutoResizedText(com.loanmaster.pro.core.formatter.formatMoney(row.principalPaid), color = greenAccent, fontSize = LoanMasterTheme.typography.label.fontSize, modifier = Modifier.weight(1.2f).wrapContentWidth(Alignment.End))
                            AutoResizedText(com.loanmaster.pro.core.formatter.formatMoney(row.interestPaid), color = goldAccent, fontSize = LoanMasterTheme.typography.label.fontSize, modifier = Modifier.weight(1.1f).wrapContentWidth(Alignment.End))
                            AutoResizedText(com.loanmaster.pro.core.formatter.formatMoney(row.remainingBalance), color = primaryText, fontSize = LoanMasterTheme.typography.label.fontSize, modifier = Modifier.weight(1.2f).wrapContentWidth(Alignment.End))
                        }
                        if (row.month < schedule.size) {
                            HorizontalDivider(color = borderColor.copy(alpha = 0.3f), thickness = 0.5.dp)
                        }
                    }
                }

                Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.md))

                Row(horizontalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.sm), modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = {
                            haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f).heightIn(min = LoanMasterTheme.components.buttonHeight),
                        shape = RoundedCornerShape(LoanMasterTheme.spacing.md),
                        border = BorderStroke(1.dp, borderColor)
                    ) {
                        Text("Close", color = primaryText)
                    }
                    val context = androidx.compose.ui.platform.LocalContext.current
                    Button(
                        onClick = { 
                            haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                            ExportUtils.exportToPdf(
                                context,
                                "Amortization Schedule",
                                schedule.map { "Month ${it.month}" to "EMI: ${com.loanmaster.pro.core.formatter.formatMoney(it.emi)}, Prin: ${com.loanmaster.pro.core.formatter.formatMoney(it.principalPaid)}, Int: ${com.loanmaster.pro.core.formatter.formatMoney(it.interestPaid)}, Bal: ${com.loanmaster.pro.core.formatter.formatMoney(it.remainingBalance)}" }
                            )
                        },
                        modifier = Modifier.weight(1f).heightIn(min = LoanMasterTheme.components.buttonHeight),
                        colors = ButtonDefaults.buttonColors(containerColor = blueAccent),
                        shape = RoundedCornerShape(LoanMasterTheme.spacing.md)
                    ) {
                        Icon(Icons.Rounded.Download, contentDescription = null, modifier = Modifier.size(LoanMasterTheme.components.iconSmall))
                        Spacer(Modifier.widthIn(min = LoanMasterTheme.spacing.xs))
                        Text("Export")
                    }
                }
            }
        }
    }
}

// ==================== MAIN SCREEN ====================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmiScreen(
    onNavigateBack: () -> Unit = {},
    historyViewModel: HistoryViewModel? = null,
    initialHistory: CalculationHistory? = null,
    onHistoryConsumed: () -> Unit = {},
    viewModel: EmiViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val dummyCurrency = com.loanmaster.pro.LocalCurrency.current
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    
    val sizeClass = when {
        configuration.screenWidthDp < 600 -> WindowWidthSizeClass.COMPACT
        configuration.screenWidthDp < 840 -> WindowWidthSizeClass.MEDIUM
        else -> WindowWidthSizeClass.EXPANDED
    }
    
    val isExpanded = sizeClass == WindowWidthSizeClass.EXPANDED
    val isMedium = sizeClass == WindowWidthSizeClass.MEDIUM

    val horizPadding = LoanMasterTheme.spacing.screenPadding
    val cardSpacing = LoanMasterTheme.spacing.screenPadding
    
    val bgColor = androidx.compose.material3.MaterialTheme.colorScheme.background
    val primaryCard = androidx.compose.material3.MaterialTheme.colorScheme.surface
    val inputBg = androidx.compose.material3.MaterialTheme.colorScheme.surface
    val borderColor = androidx.compose.material3.MaterialTheme.colorScheme.outlineVariant
    val primaryText = androidx.compose.material3.MaterialTheme.colorScheme.onBackground
    val secondaryText = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant

    val blueAccent = Color(0xFF2D7DFF)
    val goldAccent = Color(0xFFFFC328)
    val greenAccent = Color(0xFF22C55E)
    val purpleAccent = Color(0xFF7C4DFF)

    var showFullSchedule by rememberSaveable { mutableStateOf(false) }

    
    LaunchedEffect(uiState.loanAmountText, uiState.interestRateText, uiState.tenureInputText, uiState.isTenureInMonths, uiState.loanType) {
        kotlinx.coroutines.delay(2000)
        if (historyViewModel != null && uiState.hasValidInput) {
            val history = CalculationHistory(
                id = uiState.currentHistoryId,
                calculatorType = "EMI",
                title = "$uiState.loanType - ${com.loanmaster.pro.core.formatter.formatMoney(uiState.parsedLoanAmount)}",
                param1 = uiState.loanAmountText,
                param2 = uiState.interestRateText,
                param3 = uiState.tenureInputText,
                param4 = uiState.isTenureInMonths.toString(),
                param5 = uiState.loanType,
                result1 = uiState.monthlyEmi,
                result2 = uiState.totalInterest,
                result3 = uiState.totalPayment
            )
            historyViewModel.insert(history) { id ->
                viewModel.updateHistoryId(id)
            }
        }
    }

    LaunchedEffect(initialHistory) {
        if (initialHistory != null) {
            viewModel.loadFromHistory(initialHistory)
            onHistoryConsumed()
        }
    }
    
    
    
    // Bottom Sheet State
    // RESPONSIVE: use rememberSaveable
    var selectedRecommendation by rememberSaveable { mutableStateOf<SmartRecommendation?>(null) }
    val haptic = androidx.compose.ui.platform.LocalHapticFeedback.current

    
    
    

    val totalPrincipal = uiState.parsedLoanAmount

    val animatedEmi by animateFloatAsState(
        targetValue = if (uiState.hasValidInput) uiState.monthlyEmi.toFloat() else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "Animated EMI"
    )

    

    val prinPct = uiState.principalPercentage
    val intPct = uiState.interestPercentage

    Scaffold(
        containerColor = bgColor,
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { 
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Calculate, contentDescription = null, tint = Color(0xFF2D7DFF), modifier = Modifier.size(24.dp))
                            Spacer(Modifier.widthIn(min = 8.dp))
                            Text("EMI Calculator", color = primaryText, fontSize = LoanMasterTheme.typography.title.fontSize, fontWeight = FontWeight.Bold, minLines = 1, maxLines = 2, overflow = TextOverflow.Ellipsis)
                        }
                        Text("Calculate your loan EMI and plan better", color = secondaryText, fontSize = LoanMasterTheme.typography.label.fontSize, minLines = 1, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    }
                },
                actions = {},
                colors = TopAppBarDefaults.topAppBarColors(containerColor = bgColor)
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            CalculatorScreenLayout(
                widthSizeClass = sizeClass,
                animationTriggerState = uiState.monthlyEmi,
                headerSection = { },
                inputControlsSection = {
                    // INPUT SECTION
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = primaryCard),
                        shape = RoundedCornerShape(LoanMasterTheme.components.cardRadius),
                        border = BorderStroke(1.dp, borderColor)
                    ) {
                        Column(modifier = Modifier.padding(LoanMasterTheme.spacing.sm), verticalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.sm)) {
                            AdaptiveRowCol(
                                columns = LoanMasterTheme.grids.inputColumns,
                                content1 = { modifier ->
                                    PremiumInputField(
                                        label = "Loan Amount", value = uiState.loanAmountText, onValueChange = { viewModel.updateInputs(loanAmount = it) },
                                        icon = Icons.Rounded.AccountBalanceWallet, iconTint = blueAccent, modifier = modifier,
                                        errorMessage = uiState.loanAmountError
                                    )
                                },
                                content2 = { modifier ->
                                    PremiumInputField(
                                        label = "Interest Rate (p.a.)", value = uiState.interestRateText, onValueChange = { viewModel.updateInputs(interestRate = it) },
                                        icon = Icons.Rounded.Percent, iconTint = blueAccent, modifier = modifier,
                                        infoText = "The annual interest rate charged on your loan.",
                                        errorMessage = uiState.interestRateError
                                    )
                                }
                            )
                            AdaptiveRowCol(
                                columns = LoanMasterTheme.grids.inputColumns,
                                content1 = { modifier ->
                                    TenureInputField(
                                        label = "Tenure", value = uiState.tenureInputText, onValueChange = { viewModel.updateInputs(tenureInput = it) },
                                        isMonths = uiState.isTenureInMonths, onToggleIsMonths = { viewModel.updateInputs(isTenureMonths = it) },
                                        icon = Icons.Rounded.DateRange, iconTint = blueAccent,
                                        inputBg = inputBg, borderColor = borderColor, secondaryText = secondaryText, modifier = modifier,
                                        errorMessage = uiState.tenureError
                                    )
                                },
                                content2 = { modifier ->
                                    LoanTypeSelector(
                                        selectedType = uiState.loanType, onTypeSelected = { viewModel.updateInputs(type = it) }, inputBg = inputBg, borderColor = borderColor, secondaryText = secondaryText, modifier = modifier
                                    )
                                }
                            )
                        }
                    }
                },
                resultsSection = {
                    Column(verticalArrangement = Arrangement.spacedBy(cardSpacing)) {
            // PLACEHOLDER
            if (!uiState.hasValidInput) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = primaryCard),
                    shape = RoundedCornerShape(LoanMasterTheme.components.cardRadius),
                    border = BorderStroke(1.dp, borderColor)
                ) {
                    Column(
                        modifier = Modifier.padding(LoanMasterTheme.spacing.xl).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Rounded.Calculate, contentDescription = null, tint = secondaryText.copy(0.5f), modifier = Modifier.size(LoanMasterTheme.components.iconMedium * 2f))
                        Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.md))
                        Text("Enter loan amount, rate & tenure to see results", color = secondaryText, fontSize = LoanMasterTheme.typography.body.fontSize, textAlign = TextAlign.Center)
                    }
                }
            }

            // ANIMATED RESULTS
            AnimatedVisibility(
                visible = uiState.hasValidInput,
                enter = fadeIn(tween(400)) + scaleIn(initialScale = 0.95f, animationSpec = tween(400))
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(cardSpacing)) {

                    // ==================== HERO EMI CARD ====================
                    Card(
                        modifier = Modifier.fillMaxWidth().shadow(LoanMasterTheme.spacing.md, spotColor = blueAccent.copy(0.4f)),
                        shape = RoundedCornerShape(LoanMasterTheme.components.iconSmall),
                        border = BorderStroke(1.dp, borderColor)
                    ) {
                        Box(modifier = Modifier.background(Brush.linearGradient(listOf(inputBg, Color(0xFF0A2150))))) {
                            Column(modifier = Modifier.padding(if (isExpanded) LoanMasterTheme.spacing.xl else LoanMasterTheme.components.iconSmall)) {
                                // EMI Header
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Your Monthly EMI", color = secondaryText, fontSize = LoanMasterTheme.typography.body.fontSize.value.sp * 0.9f)
                                        AutoResizedText(
                                            text = com.loanmaster.pro.core.formatter.formatMoney(animatedEmi.toDouble()),
                                            color = blueAccent,
                                            fontSize = LoanMasterTheme.typography.title.fontSize.value.sp * 1.5f,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                    Spacer(Modifier.widthIn(min = LoanMasterTheme.spacing.md))
                                    Icon(
                                        imageVector = Icons.Rounded.CalendarMonth,
                                        contentDescription = null,
                                        tint = blueAccent.copy(alpha = 0.3f),
                                        modifier = Modifier.size(LoanMasterTheme.components.iconMedium * 1.5f)
                                    )
                                }

                                // Cost Breakdown Stacked Bar
                                Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.md))
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    val prinPctFloat = if (prinPct.isNaN() || prinPct.isInfinite()) 0f else prinPct.toFloat()
                                    val intPctFloat = if (intPct.isNaN() || intPct.isInfinite()) 0f else intPct.toFloat()
                                    Row(modifier = Modifier.fillMaxWidth().heightIn(min = LoanMasterTheme.spacing.md).clip(RoundedCornerShape(LoanMasterTheme.spacing.sm))) {
                                        if (prinPctFloat > 0) {
                                            Box(modifier = Modifier.weight(prinPctFloat).fillMaxHeight().background(blueAccent))
                                        }
                                        if (intPctFloat > 0) {
                                            Box(modifier = Modifier.weight(intPctFloat).fillMaxHeight().background(goldAccent))
                                        }
                                    }
                                    Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.md))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                            Box(Modifier.size(LoanMasterTheme.spacing.gridGutter).background(blueAccent, CircleShape))
                                            Spacer(Modifier.widthIn(min = LoanMasterTheme.spacing.sm))
                                            AutoResizedText(text = "Principal ${prinPct.toInt()}%", color = primaryText, fontSize = LoanMasterTheme.typography.body.fontSize)
                                        }
                                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.End) {
                                            Box(Modifier.size(LoanMasterTheme.spacing.gridGutter).background(goldAccent, CircleShape))
                                            Spacer(Modifier.widthIn(min = LoanMasterTheme.spacing.sm))
                                            AutoResizedText(text = "Interest ${intPct.toInt()}%", color = primaryText, fontSize = LoanMasterTheme.typography.body.fontSize)
                                        }
                                    }
                                }

                                Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.md))
                                // Totals Row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.AutoMirrored.Rounded.TrendingUp, contentDescription = null, tint = secondaryText, modifier = Modifier.size(14.dp))
                                            Spacer(Modifier.widthIn(min = 4.dp))
                                            Text("Total Interest", color = secondaryText, fontSize = LoanMasterTheme.typography.body.fontSize.value.sp * 0.8f)
                                        }
                                        AutoResizedText(text = com.loanmaster.pro.core.formatter.formatMoney(uiState.totalInterest), color = greenAccent, fontSize = LoanMasterTheme.typography.body.fontSize.value.sp * 1.1f, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(Modifier.widthIn(min = LoanMasterTheme.spacing.sm))
                                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Rounded.Payments, contentDescription = null, tint = secondaryText, modifier = Modifier.size(14.dp))
                                            Spacer(Modifier.widthIn(min = 4.dp))
                                            Text("Total Payment", color = secondaryText, fontSize = LoanMasterTheme.typography.body.fontSize.value.sp * 0.8f)
                                        }
                                        AutoResizedText(text = com.loanmaster.pro.core.formatter.formatMoney(uiState.totalPayment), color = primaryText, fontSize = LoanMasterTheme.typography.body.fontSize.value.sp * 1.1f, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    // ==================== DYNAMIC SMART RECOMMENDATIONS ====================
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = primaryCard),
                        shape = RoundedCornerShape(LoanMasterTheme.components.cardRadius),
                        border = BorderStroke(1.dp, borderColor)
                    ) {
                        Column(modifier = Modifier.padding(LoanMasterTheme.spacing.md)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Rounded.Assistant, contentDescription = null, tint = goldAccent, modifier = Modifier.size(20.dp))
                                    Spacer(Modifier.widthIn(min = 8.dp))
                                    Text("Smart Recommendations", color = primaryText, fontSize = LoanMasterTheme.typography.title.fontSize.value.sp * 0.8f, fontWeight = FontWeight.SemiBold)
                                }
                                Spacer(Modifier.widthIn(min = LoanMasterTheme.spacing.sm))
                                Surface(color = Color(0xFF3B2A6E), shape = RoundedCornerShape(LoanMasterTheme.components.iconSmall)) {
                                    Text("PRO", color = Color(0xFFB39DFF), fontSize = LoanMasterTheme.typography.label.fontSize, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 9.dp, vertical = LoanMasterTheme.spacing.xs))
                                }
                            }

                            Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.md))

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.md),
                                contentPadding = PaddingValues(end = LoanMasterTheme.spacing.md)
                            ) {
                                items(uiState.recommendations, key = { it.id }) { rec ->
                                    val cardWidth = if (isExpanded) LoanMasterTheme.components.bannerHeight else LoanMasterTheme.components.featuredCardHeight
                                    Column(
                                        modifier = Modifier
                                            .widthIn(min = cardWidth)
                                            .clip(RoundedCornerShape(LoanMasterTheme.spacing.md))
                                            .background(Color(0xFF0A1D3D))
                                            .clickable { 
                                                haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                                                selectedRecommendation = rec 
                                            }
                                            .border(
                                                1.dp,
                                                if (rec.isRecommended) when(rec.id) { "best_savings" -> Color(0xFF22C55E); "fastest_closure" -> Color(0xFF3B82F6); "lowest_emi" -> Color(0xFFA855F7); "ai_recommended" -> Color(0xFFF59E0B); else -> Color.Gray } else borderColor,
                                                RoundedCornerShape(LoanMasterTheme.spacing.md)
                                            )
                                            .padding(LoanMasterTheme.spacing.md)
                                    ) {
                                        if (rec.isRecommended) {
                                            Text(
                                                "RECOMMENDED",
                                                color = when(rec.id) { "best_savings" -> Color(0xFF22C55E); "fastest_closure" -> Color(0xFF3B82F6); "lowest_emi" -> Color(0xFFA855F7); "ai_recommended" -> Color(0xFFF59E0B); else -> Color.Gray },
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(bottom = LoanMasterTheme.spacing.sm)
                                            )
                                        }
                                        Icon(imageVector = when(rec.id) { "best_savings" -> Icons.Rounded.Savings; "fastest_closure" -> Icons.Rounded.Speed; "lowest_emi" -> Icons.AutoMirrored.Rounded.TrendingDown; "ai_recommended" -> Icons.Rounded.AutoAwesome; else -> Icons.Rounded.Info }, contentDescription = null, tint = when(rec.id) { "best_savings" -> Color(0xFF22C55E); "fastest_closure" -> Color(0xFF3B82F6); "lowest_emi" -> Color(0xFFA855F7); "ai_recommended" -> Color(0xFFF59E0B); else -> Color.Gray }, modifier = Modifier.size(LoanMasterTheme.components.iconMedium))
                                        Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.md))
                                        Text(rec.title, color = secondaryText, fontSize = LoanMasterTheme.typography.label.fontSize)
                                        Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.xs))
                                        Text(rec.description, color = when(rec.id) { "best_savings" -> Color(0xFF22C55E); "fastest_closure" -> Color(0xFF3B82F6); "lowest_emi" -> Color(0xFFA855F7); "ai_recommended" -> Color(0xFFF59E0B); else -> Color.Gray }, fontSize = LoanMasterTheme.typography.body.fontSize, fontWeight = FontWeight.Bold, lineHeight = LoanMasterTheme.typography.title.fontSize)
                                    }
                                }
                            }
                        }
                    }

                    // ==================== LOAN INTELLIGENCE (Integrated) ====================
                    if (uiState.hasValidInput) {
                        LoanIntelligenceCard(
                            loanType = uiState.loanType,
                            loanAmount = uiState.parsedLoanAmount,
                            interestRate = uiState.parsedInterestRate,
                            tenureYears = uiState.parsedTenureYears,
                            monthlyEmi = uiState.monthlyEmi,
                            totalInterest = uiState.totalInterest,
                            totalPayment = uiState.totalPayment,
                            alerts = uiState.alerts,
                            opportunities = uiState.opportunities
                        )
                    }

                    // ==================== AMORTIZATION SCHEDULE ====================
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = primaryCard),
                        shape = RoundedCornerShape(LoanMasterTheme.components.cardRadius),
                        border = BorderStroke(1.dp, borderColor)
                    ) {
                        Column(modifier = Modifier.padding(LoanMasterTheme.spacing.md)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Rounded.TableChart, contentDescription = null, tint = blueAccent, modifier = Modifier.size(20.dp))
                                    Spacer(Modifier.widthIn(min = 8.dp))
                                    Text("Amortization Schedule", color = primaryText, fontSize = LoanMasterTheme.typography.title.fontSize.value.sp * 0.75f, fontWeight = FontWeight.SemiBold)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { 
                                    haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                                    showFullSchedule = true 
                                }) {
                                    Text("Full Schedule", color = blueAccent, fontSize = LoanMasterTheme.typography.body.fontSize.value.sp * 0.9f)
                                    Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = blueAccent, modifier = Modifier.size(16.dp))
                                }
                            }
                            Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.md))

                            val scheduleData = if (uiState.yearBreakdown.size <= 4) uiState.yearBreakdown else uiState.yearBreakdown.take(3) + listOf(uiState.yearBreakdown.last())

                            Row(
                                modifier = Modifier.fillMaxWidth().padding(bottom = LoanMasterTheme.spacing.sm),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Year", color = secondaryText, fontSize = LoanMasterTheme.typography.body.fontSize.value.sp * 0.8f, modifier = Modifier.weight(1f))
                                Text("EMI Paid", color = secondaryText, fontSize = LoanMasterTheme.typography.body.fontSize.value.sp * 0.8f, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                                Text("Principal", color = secondaryText, fontSize = LoanMasterTheme.typography.body.fontSize.value.sp * 0.8f, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                                Text("Interest", color = secondaryText, fontSize = LoanMasterTheme.typography.body.fontSize.value.sp * 0.8f, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                            }
                            HorizontalDivider(color = borderColor.copy(alpha = 0.5f))

                            scheduleData.forEachIndexed { index, row ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = LoanMasterTheme.spacing.sm),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Y${row.year}", color = if (index == scheduleData.lastIndex) secondaryText else primaryText, fontSize = LoanMasterTheme.typography.body.fontSize, modifier = Modifier.weight(1f))
                                    Text(com.loanmaster.pro.core.formatter.formatMoney(row.emi), color = primaryText, fontSize = LoanMasterTheme.typography.body.fontSize, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                                    Text(com.loanmaster.pro.core.formatter.formatMoney(row.principalPaid), color = Color(0xFF22C55E), fontSize = LoanMasterTheme.typography.body.fontSize, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                                    Text(com.loanmaster.pro.core.formatter.formatMoney(row.interestPaid), color = Color(0xFFFFC328), fontSize = LoanMasterTheme.typography.body.fontSize, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                                }
                                if (index < scheduleData.lastIndex) {
                                    HorizontalDivider(color = borderColor.copy(alpha = 0.35f))
                                }
                            }
                        }
                    }

                    // ==================== LOAN MILESTONES ====================
                    if (uiState.hasValidInput && uiState.monthlySchedule.isNotEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = primaryCard),
                            shape = RoundedCornerShape(LoanMasterTheme.components.cardRadius),
                            border = BorderStroke(1.dp, borderColor)
                        ) {
                            Column(modifier = Modifier.padding(LoanMasterTheme.spacing.md)) {
                                Text("Loan Milestones", color = primaryText, fontSize = LoanMasterTheme.typography.title.fontSize.value.sp * 0.75f, fontWeight = FontWeight.SemiBold)
                                Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.md))

                                var cumPrincipal = 0.0
                                var m25: Int? = null
                                var m50: Int? = null
                                var m75: Int? = null
                                var mPrinExceedsInt: Int? = null

                                for (item in uiState.monthlySchedule) {
                                    cumPrincipal += item.principalPaid
                                    if (m25 == null && cumPrincipal >= uiState.parsedLoanAmount * 0.25) m25 = item.month
                                    if (m50 == null && cumPrincipal >= uiState.parsedLoanAmount * 0.50) m50 = item.month
                                    if (m75 == null && cumPrincipal >= uiState.parsedLoanAmount * 0.75) m75 = item.month
                                    if (mPrinExceedsInt == null && item.principalPaid > item.interestPaid) mPrinExceedsInt = item.month
                                }

                                val formatMonthYear = { m: Int? -> 
                                    if (m == null) "N/A" 
                                    else {
                                        val y = m / 12
                                        val mo = m % 12
                                        if (y > 0 && mo > 0) "${y} Yrs, ${mo} Mos" else if (y > 0) "${y} Yrs" else "${mo} Mos"
                                    }
                                }

                                val milestoneRows = listOf(
                                    "Principal > Interest" to formatMonthYear(mPrinExceedsInt),
                                    "25% Repaid" to formatMonthYear(m25),
                                    "50% Repaid" to formatMonthYear(m50),
                                    "75% Repaid" to formatMonthYear(m75),
                                    "Final EMI" to formatMonthYear(uiState.monthlySchedule.last().month)
                                )

                                milestoneRows.forEachIndexed { index, pair ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = LoanMasterTheme.spacing.sm),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(modifier = Modifier.size(LoanMasterTheme.spacing.sm).clip(androidx.compose.foundation.shape.CircleShape).background(blueAccent))
                                            Spacer(Modifier.widthIn(min = LoanMasterTheme.spacing.sm))
                                            Text(pair.first, color = secondaryText, fontSize = LoanMasterTheme.typography.body.fontSize)
                                        }
                                        Text(pair.second, color = primaryText, fontSize = LoanMasterTheme.typography.body.fontSize, fontWeight = FontWeight.Medium)
                                    }
                                    if (index < milestoneRows.lastIndex) {
                                        HorizontalDivider(color = borderColor.copy(alpha = 0.3f))
                                    }
                                }
                            }
                        }
                    }

                    // ==================== BOTTOM ACTIONS ====================
                    Row(horizontalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.md), modifier = Modifier.padding(top = LoanMasterTheme.spacing.sm, bottom = LoanMasterTheme.spacing.lg)) {
                        val context = androidx.compose.ui.platform.LocalContext.current
                        OutlinedButton(
                            onClick = { 
                                haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                                ExportUtils.exportToPdf(
                                    context,
                                    "EMI Calculator Report",
                                    listOf(
                                        "Loan Amount" to com.loanmaster.pro.core.formatter.formatMoney(uiState.parsedLoanAmount),
                                        "Interest Rate" to "$uiState.parsedInterestRate%",
                                        "Tenure" to "$uiState.parsedTenureYears Years ${uiState.totalMonths % 12} Months",
                                        "" to "",
                                        "Monthly EMI" to com.loanmaster.pro.core.formatter.formatMoney(uiState.monthlyEmi),
                                        "Total Interest" to com.loanmaster.pro.core.formatter.formatMoney(uiState.totalInterest),
                                        "Total Payment" to com.loanmaster.pro.core.formatter.formatMoney(uiState.totalPayment)
                                    )
                                )
                            },
                            modifier = Modifier.weight(1f).heightIn(min = LoanMasterTheme.components.buttonHeight),
                            shape = RoundedCornerShape(LoanMasterTheme.spacing.md),
                            border = BorderStroke(1.dp, borderColor)
                        ) {
                            Icon(Icons.Rounded.WorkspacePremium, contentDescription = null, modifier = Modifier.size(LoanMasterTheme.components.iconMedium), tint = goldAccent)
                            Spacer(Modifier.widthIn(min = LoanMasterTheme.spacing.sm))
                            Text("Premium Report", fontSize = LoanMasterTheme.typography.body.fontSize, color = goldAccent)
                        }
                        Button(
                            onClick = { 
                                haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                            },
                            modifier = Modifier.weight(1f).heightIn(min = LoanMasterTheme.components.buttonHeight),
                            shape = RoundedCornerShape(LoanMasterTheme.spacing.md),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F2744))
                        ) {
                            Icon(Icons.Rounded.Share, contentDescription = "Share as PDF", modifier = Modifier.size(LoanMasterTheme.components.iconMedium))
                            Spacer(Modifier.widthIn(min = LoanMasterTheme.spacing.sm))
                            Text("Share", fontSize = LoanMasterTheme.typography.body.fontSize)
                        }
                    }
                // FIX: Added Financial Disclaimer
                    FinancialDisclaimer()
                } // closes Column inside AnimatedVisibility
            } // closes AnimatedVisibility
             } // closes Column of resultsSection
            } // closes resultsSection lambda
            ) // closes ResponsiveScreenWrapper
        } // closes Box
    } // closes Scaffold

    // FULL SCHEDULE DIALOG
    if (showFullSchedule && uiState.hasValidInput) {
        FullAmortizationDialog(emi = uiState.monthlyEmi, schedule = uiState.monthlySchedule, onDismiss = { showFullSchedule = false })
    }

    // BOTTOM SHEET
    selectedRecommendation?.let { rec ->
        RecommendationBottomSheet(
            recommendation = rec,
            isExpandedWidth = isExpanded,
            isMediumWidth = isMedium,
            screenWidth = screenWidth,
            onDismissRequest = { selectedRecommendation = null }
        )
    }
}


