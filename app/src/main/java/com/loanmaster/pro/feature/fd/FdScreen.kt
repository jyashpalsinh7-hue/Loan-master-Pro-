package com.loanmaster.pro.feature.fd

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
import com.loanmaster.pro.data.repository.*
import com.loanmaster.pro.feature.currency.*
import com.loanmaster.pro.core.navigation.*
import com.loanmaster.pro.feature.compare.*
import com.loanmaster.pro.feature.loaneligibility.*
import com.loanmaster.pro.core.responsive.*
import com.loanmaster.pro.feature.home.*
import com.loanmaster.pro.feature.fd.*
import com.loanmaster.pro.domain.calculator.CompoundingFrequency

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
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.pow
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun FdScreen(
    onNavigateBack: () -> Unit,
    historyViewModel: HistoryViewModel? = null,
    initialHistory: CalculationHistory? = null,
    onHistoryConsumed: () -> Unit = {},
    viewModel: FdViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val sizeClass = when {
        configuration.screenWidthDp < 600 -> WindowWidthSizeClass.COMPACT
        configuration.screenWidthDp < 840 -> WindowWidthSizeClass.MEDIUM
        else -> WindowWidthSizeClass.EXPANDED
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val dummyCurrency = com.loanmaster.pro.LocalCurrency.current

    LaunchedEffect(uiState.depositText, uiState.rateText, uiState.tenureText, uiState.compounding) {
        if (historyViewModel != null && uiState.hasValidInput) {
            viewModel.saveToHistory()
        }
    }


    LaunchedEffect(initialHistory) {
        if (initialHistory != null) {
            viewModel.loadFromHistory(initialHistory)
            onHistoryConsumed()
        }
    }

    val depositAmountText = uiState.depositText
    val interestRatePaText = uiState.rateText
    val tenureYearsText = uiState.tenureText
    val compoundingFrequency = uiState.compounding.displayName
    val currentHistoryId = uiState.currentHistoryId
    
    val maturityValue = uiState.maturityValue
    val totalInvested = uiState.totalInvested
    val totalInterest = uiState.totalInterest
    val wealthGain = uiState.wealthGain
    val hasValidInput = uiState.hasValidInput
    val yearBreakdown = uiState.breakdown

    var showCompoundingDropdown by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(initialHistory) {
        if (initialHistory != null) {
            viewModel.loadFromHistory(initialHistory)
            onHistoryConsumed()
        }
    }
    
    val formatInr = { value: Double ->
        com.loanmaster.pro.core.formatter.formatMoney(value)
    }

    LaunchedEffect(depositAmountText, interestRatePaText, tenureYearsText, compoundingFrequency) {
        kotlinx.coroutines.delay(2000)
        if (historyViewModel != null && hasValidInput) {
            val history = CalculationHistory(
                id = currentHistoryId,
                calculatorType = "FD",
                title = "${formatInr(totalInvested)} at $interestRatePaText%",
                param1 = depositAmountText,
                param2 = interestRatePaText,
                param3 = tenureYearsText,
                param4 = compoundingFrequency
            )
            historyViewModel.insert(history) { id ->
                viewModel.updateHistoryId(id)
            }
        }
    }

    val formatDec = { value: Double ->
        val s = String.format(java.util.Locale.US, "%.2f", value)
        if (s.endsWith(".00")) s.substring(0, s.length - 3) else s
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
                        Text("FD Calculator", color = TextPrimary, fontSize = LoanMasterTheme.typography.title.fontSize, fontWeight = FontWeight.Bold)
                        Text("Calculate fixed deposit returns", color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize)
                    }
                    val context = androidx.compose.ui.platform.LocalContext.current
                    Icon(imageVector = Icons.Rounded.PictureAsPdf, contentDescription = "Export PDF", tint = TextPrimary, modifier = Modifier.size(LoanMasterTheme.spacing.lg).clickable {
                        ExportUtils.exportToPdf(
                            context,
                            "FD Calculator Report",
                            listOf(
                                "Total Investment" to formatInr(totalInvested),
                                "Interest Rate" to "$interestRatePaText%",
                                "Time Period" to "$tenureYearsText Years",
                                "" to "",
                                "Total Interest" to formatInr(totalInterest),
                                "Maturity Value" to formatInr(maturityValue)
                            )
                        )
                    })
                }
            }
        },
        containerColor = BackgroundDark
    ) { innerPadding ->
        com.loanmaster.pro.core.responsive.ResponsiveScreenWrapper(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            CalculatorScreenLayout(
                widthSizeClass = sizeClass,
                animationTriggerState = maturityValue,
                headerSection = { },
                inputControlsSection = {
                    // Inputs
                    Column(verticalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.screenPadding), modifier = Modifier.fillMaxWidth()) {
                        uiState.validationError?.let {
                            Text(it, color = MaterialTheme.colorScheme.error, fontSize = LoanMasterTheme.typography.label.fontSize, modifier = Modifier.padding(start = LoanMasterTheme.spacing.sm, bottom = LoanMasterTheme.spacing.xs))
                        }
                if (sizeClass == WindowWidthSizeClass.COMPACT) {
                    PremiumInputField(
                        label = "Deposit Amount", value = depositAmountText, onValueChange = { viewModel.updateInputs(depositAmount = it) },
                        icon = Icons.Rounded.AccountBalanceWallet, iconTint = AccentBlue, modifier = Modifier.fillMaxWidth()
                    )
                    PremiumInputField(
                        label = "Interest Rate (p.a.)", value = interestRatePaText, onValueChange = { viewModel.updateInputs(interestRatePa = it) },
                        icon = Icons.Rounded.Percent, iconTint = AccentBlue, modifier = Modifier.fillMaxWidth()
                    )
                    PremiumInputField(
                        label = "Tenure (Years)", value = tenureYearsText, onValueChange = { viewModel.updateInputs(tenureYears = it) },
                        icon = Icons.Rounded.DateRange, iconTint = AccentBlue, trailingIcon = Icons.Rounded.KeyboardArrowDown, suffix = " Yrs", modifier = Modifier.fillMaxWidth()
                    )
                    Box(modifier = Modifier.fillMaxWidth()) {
                        PremiumInputField(
                            isNumeric = false,
                            label = "Compounding", value = compoundingFrequency, onValueChange = {}, readOnly = true, onClick = { showCompoundingDropdown = true },
                            icon = Icons.Rounded.BarChart, iconTint = AccentBlue, trailingIcon = Icons.Rounded.KeyboardArrowDown, modifier = Modifier.fillMaxWidth(),
                            infoText = "How often interest is calculated and added to your principal. More frequent compounding leads to higher returns."
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
                                        viewModel.updateInputs(compoundingFreq = option)
                                        showCompoundingDropdown = false
                                    }
                                )
                            }
                        }
                    }
                } else {
                    Row(horizontalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.md), modifier = Modifier.fillMaxWidth()) {
                        Box(modifier = Modifier.weight(1f)) {
                            PremiumInputField(
                                label = "Deposit Amount", value = depositAmountText, onValueChange = { viewModel.updateInputs(depositAmount = it) },
                                icon = Icons.Rounded.AccountBalanceWallet, iconTint = AccentBlue
                            )
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            PremiumInputField(
                                label = "Interest Rate (p.a.)", value = interestRatePaText, onValueChange = { viewModel.updateInputs(interestRatePa = it) },
                                icon = Icons.Rounded.Percent, iconTint = AccentBlue
                            )
                        }
                    }
                    // FIX: Added Financial Disclaimer
                    FinancialDisclaimer()
                    Row(horizontalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.md), modifier = Modifier.fillMaxWidth()) {
                        Box(modifier = Modifier.weight(1f)) {
                            PremiumInputField(
                                label = "Tenure (Years)", value = tenureYearsText, onValueChange = { viewModel.updateInputs(tenureYears = it) },
                                icon = Icons.Rounded.DateRange, iconTint = AccentBlue, trailingIcon = Icons.Rounded.KeyboardArrowDown, suffix = " Yrs"
                            )
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            PremiumInputField(
                                isNumeric = false,
                                label = "Compounding", value = compoundingFrequency, onValueChange = {}, readOnly = true, onClick = { showCompoundingDropdown = true },
                                icon = Icons.Rounded.BarChart, iconTint = AccentBlue, trailingIcon = Icons.Rounded.KeyboardArrowDown,
                                infoText = "How often interest is calculated and added to your principal. More frequent compounding leads to higher returns."
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
                                            viewModel.updateInputs(compoundingFreq = option)
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
            Column(verticalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.screenPadding)) {
                // Hero Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(LoanMasterTheme.spacing.md))
                        .background(SurfaceDark)
                    .border(1.dp, CardStroke, RoundedCornerShape(LoanMasterTheme.spacing.md))
                    .padding(LoanMasterTheme.components.iconSmall)
            ) {
                Column {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(0.6f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Estimated Maturity Value", color = TextSecondary, fontSize = LoanMasterTheme.typography.body.fontSize)
                                Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.sm))
                                Icon(Icons.Rounded.Info, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(LoanMasterTheme.spacing.md))
                            }
                            Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.sm))
                            val formattedMaturityValue = formatInr(maturityValue)
                            AutoResizedText(
                                text = formattedMaturityValue,
                                color = AccentGreen,
                                fontSize = LoanMasterTheme.typography.display.fontSize,
                                fontWeight = FontWeight.ExtraBold,
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.md))
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(LoanMasterTheme.spacing.md))
                                    .background(AccentGreen.copy(alpha = 0.1f))
                                    .padding(horizontal = LoanMasterTheme.spacing.md, vertical = LoanMasterTheme.spacing.sm),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val parsedTenure = tenureYearsText.toDoubleOrNull() ?: 0.0
                                val effectiveYield = if (totalInvested > 0 && parsedTenure > 0) (totalInterest / totalInvested) / parsedTenure * 100 else 0.0
                                Icon(Icons.AutoMirrored.Rounded.TrendingUp, contentDescription = null, tint = AccentGreen, modifier = Modifier.size(LoanMasterTheme.spacing.md))
                                Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.xs))
                                Text("Total Returns: ${formatDec((totalInterest / totalInvested) * 100)}%", color = AccentGreen, fontSize = LoanMasterTheme.typography.label.fontSize, fontWeight = FontWeight.Bold)
                            }
                        }
                        Box(
                            modifier = Modifier
                                .weight(0.4f)
                                .heightIn(min = LoanMasterTheme.components.calculatorCardHeight),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Savings,
                                contentDescription = null,
                                tint = AccentBlue.copy(alpha = 0.8f),
                                modifier = Modifier.size(LoanMasterTheme.components.iconLarge * 2)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.lg))
                    HorizontalDivider(color = CardStroke)
                    Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.md))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(horizontalAlignment = Alignment.Start) {
                            Text("Invested Amount", color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize)
                            Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.xs))
                            Text(formatInr(totalInvested), color = TextPrimary, fontSize = LoanMasterTheme.typography.body.fontSize)
                        }
                        Box(modifier = Modifier.widthIn(min = 1.dp).heightIn(min = LoanMasterTheme.spacing.xl).background(CardStroke))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Total Interest", color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize)
                            Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.xs))
                            Text(formatInr(totalInterest), color = TextPrimary, fontSize = LoanMasterTheme.typography.body.fontSize)
                        }
                        Box(modifier = Modifier.widthIn(min = 1.dp).heightIn(min = LoanMasterTheme.spacing.xl).background(CardStroke))
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Effective Yield (CAGR)", color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize)
                            Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.xs))
                            Text("${formatDec(interestRatePaText.toDoubleOrNull() ?: 0.0)}%", color = AccentBlue, fontSize = LoanMasterTheme.typography.body.fontSize, fontWeight = FontWeight.Bold)
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
                        ScrollingTitleText("Investment Growth Over Time", color = TextPrimary, fontSize = LoanMasterTheme.typography.label.fontSize, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.xs))
                        Icon(Icons.Rounded.Info, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(LoanMasterTheme.spacing.md))
                    }
                    Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.md))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.md)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.md).heightIn(min = LoanMasterTheme.spacing.xs).background(AccentBlue))
                            Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.xs))
                            Text("Maturity Value", color = TextSecondary, fontSize = 9.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.md).heightIn(min = LoanMasterTheme.spacing.xs).background(AccentYellow))
                            Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.xs))
                            Text("Principal", color = TextSecondary, fontSize = 9.sp)
                        }
                    }
                    Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.md))
                    Row(modifier = Modifier.fillMaxWidth().heightIn(min = LoanMasterTheme.components.featuredCardHeight)) {
                        Column(modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.SpaceBetween) {
                            Text("1.6L", color = TextSecondary, fontSize = 9.sp)
                            Text("1.2L", color = TextSecondary, fontSize = 9.sp)
                            Text("80K", color = TextSecondary, fontSize = 9.sp)
                            Text("40K", color = TextSecondary, fontSize = 9.sp)
                            Text("0", color = TextSecondary, fontSize = 9.sp)
                        }
                        Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.sm))
                        Column(modifier = Modifier.weight(1f)) {
                            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                                Canvas(modifier = Modifier.fillMaxSize().padding(vertical = LoanMasterTheme.spacing.md)) {
                                    val h = size.height
                                    val w = size.width
                                    val investedPath = Path().apply {
                                        moveTo(0f, h)
                                        lineTo(w * 0.25f, h * 0.9f)
                                        lineTo(w * 0.5f, h * 0.8f)
                                        lineTo(w * 0.75f, h * 0.7f)
                                        lineTo(w, h * 0.6f)
                                    }
                                    val maturityPath = Path().apply {
                                        moveTo(0f, h)
                                        lineTo(w * 0.25f, h * 0.8f)
                                        lineTo(w * 0.5f, h * 0.55f)
                                        lineTo(w * 0.75f, h * 0.25f)
                                        lineTo(w, 0f)
                                    }
                                    // Grid lines
                                    for (i in 0..4) {
                                        drawLine(CardStroke, androidx.compose.ui.geometry.Offset(0f, h * i / 4), androidx.compose.ui.geometry.Offset(w, h * i / 4), 1.dp.toPx())
                                    }
                                    for (i in 0..4) {
                                        drawLine(CardStroke, androidx.compose.ui.geometry.Offset(w * i / 4, 0f), androidx.compose.ui.geometry.Offset(w * i / 4, h), 1.dp.toPx())
                                    }
                                    
                                    drawPath(investedPath, color = AccentYellow, style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round))
                                    drawPath(maturityPath, color = AccentBlue, style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round))
                                    
                                    // Points on the lines
                                    val pointsX = listOf(0f, w * 0.25f, w * 0.5f, w * 0.75f, w)
                                    val maturityY = listOf(h, h * 0.8f, h * 0.55f, h * 0.25f, 0f)
                                    val investedY = listOf(h, h * 0.9f, h * 0.8f, h * 0.7f, h * 0.6f)
                                    
                                    for (i in 1..4) {
                                        drawCircle(color = AccentBlue, radius = 4.dp.toPx(), center = androidx.compose.ui.geometry.Offset(pointsX[i], maturityY[i]))
                                        drawCircle(color = AccentYellow, radius = 4.dp.toPx(), center = androidx.compose.ui.geometry.Offset(pointsX[i], investedY[i]))
                                    }
                                }
                                // Popup labels
                                Box(modifier = Modifier.fillMaxSize()) {
                                    Box(
                                        modifier = Modifier.align(Alignment.TopEnd).offset(x = LoanMasterTheme.spacing.gridGutter, y = (-10).dp)
                                            .clip(RoundedCornerShape(LoanMasterTheme.spacing.xs)).background(AccentBlue).padding(horizontal = LoanMasterTheme.spacing.xs, vertical = LoanMasterTheme.spacing.xs)
                                    ) { Text(formatInr(maturityValue), color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold) }
                                    Box(
                                        modifier = Modifier.align(Alignment.BottomEnd).offset(x = LoanMasterTheme.spacing.gridGutter, y = (-30).dp)
                                            .clip(RoundedCornerShape(LoanMasterTheme.spacing.xs)).background(AccentYellow).padding(horizontal = LoanMasterTheme.spacing.xs, vertical = LoanMasterTheme.spacing.xs)
                                    ) { Text(formatInr(totalInvested), color = BackgroundDark, fontSize = 9.sp, fontWeight = FontWeight.Bold) }
                                }
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("1Y", color = TextSecondary, fontSize = 9.sp)
                                Text("2Y", color = TextSecondary, fontSize = 9.sp)
                                Text("3Y", color = TextSecondary, fontSize = 9.sp)
                                Text("4Y", color = TextSecondary, fontSize = 9.sp)
                                Text("5Y", color = TextSecondary, fontSize = 9.sp)
                            }
                        }
                    }
                }
                
                // Deposit vs Interest Earned
                Column(
                    modifier = Modifier.weight(1f).clip(RoundedCornerShape(LoanMasterTheme.spacing.md)).background(SurfaceDark).border(1.dp, CardStroke, RoundedCornerShape(LoanMasterTheme.spacing.md)).padding(LoanMasterTheme.spacing.md)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        ScrollingTitleText("Deposit vs Interest Earned", color = TextPrimary, fontSize = LoanMasterTheme.typography.label.fontSize, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.xs))
                        Icon(Icons.Rounded.Info, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(LoanMasterTheme.spacing.md))
                    }
                    Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.components.iconSmall))
                    Box(modifier = Modifier.fillMaxWidth().heightIn(min = LoanMasterTheme.components.featuredCardHeight), contentAlignment = Alignment.Center) {
                        val invPct = if (maturityValue > 0) (totalInvested / maturityValue).toFloat() else 1f
                        Canvas(modifier = Modifier.size(LoanMasterTheme.components.calculatorCardHeight).padding(LoanMasterTheme.spacing.sm)) {
                            val strokeWidth = 12.dp.toPx()
                            drawArc(
                                color = AccentBlue,
                                startAngle = -90f,
                                sweepAngle = 360f * invPct,
                                useCenter = false,
                                style = Stroke(strokeWidth, cap = StrokeCap.Butt)
                            )
                            drawArc(
                                color = AccentYellow,
                                startAngle = -90f + (360f * invPct),
                                sweepAngle = 360f * (1 - invPct),
                                useCenter = false,
                                style = Stroke(strokeWidth, cap = StrokeCap.Butt)
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(LoanMasterTheme.spacing.md)) {
                            AutoSizeText(formatInr(maturityValue), color = TextPrimary, minTextSize = LoanMasterTheme.typography.label.fontSize, maxTextSize = LoanMasterTheme.typography.body.fontSize, fontWeight = FontWeight.Bold)
                            Text("Total Value", color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize)
                        }
                    }
                    Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.md))
                    Column(modifier = Modifier.fillMaxWidth()) {
                        val invPctStr = String.format(Locale.US, "%.1f%%", if (maturityValue > 0) (totalInvested/maturityValue)*100 else 100f)
                        val retPctStr = String.format(Locale.US, "%.1f%%", if (maturityValue > 0) (totalInterest/maturityValue)*100 else 0f)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(Modifier.size(LoanMasterTheme.spacing.sm).clip(CircleShape).background(AccentBlue))
                                Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.sm))
                                Text("Deposit Amount", color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize)
                            }
                            Text("${formatInr(totalInvested)} ($invPctStr)", color = TextPrimary, fontSize = LoanMasterTheme.typography.label.fontSize)
                        }
                        Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.sm))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(Modifier.size(LoanMasterTheme.spacing.sm).clip(CircleShape).background(AccentYellow))
                                Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.sm))
                                Text("Interest Earned", color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize)
                            }
                            Text("${formatInr(totalInterest)} ($retPctStr)", color = TextPrimary, fontSize = LoanMasterTheme.typography.label.fontSize)
                        }
                    }
                }
            }

            // What If
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.md)) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text("What If?", color = TextPrimary, fontSize = LoanMasterTheme.typography.body.fontSize, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.sm))
                    Text("Quick changes to see impact", color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize)
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.md)
                ) {
                    WhatIfActionButton("+1%", "Rate", Icons.AutoMirrored.Rounded.TrendingUp, AccentGreen)
                    WhatIfActionButton("-1%", "Rate", Icons.AutoMirrored.Rounded.TrendingDown, Color(0xFFE53935))
                    WhatIfActionButton("+2 Years", "Tenure", Icons.Rounded.Event, Color(0xFF8E24AA))
                    WhatIfActionButton("+${com.loanmaster.pro.core.formatter.currentCurrencySymbol}50,000", "Deposit", Icons.Rounded.AccountBalanceWallet, AccentYellow)
                }
            }

            // Projection Summary
            Column(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(LoanMasterTheme.spacing.md)).background(SurfaceDark).border(1.dp, CardStroke, RoundedCornerShape(LoanMasterTheme.spacing.md)).padding(vertical = LoanMasterTheme.spacing.md)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(start = LoanMasterTheme.spacing.md, end = LoanMasterTheme.spacing.md, bottom = LoanMasterTheme.spacing.md),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text("FD Projection", color = TextPrimary, fontSize = LoanMasterTheme.typography.body.fontSize, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.sm))
                        Text("(Quarterly Compounding)", color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { }) {
                        Text("View Full Schedule", color = AccentBlue, fontSize = LoanMasterTheme.typography.label.fontSize)
                        Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = AccentBlue, modifier = Modifier.size(LoanMasterTheme.spacing.md))
                    }
                }
                
                Column(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
                    Row(modifier = Modifier.defaultMinSize(minWidth = 380.dp).fillMaxWidth().background(CardStroke.copy(alpha = 0.5f)).padding(vertical = LoanMasterTheme.spacing.gridGutter, horizontal = LoanMasterTheme.spacing.md)) {
                        Text("Year", color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize, modifier = Modifier.weight(1f).padding(horizontal = LoanMasterTheme.spacing.xs))
                        Text("Deposit (${com.loanmaster.pro.core.formatter.currentCurrencySymbol})", color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize, modifier = Modifier.weight(1.5f).padding(horizontal = LoanMasterTheme.spacing.xs), textAlign = TextAlign.Center)
                        Text("Interest (${com.loanmaster.pro.core.formatter.currentCurrencySymbol})", color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize, modifier = Modifier.weight(1.5f).padding(horizontal = LoanMasterTheme.spacing.xs), textAlign = TextAlign.End)
                        Text("Maturity Value (${com.loanmaster.pro.core.formatter.currentCurrencySymbol})", color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize, modifier = Modifier.weight(1.5f).padding(horizontal = LoanMasterTheme.spacing.xs), textAlign = TextAlign.End)
                    }
                    
                    yearBreakdown.forEachIndexed { index, bd ->
                        val y = bd.year
                        val p = bd.openingBalance
                        val tMat = bd.closingBalance
                        val tRet = bd.interestEarned
                        val isLast = index == yearBreakdown.size - 1
                        val color = if (isLast) AccentBlue else TextPrimary
                        val formatInrNum = { value: Double -> com.loanmaster.pro.core.formatter.formatMoney(value) }
                        
                        Row(modifier = Modifier.defaultMinSize(minWidth = 380.dp).fillMaxWidth().padding(vertical = LoanMasterTheme.spacing.md, horizontal = LoanMasterTheme.spacing.md)) {
                            val yDisplay = if (y % 1.0 == 0.0) y.toInt().toString() else y.toString()
                            Text(yDisplay, color = color, fontSize = LoanMasterTheme.typography.label.fontSize, modifier = Modifier.weight(1f).padding(horizontal = LoanMasterTheme.spacing.xs))
                            Text(formatInrNum(p), color = color, fontSize = LoanMasterTheme.typography.label.fontSize, modifier = Modifier.weight(1.5f).padding(horizontal = LoanMasterTheme.spacing.xs), textAlign = TextAlign.Center)
                            Text(formatInrNum(tRet), color = color, fontSize = LoanMasterTheme.typography.label.fontSize, modifier = Modifier.weight(1.5f).padding(horizontal = LoanMasterTheme.spacing.xs), textAlign = TextAlign.End)
                            Text(formatInrNum(tMat), color = color, fontSize = LoanMasterTheme.typography.label.fontSize, modifier = Modifier.weight(1.5f).padding(horizontal = LoanMasterTheme.spacing.xs), textAlign = TextAlign.End)
                        }
                        if (!isLast) HorizontalDivider(color = CardStroke)
                    }
                }
            }
          }
        }
    )
  }
}
}

@Composable
fun FdInputField(label: String, value: String, icon: ImageVector, iconColor: Color, hasDropdown: Boolean = false, onClick: () -> Unit) {
    Column(modifier = Modifier.wrapContentHeight()) {
        Text(label, color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize, modifier = Modifier.padding(bottom = LoanMasterTheme.spacing.sm))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(LoanMasterTheme.spacing.sm))
                .background(SurfaceDark)
                .border(1.dp, CardStroke, RoundedCornerShape(LoanMasterTheme.spacing.sm))
                .clickable { onClick() }
                .padding(horizontal = LoanMasterTheme.spacing.md, vertical = LoanMasterTheme.spacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(LoanMasterTheme.components.iconSmall))
            Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.sm))
            Text(value, color = TextPrimary, fontSize = LoanMasterTheme.typography.body.fontSize, modifier = Modifier.weight(1f))
            if (hasDropdown) {
                Icon(Icons.Rounded.KeyboardArrowDown, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(LoanMasterTheme.components.iconSmall))
            }
        }
    }
}

@Composable
fun FdLegend(label: String, value: String, percentage: String, color: Color, modifier: Modifier = Modifier) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
        Box(Modifier.size(LoanMasterTheme.spacing.sm).clip(CircleShape).background(color))
        Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.sm))
        ScrollingTitleText(label, color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize)
        Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.sm))
        Text(value, color = TextPrimary, fontSize = LoanMasterTheme.typography.label.fontSize, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.xs))
        Text(percentage, color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize)
    }
}

@Composable
fun WhatIfActionButton(title1: String, title2: String, icon: ImageVector, color: Color) {
    Row(
        modifier = Modifier.defaultMinSize(minWidth = LoanMasterTheme.components.calculatorCardHeight).clip(RoundedCornerShape(LoanMasterTheme.spacing.sm)).background(SurfaceDark).border(1.dp, CardStroke, RoundedCornerShape(LoanMasterTheme.spacing.sm)).clickable { }.padding(LoanMasterTheme.spacing.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(LoanMasterTheme.spacing.lg))
        Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.gridGutter))
        Column(modifier = Modifier.weight(1f, fill = false)) {
            Text(title1, color = color, fontSize = LoanMasterTheme.typography.label.fontSize, fontWeight = FontWeight.Bold)
            Text(title2, color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize)
        }
    }
}
