package com.example

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.material3.LocalTextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrepaymentCalculatorScreen(sizeClass: WindowWidthSizeClass = WindowWidthSizeClass.Compact) {
    val bgColor = ResponsiveUtils.BgColor
    val surfaceColor = ResponsiveUtils.SurfaceColor
    val accentBlue = Color(0xFF3B82F6)
    val accentGreen = Color(0xFF4ADE80)
    val accentOrange = Color(0xFFF97316)
    val accentPurple = Color(0xFFA855F7)
    val heroCardTint = Color(0xFF0F291E)
    val textColor = ResponsiveUtils.TextPrimary
    val textSecondary = ResponsiveUtils.TextSecondary

    val formatMoney = { amount: Double ->
        val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
        format.maximumFractionDigits = 0
        format.format(amount)
    }

    val formatMonths = { totalMonths: Double ->
        val m = Math.round(totalMonths).toInt()
        val years = m / 12
        val months = m % 12
        if (years > 0 && months > 0) "$years Years $months Months"
        else if (years > 0) "$years Years"
        else "$months Months"
    }

    var loanAmount by remember { mutableStateOf("5000000") }
    var interestRate by remember { mutableStateOf("8.5") }
    var tenureYears by remember { mutableStateOf("15") }
    var prepaymentAmount by remember { mutableStateOf("100000") }

    val p = loanAmount.toDoubleOrNull() ?: 0.0
    val rate = interestRate.toDoubleOrNull() ?: 0.0
    val terms = tenureYears.toDoubleOrNull() ?: 0.0
    val prePay = prepaymentAmount.toDoubleOrNull() ?: 0.0

    val r = if (rate > 0) (rate / 12) / 100 else 0.0
    val n = terms * 12
    val emi = if (p > 0 && r > 0 && n > 0) p * (r * Math.pow(1 + r, n)) / (Math.pow(1 + r, n) - 1) else 0.0
    
    val originalTotalPayment = emi * n
    val originalTotalInterest = originalTotalPayment - p
    
    val newPrincipal = p - prePay
    val newTenureMonths = if (newPrincipal > 0 && emi > 0 && r > 0 && (emi - newPrincipal * r) > 0) {
        Math.log(emi / (emi - newPrincipal * r)) / Math.log(1 + r)
    } else n

    val newTotalInterest = (emi * newTenureMonths) - newPrincipal
    val interestSaved = if (originalTotalInterest > newTotalInterest) originalTotalInterest - newTotalInterest else 0.0
    val tenureReducedMonths = if (n > newTenureMonths) n - newTenureMonths else 0.0
    val roiPercentage = if (prePay > 0) (interestSaved / prePay) * 100 else 0.0

    Scaffold(
        containerColor = bgColor,
        bottomBar = {
            NavigationBar(
                containerColor = bgColor,
                contentColor = textSecondary,
                tonalElevation = 0.dp,
                modifier = Modifier.border(1.dp, surfaceColor)
            ) {
                NavigationBarItem(
                    selected = false,
                    onClick = { },
                    icon = { Icon(Icons.Rounded.Home, contentDescription = "Home") },
                    label = { Text("Home", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = textSecondary,
                        unselectedTextColor = textSecondary,
                        indicatorColor = Color.Transparent
                    )
                )
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Rounded.Calculate, contentDescription = "Calculate") },
                    label = { Text("Calculate", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = accentPurple,
                        selectedTextColor = accentPurple,
                        indicatorColor = Color.Transparent
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { },
                    icon = { Icon(Icons.Rounded.History, contentDescription = "History") },
                    label = { Text("History", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = textSecondary,
                        unselectedTextColor = textSecondary,
                        indicatorColor = Color.Transparent
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { },
                    icon = { Icon(Icons.Rounded.Article, contentDescription = "Reports") },
                    label = { Text("Reports", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = textSecondary,
                        unselectedTextColor = textSecondary,
                        indicatorColor = Color.Transparent
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { },
                    icon = { Icon(Icons.Rounded.Settings, contentDescription = "Settings") },
                    label = { Text("Settings", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = textSecondary,
                        unselectedTextColor = textSecondary,
                        indicatorColor = Color.Transparent
                    )
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(bgColor)
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(
                    horizontal = ResponsiveUtils.horizontalPadding(sizeClass),
                    vertical = ResponsiveUtils.verticalPadding(sizeClass)
                ),
            verticalArrangement = Arrangement.spacedBy(ResponsiveUtils.cardSpacing(sizeClass))
        ) {
            // A. Top App Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Back",
                    tint = textColor,
                    modifier = Modifier.clickable { }
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Loan Prepayment Calculator", color = textColor, fontSize = 18.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                    Text("See how prepayments help you save interest & reduce tenure", color = textSecondary, fontSize = 12.sp, maxLines = 1)
                }
                Icon(Icons.Rounded.StarBorder, contentDescription = "Star", tint = Color(0xFFFBBF24), modifier = Modifier.size(24.dp).clickable { })
                Spacer(modifier = Modifier.width(16.dp))
                Icon(Icons.Rounded.Share, contentDescription = "Share", tint = textColor, modifier = Modifier.size(24.dp).clickable { })
            }

            // B. Top Split Dashboard
            PrepayDashboardCards(
                sizeClass = sizeClass,
                leftCard = { cardModifier ->
                    // Left Card
                    Card(
                        modifier = cardModifier,
                        colors = CardDefaults.cardColors(containerColor = surfaceColor),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Description, contentDescription = null, tint = accentBlue, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Loan Details", color = accentBlue, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Detail Rows
                        DetailRowEditable(icon = Icons.Rounded.CalendarToday, label = "Current Loan Amount", value = formatMoney(p), onClick = { })
                        DetailRowEditable(icon = Icons.Rounded.Percent, label = "Interest Rate (p.a.)", value = "$interestRate%", onClick = { })
                        DetailRowEditable(icon = Icons.Rounded.Schedule, label = "Remaining Tenure", value = formatMonths(n), onClick = { })
                        DetailRowEditable(icon = Icons.Rounded.CalendarToday, label = "Current EMI", value = formatMoney(emi), onClick = { })

                        Spacer(modifier = Modifier.height(16.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.AccountBalanceWallet, contentDescription = null, tint = accentPurple, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Prepayment Details", color = accentPurple, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        var typeExpanded by remember { mutableStateOf(false) }
                        var prepayType by remember { mutableStateOf("One-time Lump Sum") }
                        ExposedDropdownMenuBox(
                            expanded = typeExpanded,
                            onExpandedChange = { typeExpanded = it },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            AutoResizeTextFieldPrepay(
                                value = prepayType,
                                onValueChange = {},
                                label = "Prepayment Type",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(type = MenuAnchorType.PrimaryEditable, enabled = true),
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) }
                            )
                            ExposedDropdownMenu(expanded = typeExpanded, onDismissRequest = { typeExpanded = false }, containerColor = surfaceColor) {
                                DropdownMenuItem(text = { Text("One-time Lump Sum", color = textColor) }, onClick = { prepayType = "One-time Lump Sum"; typeExpanded = false })
                                DropdownMenuItem(text = { Text("Monthly Extra EMI", color = textColor) }, onClick = { prepayType = "Monthly Extra EMI"; typeExpanded = false })
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        AutoResizeTextFieldPrepay(
                            value = prepaymentAmount,
                            onValueChange = { prepaymentAmount = it },
                            label = "Prepayment Amount (₹)",
                            modifier = Modifier.fillMaxWidth(),
                            trailingIcon = { Icon(Icons.Rounded.Edit, contentDescription = null, tint = textSecondary, modifier = Modifier.size(16.dp)) }
                        )
                    }
                    }
                },
                rightCard = { cardModifier ->
                    // Right Card (Hero)
                    Card(
                        modifier = cardModifier,
                        colors = CardDefaults.cardColors(containerColor = heroCardTint),
                        shape = RoundedCornerShape(12.dp),
                        border = borderIf(true, accentGreen.copy(alpha=0.3f))
                    ) {
                    Column(
                        modifier = Modifier.padding(16.dp).fillMaxHeight(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("TOTAL INTEREST SAVED", color = accentGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Rounded.Info, contentDescription = null, tint = accentGreen, modifier = Modifier.size(12.dp))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        AutoResizeHeroTextPrep(
                            text = formatMoney(interestSaved),
                            color = accentGreen,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth().height(160.dp)) {
                            Icon(Icons.Rounded.Savings, contentDescription = null, tint = accentGreen.copy(alpha=0.8f), modifier = Modifier.size(80.dp))
                            Icon(Icons.Rounded.ArrowUpward, contentDescription = null, tint = accentGreen, modifier = Modifier.size(40.dp).offset(x=40.dp, y=(-40).dp))
                            Icon(Icons.Rounded.MonetizationOn, contentDescription = null, tint = Color(0xFFFBBF24), modifier = Modifier.size(30.dp).offset(x=(-40).dp, y=20.dp))
                            Icon(Icons.Rounded.MonetizationOn, contentDescription = null, tint = Color(0xFFFBBF24), modifier = Modifier.size(20.dp).offset(x=(-50).dp, y=(-10).dp))
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(accentGreen.copy(alpha = 0.1f)).border(1.dp, accentGreen, RoundedCornerShape(4.dp)).padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = accentGreen, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Great Decision! You are on the right track.", color = accentGreen, fontSize = 9.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
                } // close rightCard
            )

            // C. Comparison Section
            Text("Without Prepayment vs With Prepayment", color = textColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                PrepayDashboardCards(
                    sizeClass = sizeClass,
                    leftCard = { cardModifier ->
                        Card(modifier = cardModifier, colors = CardDefaults.cardColors(containerColor = surfaceColor), shape = RoundedCornerShape(12.dp)) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Without Prepayment", color = textColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(12.dp))
                                ComparisonRow(icon = Icons.Rounded.CalendarToday, label = "Total Payment", value = formatMoney(originalTotalPayment), valueColor = textColor)
                                ComparisonRow(icon = Icons.Rounded.Schedule, label = "Total Interest", value = formatMoney(originalTotalInterest), valueColor = accentOrange)
                                ComparisonRow(icon = Icons.Rounded.Schedule, label = "Remaining Tenure", value = formatMonths(n), valueColor = accentBlue)
                                ComparisonRow(icon = Icons.Rounded.Money, label = "EMI", value = formatMoney(emi), valueColor = textColor)
                            }
                        }
                    },
                    rightCard = { cardModifier ->
                        Card(modifier = cardModifier, colors = CardDefaults.cardColors(containerColor = heroCardTint.copy(alpha=0.5f)), shape = RoundedCornerShape(12.dp), border = borderIf(true, accentGreen.copy(alpha=0.3f))) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("With Prepayment", color = accentGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(12.dp))
                                ComparisonRow(icon = Icons.Rounded.CalendarToday, label = "Total Payment", value = formatMoney(p + newTotalInterest), valueColor = accentGreen)
                                ComparisonRow(icon = Icons.Rounded.Schedule, label = "Total Interest", value = formatMoney(newTotalInterest), valueColor = accentGreen)
                                ComparisonRow(icon = Icons.Rounded.Schedule, label = "Remaining Tenure", value = formatMonths(newTenureMonths), valueColor = accentGreen)
                                ComparisonRow(icon = Icons.Rounded.Money, label = "EMI (After Prepayment)", value = formatMoney(emi), valueColor = textColor)
                            }
                        }
                    }
                )
                if (sizeClass != WindowWidthSizeClass.Compact) {
                    Box(
                        modifier = Modifier.size(32.dp).clip(CircleShape).background(Color(0xFF1E1B4B)).border(1.dp, accentPurple.copy(alpha=0.5f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("VS", color = textColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // D. 3 Metric Cards
            if (sizeClass == WindowWidthSizeClass.Compact) {
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    MetricCard(modifier = Modifier.fillMaxWidth(), icon = Icons.Rounded.Event, iconTint = accentBlue, title = "Tenure Reduced", value = formatMonths(tenureReducedMonths), valueColor = accentBlue, subtext = String.format(Locale.US, "%.1f%% reduction", if(n>0) (tenureReducedMonths/n)*100 else 0.0))
                    MetricCard(modifier = Modifier.fillMaxWidth(), icon = Icons.Rounded.Savings, iconTint = Color(0xFFFBBF24), title = "Interest Saved", value = formatMoney(interestSaved), valueColor = accentGreen, subtext = String.format(Locale.US, "%.1f%% of total interest", if(originalTotalInterest>0) (interestSaved/originalTotalInterest)*100 else 0.0))
                    MetricCard(modifier = Modifier.fillMaxWidth(), icon = Icons.Rounded.PieChart, iconTint = accentPurple, title = "ROI on Prepayment", value = String.format(Locale.US, "%.0f%%", roiPercentage), valueColor = accentPurple, subtext = "High return on prepayment")
                }
            } else {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    MetricCard(modifier = Modifier.weight(1f), icon = Icons.Rounded.Event, iconTint = accentBlue, title = "Tenure Reduced", value = formatMonths(tenureReducedMonths), valueColor = accentBlue, subtext = String.format(Locale.US, "%.1f%% reduction", if(n>0) (tenureReducedMonths/n)*100 else 0.0))
                    MetricCard(modifier = Modifier.weight(1f), icon = Icons.Rounded.Savings, iconTint = Color(0xFFFBBF24), title = "Interest Saved", value = formatMoney(interestSaved), valueColor = accentGreen, subtext = String.format(Locale.US, "%.1f%% of total interest", if(originalTotalInterest>0) (interestSaved/originalTotalInterest)*100 else 0.0))
                    MetricCard(modifier = Modifier.weight(1f), icon = Icons.Rounded.PieChart, iconTint = accentPurple, title = "ROI on Prepayment", value = String.format(Locale.US, "%.0f%%", roiPercentage), valueColor = accentPurple, subtext = "High return on prepayment")
                }
            }

            // E. Charts Section
            PrepayDashboardCards(
                sizeClass = sizeClass,
                leftCard = { cardMod ->
                    Card(modifier = cardMod.height(180.dp), colors = CardDefaults.cardColors(containerColor = surfaceColor), shape = RoundedCornerShape(12.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Break-up of Total Payment", color = textColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(accentBlue))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Principal", color = textSecondary, fontSize = 10.sp)
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(accentOrange))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Interest", color = textSecondary, fontSize = 10.sp)
                                }
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                                val ogPrinPct = if(originalTotalPayment>0) (p / originalTotalPayment).toFloat() else 0f
                                val newPrinPct = if(p + newTotalInterest > 0) (p / (p + newTotalInterest)).toFloat() else 0f
                                
                                Box(modifier = Modifier.size(80.dp), contentAlignment = Alignment.Center) {
                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                        drawArc(color = accentBlue, startAngle = -90f, sweepAngle = 360f * ogPrinPct, useCenter = false, style = Stroke(width = 24f, cap = StrokeCap.Round))
                                        drawArc(color = accentOrange, startAngle = -90f + (360f * ogPrinPct), sweepAngle = 360f * (1-ogPrinPct), useCenter = false, style = Stroke(width = 24f, cap = StrokeCap.Round))
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        AutoResizeChartText(text = "₹${String.format(Locale.US, "%.2f", originalTotalPayment/100000)}L", color = textColor)
                                        Text("${(ogPrinPct*100).toInt()}%", color = accentBlue, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Text("Without Prepayment", color = textSecondary, fontSize = 8.sp, modifier = Modifier.align(Alignment.BottomCenter).offset(y=20.dp))
                                }
                                
                                Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = null, tint = textSecondary, modifier = Modifier.size(24.dp))
                                
                                Box(modifier = Modifier.size(80.dp), contentAlignment = Alignment.Center) {
                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                        drawArc(color = accentBlue, startAngle = -90f, sweepAngle = 360f * newPrinPct, useCenter = false, style = Stroke(width = 24f, cap = StrokeCap.Round))
                                        drawArc(color = accentOrange.copy(alpha=0.5f), startAngle = -90f + (360f * newPrinPct), sweepAngle = 360f * (1-newPrinPct), useCenter = false, style = Stroke(width = 24f, cap = StrokeCap.Round))
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        AutoResizeChartText(text = "₹${String.format(Locale.US, "%.2f", (p+newTotalInterest)/100000)}L", color = textColor)
                                        Text("${(newPrinPct*100).toInt()}%", color = accentBlue, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Text("With Prepayment", color = textSecondary, fontSize = 8.sp, modifier = Modifier.align(Alignment.BottomCenter).offset(y=20.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                },
                rightCard = { cardMod ->
                    Card(modifier = cardMod.height(180.dp), colors = CardDefaults.cardColors(containerColor = surfaceColor), shape = RoundedCornerShape(12.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Loan Balance Over Time", color = textColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(accentBlue))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Original Loan", color = textSecondary, fontSize = 10.sp)
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(accentGreen))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("With Prepayment", color = textSecondary, fontSize = 10.sp)
                                }
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            Box(modifier = Modifier.fillMaxWidth().height(80.dp)) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    val w = size.width
                                    val h = size.height
                                    
                                    val p1 = Path().apply { moveTo(0f, 0f); quadraticBezierTo(w*0.5f, h*0.2f, w, h) }
                                    drawPath(p1, color = accentBlue, style = Stroke(width = 4f))
                                    
                                    val prepayX = w * (newTenureMonths.toFloat() / n.toFloat())
                                    val p2 = Path().apply { moveTo(0f, 0f); quadraticBezierTo(w*0.2f, h*0.4f, prepayX, h) }
                                    drawPath(p2, color = accentGreen, style = Stroke(width = 6f))
                                    
                                    drawLine(color = textSecondary.copy(alpha=0.5f), start=Offset(prepayX, 0f), end=Offset(prepayX, h), strokeWidth=2f, pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f))
                                }
                                // Axis labels
                                Text("₹${(p/100000).toInt()}L", color = textSecondary, fontSize = 8.sp, modifier = Modifier.align(Alignment.TopStart).offset(y=(-4).dp, x=(-4).dp))
                                //Text("₹0", color = textSecondary, fontSize = 8.sp, modifier = Modifier.align(Alignment.BottomStart))
                                Row(modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter).offset(y=16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("0 Yr", color = textSecondary, fontSize = 8.sp)
                                Text("5 Yr", color = textSecondary, fontSize = 8.sp)
                                Text("10 Yr", color = textSecondary, fontSize = 8.sp)
                                Text("15 Yr", color = textSecondary, fontSize = 8.sp)
                            }
                            
                            // Tooltip
                            Box(modifier = Modifier.align(Alignment.TopEnd).offset(x=8.dp, y=20.dp).clip(RoundedCornerShape(4.dp)).background(heroCardTint).border(1.dp, accentGreen.copy(alpha=0.5f), RoundedCornerShape(4.dp)).padding(4.dp)) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Loan closed", color = textColor, fontSize = 8.sp)
                                    Text("~ ${String.format(Locale.US, "%.1f", tenureReducedMonths/12)} Years", color = accentGreen, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                    Text("earlier", color = accentGreen, fontSize = 8.sp)
                                }
                            }
                        }
                    } // close Column
                } // close Card
                } // close rightCard lambda
            ) // close PrepayDashboardCards

            // F. Scenarios & Recommendations
            Row(verticalAlignment = Alignment.Bottom) {
                Text("What If?", color = textColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Try Different Prepayment Scenarios", color = textSecondary, fontSize = 12.sp)
            }
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column(modifier = Modifier.weight(1.3f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            ScenarioChip(modifier = Modifier.weight(1f), title = "+ ₹50,000", sub = "Prepayment", save = "Save ₹4.19L", red = "Reduce by 2.1 Yrs", selected = false, icon = Icons.Rounded.Savings)
                            ScenarioChip(modifier = Modifier.weight(1f), title = "+ ₹1,00,000", sub = "Prepayment", save = "Save ₹8.45L", red = "Reduce by 4.2 Yrs", selected = true, icon = Icons.Rounded.Payments)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            ScenarioChip(modifier = Modifier.weight(1f), title = "+ ₹5,000", sub = "Extra EMI", save = "Save ₹10.27L", red = "Reduce by 5.3 Yrs", selected = false, icon = Icons.Rounded.MonetizationOn)
                            ScenarioChip(modifier = Modifier.weight(1f), title = "Prepay", sub = "Every Year", save = "Save ₹9.15L", red = "Reduce by 4.7 Yrs", selected = false, icon = Icons.Rounded.Event)
                        }
                    }
                    
                    Card(modifier = Modifier.weight(1f).height(248.dp), colors = CardDefaults.cardColors(containerColor = heroCardTint.copy(alpha=0.5f)), shape = RoundedCornerShape(12.dp), border = borderIf(true, accentGreen.copy(alpha=0.5f))) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Best Recommendation", color = accentGreen, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Icon(Icons.Rounded.Stars, contentDescription = null, tint = accentGreen, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        RecomRow("Prepay ₹1,00,000 today")
                        RecomRow("Save ₹8.45L in interest")
                        RecomRow("Reduce tenure by 4.2 years")
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { },
                            modifier = Modifier.fillMaxWidth().height(40.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = accentGreen, contentColor = bgColor),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Icon(Icons.Rounded.RocketLaunch, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Great Choice!", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
}

            // G. Premium Tools
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.WorkspacePremium, contentDescription = null, tint = Color(0xFFFBBF24), modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Premium Tools", color = textColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
            LazyRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                item { PremiumToolCard(Icons.Rounded.ReceiptLong, accentBlue, "Amortization\nSchedule") }
                item { PremiumToolCard(Icons.Rounded.CompareArrows, accentPurple, "Compare\nStrategies") }
                item { PremiumToolCard(Icons.Rounded.DynamicFeed, accentOrange, "Multiple Prepayment\nPlanner") }
                item { PremiumToolCard(Icons.Rounded.PictureAsPdf, Color(0xFFEC4899), "PDF Report\n& Share") }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun DetailRowEditable(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(label, color = Color.Gray, fontSize = 12.sp, maxLines = 1)
        Spacer(modifier = Modifier.weight(1f))
        Text(value, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1, textAlign = TextAlign.End)
        Spacer(modifier = Modifier.width(8.dp))
        Icon(Icons.Rounded.Edit, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(12.dp).clickable { onClick() })
    }
}

@Composable
fun ComparisonRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String, valueColor: Color) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(12.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(label, color = Color.Gray, fontSize = 10.sp, maxLines = 1)
        Spacer(modifier = Modifier.weight(1f))
        Text(value, color = valueColor, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1, textAlign = TextAlign.End, softWrap = false)
    }
}

@Composable
fun MetricCard(modifier: Modifier = Modifier, icon: androidx.compose.ui.graphics.vector.ImageVector, iconTint: Color, title: String, value: String, valueColor: Color, subtext: String) {
    Card(modifier = modifier.height(100.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF152238)), shape = RoundedCornerShape(12.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, color = Color.White, fontSize = 10.sp, maxLines = 1)
            }
            Spacer(modifier = Modifier.weight(1f))
            AutoResizeChartText(text = value, color = valueColor)
            Text(subtext, color = Color.Gray, fontSize = 9.sp, maxLines = 1)
        }
    }
}

@Composable
fun ScenarioChip(modifier: Modifier = Modifier, title: String, sub: String, save: String, red: String, selected: Boolean, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Box(modifier = modifier) {
        Card(
            modifier = Modifier.fillMaxWidth().height(120.dp),
            colors = CardDefaults.cardColors(containerColor = if (selected) Color(0xFF3B82F6).copy(alpha=0.1f) else Color(0xFF152238)),
            border = borderIf(selected, Color(0xFF3B82F6)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp).fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceBetween) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(icon, contentDescription = null, tint = if(selected) Color(0xFF3B82F6) else Color.Gray, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(title, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                }
                Text(sub, color = Color.Gray, fontSize = 9.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(save, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                Text(red, color = Color.Gray, fontSize = 8.sp, maxLines = 1)
            }
        }
        if (selected) {
            Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = Color(0xFF3B82F6), modifier = Modifier.align(Alignment.TopEnd).offset(x=(6).dp, y=(-6).dp).size(20.dp).background(Color.White, CircleShape))
        }
    }
}

@Composable
fun RecomRow(text: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = Color(0xFF4ADE80), modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, color = Color.White, fontSize = 11.sp)
    }
}

@Composable
fun PremiumToolCard(icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, title: String) {
    Card(modifier = Modifier.width(160.dp).height(64.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF152238)), shape = RoundedCornerShape(12.dp)) {
        Row(modifier = Modifier.padding(horizontal = 12.dp).fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(title, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
            Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
        }
    }
}

fun borderIf(condition: Boolean, color: Color): androidx.compose.foundation.BorderStroke? {
    return if (condition) androidx.compose.foundation.BorderStroke(1.dp, color) else null
}

@Composable
fun AutoResizeTextFieldPrepay(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    val inputLength = value.length
    val scaledFontSize = when {
        inputLength >= 12 -> 14.sp
        else -> 16.sp
    }
    
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        readOnly = readOnly,
        label = { Text(label, color = Color.Gray, fontSize = 12.sp, maxLines = 1, softWrap = false) },
        modifier = modifier.height(60.dp),
        singleLine = true,
        maxLines = 1,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        textStyle = LocalTextStyle.current.copy(fontSize = scaledFontSize, color = Color.White),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = Color(0xFF3B82F6),
            unfocusedBorderColor = Color(0xFF152238).copy(alpha=0.5f),
            focusedContainerColor = Color(0xFF0B132B),
            unfocusedContainerColor = Color(0xFF0B132B)
        ),
        trailingIcon = trailingIcon,
        shape = RoundedCornerShape(8.dp)
    )
}

@Composable
fun AutoResizeHeroTextPrep(
    text: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    var scaledFontSize by remember(text) { mutableStateOf(48.sp) }
    
    Text(
        text = text,
        color = color,
        fontSize = scaledFontSize,
        fontWeight = FontWeight.ExtraBold,
        maxLines = 1,
        softWrap = false,
        onTextLayout = { textLayoutResult ->
            if (textLayoutResult.hasVisualOverflow && scaledFontSize > 16.sp) {
                scaledFontSize = (scaledFontSize.value - 2f).sp
            }
        },
        modifier = modifier.fillMaxWidth(),
        textAlign = TextAlign.Center
    )
}

@Composable
fun AutoResizeChartText(
    text: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    var scaledFontSize by remember(text) { mutableStateOf(16.sp) }
    
    Text(
        text = text,
        color = color,
        fontSize = scaledFontSize,
        fontWeight = FontWeight.Bold,
        maxLines = 1,
        softWrap = false,
        onTextLayout = { textLayoutResult ->
            if (textLayoutResult.hasVisualOverflow && scaledFontSize > 8.sp) {
                scaledFontSize = (scaledFontSize.value - 1f).sp
            }
        },
        modifier = modifier
    )
}

fun Modifier.borderIf(condition: Boolean, color: Color): Modifier = 
    if (condition) this.border(1.dp, color, RoundedCornerShape(12.dp)) else this

@Composable
fun PrepayDashboardCards(
    sizeClass: WindowWidthSizeClass,
    leftCard: @Composable (Modifier) -> Unit,
    rightCard: @Composable (Modifier) -> Unit
) {
    if (sizeClass == WindowWidthSizeClass.Compact) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
            leftCard(Modifier.fillMaxWidth())
            rightCard(Modifier.fillMaxWidth())
        }
    } else {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            leftCard(Modifier.weight(1f))
            rightCard(Modifier.weight(1f))
        }
    }
}

