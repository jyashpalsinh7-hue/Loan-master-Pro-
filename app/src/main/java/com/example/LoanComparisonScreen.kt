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
    val loanAmount: Double,
    val processingFee: Double = 0.0,
    val prepaymentCharges: Double = 0.0,
    val color: Color,
    val isBest: Boolean = false
)

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

    val loansCache = remember {
        mutableStateListOf(
            LoanOffer("A", "Bank A", 0.0, 0, 0.0, 0.0, 0.0, Color(0xFF3B82F6)),
            LoanOffer("B", "Bank B", 0.0, 0, 0.0, 0.0, 0.0, Color(0xFF8B5CF6))
        )
    }

    val processedLoans = remember(loansCache.toList()) {
        val mapped = loansCache.map { loan ->
            // Instead of just relying on repayment amount, let's normalize cost to "Cost per ₹1,00,000 borrowed"
            // This is fairer if the user enters different loan amounts.
            // Also factor in processing fees.
            val loanAmountSafe = if (loan.loanAmount > 0.0) loan.loanAmount else 1.0
            val emi = localCalculateEMI(loanAmountSafe, loan.interestRate, loan.tenureYears)
            val totalPayment = (emi * loan.tenureYears * 12) + loan.processingFee
            val totalCostPer1L = (totalPayment / loanAmountSafe) * 100000.0
            
            loan to totalCostPer1L
        }
        
        val validMapped = mapped.filter { it.first.loanAmount > 0.0 && it.first.interestRate > 0.0 && it.first.tenureYears > 0 }
        
        // Find best cost
        val minCost = validMapped.minOfOrNull { it.second }
        
        // A loan is 'Best' if its cost is effectively identical to the minimum cost (within small threshold)
        val bestLoanIds = if (minCost != null) {
            validMapped.filter { Math.abs(it.second - minCost) < 1.0 }.map { it.first.id }.toSet()
        } else emptySet()
        
        loansCache.map { it.copy(isBest = it.id in bestLoanIds) }
    }

    var editingLoan by remember { mutableStateOf<LoanOffer?>(null) }
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
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(bottom = ResponsiveUtils.verticalPadding(sizeClass))
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(ResponsiveUtils.cardSpacing(sizeClass))
        ) {

            // Loan Cards Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = ResponsiveUtils.horizontalPadding(sizeClass)),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                processedLoans.forEach { loan ->
                    LoanCard(loan = loan, onEdit = { editingLoan = loan }, modifier = Modifier.width(160.dp).height(130.dp))
                }
                
                // Add Loan Card
                Box(
                    modifier = Modifier
                        .width(160.dp)
                        .height(130.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceDark.copy(alpha=0.5f))
                        .border(1.dp, CardStroke, RoundedCornerShape(12.dp))
                        .clickable {
                            if (processedLoans.size >= 3 && !isPremiumUnlocked) {
                                showUnlockDialog = true
                            } else {
                                val baseLoan = processedLoans.firstOrNull { it.loanAmount > 0.0 }
                                val p = baseLoan?.loanAmount ?: 1000000.0
                                val r = baseLoan?.interestRate ?: 8.5
                                val t = baseLoan?.tenureYears ?: 10
                                val nextId = listOf("A", "B", "C", "D", "E").firstOrNull { id -> processedLoans.none { it.id == id } } ?: "D"
                                val colors = listOf(Color(0xFF3B82F6), Color(0xFF8B5CF6), AccentYellow, Color(0xFF10B981), Color(0xFFF43F5E))
                                val colorIndex = processedLoans.size % colors.size
                                editingLoan = LoanOffer(nextId, "Bank $nextId", r, t, p, 0.0, 0.0, colors[colorIndex])
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(androidx.compose.foundation.shape.CircleShape)
                                .border(1.dp, AccentBlue, androidx.compose.foundation.shape.CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Rounded.Add, contentDescription = "Add", tint = AccentBlue)
                        }
                        Spacer(Modifier.height(8.dp))
                        Text("Add Loan", color = AccentBlue, fontSize = 14.sp)
                    }
                }
            }

            Column(modifier = Modifier.padding(horizontal = ResponsiveUtils.horizontalPadding(sizeClass)), verticalArrangement = Arrangement.spacedBy(ResponsiveUtils.cardSpacing(sizeClass))) {
                val isAnyLoanAdded = processedLoans.any { it.loanAmount > 0.0 || it.interestRate > 0.0 || it.tenureYears > 0 }
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
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(onClick = { }, modifier = Modifier.weight(1f), colors = ButtonDefaults.outlinedButtonColors(contentColor = textColor), border = BorderStroke(1.dp, CardStroke)) { 
                            Icon(Icons.Rounded.BookmarkBorder, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Save Comparison", fontSize = 12.sp) 
                        }
                        OutlinedButton(onClick = { }, modifier = Modifier.weight(1f), colors = ButtonDefaults.outlinedButtonColors(contentColor = textColor), border = BorderStroke(1.dp, CardStroke)) { 
                            Icon(Icons.Rounded.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Share Result", fontSize = 12.sp) 
                        }
                        OutlinedButton(onClick = { }, modifier = Modifier.weight(1f), colors = ButtonDefaults.outlinedButtonColors(contentColor = textColor), border = BorderStroke(1.dp, CardStroke)) { 
                            Icon(Icons.Rounded.PictureAsPdf, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Export PDF", fontSize = 12.sp) 
                        }
                    }
                }
            }
        }
    }

    editingLoan?.let { loan ->
        LoanEditBottomSheet(
            loan = loan,
            onDismiss = { editingLoan = null },
            onSave = { updatedLoan ->
                val idx = loansCache.indexOfFirst { it.id == loan.id }
                if (idx != -1) {
                    loansCache[idx] = updatedLoan
                } else {
                    loansCache.add(updatedLoan)
                }
                editingLoan = null
            }
        )
    }

    if (showUnlockDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showUnlockDialog = false },
            containerColor = SurfaceDark,
            titleContentColor = Color.White,
            textContentColor = TextSecondary,
            title = {
                Text("Unlock Unlimited Loans", fontWeight = FontWeight.Bold)
            },
            text = {
                Text("Comparing more than 3 loans is a premium feature. Watch a short ad or upgrade to Premium to unlock unlimited comparisons!")
            },
            confirmButton = {
                Button(
                    onClick = {
                        val baseLoan = processedLoans.firstOrNull { it.loanAmount > 0.0 }
                        val p = baseLoan?.loanAmount ?: 1000000.0
                        val r = if (baseLoan != null && baseLoan.interestRate > 0.0) baseLoan.interestRate - 0.25 else 8.25
                        val t = baseLoan?.tenureYears ?: 10
                        val nextId = listOf("A", "B", "C", "D", "E").firstOrNull { id -> processedLoans.none { it.id == id } } ?: "D"
                        val colors = listOf(Color(0xFF3B82F6), Color(0xFF8B5CF6), AccentYellow, Color(0xFF10B981), Color(0xFFF43F5E))
                        val colorIndex = processedLoans.size % colors.size
                        loansCache.add(LoanOffer(nextId, "Premium Bank", r, t, p, 0.0, 0.0, colors[colorIndex]))
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
            
            OutlinedTextField(
                value = bankName,
                onValueChange = { bankName = it },
                label = { Text("Bank Name", color = TextSecondary) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = AccentBlue,
                    unfocusedBorderColor = CardStroke
                ),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = loanAmount,
                onValueChange = { loanAmount = it },
                label = { Text("Loan Amount (₹)", color = TextSecondary) },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = AccentBlue,
                    unfocusedBorderColor = CardStroke
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = interestRate,
                    onValueChange = { interestRate = it },
                    label = { Text("Interest Rate (%)", color = TextSecondary) },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = AccentBlue,
                        unfocusedBorderColor = CardStroke
                    ),
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = tenure,
                    onValueChange = { tenure = it },
                    label = { Text("Tenure (Years)", color = TextSecondary) },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = AccentBlue,
                        unfocusedBorderColor = CardStroke
                    ),
                    modifier = Modifier.weight(1f)
                )
            }

            OutlinedTextField(
                value = processingFee,
                onValueChange = { processingFee = it },
                label = { Text("Processing Fee (₹)", color = TextSecondary) },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = AccentBlue,
                    unfocusedBorderColor = CardStroke
                ),
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
        val animatedColor = if (loan.isBest) AccentYellow else loan.color.copy(alpha = 0.5f)
        val bgColor = if (loan.isBest) AccentYellow.copy(alpha = 0.05f) else SurfaceDark
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(12.dp))
                .background(bgColor)
                .border(if (loan.isBest) 2.dp else 1.dp, animatedColor, RoundedCornerShape(12.dp))
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Loan ${loan.id}", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                IconButton(onClick = onEdit, modifier = Modifier.size(20.dp)) {
                    Icon(Icons.Rounded.Edit, contentDescription = "Edit", tint = TextSecondary, modifier = Modifier.size(16.dp))
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.AccountBalance, contentDescription = "Bank", tint = loan.color, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text(loan.bankName, color = TextSecondary, fontSize = 12.sp)
            }
            Text("${loan.interestRate}%  •  ${loan.tenureYears} Years", color = TextSecondary, fontSize = 12.sp)
            Text(localFormatMoney(loan.loanAmount), color = TextSecondary, fontSize = 14.sp)
        }
        
        if (loan.isBest) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .clip(RoundedCornerShape(bottomStart = 8.dp, topEnd = 12.dp))
                    .background(AccentYellow)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Star, contentDescription = null, tint = Color.Black, modifier = Modifier.size(10.dp))
                    Spacer(Modifier.width(2.dp))
                    Text("Best", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ComparisonTable(loans: List<LoanOffer>) {
    ResponsiveCard(
        bgColor = SurfaceDark,
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
                    modifier = Modifier.fillMaxWidth().background(Color(0xFF020B1F)).padding(horizontal = 12.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Comparison Overview", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.5f))
                    loans.forEach { loan ->
                        Text("Loan ${loan.id}", color = loan.color, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    }
                }
                HorizontalDivider(color = CardStroke)

                val rows = listOf(
                    Triple(Icons.Rounded.Percent, "Interest Rate (p.a.)") { l: LoanOffer -> "${l.interestRate}%" },
                    Triple(Icons.Rounded.CalendarToday, "Tenure") { l: LoanOffer -> "${l.tenureYears} Years" },
                    Triple(Icons.Rounded.CurrencyRupee, "Monthly EMI") { l: LoanOffer -> localFormatMoney(localCalculateEMI(l.loanAmount, l.interestRate, l.tenureYears)) },
                    Triple(Icons.Rounded.CurrencyRupee, "Total Interest") { l: LoanOffer -> localFormatMoney(localCalculateTotalInterest(l.loanAmount, l.interestRate, l.tenureYears)) },
                    Triple(Icons.Rounded.CurrencyRupee, "Total Payment") { l: LoanOffer -> localFormatMoney(l.loanAmount + localCalculateTotalInterest(l.loanAmount, l.interestRate, l.tenureYears)) },
                    Triple(Icons.Rounded.Description, "Processing Fee") { l: LoanOffer -> localFormatMoney(l.processingFee) },
                    Triple(Icons.Rounded.Settings, "Effective Interest Rate") { l: LoanOffer -> "${l.interestRate + 0.22}%" }, // simplified effective logic for mockup
                    Triple(Icons.Rounded.Schedule, "Prepayment Charges") { l: LoanOffer -> "${l.prepaymentCharges.toInt()}%" }
                )

                rows.forEachIndexed { index, data ->
                    val (icon, title, selector) = data
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(if (index % 2 == 0) Color.Transparent else BackgroundDark.copy(alpha = 0.5f))
                            .padding(horizontal = 12.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(modifier = Modifier.weight(1.5f), verticalAlignment = Alignment.CenterVertically) {
                            Icon(icon, contentDescription = title, tint = TextSecondary, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(title, color = TextSecondary, fontSize = 11.sp)
                            if (title == "Effective Interest Rate") {
                                Spacer(Modifier.width(4.dp))
                                Icon(Icons.Rounded.Info, contentDescription = "Info", tint = TextSecondary, modifier = Modifier.size(12.dp))
                            }
                        }
                        loans.forEach { loan ->
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.weight(1f)) {
                                AutoResizedText(text = selector(loan), color = if (loan.isBest) AccentYellow else loan.color, fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
        ResponsiveCard(
            bgColor = Color(0xFF020B1F),
            borderColor = AccentBlue.copy(alpha = 0.5f)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Badge
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(AccentBlue.copy(alpha = 0.2f))
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Rounded.AutoAwesome, contentDescription = null, tint = AccentBlue, modifier = Modifier.size(12.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("LoanMaster Advisor", color = AccentBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(12.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Lightbulb
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(48.dp)) {
                        Icon(Icons.Rounded.Lightbulb, contentDescription = null, tint = AccentYellow, modifier = Modifier.size(40.dp))
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(advice.title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(4.dp))
                        Text(advice.subtitle, color = TextSecondary, fontSize = 12.sp)
                    }
                    if (advice.showPremium) {
                        Spacer(Modifier.width(12.dp))
                        // Premium box
                        Column(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF0D1B36))
                                .padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("You could save up to", color = TextSecondary, fontSize = 10.sp)
                            Text(advice.savingsText, color = AccentYellow, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Text("in interest", color = TextSecondary, fontSize = 10.sp)
                            Spacer(Modifier.height(4.dp))
                            if (!isPremiumUnlocked) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Rounded.Lock, contentDescription = null, tint = AccentYellow, modifier = Modifier.size(10.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Premium Feature", color = AccentYellow, fontSize = 10.sp)
                                }
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = Color(0xFF4ADE80), modifier = Modifier.size(10.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Unlocked", color = Color(0xFF4ADE80), fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
        
        Spacer(Modifier.height(16.dp))
        
        if (!isPremiumUnlocked) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                OutlinedButton(
                    onClick = { onUnlockRequested() }, 
                    border = BorderStroke(1.dp, AccentYellow), 
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentYellow)
                ) {
                    Icon(Icons.Rounded.SmartDisplay, contentDescription = "Ad", modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Watch Ad to Unlock")
                }
                Text("  or  ", color = Color.White, fontSize = 14.sp)
                Button(
                    onClick = { onUnlockRequested() }, 
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentYellow, contentColor = Color.Black)
                ) {
                    Icon(Icons.Rounded.WorkspacePremium, contentDescription = "Premium", modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Unlock with Premium", fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.LockOpen, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(12.dp))
                Spacer(Modifier.width(4.dp))
                Text("One-time unlock • All insights included", color = TextSecondary, fontSize = 12.sp)
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
            savingsText = "₹0",
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
            HorizontalDivider(modifier = Modifier.weight(1f), color = CardStroke)
            Spacer(Modifier.width(16.dp))
            Text(if (isPremiumUnlocked) "Premium Tools (Unlocked)" else "What you'll unlock", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
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
                        ResponsiveCard(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { if (isPremiumUnlocked) onToolClick(title) },
                            bgColor = SurfaceDark,
                            borderColor = if (isPremiumUnlocked) AccentGreen else CardStroke
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .defaultMinSize(minHeight = 110.dp),
                                horizontalAlignment = Alignment.Start
                            ) {
                                Box(modifier = Modifier.size(32.dp)) {
                                    Icon(icon, contentDescription = null, tint = AccentBlue, modifier = Modifier.size(28.dp).align(Alignment.Center))
                                    if (isPremiumUnlocked) {
                                        Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = AccentGreen, modifier = Modifier.size(12.dp).align(Alignment.BottomEnd))
                                    } else {
                                        Icon(Icons.Rounded.Lock, contentDescription = null, tint = AccentYellow, modifier = Modifier.size(12.dp).align(Alignment.BottomEnd))
                                    }
                                }
                                Spacer(Modifier.height(12.dp))
                                Text(title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Spacer(Modifier.height(4.dp))
                                Text(desc, color = TextSecondary, fontSize = 11.sp, lineHeight = 16.sp)
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
            .padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(Color(0xFF0D1B36), shape = androidx.compose.foundation.shape.CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Rounded.AccountBalance,
                contentDescription = null,
                tint = AccentBlue.copy(alpha = 0.5f),
                modifier = Modifier.size(64.dp)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "No Loans Added Yet",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Add loans above to start comparing EMI, total interest, and more.",
            color = TextSecondary,
            fontSize = 14.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
    }
}

private fun localFormatMoney(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    format.maximumFractionDigits = 0
    return format.format(amount).replace("₹", "₹") 
}

private fun localCalculateEMI(principal: Double, rate: Double, years: Int): Double {
    if (principal <= 0 || rate <= 0 || years <= 0) return 0.0
    val r = rate / 12 / 100
    val n = years * 12
    return principal * r * (1 + r).pow(n) / ((1 + r).pow(n) - 1)
}

private fun localCalculateTotalInterest(principal: Double, rate: Double, years: Int): Double {
    val emi = localCalculateEMI(principal, rate, years)
    val totalPayment = emi * years * 12
    return totalPayment - principal
}
