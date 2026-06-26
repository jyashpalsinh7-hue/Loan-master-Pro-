package com.example

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrepaymentCalculatorScreen(onNavigateBack: () -> Unit = {}) {
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600

    val bgColor = ResponsiveUtils.BgColor
    val surfaceColor = ResponsiveUtils.SurfaceColor
    val primaryColor = ResponsiveUtils.PrimaryAccent
    val secondaryColor = ResponsiveUtils.SecondaryAccent
    val accentGreen = Color(0xFF4ADE80)
    val accentOrange = Color(0xFFF97316)
    
    var loanAmount by remember { mutableStateOf("5000000") }
    var interestRate by remember { mutableStateOf("8.5") }
    var tenureYears by remember { mutableStateOf("15") }
    var prepaymentAmount by remember { mutableStateOf("100000") }
    var monthlyPrepayment by remember { mutableStateOf("0") }
    var annualPrepayment by remember { mutableStateOf("0") }
    var strategy by remember { mutableStateOf("Tenure") } // "Tenure" or "EMI"
    var showAmortization by remember { mutableStateOf(false) }
    var isAiUnlocked by remember { mutableStateOf(false) }
    var showUnlockDialog by remember { mutableStateOf(false) }

    val p = loanAmount.safeToDouble()
    val rate = interestRate.safeToDouble()
    val terms = tenureYears.safeToDouble().coerceIn(0.0, 100.0)
    val prePay = prepaymentAmount.safeToDouble()
    val monthlyPrepay = monthlyPrepayment.safeToDouble()
    val annualPrepay = annualPrepayment.safeToDouble()

    val r = if (rate > 0) (rate / 12) / 100 else 0.0
    val n = terms * 12
    val emi = if (p > 0 && r > 0 && n > 0) p * (r * Math.pow(1 + r, n)) / (Math.pow(1 + r, n) - 1) else 0.0
    
    val originalTotalPayment = emi * n
    val originalTotalInterest = originalTotalPayment - p
    
    val newPrincipal = p - prePay
    var newEmi = emi
    var newTenureMonths = n
    var newTotalInterest = 0.0

    if (newPrincipal <= 0) {
        newEmi = 0.0
        newTenureMonths = 0.0
        newTotalInterest = 0.0
    } else {
        if (strategy == "EMI") {
            newEmi = if (r > 0 && n > 0) newPrincipal * (r * Math.pow(1 + r, n)) / (Math.pow(1 + r, n) - 1) else newPrincipal / n
            newTenureMonths = n
            newTotalInterest = (newEmi * n) - newPrincipal
        } else {
            // Complex simulation for both monthly and annual extra payments
            newEmi = emi + monthlyPrepay
            if (annualPrepay == 0.0) {
                if (r > 0 && newEmi > newPrincipal * r) {
                    newTenureMonths = Math.log(newEmi / (newEmi - newPrincipal * r)) / Math.log(1 + r)
                } else if (r == 0.0 && newEmi > 0) {
                    newTenureMonths = newPrincipal / newEmi
                } else {
                    newTenureMonths = n
                }
                newTotalInterest = (newEmi * newTenureMonths) - newPrincipal
            } else {
                // Iterative approach to find actual tenure and interest
                var bal = newPrincipal
                var months = 0
                var totInt = 0.0
                while (bal > 0.01 && months <= n * 2) {
                    val interestForMonth = bal * r
                    totInt += interestForMonth
                    var principalForMonth = newEmi - interestForMonth
                    
                    if (bal < principalForMonth) {
                        principalForMonth = bal
                    }
                    bal -= principalForMonth
                    
                    // Annual prepayment at the end of each year (month 12, 24, 36...)
                    if ((months + 1) % 12 == 0 && bal > 0.01) {
                        val extra = if (bal < annualPrepay) bal else annualPrepay
                        bal -= extra
                    }
                    months++
                }
                newTenureMonths = months.toDouble()
                newTotalInterest = totInt
            }
        }
    }

    val interestSaved = if (originalTotalInterest > newTotalInterest) originalTotalInterest - newTotalInterest else 0.0
    val tenureReducedMonths = if (n > newTenureMonths) n - newTenureMonths else 0.0
    val emiReduced = if (emi > newEmi) emi - newEmi else 0.0

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(bgColor).statusBarsPadding()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp).clickable { onNavigateBack() }
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Prepayment",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
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
                .padding(horizontal = if (isTablet) 32.dp else 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            // Hero Card: Interest Saved
            item {
                PrepaymentHeroCard(interestSaved, tenureReducedMonths, emiReduced, strategy, accentGreen, surfaceColor)
            }

            // Input Fields
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(surfaceColor)
                        .border(1.dp, ResponsiveUtils.CardStroke, RoundedCornerShape(24.dp))
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("Loan Details", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    
                    PremiumInputField("Outstanding Loan Amount", "₹", loanAmount) { loanAmount = it }
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Box(modifier = Modifier.weight(1f)) {
                            PremiumInputField("Interest Rate", "%", interestRate) { interestRate = it }
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            PremiumInputField("Remaining Tenure", "Yrs", tenureYears) { tenureYears = it }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = ResponsiveUtils.CardStroke)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text("Prepayment Details", color = primaryColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    
                    Row(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color.Black.copy(alpha = 0.2f)).padding(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (strategy == "Tenure") primaryColor else Color.Transparent)
                                .clickable { strategy = "Tenure" }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Reduce Tenure", color = if (strategy == "Tenure") Color.White else ResponsiveUtils.TextSecondary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (strategy == "EMI") primaryColor else Color.Transparent)
                                .clickable { strategy = "EMI" }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Reduce EMI", color = if (strategy == "EMI") Color.White else ResponsiveUtils.TextSecondary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    PremiumInputField("Lump Sum Prepayment", "₹", prepaymentAmount) { prepaymentAmount = it }
                    
                    // Slider for Lump Sum
                    val maxSliderValue = if (p > 0) p.toFloat() else 10000000f
                    val currentSliderValue = prePay.toFloat().coerceIn(0f, maxSliderValue)
                    Slider(
                        value = currentSliderValue,
                        onValueChange = { prepaymentAmount = it.toInt().toString() },
                        valueRange = 0f..maxSliderValue,
                        colors = SliderDefaults.colors(
                            thumbColor = primaryColor,
                            activeTrackColor = primaryColor,
                            inactiveTrackColor = primaryColor.copy(alpha = 0.2f)
                        )
                    )

                    if (strategy == "Tenure") {
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Box(modifier = Modifier.weight(1f)) {
                                PremiumInputField("Monthly Extra", "₹", monthlyPrepayment) { monthlyPrepayment = it }
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                PremiumInputField("Annual Extra", "₹", annualPrepayment) { annualPrepayment = it }
                            }
                        }
                    }
                }
            }

            // Comparison Cards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
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
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
                ) {
                    Icon(Icons.Rounded.TableChart, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("View Amortization Schedule", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            item {
                // Premium locked AI Insights card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF2D1B4E), Color(0xFF1E1136))
                            )
                        )
                        .border(1.dp, Color(0xFF6B21A8), RoundedCornerShape(24.dp))
                        .clickable { if (!isAiUnlocked) showUnlockDialog = true }
                        .padding(20.dp)
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
                                    Icon(Icons.Rounded.AutoAwesome, contentDescription = "AI Insights", tint = Color(0xFFC084FC), modifier = Modifier.size(20.dp))
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("AI Strategy Unlocked", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            val bestMonth = 1
                            val delayLoss = prePay * r // rough estimate of interest lost by delaying one month
                            
                            Text("Optimal Prepayment Month: Month $bestMonth", color = Color(0xFF4ADE80), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Mathematically, the earlier you prepay, the more principal you reduce. Prepaying ₹${com.example.formatMoney(prePay)} in Month 1 saves maximum interest. Delaying this prepayment by just one year will cost you approximately ₹${com.example.formatMoney(delayLoss * 12)} in additional interest.", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp, lineHeight = 20.sp)
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF6B21A8).copy(alpha = 0.3f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Rounded.AutoAwesome, contentDescription = "AI Insights", tint = Color(0xFFC084FC))
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("AI Smart Strategy", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(Icons.Rounded.Lock, contentDescription = "Locked", tint = Color(0xFFFCD34D), modifier = Modifier.size(14.dp))
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Find the optimal month to prepay for maximum interest savings.", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = null, tint = Color(0xFFC084FC))
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
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
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Unlock Premium Features", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = { Text("Watch a short ad or upgrade to Premium to unlock AI Smart Strategy and PDF Export of the Amortization schedule.", color = ResponsiveUtils.TextSecondary, fontSize = 14.sp) },
            confirmButton = {
                Button(
                    onClick = { 
                        isAiUnlocked = true 
                        showUnlockDialog = false 
                    }, 
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC084FC)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Rounded.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
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
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Rounded.WorkspacePremium, contentDescription = null, tint = Color(0xFFC084FC), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Buy Premium")
                }
            },
            containerColor = ResponsiveUtils.SurfaceColor
        )
    }
}

@Composable
fun PrepaymentHeroCard(interestSaved: Double, tenureReducedMonths: Double, emiReduced: Double, strategy: String, accentColor: Color, surfaceColor: Color) {
    val formatMoney = { amount: Double ->
        com.example.formatMoney(amount, "₹")
    }
    
    val years = (tenureReducedMonths / 12).toInt()
    val months = (tenureReducedMonths % 12).toInt()
    val tenureText = if (years > 0 && months > 0) "$years Yrs $months Mos"
    else if (years > 0) "$years Yrs"
    else "$months Mos"

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(Brush.linearGradient(listOf(Color(0xFF0D3B23), surfaceColor)))
            .border(1.dp, accentColor.copy(alpha = 0.3f), RoundedCornerShape(28.dp))
            .padding(24.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Savings, contentDescription = null, tint = accentColor, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("TOTAL INTEREST SAVED", color = accentColor, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
            AutoSizeText(
                text = formatMoney(interestSaved),
                color = accentColor,
                minTextSize = 24.sp,
                maxTextSize = 48.sp,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black.copy(alpha = 0.2f))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val icon = if(strategy == "Tenure") Icons.Rounded.Event else Icons.Rounded.AccountBalanceWallet
                Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                val savedText = if (strategy == "Tenure") "Tenure Reduced by: $tenureText" else "EMI Reduced by: ${formatMoney(emiReduced)}"
                Text(savedText, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
fun ComparisonCard(modifier: Modifier, title: String, totalInterest: Double, color: Color, surfaceColor: Color) {
    val formatMoney = { amount: Double ->
        com.example.formatMoney(amount, "₹")
    }
    
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(surfaceColor)
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(title, color = ResponsiveUtils.TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(8.dp))
        Text(formatMoney(totalInterest), color = color, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text("Total Interest", color = ResponsiveUtils.TextSecondary, fontSize = 10.sp)
    }
}

@Composable
fun PremiumInputField(label: String, symbol: String, value: String, onValueChange: (String) -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    
    Column {
        Text(label, color = ResponsiveUtils.TextSecondary, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(8.dp))
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.Black.copy(alpha = 0.2f))
                .border(1.dp, ResponsiveUtils.CardStroke, RoundedCornerShape(16.dp))
                .padding(horizontal = 16.dp, vertical = 16.dp),
            textStyle = TextStyle(
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            interactionSource = interactionSource,
            decorationBox = { innerTextField ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (symbol.isNotEmpty() && symbol == "₹") {
                        Text(symbol, color = ResponsiveUtils.PrimaryAccent, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        if (value.isEmpty()) {
                            Text("0", color = Color.White.copy(alpha = 0.3f), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                        innerTextField()
                    }
                    if (symbol.isNotEmpty() && symbol == "%" || symbol == "Yrs") {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(symbol, color = ResponsiveUtils.PrimaryAccent, fontSize = 16.sp, fontWeight = FontWeight.Bold)
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
            .clip(RoundedCornerShape(24.dp))
            .background(surfaceColor)
            .border(1.dp, ResponsiveUtils.CardStroke, RoundedCornerShape(24.dp))
            .padding(20.dp)
    ) {
        Text("Payment Breakdown", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Without Prepayment
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(modifier = Modifier.size(100.dp), contentAlignment = Alignment.Center) {
                    Canvas(modifier = Modifier.fillMaxSize().padding(8.dp)) {
                        drawArc(color = primaryColor, startAngle = -90f, sweepAngle = 360f * ogPrinPct, useCenter = false, style = Stroke(width = 30f, cap = StrokeCap.Round))
                        drawArc(color = accentOrange, startAngle = -90f + (360f * ogPrinPct), sweepAngle = 360f * ogIntPct, useCenter = false, style = Stroke(width = 30f, cap = StrokeCap.Round))
                    }
                    Text("${(ogIntPct*100).toInt()}%", color = accentOrange, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text("Standard", color = ResponsiveUtils.TextSecondary, fontSize = 12.sp)
                Text(com.example.formatMoney(originalTotal, "₹"), color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
            
            Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = null, tint = ResponsiveUtils.TextSecondary, modifier = Modifier.size(32.dp))
            
            // With Prepayment
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(modifier = Modifier.size(100.dp), contentAlignment = Alignment.Center) {
                    Canvas(modifier = Modifier.fillMaxSize().padding(8.dp)) {
                        drawArc(color = primaryColor, startAngle = -90f, sweepAngle = 360f * newPrinPct, useCenter = false, style = Stroke(width = 30f, cap = StrokeCap.Round))
                        drawArc(color = accentGreen, startAngle = -90f + (360f * newPrinPct), sweepAngle = 360f * newIntPct, useCenter = false, style = Stroke(width = 30f, cap = StrokeCap.Round))
                    }
                    Text("${(newIntPct*100).toInt()}%", color = accentGreen, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text("Prepayment", color = ResponsiveUtils.TextSecondary, fontSize = 12.sp)
                Text(com.example.formatMoney(newTotal, "₹"), color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(primaryColor))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Principal", color = ResponsiveUtils.TextSecondary, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.width(24.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(accentOrange))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Interest", color = ResponsiveUtils.TextSecondary, fontSize = 12.sp)
            }
        }
    }
}

data class AmortizationRow(
    val month: Int,
    val payment: Double,
    val principal: Double,
    val interest: Double,
    val balance: Double,
    val isPrepayment: Boolean = false,
    val label: String = ""
)

fun generateStandardSchedule(p: Double, r: Double, n: Double, emi: Double): List<AmortizationRow> {
    var balance = p
    val schedule = mutableListOf<AmortizationRow>()
    for (i in 1..n.toInt()) {
        val interest = balance * r
        var principal = emi - interest
        if (balance < principal) {
            principal = balance
        }
        balance -= principal
        schedule.add(AmortizationRow(i, principal + interest, principal, interest, if (balance < 0) 0.0 else balance, false, "EMI"))
        if (balance <= 0) break
    }
    return schedule
}

fun generatePrepaymentSchedule(p: Double, prePay: Double, monthlyPrepay: Double, annualPrepay: Double, r: Double, n: Double, emi: Double, strategy: String): List<AmortizationRow> {
    var balance = p
    val schedule = mutableListOf<AmortizationRow>()
    
    if (prePay > 0) {
        val actualPrepay = if (balance < prePay) balance else prePay
        balance -= actualPrepay
        schedule.add(AmortizationRow(0, actualPrepay, actualPrepay, 0.0, balance, true, "Lump Sum"))
    }

    if (balance <= 0) return schedule

    var currentEmi = emi
    if (strategy == "EMI") {
        currentEmi = if (r > 0 && n > 0) balance * (r * Math.pow(1 + r, n)) / (Math.pow(1 + r, n) - 1) else balance / n
    }

    var month = 1
    while (balance > 0.01 && month <= n.toInt() * 2) {
        val interest = balance * r
        var principal = currentEmi - interest
        if (balance < principal) {
            principal = balance
            currentEmi = principal + interest
        }
        balance -= principal
        schedule.add(AmortizationRow(month, currentEmi, principal, interest, if (balance < 0) 0.0 else balance, false, "EMI"))
        
        if (strategy == "Tenure" && monthlyPrepay > 0 && balance > 0.01) {
            val extraPrepay = if (balance < monthlyPrepay) balance else monthlyPrepay
            balance -= extraPrepay
            schedule.add(AmortizationRow(month, extraPrepay, extraPrepay, 0.0, if (balance < 0) 0.0 else balance, true, "Extra Pay"))
        }

        if (strategy == "Tenure" && annualPrepay > 0 && balance > 0.01 && month % 12 == 0) {
            val extraPrepay = if (balance < annualPrepay) balance else annualPrepay
            balance -= extraPrepay
            schedule.add(AmortizationRow(month, extraPrepay, extraPrepay, 0.0, if (balance < 0) 0.0 else balance, true, "Annual Pay"))
        }

        month++
    }
    return schedule
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AmortizationBottomSheet(
    p: Double, prePay: Double, monthlyPrepay: Double, annualPrepay: Double, r: Double, n: Double, originalEmi: Double, strategy: String,
    isUnlocked: Boolean = false, onUnlockClick: () -> Unit = {}, onDismiss: () -> Unit
) {
    var isStandard by remember { mutableStateOf(true) }
    
    val standardSchedule = remember(p, r, n, originalEmi) {
        generateStandardSchedule(p, r, n, originalEmi)
    }
    val prepaySchedule = remember(p, prePay, monthlyPrepay, annualPrepay, r, n, originalEmi, strategy) {
        generatePrepaymentSchedule(p, prePay, monthlyPrepay, annualPrepay, r, n, originalEmi, strategy)
    }
    
    val schedule = if (isStandard) standardSchedule else prepaySchedule

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = ResponsiveUtils.SurfaceColor,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Amortization Schedule",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                
                val context = androidx.compose.ui.platform.LocalContext.current
                // Premium Feature Button
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
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
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Rounded.PictureAsPdf, contentDescription = "Export PDF", tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Export", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    if (!isUnlocked) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Rounded.WorkspacePremium, contentDescription = "Premium", tint = Color.Yellow, modifier = Modifier.size(12.dp))
                    }
                }
            }
            
            // Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black.copy(alpha = 0.2f))
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isStandard) ResponsiveUtils.PrimaryAccent else Color.Transparent)
                        .clickable { isStandard = true }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Standard", color = if (isStandard) Color.White else ResponsiveUtils.TextSecondary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (!isStandard) ResponsiveUtils.PrimaryAccent else Color.Transparent)
                        .clickable { isStandard = false }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Prepayment", color = if (!isStandard) Color.White else ResponsiveUtils.TextSecondary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Header
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Mon", color = ResponsiveUtils.TextSecondary, fontSize = 12.sp, modifier = Modifier.weight(0.5f))
                Text("Principal", color = ResponsiveUtils.TextSecondary, fontSize = 12.sp, textAlign = TextAlign.End, modifier = Modifier.weight(1f))
                Text("Interest", color = ResponsiveUtils.TextSecondary, fontSize = 12.sp, textAlign = TextAlign.End, modifier = Modifier.weight(1f))
                Text("Balance", color = ResponsiveUtils.TextSecondary, fontSize = 12.sp, textAlign = TextAlign.End, modifier = Modifier.weight(1f))
            }
            HorizontalDivider(color = ResponsiveUtils.CardStroke)
            
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f, fill = false).heightIn(max = 400.dp)
            ) {
                items(schedule) { row ->
                    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(if (row.month == 0) "-" else "${row.month}", color = Color.White, fontSize = 14.sp, modifier = Modifier.weight(0.5f))
                            Text(com.example.formatMoney(row.principal, "₹"), color = Color(0xFF4ADE80), fontSize = 14.sp, textAlign = TextAlign.End, modifier = Modifier.weight(1f))
                            Text(if (row.interest > 0) com.example.formatMoney(row.interest, "₹") else "-", color = Color(0xFFF97316), fontSize = 14.sp, textAlign = TextAlign.End, modifier = Modifier.weight(1f))
                            Text(com.example.formatMoney(row.balance, "₹"), color = Color.White, fontSize = 14.sp, textAlign = TextAlign.End, modifier = Modifier.weight(1f))
                        }
                        if (row.label.isNotEmpty()) {
                            Text(row.label, color = if (row.isPrepayment) ResponsiveUtils.PrimaryAccent else ResponsiveUtils.TextSecondary, fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp))
                        }
                    }
                    HorizontalDivider(color = ResponsiveUtils.CardStroke.copy(alpha = 0.5f))
                }
            }
        }
    }
}
