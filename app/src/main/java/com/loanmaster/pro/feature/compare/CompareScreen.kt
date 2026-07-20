package com.loanmaster.pro.feature.compare

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
import com.loanmaster.pro.feature.loaneligibility.*
import com.loanmaster.pro.core.responsive.*
import com.loanmaster.pro.feature.home.*

import androidx.window.core.layout.WindowWidthSizeClass



import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material.icons.automirrored.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.focus.onFocusChanged
import kotlin.math.pow
import java.text.NumberFormat
import java.util.Locale





@Composable
fun SideBySideLoanInputs(
    loanAState: LoanOptionState,
    loanBState: LoanOptionState,
    onLoanAChange: (amount: String?, interest: String?, years: String?, months: String?) -> Unit,
    onLoanBChange: (amount: String?, interest: String?, years: String?, months: String?) -> Unit,
    onCompareClick: () -> Unit,
    onResetClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(LoanMasterTheme.spacing.lg))
            .background(Color(0xFF061633))
            .border(1.dp, CardStroke, RoundedCornerShape(LoanMasterTheme.spacing.lg))
            .padding(LoanMasterTheme.components.iconSmall)
    ) {
        Column {
            Row(modifier = Modifier.fillMaxWidth().height(androidx.compose.foundation.layout.IntrinsicSize.Min)) {
                LoanInputColumn(
                    title = "Loan A",
                    icon = Icons.Rounded.AccountBalance,
                    iconTint = Color(0xFF3B82F6),
                    state = loanAState,
                    onAmountChange = { onLoanAChange(it, null, null, null) },
                    onInterestChange = { onLoanAChange(null, it, null, null) },
                    onYearsChange = { onLoanAChange(null, null, it, null) },
                    onMonthsChange = { onLoanAChange(null, null, null, it) },
                    modifier = Modifier.weight(1f)
                )
                
                Box(
                    modifier = Modifier
                        .padding(horizontal = LoanMasterTheme.spacing.md)
                        .widthIn(min = 1.dp)
                        .fillMaxHeight()
                        .background(CardStroke)
                )
                
                LoanInputColumn(
                    title = "Loan B",
                    icon = Icons.Rounded.AccountBalance,
                    iconTint = Color(0xFF10B981),
                    state = loanBState,
                    onAmountChange = { onLoanBChange(it, null, null, null) },
                    onInterestChange = { onLoanBChange(null, it, null, null) },
                    onYearsChange = { onLoanBChange(null, null, it, null) },
                    onMonthsChange = { onLoanBChange(null, null, null, it) },
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.lg))
            
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Button(
                    onClick = onCompareClick,
                    modifier = Modifier.weight(1f).heightIn(min = LoanMasterTheme.components.buttonHeight),
                    shape = RoundedCornerShape(LoanMasterTheme.spacing.md),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentBlue, contentColor = Color.White)
                ) {
                    Icon(Icons.AutoMirrored.Rounded.CompareArrows, contentDescription = null)
                    Spacer(Modifier.widthIn(min = LoanMasterTheme.spacing.sm))
                    Text("Compare Loans", fontSize = LoanMasterTheme.typography.body.fontSize, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.md))
                IconButton(
                    onClick = onResetClick,
                    modifier = Modifier
                        .size(LoanMasterTheme.components.buttonHeight)
                        .clip(androidx.compose.foundation.shape.CircleShape)
                        .background(Color(0xFF0D1B36))
                        .border(1.dp, CardStroke, androidx.compose.foundation.shape.CircleShape)
                ) {
                    Icon(Icons.Rounded.Refresh, contentDescription = "Reset", tint = TextSecondary)
                }
            }
        }
    }
}

@Composable
fun LoanInputColumn(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    state: LoanOptionState,
    onAmountChange: (String) -> Unit,
    onInterestChange: (String) -> Unit,
    onYearsChange: (String) -> Unit,
    onMonthsChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(LoanMasterTheme.components.iconSmall))
            Spacer(Modifier.widthIn(min = LoanMasterTheme.spacing.sm))
            Text(title, color = Color.White, fontSize = LoanMasterTheme.typography.body.fontSize, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.heightIn(min = LoanMasterTheme.components.iconSmall))
        
        PremiumInputField(
            value = state.amountText,
            onValueChange = { onAmountChange(it) },
            label = "Amount",
            icon = Icons.Rounded.AccountBalanceWallet,
            iconTint = iconTint,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.md))
        PremiumInputField(
            value = state.interestText,
            onValueChange = { onInterestChange(it) },
            label = "Interest Rate (p.a.)",
            icon = Icons.Rounded.Percent,
            iconTint = iconTint,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.md))
        Text("Tenure", color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize)
        Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.sm))
        Row(horizontalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.sm)) {
            var yearsFocused by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf(false) }
            val yearsBorderColor by androidx.compose.animation.animateColorAsState(targetValue = if (yearsFocused) AccentBlue else CardStroke)
            val yearsBorderWidth by androidx.compose.animation.core.animateDpAsState(targetValue = if (yearsFocused) 2.dp else 1.dp)

            androidx.compose.material3.Surface(
                shape = RoundedCornerShape(LoanMasterTheme.spacing.md),
                color = Color(0xFF0D1B36), // SurfaceDark equivalent
                border = androidx.compose.foundation.BorderStroke(yearsBorderWidth, yearsBorderColor),
                modifier = Modifier.weight(1f).heightIn(min = LoanMasterTheme.components.iconLarge)
            ) {
                Row(modifier = Modifier.padding(horizontal = LoanMasterTheme.spacing.sm), verticalAlignment = Alignment.CenterVertically) {
                    androidx.compose.foundation.text.BasicTextField(
                        value = state.yearsText,
                        onValueChange = { onYearsChange(it.filter { c -> c.isDigit() }) },
                        textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = LoanMasterTheme.typography.label.fontSize),
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                        modifier = Modifier.weight(1f).onFocusChanged { yearsFocused = it.isFocused },
                        cursorBrush = androidx.compose.ui.graphics.SolidColor(AccentBlue)
                    ) { inner ->
                        Box {
                            if (state.yearsText.isEmpty()) Text("Years", color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize, minLines = 1, maxLines = 2, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                            inner()
                        }
                    }
                }
            }

            var monthsFocused by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf(false) }
            val monthsBorderColor by androidx.compose.animation.animateColorAsState(targetValue = if (monthsFocused) AccentBlue else CardStroke)
            val monthsBorderWidth by androidx.compose.animation.core.animateDpAsState(targetValue = if (monthsFocused) 2.dp else 1.dp)

            androidx.compose.material3.Surface(
                shape = RoundedCornerShape(LoanMasterTheme.spacing.md),
                color = Color(0xFF0D1B36),
                border = androidx.compose.foundation.BorderStroke(monthsBorderWidth, monthsBorderColor),
                modifier = Modifier.weight(1f).heightIn(min = LoanMasterTheme.components.iconLarge)
            ) {
                Row(modifier = Modifier.padding(horizontal = LoanMasterTheme.spacing.sm), verticalAlignment = Alignment.CenterVertically) {
                    androidx.compose.foundation.text.BasicTextField(
                        value = state.monthsText,
                        onValueChange = { onMonthsChange(it.filter { c -> c.isDigit() }) },
                        textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = LoanMasterTheme.typography.label.fontSize),
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                        modifier = Modifier.weight(1f).onFocusChanged { monthsFocused = it.isFocused },
                        cursorBrush = androidx.compose.ui.graphics.SolidColor(AccentBlue)
                    ) { inner ->
                        Box {
                            if (state.monthsText.isEmpty()) Text("Months", color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize, minLines = 1, maxLines = 2, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                            inner()
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompareScreen(onNavigateBack: () -> Unit, viewModel: CompareViewModel = viewModel()) {
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val sizeClass = when {
        configuration.screenWidthDp < 600 -> WindowWidthSizeClass.COMPACT
        configuration.screenWidthDp < 840 -> WindowWidthSizeClass.MEDIUM
        else -> WindowWidthSizeClass.EXPANDED
    }

    val bgColor = BackgroundDark
    val textColor = TextPrimary

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val dummyCurrency = com.loanmaster.pro.LocalCurrency.current
    
    val loanAState = uiState.loanA
    val loanBState = uiState.loanB
    val showResults = uiState.showResults
    val processedLoans = uiState.processedLoans
    val currentLoans = listOf(
        LoanOffer(loanAState.id, loanAState.bankName, loanAState.interestRate, loanAState.tenureYears, loanAState.tenureMonths, loanAState.loanAmount, 0.0, 0.0),
        LoanOffer(loanBState.id, loanBState.bankName, loanBState.interestRate, loanBState.tenureYears, loanBState.tenureMonths, loanBState.loanAmount, 0.0, 0.0)
    )
    val hasValidInput = uiState.hasValidInput

    var showUnlockDialog by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf(false) }
    val premiumManagerContext = androidx.compose.ui.platform.LocalContext.current
    val premiumManager = remember { com.loanmaster.pro.core.managers.PremiumManager(premiumManagerContext.applicationContext) }
    val isPremiumUnlocked by premiumManager.isPremium.collectAsStateWithLifecycle()
    var selectedPremiumTool by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf<String?>(null) }
    val bestLoan = processedLoans.find { it.isBest }

    Scaffold(
        containerColor = bgColor,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(bgColor)
                    .statusBarsPadding()
                    .padding(horizontal = LoanMasterTheme.spacing.sm, vertical = LoanMasterTheme.spacing.sm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back", tint = textColor)
                }
                Column(modifier = Modifier.weight(1f).padding(horizontal = LoanMasterTheme.spacing.xs)) {
                    Text("Compare Loans", color = textColor, fontWeight = FontWeight.Bold, fontSize = LoanMasterTheme.typography.title.fontSize)
                    Text("Compare up to 3 loan options side by side", color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize)
                }
                IconButton(onClick = { }) {
                    Icon(Icons.Rounded.Info, contentDescription = "Info", tint = textColor)
                }
            }
        }
    ) { padding ->
        com.loanmaster.pro.core.responsive.ResponsiveScreenWrapper(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            var emiTrigger = 0.0
            if (processedLoans.isNotEmpty()) {
                emiTrigger = processedLoans.sumOf { localCalculateEMI(it.loanAmount, it.interestRate, it.totalMonths) }
            }
            CalculatorScreenLayout(
                widthSizeClass = sizeClass,
                animationTriggerState = emiTrigger,
                headerSection = { },
                inputControlsSection = {
                    SideBySideLoanInputs(
                        loanAState = loanAState,
                        loanBState = loanBState,
                        onLoanAChange = { a, i, y, m -> viewModel.onEvent(LoanComparisonEvent.UpdateLoanA(a, i, y, m)) },
                        onLoanBChange = { a, i, y, m -> viewModel.onEvent(LoanComparisonEvent.UpdateLoanB(a, i, y, m)) },
                        onCompareClick = { viewModel.onEvent(LoanComparisonEvent.ShowResults) },
                        onResetClick = { viewModel.onEvent(LoanComparisonEvent.Reset) }
                    )
                },
                resultsSection = {
                    Column(verticalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.screenPadding)) {
                        val isAnyLoanAdded = processedLoans.any { it.loanAmount > 0.0 || it.interestRate > 0.0 || it.totalMonths > 0 }
                        if (!isAnyLoanAdded) {
                            EmptyStateIllustration()
                        } else {
                            ComparisonTable(loans = processedLoans)
                            LoanAdvisorSection(
                                loans = processedLoans,
                                bestLoan = bestLoan,
                                isPremiumUnlocked = isPremiumUnlocked,
                                onUnlockRequested = { showUnlockDialog = true }
                            )
                            WhatYouWillUnlockSection(
                                isPremiumUnlocked = isPremiumUnlocked,
                                onToolClick = { tool -> selectedPremiumTool = tool }
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth().padding(top = LoanMasterTheme.spacing.sm),
                                horizontalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.md)
                            ) {
                                Button(
                                    onClick = { }, 
                                    modifier = Modifier.weight(1f).heightIn(min = LoanMasterTheme.components.topAppBarHeight), 
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF061633), contentColor = AccentBlue), 
                                    shape = RoundedCornerShape(LoanMasterTheme.spacing.md),
                                    border = BorderStroke(1.dp, CardStroke)
                                ) { 
                                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                                        Icon(Icons.Rounded.BookmarkBorder, contentDescription = "Save calculation", modifier = Modifier.size(LoanMasterTheme.components.iconSmall))
                                        Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.xs))
                                        Text("Save", fontSize = LoanMasterTheme.typography.label.fontSize, fontWeight = FontWeight.Bold) 
                                    }
                                }
                                Button(
                                    onClick = { }, 
                                    modifier = Modifier.weight(1f).heightIn(min = LoanMasterTheme.components.topAppBarHeight), 
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF061633), contentColor = AccentYellow), 
                                    shape = RoundedCornerShape(LoanMasterTheme.spacing.md),
                                    border = BorderStroke(1.dp, CardStroke)
                                ) { 
                                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                                        Icon(Icons.Rounded.Share, contentDescription = "Share as PDF", modifier = Modifier.size(LoanMasterTheme.components.iconSmall))
                                        Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.xs))
                                        Text("Share", fontSize = LoanMasterTheme.typography.label.fontSize, fontWeight = FontWeight.Bold) 
                                    }
                                }
                                val context = androidx.compose.ui.platform.LocalContext.current
                                Button(
                                    onClick = {
                                        val reportData = mutableListOf<Pair<String, String>>()
                                        processedLoans.forEachIndexed { index, loan ->
                                            val emi = localCalculateEMI(loan.loanAmount, loan.interestRate, loan.totalMonths)
                                            val totalPayment = (emi * loan.totalMonths) + loan.processingFee
                                            val totalInterest = totalPayment - loan.loanAmount
                                            
                                            reportData.add("Loan ${index + 1} (${loan.bankName})" to "")
                                            reportData.add("Amount" to com.loanmaster.pro.core.formatter.formatMoney(loan.loanAmount))
                                            reportData.add("Rate" to "${loan.interestRate}%")
                                            reportData.add("Tenure" to "${loan.totalMonths} months")
                                            reportData.add("Monthly EMI" to com.loanmaster.pro.core.formatter.formatMoney(emi))
                                            reportData.add("Total Interest" to com.loanmaster.pro.core.formatter.formatMoney(totalInterest))
                                            reportData.add("Total Payment" to com.loanmaster.pro.core.formatter.formatMoney(totalPayment))
                                            reportData.add("" to "")
                                        }
                                        ExportUtils.exportToPdf(context, "Loan Comparison Report", reportData)
                                    }, 
                                    modifier = Modifier.weight(1f).heightIn(min = LoanMasterTheme.components.topAppBarHeight), 
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF061633), contentColor = AccentGreen), 
                                    shape = RoundedCornerShape(LoanMasterTheme.spacing.md),
                                    border = BorderStroke(1.dp, CardStroke)
                                ) { 
                                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                                        Icon(Icons.Rounded.PictureAsPdf, contentDescription = null, modifier = Modifier.size(LoanMasterTheme.components.iconSmall))
                                        Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.xs))
                                        Text("PDF", fontSize = LoanMasterTheme.typography.label.fontSize, fontWeight = FontWeight.Bold) 
                                    }
                                }
// FIX: Added Financial Disclaimer
                    FinancialDisclaimer()
                            }
                        }
                    }
                }
            )
        }
    }

    if (showUnlockDialog) {
        val dialogContext = androidx.compose.ui.platform.LocalContext.current
        com.loanmaster.pro.core.ui.PremiumUnlockDialog(
            onDismiss = { showUnlockDialog = false },
            onUnlockSuccessful = { 
                premiumManager.unlockPermanent()
            }
        )
    }

    if (selectedPremiumTool != null) {
        ModalBottomSheet(
            onDismissRequest = { selectedPremiumTool = null },
            containerColor = SurfaceDark
        ) {
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(LoanMasterTheme.spacing.md)
                .verticalScroll(rememberScrollState())
                .windowInsetsPadding(WindowInsets.safeDrawing)
            ) {
                Text(selectedPremiumTool ?: "", color = Color.White, fontSize = LoanMasterTheme.typography.title.fontSize, fontWeight = FontWeight.Bold)
                Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.md))
                when (selectedPremiumTool) {
                    "True Cost (APR)" -> {
                        Text("Banks often advertise a low interest rate, but hide costs in processing fees. Here is the 'True Cost' (Effective APR) of your loans.", color = TextSecondary, fontSize = LoanMasterTheme.typography.body.fontSize)
                        Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.md))
                        processedLoans.forEach { loan ->
                            val loanColor = if(loan.id == "A") Color(0xFF3B82F6) else Color(0xFF10B981)
                            val emi = localCalculateEMI(loan.loanAmount, loan.interestRate, loan.totalMonths)
                            val totalPayment = (emi * loan.totalMonths) + loan.processingFee
                            val totalInterestAndFees = totalPayment - loan.loanAmount
                            // Rough APR Approximation (Internal Rate of Return is hard to calculate without a loop, so we use a simplified formula)
                            // APR ≈ (24 * Total Finance Charges) / (Principal * (Total Payments + 1))
                            val financeCharges = totalInterestAndFees
                            val totalMonths = loan.totalMonths
                            val apr = (24 * financeCharges) / (loan.loanAmount * (totalMonths + 1)) * 100
                            
                            ResponsiveCard(bgColor = BackgroundDark, borderColor = loanColor) {
                                Row(modifier = Modifier.fillMaxWidth().padding(LoanMasterTheme.spacing.md), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(loan.bankName, color = Color.White, fontWeight = FontWeight.Bold)
                                        Text("Processing Fee: ${com.loanmaster.pro.core.formatter.formatMoney(loan.processingFee)}")
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("Stated: ${loan.interestRate}%", color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize, textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough)
                                        Text("True APR: ${String.format("%.2f", apr)}%", color = AccentYellow, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.sm))
                        }
                    }
                    "Prepay Impact" -> {
                        Text("Making a small 5% extra payment every year dramatically reduces your loan tenure and interest burden.", color = TextSecondary, fontSize = LoanMasterTheme.typography.body.fontSize)
                        Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.md))
                        val best = processedLoans.minByOrNull { (localCalculateEMI(it.loanAmount, it.interestRate, it.totalMonths) * it.totalMonths) + it.processingFee }
                        if (best != null) {
                            val yearlyPrepay = best.loanAmount * 0.05
                            var currB = best.loanAmount
                            val r = (best.interestRate / 12) / 100
                            val emi = localCalculateEMI(best.loanAmount, best.interestRate, best.totalMonths)
                            var months = 0
                            var intPaid = 0.0
                            while(currB > 0 && months < 1000) {
                                intPaid += currB * r
                                var principalPaid = emi - (currB * r)
                                if (months % 12 == 0 && months > 0) principalPaid += yearlyPrepay
                                currB -= principalPaid
                                months++
                            }
                            val origInt = (emi * best.totalMonths) - best.loanAmount
                            val totalSaved = origInt - intPaid
                            val monthsSaved = best.totalMonths - months
                            
                            ResponsiveCard(bgColor = Color(0xFF0D1B36), borderColor = AccentGreen) {
                                Column(Modifier.padding(LoanMasterTheme.spacing.md)) {
                                    Text("Strategy for ${best.bankName}", color = Color.White, fontWeight = FontWeight.Bold)
                                    Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.sm))
                                    Text("Pay an extra ${com.loanmaster.pro.core.formatter.formatMoney(yearlyPrepay)} every year:")
                                    Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.md))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Column {
                                            Text("Interest Saved", color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize)
                                            Text(com.loanmaster.pro.core.formatter.formatMoney(totalSaved))
                                        }
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text("Time Saved", color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize)
                                            val y = monthsSaved / 12
                                            val m = monthsSaved % 12
                                            Text("${y}y ${m}m earlier", color = AccentBlue, fontWeight = FontWeight.Bold, fontSize = LoanMasterTheme.typography.title.fontSize)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    "Break-Even" -> {
                        Text("If you are transferring your balance from an older loan, you will pay processing fees. Here is how many months it will take to recover that fee.", color = TextSecondary, fontSize = LoanMasterTheme.typography.body.fontSize)
                        Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.md))
                        
                        val oldEmi = if (processedLoans.isNotEmpty()) localCalculateEMI(processedLoans[0].loanAmount, processedLoans[0].interestRate + 1.5, processedLoans[0].totalMonths) else 0.0
                        
                        processedLoans.forEach { loan ->
                            val loanColor = if(loan.id == "A") Color(0xFF3B82F6) else Color(0xFF10B981)
                            val newEmi = localCalculateEMI(loan.loanAmount, loan.interestRate, loan.totalMonths)
                            val monthlySavings = oldEmi - newEmi
                            val breakEvenMonths = if (monthlySavings > 0) (loan.processingFee / monthlySavings).toInt() else -1
                            
                            ResponsiveCard(bgColor = BackgroundDark, borderColor = loanColor) {
                                Row(modifier = Modifier.fillMaxWidth().padding(LoanMasterTheme.spacing.md), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(loan.bankName, color = Color.White, fontWeight = FontWeight.Bold)
                                        Text("Fee: ${com.loanmaster.pro.core.formatter.formatMoney(loan.processingFee)}")
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        if (breakEvenMonths > 0) {
                                            Text("Recovers in:", color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize)
                                            Text("$breakEvenMonths Months", color = AccentYellow, fontWeight = FontWeight.Bold)
                                        } else {
                                            Text("Math doesn't", color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize)
                                            Text("make sense", color = Color(0xFFF43F5E), fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                            Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.sm))
                        }
                        Text("*Assumes your old loan had 1.5% higher interest.", color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize, modifier = Modifier.padding(top = LoanMasterTheme.spacing.sm))
                    }
                    "Negotiator" -> {
                        Text("Banks have margins. Use these tested scripts to negotiate better terms with your loan officer before you sign.", color = TextSecondary, fontSize = LoanMasterTheme.typography.body.fontSize)
                        Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.md))
                        
                        val lowestInterestBank = processedLoans.minByOrNull { it.interestRate }
                        val lowestFeeBank = processedLoans.minByOrNull { it.processingFee }
                        
                        val scripts = mutableListOf<Pair<String, String>>()
                        
                        if (lowestInterestBank != null && processedLoans.size > 1) {
                            val otherBank = processedLoans.firstOrNull { it.id != lowestInterestBank.id && it.interestRate > lowestInterestBank.interestRate }
                            if (otherBank != null) {
                                val rateDiff = String.format("%.2f", otherBank.interestRate - lowestInterestBank.interestRate)
                                scripts.add(Pair("Match ${lowestInterestBank.bankName}'s Lower Rate", "\"I prefer banking with ${otherBank.bankName}, but ${lowestInterestBank.bankName} has pre-approved me at ${String.format("%.2f", lowestInterestBank.interestRate)}% (which is $rateDiff% lower). If you can match their rate, I will sign the agreement today.\""))
                            }
                        }
                        
                        if (lowestFeeBank != null && processedLoans.size > 1) {
                            val otherBank = processedLoans.firstOrNull { it.id != lowestFeeBank.id && it.processingFee > lowestFeeBank.processingFee }
                            if (otherBank != null) {
                                val feeDiff = com.loanmaster.pro.core.formatter.formatMoney(otherBank.processingFee - lowestFeeBank.processingFee)
                                val theirFeeStr = if (otherBank.processingFee <= 0.0) "Zero" else com.loanmaster.pro.core.formatter.formatMoney(otherBank.processingFee)
                            }
                        }
                        
                        scripts.add(Pair("Leverage Credit Score (General)", "\"My CIBIL score is excellent, indicating very low risk. Standard market rates for my credit profile are usually 0.25% to 0.5% lower than the standard rate you've quoted. What is the absolute best floor rate you can offer?\""))
                        
                        scripts.add(Pair("Reject Forced Insurance / Add-ons", "\"I noticed loan protection insurance being bundled. RBI guidelines state this is strictly optional. I already have adequate term life cover, so please restructure the sanction letter without this premium added to my principal.\""))
                        
                        scripts.add(Pair("End of Month/Quarter Tactic", "\"I am looking to close this loan by the end of this week. I know it's the end of the quarter/month and branches have targets. If you can shave off 0.15% from the rate, I will do the paperwork right now.\""))

                        scripts.forEach { (title, script) ->
                            Text(title, color = AccentBlue, fontWeight = FontWeight.Bold, fontSize = LoanMasterTheme.typography.body.fontSize)
                            Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.xs))
                            ResponsiveCard(bgColor = BackgroundDark, borderColor = CardStroke) {
                                Text(script, color = Color.White, fontSize = LoanMasterTheme.typography.label.fontSize, modifier = Modifier.padding(LoanMasterTheme.spacing.md), style = androidx.compose.ui.text.TextStyle(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic))
                            }
                            Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.md))
                        }
                    }
                }
                Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.xl))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanEditBottomSheet(
    loan: LoanOffer,
    onDismiss: () -> Unit,
    onSave: (LoanOffer) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    
    var bankName by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf(loan.bankName) }
    var loanAmount by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf(if (loan.loanAmount <= 0.0) "" else loan.loanAmount.toString().replace(".0", "")) }
    var interestRate by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf(if (loan.interestRate <= 0.0) "" else loan.interestRate.toString()) }
    var tenure by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf(if (loan.tenureYears <= 0) "" else loan.tenureYears.toString()) }
    var processingFee by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf(if (loan.processingFee <= 0.0) "" else loan.processingFee.toString().replace(".0", "")) }

    var errorMessage by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf<String?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SurfaceDark
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(LoanMasterTheme.spacing.md)
                .verticalScroll(rememberScrollState())
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(bottom = LoanMasterTheme.spacing.xl),
            verticalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.md)
        ) {
            Text("Edit Loan ${loan.id}", color = Color.White, fontSize = LoanMasterTheme.typography.title.fontSize, fontWeight = FontWeight.Bold)
            
            errorMessage?.let { msg ->
                Text(msg, color = Color(0xFFF87171), fontSize = LoanMasterTheme.typography.body.fontSize)
            }
            
            PremiumInputField(
                isNumeric = false,
                value = bankName,
                onValueChange = { bankName = it },
                label = "Bank Name",
                icon = Icons.Rounded.AccountBalance,
                iconTint = AccentBlue,
                modifier = Modifier.fillMaxWidth()
            )

            PremiumInputField(
                value = loanAmount,
                onValueChange = { loanAmount = it },
                label = "Loan Amount",
                icon = Icons.Rounded.AccountBalanceWallet,
                iconTint = AccentBlue,
                modifier = Modifier.fillMaxWidth()
            )

            Row(horizontalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.md), modifier = Modifier.fillMaxWidth()) {
                PremiumInputField(
                    value = interestRate,
                    onValueChange = { interestRate = it },
                    label = "Interest Rate (p.a.)",
                    icon = Icons.Rounded.Percent,
                    iconTint = AccentBlue,
                    modifier = Modifier.weight(1f)
                )

                PremiumInputField(
                    value = tenure,
                    onValueChange = { tenure = it },
                    label = "Tenure (Years)",
                    icon = Icons.Rounded.DateRange,
                    iconTint = AccentBlue,
                    suffix = " Yrs",
                    modifier = Modifier.weight(1f)
                )
            }

            PremiumInputField(
                value = processingFee,
                onValueChange = { processingFee = it },
                label = "Processing Fee",
                icon = Icons.Rounded.MonetizationOn,
                iconTint = AccentBlue,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    val p = loanAmount.toDoubleOrNull() ?: 0.0
                    val r = interestRate.toDoubleOrNull() ?: 0.0
                    val t = tenure.toIntOrNull() ?: 0
                    val fee = processingFee.toDoubleOrNull() ?: 0.0
                    
                    if (p <= 0.0) {
                        errorMessage = "Please enter a valid loan amount."
                        return@Button
                    }
                    if (r <= 0.0 || r > 100.0) {
                        errorMessage = "Please enter a realistic interest rate (e.g., 1-100%)."
                        return@Button
                    }
                    if (t <= 0 || t > 50) {
                        errorMessage = "Please enter a realistic tenure in years (e.g., 1-50)."
                        return@Button
                    }
                    
                    errorMessage = null
                    onSave(
                        loan.copy(
                            bankName = bankName.ifBlank { "Bank ${loan.id}" },
                            loanAmount = p,
                            interestRate = r,
                            tenureYears = t,
                            processingFee = fee
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth().heightIn(min = 50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
            ) {
                Text("Save Changes", color = Color.White, fontWeight = FontWeight.Bold, fontSize = LoanMasterTheme.typography.body.fontSize)
            }
        }
    }
}

@Composable
fun LoanCard(loan: LoanOffer, onEdit: () -> Unit, modifier: Modifier = Modifier) {
    val loanColor = when(loan.id) {
        "A" -> Color(0xFF3B82F6)
        "B" -> Color(0xFF10B981)
        else -> Color.Gray
    }
    Box(modifier = modifier) {
        val strokeColor = if (loan.isBest) AccentYellow else loanColor.copy(alpha = 0.5f)
        val bgColor = if (loan.isBest) AccentYellow.copy(alpha = 0.05f) else Color(0xFF0D1B36)
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(LoanMasterTheme.spacing.md))
                .background(bgColor)
                .border(if (loan.isBest) LoanMasterTheme.spacing.xs else 1.dp, strokeColor, RoundedCornerShape(LoanMasterTheme.spacing.md))
                .clickable { onEdit() }
                .padding(LoanMasterTheme.spacing.md),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(LoanMasterTheme.spacing.lg)
                            .clip(androidx.compose.foundation.shape.CircleShape)
                            .background(loanColor.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.AccountBalance, contentDescription = "Bank", tint = loanColor, modifier = Modifier.size(LoanMasterTheme.spacing.md))
                    }
                    Spacer(Modifier.widthIn(min = LoanMasterTheme.spacing.sm))
                    Text(loan.bankName, color = Color.White, fontSize = LoanMasterTheme.typography.body.fontSize, fontWeight = FontWeight.Bold, minLines = 1, maxLines = 2, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                }
                Icon(Icons.Rounded.Edit, contentDescription = "Edit", tint = TextSecondary.copy(alpha=0.5f), modifier = Modifier.size(LoanMasterTheme.spacing.md))
            }
            
            Column {
                Text(com.loanmaster.pro.core.formatter.formatMoney(loan.loanAmount), color = Color.White, fontSize = LoanMasterTheme.typography.title.fontSize, fontWeight = FontWeight.ExtraBold)
                Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.xs))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("${loan.interestRate}%", color = AccentYellow, fontSize = LoanMasterTheme.typography.label.fontSize, fontWeight = FontWeight.Bold)
                    Text(" • ${loan.tenureYears} Yrs", color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize)
                }
            }
        }
        
        if (loan.isBest) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .clip(RoundedCornerShape(bottomStart = LoanMasterTheme.spacing.md, topEnd = LoanMasterTheme.spacing.md))
                    .background(AccentYellow)
                    .padding(horizontal = LoanMasterTheme.spacing.gridGutter, vertical = LoanMasterTheme.spacing.xs)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Star, contentDescription = null, tint = Color.Black, modifier = Modifier.size(LoanMasterTheme.spacing.md))
                    Spacer(Modifier.widthIn(min = LoanMasterTheme.spacing.xs))
                    Text("BEST DEAL", color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 0.5.sp)
                }
            }
        }
    }
}

@Composable
fun ComparisonTable(loans: List<LoanOffer>) {
    ResponsiveCard(
        bgColor = Color(0xFF061633),
        borderColor = CardStroke
    ) {
        if (loans.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(LoanMasterTheme.spacing.xl),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Add loans to start comparing",
                    color = TextSecondary,
                    fontSize = LoanMasterTheme.typography.body.fontSize
                )
            }
        } else {
            Column {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth().background(Color(0xFF020B1F)).padding(horizontal = LoanMasterTheme.spacing.md, vertical = LoanMasterTheme.spacing.md),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Comparison", color = Color.White, fontSize = LoanMasterTheme.typography.body.fontSize, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.5f), minLines = 1, maxLines = 2, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                    loans.forEach { loan ->
                        val loanColor = if(loan.id == "A") Color(0xFF3B82F6) else Color(0xFF10B981)
                        Text(loan.bankName, color = loanColor, fontSize = LoanMasterTheme.typography.label.fontSize, fontWeight = FontWeight.ExtraBold, modifier = Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.Center, minLines = 1, maxLines = 2, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                    }
                }
                HorizontalDivider(color = CardStroke)

                val rows = listOf(
                    Triple(Icons.Rounded.Percent, "Interest Rate") { l: LoanOffer -> "${l.interestRate}%" },
                    Triple(Icons.Rounded.CalendarToday, "Tenure") { l: LoanOffer -> 
                        if (l.tenureYears > 0 && l.tenureMonths > 0) "${l.tenureYears} Yrs ${l.tenureMonths} Mo"
                        else if (l.tenureYears > 0) "${l.tenureYears} Yrs"
                        else "${l.tenureMonths} Mo"
                    },
                    Triple(Icons.Rounded.AccountBalanceWallet, "Monthly EMI") { l: LoanOffer -> com.loanmaster.pro.core.formatter.formatMoney(localCalculateEMI(l.loanAmount, l.interestRate, l.totalMonths)) },
                    Triple(Icons.Rounded.AccountBalanceWallet, "Total Interest") { l: LoanOffer -> com.loanmaster.pro.core.formatter.formatMoney(localCalculateTotalInterest(l.loanAmount, l.interestRate, l.totalMonths)) },
                    Triple(Icons.Rounded.AccountBalanceWallet, "Total Payment") { l: LoanOffer -> com.loanmaster.pro.core.formatter.formatMoney(l.loanAmount + localCalculateTotalInterest(l.loanAmount, l.interestRate, l.totalMonths)) },
                    Triple(Icons.Rounded.Description, "Processing Fee") { l: LoanOffer -> com.loanmaster.pro.core.formatter.formatMoney(l.processingFee) },
                    Triple(Icons.Rounded.Settings, "Effective APR") { l: LoanOffer -> "${l.interestRate + 0.22}%" }
                )

                rows.forEachIndexed { index, data ->
                    val (icon, title, selector) = data
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(if (index % 2 == 0) Color.Transparent else Color(0xFF0D1B36))
                            .padding(horizontal = LoanMasterTheme.spacing.md, vertical = LoanMasterTheme.spacing.md),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(modifier = Modifier.weight(1.5f), verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(LoanMasterTheme.spacing.lg).clip(androidx.compose.foundation.shape.CircleShape).background(Color(0xFF1E293B)), contentAlignment = Alignment.Center) {
                                Icon(icon, contentDescription = title, tint = AccentBlue, modifier = Modifier.size(LoanMasterTheme.spacing.md))
                            }
                            Spacer(Modifier.widthIn(min = LoanMasterTheme.spacing.gridGutter))
                            Text(title, color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize, fontWeight = FontWeight.Medium, minLines = 1, maxLines = 2, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                            if (title == "Effective APR") {
                                Spacer(Modifier.widthIn(min = LoanMasterTheme.spacing.xs))
                                Icon(Icons.Rounded.Info, contentDescription = "Info", tint = TextSecondary, modifier = Modifier.size(LoanMasterTheme.spacing.md))
                            }
                        }
                        loans.forEach { loan ->
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.weight(1f)) {
                                AutoResizedText(text = selector(loan), color = if (loan.isBest) AccentYellow else Color.White, fontSize = LoanMasterTheme.typography.label.fontSize, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LoanAdvisorSection(loans: List<LoanOffer>, bestLoan: LoanOffer?, isPremiumUnlocked: Boolean = false, onUnlockRequested: () -> Unit = {}) {
    val symbol = com.loanmaster.pro.LocalCurrencySymbol.current
    val advice = remember(loans, bestLoan, symbol) { generateFinancialAdvice(loans, bestLoan, symbol) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(LoanMasterTheme.spacing.md))
                .background(androidx.compose.ui.graphics.Brush.linearGradient(listOf(Color(0xFF0F172A), Color(0xFF1E293B))))
                .border(1.dp, AccentBlue.copy(alpha=0.3f), RoundedCornerShape(LoanMasterTheme.spacing.md))
                .padding(LoanMasterTheme.spacing.md)
        ) {
            Column {
                // Badge
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(LoanMasterTheme.spacing.sm))
                        .background(AccentBlue.copy(alpha = 0.2f))
                        .padding(horizontal = LoanMasterTheme.spacing.sm, vertical = LoanMasterTheme.spacing.xs),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Rounded.AutoAwesome, contentDescription = null, tint = AccentBlue, modifier = Modifier.size(LoanMasterTheme.spacing.md))
                    Spacer(Modifier.widthIn(min = LoanMasterTheme.spacing.sm))
                    Text("AI ADVISOR", color = AccentBlue, fontSize = LoanMasterTheme.typography.label.fontSize, fontWeight = FontWeight.ExtraBold, letterSpacing = 0.5.sp)
                }
                Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.md))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Lightbulb
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(LoanMasterTheme.components.iconLarge).clip(androidx.compose.foundation.shape.CircleShape).background(AccentYellow.copy(alpha=0.1f))) {
                        Icon(Icons.Rounded.Lightbulb, contentDescription = null, tint = AccentYellow, modifier = Modifier.size(LoanMasterTheme.spacing.lg))
                    }
                    Spacer(Modifier.widthIn(min = LoanMasterTheme.spacing.md))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(advice.title, color = Color.White, fontSize = LoanMasterTheme.typography.body.fontSize, fontWeight = FontWeight.Bold, lineHeight = LoanMasterTheme.typography.title.fontSize)
                        Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.sm))
                        Text(advice.subtitle, color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize, lineHeight = LoanMasterTheme.typography.title.fontSize)
                    }
                    if (advice.showPremium) {
                        Spacer(Modifier.widthIn(min = LoanMasterTheme.spacing.md))
                        // Premium box
                        Column(
                            modifier = Modifier
                                .clip(RoundedCornerShape(LoanMasterTheme.spacing.md))
                                .background(Color(0xFF061633))
                                .border(1.dp, CardStroke, RoundedCornerShape(LoanMasterTheme.spacing.md))
                                .padding(LoanMasterTheme.spacing.md),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Potential Savings", color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize, fontWeight = FontWeight.Medium)
                            Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.xs))
                            Text(advice.savingsText, color = AccentGreen, fontSize = LoanMasterTheme.typography.title.fontSize, fontWeight = FontWeight.ExtraBold)
                            Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.sm))
                            if (!isPremiumUnlocked) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clip(RoundedCornerShape(LoanMasterTheme.spacing.xs)).background(AccentYellow.copy(alpha=0.2f)).padding(horizontal = LoanMasterTheme.spacing.sm, vertical = LoanMasterTheme.spacing.xs)) {
                                    Icon(Icons.Rounded.Lock, contentDescription = null, tint = AccentYellow, modifier = Modifier.size(LoanMasterTheme.spacing.gridGutter))
                                    Spacer(Modifier.widthIn(min = LoanMasterTheme.spacing.xs))
                                    Text("PRO", color = AccentYellow, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = AccentGreen, modifier = Modifier.size(LoanMasterTheme.spacing.md))
                                    Spacer(Modifier.widthIn(min = LoanMasterTheme.spacing.xs))
                                    Text("Unlocked", color = AccentGreen, fontSize = LoanMasterTheme.typography.label.fontSize, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
        
        Spacer(Modifier.heightIn(min = LoanMasterTheme.components.iconSmall))
        
        if (!isPremiumUnlocked) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                OutlinedButton(
                    onClick = { onUnlockRequested() }, 
                    border = BorderStroke(1.dp, AccentYellow), 
                    shape = RoundedCornerShape(LoanMasterTheme.spacing.md),
                    modifier = Modifier.weight(1f).heightIn(min = 50.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentYellow)
                ) {
                    Icon(Icons.Rounded.SmartDisplay, contentDescription = "Ad", modifier = Modifier.size(LoanMasterTheme.components.iconSmall))
                    Spacer(Modifier.widthIn(min = LoanMasterTheme.spacing.sm))
                    Text("Watch Ad", fontWeight = FontWeight.Bold)
                }
                Text("  or  ", color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize, modifier = Modifier.padding(horizontal = LoanMasterTheme.spacing.sm))
                Button(
                    onClick = { onUnlockRequested() }, 
                    shape = RoundedCornerShape(LoanMasterTheme.spacing.md),
                    modifier = Modifier.weight(1.2f).heightIn(min = 50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentYellow, contentColor = Color.Black)
                ) {
                    Icon(Icons.Rounded.WorkspacePremium, contentDescription = "Premium", modifier = Modifier.size(LoanMasterTheme.components.iconSmall))
                    Spacer(Modifier.widthIn(min = LoanMasterTheme.spacing.sm))
                    Text("Go Premium", fontWeight = FontWeight.ExtraBold)
                }
            }
            
            Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.md))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.LockOpen, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(LoanMasterTheme.spacing.md))
                Spacer(Modifier.widthIn(min = LoanMasterTheme.spacing.sm))
                Text("One-time unlock • All insights included", color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize, fontWeight = FontWeight.Medium)
            }
        }
    }
}

data class FinancialAdvice(
    val title: String,
    val subtitle: String,
    val savingsText: String,
    val showPremium: Boolean = false
)

private fun generateFinancialAdvice(loans: List<LoanOffer>, bestLoan: LoanOffer?, symbol: String): FinancialAdvice {
    if (loans.isEmpty()) {
        return FinancialAdvice(
            title = "No loans available.",
            subtitle = "Please add at least one loan to get insights.",
            savingsText = "${symbol}0",
            showPremium = false
        )
    }
    
    val base = loans.first()
    val allSame = loans.size > 1 && loans.all { 
        it.loanAmount == base.loanAmount && 
        it.interestRate == base.interestRate && 
        it.tenureYears == base.tenureYears && 
        it.processingFee == base.processingFee 
    }
    
    if (allSame) {
        val emi = localCalculateEMI(base.loanAmount, base.interestRate, base.totalMonths)
        val optimalEmi = localCalculateEMI(base.loanAmount, maxOf(0.0, base.interestRate - 0.5), base.totalMonths)
        val estimatedSavings = ((emi - optimalEmi) * base.totalMonths)
        return FinancialAdvice(
            title = "All loans are equal.",
            subtitle = "The loan offers have identical terms. You can choose any, or negotiate a 0.5% rate drop to save more.",
            savingsText = com.loanmaster.pro.core.formatter.formatMoney(estimatedSavings))
    }
    
    if (loans.size == 1) {
        val loan = loans.first()
        val emi = localCalculateEMI(loan.loanAmount, loan.interestRate, loan.totalMonths)
        val optimalEmi = localCalculateEMI(loan.loanAmount, maxOf(0.0, loan.interestRate - 1.0), loan.totalMonths)
        val estimatedSavings = ((emi - optimalEmi) * loan.totalMonths)
        
        return FinancialAdvice(
            title = "Analyzing Loan ${loan.id}...",
            subtitle = "Even with one loan, we found a smarter way to negotiate a better deal and save more.",
            savingsText = com.loanmaster.pro.core.formatter.formatMoney(estimatedSavings))
    }

    val normalizedCosts = loans.map {
        val loanAmountSafe = if (it.loanAmount > 0.0) it.loanAmount else 1.0
        val emi = localCalculateEMI(loanAmountSafe, it.interestRate, it.totalMonths)
        val totalPayment = (emi * it.totalMonths) + it.processingFee
        val totalCostPer1L = (totalPayment / loanAmountSafe) * 100000.0
        
        val actualTotalPayment = (localCalculateEMI(it.loanAmount, it.interestRate, it.totalMonths) * it.totalMonths) + it.processingFee
        Triple(it, totalCostPer1L, actualTotalPayment)
    }
    
    val minCostPer1L = normalizedCosts.minOfOrNull { it.second } ?: 0.0
    val maxCostPer1L = normalizedCosts.maxOfOrNull { it.second } ?: 0.0
    
    val bestRef = bestLoan ?: normalizedCosts.minByOrNull { it.second }?.first ?: loans.first()
    val actualDifference = (normalizedCosts.maxOfOrNull { it.third } ?: 0.0) - (normalizedCosts.minOfOrNull { it.third } ?: 0.0)
    
    // Determine why it's the best comparing against others
    val others = loans.filter { it.id != bestRef.id }
    val reason = if (others.isNotEmpty()) {
        val hasLowestInterest = others.all { it.interestRate > bestRef.interestRate }
        val hasLowestFee = others.all { it.processingFee > bestRef.processingFee }
        
        when {
            hasLowestInterest && hasLowestFee -> "due to the lowest interest rate and processing fees"
            hasLowestInterest -> "due to the lowest interest rate"
            hasLowestFee -> "due to the lowest processing fees"
            else -> "due to the lowest effective overall cost"
        }
    } else {
        "as it is the only option"
    }

    val bestEmi = localCalculateEMI(bestRef.loanAmount, bestRef.interestRate, bestRef.totalMonths)
    val optimalEmi = localCalculateEMI(bestRef.loanAmount, maxOf(0.0, bestRef.interestRate - 0.5), bestRef.totalMonths)
    val extraPremiumSavings = ((bestEmi - optimalEmi) * bestRef.totalMonths)
    val totalEstimatedSavings = Math.abs(actualDifference) + extraPremiumSavings

    val loanName = bestLoan?.let { "Loan ${it.id}" } ?: "This loan"
    
    return FinancialAdvice(
        title = "$loanName is the best choice.",
        subtitle = "This loan is best $reason. We also found strategies to help you save extra.",
        savingsText = com.loanmaster.pro.core.formatter.formatMoney(totalEstimatedSavings))
}

@Composable
fun WhatYouWillUnlockSection(isPremiumUnlocked: Boolean, onToolClick: (String) -> Unit) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text(if (isPremiumUnlocked) "Premium Tools (Unlocked)" else "What you'll unlock", color = Color.White, fontSize = LoanMasterTheme.typography.body.fontSize, fontWeight = FontWeight.Bold)
            Spacer(Modifier.widthIn(min = LoanMasterTheme.spacing.md))
            HorizontalDivider(modifier = Modifier.weight(1f), color = CardStroke)
        }
        Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.md))
        
        val items = listOf(
            Triple(Icons.Rounded.AccountBalance, "True Cost (APR)", "Evaluate real cost after processing fees"),
            Triple(Icons.AutoMirrored.Rounded.TrendingDown, "Prepay Impact", "See how 5% extra payments affect outcome"),
            Triple(Icons.AutoMirrored.Rounded.CompareArrows, "Break-Even", "Find when a balance transfer is profitable"),
            Triple(Icons.Rounded.Handshake, "Negotiator", "Scripts & tactics to lower your rates")
        )
        
        Column(verticalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.md)) {
            val chunks = items.chunked(2)
            chunks.forEach { rowItems ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.md)) {
                    rowItems.forEach { (icon, title, desc) ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(LoanMasterTheme.spacing.md))
                                .background(if (isPremiumUnlocked) Color(0xFF061633) else Color(0xFF0D1B36))
                                .border(1.dp, if (isPremiumUnlocked) AccentGreen.copy(alpha=0.5f) else CardStroke, RoundedCornerShape(LoanMasterTheme.spacing.md))
                                .clickable { if (isPremiumUnlocked) onToolClick(title) }
                                .padding(LoanMasterTheme.spacing.md)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.Start
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(LoanMasterTheme.components.iconLarge).clip(androidx.compose.foundation.shape.CircleShape).background(AccentBlue.copy(alpha=0.1f)), contentAlignment = Alignment.Center) {
                                        Icon(icon, contentDescription = null, tint = AccentBlue, modifier = Modifier.size(LoanMasterTheme.components.iconSmall))
                                    }
                                    Spacer(Modifier.weight(1f))
                                    if (isPremiumUnlocked) {
                                        Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = AccentGreen, modifier = Modifier.size(LoanMasterTheme.spacing.md))
                                    } else {
                                        Icon(Icons.Rounded.Lock, contentDescription = null, tint = AccentYellow, modifier = Modifier.size(LoanMasterTheme.spacing.md))
                                    }
                                }
                                Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.md))
                                Text(title, color = Color.White, fontSize = LoanMasterTheme.typography.body.fontSize, fontWeight = FontWeight.Bold, minLines = 1, maxLines = 2, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                                Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.xs))
                                Text(desc, color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize, lineHeight = LoanMasterTheme.typography.body.fontSize, maxLines = 2, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyStateIllustration() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(LoanMasterTheme.spacing.lg))
            .background(Color(0xFF061633))
            .border(1.dp, CardStroke, RoundedCornerShape(LoanMasterTheme.spacing.lg))
            .padding(vertical = LoanMasterTheme.components.iconLarge, horizontal = LoanMasterTheme.spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(LoanMasterTheme.components.calculatorCardHeight)
                .background(AccentBlue.copy(alpha=0.1f), shape = androidx.compose.foundation.shape.CircleShape)
                .border(1.dp, AccentBlue.copy(alpha=0.3f), androidx.compose.foundation.shape.CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Rounded.AccountBalance,
                contentDescription = null,
                tint = AccentBlue,
                modifier = Modifier.size(LoanMasterTheme.components.iconLarge)
            )
        }
        Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.lg))
        Text(
            text = "No Loans Added Yet",
            color = Color.White,
            fontSize = LoanMasterTheme.typography.title.fontSize,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.sm))
        Text(
            text = "Add loans above to start comparing EMI, total interest, and effective APR.",
            color = TextSecondary,
            fontSize = LoanMasterTheme.typography.body.fontSize,
            lineHeight = LoanMasterTheme.typography.title.fontSize,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}


private fun localCalculateEMI(principal: Double, rate: Double, totalMonths: Int): Double {
    if (principal <= 0 || rate <= 0 || totalMonths <= 0) return 0.0
    val r = rate / 12 / 100
    val n = totalMonths
    return principal * r * (1 + r).pow(n) / ((1 + r).pow(n) - 1)
}

private fun localCalculateTotalInterest(principal: Double, rate: Double, totalMonths: Int): Double {
    val emi = localCalculateEMI(principal, rate, totalMonths)
    val totalPayment = emi * totalMonths
    return totalPayment - principal
}
