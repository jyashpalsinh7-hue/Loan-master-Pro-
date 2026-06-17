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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.LocalConfiguration
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

// ==================== HELPER FUNCTIONS ====================
fun formatMoney(amt: Double): String {
    if (amt <= 0) return "₹0"
    val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    format.maximumFractionDigits = 0
    return format.format(amt)
}

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
    if (principal <= 0 || annualRate <= 0 || totalMonths <= 0) return emptyList()

    val monthlyRate = annualRate / 12 / 100
    val emi = calculateEMI(principal, annualRate, totalMonths)
    val breakdown = mutableListOf<YearBreakdown>()
    var balance = principal
    val fullYears = totalMonths / 12

    for (year in 1..fullYears) {
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

    val remainingMonths = totalMonths % 12
    if (remainingMonths > 0 && breakdown.isNotEmpty()) {
        var yearlyPrincipal = 0.0
        var yearlyInterest = 0.0

        repeat(remainingMonths) {
            val interest = balance * monthlyRate
            val principalPaid = emi - interest
            yearlyInterest += interest
            yearlyPrincipal += principalPaid
            balance -= principalPaid
        }

        breakdown.add(
            YearBreakdown(
                year = fullYears + 1,
                emi = emi,
                principalPaid = yearlyPrincipal,
                interestPaid = yearlyInterest,
                remainingBalance = balance.coerceAtLeast(0.0)
            )
        )
    }

    return breakdown
}

// ==================== REUSABLE INPUT FIELD ====================
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
        Text(label, color = secondaryText, fontSize = 11.sp)
        Spacer(Modifier.height(4.dp))
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = inputBg,
            border = BorderStroke(1.dp, borderColor)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
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
                if (suffix.isNotEmpty()) {
                    Text(suffix, color = Color(0xFF9AA6C8), fontSize = 13.sp)
                }
                trailingIcon?.let {
                    Icon(imageVector = it, contentDescription = null, tint = Color(0xFF9AA6C8), modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

// ==================== LOAN TYPE SELECTOR ====================
@Composable
fun LoanTypeSelector(
    selectedType: String,
    onTypeSelected: (String) -> Unit,
    inputBg: Color,
    borderColor: Color,
    secondaryText: Color
) {
    Column {
        Text("Loan Type", color = secondaryText, fontSize = 12.sp)
        Spacer(Modifier.height(6.dp))
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = inputBg,
            border = BorderStroke(1.dp, borderColor),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { /* TODO: Open Loan Type Bottom Sheet */ }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 11.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.Home,
                    contentDescription = null,
                    tint = Color(0xFF22C55E),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(selectedType, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                    Text("Required for accurate insights", color = Color(0xFF9AA6C8), fontSize = 11.sp)
                }
                Icon(
                    imageVector = Icons.Rounded.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color(0xFF9AA6C8),
                    modifier = Modifier.size(20.dp)
                )
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
                .heightIn(max = 580.dp),
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
                Text(
                    "${totalMonths} months • EMI: ${formatMoney(emi)}",
                    color = Color(0xFFA8B3D1),
                    fontSize = 13.sp
                )

                Spacer(Modifier.height(16.dp))

                // Column Headers
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0A2150), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Month", color = Color(0xFF7C8DB5), fontSize = 11.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(0.9f))
                    Text("EMI", color = Color(0xFF7C8DB5), fontSize = 11.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1.1f), textAlign = TextAlign.End)
                    Text("Principal", color = Color(0xFF7C8DB5), fontSize = 11.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1.2f), textAlign = TextAlign.End)
                    Text("Interest", color = Color(0xFF7C8DB5), fontSize = 11.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1.1f), textAlign = TextAlign.End)
                    Text("Balance", color = Color(0xFF7C8DB5), fontSize = 11.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1.2f), textAlign = TextAlign.End)
                }

                Spacer(Modifier.height(4.dp))

                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(items = schedule, key = { it.month }) { row ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp, vertical = 7.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("M${row.month}", color = Color(0xFFA8B3D1), fontSize = 12.sp, modifier = Modifier.weight(0.9f))
                            Text(formatMoney(row.emi), color = Color.White, fontSize = 12.sp, modifier = Modifier.weight(1.1f), textAlign = TextAlign.End)
                            Text(formatMoney(row.principalPaid), color = Color(0xFF22C55E), fontSize = 12.sp, modifier = Modifier.weight(1.2f), textAlign = TextAlign.End)
                            Text(formatMoney(row.interestPaid), color = Color(0xFFFFC328), fontSize = 12.sp, modifier = Modifier.weight(1.1f), textAlign = TextAlign.End)
                            Text(formatMoney(row.remainingBalance), color = Color.White, fontSize = 12.sp, modifier = Modifier.weight(1.2f), textAlign = TextAlign.End)
                        }
                        if (row.month < schedule.size) {
                            HorizontalDivider(color = Color(0xFF183C8A).copy(alpha = 0.3f), thickness = 0.5.dp)
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFF183C8A))
                    ) {
                        Text("Close", color = Color.White)
                    }
                    Button(
                        onClick = { /* TODO: Export CSV */ },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D7DFF)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Rounded.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Export CSV")
                    }
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

    // ==================== RESPONSIVE BREAKPOINTS (Phase 1) ====================
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp

    val isCompact = screenWidth < 360
    val isMedium = screenWidth in 360..599
    val isTablet = screenWidth >= 600

    // State with new defaults as per approved design
    var loanAmountText by remember { mutableStateOf("") }
    var interestRateText by remember { mutableStateOf("") }
    var tenureYearsText by remember { mutableStateOf("") }
    var loanType by remember { mutableStateOf("Home Loan") }
    var showFullSchedule by remember { mutableStateOf(false) }

    // ==================== BOTTOM SHEET STATE ====================
    var showRecommendationSheet by remember { mutableStateOf(false) }
    var selectedRecommendation by remember { mutableStateOf<String?>(null) }

    val loanAmount = loanAmountText.toDoubleOrNull() ?: 0.0
    val interestRate = interestRateText.toDoubleOrNull() ?: 0.0
    val tenureYears = tenureYearsText.toIntOrNull() ?: 0
    val totalMonths = tenureYears * 12

    val hasValidInput = loanAmount > 0 && interestRate > 0 && tenureYears > 0

    val monthlyEmi = if (hasValidInput) calculateEMI(loanAmount, interestRate, totalMonths) else 0.0
    val totalPayment = monthlyEmi * totalMonths
    val totalInterest = totalPayment - loanAmount
    val totalPrincipal = loanAmount

    val animatedEmi by animateFloatAsState(
        targetValue = if (hasValidInput) monthlyEmi.toFloat() else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "Animated EMI"
    )

    val yearBreakdown = remember(loanAmount, interestRate, totalMonths) {
        getYearWiseBreakdown(loanAmount, interestRate, totalMonths)
    }

    val prinPct = if (totalPayment > 0) (totalPrincipal / totalPayment) * 100 else 0.0
    val intPct = if (totalPayment > 0) (totalInterest / totalPayment) * 100 else 0.0

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
        // ==================== TABLET-FRIENDLY WRAPPER (Phase 1) ====================
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .widthIn(max = 700.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
            // HEADER
            item {
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
            }
            }   // end item Header

            // INPUT SECTION
            item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = primaryCard),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, borderColor)
            ) {
                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {

                    // ==================== RESPONSIVE INPUT SECTION (Phase 2) ====================
                    if (isCompact) {
                        // Compact phones: 1 column (stacked)
                        PremiumInputField(
                            label = "Loan Amount",
                            value = loanAmountText,
                            onValueChange = { loanAmountText = it },
                            icon = Icons.Rounded.AccountBalanceWallet,
                            iconTint = blueAccent,
                            inputBg = inputBg,
                            borderColor = borderColor,
                            secondaryText = secondaryText
                        )
                        PremiumInputField(
                            label = "Interest Rate (p.a.)",
                            value = interestRateText,
                            onValueChange = { interestRateText = it },
                            icon = Icons.Rounded.Percent,
                            iconTint = blueAccent,
                            inputBg = inputBg,
                            borderColor = borderColor,
                            secondaryText = secondaryText
                        )
                        PremiumInputField(
                            label = "Tenure",
                            value = tenureYearsText,
                            onValueChange = { tenureYearsText = it },
                            icon = Icons.Rounded.DateRange,
                            iconTint = blueAccent,
                            trailingIcon = Icons.Rounded.KeyboardArrowDown,
                            suffix = " Years",
                            inputBg = inputBg,
                            borderColor = borderColor,
                            secondaryText = secondaryText
                        )
                        LoanTypeSelector(
                            selectedType = loanType,
                            onTypeSelected = { loanType = it },
                            inputBg = inputBg,
                            borderColor = borderColor,
                            secondaryText = secondaryText
                        )
                    } else {
                        // Medium phones + Tablets: 2×2 grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                PremiumInputField(
                                    label = "Loan Amount",
                                    value = loanAmountText,
                                    onValueChange = { loanAmountText = it },
                                    icon = Icons.Rounded.AccountBalanceWallet,
                                    iconTint = blueAccent,
                                    inputBg = inputBg,
                                    borderColor = borderColor,
                                    secondaryText = secondaryText
                                )
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                PremiumInputField(
                                    label = "Interest Rate (p.a.)",
                                    value = interestRateText,
                                    onValueChange = { interestRateText = it },
                                    icon = Icons.Rounded.Percent,
                                    iconTint = blueAccent,
                                    inputBg = inputBg,
                                    borderColor = borderColor,
                                    secondaryText = secondaryText
                                )
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                PremiumInputField(
                                    label = "Tenure",
                                    value = tenureYearsText,
                                    onValueChange = { tenureYearsText = it },
                                    icon = Icons.Rounded.DateRange,
                                    iconTint = blueAccent,
                                    trailingIcon = Icons.Rounded.KeyboardArrowDown,
                                    suffix = " Years",
                                    inputBg = inputBg,
                                    borderColor = borderColor,
                                    secondaryText = secondaryText
                                )
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                LoanTypeSelector(
                                    selectedType = loanType,
                                    onTypeSelected = { loanType = it },
                                    inputBg = inputBg,
                                    borderColor = borderColor,
                                    secondaryText = secondaryText
                                )
                            }
                        }
                    }
                    Text(
                        "Loan type helps us provide accurate insights and better recommendations.",
                        color = secondaryText,
                        fontSize = 9.sp,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }
            }   // end item Input Section

            // PLACEHOLDER (smaller + "Load Example" button)
            item {
            if (!hasValidInput) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = primaryCard),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, borderColor)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Rounded.Calculate, contentDescription = null, tint = secondaryText.copy(0.6f), modifier = Modifier.size(32.dp))
                        Spacer(Modifier.height(8.dp))
                        Text("Enter loan amount, rate & tenure to see results", color = secondaryText, fontSize = 13.sp, textAlign = TextAlign.Center)
                        Spacer(Modifier.height(10.dp))
                        Button(
                            onClick = {
                                loanAmountText = "5000000"
                                interestRateText = "7"
                                tenureYearsText = "14"
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D7DFF)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text("Load Example Values", fontSize = 12.sp)
                        }
                    }
                }
            }
            }   // end item Placeholder

            // ANIMATED RESULTS
            item {
            AnimatedVisibility(
                visible = hasValidInput,
                enter = fadeIn(tween(400)) + scaleIn(initialScale = 0.95f, animationSpec = tween(400))
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

                    // ==================== HERO EMI CARD (Responsive - Phase 3) ====================
                    Card(
                        modifier = Modifier.fillMaxWidth().shadow(16.dp, spotColor = blueAccent.copy(0.4f)),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, borderColor)
                    ) {
                        Box(modifier = Modifier.background(Brush.linearGradient(listOf(inputBg, Color(0xFF0A2150))))) {
                            Column(modifier = Modifier.padding(18.dp)) {

                                if (isCompact) {
                                    // ==================== COMPACT: Vertical Layout ====================
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Your Monthly EMI", color = secondaryText, fontSize = 13.sp)
                                        Text(
                                            formatMoney(animatedEmi.toDouble()),
                                            color = blueAccent,
                                            fontSize = 32.sp,
                                            fontWeight = FontWeight.Bold
                                        )

                                        Spacer(Modifier.height(16.dp))

                                        // Donut
                                        Box(contentAlignment = Alignment.Center) {
                                            val donutSize = 100.dp
                                            val strokeWidth = 20f

                                            Canvas(modifier = Modifier.size(donutSize)) {
                                                val sweepPrincipal = (prinPct / 100f * 360f).toFloat()
                                                drawArc(color = blueAccent, startAngle = -90f, sweepAngle = sweepPrincipal, useCenter = false, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))
                                                drawArc(color = goldAccent, startAngle = -90f + sweepPrincipal, sweepAngle = (360f - sweepPrincipal), useCenter = false, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))
                                            }
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text("${prinPct.toInt()}%", color = blueAccent, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                                Text("Principal", color = secondaryText, fontSize = 10.sp)
                                            }
                                        }

                                        Spacer(Modifier.height(16.dp))

                                        // ==================== TOTALS INSIDE HERO CARD ====================
                                        Column(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text("Principal", color = secondaryText, fontSize = 14.sp)
                                                Text(formatMoney(totalPrincipal), color = purpleAccent, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                            }
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text("Interest", color = secondaryText, fontSize = 14.sp)
                                                Text(formatMoney(totalInterest), color = greenAccent, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                            }
                                            HorizontalDivider(color = borderColor.copy(alpha = 0.5f))
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text("Total Payment", color = primaryText, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                                                Text(formatMoney(totalPayment), color = primaryText, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }

                                } else {
                                    // ==================== MEDIUM + TABLET: Current Layout ====================
                                    // EMI Header
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text("Your Monthly EMI", color = secondaryText, fontSize = 13.sp)
                                            Text(
                                                formatMoney(animatedEmi.toDouble()),
                                                color = blueAccent,
                                                fontSize = 34.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Icon(
                                            imageVector = Icons.Rounded.CalendarMonth,
                                            contentDescription = null,
                                            tint = blueAccent.copy(alpha = 0.3f),
                                            modifier = Modifier.size(42.dp)
                                        )
                                    }

                                    Spacer(Modifier.height(20.dp))

                                    // Donut
                                    Box(
                                        modifier = Modifier.fillMaxWidth(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        val donutSize = 110.dp
                                        val strokeWidth = 22f

                                        Canvas(modifier = Modifier.size(donutSize)) {
                                            val sweepPrincipal = (prinPct / 100f * 360f).toFloat()
                                            drawArc(color = blueAccent, startAngle = -90f, sweepAngle = sweepPrincipal, useCenter = false, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))
                                            drawArc(color = goldAccent, startAngle = -90f + sweepPrincipal, sweepAngle = (360f - sweepPrincipal), useCenter = false, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))
                                        }

                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text("${prinPct.toInt()}%", color = blueAccent, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                                            Text("Principal", color = secondaryText, fontSize = 11.sp)
                                        }
                                    }

                                    Spacer(Modifier.height(8.dp))

                                    // Legend
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(Modifier.size(8.dp).background(blueAccent, CircleShape))
                                            Spacer(Modifier.width(6.dp))
                                            Text("Principal ${prinPct.toInt()}%", color = primaryText, fontSize = 13.sp)
                                        }
                                        Spacer(Modifier.width(20.dp))
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(Modifier.size(8.dp).background(goldAccent, CircleShape))
                                            Spacer(Modifier.width(6.dp))
                                            Text("Interest ${intPct.toInt()}%", color = primaryText, fontSize = 13.sp)
                                        }
                                    }

                                    Spacer(Modifier.height(20.dp))

                                    // ==================== TOTALS INSIDE HERO CARD ====================
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("Principal", color = secondaryText, fontSize = 14.sp)
                                            Text(formatMoney(totalPrincipal), color = purpleAccent, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("Interest", color = secondaryText, fontSize = 14.sp)
                                            Text(formatMoney(totalInterest), color = greenAccent, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                        }
                                        HorizontalDivider(color = borderColor.copy(alpha = 0.5f))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("Total Payment", color = primaryText, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                                            Text(formatMoney(totalPayment), color = primaryText, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // ==================== LOAN INSIGHTS (NEW) ====================
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = primaryCard),
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(1.dp, borderColor)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Loan Insights", color = primaryText, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                            }

                            Spacer(Modifier.height(16.dp))

                            // Transparency Score
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(64.dp)) {
                                    CircularProgressIndicator(
                                        progress = 0.92f,
                                        modifier = Modifier.size(58.dp),
                                        color = greenAccent,
                                        strokeWidth = 6.dp,
                                        trackColor = Color(0xFF1A2A4A)
                                    )
                                    Text("92", color = primaryText, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(Modifier.width(16.dp))
                                Column {
                                    Text("Transparency Score", color = secondaryText, fontSize = 13.sp)
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("🟢", fontSize = 16.sp)
                                        Spacer(Modifier.width(6.dp))
                                        Text("Excellent  92/100", color = greenAccent, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }

                            Spacer(Modifier.height(16.dp))
                            HorizontalDivider(color = borderColor.copy(alpha = 0.5f))
                            Spacer(Modifier.height(12.dp))

                            // Insights
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = greenAccent, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(10.dp))
                                    Text("EMI matches expected rate → ", color = primaryText, fontSize = 13.sp)
                                    Text("Excellent", color = greenAccent, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = greenAccent, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(10.dp))
                                    Text("Effective Rate (Approx.) 7.1% p.a. → ", color = primaryText, fontSize = 13.sp)
                                    Text("Very Close", color = goldAccent, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = greenAccent, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(10.dp))
                                    Text("Competitive for Home Loan → ", color = primaryText, fontSize = 13.sp)
                                    Text("Top 25%", color = blueAccent, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                }
                            }

                            Spacer(Modifier.height(16.dp))

                            Button(
                                onClick = { /* TODO: Open Detailed Analysis Bottom Sheet */ },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A3A6E)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("View Detailed Analysis")
                            }
                        }
                    }

                    // ==================== SMART RECOMMENDATIONS (Updated) ====================
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = primaryCard),
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(1.dp, borderColor)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Smart Recommendations", color = primaryText, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                            }

                            Spacer(Modifier.height(4.dp))
                            Text("Potential savings up to ₹6.4L with optimal strategy", color = greenAccent, fontSize = 14.sp, fontWeight = FontWeight.Medium)

                            Spacer(Modifier.height(14.dp))

                            // ==================== SMART RECOMMENDATIONS - LazyRow (Phase 4) ====================
                            val recommendationCardWidth = when {
                                isTablet -> 220.dp
                                isCompact -> 160.dp
                                else -> 180.dp
                            }

                            val recommendations = listOf(
                                Recommendation(
                                    title = "Best Savings",
                                    benefit = "Save ₹4.2L over tenure",
                                    accent = greenAccent,
                                    description = "This strategy focuses on minimizing total interest paid by making slightly higher EMIs or lump-sum payments when possible."
                                ),
                                Recommendation(
                                    title = "Fastest Closure",
                                    benefit = "Finish 3.2 years early",
                                    accent = blueAccent,
                                    description = "Aggressive repayment plan that helps you close the loan much earlier by directing extra funds toward principal reduction."
                                ),
                                Recommendation(
                                    title = "Lowest EMI",
                                    benefit = "Reduce EMI by ₹3,800",
                                    accent = goldAccent,
                                    description = "Optimized for lower monthly outflow. Best suited if you want to keep more cash flow available every month."
                                ),
                                Recommendation(
                                    title = "AI Recommended",
                                    benefit = "Best overall balance",
                                    accent = purpleAccent,
                                    description = "Our AI has analyzed your profile and recommends this balanced approach considering interest rate, tenure, and your financial goals."
                                )
                            )

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(recommendations) { rec ->
                                    Column(
                                        modifier = Modifier
                                            .width(recommendationCardWidth)
                                            .height(220.dp)
                                            .background(Color(0xFF0F2744), RoundedCornerShape(14.dp))
                                            .padding(12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(Icons.Rounded.Lightbulb, contentDescription = null, tint = rec.accent, modifier = Modifier.size(22.dp))
                                        Spacer(Modifier.height(8.dp))
                                        Text(rec.title, color = primaryText, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
                                        Spacer(Modifier.height(4.dp))
                                        Text(rec.benefit, color = secondaryText, fontSize = 10.sp, textAlign = TextAlign.Center)
                                        Spacer(Modifier.height(10.dp))
                                        Button(
                                            onClick = {
                                                selectedRecommendation = rec.title
                                                showRecommendationSheet = true
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(32.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1F3A5F)),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(0.dp)
                                        ) {
                                            Text("View Plan", fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // ==================== AMORTIZATION SCHEDULE ====================
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = primaryCard),
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(1.dp, borderColor)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Amortization Schedule", color = primaryText, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                                Text("View Full Schedule ›", color = blueAccent, fontSize = 12.sp, modifier = Modifier.clickable { showFullSchedule = true })
                            }
                            Spacer(Modifier.height(10.dp))

                            val scheduleData = if (yearBreakdown.size <= 4) {
                                yearBreakdown
                            } else {
                                yearBreakdown.take(3) + listOf(yearBreakdown.last())
                            }

                            scheduleData.forEachIndexed { index, row ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Year ${row.year}", color = if (index == scheduleData.lastIndex) secondaryText else primaryText, fontSize = 12.sp, modifier = Modifier.weight(1f))
                                    Text(formatMoney(row.emi), color = primaryText, fontSize = 12.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                                    Text(formatMoney(row.principalPaid), color = Color(0xFF22C55E), fontSize = 12.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                                    Text(formatMoney(row.interestPaid), color = Color(0xFFFFC328), fontSize = 12.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                                }
                                if (index < scheduleData.lastIndex) {
                                    HorizontalDivider(color = borderColor.copy(alpha = 0.35f))
                                }
                            }
                        }
                    }

                    // ==================== BOTTOM ACTIONS ====================
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(
                            onClick = { /* TODO: Generate PDF */ },
                            modifier = Modifier.weight(1f).height(52.dp),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, borderColor)
                        ) {
                            Icon(Icons.Rounded.PictureAsPdf, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Export PDF")
                        }
                        Button(
                            onClick = { /* TODO: Share */ },
                            modifier = Modifier.weight(1f).height(52.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F2744))
                        ) {
                            Icon(Icons.Rounded.Share, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Share Result")
                        }
                    }
                }
            }
            }   // end item Results

        }   // end LazyColumn
        }   // end Box (tablet wrapper)
    }   // end Scaffold content lambda

    // FULL SCHEDULE DIALOG
    if (showFullSchedule && hasValidInput) {
        FullAmortizationDialog(
            principal = loanAmount,
            annualRate = interestRate,
            totalMonths = totalMonths,
            onDismiss = { showFullSchedule = false }
        )
    }

    // ==================== RECOMMENDATION BOTTOM SHEET ====================
    if (showRecommendationSheet && selectedRecommendation != null) {

        ModalBottomSheet(
            onDismissRequest = { showRecommendationSheet = false },
            modifier = Modifier.fillMaxHeight(0.65f),
            sheetState = rememberModalBottomSheetState(),
            containerColor = Color(0xFF061633)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (selectedRecommendation) {

                    "Best Savings" -> {
                        BottomSheetContent(
                            title = "Best Savings",
                            benefit = "Save ₹4.2L over tenure",
                            description = "This strategy focuses on minimizing total interest paid by making slightly higher EMIs or lump-sum payments when possible."
                        )
                    }

                    "Fastest Closure" -> {
                        BottomSheetContent(
                            title = "Fastest Closure",
                            benefit = "Finish 3.2 years early",
                            description = "Aggressive repayment plan that helps you close the loan much earlier by directing extra funds toward principal reduction."
                        )
                    }

                    "Lowest EMI" -> {
                        BottomSheetContent(
                            title = "Lowest EMI",
                            benefit = "Reduce EMI by ₹3,800",
                            description = "Optimized for lower monthly outflow. Best suited if you want to keep more cash flow available every month."
                        )
                    }

                    "AI Recommended" -> {
                        BottomSheetContent(
                            title = "AI Recommended",
                            benefit = "Best overall balance",
                            description = "Our AI has analyzed your profile and recommends this balanced approach considering interest rate, tenure, and your financial goals."
                        )
                    }
                }
            }
        }
    }
// ==================== REUSABLE BOTTOM SHEET CONTENT ====================
@Composable
fun BottomSheetContent(
    title: String,
    benefit: String,
    description: String
) {
    val primaryText = Color(0xFFFFFFFF)
    val secondaryText = Color(0xFFA8B3D1)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Rounded.Lightbulb,
            contentDescription = null,
            tint = Color(0xFF7C4DFF),
            modifier = Modifier.size(48.dp)
        )

        Spacer(Modifier.height(16.dp))

        Text(
            title,
            color = primaryText,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(6.dp))

        Text(
            benefit,
            color = Color(0xFF7C4DFF),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F2744)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    "How this strategy works",
                    color = primaryText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    description,
                    color = secondaryText,
                    fontSize = 15.sp,
                    lineHeight = 22.sp
                )
            }
        }

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = { /* Will be handled by onDismissRequest */ },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C4DFF)),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("Got it, Close", color = Color.Black, fontWeight = FontWeight.Bold)
        }
    }
}
}  // closes EmiCalculatorScreen function
