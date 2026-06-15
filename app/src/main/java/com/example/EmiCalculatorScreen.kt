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
import androidx.compose.ui.geometry.Offset
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
    var loanAmountText by remember { mutableStateOf("0") }
    var interestRateText by remember { mutableStateOf("0") }
    var tenureYearsText by remember { mutableStateOf("0") }
    var firstEmiDate by remember { mutableStateOf("Select Date") }

    val loanAmount = loanAmountText.toDoubleOrNull() ?: 0.0
    val interestRate = interestRateText.toDoubleOrNull() ?: 0.0
    val tenureYears = tenureYearsText.toIntOrNull() ?: 0

    val totalMonths = tenureYears * 12
    val r = if (interestRate > 0) (interestRate / 12) / 100 else 0.0

    val monthlyEmi = if (loanAmount > 0 && r > 0 && totalMonths > 0) {
        loanAmount * (r * (1 + r).pow(totalMonths)) / ((1 + r).pow(totalMonths) - 1)
    } else 0.0

    val totalPayment = monthlyEmi * totalMonths
    val totalInterest = totalPayment - loanAmount
    val totalPrincipal = loanAmount

    val hasValidInput = loanAmount > 0 && interestRate > 0 && tenureYears > 0

    val formatMoney = { amt: Double ->
        val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
        format.maximumFractionDigits = 0
        format.format(amt)
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
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
                            icon = Icons.Rounded.AccountBalanceWallet,
                            iconTint = blueAccent
                        )
                        PremiumInputFieldEmi(
                            modifier = Modifier.weight(1f),
                            label = "Interest Rate (p.a.)",
                            value = interestRateText,
                            onValueChange = { interestRateText = it },
                            icon = Icons.Rounded.Percent,
                            iconTint = blueAccent
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
                        PremiumInputFieldEmi(
                            modifier = Modifier.weight(1f),
                            label = "Tenure",
                            value = tenureYearsText,
                            onValueChange = { tenureYearsText = it },
                            icon = Icons.Rounded.DateRange,
                            iconTint = blueAccent,
                            trailingIcon = Icons.Rounded.KeyboardArrowDown,
                            textSuffix = " Years"
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            PremiumInputFieldEmi(
                                label = "First EMI Date (Optional)",
                                value = firstEmiDate,
                                onValueChange = {},
                                icon = Icons.Rounded.DateRange,
                                iconTint = blueAccent,
                                trailingIcon = Icons.Rounded.KeyboardArrowDown
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
                                        Icon(Icons.Rounded.Info, tint = secondaryText, modifier = Modifier.size(14.dp))
                                    }
                                    Spacer(Modifier.height(6.dp))
                                    Text(
                                        text = formatMoney(monthlyEmi),
                                        color = blueAccent,
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                // Nice illustration
                                Box(modifier = Modifier.size(78.dp), contentAlignment = Alignment.Center) {
                                    Icon(Icons.Rounded.CalendarMonth, contentDescription = null, tint = blueAccent, modifier = Modifier.size(62.dp))
                                    Icon(Icons.Rounded.CurrencyRupee, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp).offset(y = 3.dp))
                                    Icon(Icons.Rounded.MonetizationOn, contentDescription = null, tint = goldAccent, modifier = Modifier.size(26.dp).align(Alignment.BottomStart).offset(x = -6.dp, y = 6.dp))
                                    Icon(Icons.Rounded.BarChart, contentDescription = null, tint = greenAccent, modifier = Modifier.size(30.dp).align(Alignment.BottomEnd).offset(x = 10.dp, y = 8.dp))
                                }
                            }

                            Spacer(Modifier.height(18.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                HeroMetricEmi("Total Interest Payable", formatMoney(totalInterest), greenAccent)
                                VerticalDivider(thickness = 1.dp, color = borderColor, modifier = Modifier.height(32.dp))
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Total Payment", color = secondaryText, fontSize = 10.sp)
                                    Text(formatMoney(totalPayment), color = primaryText, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    Text("(Principal + Interest)", color = secondaryText, fontSize = 9.sp)
                                }
                                VerticalDivider(thickness = 1.dp, color = borderColor, modifier = Modifier.height(32.dp))
                                HeroMetricEmi("Total Principal", formatMoney(totalPrincipal), purpleAccent)
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Principal vs Interest Donut
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = primaryCard),
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(1.dp, borderColor)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Principal vs Interest", color = primaryText, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                                Spacer(Modifier.width(6.dp))
                                Icon(Icons.Rounded.Info, tint = secondaryText, modifier = Modifier.size(14.dp))
                            }
                            Spacer(Modifier.height(12.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.weight(0.42f), contentAlignment = Alignment.Center) {
                                    val prinPct = if (totalPayment > 0) (totalPrincipal / totalPayment).toFloat() else 0f
                                    Canvas(modifier = Modifier.size(92.dp)) {
                                        drawArc(color = blueAccent, startAngle = -90f, sweepAngle = 360f * prinPct, useCenter = false, style = Stroke(width = 34f, cap = StrokeCap.Butt))
                                        drawArc(color = goldAccent, startAngle = -90f + (360f * prinPct), sweepAngle = 360f * (1 - prinPct), useCenter = false, style = Stroke(width = 34f, cap = StrokeCap.Butt))
                                    }
                                    // Center text
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("${(prinPct * 100).toInt()}%", color = primaryText, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                        Text("Principal", color = secondaryText, fontSize = 10.sp)
                                    }
                                }
                                Column(modifier = Modifier.weight(0.58f).padding(start = 8.dp)) {
                                    val prinPctStr = String.format(Locale.US, "%.1f", (totalPrincipal / totalPayment) * 100)
                                    val intPctStr = String.format(Locale.US, "%.1f", (totalInterest / totalPayment) * 100)
                                    LegendItemDonutEmi(blueAccent, "Principal", formatMoney(totalPrincipal), "($prinPctStr%)")
                                    Spacer(Modifier.height(14.dp))
                                    LegendItemDonutEmi(goldAccent, "Interest", formatMoney(totalInterest), "($intPctStr%)")
                                }
                            }
                        }
                    }

                    // Interest Rate Comparison
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = primaryCard),
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(1.dp, borderColor)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Interest Rate Comparison", color = primaryText, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                                Spacer(Modifier.width(6.dp))
                                Icon(Icons.Rounded.Info, tint = secondaryText, modifier = Modifier.size(14.dp))
                            }
                            Spacer(Modifier.height(10.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Interest Rate (p.a.)", color = secondaryText, fontSize = 11.sp)
                                Text("Monthly EMI", color = secondaryText, fontSize = 11.sp)
                            }
                            Spacer(Modifier.height(6.dp))
                            HorizontalDivider(color = borderColor)

                            val rates = listOf(8.0, 8.5, 9.0, 9.5)
                            rates.forEach { rate ->
                                val isCurrent = rate == 8.5
                                val emiAtRate = if (loanAmount > 0 && rate > 0) {
                                    val mr = rate / 12 / 100
                                    loanAmount * (mr * (1 + mr).pow(totalMonths)) / ((1 + mr).pow(totalMonths) - 1)
                                } else 0.0

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp)
                                        .background(if (isCurrent) Color(0xFF1A3A6E) else Color.Transparent, RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        "\( {rate}% \){if (isCurrent) " (Current)" else ""}",
                                        color = if (isCurrent) goldAccent else primaryText,
                                        fontSize = 13.sp,
                                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                                    )
                                    Text(
                                        formatMoney(emiAtRate),
                                        color = if (isCurrent) goldAccent else primaryText,
                                        fontSize = 13.sp,
                                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                            Text("View More Rates ›", color = blueAccent, fontSize = 12.sp, modifier = Modifier.align(Alignment.End).padding(top = 4.dp))
                        }
                    }
                }
            }

            // SMART RECOMMENDATIONS + AMORTIZATION + BUTTONS (only when valid input)
            if (hasValidInput) {
                // Smart Recommendations
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = primaryCard),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, borderColor)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row {
                            Text("Smart Recommendations", color = primaryText, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                            Spacer(Modifier.width(8.dp))
                            Surface(color = Color(0xFF3B2A6E), shape = RoundedCornerShape(20.dp)) {
                                Text("4 Strategies", color = Color(0xFFB39DFF), fontSize = 11.sp, modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp))
                            }
                        }
                        Text("Potential savings up to ₹6.4L", color = greenAccent, fontSize = 18.sp, fontWeight = FontWeight.Bold)

                        Spacer(Modifier.height(12.dp))
                        Button(
                            onClick = {},
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6B4EFF)),
                            shape = RoundedCornerShape(50.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Rounded.PlayArrow, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Watch Ad To Unlock", color = Color.White)
                        }

                        Spacer(Modifier.height(14.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(
                                "Best Savings" to Icons.Rounded.EmojiEvents,
                                "Fastest Closure" to Icons.Rounded.Bolt,
                                "Lowest EMI" to Icons.Rounded.TrendingDown,
                                "AI Recommended" to Icons.Rounded.SmartToy
                            ).forEach { (title, icon) ->
                                Column(
                                    modifier = Modifier.weight(1f).background(Color(0xFF0F2744), RoundedCornerShape(12.dp)).padding(vertical = 10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(icon, contentDescription = null, tint = Color(0xFF6B4EFF), modifier = Modifier.size(22.dp))
                                    Spacer(Modifier.height(6.dp))
                                    Text(title, color = primaryText, fontSize = 11.sp, textAlign = TextAlign.Center)
                                    Spacer(Modifier.height(4.dp))
                                    Icon(Icons.Rounded.Lock, contentDescription = "Locked", tint = Color(0xFF888888), modifier = Modifier.size(14.dp))
                                }
                            }
                        }
                    }
                }

                // Amortization Schedule
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = primaryCard),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, borderColor)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Amortization Schedule", color = primaryText, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                            Text("View Full Schedule ›", color = blueAccent, fontSize = 12.sp)
                        }
                        Spacer(Modifier.height(10.dp))

                        val scheduleData = listOf(
                            listOf("Year", "EMI (₹)", "Principal (₹)", "Interest (₹)", "Balance (₹)"),
                            listOf("1", formatMoney(monthlyEmi).replace("₹", ""), "1,25,054", "1,12,700", "13,74,946"),
                            listOf("2", formatMoney(monthlyEmi).replace("₹", ""), "1,34,613", "1,09,141", "12,40,333"),
                            listOf("240", formatMoney(monthlyEmi).replace("₹", ""), "23,430", "324", "0")
                        )

                        scheduleData.forEachIndexed { index, row ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp, horizontal = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                row.forEach { cell ->
                                    Text(cell, color = if (index == 0) secondaryText else primaryText, fontSize = 11.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                                }
                            }
                            if (index < scheduleData.lastIndex) HorizontalDivider(color = borderColor.copy(alpha = 0.5f))
                        }
                    }
                }

                // PDF + Share
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = {},
                        modifier = Modifier.weight(1f).height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, borderColor)
                    ) {
                        Icon(Icons.Rounded.PictureAsPdf, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("PDF Report", fontSize = 14.sp)
                    }
                    Button(
                        onClick = {},
                        modifier = Modifier.weight(1f).height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F2744))
                    ) {
                        Icon(Icons.Rounded.Share, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Share Result", fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

// ============ HELPER COMPOSABLES ============

@Composable
fun PremiumInputFieldEmi(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    trailingIcon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    textSuffix: String = ""
) {
    Column(modifier = modifier) {
        Text(label, color = Color(0xFFA8B3D1), fontSize = 12.sp)
        Spacer(Modifier.height(6.dp))
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF071833),
            border = BorderStroke(1.dp, Color(0xFF183C8A))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(10.dp))
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    textStyle = TextStyle(color = Color.White, fontSize = 15.sp),
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                if (textSuffix.isNotEmpty()) Text(textSuffix, color = Color(0xFF9AA6C8), fontSize = 13.sp)
                if (trailingIcon != null) Icon(trailingIcon, contentDescription = null, tint = Color(0xFF9AA6C8), modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
fun HeroMetricEmi(label: String, value: String, accent: Color) {
    Column {
        Text(label, color = Color(0xFFA8B3D1), fontSize = 10.sp)
        Spacer(Modifier.height(2.dp))
        Text(value, color = accent, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun LegendItemDonutEmi(color: Color, label: String, value: String, percent: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(12.dp).background(color, CircleShape))
        Spacer(Modifier.width(8.dp))
        Column {
            Text(label, color = Color.White, fontSize = 13.sp)
            Text("$value $percent", color = Color(0xFFA8B3D1), fontSize = 12.sp)
        }
    }
}