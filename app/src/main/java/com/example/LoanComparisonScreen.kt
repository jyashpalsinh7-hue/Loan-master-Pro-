package com.example

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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.CardStroke
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentYellow
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlin.math.pow
import java.text.NumberFormat
import java.util.Locale

data class LoanOffer(
    val id: String,
    val bankName: String,
    val interestRate: Double,
    val tenureYears: Int,
    val tenureMonths: Int = 0,
    val loanAmount: Double,
    val processingFee: Double = 0.0,
    val prepaymentCharges: Double = 0.0,
    val color: Color,
    val isBest: Boolean = false
) {
    val totalMonths: Int get() = tenureYears * 12 + tenureMonths
}

class LoanInputState(
    initialAmount: String = "",
    initialInterest: String = "",
    initialYears: String = "",
    initialMonths: String = ""
) {
    var amount by mutableStateOf(initialAmount)
    var interest by mutableStateOf(initialInterest)
    var years by mutableStateOf(initialYears)
    var months by mutableStateOf(initialMonths)
    
    fun toLoanOffer(id: String, bankName: String, color: Color): LoanOffer {
        return LoanOffer(
            id = id,
            bankName = bankName,
            interestRate = interest.toDoubleOrNull() ?: 0.0,
            tenureYears = years.toIntOrNull() ?: 0,
            tenureMonths = months.toIntOrNull() ?: 0,
            loanAmount = amount.toDoubleOrNull() ?: 0.0,
            processingFee = 0.0,
            prepaymentCharges = 0.0,
            color = color
        )
    }
    
    fun clear() {
        amount = ""
        interest = ""
        years = ""
        months = ""
    }
}

@Composable
fun SideBySideLoanInputs(
    loanAState: LoanInputState,
    loanBState: LoanInputState,
    onCompareClick: () -> Unit,
    onResetClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFF061633))
            .border(1.dp, CardStroke, RoundedCornerShape(24.dp))
            .padding(20.dp)
    ) {
        Column {
            Row(modifier = Modifier.fillMaxWidth().height(androidx.compose.foundation.layout.IntrinsicSize.Min)) {
                LoanInputColumn(
                    title = "Loan A",
                    icon = Icons.Rounded.AccountBalance,
                    iconTint = Color(0xFF3B82F6),
                    state = loanAState,
                    modifier = Modifier.weight(1f)
                )
                
                Box(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .width(1.dp)
                        .fillMaxHeight()
                        .background(CardStroke)
                )
                
                LoanInputColumn(
                    title = "Loan B",
                    icon = Icons.Rounded.AccountBalance,
                    iconTint = Color(0xFF10B981),
                    state = loanBState,
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Button(
                    onClick = onCompareClick,
                    modifier = Modifier.weight(1f).height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentBlue, contentColor = Color.White)
                ) {
                    Icon(Icons.Rounded.CompareArrows, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Compare Loans", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(12.dp))
                IconButton(
                    onClick = onResetClick,
                    modifier = Modifier
                        .size(56.dp)
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
    state: LoanInputState,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text(title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(20.dp))
        
        PremiumInputField(
            value = state.amount,
            onValueChange = { state.amount = it },
            label = "Amount",
            icon = Icons.Rounded.CurrencyRupee,
            iconTint = iconTint,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(Modifier.height(16.dp))
        PremiumInputField(
            value = state.interest,
            onValueChange = { state.interest = it },
            label = "Interest Rate (p.a.)",
            icon = Icons.Rounded.Percent,
            iconTint = iconTint,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(Modifier.height(16.dp))
        Text("Tenure", color = TextSecondary, fontSize = 12.sp)
        Spacer(Modifier.height(6.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            androidx.compose.material3.Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF0D1B36), // SurfaceDark equivalent
                border = androidx.compose.foundation.BorderStroke(1.dp, CardStroke),
                modifier = Modifier.weight(1f).height(48.dp)
            ) {
                Row(modifier = Modifier.padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    androidx.compose.foundation.text.BasicTextField(
                        value = state.years,
                        onValueChange = { state.years = it.filter { c -> c.isDigit() } },
                        textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 13.sp),
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        cursorBrush = androidx.compose.ui.graphics.SolidColor(AccentBlue)
                    ) { inner ->
                        Box {
                            if (state.years.isEmpty()) Text("Years", color = TextSecondary, fontSize = 12.sp, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                            inner()
                        }
                    }
                }
            }
            androidx.compose.material3.Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF0D1B36),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardStroke),
                modifier = Modifier.weight(1f).height(48.dp)
            ) {
                Row(modifier = Modifier.padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    androidx.compose.foundation.text.BasicTextField(
                        value = state.months,
                        onValueChange = { state.months = it.filter { c -> c.isDigit() } },
                        textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 13.sp),
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        cursorBrush = androidx.compose.ui.graphics.SolidColor(AccentBlue)
                    ) { inner ->
                        Box {
                            if (state.months.isEmpty()) Text("Months", color = TextSecondary, fontSize = 12.sp, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
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
fun LoanComparisonScreen(onNavigateBack: () -> Unit) {
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val sizeClass = when {
        configuration.screenWidthDp < 600 -> WindowWidthSizeClass.Compact
        configuration.screenWidthDp < 840 -> WindowWidthSizeClass.Medium
        else -> WindowWidthSizeClass.Expanded
    }

    val bgColor = ResponsiveUtils.BgColor
    val textColor = ResponsiveUtils.TextPrimary

    val loanAState = remember { LoanInputState("1000000", "8.5", "5", "0") }
    val loanBState = remember { LoanInputState("1000000", "8.0", "5", "0") }
    var showResults by remember { mutableStateOf(false) }

    val currentLoans = remember(
        loanAState.amount, loanAState.interest, loanAState.years, loanAState.months,
        loanBState.amount, loanBState.interest, loanBState.years, loanBState.months
    ) {
        listOf(
            loanAState.toLoanOffer("A", "Loan A", Color(0xFF3B82F6)),
            loanBState.toLoanOffer("B", "Loan B", Color(0xFF10B981))
        )
    }

    val processedLoans = remember(currentLoans, showResults) {
        if (!showResults) emptyList()
        else {
            val mapped = currentLoans.map { loan ->
                val loanAmountSafe = if (loan.loanAmount > 0.0) loan.loanAmount else 1.0
                val emi = localCalculateEMI(loanAmountSafe, loan.interestRate, loan.totalMonths)
                val totalPayment = (emi * loan.totalMonths) + loan.processingFee
                val totalCostPer1L = (totalPayment / loanAmountSafe) * 100000.0
                loan to totalCostPer1L
            }
            
            val validMapped = mapped.filter { it.first.loanAmount > 0.0 && it.first.interestRate > 0.0 && it.first.totalMonths > 0 }
            val minCost = validMapped.minOfOrNull { it.second }
            val bestLoanIds = if (minCost != null) {
                validMapped.filter { Math.abs(it.second - minCost) < 1.0 }.map { it.first.id }.toSet()
            } else emptySet()
            
            currentLoans.map { it.copy(isBest = it.id in bestLoanIds) }
        }
    }

    var showUnlockDialog by remember { mutableStateOf(false) }
    var isPremiumUnlocked by remember { mutableStateOf(false) }
    var selectedPremiumTool by remember { mutableStateOf<String?>(null) }
    val bestLoan = processedLoans.find { it.isBest }

    Scaffold(
        containerColor = bgColor,
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("Compare Loans", color = textColor, fontWeight = FontWeight.Bold)
                        Text("Compare up to 3 loan options side by side", color = TextSecondary, fontSize = 12.sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back", tint = textColor)
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Rounded.Info, contentDescription = "Info", tint = textColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = bgColor)
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            var emiTrigger = 0.0
            if (processedLoans.isNotEmpty()) {
                emiTrigger = processedLoans.sumOf { localCalculateEMI(it.loanAmount, it.interestRate, it.totalMonths) }
            }
            ResponsiveScreenWrapper(
                widthSizeClass = sizeClass,
                animationTriggerState = emiTrigger,
                headerSection = { },
                inputControlsSection = {
                    SideBySideLoanInputs(
                        loanAState = loanAState,
                        loanBState = loanBState,
                        onCompareClick = { showResults = true },
                        onResetClick = {
                            loanAState.clear()
                            loanBState.clear()
                            showResults = false
                        }
                    )
                },
                resultsSection = {
                    Column(verticalArrangement = Arrangement.spacedBy(ResponsiveUtils.cardSpacing(sizeClass))) {
                        val isAnyLoanAdded = processedLoans.any { it.loanAmount > 0.0 || it.interestRate > 0.0 || it.totalMonths > 0 }
                        if (!isAnyLoanAdded) {
                            EmptyStateIllustration()
                        } else {
                            ComparisonTable(loans = processedLoans)
                            LoanAdvisorSection(
                                loans = processedLoans,
                                bestLoan = bestLoan,
                                isPremiumUnlocked = isPremiumUnlocked,
                                onUnlockRequested = { isPremiumUnlocked = true }
                            )
                            WhatYouWillUnlockSection(
                                isPremiumUnlocked = isPremiumUnlocked,
                                onToolClick = { tool -> selectedPremiumTool = tool }
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Button(
                                    onClick = { }, 
                                    modifier = Modifier.weight(1f).height(64.dp), 
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF061633), contentColor = AccentBlue), 
                                    shape = RoundedCornerShape(16.dp),
                                    border = BorderStroke(1.dp, CardStroke)
                                ) { 
                                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                                        Icon(Icons.Rounded.BookmarkBorder, contentDescription = null, modifier = Modifier.size(20.dp))
                                        Spacer(Modifier.height(4.dp))
                                        Text("Save", fontSize = 12.sp, fontWeight = FontWeight.Bold) 
                                    }
                                }
                                Button(
                                    onClick = { }, 
                                    modifier = Modifier.weight(1f).height(64.dp), 
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF061633), contentColor = AccentYellow), 
                                    shape = RoundedCornerShape(16.dp),
                                    border = BorderStroke(1.dp, CardStroke)
                                ) { 
                                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                                        Icon(Icons.Rounded.Share, contentDescription = null, modifier = Modifier.size(20.dp))
                                        Spacer(Modifier.height(4.dp))
                                        Text("Share", fontSize = 12.sp, fontWeight = FontWeight.Bold) 
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
                                            reportData.add("Amount" to localFormatMoney(loan.loanAmount, com.example.globalCurrencySymbol))
                                            reportData.add("Rate" to "${loan.interestRate}%")
                                            reportData.add("Tenure" to "${loan.totalMonths} months")
                                            reportData.add("Monthly EMI" to localFormatMoney(emi, com.example.globalCurrencySymbol))
                                            reportData.add("Total Interest" to localFormatMoney(totalInterest, com.example.globalCurrencySymbol))
                                            reportData.add("Total Payment" to localFormatMoney(totalPayment, com.example.globalCurrencySymbol))
                                            reportData.add("" to "")
                                        }
                                        ExportUtils.exportToPdf(context, "Loan Comparison Report", reportData)
                                    }, 
                                    modifier = Modifier.weight(1f).height(64.dp), 
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF061633), contentColor = AccentGreen), 
                                    shape = RoundedCornerShape(16.dp),
                                    border = BorderStroke(1.dp, CardStroke)
                                ) { 
                                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                                        Icon(Icons.Rounded.PictureAsPdf, contentDescription = null, modifier = Modifier.size(20.dp))
                                        Spacer(Modifier.height(4.dp))
                                        Text("PDF", fontSize = 12.sp, fontWeight = FontWeight.Bold) 
                                    }
                                }
                            }
                        }
                    }
                }
            )
        }
    }

    if (showUnlockDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showUnlockDialog = false },
            containerColor = SurfaceDark,
            titleContentColor = Color.White,
            textContentColor = TextSecondary,
            title = {
                Text("Unlock Premium", fontWeight = FontWeight.Bold)
            },
            text = {
                Text("Comparing more than 3 loans is a premium feature. Watch a short ad or upgrade to Premium to unlock unlimited comparisons!")
            },
            confirmButton = {
                Button(
                    onClick = {
                        isPremiumUnlocked = true
                        showUnlockDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentYellow, contentColor = Color.Black)
                ) {
                    Text("Watch Ad / Go Premium", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showUnlockDialog = false }) {
                    Text("Cancel", color = Color.White)
                }
            }
        )
    }

    if (selectedPremiumTool != null) {
        ModalBottomSheet(
            onDismissRequest = { selectedPremiumTool = null },
            containerColor = SurfaceDark
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Text(selectedPremiumTool ?: "", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(16.dp))
                when (selectedPremiumTool) {
                    "True Cost (APR)" -> {
                        Text("Banks often advertise a low interest rate, but hide costs in processing fees. Here is the 'True Cost' (Effective APR) of your loans.", color = TextSecondary, fontSize = 14.sp)
                        Spacer(Modifier.height(16.dp))
                        processedLoans.forEach { loan ->
                            val emi = localCalculateEMI(loan.loanAmount, loan.interestRate, loan.tenureYears)
                            val totalPayment = (emi * loan.tenureYears * 12) + loan.processingFee
                            val totalInterestAndFees = totalPayment - loan.loanAmount
                            // Rough APR Approximation (Internal Rate of Return is hard to calculate without a loop, so we use a simplified formula)
                            // APR ≈ (24 * Total Finance Charges) / (Principal * (Total Payments + 1))
                            val financeCharges = totalInterestAndFees
                            val totalMonths = loan.tenureYears * 12
                            val apr = (24 * financeCharges) / (loan.loanAmount * (totalMonths + 1)) * 100
                            
                            ResponsiveCard(bgColor = BackgroundDark, borderColor = loan.color) {
                                Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(loan.bankName, color = Color.White, fontWeight = FontWeight.Bold)
                                        Text("Processing Fee: ${localFormatMoney(loan.processingFee)}", color = TextSecondary, fontSize = 12.sp)
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("Stated: ${loan.interestRate}%", color = TextSecondary, fontSize = 12.sp, textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough)
                                        Text("True APR: ${String.format("%.2f", apr)}%", color = AccentYellow, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                    "Prepay Impact" -> {
                        Text("Making a small 5% extra payment every year dramatically reduces your loan tenure and interest burden.", color = TextSecondary, fontSize = 14.sp)
                        Spacer(Modifier.height(16.dp))
                        val best = processedLoans.minByOrNull { (localCalculateEMI(it.loanAmount, it.interestRate, it.tenureYears) * it.tenureYears * 12) + it.processingFee }
                        if (best != null) {
                            val yearlyPrepay = best.loanAmount * 0.05
                            var currB = best.loanAmount
                            val r = (best.interestRate / 12) / 100
                            val emi = localCalculateEMI(best.loanAmount, best.interestRate, best.tenureYears)
                            var months = 0
                            var intPaid = 0.0
                            while(currB > 0 && months < 1000) {
                                intPaid += currB * r
                                var principalPaid = emi - (currB * r)
                                if (months % 12 == 0 && months > 0) principalPaid += yearlyPrepay
                                currB -= principalPaid
                                months++
                            }
                            val origInt = (emi * best.tenureYears * 12) - best.loanAmount
                            val totalSaved = origInt - intPaid
                            val monthsSaved = (best.tenureYears * 12) - months
                            
                            ResponsiveCard(bgColor = Color(0xFF0D1B36), borderColor = AccentGreen) {
                                Column(Modifier.padding(16.dp)) {
                                    Text("Strategy for ${best.bankName}", color = Color.White, fontWeight = FontWeight.Bold)
                                    Spacer(Modifier.height(8.dp))
                                    Text("Pay an extra ${localFormatMoney(yearlyPrepay)} every year:", color = TextSecondary, fontSize = 14.sp)
                                    Spacer(Modifier.height(12.dp))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Column {
                                            Text("Interest Saved", color = TextSecondary, fontSize = 12.sp)
                                            Text(localFormatMoney(totalSaved), color = AccentGreen, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                        }
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text("Time Saved", color = TextSecondary, fontSize = 12.sp)
                                            val y = monthsSaved / 12
                                            val m = monthsSaved % 12
                                            Text("${y}y ${m}m earlier", color = AccentBlue, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    "Break-Even" -> {
                        Text("If you are transferring your balance from an older loan, you will pay processing fees. Here is how many months it will take to recover that fee.", color = TextSecondary, fontSize = 14.sp)
                        Spacer(Modifier.height(16.dp))
                        
                        val oldEmi = if (processedLoans.isNotEmpty()) localCalculateEMI(processedLoans[0].loanAmount, processedLoans[0].interestRate + 1.5, processedLoans[0].tenureYears) else 0.0
                        
                        processedLoans.forEach { loan ->
                            val newEmi = localCalculateEMI(loan.loanAmount, loan.interestRate, loan.tenureYears)
                            val monthlySavings = oldEmi - newEmi
                            val breakEvenMonths = if (monthlySavings > 0) (loan.processingFee / monthlySavings).toInt() else -1
                            
                            ResponsiveCard(bgColor = BackgroundDark, borderColor = loan.color) {
                                Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(loan.bankName, color = Color.White, fontWeight = FontWeight.Bold)
                                        Text("Fee: ${localFormatMoney(loan.processingFee)}", color = TextSecondary, fontSize = 12.sp)
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        if (breakEvenMonths > 0) {
                                            Text("Recovers in:", color = TextSecondary, fontSize = 12.sp)
                                            Text("$breakEvenMonths Months", color = AccentYellow, fontWeight = FontWeight.Bold)
                                        } else {
                                            Text("Math doesn't", color = TextSecondary, fontSize = 12.sp)
                                            Text("make sense", color = Color(0xFFF43F5E), fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                        }
                        Text("*Assumes your old loan had 1.5% higher interest.", color = TextSecondary, fontSize = 10.sp, modifier = Modifier.padding(top = 8.dp))
                    }
                    "Negotiator" -> {
                        Text("Banks have margins. Use these tested scripts to negotiate better terms with your loan officer before you sign.", color = TextSecondary, fontSize = 14.sp)
                        Spacer(Modifier.height(16.dp))
                        
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
                                val feeDiff = localFormatMoney(otherBank.processingFee - lowestFeeBank.processingFee)
                                val theirFeeStr = if (lowestFeeBank.processingFee == 0.0) "waiving the processing fee completely" else "offering a processing fee of just ${localFormatMoney(lowestFeeBank.processingFee)}"
                                scripts.add(Pair("Reduce ${otherBank.bankName}'s Processing Fee", "\"${otherBank.bankName}, I am evaluating your offer against ${lowestFeeBank.bankName}. They are $theirFeeStr. Can you waive the $feeDiff difference so we can move forward?\""))
                            }
                        }
                        
                        scripts.add(Pair("Leverage Credit Score (General)", "\"My CIBIL score is excellent, indicating very low risk. Standard market rates for my credit profile are usually 0.25% to 0.5% lower than the standard rate you've quoted. What is the absolute best floor rate you can offer?\""))
                        
                        scripts.add(Pair("Reject Forced Insurance / Add-ons", "\"I noticed loan protection insurance being bundled. RBI guidelines state this is strictly optional. I already have adequate term life cover, so please restructure the sanction letter without this premium added to my principal.\""))
                        
                        scripts.add(Pair("End of Month/Quarter Tactic", "\"I am looking to close this loan by the end of this week. I know it's the end of the quarter/month and branches have targets. If you can shave off 0.15% from the rate, I will do the paperwork right now.\""))

                        scripts.forEach { (title, script) ->
                            Text(title, color = AccentBlue, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(Modifier.height(4.dp))
                            ResponsiveCard(bgColor = BackgroundDark, borderColor = CardStroke) {
                                Text(script, color = Color.White, fontSize = 13.sp, modifier = Modifier.padding(12.dp), style = androidx.compose.ui.text.TextStyle(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic))
                            }
                            Spacer(Modifier.height(16.dp))
                        }
                    }
                }
                Spacer(Modifier.height(32.dp))
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
    
    var bankName by remember { mutableStateOf(loan.bankName) }
    var loanAmount by remember { mutableStateOf(if (loan.loanAmount <= 0.0) "" else loan.loanAmount.toString().replace(".0", "")) }
    var interestRate by remember { mutableStateOf(if (loan.interestRate <= 0.0) "" else loan.interestRate.toString()) }
    var tenure by remember { mutableStateOf(if (loan.tenureYears <= 0) "" else loan.tenureYears.toString()) }
    var processingFee by remember { mutableStateOf(if (loan.processingFee <= 0.0) "" else loan.processingFee.toString().replace(".0", "")) }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SurfaceDark
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Edit Loan ${loan.id}", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            
            errorMessage?.let { msg ->
                Text(msg, color = Color(0xFFF87171), fontSize = 14.sp)
            }
            
            PremiumInputField(
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

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
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
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
            ) {
                Text("Save Changes", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun LoanCard(loan: LoanOffer, onEdit: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        val strokeColor = if (loan.isBest) AccentYellow else loan.color.copy(alpha = 0.5f)
        val bgColor = if (loan.isBest) AccentYellow.copy(alpha = 0.05f) else Color(0xFF0D1B36)
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(16.dp))
                .background(bgColor)
                .border(if (loan.isBest) 2.dp else 1.dp, strokeColor, RoundedCornerShape(16.dp))
                .clickable { onEdit() }
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(androidx.compose.foundation.shape.CircleShape)
                            .background(loan.color.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.AccountBalance, contentDescription = "Bank", tint = loan.color, modifier = Modifier.size(14.dp))
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(loan.bankName, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                }
                Icon(Icons.Rounded.Edit, contentDescription = "Edit", tint = TextSecondary.copy(alpha=0.5f), modifier = Modifier.size(16.dp))
            }
            
            Column {
                Text(localFormatMoney(loan.loanAmount, extractCurrencySymbol(LocalCurrency.current)), color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("${loan.interestRate}%", color = AccentYellow, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(" • ${loan.tenureYears} Yrs", color = TextSecondary, fontSize = 12.sp)
                }
            }
        }
        
        if (loan.isBest) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .clip(RoundedCornerShape(bottomStart = 12.dp, topEnd = 16.dp))
                    .background(AccentYellow)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Star, contentDescription = null, tint = Color.Black, modifier = Modifier.size(12.dp))
                    Spacer(Modifier.width(4.dp))
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
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Add loans to start comparing",
                    color = TextSecondary,
                    fontSize = 14.sp
                )
            }
        } else {
            Column {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth().background(Color(0xFF020B1F)).padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Comparison", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.5f), maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                    loans.forEach { loan ->
                        Text(loan.bankName, color = loan.color, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.Center, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
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
                    Triple(Icons.Rounded.CurrencyRupee, "Monthly EMI") { l: LoanOffer -> localFormatMoney(localCalculateEMI(l.loanAmount, l.interestRate, l.totalMonths), com.example.globalCurrencySymbol) },
                    Triple(Icons.Rounded.CurrencyRupee, "Total Interest") { l: LoanOffer -> localFormatMoney(localCalculateTotalInterest(l.loanAmount, l.interestRate, l.totalMonths), com.example.globalCurrencySymbol) },
                    Triple(Icons.Rounded.CurrencyRupee, "Total Payment") { l: LoanOffer -> localFormatMoney(l.loanAmount + localCalculateTotalInterest(l.loanAmount, l.interestRate, l.totalMonths), com.example.globalCurrencySymbol) },
                    Triple(Icons.Rounded.Description, "Processing Fee") { l: LoanOffer -> localFormatMoney(l.processingFee, com.example.globalCurrencySymbol) },
                    Triple(Icons.Rounded.Settings, "Effective APR") { l: LoanOffer -> "${l.interestRate + 0.22}%" }
                )

                rows.forEachIndexed { index, data ->
                    val (icon, title, selector) = data
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(if (index % 2 == 0) Color.Transparent else Color(0xFF0D1B36))
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(modifier = Modifier.weight(1.5f), verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(24.dp).clip(androidx.compose.foundation.shape.CircleShape).background(Color(0xFF1E293B)), contentAlignment = Alignment.Center) {
                                Icon(icon, contentDescription = title, tint = AccentBlue, modifier = Modifier.size(12.dp))
                            }
                            Spacer(Modifier.width(10.dp))
                            Text(title, color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Medium, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                            if (title == "Effective APR") {
                                Spacer(Modifier.width(4.dp))
                                Icon(Icons.Rounded.Info, contentDescription = "Info", tint = TextSecondary, modifier = Modifier.size(12.dp))
                            }
                        }
                        loans.forEach { loan ->
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.weight(1f)) {
                                AutoResizedText(text = selector(loan), color = if (loan.isBest) AccentYellow else Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
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
    val advice = remember(loans, bestLoan) { generateFinancialAdvice(loans, bestLoan) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(androidx.compose.ui.graphics.Brush.linearGradient(listOf(Color(0xFF0F172A), Color(0xFF1E293B))))
                .border(1.dp, AccentBlue.copy(alpha=0.3f), RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column {
                // Badge
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(AccentBlue.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Rounded.AutoAwesome, contentDescription = null, tint = AccentBlue, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("AI ADVISOR", color = AccentBlue, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 0.5.sp)
                }
                Spacer(Modifier.height(16.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Lightbulb
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(48.dp).clip(androidx.compose.foundation.shape.CircleShape).background(AccentYellow.copy(alpha=0.1f))) {
                        Icon(Icons.Rounded.Lightbulb, contentDescription = null, tint = AccentYellow, modifier = Modifier.size(24.dp))
                    }
                    Spacer(Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(advice.title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold, lineHeight = 20.sp)
                        Spacer(Modifier.height(6.dp))
                        Text(advice.subtitle, color = TextSecondary, fontSize = 13.sp, lineHeight = 18.sp)
                    }
                    if (advice.showPremium) {
                        Spacer(Modifier.width(16.dp))
                        // Premium box
                        Column(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF061633))
                                .border(1.dp, CardStroke, RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Potential Savings", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                            Spacer(Modifier.height(4.dp))
                            Text(advice.savingsText, color = AccentGreen, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                            Spacer(Modifier.height(6.dp))
                            if (!isPremiumUnlocked) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(AccentYellow.copy(alpha=0.2f)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                                    Icon(Icons.Rounded.Lock, contentDescription = null, tint = AccentYellow, modifier = Modifier.size(10.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("PRO", color = AccentYellow, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = AccentGreen, modifier = Modifier.size(12.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Unlocked", color = AccentGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
        
        Spacer(Modifier.height(20.dp))
        
        if (!isPremiumUnlocked) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                OutlinedButton(
                    onClick = { onUnlockRequested() }, 
                    border = BorderStroke(1.dp, AccentYellow), 
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f).height(50.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentYellow)
                ) {
                    Icon(Icons.Rounded.SmartDisplay, contentDescription = "Ad", modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Watch Ad", fontWeight = FontWeight.Bold)
                }
                Text("  or  ", color = TextSecondary, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 8.dp))
                Button(
                    onClick = { onUnlockRequested() }, 
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1.2f).height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentYellow, contentColor = Color.Black)
                ) {
                    Icon(Icons.Rounded.WorkspacePremium, contentDescription = "Premium", modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Go Premium", fontWeight = FontWeight.ExtraBold)
                }
            }
            
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.LockOpen, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(6.dp))
                Text("One-time unlock • All insights included", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

data class FinancialAdvice(
    val title: String,
    val subtitle: String,
    val savingsText: String,
    val showPremium: Boolean
)

private fun generateFinancialAdvice(loans: List<LoanOffer>, bestLoan: LoanOffer?): FinancialAdvice {
    if (loans.isEmpty()) {
        return FinancialAdvice(
            title = "No loans available.",
            subtitle = "Please add at least one loan to get insights.",
            savingsText = "${com.example.globalCurrencySymbol}0",
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
        val emi = localCalculateEMI(base.loanAmount, base.interestRate, base.tenureYears)
        val optimalEmi = localCalculateEMI(base.loanAmount, maxOf(0.0, base.interestRate - 0.5), base.tenureYears)
        val estimatedSavings = ((emi - optimalEmi) * base.tenureYears * 12)
        return FinancialAdvice(
            title = "All loans are equal.",
            subtitle = "The loan offers have identical terms. You can choose any, or negotiate a 0.5% rate drop to save more.",
            savingsText = localFormatMoney(estimatedSavings),
            showPremium = true
        )
    }
    
    if (loans.size == 1) {
        val loan = loans.first()
        val emi = localCalculateEMI(loan.loanAmount, loan.interestRate, loan.tenureYears)
        val optimalEmi = localCalculateEMI(loan.loanAmount, maxOf(0.0, loan.interestRate - 1.0), loan.tenureYears)
        val estimatedSavings = ((emi - optimalEmi) * loan.tenureYears * 12)
        
        return FinancialAdvice(
            title = "Analyzing Loan ${loan.id}...",
            subtitle = "Even with one loan, we found a smarter way to negotiate a better deal and save more.",
            savingsText = localFormatMoney(estimatedSavings),
            showPremium = true
        )
    }

    val normalizedCosts = loans.map {
        val loanAmountSafe = if (it.loanAmount > 0.0) it.loanAmount else 1.0
        val emi = localCalculateEMI(loanAmountSafe, it.interestRate, it.tenureYears)
        val totalPayment = (emi * it.tenureYears * 12) + it.processingFee
        val totalCostPer1L = (totalPayment / loanAmountSafe) * 100000.0
        
        val actualTotalPayment = (localCalculateEMI(it.loanAmount, it.interestRate, it.tenureYears) * it.tenureYears * 12) + it.processingFee
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

    val bestEmi = localCalculateEMI(bestRef.loanAmount, bestRef.interestRate, bestRef.tenureYears)
    val optimalEmi = localCalculateEMI(bestRef.loanAmount, maxOf(0.0, bestRef.interestRate - 0.5), bestRef.tenureYears)
    val extraPremiumSavings = ((bestEmi - optimalEmi) * bestRef.tenureYears * 12)
    val totalEstimatedSavings = Math.abs(actualDifference) + extraPremiumSavings

    val loanName = bestLoan?.let { "Loan ${it.id}" } ?: "This loan"
    
    return FinancialAdvice(
        title = "$loanName is the best choice.",
        subtitle = "This loan is best $reason. We also found strategies to help you save extra.",
        savingsText = localFormatMoney(totalEstimatedSavings),
        showPremium = true
    )
}

@Composable
fun WhatYouWillUnlockSection(isPremiumUnlocked: Boolean, onToolClick: (String) -> Unit) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text(if (isPremiumUnlocked) "Premium Tools (Unlocked)" else "What you'll unlock", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.width(16.dp))
            HorizontalDivider(modifier = Modifier.weight(1f), color = CardStroke)
        }
        Spacer(Modifier.height(16.dp))
        
        val items = listOf(
            Triple(Icons.Rounded.AccountBalance, "True Cost (APR)", "Evaluate real cost after processing fees"),
            Triple(Icons.Rounded.TrendingDown, "Prepay Impact", "See how 5% extra payments affect outcome"),
            Triple(Icons.Rounded.CompareArrows, "Break-Even", "Find when a balance transfer is profitable"),
            Triple(Icons.Rounded.Handshake, "Negotiator", "Scripts & tactics to lower your rates")
        )
        
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            val chunks = items.chunked(2)
            chunks.forEach { rowItems ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    rowItems.forEach { (icon, title, desc) ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isPremiumUnlocked) Color(0xFF061633) else Color(0xFF0D1B36))
                                .border(1.dp, if (isPremiumUnlocked) AccentGreen.copy(alpha=0.5f) else CardStroke, RoundedCornerShape(16.dp))
                                .clickable { if (isPremiumUnlocked) onToolClick(title) }
                                .padding(16.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.Start
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(36.dp).clip(androidx.compose.foundation.shape.CircleShape).background(AccentBlue.copy(alpha=0.1f)), contentAlignment = Alignment.Center) {
                                        Icon(icon, contentDescription = null, tint = AccentBlue, modifier = Modifier.size(20.dp))
                                    }
                                    Spacer(Modifier.weight(1f))
                                    if (isPremiumUnlocked) {
                                        Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = AccentGreen, modifier = Modifier.size(16.dp))
                                    } else {
                                        Icon(Icons.Rounded.Lock, contentDescription = null, tint = AccentYellow, modifier = Modifier.size(16.dp))
                                    }
                                }
                                Spacer(Modifier.height(12.dp))
                                Text(title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                                Spacer(Modifier.height(4.dp))
                                Text(desc, color = TextSecondary, fontSize = 11.sp, lineHeight = 16.sp, maxLines = 2, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
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
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFF061633))
            .border(1.dp, CardStroke, RoundedCornerShape(24.dp))
            .padding(vertical = 48.dp, horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(AccentBlue.copy(alpha=0.1f), shape = androidx.compose.foundation.shape.CircleShape)
                .border(1.dp, AccentBlue.copy(alpha=0.3f), androidx.compose.foundation.shape.CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Rounded.AccountBalance,
                contentDescription = null,
                tint = AccentBlue,
                modifier = Modifier.size(48.dp)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "No Loans Added Yet",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Add loans above to start comparing EMI, total interest, and effective APR.",
            color = TextSecondary,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

private fun localFormatMoney(amount: Double, currencySym: String = com.example.globalCurrencySymbol): String {
    return formatMoney(amount, com.example.globalCurrencySymbol)
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
