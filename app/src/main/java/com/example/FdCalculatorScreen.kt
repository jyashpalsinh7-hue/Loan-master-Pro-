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
fun FdCalculatorScreen(onNavigateBack: () -> Unit) {
    var depositAmount by remember { mutableDoubleStateOf(100000.0) }
    var interestRatePa by remember { mutableDoubleStateOf(7.5) }
    var tenureYears by remember { mutableDoubleStateOf(5.0) }

    var editingField by remember { mutableStateOf<String?>(null) }
    var editValue by remember { mutableStateOf("") }

    val formatInr = { value: Double ->
        val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
        format.maximumFractionDigits = 0
        format.format(value).replace("₹", "₹")
    }
    
    val formatDec = { value: Double ->
        String.format(Locale.US, "%.2f", value)
    }

    val p = depositAmount
    val r = interestRatePa / 100
    val t = tenureYears
    val n = 4.0 // Compounding quarterly
    
    val maturityValue = p * (1 + r / n).pow(n * t)
    val totalInvested = p
    val totalReturns = maturityValue - totalInvested
    val wealthGain = if (totalInvested > 0) (totalReturns / totalInvested) * 100 else 0.0

    if (editingField != null) {
        AlertDialog(
            onDismissRequest = { editingField = null },
            title = { Text("Edit $editingField", color = TextPrimary) },
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
                        when (editingField) {
                            "Deposit" -> depositAmount = parsed
                            "Interest Rate" -> interestRatePa = parsed
                            "Tenure" -> tenureYears = parsed
                        }
                        editingField = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentYellow, contentColor = BackgroundDark)
                ) { Text("Save", fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { editingField = null }) { Text("Cancel", color = TextSecondary) }
            },
            containerColor = SurfaceDark
        )
    }

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
                        Text("FD Calculator", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text("Calculate fixed deposit returns", color = TextSecondary, fontSize = 12.sp)
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
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Inputs
            Column(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
                    Box(modifier = Modifier.weight(1f)) {
                        FdInputField("Deposit Amount", formatInr(depositAmount), Icons.Rounded.AccountBalanceWallet, AccentBlue) {
                            editingField = "Deposit"
                            editValue = depositAmount.toLong().toString()
                        }
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        FdInputField("Interest Rate (p.a.)", "${formatDec(interestRatePa)}%", Icons.Rounded.Percent, AccentBlue) {
                            editingField = "Interest Rate"
                            editValue = interestRatePa.toString()
                        }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
                    Box(modifier = Modifier.weight(1f)) {
                        FdInputField("Tenure", "${tenureYears.toInt()} Years", Icons.Rounded.DateRange, AccentBlue, hasDropdown = true) {
                            editingField = "Tenure"
                            editValue = tenureYears.toInt().toString()
                        }
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        FdInputField("Compounding Frequency", "Quarterly", Icons.Rounded.BarChart, AccentBlue, hasDropdown = true) {}
                    }
                }
            }

            // Hero Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceDark)
                    .border(1.dp, CardStroke, RoundedCornerShape(12.dp))
                    .padding(20.dp)
            ) {
                Column {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(0.6f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Estimated Maturity Value", color = TextSecondary, fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(Icons.Rounded.Info, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(14.dp))
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            val formattedMaturityValue = formatInr(maturityValue)
                            AutoResizedText(
                                text = formattedMaturityValue,
                                color = AccentGreen,
                                fontSize = 42.sp,
                                fontWeight = FontWeight.ExtraBold,
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(AccentGreen.copy(alpha = 0.1f))
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val effectiveYield = (totalReturns / totalInvested) / tenureYears * 100
                                Icon(Icons.Rounded.TrendingUp, contentDescription = null, tint = AccentGreen, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Total Returns: ${formatDec((totalReturns / totalInvested) * 100)}%", color = AccentGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Box(
                            modifier = Modifier
                                .weight(0.4f)
                                .height(100.dp),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Savings,
                                contentDescription = null,
                                tint = AccentBlue.copy(alpha = 0.8f),
                                modifier = Modifier.size(80.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    HorizontalDivider(color = CardStroke)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(horizontalAlignment = Alignment.Start) {
                            Text("Invested Amount", color = TextSecondary, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(formatInr(totalInvested), color = TextPrimary, fontSize = 14.sp)
                        }
                        Box(modifier = Modifier.width(1.dp).height(30.dp).background(CardStroke))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Total Interest", color = TextSecondary, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(formatInr(totalReturns), color = TextPrimary, fontSize = 14.sp)
                        }
                        Box(modifier = Modifier.width(1.dp).height(30.dp).background(CardStroke))
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Effective Yield (CAGR)", color = TextSecondary, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("${formatDec(interestRatePa)}%", color = AccentBlue, fontSize = 14.sp, fontWeight = FontWeight.Bold)
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
                        ScrollingTitleText("Investment Growth Over Time", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Rounded.Info, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(14.dp))
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.width(12.dp).height(2.dp).background(AccentBlue))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Maturity Value", color = TextSecondary, fontSize = 9.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.width(12.dp).height(2.dp).background(AccentYellow))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Principal", color = TextSecondary, fontSize = 9.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth().height(160.dp)) {
                        Column(modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.SpaceBetween) {
                            Text("1.6L", color = TextSecondary, fontSize = 9.sp)
                            Text("1.2L", color = TextSecondary, fontSize = 9.sp)
                            Text("80K", color = TextSecondary, fontSize = 9.sp)
                            Text("40K", color = TextSecondary, fontSize = 9.sp)
                            Text("0", color = TextSecondary, fontSize = 9.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                                Canvas(modifier = Modifier.fillMaxSize().padding(vertical = 12.dp)) {
                                    val h = size.height
                                    val w = size.width
                                    val investedPath = Path().apply {
                                        moveTo(0f, h)
                                        lineTo(w * 0.25f, h * 0.9f)
                                        lineTo(w * 0.5f, h * 0.8f)
                                        lineTo(w * 0.75f, h * 0.7f)
                                        lineTo(w, h * 0.6f)
                                    }
                                    val maturityPath = Path().apply {
                                        moveTo(0f, h)
                                        lineTo(w * 0.25f, h * 0.8f)
                                        lineTo(w * 0.5f, h * 0.55f)
                                        lineTo(w * 0.75f, h * 0.25f)
                                        lineTo(w, 0f)
                                    }
                                    // Grid lines
                                    for (i in 0..4) {
                                        drawLine(CardStroke, androidx.compose.ui.geometry.Offset(0f, h * i / 4), androidx.compose.ui.geometry.Offset(w, h * i / 4), 1.dp.toPx())
                                    }
                                    for (i in 0..4) {
                                        drawLine(CardStroke, androidx.compose.ui.geometry.Offset(w * i / 4, 0f), androidx.compose.ui.geometry.Offset(w * i / 4, h), 1.dp.toPx())
                                    }
                                    
                                    drawPath(investedPath, color = AccentYellow, style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round))
                                    drawPath(maturityPath, color = AccentBlue, style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round))
                                    
                                    // Points on the lines
                                    val pointsX = listOf(0f, w * 0.25f, w * 0.5f, w * 0.75f, w)
                                    val maturityY = listOf(h, h * 0.8f, h * 0.55f, h * 0.25f, 0f)
                                    val investedY = listOf(h, h * 0.9f, h * 0.8f, h * 0.7f, h * 0.6f)
                                    
                                    for (i in 1..4) {
                                        drawCircle(color = AccentBlue, radius = 4.dp.toPx(), center = androidx.compose.ui.geometry.Offset(pointsX[i], maturityY[i]))
                                        drawCircle(color = AccentYellow, radius = 4.dp.toPx(), center = androidx.compose.ui.geometry.Offset(pointsX[i], investedY[i]))
                                    }
                                }
                                // Popup labels
                                Box(modifier = Modifier.fillMaxSize()) {
                                    Box(
                                        modifier = Modifier.align(Alignment.TopEnd).offset(x = 10.dp, y = (-10).dp)
                                            .clip(RoundedCornerShape(4.dp)).background(AccentBlue).padding(horizontal = 4.dp, vertical = 2.dp)
                                    ) { Text(formatInr(maturityValue), color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold) }
                                    Box(
                                        modifier = Modifier.align(Alignment.BottomEnd).offset(x = 10.dp, y = (-30).dp)
                                            .clip(RoundedCornerShape(4.dp)).background(AccentYellow).padding(horizontal = 4.dp, vertical = 2.dp)
                                    ) { Text(formatInr(totalInvested), color = BackgroundDark, fontSize = 9.sp, fontWeight = FontWeight.Bold) }
                                }
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("1Y", color = TextSecondary, fontSize = 9.sp)
                                Text("2Y", color = TextSecondary, fontSize = 9.sp)
                                Text("3Y", color = TextSecondary, fontSize = 9.sp)
                                Text("4Y", color = TextSecondary, fontSize = 9.sp)
                                Text("5Y", color = TextSecondary, fontSize = 9.sp)
                            }
                        }
                    }
                }
                
                // Deposit vs Interest Earned
                Column(
                    modifier = Modifier.weight(1f).clip(RoundedCornerShape(12.dp)).background(SurfaceDark).border(1.dp, CardStroke, RoundedCornerShape(12.dp)).padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        ScrollingTitleText("Deposit vs Interest Earned", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Rounded.Info, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(14.dp))
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Box(modifier = Modifier.fillMaxWidth().height(160.dp), contentAlignment = Alignment.Center) {
                        val invPct = if (maturityValue > 0) (totalInvested / maturityValue).toFloat() else 1f
                        Canvas(modifier = Modifier.size(120.dp)) {
                            val strokeWidth = 16.dp.toPx()
                            drawArc(
                                color = AccentBlue,
                                startAngle = -90f,
                                sweepAngle = 360f * invPct,
                                useCenter = false,
                                style = Stroke(strokeWidth, cap = StrokeCap.Butt)
                            )
                            drawArc(
                                color = AccentYellow,
                                startAngle = -90f + (360f * invPct),
                                sweepAngle = 360f * (1 - invPct),
                                useCenter = false,
                                style = Stroke(strokeWidth, cap = StrokeCap.Butt)
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(formatInr(maturityValue), color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Text("Total Value", color = TextSecondary, fontSize = 10.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Column(modifier = Modifier.fillMaxWidth()) {
                        val invPctStr = String.format(Locale.US, "%.1f%%", if (maturityValue > 0) (totalInvested/maturityValue)*100 else 100f)
                        val retPctStr = String.format(Locale.US, "%.1f%%", if (maturityValue > 0) (totalReturns/maturityValue)*100 else 0f)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(Modifier.size(8.dp).clip(CircleShape).background(AccentBlue))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Deposit Amount", color = TextSecondary, fontSize = 11.sp)
                            }
                            Text("${formatInr(totalInvested)} ($invPctStr)", color = TextPrimary, fontSize = 11.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(Modifier.size(8.dp).clip(CircleShape).background(AccentYellow))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Interest Earned", color = TextSecondary, fontSize = 11.sp)
                            }
                            Text("${formatInr(totalReturns)} ($retPctStr)", color = TextPrimary, fontSize = 11.sp)
                        }
                    }
                }
            }

            // What If
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text("What If?", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Quick changes to see impact", color = TextSecondary, fontSize = 12.sp)
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    WhatIfActionButton("+1%", "Rate", Icons.Rounded.TrendingUp, AccentGreen)
                    WhatIfActionButton("-1%", "Rate", Icons.Rounded.TrendingDown, Color(0xFFE53935))
                    WhatIfActionButton("+2 Years", "Tenure", Icons.Rounded.Event, Color(0xFF8E24AA))
                    WhatIfActionButton("+₹50,000", "Deposit", Icons.Rounded.AccountBalanceWallet, AccentYellow)
                }
            }

            // Projection Summary
            Column(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(SurfaceDark).border(1.dp, CardStroke, RoundedCornerShape(12.dp)).padding(vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text("FD Projection", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("(Quarterly Compounding)", color = TextSecondary, fontSize = 12.sp)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { }) {
                        Text("View Full Schedule", color = AccentBlue, fontSize = 12.sp)
                        Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = AccentBlue, modifier = Modifier.size(16.dp))
                    }
                }
                
                Column(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
                    Row(modifier = Modifier.defaultMinSize(minWidth = 380.dp).fillMaxWidth().background(CardStroke.copy(alpha = 0.5f)).padding(vertical = 10.dp, horizontal = 16.dp)) {
                        Text("Year", color = TextSecondary, fontSize = 12.sp, modifier = Modifier.weight(1f).padding(horizontal = 4.dp))
                        Text("Deposit (₹)", color = TextSecondary, fontSize = 12.sp, modifier = Modifier.weight(1.5f).padding(horizontal = 4.dp), textAlign = TextAlign.Center)
                        Text("Interest (₹)", color = TextSecondary, fontSize = 12.sp, modifier = Modifier.weight(1.5f).padding(horizontal = 4.dp), textAlign = TextAlign.End)
                        Text("Maturity Value (₹)", color = TextSecondary, fontSize = 12.sp, modifier = Modifier.weight(1.5f).padding(horizontal = 4.dp), textAlign = TextAlign.End)
                    }
                    
                    val yearsList = listOf(1.0, 2.0, 3.0, 4.0, 5.0) // Matching the screenshot exactly
                    yearsList.forEachIndexed { index, y ->
                        val tMat = p * (1 + r / n).pow(n * y)
                        val tRet = tMat - p
                        val isLast = index == yearsList.size - 1
                        val color = if (isLast) AccentBlue else TextPrimary
                        val formatInrNum = { value: Double ->
                            val format = NumberFormat.getNumberInstance(Locale("en", "IN"))
                            format.maximumFractionDigits = 0
                            format.format(value)
                        }
                        
                        Row(modifier = Modifier.defaultMinSize(minWidth = 380.dp).fillMaxWidth().padding(vertical = 12.dp, horizontal = 16.dp)) {
                            Text("${y.toInt()}", color = color, fontSize = 12.sp, modifier = Modifier.weight(1f).padding(horizontal = 4.dp))
                            Text(formatInrNum(p), color = color, fontSize = 12.sp, modifier = Modifier.weight(1.5f).padding(horizontal = 4.dp), textAlign = TextAlign.Center)
                            Text(formatInrNum(tRet), color = color, fontSize = 12.sp, modifier = Modifier.weight(1.5f).padding(horizontal = 4.dp), textAlign = TextAlign.End)
                            Text(formatInrNum(tMat), color = color, fontSize = 12.sp, modifier = Modifier.weight(1.5f).padding(horizontal = 4.dp), textAlign = TextAlign.End)
                        }
                        if (!isLast) HorizontalDivider(color = CardStroke)
                    }
                }
            }

        }
    }
}

@Composable
fun FdInputField(label: String, value: String, icon: ImageVector, iconColor: Color, hasDropdown: Boolean = false, onClick: () -> Unit) {
    Column(modifier = Modifier.wrapContentHeight()) {
        Text(label, color = TextSecondary, fontSize = 12.sp, modifier = Modifier.padding(bottom = 8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(SurfaceDark)
                .border(1.dp, CardStroke, RoundedCornerShape(8.dp))
                .clickable { onClick() }
                .padding(horizontal = 12.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(value, color = TextPrimary, fontSize = 16.sp, modifier = Modifier.weight(1f))
            if (hasDropdown) {
                Icon(Icons.Rounded.KeyboardArrowDown, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
fun FdLegend(label: String, value: String, percentage: String, color: Color, modifier: Modifier = Modifier) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
        Box(Modifier.size(8.dp).clip(CircleShape).background(color))
        Spacer(modifier = Modifier.width(6.dp))
        ScrollingTitleText(label, color = TextSecondary, fontSize = 12.sp)
        Spacer(modifier = Modifier.width(8.dp))
        Text(value, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.width(4.dp))
        Text(percentage, color = TextSecondary, fontSize = 12.sp)
    }
}

@Composable
fun WhatIfActionButton(title1: String, title2: String, icon: ImageVector, color: Color) {
    Row(
        modifier = Modifier.defaultMinSize(minWidth = 120.dp).clip(RoundedCornerShape(8.dp)).background(SurfaceDark).border(1.dp, CardStroke, RoundedCornerShape(8.dp)).clickable { }.padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f, fill = false)) {
            Text(title1, color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text(title2, color = TextSecondary, fontSize = 10.sp)
        }
    }
}
