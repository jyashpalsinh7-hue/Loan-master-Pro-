package com.example

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
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
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.pow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmiCalculatorScreen(onNavigateBack: () -> Unit = {}) {
    val bgColor = Color(0xFF020B1F)
    val primaryCard = Color(0xFF061633)
    val inputBg = Color(0xFF071833)
    val borderColor = Color(0xFF183C8A)
    val primaryText = Color(0xFFFFFFFF)
    val secondaryText = Color(0xFFA8B3D1)

    val blueAccent = Color(0xFF2D7DFF)
    val goldAccent = Color(0xFFFFC328)
    val greenAccent = Color(0xFF22C55E)
    val purpleAccent = Color(0xFF7C4DFF)
    val activeNav = Color(0xFFFFC328)
    val inactiveNav = Color(0xFF9AA6C8)

    // === START WITH ZEROS ===
    val configuration = LocalConfiguration.current
    val isCompact = configuration.screenWidthDp < 400

    var loanAmountText by remember { mutableStateOf("0") }
    var interestRateText by remember { mutableStateOf("0") }
    var tenureText by remember { mutableStateOf("0") }
    var isTenureMonths by remember { mutableStateOf(false) }
    var firstEmiDate by remember { mutableStateOf("Select Date") }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    val loanAmount = loanAmountText.toDoubleOrNull() ?: 0.0
    val interestRate = interestRateText.toDoubleOrNull() ?: 0.0
    val tenure = tenureText.toIntOrNull() ?: 0

    val totalMonths = if (isTenureMonths) tenure else tenure * 12
    val r = if (interestRate > 0) (interestRate / 12) / 100 else 0.0

    val monthlyEmi = if (loanAmount > 0 && r > 0 && totalMonths > 0) {
        loanAmount * (r * (1 + r).pow(totalMonths)) / ((1 + r).pow(totalMonths) - 1)
    } else 0.0

    val totalPayment = monthlyEmi * totalMonths
    val totalInterest = totalPayment - loanAmount
    val totalPrincipal = loanAmount

    val isLoanAmountInvalid = loanAmountText.isNotEmpty() && loanAmountText != "0" && loanAmountText.toDoubleOrNull() == null
    val loanAmountErrorMsg = when {
        isLoanAmountInvalid -> "Invalid number"
        loanAmountText.isNotEmpty() && loanAmountText != "0" && loanAmount <= 0 -> "Must be > 0"
        loanAmountText.isNotEmpty() && loanAmountText != "0" && loanAmount > 100000000000000.0 -> "Too large"
        else -> ""
    }
    val isLoanAmountError = loanAmountErrorMsg.isNotEmpty()

    val isInterestRateInvalid = interestRateText.isNotEmpty() && interestRateText != "0" && interestRateText.toDoubleOrNull() == null
    val interestRateErrorMsg = when {
        isInterestRateInvalid -> "Invalid number"
        interestRateText.isNotEmpty() && interestRateText != "0" && interestRate <= 0 -> "Must be > 0"
        interestRateText.isNotEmpty() && interestRateText != "0" && interestRate > 100 -> "Max 100%"
        else -> ""
    }
    val isInterestRateError = interestRateErrorMsg.isNotEmpty()

    val isTenureInvalid = tenureText.isNotEmpty() && tenureText != "0" && tenureText.toIntOrNull() == null
    val tenureErrorMsg = when {
        isTenureInvalid -> "Invalid number"
        tenureText.isNotEmpty() && tenureText != "0" && tenure <= 0 -> "Must be > 0"
        isTenureMonths && tenureText.isNotEmpty() && tenureText != "0" && tenure > 600 -> "Max 600 mos"
        !isTenureMonths && tenureText.isNotEmpty() && tenureText != "0" && tenure > 50 -> "Max 50 yrs"
        else -> ""
    }
    val isTenureError = tenureErrorMsg.isNotEmpty()

    val hasValidInput = !isLoanAmountError && !isInterestRateError && !isTenureError && loanAmount > 0 && interestRate > 0 && tenure > 0

    val formatMoney = { amt: Double ->
        if (amt >= 10000000) {
            "₹" + String.format(Locale.US, "%.2fCr", amt / 10000000)
        } else if (amt >= 100000) {
            "₹" + String.format(Locale.US, "%.2fL", amt / 100000)
        } else {
            val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
            format.maximumFractionDigits = 0
            format.format(amt)
        }
    }

    Scaffold(
        containerColor = bgColor,
        bottomBar = {
            NavigationBar(
                containerColor = bgColor,
                tonalElevation = 0.dp,
                modifier = Modifier.border(1.dp, primaryCard)
            ) {
                val items = listOf(
                    "Home" to Icons.Rounded.Home,
                    "History" to Icons.Rounded.History,
                    "Calculate" to Icons.Rounded.Calculate,
                    "Compare" to Icons.Rounded.CompareArrows,
                    "Settings" to Icons.Rounded.Settings
                )
                items.forEachIndexed { index, (label, icon) ->
                    NavigationBarItem(
                        selected = index == 2,
                        onClick = {},
                        icon = { Icon(icon, contentDescription = label) },
                        label = { Text(label, fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = activeNav,
                            selectedTextColor = activeNav,
                            unselectedIconColor = inactiveNav,
                            unselectedTextColor = inactiveNav,
                            indicatorColor = Color.Transparent
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        showDatePicker = false
                        val sel = datePickerState.selectedDateMillis
                        if (sel != null) {
                            val cal = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC"))
                            cal.timeInMillis = sel
                            val sdf = java.text.SimpleDateFormat("MMM yyyy", Locale.getDefault())
                            firstEmiDate = sdf.format(cal.time)
                        }
                    }) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // HEADER
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Back",
                    tint = primaryText,
                    modifier = Modifier.clickable { onNavigateBack() }
                )
                Spacer(Modifier.width(16.dp))
                Column(Modifier.weight(1f)) {
                    Text("EMI Calculator", color = primaryText, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("Calculate your loan EMI and plan better", color = secondaryText, fontSize = 11.sp)
                }
                Icon(Icons.Rounded.StarBorder, contentDescription = "Favorite", tint = goldAccent, modifier = Modifier.size(24.dp))
                Spacer(Modifier.width(16.dp))
                Icon(Icons.Rounded.Share, contentDescription = "Share", tint = primaryText, modifier = Modifier.size(22.dp))
            }

            // INPUT CARD
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = primaryCard),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, borderColor)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        PremiumInputFieldEmi(
                            modifier = Modifier.weight(1f),
                            label = "Loan Amount",
                            value = loanAmountText,
                            onValueChange = { loanAmountText = it },
                            placeholder = "0",
                            icon = Icons.Rounded.AccountBalanceWallet,
                            iconTint = blueAccent,
                            isError = isLoanAmountError,
                            errorMessage = loanAmountErrorMsg
                        )
                        PremiumInputFieldEmi(
                            modifier = Modifier.weight(1f),
                            label = "Interest Rate (p.a.)",
                            value = interestRateText,
                            onValueChange = { interestRateText = it },
                            placeholder = "0",
                            icon = Icons.Rounded.Percent,
                            iconTint = blueAccent,
                            isError = isInterestRateError,
                            errorMessage = interestRateErrorMsg
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
                        PremiumInputFieldEmi(
                            modifier = Modifier.weight(1f),
                            label = "Tenure",
                            value = tenureText,
                            onValueChange = { tenureText = it },
                            placeholder = "0",
                            icon = Icons.Rounded.DateRange,
                            iconTint = blueAccent,
                            trailingContent = {
                                Surface(
                                    color = Color.Transparent, 
                                    border = BorderStroke(1.dp, blueAccent), 
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.clickable { isTenureMonths = !isTenureMonths }
                                ) {
                                    Text(
                                        text = if (isTenureMonths) "Mo" else "Yr",
                                        color = blueAccent,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            },
                            isError = isTenureError,
                            errorMessage = tenureErrorMsg
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            PremiumInputFieldEmi(
                                label = "First EMI Date (Optional)",
                                value = if (firstEmiDate == "Select Date") "" else firstEmiDate,
                                placeholder = "Select Date",
                                onValueChange = {},
                                icon = Icons.Rounded.DateRange,
                                iconTint = blueAccent,
                                trailingContent = {
                                    Icon(Icons.Rounded.KeyboardArrowDown, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                                },
                                readOnly = true,
                                onClick = { showDatePicker = true },
                                fontSize = 14.sp
                            )
                            Text("Leave blank to use current month", color = secondaryText, fontSize = 9.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                        }
                    }
                }
            }

            // HERO RESULT CARD (with animation)
            AnimatedVisibility(
                visible = hasValidInput,
                enter = fadeIn(tween(400)) + scaleIn(initialScale = 0.95f, animationSpec = tween(400))
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth().shadow(16.dp, spotColor = blueAccent.copy(0.4f)),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, borderColor)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Brush.linearGradient(listOf(inputBg, Color(0xFF0A2150))))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("Your Monthly EMI", color = secondaryText, fontSize = 13.sp)
                                        Spacer(Modifier.width(4.dp))
                                        Icon(Icons.Rounded.Info, contentDescription = null, tint = secondaryText, modifier = Modifier.size(14.dp))
                                    }
                                    Spacer(Modifier.height(6.dp))
                                    AutoResizeHeroTextEmi(
                                        text = formatMoney(monthlyEmi),
                                        color = blueAccent
                                    )
                                }

                                // Premium financial visual (Compact Donut Chart)
                                Box(modifier = Modifier.size(78.dp), contentAlignment = Alignment.Center) {
                                    val prinPct = if (totalPayment > 0) (totalPrincipal / totalPayment).toFloat() else 0f
                                    
                                    // Subtle dark background
                                    Box(modifier = Modifier.size(64.dp).background(Color(0xFF071833), CircleShape))
                                    
                                    Canvas(modifier = Modifier.size(54.dp)) {
                                        // Track background
                                        drawArc(
                                            color = Color(0xFF1E3A8A).copy(alpha = 0.4f), 
                                            startAngle = 0f, 
                                            sweepAngle = 360f, 
                                            useCenter = false, 
                                            style = Stroke(width = 14f, cap = StrokeCap.Round)
                                        )
                                        
                                        // Principal arc (Blue)
                                        drawArc(
                                            color = blueAccent, 
                                            startAngle = -90f + 5f, 
                                            sweepAngle = Math.max(0f, 360f * prinPct - 10f), 
                                            useCenter = false, 
                                            style = Stroke(width = 14f, cap = StrokeCap.Round)
                                        )
                                        
                                        // Interest arc (Gold)
                                        drawArc(
                                            color = goldAccent, 
                                            startAngle = -90f + (360f * prinPct) + 5f, 
                                            sweepAngle = Math.max(0f, 360f * (1f - prinPct) - 10f), 
                                            useCenter = false, 
                                            style = Stroke(width = 14f, cap = StrokeCap.Round)
                                        )
                                    }
                                    
                                    // Center Rupee symbol
                                    Icon(Icons.Rounded.CurrencyRupee, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                                }
                            }

                            Spacer(Modifier.height(18.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                HeroMetricEmi(Modifier.weight(1f), "Total Interest", formatMoney(totalInterest), greenAccent)
                                VerticalDivider(thickness = 1.dp, color = borderColor, modifier = Modifier.height(32.dp).padding(horizontal = 2.dp))
                                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1.2f)) {
                                    Text("Total Payment", color = secondaryText, fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    val totalPaymentStr = formatMoney(totalPayment)
                                    var ptScaledFontSize by remember(totalPaymentStr) { mutableStateOf(14.sp) }
                                    Text(
                                        text = totalPaymentStr, 
                                        color = primaryText, 
                                        fontSize = ptScaledFontSize, 
                                        fontWeight = FontWeight.Bold, 
                                        maxLines = 1, 
                                        overflow = TextOverflow.Visible, 
                                        softWrap = false,
                                        onTextLayout = { result ->
                                            if (result.hasVisualOverflow && ptScaledFontSize > 8.sp) {
                                                ptScaledFontSize = (ptScaledFontSize.value - 1f).sp
                                            }
                                        }
                                    )
                                    Text("(Principal+Interest)", color = secondaryText, fontSize = 9.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                                VerticalDivider(thickness = 1.dp, color = borderColor, modifier = Modifier.height(32.dp).padding(horizontal = 2.dp))
                                HeroMetricEmi(Modifier.weight(1f), "Total Principal", formatMoney(totalPrincipal), purpleAccent)
                            }
                        }
                    }
                }
            }

            // When no input yet
            if (!hasValidInput) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = primaryCard),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, borderColor)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Rounded.Calculate, contentDescription = null, tint = secondaryText.copy(0.6f), modifier = Modifier.size(48.dp))
                        Spacer(Modifier.height(12.dp))
                        Text("Enter loan details above to calculate EMI", color = secondaryText, fontSize = 14.sp, textAlign = TextAlign.Center)
                    }
                }
            }

            // PRINCIPAL VS INTEREST + RATE COMPARISON
            if (hasValidInput) {
                if (isCompact) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        PrincipalCardEmi(Modifier.fillMaxWidth(), totalPrincipal, totalPayment, totalInterest, { formatMoney(it) })
                        RateImpactAnalysisCardEmi(Modifier.fillMaxWidth(), loanAmount, totalMonths, interestRate, { formatMoney(it) }, monthlyEmi)
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        PrincipalCardEmi(Modifier.weight(1f), totalPrincipal, totalPayment, totalInterest, { formatMoney(it) })
                        RateImpactAnalysisCardEmi(Modifier.weight(1f), loanAmount, totalMonths, interestRate, { formatMoney(it) }, monthlyEmi)
                    }
                }

                // SMART RECOMMENDATIONS & AMORTIZATION (restored from original)
                // F. SMART RECOMMENDATIONS (LOCKED)
                Card(
                    modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                    colors = CardDefaults.cardColors(containerColor = primaryCard),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, purpleAccent.copy(alpha=0.5f))
                ) {
                    Column(modifier = Modifier.padding(14.dp).fillMaxWidth()) {
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.AutoAwesome, contentDescription=null, tint=purpleAccent, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Smart Recommendations", color = primaryText, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.width(12.dp))
                            Box(modifier = Modifier.background(purpleAccent.copy(alpha=0.2f), RoundedCornerShape(12.dp)).padding(horizontal=8.dp, vertical=4.dp)) {
                                Text("4 Strategies", color = purpleAccent, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        if (isCompact) {
                            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.SpaceBetween) {
                                    Text("Unlock personalized strategies to save more and repay faster.", color = secondaryText, fontSize = 11.sp, lineHeight = 16.sp)
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Column {
                                        Button(
                                            onClick = { },
                                            colors = ButtonDefaults.buttonColors(containerColor = purpleAccent, contentColor = Color.White),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.fillMaxWidth().height(36.dp),
                                            contentPadding = PaddingValues(0.dp)
                                        ) {
                                            Icon(Icons.Rounded.PlayCircle, contentDescription=null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Watch Ad to Unlock", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Rounded.Lock, contentDescription=null, tint=secondaryText, modifier = Modifier.size(10.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Takes 15 seconds", color = secondaryText, fontSize = 10.sp)
                                        }
                                    }
                                }
                                
                                LazyRow(modifier = Modifier.fillMaxWidth().height(110.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    item { LockedStrategyCardEmi("Best Savings", Icons.Rounded.EmojiEvents, greenAccent) }
                                    item { LockedStrategyCardEmi("Fastest Closure", Icons.Rounded.FlashOn, purpleAccent) }
                                    item { LockedStrategyCardEmi("Lowest EMI", Icons.Rounded.ArrowDownward, goldAccent) } 
                                    item { LockedStrategyCardEmi("AI Recommended", Icons.Rounded.SmartToy, blueAccent) }
                                }
                            }
                        } else {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                Column(modifier = Modifier.weight(0.35f).height(110.dp), verticalArrangement = Arrangement.SpaceBetween) {
                                    Text("Unlock personalized strategies to save more and repay faster.", color = secondaryText, fontSize = 11.sp, lineHeight = 16.sp)
                                    Column {
                                        Button(
                                            onClick = { },
                                            colors = ButtonDefaults.buttonColors(containerColor = purpleAccent, contentColor = Color.White),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.fillMaxWidth().height(36.dp),
                                            contentPadding = PaddingValues(0.dp)
                                        ) {
                                            Icon(Icons.Rounded.PlayCircle, contentDescription=null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Watch Ad to Unlock", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Rounded.Lock, contentDescription=null, tint=secondaryText, modifier = Modifier.size(10.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Takes 15 seconds", color = secondaryText, fontSize = 10.sp)
                                        }
                                    }
                                }
                                
                                LazyRow(modifier = Modifier.weight(0.65f).height(110.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    item { LockedStrategyCardEmi("Best Savings", Icons.Rounded.EmojiEvents, greenAccent) }
                                    item { LockedStrategyCardEmi("Fastest Closure", Icons.Rounded.FlashOn, purpleAccent) }
                                    item { LockedStrategyCardEmi("Lowest EMI", Icons.Rounded.ArrowDownward, goldAccent) } 
                                    item { LockedStrategyCardEmi("AI Recommended", Icons.Rounded.SmartToy, blueAccent) }
                                }
                            }
                        }
                    }
                }
                
                // G. AMORTIZATION SCHEDULE
                Card(
                    modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                    colors = CardDefaults.cardColors(containerColor = primaryCard),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, borderColor)
                ) {
                    Column(modifier = Modifier.padding(vertical = 14.dp).fillMaxWidth()) {
                        Row(modifier = Modifier.fillMaxWidth().padding(horizontal=14.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Amortization Schedule", color = primaryText, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(Icons.Rounded.Info, contentDescription = null, tint = secondaryText, modifier = Modifier.size(14.dp))
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("View Full Schedule", color = blueAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = null, tint = blueAccent, modifier = Modifier.size(16.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(modifier = Modifier.fillMaxWidth().padding(horizontal=14.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Year", color = secondaryText, fontSize = 10.sp, modifier = Modifier.weight(0.5f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text("EMI (₹)", color = secondaryText, fontSize = 10.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Center, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text("Principal (₹)", color = secondaryText, fontSize = 10.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Center, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text("Interest (₹)", color = secondaryText, fontSize = 10.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Center, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text("Balance (₹)", color = secondaryText, fontSize = 10.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.End, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = borderColor)
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Column(modifier = Modifier.fillMaxWidth().padding(horizontal=14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Mock schedule values
                            ScheduleRowEmi("1", "23,754", "1,25,054", "1,12,700", "13,74,946", primaryText)
                            ScheduleRowEmi("2", "23,754", "1,34,613", "1,09,141", "12,40,333", primaryText)
                            ScheduleRowEmi("3", "23,754", "1,44,923", "98,831", "10,95,410", primaryText)
                            ScheduleRowEmi("...", "...", "...", "...", "...", blueAccent)
                            ScheduleRowEmi("${if (totalMonths >= 12) totalMonths / 12 else 1}", "23,754", "23,430", "324", "0", blueAccent)
                        }
                    }
                }
    
                // H. ACTION SECTION
                Row(modifier = Modifier.fillMaxWidth().height(60.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ActionCardEmi(
                        modifier = Modifier.weight(1f), 
                        title = "PDF Report", 
                        subtitle = "Download full report", 
                        icon = Icons.Rounded.PictureAsPdf, 
                        iconTint = purpleAccent,
                        bg = primaryCard,
                        border = borderColor
                    )
                    ActionCardEmi(
                        modifier = Modifier.weight(1f), 
                        title = "Share Result", 
                        subtitle = "Send summary to others", 
                        icon = Icons.Rounded.Share, 
                        iconTint = greenAccent,
                        bg = primaryCard,
                        border = borderColor
                    )
                }
            } // END HAS VALID INPUT BLOCK

        }
    }
}

@Composable
fun PremiumInputFieldEmi(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorMessage: String = "",
    trailingContent: @Composable (() -> Unit)? = null,
    readOnly: Boolean = false,
    onClick: (() -> Unit)? = null,
    fontSize: androidx.compose.ui.unit.TextUnit = 18.sp
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(label, color = Color(0xFFA8B3D1), fontSize = 12.sp, maxLines = 1)
            if (isError && errorMessage.isNotEmpty()) {
                Text(errorMessage, color = Color(0xFFEF4444), fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
        val textFieldModifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .let { if (onClick != null) it.clickable(interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }, indication = null) { onClick() } else it }
            
        BasicTextField(
            value = if(value == "0") "" else value,
            onValueChange = onValueChange,
            readOnly = readOnly,
            enabled = onClick == null,
            textStyle = androidx.compose.material3.LocalTextStyle.current.copy(
                textAlign = androidx.compose.ui.text.style.TextAlign.Start,
                color = Color.White,
                fontSize = fontSize,
                fontWeight = FontWeight.SemiBold
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            maxLines = 1,
            modifier = textFieldModifier,
            cursorBrush = androidx.compose.ui.graphics.SolidColor(Color.White),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF071833), RoundedCornerShape(12.dp))
                        .border(1.dp, if (isError) Color(0xFFEF4444) else Color(0xFF183C8A), RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                            if (label == "Loan Amount" && value.isNotEmpty() && value != "0") {
                                Text("₹", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                                if (value.isEmpty() || value == "0") Text(placeholder, color = Color.Gray, fontSize = fontSize)
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    innerTextField()
                                }
                            }
                        }
                        if (trailingContent != null) {
                            Spacer(modifier = Modifier.width(8.dp))
                            trailingContent()
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun HeroMetricEmi(modifier: Modifier = Modifier, title: String, value: String, color: Color) {
    Column(modifier = modifier) {
        Text(title, color = Color(0xFFA8B3D1), fontSize = 9.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Spacer(modifier = Modifier.height(4.dp))
        var scaledFontSize by remember(value) { mutableStateOf(13.sp) }
        Text(
            text = value,
            color = color,
            fontSize = scaledFontSize,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Visible,
            softWrap = false,
            onTextLayout = { result ->
                if (result.hasVisualOverflow && scaledFontSize > 8.sp) {
                    scaledFontSize = (scaledFontSize.value - 1f).sp
                }
            }
        )
    }
}

@Composable
fun LegendItemDonutEmi(color: Color, title: String, value: String, percent: String) {
    Row(verticalAlignment = Alignment.Top) {
        Box(modifier = Modifier.padding(top=4.dp).size(8.dp).background(color, CircleShape))
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(title, color = Color.White, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            var scaledFontSize by remember(value) { mutableStateOf(12.sp) }
            Text(
                text = value,
                color = Color(0xFFA8B3D1),
                fontSize = scaledFontSize,
                maxLines = 1,
                overflow = TextOverflow.Visible,
                softWrap = false,
                onTextLayout = { result ->
                    if (result.hasVisualOverflow && scaledFontSize > 8.sp) {
                        scaledFontSize = (scaledFontSize.value - 1f).sp
                    }
                }
            )
            Text(percent, color = Color(0xFFA8B3D1), fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
fun LockedStrategyCardEmi(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, iconTint: Color) {
    Card(
        modifier = Modifier.fillMaxHeight().width(80.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF040A1A)),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFF183C8A).copy(alpha=0.5f))
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceBetween) {
            Box(modifier = Modifier.size(24.dp).background(iconTint.copy(alpha=0.15f), CircleShape), contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription=null, tint=iconTint, modifier = Modifier.size(14.dp))
            }
            Text(title, color = Color.White, fontSize = 10.sp, textAlign = TextAlign.Center, lineHeight = 12.sp)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Rounded.Lock, contentDescription=null, tint=Color.Gray, modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.height(2.dp))
                Text("Locked", color = Color.Gray, fontSize = 9.sp)
            }
        }
    }
}

@Composable
fun ScheduleRowEmi(col1: String, col2: String, col3: String, col4: String, col5: String, color: Color) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(col1, color = color, fontSize = 12.sp, modifier = Modifier.weight(0.5f), maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(col2, color = color, fontSize = 12.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Center, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(col3, color = color, fontSize = 12.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Center, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(col4, color = color, fontSize = 12.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Center, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(col5, color = color, fontSize = 12.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.End, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
fun ActionCardEmi(modifier: Modifier = Modifier, title: String, subtitle: String, icon: androidx.compose.ui.graphics.vector.ImageVector, iconTint: Color, bg: Color, border: Color) {
    Card(
        modifier = modifier.fillMaxHeight(),
        colors = CardDefaults.cardColors(containerColor = bg),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, border)
    ) {
        Row(modifier = Modifier.padding(12.dp).fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(36.dp).background(iconTint.copy(alpha=0.15f), CircleShape), contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
                Text(title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(subtitle, color = Color(0xFFA8B3D1), fontSize = 9.sp, lineHeight = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
fun PrincipalCardEmi(
    modifier: Modifier,
    totalPrincipal: Double,
    totalPayment: Double,
    totalInterest: Double,
    formatMoney: (Double) -> String
) {
    val primaryCard = Color(0xFF061633)
    val borderColor = Color(0xFF183C8A)
    val primaryText = Color(0xFFFFFFFF)
    val secondaryText = Color(0xFFA8B3D1)
    val blueAccent = Color(0xFF2D7DFF)
    val goldAccent = Color(0xFFFFC328)

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = primaryCard),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Principal vs Interest", color = primaryText, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.width(6.dp))
                Icon(Icons.Rounded.Info, contentDescription = null, tint = secondaryText, modifier = Modifier.size(14.dp))
            }
            Spacer(Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.weight(0.42f), contentAlignment = Alignment.Center) {
                    val prinPct = if (totalPayment > 0) (totalPrincipal / totalPayment).toFloat() else 0f
                    Canvas(modifier = Modifier.size(92.dp)) {
                        drawArc(color = blueAccent, startAngle = -90f, sweepAngle = 360f * prinPct, useCenter = false, style = Stroke(width = 34f, cap = StrokeCap.Butt))
                        drawArc(color = goldAccent, startAngle = -90f + (360f * prinPct), sweepAngle = 360f * (1f - prinPct), useCenter = false, style = Stroke(width = 34f, cap = StrokeCap.Butt))
                    }
                    // Center text
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${(prinPct * 100).toInt()}%", color = primaryText, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text("Principal", color = secondaryText, fontSize = 10.sp)
                    }
                }
                Column(modifier = Modifier.weight(0.58f).padding(start = 8.dp)) {
                    val prinPctStr = String.format(Locale.US, "%.1f", if (totalPayment > 0) (totalPrincipal / totalPayment) * 100 else 0.0)
                    val intPctStr = String.format(Locale.US, "%.1f", if (totalPayment > 0) (totalInterest / totalPayment) * 100 else 0.0)
                    LegendItemDonutEmi(blueAccent, "Principal", formatMoney(totalPrincipal), "($prinPctStr%)")
                    Spacer(Modifier.height(14.dp))
                    LegendItemDonutEmi(goldAccent, "Interest", formatMoney(totalInterest), "($intPctStr%)")
                }
            }
        }
    }
}

@Composable
fun RateImpactAnalysisCardEmi(
    modifier: Modifier,
    loanAmount: Double,
    totalMonths: Int,
    currentRate: Double,
    formatMoney: (Double) -> String,
    currentMonthlyEmi: Double
) {
    val primaryCard = Color(0xFF061633)
    val borderColor = Color(0xFF183C8A)
    val primaryText = Color(0xFFFFFFFF)
    val secondaryText = Color(0xFFA8B3D1)
    val blueAccent = Color(0xFF2D7DFF)
    val greenAccent = Color(0xFF22C55E)
    val orangeAccent = Color(0xFFF97316)

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = primaryCard),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Rate Impact Analysis", color = primaryText, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.width(6.dp))
                Icon(Icons.Rounded.Info, contentDescription = null, tint = secondaryText, modifier = Modifier.size(14.dp))
            }
            Spacer(Modifier.height(10.dp))

            val rates = listOf(
                currentRate - 0.5 to "Lower Rate",
                currentRate to "Current Rate",
                currentRate + 0.5 to "Higher Rate",
                currentRate + 1.0 to "Higher Rate"
            ).filter { it.first > 0 }

            rates.forEachIndexed { index, (rate, label) ->
                val isCurrent = rate == currentRate
                val emiAtRate = if (loanAmount > 0 && rate > 0) {
                    val mr = rate / 12 / 100
                    loanAmount * (mr * (1 + mr).pow(totalMonths)) / ((1 + mr).pow(totalMonths) - 1)
                } else 0.0
                val diff = emiAtRate - currentMonthlyEmi

                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("${"%.2f".format(Locale.US, rate)}%", color = primaryText, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            if (isCurrent) {
                                Spacer(Modifier.width(6.dp))
                                Surface(color = blueAccent.copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp), border = BorderStroke(0.5.dp, blueAccent)) {
                                    Text("Current", color = blueAccent, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                }
                            }
                        }
                        Text(formatMoney(emiAtRate), color = primaryText, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                    if (!isCurrent) {
                        val diffStr = formatMoney(Math.abs(diff))
                        if (diff < 0) {
                            Text("Save $diffStr/mo", color = greenAccent, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        } else {
                            Text("Cost $diffStr/mo more", color = orangeAccent, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
                if (index < rates.lastIndex) {
                    HorizontalDivider(color = borderColor.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 4.dp))
                }
            }
        }
    }
}

@Composable
fun AutoResizeHeroTextEmi(text: String, color: Color) {
    var scaledFontSize by remember(text) { mutableStateOf(36.sp) }
    Text(
        text = text,
        color = color,
        fontSize = scaledFontSize,
        fontWeight = FontWeight.Bold,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        softWrap = false,
        onTextLayout = { result ->
            if (result.hasVisualOverflow && scaledFontSize > 16.sp) {
                scaledFontSize = (scaledFontSize.value - 2f).sp
            }
        }
    )
}
