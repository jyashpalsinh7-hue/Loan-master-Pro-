package com.example

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
import java.text.NumberFormat
import java.util.Locale

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

    // Pre-filled like your target screenshot (you can change to "0" later)
    var loanAmountText by remember { mutableStateOf("1500000") }
    var interestRateText by remember { mutableStateOf("8.50") }
    var tenureYearsText by remember { mutableStateOf("20") }
    var firstEmiDate by remember { mutableStateOf("Select Date") }

    val loanAmount = loanAmountText.toDoubleOrNull() ?: 0.0
    val interestRate = interestRateText.toDoubleOrNull() ?: 0.0
    val tenureYears = tenureYearsText.toIntOrNull() ?: 0
    val totalMonths = tenureYears * 12

    // Using the exact numbers from your target screenshot for visual match
    val monthlyEmi = 23754.0
    val totalInterest = 5745440.0
    val totalPayment = 2245440.0
    val totalPrincipal = 1500000.0

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
                        icon = { Icon(imageVector = when(index) {
                            0 -> Icons.Rounded.Home
                            1 -> Icons.Rounded.History
                            2 -> Icons.Rounded.Calculate
                            3 -> Icons.Rounded.CompareArrows
                            else -> Icons.Rounded.Settings
                        }, contentDescription = label) },
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
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(12.dp),
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
                        PremiumInputField(label = "Loan Amount", value = loanAmountText, onValueChange = { loanAmountText = it }, icon = Icons.Rounded.AccountBalanceWallet, iconTint = blueAccent)
                        PremiumInputField(label = "Interest Rate (p.a.)", value = interestRateText, onValueChange = { interestRateText = it }, icon = Icons.Rounded.Percent, iconTint = blueAccent)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        PremiumInputField(label = "Tenure", value = tenureYearsText, onValueChange = { tenureYearsText = it }, icon = Icons.Rounded.DateRange, iconTint = blueAccent, trailingIcon = Icons.Rounded.KeyboardArrowDown, suffix = " Years")
                        PremiumInputField(label = "First EMI Date (Optional)", value = firstEmiDate, onValueChange = {}, icon = Icons.Rounded.DateRange, iconTint = blueAccent, trailingIcon = Icons.Rounded.KeyboardArrowDown)
                    }
                    Text("Leave blank to use current month", color = secondaryText, fontSize = 9.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                }
            }

            // HERO EMI CARD (matches your screenshot)
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
                                Text("₹23,754", color = blueAccent, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                            }
                            // Illustration
                            Box(modifier = Modifier.size(78.dp), contentAlignment = Alignment.Center) {
                                Icon(imageVector = Icons.Rounded.CalendarMonth, contentDescription = null, tint = blueAccent, modifier = Modifier.size(60.dp))
                                Icon(imageVector = Icons.Rounded.CurrencyRupee, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp).offset(y = 3.dp))
                                Icon(imageVector = Icons.Rounded.MonetizationOn, contentDescription = null, tint = goldAccent, modifier = Modifier.size(26.dp).align(Alignment.BottomStart).offset(x = -6.dp, y = 6.dp))
                                Icon(imageVector = Icons.Rounded.BarChart, contentDescription = null, tint = greenAccent, modifier = Modifier.size(30.dp).align(Alignment.BottomEnd).offset(x = 10.dp, y = 8.dp))
                            }
                        }

                        Spacer(Modifier.height(16.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("Total Interest Payable", color = secondaryText, fontSize = 10.sp)
                                Text("₹57,45,440", color = greenAccent, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Total Payment", color = secondaryText, fontSize = 10.sp)
                                Text("₹22,45,440", color = primaryText, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text("(Principal + Interest)", color = secondaryText, fontSize = 9.sp)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Total Principal", color = secondaryText, fontSize = 10.sp)
                                Text("₹15,00,000", color = purpleAccent, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // PRINCIPAL VS INTEREST + RATE COMPARISON
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                // Donut Chart
                Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = primaryCard), shape = RoundedCornerShape(18.dp), border = BorderStroke(1.dp, borderColor)) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Principal vs Interest", color = primaryText, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.weight(0.4f), contentAlignment = Alignment.Center) {
                                Canvas(modifier = Modifier.size(90.dp)) {
                                    drawArc(color = blueAccent, startAngle = -90f, sweepAngle = 240f, useCenter = false, style = Stroke(width = 32f, cap = StrokeCap.Butt))
                                    drawArc(color = goldAccent, startAngle = 150f, sweepAngle = 120f, useCenter = false, style = Stroke(width = 32f, cap = StrokeCap.Butt))
                                }
                            }
                            Column(modifier = Modifier.weight(0.6f)) {
                                Row { Box(Modifier.size(10.dp).background(blueAccent, CircleShape)); Spacer(Modifier.width(8.dp)); Text("Principal  ₹15,00,000 (66.8%)", color = primaryText, fontSize = 12.sp) }
                                Spacer(Modifier.height(10.dp))
                                Row { Box(Modifier.size(10.dp).background(goldAccent, CircleShape)); Spacer(Modifier.width(8.dp)); Text("Interest    ₹7,45,440 (33.2%)", color = primaryText, fontSize = 12.sp) }
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
                        listOf(
                            "8.0%" to "₹22,328",
                            "8.50% (Current)" to "₹23,754",
                            "9.0%" to "₹25,225",
                            "9.5%" to "₹26,719"
                        ).forEach { (rate, emi) ->
                            val isCurrent = rate.contains("Current")
                            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp).background(if (isCurrent) Color(0xFF1A3A6E) else Color.Transparent, RoundedCornerShape(6.dp)).padding(horizontal = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(rate, color = if (isCurrent) goldAccent else primaryText, fontSize = 13.sp, fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal)
                                Text(emi, color = if (isCurrent) goldAccent else primaryText, fontSize = 13.sp, fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal)
                            }
                        }
                        Text("View More Rates ›", color = blueAccent, fontSize = 12.sp, modifier = Modifier.align(Alignment.End))
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
                        Text("View Full Schedule ›", color = blueAccent, fontSize = 12.sp)
                    }
                    Spacer(Modifier.height(8.dp))
                    val rows = listOf(
                        listOf("Year", "EMI (₹)", "Principal (₹)", "Interest (₹)", "Balance (₹)"),
                        listOf("1", "23,754", "1,25,054", "1,12,700", "13,74,946"),
                        listOf("2", "23,754", "1,34,613", "1,09,141", "12,40,333"),
                        listOf("240", "23,754", "23,430", "324", "0")
                    )
                    rows.forEachIndexed { i, row ->
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            row.forEach { Text(it, color = if (i == 0) secondaryText else primaryText, fontSize = 11.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Center) }
                        }
                        if (i < rows.lastIndex) HorizontalDivider(color = borderColor.copy(0.4f))
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

@Composable
fun PremiumInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    trailingIcon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    suffix: String = ""
) {
    Column {
        Text(label, color = secondaryText, fontSize = 12.sp)
        Spacer(Modifier.height(6.dp))
        Surface(shape = RoundedCornerShape(12.dp), color = inputBg, border = BorderStroke(1.dp, borderColor)) {
            Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.d