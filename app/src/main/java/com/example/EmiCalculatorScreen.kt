package com.example

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.pow

// ==================== DATA CLASSES ====================
data class YearBreakdown(
    val year: Int,
    val emi: Double,
    val principalPaid: Double,
    val interestPaid: Double,
    val remainingBalance: Double
)

data class MonthlyAmortization(
    val month: Int,
    val emi: Double,
    val principalPaid: Double,
    val interestPaid: Double,
    val remainingBalance: Double
)

// ==================== CALCULATION FUNCTIONS ====================
fun calculateEMI(principal: Double, annualRate: Double, months: Int): Double {
    if (principal <= 0 || annualRate <= 0 || months <= 0) return 0.0
    val r = annualRate / 12 / 100
    return principal * r * (1 + r).pow(months) / ((1 + r).pow(months) - 1)
}

fun getYearWiseBreakdown(
    principal: Double,
    annualRate: Double,
    totalMonths: Int
): List<YearBreakdown> {
    val monthlyRate = annualRate / 12 / 100
    val emi = calculateEMI(principal, annualRate, totalMonths)
    val breakdown = mutableListOf<YearBreakdown>()
    var balance = principal

    for (year in 1..(totalMonths / 12)) {
        var yearlyPrincipal = 0.0
        var yearlyInterest = 0.0

        repeat(12) {
            val interest = balance * monthlyRate
            val principalPaid = emi - interest
            yearlyInterest += interest
            yearlyPrincipal += principalPaid
            balance -= principalPaid
        }

        breakdown.add(
            YearBreakdown(
                year = year,
                emi = emi,
                principalPaid = yearlyPrincipal,
                interestPaid = yearlyInterest,
                remainingBalance = balance.coerceAtLeast(0.0)
            )
        )
    }
    return breakdown
}
// ==================== INPUT FIELD ====================
@Composable
fun PremiumInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    trailingIcon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    suffix: String = "",
    inputBg: Color,
    borderColor: Color,
    secondaryText: Color
) {
    Column {
        Text(label, color = secondaryText, fontSize = 12.sp)
        Spacer(Modifier.height(6.dp))
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = inputBg,
            border = BorderStroke(1.dp, borderColor)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(10.dp))
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    textStyle = TextStyle(color = Color.White, fontSize = 15.sp),
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                if (suffix.isNotEmpty()) Text(suffix, color = Color(0xFF9AA6C8), fontSize = 13.sp)
                trailingIcon?.let {
                    Icon(imageVector = it, contentDescription = null, tint = Color(0xFF9AA6C8), modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

// ==================== FULL AMORTIZATION DIALOG ====================
@Composable
fun FullAmortizationDialog(
    principal: Double,
    annualRate: Double,
    totalMonths: Int,
    onDismiss: () -> Unit
) {
    val monthlyRate = annualRate / 12 / 100
    val emi = calculateEMI(principal, annualRate, totalMonths)

    val schedule = remember(principal, annualRate, totalMonths) {
        val list = mutableListOf<MonthlyAmortization>()
        var balance = principal

        for (month in 1..totalMonths) {
            val interest = balance * monthlyRate
            val principalPaid = emi - interest
            balance = (balance - principalPaid).coerceAtLeast(0.0)

            list.add(
                MonthlyAmortization(
                    month = month,
                    emi = emi,
                    principalPaid = principalPaid,
                    interestPaid = interest,
                    remainingBalance = balance
                )
            )
        }
        list
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 520.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF061633))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Full Monthly Amortization Schedule",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(12.dp))

                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(
                        items = schedule,
                        key = { it.month }
                    ) { row ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("M${row.month}", color = Color(0xFFA8B3D1), fontSize = 12.sp)
                            Text(formatMoney(row.emi), color = Color.White, fontSize = 12.sp)
                            Text(formatMoney(row.principalPaid), color = Color(0xFF22C55E), fontSize = 12.sp)
                            Text(formatMoney(row.interestPaid), color = Color(0xFFFFC328), fontSize = 12.sp)
                            Text(formatMoney(row.remainingBalance), color = Color.White, fontSize = 12.sp)
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D7DFF))
                ) {
                    Text("Close")
                }
            }
        }
    }
}
// ==================== MAIN SCREEN ====================
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

    // State - Starts with Zeros
    var loanAmountText by remember { mutableStateOf("0") }
    var interestRateText by remember { mutableStateOf("0") }
    var tenureYearsText by remember { mutableStateOf("0") }
    var firstEmiDate by remember { mutableStateOf("Select Date") }
    var showFullSchedule by remember { mutableStateOf(false) }

    val loanAmount = loanAmountText.toDoubleOrNull() ?: 0.0
    val interestRate = interestRateText.toDoubleOrNull() ?: 0.0
    val tenureYears = tenureYearsText.toIntOrNull() ?: 0
    val totalMonths = tenureYears * 12

    val hasValidInput = loanAmount > 0 && interestRate > 0 && tenureYears > 0

    val monthlyEmi = if (hasValidInput) calculateEMI(loanAmount, interestRate, totalMonths) else 0.0
    val totalPayment = monthlyEmi * totalMonths
    val totalInterest = totalPayment - loanAmount
    val totalPrincipal = loanAmount

    // Animated EMI
    val animatedEmi by animateFloatAsState(
        targetValue = if (hasValidInput) monthlyEmi.toFloat() else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "Animated EMI"
    )

    val formatMoney = { amt: Double ->
        val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
        format.maximumFractionDigits = 0
        format.format(amt)
    }

    Scaffold(
        containerColor = bgColor,
        bottomBar = {
            NavigationBar(containerColor = bgColor) {
                val items = listOf("Home", "History", "Calculate", "Compare", "Settings")
                items.forEachIndexed { index, label ->
                    NavigationBarItem(
                        selected = index == 2,
                        onClick = {},
                        icon = {
                            Icon(
                                imageVector = when (index) {
                                    0 -> Icons.Rounded.Home
                                    1 -> Icons.Rounded.History
                                    2 -> Icons.Rounded.Calculate
                                    3 -> Icons.Rounded.CompareArrows
                                    else -> Icons.Rounded.Settings
                                },
                                contentDescription = label
                            )
                        },
                        label = { Text(label, fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = goldAccent,
                            selectedTextColor = goldAccent,
                            indicatorColor = Color.Transparent
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // HEADER
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Back",
                    tint = primaryText,
                    modifier = Modifier.clickable { onNavigateBack() }
                )
                Spacer(Modifier.width(16.dp))
                Column(Modifier.weight(1f)) {
                    Text("EMI Calculator", color = primaryText, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("Calculate your loan EMI and plan better", color = secondaryText, fontSize = 11.sp)
                }
                Icon(imageVector = Icons.Rounded.StarBorder, contentDescription = null, tint = goldAccent, modifier = Modifier.size(24.dp))
                Spacer(Modifier.width(16.dp))
                Icon(imageVector = Icons.Rounded.Share, contentDescription = null, tint = primaryText, modifier = Modifier.size(22.dp))
            }

            // INPUT SECTION
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = primaryCard),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, borderColor)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        PremiumInputField(label = "Loan Amount", value = loanAmountText, onValueChange = { loanAmountText = it }, icon = Icons.Rounded.AccountBalanceWallet, iconTint = blueAccent, inputBg = inputBg, borderColor = borderColor, secondaryText = secondaryText)
                        PremiumInputField(label = "Interest Rate (p.a.)", value = interestRateText, onValueChange = { interestRateText = it }, icon = Icons.Rounded.Percent, iconTint = blueAccent, inputBg = inputBg, borderColor = borderColor, secondaryText = secondaryText)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        PremiumInputField(label = "Tenure", value = tenureYearsText, onValueChange = { tenureYearsText = it }, icon = Icons.Rounded.DateRange, iconTint = blueAccent, trailingIcon = Icons.Rounded.KeyboardArrowDown, suffix = " Years", inputBg = inputBg, borderColor = borderColor, secondaryText = secondaryText)
                        PremiumInputField(label = "First EMI Date (Optional)", value = firstEmiDate, onValueChange = {}, icon = Icons.Rounded.DateRange, iconTint = blueAccent, trailingIcon = Icons.Rounded.KeyboardArrowDown, inputBg = inputBg, borderColor = borderColor, secondaryText = secondaryText)
                    }
                    Text("Leave blank to use current month", color = secondaryText, fontSize = 9.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                }
            }

            // PLACEHOLDER
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
                        Icon(Icons.Rounded.Calculate, contentDescription = null, tint = secondaryText.copy(0.5f), modifier = Modifier.size(48.dp))
                        Spacer(Modifier.height(12.dp))
                        Text("Enter loan amount, rate & tenure to see results", color = secondaryText, fontSize = 14.sp, textAlign = TextAlign.Center)
                    }
                }
            }

            // ANIMATED RESULTS
            AnimatedVisibility(
                visible = hasValidInput,
                enter = fadeIn(tween(400)) + scaleIn(initialScale = 0.95f, animationSpec = tween(400))
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {

                    // HERO EMI CARD
                    Card(
                        modifier = Modifier.fillMaxWidth().shadow(16.dp, spotColor = blueAccent.copy(0.4f)),
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(1.dp, borderColor)
                    ) {
                        Box(modifier = Modifier.background(Brush.linearGradient(listOf(inputBg, Color(0xFF0A2150))))) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Column {
                                        Text("Your Monthly EMI", color = secondaryText, fontSize = 13.sp)
                                        Text(formatMoney(animatedEmi.toDouble()), color = blueAccent, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Box(modifier = Modifier.size(78.dp), contentAlignment = Alignment.Center) {
                                        Icon(imageVector = Icons.Rounded.CalendarMonth, contentDescription = null, tint = blueAccent, modifier = Modifier.size(60.dp))
                                        Icon(imageVector = Icons.Rounded.CurrencyRupee, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp).offset(y = 3.dp))
                                        Icon(imageVector = Icons.Rounded.MonetizationOn, contentDescription = null, tint = goldAccent, modifier = Modifier.size(26.dp).align(Alignment.BottomStart).offset(x = -6.dp, y = 6.dp))
                                        Icon(imageVector = Icons.Rounded.BarChart, contentDescription = null, tint = greenAccent, modifier = Modifier.size(30.dp).align(Alignment.BottomEnd).offset(x = 10.dp, y = 8.dp))
                                    }
                                }
                                Spacer(Modifier.height(16.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Column { Text("Total Interest Payable", color = secondaryText, fontSize = 10.sp); Text(formatMoney(totalInterest), color = greenAccent, fontSize = 14.sp, fontWeight = FontWeight.Bold) }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) { Text("Total Payment", color = secondaryText, fontSize = 10.sp); Text(formatMoney(totalPayment), color = primaryText, fontSize = 14.sp, fontWeight = FontWeight.Bold); Text("(Principal + Interest)", color = secondaryText, fontSize = 9.sp) }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) { Text("Total Principal", color = secondaryText, fontSize = 10.sp); Text(formatMoney(totalPrincipal), color = purpleAccent, fontSize = 14.sp, fontWeight = FontWeight.Bold) }
                                }
                            }
                        }
                    }

                    // PRINCIPAL VS INTEREST + RATE COMPARISON
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        // Donut Card
                        Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = primaryCard), shape = RoundedCornerShape(18.dp), border = BorderStroke(1.dp, borderColor)) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("Principal vs Interest", color = primaryText, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                                Spacer(Modifier.height(12.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.weight(0.4f), contentAlignment = Alignment.Center) {
                                        val prinPct = if (totalPayment > 0) (totalPrincipal / totalPayment).toFloat() else 0f
                                        Canvas(modifier = Modifier.size(90.dp)) {
                                            drawArc(color = blueAccent, startAngle = -90f, sweepAngle = 360f * prinPct, useCenter = false, style = Stroke(width = 32f, cap = StrokeCap.Butt))
                                            drawArc(color = goldAccent, startAngle = -90f + (360f * prinPct), sweepAngle = 360f * (1 - prinPct), useCenter = false, style = Stroke(width = 32f, cap = StrokeCap.Butt))
                                        }
                                    }
                                    Column(modifier = Modifier.weight(0.6f)) {
                                        val prinPctStr = if (totalPayment > 0) String.format(Locale.US, "%.1f", (totalPrincipal / totalPayment) * 100) else "0.0"
                                        val intPctStr = if (totalPayment > 0) String.format(Locale.US, "%.1f", (totalInterest / totalPayment) * 100) else "0.0"
                                        Row(verticalAlignment = Alignment.CenterVertically) { Box(Modifier.size(10.dp).background(blueAccent, CircleShape)); Spacer(Modifier.width(8.dp)); Text("Principal  ${formatMoney(totalPrincipal)} ($prinPctStr%)", color = primaryText, fontSize = 12.sp) }
                                        Spacer(Modifier.height(8.dp))
                                        Row(verticalAlignment = Alignment.CenterVertically) { Box(Modifier.size(10.dp).background(goldAccent, CircleShape)); Spacer(Modifier.width(8.dp)); Text("Interest    ${formatMoney(totalInterest)} ($intPctStr%)", color = primaryText, fontSize = 12.sp) }
                                    }
                                }
                            }
                        }

                        // Rate Comparison
                        Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = primaryCard), shape = RoundedCornerShape(18.dp), border = BorderStroke(1.dp, borderColor)) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("Interest Rate Comparison", color = primaryText, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                                Spacer(Modifier.height(8.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Interest Rate (p.a.)", color = secondaryText, fontSize = 11.sp)
                                    Text("Monthly EMI", color = secondaryText, fontSize = 11.sp)
                                }
                                Spacer(Modifier.height(6.dp))
                                HorizontalDivider(color = borderColor)
                                listOf(8.0, 8.5, 9.0, 9.5).forEach { rate ->
                                    val isCurrent = rate == 8.5
                                    val emiAtRate = if (hasValidInput) calculateEMI(loanAmount, rate, totalMonths) else 0.0
                                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp).background(if (isCurrent) Color(0xFF1A3A6E) else Color.Transparent, RoundedCornerShape(6.dp)).padding(horizontal = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("\( {rate}% \){if (isCurrent) " (Current)" else ""}", color = if (isCurrent) goldAccent else primaryText, fontSize = 13.sp, fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal)
                                        Text(formatMoney(emiAtRate), color = if (isCurrent) goldAccent else primaryText, fontSize = 13.sp, fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal)
                                    }
                                }
                            }
                        }
                    }
// SMART RECOMMENDATIONS
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = primaryCard), shape = RoundedCornerShape(18.dp), border = BorderStroke(1.dp, borderColor)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row {
                                Text("Smart Recommendations", color = primaryText, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                                Spacer(Modifier.width(8.dp))
                                Surface(color = Color(0xFF3B2A6E), shape = RoundedCornerShape(20.dp)) { Text("4 Strategies", color = Color(0xFFB39DFF), fontSize = 11.sp, modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp)) }
                            }
                            Text("Potential savings up to ₹6.4L", color = greenAccent, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(10.dp))
                            Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6B4EFF)), shape = RoundedCornerShape(50.dp), modifier = Modifier.fillMaxWidth()) {
                                Icon(Icons.Rounded.PlayArrow, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("Watch Ad To Unlock")
                            }
                            Text("Takes 15 seconds", color = secondaryText, fontSize = 10.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
                            Spacer(Modifier.height(12.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf("Best Savings", "Fastest Closure", "Lowest EMI", "AI Recommended").forEach { title ->
                                    Column(modifier = Modifier.weight(1f).background(Color(0xFF0F2744), RoundedCornerShape(12.dp)).padding(vertical = 10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Rounded.Lock, contentDescription = null, tint = Color(0xFF888888), modifier = Modifier.size(20.dp))
                                        Spacer(Modifier.height(6.dp))
                                        Text(title, color = primaryText, fontSize = 11.sp, textAlign = TextAlign.Center)
                                    }
                                }
                            }
                        }
                    }

                    // AMORTIZATION SCHEDULE
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = primaryCard), shape = RoundedCornerShape(18.dp), border = BorderStroke(1.dp, borderColor)) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Amortization Schedule", color = primaryText, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                                Text("View Full Schedule ›", color = blueAccent, fontSize = 12.sp, modifier = Modifier.clickable { showFullSchedule = true })
                            }
                            Spacer(Modifier.height(8.dp))

                            val scheduleData = getYearWiseBreakdown(loanAmount, interestRate, totalMonths).take(3) + listOf(getYearWiseBreakdown(loanAmount, interestRate, totalMonths).last())

                            scheduleData.forEachIndexed { index, row ->
                                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Year ${row.year}", color = if (index == scheduleData.lastIndex) secondaryText else primaryText, fontSize = 12.sp, modifier = Modifier.weight(1f))
                                    Text(formatMoney(row.emi), color = primaryText, fontSize = 12.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                                    Text(formatMoney(row.principalPaid), color = Color(0xFF22C55E), fontSize = 12.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                                    Text(formatMoney(row.interestPaid), color = Color(0xFFFFC328), fontSize = 12.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                                }
                                if (index < scheduleData.lastIndex) HorizontalDivider(color = borderColor.copy(alpha = 0.4f))
                            }
                        }
                    }

                    // PDF + SHARE
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(onClick = {}, modifier = Modifier.weight(1f).height(50.dp), shape = RoundedCornerShape(14.dp), border = BorderStroke(1.dp, borderColor)) {
                            Icon(Icons.Rounded.PictureAsPdf, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("PDF Report")
                        }
                        Button(onClick = {}, modifier = Modifier.weight(1f).height(50.dp), shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F2744))) {
                            Icon(Icons.Rounded.Share, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Share Result")
                        }
                    }
                }
            }
        }
    }

    // FULL SCHEDULE DIALOG
    if (showFullSchedule && hasValidInput) {
        FullAmortizationDialog(
            principal = loanAmount,
            annualRate = interestRate,
            totalMonths = totalMonths,
            onDismiss = { showFullSchedule = false }
        )
    }
}