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
import androidx.compose.ui.platform.LocalContext
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.min

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

data class Recommendation(
    val title: String,
    val benefit: String,
    val accent: Color,
    val description: String,
    val score: Double = 0.0
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

// Helper: Calculate how many months are needed with a higher EMI
fun calculateReducedTenure(principal: Double, annualRate: Double, newEmi: Double): Int {
    if (principal <= 0 || annualRate <= 0 || newEmi <= 0) return 0
    val monthlyRate = annualRate / 12 / 100
    var balance = principal
    var months = 0
    while (balance > 0 && months < 600) {
        val interest = balance * monthlyRate
        val principalPaid = newEmi - interest
        balance -= principalPaid
        months++
    }
    return months
}

// Helper: Calculate total interest paid over a given tenure
fun calculateTotalInterest(principal: Double, annualRate: Double, months: Int): Double {
    val emi = calculateEMI(principal, annualRate, months)
    return (emi * months) - principal
}

// ==================== PHASE 2: Generate Recommendation Detail ====================
fun generateRecommendationDetail(
    title: String,
    loanAmount: Double,
    interestRate: Double,
    monthlyEmi: Double,
    totalMonths: Int,
    totalInterest: Double,
    totalPayment: Double
): RecommendationDetail {
    val currentPlan = RecommendationPlan(
        monthlyEmi = monthlyEmi,
        tenureMonths = totalMonths,
        totalInterest = totalInterest,
        totalPayment = totalPayment
    )

    return when (title) {
        "Best Savings" -> {
            val newEmi = monthlyEmi * 1.15
            val newMonths = calculateReducedTenure(loanAmount, interestRate, newEmi)
            val newInterest = calculateTotalInterest(loanAmount, interestRate, newMonths)
            val interestSaved = (totalInterest - newInterest).coerceAtLeast(0.0)
            val yearsSaved = (totalMonths - newMonths) / 12.0

            RecommendationDetail(
                title = "Best Savings",
                valueProposition = "Save maximum interest over loan lifetime",
                currentPlan = currentPlan,
                recommendedPlan = RecommendationPlan(
                    monthlyEmi = newEmi,
                    tenureMonths = newMonths,
                    totalInterest = newInterest,
                    totalPayment = loanAmount + newInterest
                ),
                interestSaved = interestSaved,
                yearsSaved = yearsSaved
            )
        }

        "Fastest Closure" -> {
            val newEmi = monthlyEmi * 1.25
            val newMonths = calculateReducedTenure(loanAmount, interestRate, newEmi)
            val newInterest = calculateTotalInterest(loanAmount, interestRate, newMonths)
            val interestSaved = (totalInterest - newInterest).coerceAtLeast(0.0)
            val yearsSaved = (totalMonths - newMonths) / 12.0

            RecommendationDetail(
                title = "Fastest Closure",
                valueProposition = "Become debt-free sooner",
                currentPlan = currentPlan,
                recommendedPlan = RecommendationPlan(
                    monthlyEmi = newEmi,
                    tenureMonths = newMonths,
                    totalInterest = newInterest,
                    totalPayment = loanAmount + newInterest
                ),
                interestSaved = interestSaved,
                yearsSaved = yearsSaved
            )
        }

        "Lowest EMI" -> {
            val extendedMonths = min(totalMonths + 60, 360)
            val newEmi = calculateEMI(loanAmount, interestRate, extendedMonths)
            val newInterest = calculateTotalInterest(loanAmount, interestRate, extendedMonths)
            val emiReduction = (monthlyEmi - newEmi).coerceAtLeast(0.0)
            val additionalInterest = (newInterest - totalInterest).coerceAtLeast(0.0)

            RecommendationDetail(
                title = "Lowest EMI",
                valueProposition = "Reduce monthly financial pressure",
                currentPlan = currentPlan,
                recommendedPlan = RecommendationPlan(
                    monthlyEmi = newEmi,
                    tenureMonths = extendedMonths,
                    totalInterest = newInterest,
                    totalPayment = loanAmount + newInterest
                ),
                emiReduction = emiReduction,
                additionalInterest = additionalInterest
            )
        }

        else -> { // AI Recommended (Phase 4)
            RecommendationDetail(
                title = "AI Recommended",
                valueProposition = "Best balance of savings and affordability",
                currentPlan = currentPlan,
                recommendedPlan = when (recommendedTitle) {
                    "Best Savings" -> RecommendationPlan(
                        monthlyEmi = monthlyEmi * 1.15,
                        tenureMonths = calculateReducedTenure(loanAmount, interestRate, monthlyEmi * 1.15),
                        totalInterest = calculateTotalInterest(loanAmount, interestRate, calculateReducedTenure(loanAmount, interestRate, monthlyEmi * 1.15)),
                        totalPayment = loanAmount + calculateTotalInterest(loanAmount, interestRate, calculateReducedTenure(loanAmount, interestRate, monthlyEmi * 1.15))
                    )
                    "Fastest Closure" -> RecommendationPlan(
                        monthlyEmi = monthlyEmi * 1.25,
                        tenureMonths = calculateReducedTenure(loanAmount, interestRate, monthlyEmi * 1.25),
                        totalInterest = calculateTotalInterest(loanAmount, interestRate, calculateReducedTenure(loanAmount, interestRate, monthlyEmi * 1.25)),
                        totalPayment = loanAmount + calculateTotalInterest(loanAmount, interestRate, calculateReducedTenure(loanAmount, interestRate, monthlyEmi * 1.25))
                    )
                    else -> RecommendationPlan(
                        monthlyEmi = calculateEMI(loanAmount, interestRate, min(totalMonths + 60, 360)),
                        tenureMonths = min(totalMonths + 60, 360),
                        totalInterest = calculateTotalInterest(loanAmount, interestRate, min(totalMonths + 60, 360)),
                        totalPayment = loanAmount + calculateTotalInterest(loanAmount, interestRate, min(totalMonths + 60, 360))
                    )
                },
                whyRecommended = aiReason
            )
        }
    }
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
    schedule: List<YearBreakdown>,
    monthlyEmi: Double,
    onDismiss: () -> Unit,
    onExportCsv: () -> Unit
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
                        onClick = onExportCsv,
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

    // ==================== CSV EXPORT LAUNCHER ====================
    val context = LocalContext.current
    val csvLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        uri?.let {
            // We'll call the export function when dialog requests it
        }
    }

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

    val principalPercent = if (totalPayment > 0) {
        ((totalPrincipal / totalPayment) * 100).roundToInt()
    } else 0

    val interestPercent = if (totalPayment > 0) {
        100 - principalPercent
    } else 0

    val prinPct = principalPercent.toDouble()
    val intPct = interestPercent.toDouble()

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

                                    // ==================== HERO CARD - 3 STATISTIC CARDS ====================
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        // Principal Card
                                        Column(
                                            modifier = Modifier
                                                .weight(1f)
                                                .background(Color(0xFF0F2744), RoundedCornerShape(12.dp))
                                                .padding(vertical = 12.dp, horizontal = 8.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text("Principal", color = secondaryText, fontSize = 12.sp)
                                            Spacer(Modifier.height(4.dp))
                                            Text(formatMoney(totalPrincipal), color = purpleAccent, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                        }

                                        // Interest Card
                                        Column(
                                            modifier = Modifier
                                                .weight(1f)
                                                .background(Color(0xFF0F2744), RoundedCornerShape(12.dp))
                                                .padding(vertical = 12.dp, horizontal = 8.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text("Interest", color = secondaryText, fontSize = 12.sp)
                                            Spacer(Modifier.height(4.dp))
                                            Text(formatMoney(totalInterest), color = greenAccent, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                        }

                                        // Total Payment Card
                                        Column(
                                            modifier = Modifier
                                                .weight(1f)
                                                .background(Color(0xFF0F2744), RoundedCornerShape(12.dp))
                                                .padding(vertical = 12.dp, horizontal = 8.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text("Total", color = secondaryText, fontSize = 12.sp)
                                            Spacer(Modifier.height(4.dp))
                                            Text(formatMoney(totalPayment), color = primaryText, fontSize = 15.sp, fontWeight = FontWeight.Bold)
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
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = greenAccent, modifier = Modifier.size(18.dp))
                                        Spacer(Modifier.width(10.dp))
                                        Text("Effective Rate (Approx.) 7.1% p.a.", color = primaryText, fontSize = 13.sp)
                                    }
                                    Text("Very Close", color = goldAccent, fontSize = 13.sp, fontWeight = FontWeight.Medium, maxLines = 1)
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
                            Text("Personalized repayment strategies", color = greenAccent, fontSize = 14.sp, fontWeight = FontWeight.Medium)

                            Spacer(Modifier.height(14.dp))

                            // ==================== SMART RECOMMENDATIONS - LazyRow (Phase 4) ====================
                            val recommendationCardWidth = when {
                                isTablet -> 220.dp
                                isMedium -> 180.dp
                                else -> 150.dp
                            }

                            // ==================== DYNAMIC RECOMMENDATIONS ====================
                            val originalTotalInterest = totalInterest
                            val originalMonths = totalMonths

                            // Best Savings: Increase EMI by 15%
                            val bestSavingsEmi = monthlyEmi * 1.15
                            val bestSavingsMonths = calculateReducedTenure(loanAmount, interestRate, bestSavingsEmi)
                            val bestSavingsInterest = calculateTotalInterest(loanAmount, interestRate, bestSavingsMonths)
                            val bestSavingsSaved = (originalTotalInterest - bestSavingsInterest).coerceAtLeast(0.0)

                            // Fastest Closure: Increase EMI by 25%
                            val fastestEmi = monthlyEmi * 1.25
                            val fastestMonths = calculateReducedTenure(loanAmount, interestRate, fastestEmi)
                            val fastestYearsSaved = ((originalMonths - fastestMonths) / 12.0).coerceAtLeast(0.0)

                            // Lowest EMI: Extend tenure by up to 5 years
                            val extendedMonths = min(originalMonths + 60, 360)
                            val lowestEmi = calculateEMI(loanAmount, interestRate, extendedMonths)
                            val lowestEmiReduction = (monthlyEmi - lowestEmi).coerceAtLeast(0.0)

                            // AI Recommended - Scoring System
                            val scoreSavings = bestSavingsSaved * 0.4
                            val scoreClosure = fastestYearsSaved * 12 * 0.4
                            val scoreAffordability = lowestEmiReduction * 0.2

                            val bestScore = maxOf(scoreSavings, scoreClosure, scoreAffordability)
                            val recommendedTitle = when (bestScore) {
                                scoreSavings -> "Best Savings"
                                scoreClosure -> "Fastest Closure"
                                else -> "Lowest EMI"
                            }

                            val recommendations = listOf(
                                Recommendation(
                                    title = "Best Savings",
                                    benefit = "Save ${formatMoney(bestSavingsSaved)} interest",
                                    accent = greenAccent,
                                    description = "Increase your EMI by 15% to significantly reduce total interest paid over the loan tenure.",
                                    score = scoreSavings
                                ),
                                Recommendation(
                                    title = "Fastest Closure",
                                    benefit = "Finish ${"%.1f".format(fastestYearsSaved)} years earlier",
                                    accent = blueAccent,
                                    description = "Increase EMI by 25% to close your loan much earlier by directing more money toward the principal.",
                                    score = scoreClosure
                                ),
                                Recommendation(
                                    title = "Lowest EMI",
                                    benefit = "Reduce EMI by ${formatMoney(lowestEmiReduction)}",
                                    accent = goldAccent,
                                    description = "Extend your tenure to lower your monthly payment while keeping the loan manageable.",
                                    score = scoreAffordability
                                ),
                                Recommendation(
                                    title = "AI Recommended",
                                    benefit = if (recommendedTitle == "Best Savings") "Save ${formatMoney(bestSavingsSaved)} interest"
                                              else if (recommendedTitle == "Fastest Closure") "Finish ${"%.1f".format(fastestYearsSaved)} years earlier"
                                              else "Reduce EMI by ${formatMoney(lowestEmiReduction)}",
                                    accent = purpleAccent,
                                    description = aiReason,
                                    score = bestScore
                                )
                            ).sortedByDescending { it.score }

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(14.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp)
                            ) {
                                items(recommendations) { rec ->
                                    Column(
                                        modifier = Modifier
                                            .widthIn(min = 160.dp, max = 280.dp)
                                            .height(220.dp)
                                            .background(Color(0xFF0F2744), RoundedCornerShape(14.dp))
                                            .padding(12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        // Recommended badge only on the best card
                                        if (rec.title == recommendedTitle) {
                                            Surface(
                                                color = Color(0xFF22C55E).copy(alpha = 0.15f),
                                                shape = RoundedCornerShape(20.dp)
                                            ) {
                                                Text(
                                                    "Recommended",
                                                    color = Color(0xFF22C55E),
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp)
                                                )
                                            }
                                            Spacer(Modifier.height(6.dp))
                                        }

                                        Icon(Icons.Rounded.Lightbulb, contentDescription = null, tint = rec.accent, modifier = Modifier.size(22.dp))
                                        Spacer(Modifier.height(8.dp))
                                        Text(rec.title, color = primaryText, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
                                        Spacer(Modifier.height(4.dp))
                                        Text(
                                            rec.benefit,
                                            color = secondaryText,
                                            fontSize = 10.sp,
                                            textAlign = TextAlign.Center,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(Modifier.height(10.dp))
                                        Button(
                                            onClick = {
                                                // Phase 2: Open new premium recommendation sheet with calculated data
                                                val detail = generateRecommendationDetail(
                                                    title = rec.title,
                                                    loanAmount = loanAmount,
                                                    interestRate = interestRate,
                                                    monthlyEmi = monthlyEmi,
                                                    totalMonths = totalMonths,
                                                    totalInterest = totalInterest,
                                                    totalPayment = totalPayment
                                                )
                                                selectedRecommendationDetail = detail
                                                showPremiumRecommendationSheet = true
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
            schedule = yearBreakdown,
            monthlyEmi = monthlyEmi,
            onDismiss = { showFullSchedule = false },
            onExportCsv = {
                csvLauncher.launch("loan_amortization_schedule.csv")
            }
        )
    }

    // ==================== NEW PREMIUM RECOMMENDATION BOTTOM SHEET ====================
    if (showPremiumRecommendationSheet && selectedRecommendationDetail != null) {
        RecommendationBottomSheet(
            detail = selectedRecommendationDetail!!,
            onDismiss = {
                showPremiumRecommendationSheet = false
                selectedRecommendationDetail = null
            }
        )
    }

    // Old bottom sheet disabled (new one is now primary)
    // if (showRecommendationSheet && selectedRecommendation != null) { ... }
}  // closes EmiCalculatorScreen function

// ==================== CSV EXPORT HELPER ====================
fun exportScheduleToCsv(
    context: android.content.Context,
    uri: android.net.Uri,
    schedule: List<YearBreakdown>,
    loanAmount: Double,
    interestRate: Double,
    totalMonths: Int,
    monthlyEmi: Double
) {
    try {
        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
            val writer = outputStream.bufferedWriter()

            // Header
            writer.write("Month,EMI,Principal,Interest,Balance\n")

            var runningBalance = loanAmount
            val monthlyRate = interestRate / 12 / 100

            schedule.forEachIndexed { index, year ->
                val monthNumber = index + 1
                val interest = runningBalance * monthlyRate
                val principalPaid = (monthlyEmi - interest).coerceAtLeast(0.0)
                runningBalance = (runningBalance - principalPaid).coerceAtLeast(0.0)

                writer.write(
                    "$monthNumber,${"%.0f".format(monthlyEmi)},${"%.0f".format(principalPaid)},${"%.0f".format(interest)},${"%.0f".format(runningBalance)}\n"
                )
            }
            writer.flush()
        }
    } catch (e: Exception) {
        e.printStackTrace()
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
            onClick = { /* Handled by onDismissRequest */ },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C4DFF)),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("Got it, Close", color = Color.Black, fontWeight = FontWeight.Bold)
        }
    }
}

// ==================== PHASE 1: NEW RECOMMENDATION BOTTOM SHEET ====================

@Composable
fun RecommendationBottomSheet(
    detail: RecommendationDetail,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxHeight(0.85f),
        sheetState = rememberModalBottomSheetState(),
        containerColor = Color(0xFF061633)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Header
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Rounded.Lightbulb,
                    contentDescription = null,
                    tint = detail.currentPlan.monthlyEmi.let { Color(0xFF7C4DFF) },
                    modifier = Modifier.size(48.dp)
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    detail.title,
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    detail.valueProposition,
                    color = Color(0xFFA8B3D1),
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(24.dp))

            // Comparison Section
            Text(
                "Current vs Recommended",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(12.dp))

            RecommendationComparisonCard(
                current = detail.currentPlan,
                recommended = detail.recommendedPlan,
                interestSaved = detail.interestSaved,
                yearsSaved = detail.yearsSaved,
                emiChange = detail.recommendedPlan.monthlyEmi - detail.currentPlan.monthlyEmi
            )

            Spacer(Modifier.height(24.dp))

            // Strategy-specific content placeholder (will be expanded in later phases)
            // ==================== PHASE 5: VISUAL POLISH + RICH CONTENT ====================
            when (detail.title) {
                "Best Savings" -> {
                    Column {
                        // Savings highlight
                        Surface(
                            color = Color(0xFF22C55E).copy(alpha = 0.12f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    "You save ₹${formatMoney(detail.interestSaved)} in interest",
                                    color = Color(0xFF22C55E),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    "Loan closes ${"%.1f".format(detail.yearsSaved)} years earlier",
                                    color = Color(0xFFA8B3D1),
                                    fontSize = 15.sp
                                )
                            }
                        }
                        Spacer(Modifier.height(16.dp))

                        StrategyInsightCard(
                            title = "How this works",
                            points = listOf(
                                "Increase your EMI by 15%",
                                "More money goes toward principal every month",
                                "Significantly reduces total interest paid"
                            ),
                            isPositive = true
                        )
                    }
                }

                "Fastest Closure" -> {
                    Column {
                        Surface(
                            color = Color(0xFF3B82F6).copy(alpha = 0.12f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    "Become debt-free ${"%.1f".format(detail.yearsSaved)} years sooner",
                                    color = Color(0xFF3B82F6),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    "Save ₹${formatMoney(detail.interestSaved)} in interest",
                                    color = Color(0xFFA8B3D1),
                                    fontSize = 15.sp
                                )
                            }
                        }
                        Spacer(Modifier.height(16.dp))

                        StrategyInsightCard(
                            title = "How this works",
                            points = listOf(
                                "Increase your EMI by 25%",
                                "Aggressively pay down the principal",
                                "Best option if you want to be debt-free fast"
                            ),
                            isPositive = true
                        )
                    }
                }

                "Lowest EMI" -> {
                    Column {
                        Text(
                            "Reduce EMI by ₹${formatMoney(detail.emiReduction)}/month",
                            color = Color(0xFFF59E0B),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(8.dp))

                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF3F2A1F)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Rounded.Warning, contentDescription = null, tint = Color(0xFFF59E0B))
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    "Warning: This increases total interest by ₹${formatMoney(detail.additionalInterest)}",
                                    color = Color(0xFFF59E0B),
                                    fontSize = 13.sp
                                )
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        StrategyInsightCard(
                            title = "Trade-off",
                            points = listOf(
                                "Lower monthly burden",
                                "Longer time in debt",
                                "Pay more interest overall"
                            ),
                            isPositive = false
                        )
                    }
                }

                else -> { // AI Recommended
                    Column {
                        Text(
                            "Best overall balance for your profile",
                            color = Color(0xFF7C4DFF),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(12.dp))

                        StrategyInsightCard(
                            title = "Why AI chose this",
                            points = listOf(
                                detail.whyRecommended,
                                "Optimal balance between savings and monthly comfort",
                                "Recommended based on your current financial situation"
                            ),
                            isPositive = true
                        )
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C4DFF)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Got it, Close", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun RecommendationComparisonCard(
    current: RecommendationPlan,
    recommended: RecommendationPlan,
    interestSaved: Double = 0.0,
    yearsSaved: Double = 0.0,
    emiChange: Double = 0.0
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Current Plan
            Column(
                modifier = Modifier
                    .weight(1f)
                    .background(Color(0xFF0F2744), RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Text("Current Plan", color = Color(0xFFA8B3D1), fontSize = 13.sp)
                Spacer(Modifier.height(12.dp))
                ComparisonItem("Monthly EMI", formatMoney(current.monthlyEmi))
                ComparisonItem("Tenure", "${current.tenureMonths / 12} years")
                ComparisonItem("Total Interest", formatMoney(current.totalInterest))
                ComparisonItem("Total Payment", formatMoney(current.totalPayment))
            }

            // Recommended Plan
            Column(
                modifier = Modifier
                    .weight(1f)
                    .background(Color(0xFF0F2744), RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Text("Recommended Plan", color = Color(0xFF22C55E), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(12.dp))
                ComparisonItem("Monthly EMI", formatMoney(recommended.monthlyEmi))
                ComparisonItem("Tenure", "${recommended.tenureMonths / 12} years")
                ComparisonItem("Total Interest", formatMoney(recommended.totalInterest))
                ComparisonItem("Total Payment", formatMoney(recommended.totalPayment))
            }
        }

        // Impact Summary (only show if we have meaningful data)
        if (interestSaved > 0 || yearsSaved > 0 || emiChange != 0.0) {
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (interestSaved > 0) {
                    Surface(
                        color = Color(0xFF22C55E).copy(alpha = 0.12f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Interest Saved", color = Color(0xFF22C55E), fontSize = 11.sp)
                            Text("₹${formatMoney(interestSaved)}", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                if (yearsSaved > 0) {
                    Surface(
                        color = Color(0xFF3B82F6).copy(alpha = 0.12f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Years Saved", color = Color(0xFF3B82F6), fontSize = 11.sp)
                            Text("${"%.1f".format(yearsSaved)} yrs", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Extra EMI Required (for Best Savings & Fastest Closure)
                val extraEmi = recommended.monthlyEmi - current.monthlyEmi
                if (extraEmi > 0) {
                    Surface(
                        color = Color(0xFF7C4DFF).copy(alpha = 0.12f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Extra EMI Required", color = Color(0xFF7C4DFF), fontSize = 11.sp)
                            Text("₹${formatMoney(extraEmi)} / mo", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Lowest EMI Tradeoff (explicit)
            if (detail.title == "Lowest EMI") {
                if (detail.emiReduction > 0) {
                    ImpactCard(
                        icon = Icons.Rounded.TrendingDown,
                        title = "EMI Reduced",
                        value = "₹${formatMoney(detail.emiReduction)} / month",
                        color = Color(0xFF22C55E)
                    )
                }
                if (detail.additionalInterest > 0) {
                    ImpactCard(
                        icon = Icons.Rounded.Warning,
                        title = "Additional Interest Cost",
                        value = "₹${formatMoney(detail.additionalInterest)}",
                        color = Color(0xFFF59E0B)
                    )
                }
            }
        }
    }
}

@Composable
fun ComparisonItem(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(label, color = Color(0xFFA8B3D1), fontSize = 12.sp)
        Text(value, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun StrategyInsightCard(
    title: String,
    points: List<String>,
    isPositive: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F2744)),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(12.dp))
            points.forEach { point ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isPositive) Icons.Rounded.CheckCircle else Icons.Rounded.Warning,
                        contentDescription = null,
                        tint = if (isPositive) Color(0xFF22C55E) else Color(0xFFF59E0B),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(point, color = Color(0xFFA8B3D1), fontSize = 14.sp)
                }
                Spacer(Modifier.height(6.dp))
            }
        }
    }
}
