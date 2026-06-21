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
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.log
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

fun getMonthlyAmortizationSchedule(principal: Double, annualRate: Double, totalMonths: Int): List<MonthlyAmortization> {
    if (principal <= 0 || annualRate <= 0 || totalMonths <= 0) return emptyList()
    val monthlyRate = annualRate / 12 / 100
    val emi = calculateEMI(principal, annualRate, totalMonths)
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
    return list
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

fun generateRecommendations(
    principal: Double,
    annualRate: Double,
    totalMonths: Int,
    baseEmi: Double,
    baseInterest: Double
): List<SmartRecommendation> {
    if (principal <= 0 || annualRate <= 0 || totalMonths <= 0) return emptyList()
    
    val r = annualRate / 12 / 100
    
    fun calc(emi: Double): Pair<Int, Double> {
        var bal = principal
        var m = 0
        var totInt = 0.0
        while (bal > 0 && m < totalMonths * 3) {
            m++
            val int = bal * r
            val prin = emi - int
            if (prin <= 0) return Pair(totalMonths * 2, Double.MAX_VALUE)
            totInt += int
            bal -= prin
        }
        return Pair(m, totInt)
    }

    val emi1 = baseEmi * 1.15
    val (m1, int1) = calc(emi1)
    
    val emi2 = baseEmi * 1.25
    val (m2, int2) = calc(emi2)
    
    val m3 = totalMonths + 60
    val emi3 = calculateEMI(principal, annualRate, m3)
    val (_, int3) = calc(emi3)
    
    val emi4 = baseEmi * 1.10
    val (m4, int4) = calc(emi4)

    return listOf(
        SmartRecommendation(
            id = "best_savings",
            title = "Best Savings",
            description = "Save up to ${formatMoney(baseInterest - int1)}",
            icon = Icons.Rounded.Savings,
            accentColor = Color(0xFF22C55E),
            currentEmi = baseEmi,
            targetEmi = emi1,
            currentTotalInterest = baseInterest,
            targetTotalInterest = int1,
            currentTenureMonths = totalMonths,
            targetTenureMonths = m1,
            isRecommended = false
        ),
        SmartRecommendation(
            id = "fastest_closure",
            title = "Fast Closure",
            description = "Finish ${(totalMonths - m2) / 12} years early",
            icon = Icons.Rounded.Speed,
            accentColor = Color(0xFF2D7DFF),
            currentEmi = baseEmi,
            targetEmi = emi2,
            currentTotalInterest = baseInterest,
            targetTotalInterest = int2,
            currentTenureMonths = totalMonths,
            targetTenureMonths = m2,
            isRecommended = false
        ),
        SmartRecommendation(
            id = "lowest_emi",
            title = "Low EMI",
            description = "Reduce EMI to ${formatMoney(emi3)}",
            icon = Icons.Rounded.TrendingDown,
            accentColor = Color(0xFFFFC328),
            currentEmi = baseEmi,
            targetEmi = emi3,
            currentTotalInterest = baseInterest,
            targetTotalInterest = int3,
            currentTenureMonths = totalMonths,
            targetTenureMonths = m3,
            isRecommended = false
        ),
        SmartRecommendation(
            id = "ai_recommended",
            title = "AI Peak Plan",
            description = "Top Score: Optimal Balance",
            icon = Icons.Rounded.AutoAwesome,
            accentColor = Color(0xFF7C4DFF),
            currentEmi = baseEmi,
            targetEmi = emi4,
            currentTotalInterest = baseInterest,
            targetTotalInterest = int4,
            currentTenureMonths = totalMonths,
            targetTenureMonths = m4,
            isRecommended = true
        )
    )
}



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
    sizeClass: WindowWidthSizeClass,
    modifier: Modifier = Modifier
) {
    val haptic = androidx.compose.ui.platform.LocalHapticFeedback.current
    PremiumInputField(
        label = label,
        value = value,
        onValueChange = onValueChange,
        icon = icon,
        iconTint = iconTint,
        sizeClass = sizeClass,
        modifier = modifier,
        trailingContent = {
            // Toggle Buttons
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(inputBg)
                    .border(1.dp, borderColor, RoundedCornerShape(8.dp)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .background(if (!isMonths) Color(0xFF2D7DFF) else Color.Transparent)
                        .clickable { 
                            haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                            onToggleIsMonths(false) 
                        }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Text("Yrs", color = if (!isMonths) Color.White else secondaryText, fontSize = ResponsiveUtils.bodyFontSize(sizeClass).value.sp * 0.85f, fontWeight = FontWeight.Medium)
                }
                Box(
                    modifier = Modifier
                        .background(if (isMonths) Color(0xFF2D7DFF) else Color.Transparent)
                        .clickable { 
                            haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                            onToggleIsMonths(true) 
                        }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Text("Mo", color = if (isMonths) Color.White else secondaryText, fontSize = ResponsiveUtils.bodyFontSize(sizeClass).value.sp * 0.85f, fontWeight = FontWeight.Medium)
                }
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
    sizeClass: WindowWidthSizeClass,
    modifier: Modifier = Modifier
) {
    var isDropdownExpanded by remember { mutableStateOf(false) }
    val haptic = androidx.compose.ui.platform.LocalHapticFeedback.current

    Column(modifier = modifier) {
        Text("Loan Type", color = secondaryText, fontSize = ResponsiveUtils.bodyFontSize(sizeClass).value.sp * 0.85f)
        Spacer(Modifier.height(6.dp))
        Surface(
            shape = RoundedCornerShape(12.dp),
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
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val icon = when (selectedType) {
                        "Home Loan" -> Icons.Rounded.Home
                        "Car Loan" -> Icons.Rounded.DirectionsCar
                        "Personal Loan" -> Icons.Rounded.Person
                        "Education Loan" -> Icons.Rounded.School
                        else -> Icons.Rounded.AccountBalance
                    }
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color(0xFF22C55E),
                        modifier = Modifier.size(ResponsiveUtils.iconSize(sizeClass).value.dp * 0.8f)
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(selectedType, color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface, fontSize = ResponsiveUtils.bodyFontSize(sizeClass), fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                    Icon(
                        imageVector = Icons.Rounded.KeyboardArrowDown,
                        contentDescription = null,
                        tint = secondaryText,
                        modifier = Modifier.size(ResponsiveUtils.iconSize(sizeClass).value.dp * 0.8f)
                    )
                }
                androidx.compose.material3.DropdownMenu(
                    expanded = isDropdownExpanded,
                    onDismissRequest = { isDropdownExpanded = false },
                    modifier = Modifier.background(inputBg).border(1.dp, borderColor, RoundedCornerShape(8.dp))
                ) {
                    val loanTypes = listOf("Home Loan", "Car Loan", "Personal Loan", "Education Loan", "Business Loan")
                    loanTypes.forEach { type ->
                        androidx.compose.material3.DropdownMenuItem(
                            text = { Text(type, color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface) },
                            onClick = {
                                haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                                onTypeSelected(type)
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
    principal: Double,
    annualRate: Double,
    totalMonths: Int,
    onDismiss: () -> Unit
) {
    val haptic = androidx.compose.ui.platform.LocalHapticFeedback.current
    val emi = calculateEMI(principal, annualRate, totalMonths)
    val schedule = remember(principal, annualRate, totalMonths) {
        getMonthlyAmortizationSchedule(principal, annualRate, totalMonths)
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
                    Text("Mo.", color = Color(0xFF7C8DB5), fontSize = 11.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(0.9f))
                    Text("EMI", color = Color(0xFF7C8DB5), fontSize = 11.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1.1f), textAlign = TextAlign.End)
                    Text("Prin", color = Color(0xFF7C8DB5), fontSize = 11.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1.2f), textAlign = TextAlign.End)
                    Text("Int", color = Color(0xFF7C8DB5), fontSize = 11.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1.1f), textAlign = TextAlign.End)
                    Text("Bal", color = Color(0xFF7C8DB5), fontSize = 11.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1.2f), textAlign = TextAlign.End)
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
                        onClick = {
                            haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFF183C8A))
                    ) {
                        Text("Close", color = Color.White)
                    }
                    Button(
                        onClick = { 
                            haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                            /* TODO: Export CSV */ 
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D7DFF)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Rounded.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
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
fun EmiCalculatorScreen(onNavigateBack: () -> Unit = {}) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    
    val sizeClass = when {
        configuration.screenWidthDp < 600 -> WindowWidthSizeClass.Compact
        configuration.screenWidthDp < 840 -> WindowWidthSizeClass.Medium
        else -> WindowWidthSizeClass.Expanded
    }
    
    val isExpanded = sizeClass == WindowWidthSizeClass.Expanded
    val isMedium = sizeClass == WindowWidthSizeClass.Medium

    val horizPadding = ResponsiveUtils.horizontalPadding(sizeClass)
    val cardSpacing = ResponsiveUtils.cardSpacing(sizeClass)
    
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

    // State
    var loanAmountText by remember { mutableStateOf("0") }
    var interestRateText by remember { mutableStateOf("0") }
    var tenureInputText by remember { mutableStateOf("0") }
    var isTenureInMonths by remember { mutableStateOf(false) }
    var loanType by remember { mutableStateOf("Home Loan") }
    var showFullSchedule by remember { mutableStateOf(false) }
    
    // Bottom Sheet State
    var selectedRecommendation by remember { mutableStateOf<SmartRecommendation?>(null) }
    val haptic = androidx.compose.ui.platform.LocalHapticFeedback.current

    val loanAmount = loanAmountText.safeToDouble()
    val interestRate = interestRateText.safeToDouble()
    val tenureValue = tenureInputText.safeToDouble().toInt().coerceIn(0, 1200)
    val totalMonths = if (isTenureInMonths) tenureValue else tenureValue * 12
    val tenureYears = if (totalMonths > 0) totalMonths / 12 else 0

    val hasValidInput = loanAmount > 0 && interestRate > 0 && totalMonths > 0

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

    val monthlySchedule = remember(loanAmount, interestRate, totalMonths) {
        getMonthlyAmortizationSchedule(loanAmount, interestRate, totalMonths)
    }
    
    val recommendations = remember(hasValidInput, loanAmount, interestRate, totalMonths, monthlyEmi, totalInterest) {
        if (hasValidInput) {
            generateRecommendations(loanAmount, interestRate, totalMonths, monthlyEmi, totalInterest)
        } else {
            emptyList()
        }
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            ResponsiveScreenWrapper(
                widthSizeClass = sizeClass,
                animationTriggerState = monthlyEmi,
                headerSection = {
                    // HEADER
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = primaryText,
                            modifier = Modifier.clickable { 
                                haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                                onNavigateBack() 
                            }.size(ResponsiveUtils.iconSize(sizeClass))
                        )
                        Spacer(Modifier.width(16.dp))
                        Column(Modifier.weight(1f)) {
                            Text("EMI Calculator", color = primaryText, fontSize = ResponsiveUtils.titleFontSize(sizeClass), fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text("Calculate your loan EMI and plan better", color = secondaryText, fontSize = ResponsiveUtils.bodyFontSize(sizeClass).value.sp * 0.8f, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                        Icon(imageVector = Icons.Rounded.StarBorder, contentDescription = null, tint = goldAccent, modifier = Modifier.size(ResponsiveUtils.iconSize(sizeClass)))
                        Spacer(Modifier.width(16.dp))
                        Icon(imageVector = Icons.Rounded.Share, contentDescription = null, tint = primaryText, modifier = Modifier.size(ResponsiveUtils.iconSize(sizeClass)))
                    }
                },
                inputControlsSection = {
                    // INPUT SECTION
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = primaryCard),
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(1.dp, borderColor)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            if (isExpanded) {
                                // 2x2 Grid for Wide Screens
                                Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
                                    PremiumInputField(
                                        label = "Loan Amount", value = loanAmountText, onValueChange = { loanAmountText = it },
                                        icon = Icons.Rounded.AccountBalanceWallet, iconTint = blueAccent,
                                        sizeClass = sizeClass, modifier = Modifier.weight(1f)
                                    )
                                    PremiumInputField(
                                        label = "Interest Rate (p.a.)", value = interestRateText, onValueChange = { interestRateText = it },
                                        icon = Icons.Rounded.Percent, iconTint = blueAccent,
                                        sizeClass = sizeClass, modifier = Modifier.weight(1f),
                                        infoText = "The annual interest rate charged on your loan."
                                    )
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
                                    TenureInputField(
                                        label = "Tenure", value = tenureInputText, onValueChange = { tenureInputText = it },
                                        isMonths = isTenureInMonths, onToggleIsMonths = { isTenureInMonths = it },
                                        icon = Icons.Rounded.DateRange, iconTint = blueAccent,
                                        inputBg = inputBg, borderColor = borderColor, secondaryText = secondaryText,
                                        sizeClass = sizeClass, modifier = Modifier.weight(1f)
                                    )
                                    LoanTypeSelector(
                                        selectedType = loanType, onTypeSelected = { loanType = it }, inputBg = inputBg, borderColor = borderColor, secondaryText = secondaryText,
                                        sizeClass = sizeClass, modifier = Modifier.weight(1f)
                                    )
                                }
                            } else {
                                // Stacked or 1x2 then 2x2 depending on medium vs compact
                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                                    PremiumInputField(
                                        label = "Loan Amount", value = loanAmountText, onValueChange = { loanAmountText = it },
                                        icon = Icons.Rounded.AccountBalanceWallet, iconTint = blueAccent,
                                        sizeClass = sizeClass, modifier = Modifier.weight(1f)
                                    )
                                    PremiumInputField(
                                        label = "Interest", value = interestRateText, onValueChange = { interestRateText = it },
                                        icon = Icons.Rounded.Percent, iconTint = blueAccent,
                                        sizeClass = sizeClass, modifier = Modifier.weight(1f),
                                        infoText = "The annual interest rate charged on your loan."
                                    )
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                                    TenureInputField(
                                        label = "Tenure", value = tenureInputText, onValueChange = { tenureInputText = it },
                                        isMonths = isTenureInMonths, onToggleIsMonths = { isTenureInMonths = it },
                                        icon = Icons.Rounded.DateRange, iconTint = blueAccent,
                                        inputBg = inputBg, borderColor = borderColor, secondaryText = secondaryText,
                                        sizeClass = sizeClass, modifier = Modifier.weight(1f)
                                    )
                                    LoanTypeSelector(
                                        selectedType = loanType, onTypeSelected = { loanType = it }, inputBg = inputBg, borderColor = borderColor, secondaryText = secondaryText,
                                        sizeClass = sizeClass, modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                },
                resultsSection = {
                    Column(verticalArrangement = Arrangement.spacedBy(cardSpacing)) {
            // PLACEHOLDER
            if (!hasValidInput) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = primaryCard),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, borderColor)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Rounded.Calculate, contentDescription = null, tint = secondaryText.copy(0.5f), modifier = Modifier.size(ResponsiveUtils.iconSize(sizeClass) * 2f))
                        Spacer(Modifier.height(16.dp))
                        Text("Enter loan amount, rate & tenure to see results", color = secondaryText, fontSize = ResponsiveUtils.bodyFontSize(sizeClass), textAlign = TextAlign.Center)
                    }
                }
            }

            // ANIMATED RESULTS
            AnimatedVisibility(
                visible = hasValidInput,
                enter = fadeIn(tween(400)) + scaleIn(initialScale = 0.95f, animationSpec = tween(400))
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(cardSpacing)) {

                    // ==================== HERO EMI CARD ====================
                    Card(
                        modifier = Modifier.fillMaxWidth().shadow(16.dp, spotColor = blueAccent.copy(0.4f)),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, borderColor)
                    ) {
                        Box(modifier = Modifier.background(Brush.linearGradient(listOf(inputBg, Color(0xFF0A2150))))) {
                            Column(modifier = Modifier.padding(if (isExpanded) 32.dp else 20.dp)) {
                                // EMI Header
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Your Monthly EMI", color = secondaryText, fontSize = ResponsiveUtils.bodyFontSize(sizeClass).value.sp * 0.9f)
                                        Text(
                                            formatMoney(animatedEmi.toDouble()),
                                            color = blueAccent,
                                            fontSize = ResponsiveUtils.titleFontSize(sizeClass).value.sp * 1.5f,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.Rounded.CalendarMonth,
                                        contentDescription = null,
                                        tint = blueAccent.copy(alpha = 0.3f),
                                        modifier = Modifier.size(ResponsiveUtils.iconSize(sizeClass) * 1.5f)
                                    )
                                }

                                Spacer(Modifier.height(24.dp))

                                // LARGE DONUT CHART (Centered)
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    val donutSize = if (isExpanded) 180.dp else 140.dp
                                    val strokeWidth = if (isExpanded) 48f else 38f

                                    Canvas(modifier = Modifier.size(donutSize)) {
                                        val sweepPrincipal = (prinPct / 100f * 360f).toFloat()
                                        drawArc(
                                            color = blueAccent,
                                            startAngle = -90f,
                                            sweepAngle = sweepPrincipal,
                                            useCenter = false,
                                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                        )
                                        drawArc(
                                            color = goldAccent,
                                            startAngle = -90f + sweepPrincipal,
                                            sweepAngle = (360f - sweepPrincipal),
                                            useCenter = false,
                                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                        )
                                    }

                                    // Center text inside donut
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            "${prinPct.toInt()}%",
                                            color = blueAccent,
                                            fontSize = ResponsiveUtils.titleFontSize(sizeClass),
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text("Principal", color = secondaryText, fontSize = ResponsiveUtils.bodyFontSize(sizeClass).value.sp * 0.8f)
                                    }
                                }

                                Spacer(Modifier.height(16.dp))

                                // Percentage labels below donut
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(Modifier.size(10.dp).background(blueAccent, CircleShape))
                                        Spacer(Modifier.width(6.dp))
                                        Text("Principal ${prinPct.toInt()}%", color = primaryText, fontSize = ResponsiveUtils.bodyFontSize(sizeClass))
                                    }
                                    Spacer(Modifier.width(20.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(Modifier.size(10.dp).background(goldAccent, CircleShape))
                                        Spacer(Modifier.width(6.dp))
                                        Text("Interest ${intPct.toInt()}%", color = primaryText, fontSize = ResponsiveUtils.bodyFontSize(sizeClass))
                                    }
                                }

                                Spacer(Modifier.height(28.dp))

                                // Totals Row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Total Interest", color = secondaryText, fontSize = ResponsiveUtils.bodyFontSize(sizeClass).value.sp * 0.8f)
                                        Text(formatMoney(totalInterest), color = greenAccent, fontSize = ResponsiveUtils.bodyFontSize(sizeClass).value.sp * 1.1f, fontWeight = FontWeight.Bold)
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Total Payment", color = secondaryText, fontSize = ResponsiveUtils.bodyFontSize(sizeClass).value.sp * 0.8f)
                                        Text(formatMoney(totalPayment), color = primaryText, fontSize = ResponsiveUtils.bodyFontSize(sizeClass).value.sp * 1.1f, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    // ==================== DYNAMIC SMART RECOMMENDATIONS ====================
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = primaryCard),
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(1.dp, borderColor)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Smart Recommendations", color = primaryText, fontSize = ResponsiveUtils.titleFontSize(sizeClass).value.sp * 0.8f, fontWeight = FontWeight.SemiBold)
                                Spacer(Modifier.width(8.dp))
                                Surface(color = Color(0xFF3B2A6E), shape = RoundedCornerShape(20.dp)) {
                                    Text("PRO", color = Color(0xFFB39DFF), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 9.dp, vertical = 2.dp))
                                }
                            }

                            Spacer(Modifier.height(16.dp))

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                contentPadding = PaddingValues(end = 16.dp)
                            ) {
                                items(recommendations, key = { it.id }) { rec ->
                                    val cardWidth = if (isExpanded) 220.dp else 160.dp
                                    Column(
                                        modifier = Modifier
                                            .width(cardWidth)
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(Color(0xFF0A1D3D))
                                            .clickable { 
                                                haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                                                selectedRecommendation = rec 
                                            }
                                            .border(
                                                1.dp,
                                                if (rec.isRecommended) rec.accentColor else borderColor,
                                                RoundedCornerShape(16.dp)
                                            )
                                            .padding(16.dp)
                                    ) {
                                        if (rec.isRecommended) {
                                            Text(
                                                "RECOMMENDED",
                                                color = rec.accentColor,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(bottom = 8.dp)
                                            )
                                        }
                                        Icon(imageVector = rec.icon, contentDescription = null, tint = rec.accentColor, modifier = Modifier.size(28.dp))
                                        Spacer(Modifier.height(12.dp))
                                        Text(rec.title, color = primaryText, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                        Spacer(Modifier.height(4.dp))
                                        Text(rec.description, color = secondaryText, fontSize = 12.sp, lineHeight = 16.sp)
                                    }
                                }
                            }
                        }
                    }

                    // ==================== LOAN INTELLIGENCE (Integrated) ====================
                    if (hasValidInput) {
                        com.aistudio.loanmaster.xklzmw.LoanIntelligenceCard(
                            loanType = loanType,
                            loanAmount = loanAmount,
                            interestRate = interestRate,
                            tenureYears = tenureYears,
                            monthlyEmi = monthlyEmi,
                            totalInterest = totalInterest,
                            totalPayment = totalPayment,
                            sizeClass = sizeClass
                        )
                    }

                    // ==================== AMORTIZATION SCHEDULE ====================
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = primaryCard),
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(1.dp, borderColor)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Amortization Schedule", color = primaryText, fontSize = ResponsiveUtils.titleFontSize(sizeClass).value.sp * 0.75f, fontWeight = FontWeight.SemiBold)
                                Text("Full Schedule ›", color = blueAccent, fontSize = ResponsiveUtils.bodyFontSize(sizeClass).value.sp * 0.9f, modifier = Modifier.clickable { 
                                    haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                                    showFullSchedule = true 
                                })
                            }
                            Spacer(Modifier.height(12.dp))

                            val scheduleData = if (yearBreakdown.size <= 4) yearBreakdown else yearBreakdown.take(3) + listOf(yearBreakdown.last())

                            scheduleData.forEachIndexed { index, row ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Y${row.year}", color = if (index == scheduleData.lastIndex) secondaryText else primaryText, fontSize = ResponsiveUtils.bodyFontSize(sizeClass), modifier = Modifier.weight(1f))
                                    Text(formatMoney(row.emi), color = primaryText, fontSize = ResponsiveUtils.bodyFontSize(sizeClass), modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                                    Text(formatMoney(row.principalPaid), color = Color(0xFF22C55E), fontSize = ResponsiveUtils.bodyFontSize(sizeClass), modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                                    Text(formatMoney(row.interestPaid), color = Color(0xFFFFC328), fontSize = ResponsiveUtils.bodyFontSize(sizeClass), modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                                }
                                if (index < scheduleData.lastIndex) {
                                    HorizontalDivider(color = borderColor.copy(alpha = 0.35f))
                                }
                            }
                        }
                    }

                    // ==================== LOAN MILESTONES ====================
                    if (hasValidInput && monthlySchedule.isNotEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = primaryCard),
                            shape = RoundedCornerShape(18.dp),
                            border = BorderStroke(1.dp, borderColor)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Loan Milestones", color = primaryText, fontSize = ResponsiveUtils.titleFontSize(sizeClass).value.sp * 0.75f, fontWeight = FontWeight.SemiBold)
                                Spacer(Modifier.height(12.dp))

                                var cumPrincipal = 0.0
                                var m25: Int? = null
                                var m50: Int? = null
                                var m75: Int? = null
                                var mPrinExceedsInt: Int? = null

                                for (item in monthlySchedule) {
                                    cumPrincipal += item.principalPaid
                                    if (m25 == null && cumPrincipal >= loanAmount * 0.25) m25 = item.month
                                    if (m50 == null && cumPrincipal >= loanAmount * 0.50) m50 = item.month
                                    if (m75 == null && cumPrincipal >= loanAmount * 0.75) m75 = item.month
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
                                    "Final EMI" to formatMonthYear(monthlySchedule.last().month)
                                )

                                milestoneRows.forEachIndexed { index, pair ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(modifier = Modifier.size(6.dp).clip(androidx.compose.foundation.shape.CircleShape).background(blueAccent))
                                            Spacer(Modifier.width(8.dp))
                                            Text(pair.first, color = secondaryText, fontSize = ResponsiveUtils.bodyFontSize(sizeClass))
                                        }
                                        Text(pair.second, color = primaryText, fontSize = ResponsiveUtils.bodyFontSize(sizeClass), fontWeight = FontWeight.Medium)
                                    }
                                    if (index < milestoneRows.lastIndex) {
                                        HorizontalDivider(color = borderColor.copy(alpha = 0.3f))
                                    }
                                }
                            }
                        }
                    }

                    // ==================== BOTTOM ACTIONS ====================
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)) {
                        OutlinedButton(
                            onClick = { 
                                haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                            },
                            modifier = Modifier.weight(1f).height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, borderColor)
                        ) {
                            Icon(Icons.Rounded.PictureAsPdf, contentDescription = null, modifier = Modifier.size(ResponsiveUtils.iconSize(sizeClass)))
                            Spacer(Modifier.width(8.dp))
                            Text("Export PDF", fontSize = ResponsiveUtils.bodyFontSize(sizeClass))
                        }
                        Button(
                            onClick = { 
                                haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                            },
                            modifier = Modifier.weight(1f).height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F2744))
                        ) {
                            Icon(Icons.Rounded.Share, contentDescription = null, modifier = Modifier.size(ResponsiveUtils.iconSize(sizeClass)))
                            Spacer(Modifier.width(8.dp))
                            Text("Share", fontSize = ResponsiveUtils.bodyFontSize(sizeClass))
                        }
                    }
                } // closes Column inside AnimatedVisibility
            } // closes AnimatedVisibility
             } // closes Column of resultsSection
            } // closes resultsSection lambda
            ) // closes ResponsiveScreenWrapper
        } // closes Box
    } // closes Scaffold

    // FULL SCHEDULE DIALOG
    if (showFullSchedule && hasValidInput) {
        FullAmortizationDialog(principal = loanAmount, annualRate = interestRate, totalMonths = totalMonths, onDismiss = { showFullSchedule = false })
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


