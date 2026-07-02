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
import androidx.compose.material.icons.automirrored.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.example.ui.theme.LoanMasterTheme
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
            description = "Save ${formatMoney(baseInterest - int1)}",
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
            description = "Finish ${(totalMonths - m2) / 12} Years Early",
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
            icon = Icons.AutoMirrored.Rounded.TrendingDown,
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
            description = "Save ${formatMoney(baseInterest - int4)}\n& Close Faster",
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
            // Compact Toggle Button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF2D7DFF).copy(alpha = 0.15f))
                    .clickable { 
                        haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                        onToggleIsMonths(!isMonths) 
                    }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isMonths) "Mo" else "Yrs",
                    color = Color(0xFF2D7DFF),
                    fontSize = ResponsiveUtils.bodyFontSize(sizeClass).value.sp * 0.9f,
                    fontWeight = FontWeight.Bold
                )
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
    var isDropdownExpanded by rememberSaveable { mutableStateOf(false) }
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

    Dialog(onDismissRequest = onDismiss, properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)) {
        val bgColor = Color(0xFF061633)
        val headerColor = Color(0xFF0A2150)
        val primaryText = Color.White
        val secondaryText = Color(0xFFA8B3D1)
        val blueAccent = Color(0xFF2D7DFF)
        val greenAccent = Color(0xFF22C55E)
        val goldAccent = Color(0xFFFFC328)
        val borderColor = Color(0xFF183C8A)

        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(LoanMasterTheme.components.cardRadius),
            colors = CardDefaults.cardColors(containerColor = bgColor)
        ) {
            Column(modifier = Modifier.padding(LoanMasterTheme.spacing.md)) {
                Text(
                    "Full Monthly Amortization Schedule",
                    color = primaryText,
                    style = LoanMasterTheme.typography.title,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "${totalMonths} months • EMI: ${formatMoney(emi)}",
                    color = secondaryText,
                    style = LoanMasterTheme.typography.body
                )

                Spacer(Modifier.height(LoanMasterTheme.spacing.md))

                // Column Headers
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(headerColor, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Mo.", color = secondaryText, style = LoanMasterTheme.typography.label, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(0.9f))
                    Text("EMI", color = secondaryText, style = LoanMasterTheme.typography.label, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1.1f), textAlign = TextAlign.End)
                    Text("Prin", color = secondaryText, style = LoanMasterTheme.typography.label, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1.2f), textAlign = TextAlign.End)
                    Text("Int", color = secondaryText, style = LoanMasterTheme.typography.label, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1.1f), textAlign = TextAlign.End)
                    Text("Bal", color = secondaryText, style = LoanMasterTheme.typography.label, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1.2f), textAlign = TextAlign.End)
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
                            Text("M${row.month}", color = secondaryText, style = LoanMasterTheme.typography.label, modifier = Modifier.weight(0.9f))
                            AutoResizedText(formatMoney(row.emi), color = primaryText, fontSize = LoanMasterTheme.typography.label.fontSize, modifier = Modifier.weight(1.1f).wrapContentWidth(Alignment.End))
                            AutoResizedText(formatMoney(row.principalPaid), color = greenAccent, fontSize = LoanMasterTheme.typography.label.fontSize, modifier = Modifier.weight(1.2f).wrapContentWidth(Alignment.End))
                            AutoResizedText(formatMoney(row.interestPaid), color = goldAccent, fontSize = LoanMasterTheme.typography.label.fontSize, modifier = Modifier.weight(1.1f).wrapContentWidth(Alignment.End))
                            AutoResizedText(formatMoney(row.remainingBalance), color = primaryText, fontSize = LoanMasterTheme.typography.label.fontSize, modifier = Modifier.weight(1.2f).wrapContentWidth(Alignment.End))
                        }
                        if (row.month < schedule.size) {
                            HorizontalDivider(color = borderColor.copy(alpha = 0.3f), thickness = 0.5.dp)
                        }
                    }
                }

                Spacer(Modifier.height(LoanMasterTheme.spacing.md))

                Row(horizontalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.sm), modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = {
                            haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f).height(LoanMasterTheme.components.buttonHeight),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, borderColor)
                    ) {
                        Text("Close", color = primaryText)
                    }
                    val context = androidx.compose.ui.platform.LocalContext.current
                    Button(
                        onClick = { 
                            haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                            ExportUtils.exportToPdf(
                                context,
                                "Amortization Schedule",
                                schedule.map { "Month ${it.month}" to "EMI: ${formatMoney(it.emi)}, Prin: ${formatMoney(it.principalPaid)}, Int: ${formatMoney(it.interestPaid)}, Bal: ${formatMoney(it.remainingBalance)}" }
                            )
                        },
                        modifier = Modifier.weight(1f).height(LoanMasterTheme.components.buttonHeight),
                        colors = ButtonDefaults.buttonColors(containerColor = blueAccent),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Rounded.Download, contentDescription = null, modifier = Modifier.size(LoanMasterTheme.components.iconSmall))
                        Spacer(Modifier.width(LoanMasterTheme.spacing.xs))
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
fun EmiCalculatorScreen(
    onNavigateBack: () -> Unit = {},
    historyViewModel: HistoryViewModel? = null,
    initialHistory: CalculationHistory? = null,
    onHistoryConsumed: () -> Unit = {}
) {
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

    // RESPONSIVE: use rememberSaveable
    var loanAmountText by rememberSaveable { mutableStateOf(initialHistory?.param1 ?: "") }
    var interestRateText by rememberSaveable { mutableStateOf(initialHistory?.param2 ?: "") }
    var tenureInputText by rememberSaveable { mutableStateOf(initialHistory?.param3 ?: "") }
    var isTenureInMonths by rememberSaveable { mutableStateOf(initialHistory?.param4 == "true") }
    var loanType by rememberSaveable { mutableStateOf(initialHistory?.param5?.takeIf { it.isNotEmpty() } ?: "Home Loan") }
    var showFullSchedule by rememberSaveable { mutableStateOf(false) }
    
    var currentHistoryId by rememberSaveable { mutableStateOf(initialHistory?.id ?: 0) }

    LaunchedEffect(initialHistory) {
        if (initialHistory != null) {
            onHistoryConsumed()
        }
    }
    
    LaunchedEffect(loanAmountText, interestRateText, tenureInputText, isTenureInMonths, loanType) {
        kotlinx.coroutines.delay(2000)
        val loanAmount = loanAmountText.toDoubleOrNull() ?: 0.0
        val interestRate = interestRateText.toDoubleOrNull() ?: 0.0
        val tenureInput = tenureInputText.toDoubleOrNull() ?: 0.0
        val hasValidInput = loanAmount > 0 && interestRate > 0 && tenureInput > 0
        if (historyViewModel != null && hasValidInput) {
            val history = CalculationHistory(
                id = currentHistoryId,
                calculatorType = "EMI",
                title = "$loanType - ${formatMoney(loanAmount)}",
                param1 = loanAmountText,
                param2 = interestRateText,
                param3 = tenureInputText,
                param4 = isTenureInMonths.toString(),
                param5 = loanType
            )
            historyViewModel.insert(history) { id ->
                currentHistoryId = id
            }
        }
    }
    
    // Bottom Sheet State
    // RESPONSIVE: use rememberSaveable
    var selectedRecommendation by rememberSaveable { mutableStateOf<SmartRecommendation?>(null) }
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
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { 
                    Column {
                        Text("EMI Calculator", color = primaryText, fontSize = 20.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text("Calculate your loan EMI and plan better", color = secondaryText, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { 
                        haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                        onNavigateBack() 
                    }) {
                        Icon(imageVector = Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back", tint = primaryText)
                    }
                },
                actions = {
                    val context = androidx.compose.ui.platform.LocalContext.current
                    IconButton(onClick = {
                        ExportUtils.exportToPdf(
                            context,
                            "EMI Calculator Report",
                            listOf(
                                "Loan Amount" to formatMoney(loanAmount.toDouble()),
                                "Interest Rate" to "$interestRate%",
                                "Tenure" to "$tenureYears Years ${totalMonths % 12} Months",
                                "" to "",
                                "Monthly EMI" to formatMoney(monthlyEmi),
                                "Total Interest" to formatMoney(totalInterest),
                                "Total Payment" to formatMoney(totalPayment)
                            )
                        )
                    }) {
                        Icon(imageVector = Icons.Rounded.PictureAsPdf, contentDescription = "Export to PDF", tint = primaryText)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = bgColor)
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            ResponsiveScreenWrapper(
                widthSizeClass = sizeClass,
                animationTriggerState = monthlyEmi,
                headerSection = { },
                inputControlsSection = {
                    // INPUT SECTION
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = primaryCard),
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(1.dp, borderColor)
                    ) {
                        Column(modifier = Modifier.padding(LoanMasterTheme.spacing.md), verticalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.md)) {
                            com.example.ui.theme.AdaptiveRowCol(
                                columns = LoanMasterTheme.grids.calculatorColumns,
                                content1 = { modifier ->
                                    PremiumInputField(
                                        label = "Loan Amount", value = loanAmountText, onValueChange = { loanAmountText = it },
                                        icon = Icons.Rounded.AccountBalanceWallet, iconTint = blueAccent,
                                        sizeClass = sizeClass, modifier = modifier
                                    )
                                },
                                content2 = { modifier ->
                                    PremiumInputField(
                                        label = "Interest Rate (p.a.)", value = interestRateText, onValueChange = { interestRateText = it },
                                        icon = Icons.Rounded.Percent, iconTint = blueAccent,
                                        sizeClass = sizeClass, modifier = modifier,
                                        infoText = "The annual interest rate charged on your loan."
                                    )
                                }
                            )
                            com.example.ui.theme.AdaptiveRowCol(
                                columns = LoanMasterTheme.grids.calculatorColumns,
                                content1 = { modifier ->
                                    TenureInputField(
                                        label = "Tenure", value = tenureInputText, onValueChange = { tenureInputText = it },
                                        isMonths = isTenureInMonths, onToggleIsMonths = { isTenureInMonths = it },
                                        icon = Icons.Rounded.DateRange, iconTint = blueAccent,
                                        inputBg = inputBg, borderColor = borderColor, secondaryText = secondaryText,
                                        sizeClass = sizeClass, modifier = modifier
                                    )
                                },
                                content2 = { modifier ->
                                    LoanTypeSelector(
                                        selectedType = loanType, onTypeSelected = { loanType = it }, inputBg = inputBg, borderColor = borderColor, secondaryText = secondaryText,
                                        sizeClass = sizeClass, modifier = modifier
                                    )
                                }
                            )
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
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Your Monthly EMI", color = secondaryText, fontSize = ResponsiveUtils.bodyFontSize(sizeClass).value.sp * 0.9f)
                                        AutoResizedText(
                                            text = formatMoney(animatedEmi.toDouble()),
                                            color = blueAccent,
                                            fontSize = ResponsiveUtils.titleFontSize(sizeClass).value.sp * 1.5f,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                    Spacer(Modifier.width(LoanMasterTheme.spacing.md))
                                    Icon(
                                        imageVector = Icons.Rounded.CalendarMonth,
                                        contentDescription = null,
                                        tint = blueAccent.copy(alpha = 0.3f),
                                        modifier = Modifier.size(ResponsiveUtils.iconSize(sizeClass) * 1.5f)
                                    )
                                }

                                Spacer(Modifier.height(24.dp))

                                // Cost Breakdown Stacked Bar
                                Spacer(Modifier.height(16.dp))
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    val prinPctFloat = if (prinPct.isNaN() || prinPct.isInfinite()) 0f else prinPct.toFloat()
                                    val intPctFloat = if (intPct.isNaN() || intPct.isInfinite()) 0f else intPct.toFloat()
                                    Row(modifier = Modifier.fillMaxWidth().height(16.dp).clip(RoundedCornerShape(8.dp))) {
                                        if (prinPctFloat > 0) {
                                            Box(modifier = Modifier.weight(prinPctFloat).fillMaxHeight().background(blueAccent))
                                        }
                                        if (intPctFloat > 0) {
                                            Box(modifier = Modifier.weight(intPctFloat).fillMaxHeight().background(goldAccent))
                                        }
                                    }
                                    Spacer(Modifier.height(12.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                            Box(Modifier.size(10.dp).background(blueAccent, CircleShape))
                                            Spacer(Modifier.width(6.dp))
                                            AutoResizedText(text = "Principal ${prinPct.toInt()}%", color = primaryText, fontSize = ResponsiveUtils.bodyFontSize(sizeClass))
                                        }
                                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.End) {
                                            Box(Modifier.size(10.dp).background(goldAccent, CircleShape))
                                            Spacer(Modifier.width(6.dp))
                                            AutoResizedText(text = "Interest ${intPct.toInt()}%", color = primaryText, fontSize = ResponsiveUtils.bodyFontSize(sizeClass))
                                        }
                                    }
                                }

                                Spacer(Modifier.height(28.dp))

                                // Totals Row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                                        Text("Total Interest", color = secondaryText, fontSize = ResponsiveUtils.bodyFontSize(sizeClass).value.sp * 0.8f)
                                        AutoResizedText(text = formatMoney(totalInterest), color = greenAccent, fontSize = ResponsiveUtils.bodyFontSize(sizeClass).value.sp * 1.1f, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(Modifier.width(LoanMasterTheme.spacing.sm))
                                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                                        Text("Total Payment", color = secondaryText, fontSize = ResponsiveUtils.bodyFontSize(sizeClass).value.sp * 0.8f)
                                        AutoResizedText(text = formatMoney(totalPayment), color = primaryText, fontSize = ResponsiveUtils.bodyFontSize(sizeClass).value.sp * 1.1f, fontWeight = FontWeight.Bold)
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
                                        Text(rec.title, color = secondaryText, fontSize = 12.sp)
                                        Spacer(Modifier.height(4.dp))
                                        Text(rec.description, color = rec.accentColor, fontSize = 16.sp, fontWeight = FontWeight.Bold, lineHeight = 20.sp)
                                    }
                                }
                            }
                        }
                    }

                    // ==================== LOAN INTELLIGENCE (Integrated) ====================
                    if (hasValidInput) {
                        LoanIntelligenceCard(
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

                            Row(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Year", color = secondaryText, fontSize = ResponsiveUtils.bodyFontSize(sizeClass).value.sp * 0.8f, modifier = Modifier.weight(1f))
                                Text("EMI Paid", color = secondaryText, fontSize = ResponsiveUtils.bodyFontSize(sizeClass).value.sp * 0.8f, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                                Text("Principal", color = secondaryText, fontSize = ResponsiveUtils.bodyFontSize(sizeClass).value.sp * 0.8f, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                                Text("Interest", color = secondaryText, fontSize = ResponsiveUtils.bodyFontSize(sizeClass).value.sp * 0.8f, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                            }
                            HorizontalDivider(color = borderColor.copy(alpha = 0.5f))

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
                        val context = androidx.compose.ui.platform.LocalContext.current
                        OutlinedButton(
                            onClick = { 
                                haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                                ExportUtils.exportToPdf(
                                    context,
                                    "EMI Calculator Report",
                                    listOf(
                                        "Loan Amount" to formatMoney(loanAmount.toDouble()),
                                        "Interest Rate" to "$interestRate%",
                                        "Tenure" to "$tenureYears Years ${totalMonths % 12} Months",
                                        "" to "",
                                        "Monthly EMI" to formatMoney(monthlyEmi),
                                        "Total Interest" to formatMoney(totalInterest),
                                        "Total Payment" to formatMoney(totalPayment)
                                    )
                                )
                            },
                            modifier = Modifier.weight(1f).height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, borderColor)
                        ) {
                            Icon(Icons.Rounded.WorkspacePremium, contentDescription = null, modifier = Modifier.size(ResponsiveUtils.iconSize(sizeClass)), tint = goldAccent)
                            Spacer(Modifier.width(8.dp))
                            Text("Premium Report", fontSize = ResponsiveUtils.bodyFontSize(sizeClass), color = goldAccent)
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


