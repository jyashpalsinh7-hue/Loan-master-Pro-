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
                            placeholder = "0",
                            icon = Icons.Rounded.AccountBalanceWallet,
                            iconTint = blueAccent
                        )
                        PremiumInputFieldEmi(
                            modifier = Modifier.weight(1f),
                            label = "Interest Rate (p.a.)",
                            value = interestRateText,
                            onValueChange = { interestRateText = it },
                            placeholder = "0",
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
                            placeholder = "0",
                            icon = Icons.Rounded.DateRange,
                            iconTint = blueAccent,
                            trailingIcon = Icons.Rounded.KeyboardArrowDown,
                            textSuffix = " Years"
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            PremiumInputFieldEmi(
                                label = "First EMI Date (Optional)",
                                value = firstEmiDate,
                                placeholder = "Select Date",
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
                                        Icon(Icons.Rounded.Info, contentDescription = null, tint = secondaryText, modifier = Modifier.size(14.dp))
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
                                    Icon(Icons.Rounded.MonetizationOn, contentDescription = null, tint = goldAccent, modifier = Modifier.size(26.dp).align(Alignment.BottomStart).offset(x = (-6).dp, y = 6.dp))
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
                                Icon(Icons.Rounded.Info, contentDescription = null, tint = secondaryText, modifier = Modifier.size(14.dp))
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
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val textColor = if (isCurrent) goldAccent else primaryText
                                    Text(if(isCurrent) "${"%.2f".format(Locale.US, rate)}% (Current)" else "${"%.1f".format(Locale.US, rate)}%", color = textColor, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                    Text(formatMoney(emiAtRate), color = textColor, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                }
                                HorizontalDivider(color = borderColor.copy(alpha = 0.5f))
                            }
                            Row(modifier = Modifier.fillMaxWidth().padding(top=12.dp), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
                                Text("View More Rates", color = blueAccent, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = null, tint = blueAccent, modifier = Modifier.size(16.dp))
                            }
                        }
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
                            Text("Year", color = secondaryText, fontSize = 10.sp, modifier = Modifier.weight(0.5f))
                            Text("EMI (₹)", color = secondaryText, fontSize = 10.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                            Text("Principal (₹)", color = secondaryText, fontSize = 10.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                            Text("Interest (₹)", color = secondaryText, fontSize = 10.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                            Text("Balance (₹)", color = secondaryText, fontSize = 10.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
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
                            ScheduleRowEmi("${tenureYears}", "23,754", "23,430", "324", "0", blueAccent)
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
    trailingIcon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    textSuffix: String = ""
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, color = Color(0xFFA8B3D1), fontSize = 12.sp, maxLines = 1)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .background(Color(0xFF071833), RoundedCornerShape(12.dp))
                .border(1.dp, Color(0xFF183C8A), RoundedCornerShape(12.dp))
                .padding(horizontal = 14.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(10.dp))
                BasicTextField(
                    value = if(value == "0") "" else value,
                    onValueChange = onValueChange,
                    textStyle = TextStyle(color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    maxLines = 1,
                    modifier = Modifier.weight(1f),
                    decorationBox = { innerTextField ->
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            if (label == "Loan Amount" && value.isNotEmpty() && value != "0") {
                                Text("₹", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                if (value.isEmpty() || value == "0") Text(placeholder, color = Color.Gray, fontSize = 18.sp)
                                innerTextField()
                            }
                            if (textSuffix.isNotEmpty()) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(textSuffix, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.wrapContentWidth())
                            }
                        }
                    }
                )
                if (trailingIcon != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(trailingIcon, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                }
            }
        }
    }
}

@Composable
fun HeroMetricEmi(title: String, value: String, color: Color) {
    Column {
        Text(title, color = Color(0xFFA8B3D1), fontSize = 9.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, color = color, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun LegendItemDonutEmi(color: Color, title: String, value: String, percent: String) {
    Row(verticalAlignment = Alignment.Top) {
        Box(modifier = Modifier.padding(top=4.dp).size(8.dp).background(color, CircleShape))
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(title, color = Color.White, fontSize = 12.sp)
            Text(value, color = Color(0xFFA8B3D1), fontSize = 12.sp)
            Text(percent, color = Color(0xFFA8B3D1), fontSize = 11.sp)
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
        Text(col1, color = color, fontSize = 12.sp, modifier = Modifier.weight(0.5f))
        Text(col2, color = color, fontSize = 12.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
        Text(col3, color = color, fontSize = 12.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
        Text(col4, color = color, fontSize = 12.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
        Text(col5, color = color, fontSize = 12.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
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
                Text(title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Text(subtitle, color = Color(0xFFA8B3D1), fontSize = 9.sp, lineHeight = 12.sp)
            }
            Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
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
        softWrap = false,
        onTextLayout = { result ->
            if (result.hasVisualOverflow && scaledFontSize > 16.sp) {
                scaledFontSize = (scaledFontSize.value - 2f).sp
            }
        }
    )
}
