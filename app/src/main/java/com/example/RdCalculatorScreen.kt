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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.pow

@Composable
fun RdCalculatorScreen(onNavigateBack: () -> Unit) {
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val sizeClass = when {
        configuration.screenWidthDp < 600 -> WindowWidthSizeClass.Compact
        configuration.screenWidthDp < 840 -> WindowWidthSizeClass.Medium
        else -> WindowWidthSizeClass.Expanded
    }

    var monthlyDepositText by remember { mutableStateOf("5000") }
    var interestRatePaText by remember { mutableStateOf("6.5") }
    var tenureYearsText by remember { mutableStateOf("5") }
    var compoundingFrequency by remember { mutableStateOf("Quarterly") }
    var showCompoundingDropdown by remember { mutableStateOf(false) }



    val formatInr = { value: Double ->
        val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
        format.maximumFractionDigits = 0
        format.format(value).replace("₹", "₹")
    }
    
    val formatDec = { value: Double ->
        val s = String.format(Locale.US, "%.2f", value)
        if (s.endsWith(".00")) s.substring(0, s.length - 3) else s
    }

    val p = monthlyDepositText.safeToDouble()
    val annualRate = interestRatePaText.safeToDouble() / 100
    val t = tenureYearsText.safeToDouble().coerceIn(0.0, 100.0)
    val months = (t * 12).toInt()
    
    val n = when (compoundingFrequency) {
        "Yearly" -> 1.0
        "Half-Yearly" -> 2.0
        "Quarterly" -> 4.0
        "Monthly" -> 12.0
        else -> 4.0
    }
    
    // RD calculation with different compounding frequencies
    // M = P * \sum_{i=1}^{months} (1 + R/N)^{N * (months - i + 1)/12}
    var maturityValue = 0.0
    if (annualRate > 0 && months > 0) {
        for (i in 1..months) {
            val remainingTimeYears = (months - i + 1) / 12.0
            maturityValue += p * (1 + annualRate / n).pow(n * remainingTimeYears)
        }
    } else {
        maturityValue = p * months
    }
    
    val totalInvested = p * months
    val totalReturns = maturityValue - totalInvested
    val wealthGain = if (totalInvested > 0) (totalReturns / totalInvested) * 100 else 0.0



    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(BackgroundDark).statusBarsPadding()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary,
                        modifier = Modifier.size(24.dp).clickable { onNavigateBack() }
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("RD Calculator", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text("Plan your Recurring Deposit", color = TextSecondary, fontSize = 12.sp)
                    }
                    Icon(imageVector = Icons.Rounded.StarBorder, contentDescription = "Favorite", tint = TextPrimary, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(imageVector = Icons.Rounded.Share, contentDescription = "Share", tint = TextPrimary, modifier = Modifier.size(24.dp))
                }
            }
        },
        bottomBar = { AppBottomBar(selectedRoute = "calculators") },
        containerColor = BackgroundDark
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            ResponsiveScreenWrapper(
                widthSizeClass = sizeClass,
                animationTriggerState = maturityValue,
                headerSection = { },
                inputControlsSection = {
                    // Inputs
                    Column(verticalArrangement = Arrangement.spacedBy(ResponsiveUtils.cardSpacing(sizeClass)), modifier = Modifier.fillMaxWidth()) {
                if (sizeClass == WindowWidthSizeClass.Compact) {
                    PremiumInputField(
                        label = "Monthly Deposit", value = monthlyDepositText, onValueChange = { monthlyDepositText = it },
                        icon = Icons.Rounded.CurrencyRupee, iconTint = AccentBlue, sizeClass = sizeClass, modifier = Modifier.fillMaxWidth()
                    )
                    PremiumInputField(
                        label = "Interest Rate (p.a.)", value = interestRatePaText, onValueChange = { interestRatePaText = it },
                        icon = Icons.Rounded.Percent, iconTint = Color(0xFF7C4DFF), sizeClass = sizeClass, modifier = Modifier.fillMaxWidth()
                    )
                    PremiumInputField(
                        label = "Tenure", value = tenureYearsText, onValueChange = { tenureYearsText = it },
                        icon = Icons.Rounded.DateRange, iconTint = AccentGreen, trailingIcon = Icons.Rounded.KeyboardArrowDown, suffix = " Yrs", sizeClass = sizeClass, modifier = Modifier.fillMaxWidth()
                    )
                    Box(modifier = Modifier.fillMaxWidth()) {
                        PremiumInputField(
                            label = "Compounding", value = compoundingFrequency, onValueChange = {}, readOnly = true, onClick = { showCompoundingDropdown = true },
                            icon = Icons.Rounded.BarChart, iconTint = AccentBlue, trailingIcon = Icons.Rounded.KeyboardArrowDown, sizeClass = sizeClass, modifier = Modifier.fillMaxWidth(),
                            infoText = "How often interest is calculated and added to your principal. Banks usually compound RD interest quarterly."
                        )
                        DropdownMenu(
                            expanded = showCompoundingDropdown,
                            onDismissRequest = { showCompoundingDropdown = false },
                            modifier = Modifier.background(SurfaceDark).fillMaxWidth(0.9f)
                        ) {
                            listOf("Yearly", "Half-Yearly", "Quarterly", "Monthly").forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option, color = TextPrimary) },
                                    onClick = {
                                        compoundingFrequency = option
                                        showCompoundingDropdown = false
                                    }
                                )
                            }
                        }
                    }
                } else {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
                        Box(modifier = Modifier.weight(1f)) {
                            PremiumInputField(
                                label = "Monthly Deposit", value = monthlyDepositText, onValueChange = { monthlyDepositText = it },
                                icon = Icons.Rounded.CurrencyRupee, iconTint = AccentBlue, sizeClass = sizeClass
                            )
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            PremiumInputField(
                                label = "Interest Rate (p.a.)", value = interestRatePaText, onValueChange = { interestRatePaText = it },
                                icon = Icons.Rounded.Percent, iconTint = Color(0xFF7C4DFF), sizeClass = sizeClass
                            )
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
                        Box(modifier = Modifier.weight(1f)) {
                            PremiumInputField(
                                label = "Tenure", value = tenureYearsText, onValueChange = { tenureYearsText = it },
                                icon = Icons.Rounded.DateRange, iconTint = AccentGreen, trailingIcon = Icons.Rounded.KeyboardArrowDown, suffix = " Yrs", sizeClass = sizeClass
                            )
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            PremiumInputField(
                                label = "Compounding", value = compoundingFrequency, onValueChange = {}, readOnly = true, onClick = { showCompoundingDropdown = true },
                                icon = Icons.Rounded.BarChart, iconTint = AccentBlue, trailingIcon = Icons.Rounded.KeyboardArrowDown, sizeClass = sizeClass,
                                infoText = "How often interest is calculated and added to your principal. Banks usually compound RD interest quarterly."
                            )
                            DropdownMenu(
                                expanded = showCompoundingDropdown,
                                onDismissRequest = { showCompoundingDropdown = false },
                                modifier = Modifier.background(SurfaceDark).fillMaxWidth(0.9f)
                            ) {
                                listOf("Yearly", "Half-Yearly", "Quarterly", "Monthly").forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option, color = TextPrimary) },
                                        onClick = {
                                            compoundingFrequency = option
                                            showCompoundingDropdown = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        resultsSection = {
            Column(verticalArrangement = Arrangement.spacedBy(ResponsiveUtils.cardSpacing(sizeClass))) {
                // Hero Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceDark)
                    .border(1.dp, CardStroke, RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(modifier = Modifier.weight(0.65f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Estimated Maturity Value", color = TextSecondary, fontSize = 14.sp, modifier = Modifier.padding(horizontal = 4.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(Icons.Rounded.Info, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(14.dp))
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            val formattedMaturityValue = formatInr(maturityValue)
                            AutoResizedText(
                                text = formattedMaturityValue,
                                color = AccentYellow,
                                fontSize = 38.sp,
                                fontWeight = FontWeight.ExtraBold,
                                maxLines = 1,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .weight(0.35f)
                                .height(80.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Brush.radialGradient(colors = listOf(AccentBlue.copy(alpha = 0.2f), Color.Transparent))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Savings,
                                contentDescription = null,
                                tint = AccentBlue.copy(alpha = 0.8f),
                                modifier = Modifier.size(64.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    HorizontalDivider(color = CardStroke)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.padding(end = 24.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Total Invested", color = TextSecondary, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 4.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Rounded.Info, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(12.dp))
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(formatInr(totalInvested), color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp))
                        }
                        Column(modifier = Modifier.padding(end = 24.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Total Returns", color = TextSecondary, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 4.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Rounded.Info, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(12.dp))
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(formatInr(totalReturns), color = AccentGreen, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp))
                        }
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Wealth Gain", color = TextSecondary, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 4.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Rounded.Info, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(12.dp))
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("${formatDec(wealthGain)}%", color = AccentGreen, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp))
                        }
                    }
                }
            }

            // Visuals
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                // Growth Chart
                Column(
                    modifier = Modifier.weight(1f).clip(RoundedCornerShape(12.dp)).background(SurfaceDark).border(1.dp, CardStroke, RoundedCornerShape(12.dp)).padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        ScrollingTitleText("Investment Growth", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Rounded.Info, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(14.dp))
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(AccentBlue))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Maturity Value", color = TextSecondary, fontSize = 9.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(AccentGreen))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Total Invested", color = TextSecondary, fontSize = 9.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth().height(140.dp)) {
                        Column(modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.SpaceBetween) {
                            Text("120L", color = TextSecondary, fontSize = 9.sp)
                            Text("90L", color = TextSecondary, fontSize = 9.sp)
                            Text("60L", color = TextSecondary, fontSize = 9.sp)
                            Text("30L", color = TextSecondary, fontSize = 9.sp)
                            Text("0", color = TextSecondary, fontSize = 9.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                                Canvas(modifier = Modifier.fillMaxSize().padding(vertical = 4.dp)) {
                                    val h = size.height
                                    val w = size.width
                                    val investedPath = Path().apply {
                                        moveTo(0f, h)
                                        lineTo(w, h * 0.75f)
                                    }
                                    val maturityPath = Path().apply {
                                        moveTo(0f, h)
                                        cubicTo(w * 0.4f, h, w * 0.7f, h * 0.6f, w, 0f)
                                    }
                                    drawPath(investedPath, color = AccentGreen, style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round))
                                    drawPath(maturityPath, color = AccentBlue, style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round))
                                }
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("0", color = TextSecondary, fontSize = 9.sp)
                                Text("5Y", color = TextSecondary, fontSize = 9.sp)
                                Text("10Y", color = TextSecondary, fontSize = 9.sp)
                                Text("15Y", color = TextSecondary, fontSize = 9.sp)
                                Text("20Y", color = TextSecondary, fontSize = 9.sp)
                            }
                        }
                    }
                }
                
                // Breakup Chart
                Column(
                    modifier = Modifier.weight(1f).clip(RoundedCornerShape(12.dp)).background(SurfaceDark).border(1.dp, CardStroke, RoundedCornerShape(12.dp)).padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        ScrollingTitleText("Breakup at Maturity", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Rounded.Info, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(14.dp))
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Box(modifier = Modifier.size(100.dp), contentAlignment = Alignment.Center) {
                            val invPct = if (maturityValue > 0) (totalInvested / maturityValue).toFloat() else 1f
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val strokeWidth = 24.dp.toPx()
                                drawArc(
                                    color = AccentBlue,
                                    startAngle = 90f,
                                    sweepAngle = 360f * invPct,
                                    useCenter = false,
                                    style = Stroke(strokeWidth, cap = StrokeCap.Butt)
                                )
                                drawArc(
                                    color = AccentYellow,
                                    startAngle = 90f + (360f * invPct),
                                    sweepAngle = 360f * (1 - invPct),
                                    useCenter = false,
                                    style = Stroke(strokeWidth, cap = StrokeCap.Butt)
                                )
                            }
                        }
                        Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                            val invPctStr = String.format(Locale.US, "%.1f%%", if (maturityValue > 0) (totalInvested/maturityValue)*100 else 100f)
                            val retPctStr = String.format(Locale.US, "%.1f%%", if (maturityValue > 0) (totalReturns/maturityValue)*100 else 0f)
                            RdLegend("Total Invested", formatInr(totalInvested), "($invPctStr)", AccentBlue, Modifier.padding(end = 8.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            RdLegend("Total Returns", formatInr(totalReturns), "($retPctStr)", AccentYellow, Modifier.padding(end = 8.dp))
                        }
                    }
                }
            }

            // What if Banner
            Row(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(SurfaceDark).border(1.dp, CardStroke, RoundedCornerShape(8.dp)).padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Rounded.EmojiObjects, contentDescription = null, tint = Color(0xFF7C4DFF), modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("What if?", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    ScrollingTitleText("Adjust values and see how your wealth changes", color = TextSecondary, fontSize = 11.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(6.dp)).border(1.dp, CardStroke, RoundedCornerShape(6.dp)).clickable { }.padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("Explore Scenarios ->", color = TextPrimary, fontSize = 12.sp)
                }
            }

            // Projection Summary
            Column(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(SurfaceDark).border(1.dp, CardStroke, RoundedCornerShape(12.dp)).padding(top = 16.dp, bottom = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("RD Projection Summary", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Rounded.Info, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(14.dp))
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { }) {
                        Text("View Full Yearly Report", color = AccentBlue, fontSize = 12.sp)
                        Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = AccentBlue, modifier = Modifier.size(16.dp))
                    }
                }
                
                Column(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
                    Row(modifier = Modifier.defaultMinSize(minWidth = 380.dp).fillMaxWidth().background(CardStroke.copy(alpha = 0.5f)).padding(vertical = 10.dp, horizontal = 16.dp)) {
                        Text("Year", color = TextSecondary, fontSize = 12.sp, modifier = Modifier.weight(1f).padding(horizontal = 4.dp))
                        Text("Total Invested", color = TextSecondary, fontSize = 12.sp, modifier = Modifier.weight(1.5f).padding(horizontal = 4.dp), textAlign = TextAlign.Center)
                        Text("Est. Returns", color = TextSecondary, fontSize = 12.sp, modifier = Modifier.weight(1.5f).padding(horizontal = 4.dp), textAlign = TextAlign.Center)
                        Text("Maturity Value", color = TextSecondary, fontSize = 12.sp, modifier = Modifier.weight(1.5f).padding(horizontal = 4.dp), textAlign = TextAlign.End)
                    }
                    
                    val yearsList = listOf(1.0, 3.0, 5.0, 10.0)
                    yearsList.forEachIndexed { index, y ->
                        val tMonths = (y * 12).toInt()
                        val tInvested = p * tMonths
                        var tMat = 0.0
                        if (annualRate > 0 && tMonths > 0) {
                            for (i in 1..tMonths) {
                                val remainingTimeYears = (tMonths - i + 1) / 12.0
                                tMat += p * (1 + annualRate / n).pow(n * remainingTimeYears)
                            }
                        } else {
                            tMat = p * tMonths
                        }
                        val tRet = tMat - tInvested
                        val isLast = index == yearsList.size - 1
                        val color = if (isLast) AccentGreen else TextPrimary
                        
                        Row(modifier = Modifier.defaultMinSize(minWidth = 380.dp).fillMaxWidth().padding(vertical = 12.dp, horizontal = 16.dp)) {
                            Text("${y.toInt()} Years", color = TextPrimary, fontSize = 12.sp, modifier = Modifier.weight(1f).padding(horizontal = 4.dp))
                            Text(formatInr(tInvested), color = TextPrimary, fontSize = 12.sp, modifier = Modifier.weight(1.5f).padding(horizontal = 4.dp), textAlign = TextAlign.Center)
                            Text(formatInr(tRet), color = TextPrimary, fontSize = 12.sp, modifier = Modifier.weight(1.5f).padding(horizontal = 4.dp), textAlign = TextAlign.Center)
                            Text(formatInr(tMat), color = color, fontSize = 12.sp, fontWeight = if(isLast) FontWeight.Bold else FontWeight.Normal, modifier = Modifier.weight(1.5f).padding(horizontal = 4.dp), textAlign = TextAlign.End)
                        }
                        if (!isLast) HorizontalDivider(color = CardStroke)
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text("* Values are rounded off", color = TextSecondary, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 16.dp))
            }

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                RdActionButton("RD Schedule", "Monthly Breakdown", Icons.Rounded.DateRange, AccentBlue)
                RdActionButton("Charts", "Visual Analysis", Icons.Rounded.PieChart, Color(0xFF7C4DFF))
                RdActionButton("Download Report", "Save as PDF", Icons.Rounded.Download, AccentGreen)
                RdActionButton("Share Results", "Share Projection", Icons.Rounded.Share, AccentYellow)
            }
          }
        }
    )
  }
 }
}

@Composable
fun RdInputCard(title: String, value: String, subValue: String?, icon: ImageVector, iconColor: Color, onClick: () -> Unit = {}) {
    ResponsiveCard(
        minWidth = 140.dp,
        onClick = onClick,
        modifier = Modifier.wrapContentWidth()
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(iconColor.copy(alpha = 0.2f)), contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(14.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                ScrollingTitleText(title, color = TextSecondary, fontSize = 10.sp)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.padding(end = 8.dp)) {
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
fun RdLegend(label: String, value: String, percentage: String, color: Color, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
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
fun RdActionButton(title: String, subtitle: String, icon: ImageVector, color: Color) {
    Row(
        modifier = Modifier.defaultMinSize(minWidth = 160.dp).clip(RoundedCornerShape(8.dp)).background(SurfaceDark).border(1.dp, CardStroke, RoundedCornerShape(8.dp)).clickable { }.padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(color.copy(alpha = 0.2f)), contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f, fill = false)) {
            ScrollingTitleText(title, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            ScrollingTitleText(subtitle, color = TextSecondary, fontSize = 10.sp)
        }
    }
}
