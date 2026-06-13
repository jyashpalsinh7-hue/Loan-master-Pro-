package com.example

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import com.example.ui.theme.*
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.pow

val AccentGreen = Color(0xFF00C853)
val AccentPurple = Color(0xFF8E24AA)

enum class EditType { PRINCIPAL, RATE, TENURE }

@Composable
fun EmiCalculatorScreen(onNavigateBack: () -> Unit) {
    var principal by remember { mutableDoubleStateOf(1500000.0) }
    var annualInterestRate by remember { mutableDoubleStateOf(8.5) }
    var tenureYears by remember { mutableIntStateOf(20) }
    
    var editingType by remember { mutableStateOf<EditType?>(null) }
    var editValue by remember { mutableStateOf("") }

    // Math
    val r = annualInterestRate / 12 / 100
    val n = tenureYears * 12
    val emi = if (r > 0) {
        principal * r * (1 + r).pow(n) / ((1 + r).pow(n) - 1)
    } else {
        if (n > 0) principal / n else 0.0
    }
    val totalPayment = emi * n
    val totalInterest = totalPayment - principal
    
    if (editingType != null) {
        AlertDialog(
            onDismissRequest = { editingType = null },
            title = {
                Text(
                    text = when (editingType) {
                        EditType.PRINCIPAL -> "Edit Loan Amount"
                        EditType.RATE -> "Edit Interest Rate (%)"
                        EditType.TENURE -> "Edit Tenure (Years)"
                        null -> ""
                    },
                    color = TextPrimary
                )
            },
            text = {
                OutlinedTextField(
                    value = editValue,
                    onValueChange = { editValue = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = AccentYellow,
                        unfocusedBorderColor = CardStroke
                    )
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val parsed = editValue.toDoubleOrNull() ?: 0.0
                        when (editingType) {
                            EditType.PRINCIPAL -> principal = parsed
                            EditType.RATE -> annualInterestRate = parsed
                            EditType.TENURE -> tenureYears = parsed.toInt()
                            null -> {}
                        }
                        editingType = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentYellow, contentColor = BackgroundDark)
                ) {
                    Text("Save", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { editingType = null }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = SurfaceDark
        )
    }

    Scaffold(
        topBar = { EmiTopBar(onNavigateBack) },
        bottomBar = { EmiBottomBar() },
        containerColor = BackgroundDark
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            InputParametersRow(
                principal = principal, 
                rate = annualInterestRate, 
                tenure = tenureYears,
                onEditClick = { type ->
                    editingType = type
                    editValue = when (type) {
                        EditType.PRINCIPAL -> principal.toLong().toString()
                        EditType.RATE -> annualInterestRate.toString()
                        EditType.TENURE -> tenureYears.toString()
                    }
                }
            )
            EmiHeroCard(emi)
            SummaryMetricsRow(totalInterest, totalPayment)
            VisualsSection(principal, totalInterest, totalPayment, annualInterestRate, tenureYears * 12)
            WhatIfSection()
            AmortizationSchedule(principal, r, tenureYears)
            ActionButtonsRow()
        }
    }
}

@Composable
fun EmiTopBar(onNavigateBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
            contentDescription = "Back",
            tint = TextPrimary,
            modifier = Modifier
                .size(28.dp)
                .clickable { onNavigateBack() }
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = "EMI Calculator",
            color = TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Rounded.StarBorder,
            contentDescription = "Star",
            tint = TextPrimary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Icon(
            imageVector = Icons.Rounded.Share,
            contentDescription = "Share",
            tint = TextPrimary,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun EmiHeroCard(emi: Double) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceDark)
            .border(1.dp, CardStroke, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.weight(0.65f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Your Monthly EMI", color = TextSecondary, fontSize = 14.sp, modifier = Modifier.padding(horizontal = 4.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(Icons.Rounded.Info, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(14.dp))
                }
                Spacer(modifier = Modifier.height(4.dp))
                AutoResizedText(
                    text = formatInr(emi),
                    color = AccentYellow,
                    fontSize = 44.sp,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.horizontalScroll(rememberScrollState())
                ) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF00C853).copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Rounded.Check, contentDescription = null, tint = Color(0xFF00C853), modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Affordable EMI", color = Color(0xFF00C853), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("EMI to Income Ratio", color = TextSecondary, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 4.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("28%", color = Color(0xFF00C853), fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 2.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Status: ", color = TextSecondary, fontSize = 10.sp, modifier = Modifier.padding(start = 4.dp))
                    Text("Healthy", color = Color(0xFF00C853), fontSize = 10.sp, modifier = Modifier.padding(end = 4.dp))
                }
            }
            Box(
                modifier = Modifier
                    .weight(0.35f)
                    .height(100.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(AccentBlue.copy(alpha = 0.3f), Color.Transparent),
                            radius = 150f
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.AccountBalanceWallet,
                    contentDescription = null,
                    tint = AccentBlue.copy(alpha = 0.6f),
                    modifier = Modifier.size(72.dp)
                )
            }
        }
    }
}

@Composable
fun InputParametersRow(principal: Double, rate: Double, tenure: Int, onEditClick: (EditType) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        InputCard(
            title = "Loan Amount",
            value = formatInr(principal),
            subValue = null,
            icon = Icons.Rounded.Payments,
            iconColor = AccentBlue,
            modifier = Modifier.weight(1f),
            onClick = { onEditClick(EditType.PRINCIPAL) }
        )
        InputCard(
            title = "Interest Rate",
            value = String.format(Locale.US, "%.2f%%", rate),
            subValue = null,
            icon = Icons.Rounded.Percent,
            iconColor = AccentPurple,
            modifier = Modifier.weight(1f),
            onClick = { onEditClick(EditType.RATE) }
        )
        InputCard(
            title = "Tenure",
            value = "$tenure Years",
            subValue = "(${tenure * 12} Mos)",
            icon = Icons.Rounded.Event,
            iconColor = AccentGreen,
            modifier = Modifier.weight(1f),
            onClick = { onEditClick(EditType.TENURE) }
        )
    }
}

@Composable
fun InputCard(title: String, value: String, subValue: String?, icon: ImageVector, iconColor: Color, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    ResponsiveCard(
        modifier = modifier,
        onClick = onClick
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(24.dp).clip(CircleShape).background(iconColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(14.dp))
                }
                Spacer(modifier = Modifier.width(6.dp))
                ScrollingTitleText(title, color = TextSecondary, fontSize = 11.sp)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                    AutoResizedText(value, color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    if (subValue != null) {
                        ScrollingTitleText(subValue, color = TextSecondary, fontSize = 11.sp)
                    }
                }
                Icon(Icons.Rounded.Edit, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
fun SummaryMetricsRow(totalInterest: Double, totalPayment: Double) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceDark)
            .border(1.dp, CardStroke, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f).padding(end=4.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ScrollingTitleText("Total Interest Payable", color = TextSecondary, fontSize = 11.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Rounded.Info, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(12.dp))
                }
                Spacer(modifier = Modifier.height(4.dp))
                AutoResizedText(formatInr(totalInterest), color = AccentGreen, fontSize = 20.sp, fontWeight = FontWeight.Bold, maxLines = 1)
            }
            
            Box(modifier = Modifier.width(1.dp).height(40.dp).background(CardStroke))
            
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f).padding(start=4.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ScrollingTitleText("Total Payment", color = TextSecondary, fontSize = 11.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Rounded.Info, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(12.dp))
                }
                Spacer(modifier = Modifier.height(4.dp))
                AutoResizedText(formatInr(totalPayment), color = AccentBlue, fontSize = 20.sp, fontWeight = FontWeight.Bold, maxLines = 1)
            }
        }
    }
}

@Composable
fun SummaryMetricItem(title: String, value: String, color: Color, modifier: Modifier) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(title, color = TextSecondary, fontSize = 11.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Icon(Icons.Rounded.Info, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(12.dp))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, color = color, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun VisualsSection(principal: Double, totalInterest: Double, totalPayment: Double, currentRate: Double, tenureMonths: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Left Panel (Donut Chart)
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(SurfaceDark)
                .border(1.dp, CardStroke, RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Column {
                Text("Principal vs Interest", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(20.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(100.dp), contentAlignment = Alignment.Center) {
                        val principalPct = if (totalPayment > 0) (principal / totalPayment).toFloat() else 1f
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val strokeWidth = 24.dp.toPx()
                            drawArc(
                                color = AccentBlue,
                                startAngle = 90f,
                                sweepAngle = 360f * principalPct,
                                useCenter = false,
                                style = Stroke(strokeWidth, cap = StrokeCap.Butt)
                            )
                            drawArc(
                                color = AccentYellow,
                                startAngle = 90f + (360f * principalPct),
                                sweepAngle = 360f * (1 - principalPct),
                                useCenter = false,
                                style = Stroke(strokeWidth, cap = StrokeCap.Butt)
                            )
                        }
                    }
                    Column(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
                        val pPctStr = String.format(Locale.US, "%.1f%%", if (totalPayment > 0) (principal/totalPayment)*100 else 100f)
                        val iPctStr = String.format(Locale.US, "%.1f%%", if (totalPayment > 0) (totalInterest/totalPayment)*100 else 0f)
                        LegendItem("Principal", formatInr(principal), "($pPctStr)", AccentBlue)
                        Spacer(modifier = Modifier.height(12.dp))
                        LegendItem("Interest", formatInr(totalInterest), "($iPctStr)", AccentYellow)
                    }
                }
            }
        }
        
        // Right Panel (Interest Rate Comparison)
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(SurfaceDark)
                .border(1.dp, CardStroke, RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Interest Rate Comparison", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Rounded.Info, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(14.dp))
                }
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                    Text("Interest Rate (p.a.)", color = TextSecondary, fontSize = 11.sp, modifier = Modifier.weight(1f))
                    Text("Monthly EMI", color = TextSecondary, fontSize = 11.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                }
                HorizontalDivider(color = CardStroke)
                
                val calculateEmiForRate = { ratePca: Double ->
                    val r = ratePca / 12 / 100
                    val n = tenureMonths
                    if (r > 0) principal * r * (1 + r).pow(n) / ((1 + r).pow(n) - 1) else if (n > 0) principal / n else 0.0
                }
                
                val rates = listOf(8.0, currentRate, 9.0, 9.5)
                rates.forEach { rate ->
                    val isCurrent = rate == currentRate
                    val emiValue = calculateEmiForRate(rate)
                    val textColor = if (isCurrent) AccentYellow else TextPrimary
                    val label = if (isCurrent) "${String.format(Locale.US, "%.2f", rate)}% (Current)" else "${String.format(Locale.US, "%.1f", rate)}%"
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                        Text(
                            label, 
                            color = textColor, 
                            fontSize = 12.sp, 
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            formatInr(emiValue), 
                            color = textColor, 
                            fontSize = 12.sp, 
                            modifier = Modifier.weight(1f), 
                            textAlign = TextAlign.End
                        )
                    }
                    HorizontalDivider(color = CardStroke)
                }
                
                Spacer(modifier = Modifier.height(10.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
                    Text("View More Rates", color = AccentBlue, fontSize = 12.sp)
                    Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = AccentBlue, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun LegendItem(label: String, value: String, percentage: String, color: Color) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(8.dp).clip(CircleShape).background(color))
            Spacer(modifier = Modifier.width(6.dp))
            ScrollingTitleText(label, color = TextPrimary, fontSize = 12.sp)
        }
        AutoResizedText(value, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 14.dp))
        ScrollingTitleText(percentage, color = TextSecondary, fontSize = 12.sp, modifier = Modifier.padding(start = 14.dp))
    }
}

@Composable
fun LegendLabel(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(6.dp).clip(CircleShape).background(color))
        Spacer(modifier = Modifier.width(4.dp))
        Text(label, color = TextSecondary, fontSize = 10.sp)
    }
}

@Composable
fun WhatIfSection() {
    Column {
        Row(verticalAlignment = Alignment.Bottom) {
            Text("What if?", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Quick changes to see how it affects your EMI", color = TextSecondary, fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            WhatIfCard("Reduce Tenure", "By 5 Years", Icons.Rounded.Update, AccentBlue)
            WhatIfCard("Increase Loan", "By ₹5,00,000", Icons.Rounded.CurrencyRupee, AccentPurple)
            WhatIfCard("Change Interest Rate", "By ±1%", Icons.Rounded.Percent, AccentGreen)
        }
    }
}

@Composable
fun WhatIfCard(title: String, subtitle: String, icon: ImageVector, iconColor: Color) {
    Row(
        modifier = Modifier
            .defaultMinSize(minWidth = 180.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(SurfaceDark)
            .border(1.dp, CardStroke, RoundedCornerShape(8.dp))
            .clickable { }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(iconColor.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            ScrollingTitleText(title, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            ScrollingTitleText(subtitle, color = TextSecondary, fontSize = 11.sp)
        }
        Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
    }
}

@Composable
fun AmortizationSchedule(principal: Double, monthlyRate: Double, tenureYears: Int) {
    var bal = principal
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceDark)
            .border(1.dp, CardStroke, RoundedCornerShape(12.dp))
            .padding(top = 16.dp, bottom = 8.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp, start = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Amortization Schedule (First 3 Months)", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { }) {
                    Text("View Full Schedule", color = AccentBlue, fontSize = 12.sp)
                    Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = AccentBlue, modifier = Modifier.size(16.dp))
                }
            }
            
            Column(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
                // Header
                Row(modifier = Modifier.defaultMinSize(minWidth = 380.dp).fillMaxWidth().padding(vertical = 8.dp, horizontal = 12.dp).wrapContentHeight()) {
                    Text("Month", color = TextSecondary, fontSize = 12.sp, modifier = Modifier.weight(1f).padding(horizontal = 4.dp), textAlign = TextAlign.Center, maxLines = 2)
                    Text("EMI", color = TextSecondary, fontSize = 12.sp, modifier = Modifier.weight(1.5f).padding(horizontal = 4.dp), textAlign = TextAlign.Center, maxLines = 2)
                    Text("Principal", color = TextSecondary, fontSize = 12.sp, modifier = Modifier.weight(1.5f).padding(horizontal = 4.dp), textAlign = TextAlign.Center, maxLines = 2)
                    Text("Interest", color = TextSecondary, fontSize = 12.sp, modifier = Modifier.weight(1.5f).padding(horizontal = 4.dp), textAlign = TextAlign.Center, maxLines = 2)
                    Text("Balance", color = TextSecondary, fontSize = 12.sp, modifier = Modifier.weight(1.5f).padding(horizontal = 4.dp), textAlign = TextAlign.Center, maxLines = 2)
                }
            HorizontalDivider(color = CardStroke)
            
            // Generate rows
            var balance = principal
            val n = tenureYears * 12
            val actualEmi = if (monthlyRate > 0) {
                principal * monthlyRate * (1 + monthlyRate).pow(n) / ((1 + monthlyRate).pow(n) - 1)
            } else {
                if (n > 0) principal / n else 0.0
            }
            val usedEmi = if(actualEmi.isNaN() || actualEmi.isInfinite()) 0.0 else actualEmi
            
            for (i in 1..3) {
                val interest = balance * monthlyRate
                val prin = usedEmi - interest
                balance -= prin
                
                Row(modifier = Modifier.defaultMinSize(minWidth = 380.dp).fillMaxWidth().padding(vertical = 12.dp, horizontal = 12.dp).wrapContentHeight()) {
                    Text("$i", color = TextPrimary, fontSize = 12.sp, modifier = Modifier.weight(1f).padding(horizontal = 4.dp), textAlign = TextAlign.Center, maxLines = 2)
                    Text(formatInr(usedEmi), color = TextPrimary, fontSize = 12.sp, modifier = Modifier.weight(1.5f).padding(horizontal = 4.dp), textAlign = TextAlign.Center, maxLines = 2)
                    Text(formatInr(prin), color = TextPrimary, fontSize = 12.sp, modifier = Modifier.weight(1.5f).padding(horizontal = 4.dp), textAlign = TextAlign.Center, maxLines = 2)
                    Text(formatInr(interest), color = TextPrimary, fontSize = 12.sp, modifier = Modifier.weight(1.5f).padding(horizontal = 4.dp), textAlign = TextAlign.Center, maxLines = 2)
                    Text(formatInr(balance.coerceAtLeast(0.0)), color = TextPrimary, fontSize = 12.sp, modifier = Modifier.weight(1.5f).padding(horizontal = 4.dp), textAlign = TextAlign.Center, maxLines = 2)
                }
                if (i < 3) HorizontalDivider(color = CardStroke)
            }
            } // Close horizontal scroll Column
            
            Spacer(modifier = Modifier.height(4.dp))
            Text("* Values are rounded off", color = TextSecondary, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 16.dp))
        }
    }
}

@Composable
fun ActionButtonsRow() {
    Row(
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ActionButton("Schedule", "Monthly Breakdown", Icons.Rounded.CalendarMonth, AccentBlue)
        ActionButton("Charts", "Visual Analysis", Icons.Rounded.PieChart, AccentPurple)
        ActionButton("PDF Report", "Download PDF", Icons.Rounded.PictureAsPdf, AccentGreen)
        ActionButton("Share", "Share Results", Icons.Rounded.Share, AccentYellow)
    }
}

@Composable
fun ActionButton(title: String, subtitle: String, icon: ImageVector, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceDark)
            .border(1.dp, CardStroke, RoundedCornerShape(12.dp))
            .clickable { }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(color), contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = BackgroundDark, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.wrapContentWidth()) {
                ScrollingTitleText(title, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                ScrollingTitleText(subtitle, color = TextSecondary, fontSize = 10.sp)
            }
        }
    }
}

@Composable
fun EmiBottomBar() {
    NavigationBar(
        containerColor = NavBackground,
        contentColor = TextSecondary,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = false,
            onClick = { },
            icon = { Icon(Icons.Rounded.Home, contentDescription = "Home") },
            label = { Text("Home") },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary,
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = { },
            icon = { Icon(Icons.Rounded.History, contentDescription = "History") },
            label = { Text("History") },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary,
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            selected = true,
            onClick = { },
            icon = { Icon(Icons.Rounded.Calculate, contentDescription = "Calculate") },
            label = { Text("Calculate") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AccentYellow,
                selectedTextColor = AccentYellow,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary,
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = { },
            icon = { Icon(Icons.Rounded.Equalizer, contentDescription = "Compare") },
            label = { Text("Compare") },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary,
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = { },
            icon = { Icon(Icons.Rounded.Settings, contentDescription = "Settings") },
            label = { Text("Settings") },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary,
                indicatorColor = Color.Transparent
            )
        )
    }
}

fun formatInr(amount: Double): String {
    if(amount.isNaN() || amount.isInfinite()) return "₹0"
    val formatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    formatter.maximumFractionDigits = 0
    return formatter.format(amount)
}
