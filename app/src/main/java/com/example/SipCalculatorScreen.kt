package com.example

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.draw.blur
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

// --- Colors ---
private val NavyBg = Color(0xFF070D1B) // Very dark blue/black
private val CardBg = Color(0xFF0F172A) // Slate slate-900
private val BluePrimary = Color(0xFF2563EB) // Blue-600
private val GoldAccent = Color(0xFFEAB308) // Yellow-500
private val GreenSuccess = Color(0xFF22C55E) // Green-500
private val TextSec = Color(0xFF94A3B8) // Slate-400
private val StrokeNavy = Color(0xFF1E293B) // Slate-800
private val BadgeBg = Color(0xFF1E3A8A)

data class YearlyData(
    val year: Int,
    val investedForYear: Double,
    val totalInvested: Double,
    val returns: Double,
    val maturity: Double
)

@Composable
fun SipCalculatorScreen(onNavigateBack: () -> Unit) {
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    val configuration = LocalConfiguration.current
    val isWide = configuration.screenWidthDp > 600

    var amountText by remember { mutableStateOf("10000") }
    var returnRateText by remember { mutableStateOf("12") }
    var yearsText by remember { mutableStateOf("10") }
    var stepUpText by remember { mutableStateOf("0") }

    val amount = amountText.toDoubleOrNull() ?: 10000.0
    val returnRate = returnRateText.toDoubleOrNull() ?: 12.0
    val years = yearsText.toIntOrNull() ?: 10
    val stepUpRate = stepUpText.toDoubleOrNull() ?: 0.0

    // Engine Math
    var totalInvested = 0.0
    var maturityValue = 0.0
    var currentMonthlySip = amount
    val monthlyReturnRate = (returnRate / 100.0) / 12.0
    val totalMonths = years * 12
    val stepUpFraction = stepUpRate / 100.0
    
    val yearlyDataList = mutableListOf<YearlyData>()
    var investedThisYear = 0.0

    for (m in 1..totalMonths) {
        totalInvested += currentMonthlySip
        investedThisYear += currentMonthlySip
        maturityValue = (maturityValue + currentMonthlySip) * (1 + monthlyReturnRate)
        if (m % 12 == 0) {
            val year = m / 12
            yearlyDataList.add(
                YearlyData(
                    year = year,
                    investedForYear = investedThisYear,
                    totalInvested = totalInvested,
                    returns = maturityValue - totalInvested,
                    maturity = maturityValue
                )
            )
            investedThisYear = 0.0
            currentMonthlySip += currentMonthlySip * stepUpFraction
        }
    }
    val totalGain = maturityValue - totalInvested

    Scaffold(
        containerColor = NavyBg,
        bottomBar = { SipBottomNav() },
        modifier = Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() }, indication = null
        ) { focusManager.clearFocus() }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = if (isWide) 32.dp else 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            SipTopBar(onNavigateBack)
            InputsSection(amountText, returnRateText, yearsText, stepUpText, { amountText = it }, { returnRateText = it }, { yearsText = it }, { stepUpText = it }, isWide)
            HeroCard(totalInvested, totalGain, maturityValue, returnRate, years, isWide)
            
            if (isWide) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    GrowthVisualizationCard(yearlyDataList, modifier = Modifier.weight(1.5f))
                    Box(modifier = Modifier.weight(1f)) { InflationAdjustedCard(maturityValue, years) }
                }
            } else {
                GrowthVisualizationCard(yearlyDataList, modifier = Modifier.fillMaxWidth())
                InflationAdjustedCard(maturityValue, years)
            }
            
            LifestyleFundsSection(isWide, maturityValue, years)
            
            WealthOpportunityCard(maturityValue)
            
            SipScheduleCard(yearlyDataList)
            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun SipBottomNav() {
    NavigationBar(
        containerColor = NavyBg,
        contentColor = TextSec,
        tonalElevation = 0.dp,
        modifier = Modifier.clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)).border(1.dp, StrokeNavy, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
    ) {
        NavigationBarItem(selected = false, onClick = {}, icon = { Icon(Icons.Rounded.Home, contentDescription = null) }, label = { Text("Home", fontSize = 10.sp) }, colors = NavigationBarItemDefaults.colors(unselectedIconColor = TextSec, unselectedTextColor = TextSec, indicatorColor = Color.Transparent))
        NavigationBarItem(selected = true, onClick = {}, icon = { Icon(Icons.Rounded.Calculate, contentDescription = null) }, label = { Text("Tools", fontSize = 10.sp) }, colors = NavigationBarItemDefaults.colors(selectedIconColor = BluePrimary, selectedTextColor = BluePrimary, indicatorColor = BluePrimary.copy(alpha=0.15f)))
        NavigationBarItem(selected = false, onClick = {}, icon = { Icon(Icons.Rounded.AccountBalanceWallet, contentDescription = null) }, label = { Text("Portfolio", fontSize = 10.sp) }, colors = NavigationBarItemDefaults.colors(unselectedIconColor = TextSec, unselectedTextColor = TextSec, indicatorColor = Color.Transparent))
        NavigationBarItem(selected = false, onClick = {}, icon = { Icon(Icons.Rounded.Person, contentDescription = null) }, label = { Text("Profile", fontSize = 10.sp) }, colors = NavigationBarItemDefaults.colors(unselectedIconColor = TextSec, unselectedTextColor = TextSec, indicatorColor = Color.Transparent))
    }
}

@Composable
private fun SipTopBar(onNavigateBack: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().statusBarsPadding(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier.size(48.dp).clip(CircleShape).background(Color.Transparent).border(1.dp, StrokeNavy, CircleShape)
        ) {
            Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back", tint = Color.White)
        }
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text("SIP Calculator", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold, lineHeight = 28.sp)
            Text("Plan your investments", color = TextSec, fontSize = 13.sp, lineHeight = 18.sp)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = {}, modifier = Modifier.size(48.dp).clip(CircleShape).border(1.dp, StrokeNavy, CircleShape)) {
                Icon(Icons.Rounded.FavoriteBorder, contentDescription = "Favorite", tint = Color.White, modifier = Modifier.size(24.dp))
            }
            IconButton(onClick = {}, modifier = Modifier.size(48.dp).clip(CircleShape).border(1.dp, StrokeNavy, CircleShape)) {
                Icon(Icons.Rounded.Share, contentDescription = "Share", tint = Color.White, modifier = Modifier.size(24.dp))
            }
        }
    }
}

fun formatMoneyObj(value: Double): String {
    return if (value >= 1_00_00_000) {
        String.format("%.2f Cr", value / 1_00_00_000)
    } else if (value >= 1_00_000) {
        String.format("%.2f L", value / 1_00_000)
    } else if (value >= 1_000) {
        String.format("%.1f K", value / 1_000)
    } else {
        String.format("%,.0f", value)
    }
}

@Composable
private fun InputsSection(
    amount: String, returnRate: String, years: String, stepUp: String,
    onAmount: (String) -> Unit, onRate: (String) -> Unit, onYears: (String) -> Unit, onStepUp: (String) -> Unit,
    isWide: Boolean
) {
    if (isWide) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            Box(Modifier.weight(1f)) { CustomInput("Monthly SIP", amount, onAmount, Icons.Rounded.Edit, prefix = globalCurrencySymbol) }
            Box(Modifier.weight(1f)) { CustomInput("Expected Return", returnRate, onRate, Icons.Rounded.Edit, suffix = "%") }
            Box(Modifier.weight(1f)) { CustomInput("Period", years, onYears, Icons.Rounded.KeyboardArrowDown, suffix = " Yr") }
            Box(Modifier.weight(1f)) { CustomInput("Step-Up", stepUp, onStepUp, Icons.Rounded.KeyboardArrowDown, suffix = "%") }
        }
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(Modifier.weight(1f)) { CustomInput("Monthly SIP", amount, onAmount, Icons.Rounded.Edit, prefix = globalCurrencySymbol) }
                Box(Modifier.weight(1f)) { CustomInput("Return Rate", returnRate, onRate, Icons.Rounded.Edit, suffix = "%") }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(Modifier.weight(1f)) { CustomInput("Period", years, onYears, Icons.Rounded.KeyboardArrowDown, suffix = " Yr") }
                Box(Modifier.weight(1f)) { CustomInput("Step-Up", stepUp, onStepUp, Icons.Rounded.KeyboardArrowDown, suffix = "%") }
            }
        }
    }
}

@Composable
private fun CustomInput(label: String, value: String, onValueChange: (String) -> Unit, icon: androidx.compose.ui.graphics.vector.ImageVector, prefix: String = "", suffix: String = "") {
    Column {
        Text(label, color = TextSec, fontSize = 13.sp, lineHeight = 16.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Spacer(Modifier.height(4.dp))
        Box(
            modifier = Modifier.fillMaxWidth().height(56.dp).background(Color.Transparent).border(1.dp, StrokeNavy, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                if (prefix.isNotEmpty()) {
                    Text(prefix, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width(8.dp))
                }
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold),
                    modifier = Modifier.weight(1f).fillMaxHeight().wrapContentHeight(Alignment.CenterVertically)
                )
                if (suffix.isNotEmpty()) {
                    Text(suffix, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.width(12.dp))
                Icon(icon, contentDescription = null, tint = TextSec, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
private fun HeroStat(label: String, value: String, color: Color = Color.White, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.Top) {
            Text(label, color = TextSec, fontSize = 12.sp, lineHeight = 16.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
            Spacer(Modifier.width(4.dp))
            Icon(Icons.Rounded.Info, contentDescription = null, tint = TextSec, modifier = Modifier.size(12.dp).padding(top = 2.dp))
        }
        Spacer(Modifier.height(4.dp))
        AutoSizeText(if (label.contains("Multiplier") || label.contains("Return") && !label.contains("Total")) value else "${globalCurrencySymbol}$value", color = color, fontSize = 16.sp, lineHeight = 20.sp, fontWeight = FontWeight.Bold, maxLines = 1)
    }
}

fun formatMoneyExact(value: Double): String {
    val formatter = java.text.NumberFormat.getNumberInstance(java.util.Locale("en", "IN"))
    formatter.maximumFractionDigits = 0
    return formatter.format(value)
}

@Composable
private fun HeroCard(invested: Double, gain: Double, maturity: Double, ret: Double, years: Int, isWide: Boolean) {
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val heroFontSize = when {
        screenWidth >= 480 -> 42.sp
        screenWidth >= 393 -> 38.sp
        else -> 34.sp
    }
    val heroLineHeight = when {
        screenWidth >= 480 -> 48.sp
        screenWidth >= 393 -> 44.sp
        else -> 40.sp
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF0C162C))
            .border(1.dp, Color(0xFF1E3A8A).copy(alpha=0.5f), RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
            // Top Section
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Estimated Maturity Value", color = TextSec, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Spacer(Modifier.width(6.dp))
                        Icon(Icons.Rounded.Info, contentDescription = null, tint = TextSec, modifier = Modifier.size(14.dp))
                    }
                    Spacer(Modifier.height(8.dp))
                    AutoSizeText(
                        "${globalCurrencySymbol}${formatMoneyExact(maturity)}", 
                        color = GoldAccent, 
                        fontSize = heroFontSize, 
                        lineHeight = heroLineHeight,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1
                    )
                    Spacer(Modifier.height(4.dp))
                    val adjustedValue = maturity / Math.pow(1 + 0.06, years.toDouble())
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(Color.White.copy(alpha=0.05f)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                        Text("Real Value Today: ", color = TextSec, fontSize = 11.sp, maxLines = 1)
                        Text("${globalCurrencySymbol}${formatMoneyObj(adjustedValue)}", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
                    }
                }
                
                // Piggy bank icon with glow
                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(start = 16.dp)) {
                    Box(modifier = Modifier.size(48.dp).blur(24.dp).background(BluePrimary.copy(alpha = 0.6f), CircleShape))
                    Icon(
                        Icons.Rounded.Savings, 
                        contentDescription = "Savings", 
                        tint = BluePrimary, 
                        modifier = Modifier.size(64.dp)
                    )
                }
            }
            
            Spacer(Modifier.height(24.dp))
            HorizontalDivider(color = Color(0xFF1E3A8A).copy(alpha=0.5f))
            Spacer(Modifier.height(20.dp))
            
            // Stats Row
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                HeroStat("Total Invested", formatMoneyExact(invested), modifier = Modifier.weight(1f))
                HeroStat("Total Returns", formatMoneyExact(gain), color = GreenSuccess, modifier = Modifier.weight(1f).padding(start = 8.dp))
                if (isWide) {
                    HeroStat("Wealth Multiplier", "${String.format("%.2f", maturity/invested)}x", color = GreenSuccess, modifier = Modifier.weight(1f).padding(start = 8.dp))
                    HeroStat("Avg. Return", "${ret.toInt()}%", modifier = Modifier.weight(1f).padding(start = 8.dp))
                } else {
                    HeroStat("Multiplier", "${String.format("%.2f", maturity/invested)}x", color = GreenSuccess, modifier = Modifier.weight(0.7f).padding(start = 8.dp))
                }
            }
        }
    }
}

@Composable
private fun GrowthVisualizationCard(yearlyDataList: List<YearlyData>, modifier: Modifier = Modifier) {
    Column(modifier = modifier.height(280.dp).clip(RoundedCornerShape(16.dp)).background(Color(0xFF0C162C)).border(1.dp, Color(0xFF1E3A8A).copy(alpha=0.5f), RoundedCornerShape(16.dp)).padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Investment Growth", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Icon(Icons.Rounded.MoreVert, contentDescription = null, tint = TextSec, modifier = Modifier.size(16.dp))
        }
        Spacer(Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                LegendDot("Maturity Value", BluePrimary)
            }
            Column(modifier = Modifier.weight(1f)) {
                LegendDot("Invested", GreenSuccess)
            }
        }
        Spacer(Modifier.height(16.dp))
        
        val maxMaturity = yearlyDataList.maxOfOrNull { it.maturity } ?: 1.0
        val maxYValue = maxMaturity * 1.1 // Add 10% padding to top
        val maxYears = yearlyDataList.maxOfOrNull { it.year } ?: 1
        
        Column(modifier = Modifier.weight(1f).fillMaxWidth()) {
            Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                // Y-Axis
                Column(modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.SpaceBetween) {
                    Text(formatMoneyObj(maxYValue), color = TextSec, fontSize = 10.sp)
                    Text(formatMoneyObj(maxYValue * 0.66), color = TextSec, fontSize = 10.sp)
                    Text(formatMoneyObj(maxYValue * 0.33), color = TextSec, fontSize = 10.sp)
                    Text("0", color = TextSec, fontSize = 10.sp)
                }
                Spacer(Modifier.width(8.dp))
                // Chart
                Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height
                        
                        // Grid lines
                        for(i in 1..4) {
                            val x = w * (i / 4f)
                            drawLine(Color.White.copy(alpha=0.05f), Offset(x, 0f), Offset(x, h), 1f)
                        }
                        for(i in 0..3) {
                            val y = h * (i / 3f)
                            drawLine(Color.White.copy(alpha=0.05f), Offset(0f, y), Offset(w, y), 1f)
                        }
                        
                        if (yearlyDataList.isEmpty()) return@Canvas
                        
                        // Curve 1 (Portfolio)
                        val path1 = Path()
                        path1.moveTo(0f, h)
                        yearlyDataList.forEach { data ->
                            val x = (data.year.toFloat() / maxYears) * w
                            val y = h - ((data.maturity / maxYValue).toFloat() * h)
                            path1.lineTo(x, y)
                        }
                        drawPath(path1, BluePrimary, style = Stroke(6f, cap = StrokeCap.Round, join = StrokeJoin.Round))
                        
                        // Curve 2 (Invested)
                        val path2 = Path()
                        path2.moveTo(0f, h)
                        yearlyDataList.forEach { data ->
                            val x = (data.year.toFloat() / maxYears) * w
                            val y = h - ((data.totalInvested / maxYValue).toFloat() * h)
                            path2.lineTo(x, y)
                        }
                        drawPath(path2, GreenSuccess, style = Stroke(6f, cap = StrokeCap.Round, join = StrokeJoin.Round))
                        
                        // Markers
                        val step = (yearlyDataList.size / 4).coerceAtLeast(1)
                        for (i in step..yearlyDataList.size step step) {
                            val data = yearlyDataList.getOrNull(i - 1) ?: continue
                            val x = (data.year.toFloat() / maxYears) * w
                            val y1 = h - ((data.maturity / maxYValue).toFloat() * h)
                            val y2 = h - ((data.totalInvested / maxYValue).toFloat() * h)
                            
                            drawCircle(BluePrimary, 8f, Offset(x, y1))
                            drawCircle(Color.White, 4f, Offset(x, y1))
                            
                            drawCircle(GreenSuccess, 8f, Offset(x, y2))
                            drawCircle(Color.White, 4f, Offset(x, y2))
                        }
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth().padding(start = 32.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("0Y", color = Color.Transparent, fontSize = 10.sp)
                Text("${maxYears / 4}Y", color = TextSec, fontSize = 10.sp)
                Text("${maxYears / 2}Y", color = TextSec, fontSize = 10.sp)
                Text("${(maxYears * 3) / 4}Y", color = TextSec, fontSize = 10.sp)
                Text("${maxYears}Y", color = TextSec, fontSize = 10.sp)
            }
        }
    }
}

@Composable
private fun LegendDot(lbl: String, col: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(col))
        Spacer(Modifier.width(8.dp))
        Text(lbl, color = TextSec, fontSize = 12.sp)
    }
}

@Composable
private fun LifestyleFundsSection(isWide: Boolean, maturityValue: Double, years: Int) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("With this corpus, you can afford:", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(16.dp))
        
        data class FutureGoal(val title: String, val target: Double, val icon: androidx.compose.ui.graphics.vector.ImageVector)
        val items = mutableListOf<FutureGoal>()
        if (maturityValue > 5000000) {
            items.add(FutureGoal("Premium Apartment", 15000000.0, Icons.Rounded.Home))
            items.add(FutureGoal("Luxury SUV", 7500000.0, Icons.Rounded.DirectionsCar))
        } else if (maturityValue > 1500000) {
            items.add(FutureGoal("Home Downpayment", 2500000.0, Icons.Rounded.Home))
            items.add(FutureGoal("Mahindra XUV700", 2200000.0, Icons.Rounded.DirectionsCar))
        } else {
            items.add(FutureGoal("Honda City", 1200000.0, Icons.Rounded.DirectionsCar))
            items.add(FutureGoal("International Travel", 600000.0, Icons.Rounded.FlightTakeoff))
        }
        items.add(FutureGoal("Emergency Fund", Math.max(500000.0, maturityValue * 1.5), Icons.Rounded.AccountBalanceWallet))
        items.add(FutureGoal("Child Education", Math.max(1000000.0, maturityValue * 2.0), Icons.Rounded.School))
        
        val columns = if (isWide) 4 else 2
        val chunked = items.chunked(columns)
        
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            chunked.forEach { rowItems ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    rowItems.forEach { item ->
                        LifeCard(
                            title = item.title,
                            targetAmount = item.target,
                            maturityValue = maturityValue,
                            years = years,
                            icon = item.icon,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (rowItems.size < columns) {
                        repeat(columns - rowItems.size) {
                            Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LifeCard(title: String, targetAmount: Double, maturityValue: Double, years: Int, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier = Modifier) {
    val percent = ((maturityValue / targetAmount) * 100).toInt()
    val progress = (maturityValue / targetAmount).coerceAtMost(1.0)
    val shortAmount = (targetAmount - maturityValue).coerceAtLeast(0.0)

    Column(
        modifier = modifier.clip(RoundedCornerShape(16.dp))
            .background(Brush.linearGradient(listOf(Color(0xFF1E293B), Color(0xFF0F172A))))
            .border(1.dp, Color.White.copy(alpha=0.1f), RoundedCornerShape(16.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = BluePrimary, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
            Text(title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Spacer(Modifier.height(12.dp))
        
        Text("$percent% Achieved", color = if (percent >= 100) GreenSuccess else GoldAccent, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Spacer(Modifier.height(6.dp))
        Box(modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape).background(Color.White.copy(alpha=0.1f))) {
            Box(modifier = Modifier.fillMaxWidth(progress.toFloat()).height(4.dp).clip(CircleShape).background(if (percent >= 100) GreenSuccess else BluePrimary))
        }
        
        Spacer(Modifier.height(8.dp))
        if (shortAmount > 0) {
            Text("${globalCurrencySymbol}${formatMoneyObj(shortAmount)} Short", color = TextSec, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        } else {
            Text("Goal Reached", color = GreenSuccess, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun InflationAdjustedCard(maturityValue: Double, years: Int) {
    val inflationRate = 0.06 // 6% annual inflation
    val adjustedValue = maturityValue / Math.pow(1 + inflationRate, years.toDouble())
    val valueLost = maturityValue - adjustedValue

    Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(CardBg).border(1.dp, StrokeNavy, RoundedCornerShape(16.dp)).padding(24.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Inflation Impact", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.width(8.dp))
            Icon(Icons.Rounded.Info, contentDescription = null, tint = TextSec, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.height(24.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Future Corpus", color = TextSec, fontSize = 14.sp, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
            AutoSizeText(globalCurrencySymbol + formatMoneyExact(maturityValue), color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
        }
        Spacer(Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Inflation Adjusted", color = TextSec, fontSize = 14.sp, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
            AutoSizeText(globalCurrencySymbol + formatMoneyExact(adjustedValue), color = GoldAccent, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
        }
        Spacer(Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Value Lost", color = TextSec, fontSize = 14.sp, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
            AutoSizeText("- " + globalCurrencySymbol + formatMoneyExact(valueLost), color = Color(0xFFF87171), fontSize = 16.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
        }
        
        Spacer(Modifier.height(20.dp))
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(Color.White.copy(alpha=0.05f)).padding(16.dp)) {
            Icon(Icons.Rounded.TrendingDown, contentDescription = null, tint = Color(0xFFF87171), modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(12.dp))
            Column {
                Text("Purchasing Power Reduced", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Text("Inflation eats into your returns.", color = TextSec, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun WealthOpportunityCard(maturityValue: Double) {
    val potentialCorpus = maturityValue * 1.35
    val potentialGain = potentialCorpus - maturityValue

    Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Brush.horizontalGradient(listOf(Color(0xFF2C240E), CardBg))).border(1.dp, GoldAccent.copy(alpha=0.6f), RoundedCornerShape(16.dp)).padding(24.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Rounded.AutoAwesome, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(12.dp))
            Text("Wealth Opportunity Found", color = GoldAccent, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = buildAnnotatedString {
                append("AI analyzed your SIP strategy and ")
                withStyle(SpanStyle(color = GoldAccent)) {
                    append("found multiple opportunities")
                }
                append(" that may increase your final corpus.")
            },
            color = Color.White.copy(alpha=0.9f), fontSize = 14.sp, lineHeight = 20.sp
        )
        
        Spacer(Modifier.height(24.dp))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Current Corpus", color = TextSec, fontSize = 11.sp, lineHeight = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(4.dp))
                AutoSizeText(globalCurrencySymbol + formatMoneyExact(maturityValue), color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold, maxLines = 1)
            }
            Box(modifier = Modifier.width(1.dp).height(40.dp).background(StrokeNavy))
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1.3f)) {
                Text("Potential Corpus", color = TextSec, fontSize = 11.sp, lineHeight = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Lock, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    val formatted = formatMoneyExact(potentialCorpus)
                    val blurredText = if (formatted.length > 5) formatted.take(2) + ",XX,XXX" else "XX,XXX"
                    AutoSizeText(globalCurrencySymbol + blurredText, color = GoldAccent, fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.blur(4.dp), maxLines = 1)
                }
            }
            Box(modifier = Modifier.width(1.dp).height(40.dp).background(StrokeNavy))
            Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1f)) {
                Text("Potential Gain", color = TextSec, fontSize = 11.sp, lineHeight = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Lock, contentDescription = null, tint = GreenSuccess, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    val formattedGain = formatMoneyExact(potentialGain)
                    val blurredGain = if (formattedGain.length > 5) formattedGain.take(1) + ",XX,XXX" else "X,XXX"
                    AutoSizeText("+" + globalCurrencySymbol + blurredGain, color = GreenSuccess, fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.blur(4.dp), maxLines = 1)
                }
            }
        }
        
        Spacer(Modifier.height(24.dp))
        listOf("Better Step-Up Strategy", "Faster Wealth Growth", "Goal Achievement Optimization").forEach { text ->
           Row(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp), verticalAlignment = Alignment.CenterVertically) {
               Icon(Icons.Rounded.AutoAwesome, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(16.dp))
               Spacer(Modifier.width(12.dp))
               Text(text, color = Color.White, fontSize = 14.sp)
               Spacer(Modifier.weight(1f))
               Box(modifier = Modifier.width(100.dp).height(6.dp).clip(CircleShape).background(Color.White.copy(alpha=0.1f)).blur(2.dp))
               Spacer(Modifier.width(12.dp))
               Icon(Icons.Rounded.Lock, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(14.dp))
           }
        }
        
        Spacer(Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(
                onClick = {}, modifier = Modifier.weight(1f).height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NavyBg.copy(alpha=0.6f), contentColor = Color.White),
                shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, StrokeNavy),
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                Icon(Icons.Rounded.PlayCircleOutline, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                   Text("Watch Ad", fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                   Text("Unlock AI Insight", fontSize = 9.sp, color = TextSec, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
            Button(
                onClick = {}, modifier = Modifier.weight(1f).height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = NavyBg),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                Icon(Icons.Rounded.WorkspacePremium, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                   Text("Premium", fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                   Text("Unlimited AI Insights", fontSize = 9.sp, color = NavyBg.copy(alpha=0.8f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
        }
    }
}

@Composable
private fun SipScheduleCard(yearlyDataList: List<YearlyData>) {
    Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(CardBg).border(1.dp, StrokeNavy, RoundedCornerShape(16.dp)).padding(vertical = 24.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 24.dp)) {
            Text("SIP Schedule (Year-wise)", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.width(8.dp))
            Icon(Icons.Rounded.Info, contentDescription = null, tint = TextSec, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.height(24.dp))
        Row(modifier = Modifier.fillMaxWidth().padding(start = 24.dp, end = 24.dp, bottom = 12.dp)) {
            Text("Yr", color = TextSec, fontSize = 11.sp, lineHeight = 16.sp, modifier = Modifier.weight(0.5f), maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("Invested", color = TextSec, fontSize = 11.sp, lineHeight = 16.sp, modifier = Modifier.weight(1.3f), maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("Total", color = TextSec, fontSize = 11.sp, lineHeight = 16.sp, modifier = Modifier.weight(1.2f), maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("Returns", color = TextSec, fontSize = 11.sp, lineHeight = 16.sp, modifier = Modifier.weight(1.2f), maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("Corpus", color = BluePrimary, fontSize = 11.sp, lineHeight = 16.sp, modifier = Modifier.weight(1.2f), textAlign = TextAlign.End, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        
        val rowsToDisplay = listOf(1, 5, 10).mapNotNull { targetYear ->
            yearlyDataList.find { it.year == targetYear }
        }
        rowsToDisplay.forEach { data ->
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp)) {
                Text(data.year.toString(), color = Color.White, fontSize = 12.sp, lineHeight = 16.sp, modifier = Modifier.weight(0.5f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(globalCurrencySymbol + formatMoneyObj(data.investedForYear), color = Color.White, fontSize = 12.sp, lineHeight = 16.sp, modifier = Modifier.weight(1.3f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(globalCurrencySymbol + formatMoneyObj(data.totalInvested), color = Color.White, fontSize = 12.sp, lineHeight = 16.sp, modifier = Modifier.weight(1.2f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(globalCurrencySymbol + formatMoneyObj(data.returns), color = GreenSuccess, fontSize = 12.sp, lineHeight = 16.sp, modifier = Modifier.weight(1.2f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(globalCurrencySymbol + formatMoneyObj(data.maturity), color = GoldAccent, fontSize = 12.sp, lineHeight = 16.sp, modifier = Modifier.weight(1.2f), textAlign = TextAlign.End, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            HorizontalDivider(color = StrokeNavy)
        }
        
        if (yearlyDataList.size > 5) {
            val remainingYears = yearlyDataList.size - 5
            Spacer(Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).clip(RoundedCornerShape(8.dp)).background(Color.White.copy(alpha=0.05f)).padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                Icon(Icons.Rounded.Lock, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text("$remainingYears More Hidden", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                Spacer(Modifier.width(8.dp))
                androidx.compose.material3.Button(onClick = {}, colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = GoldAccent), contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp), modifier = Modifier.height(32.dp)) {
                    Text("Unlock", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                }
            }
        }
    }
}

@Composable
fun AutoSizeText(
    text: String,
    color: Color = Color.White,
    fontSize: TextUnit = 16.sp,
    fontWeight: FontWeight = FontWeight.Normal,
    lineHeight: TextUnit = TextUnit.Unspecified,
    modifier: Modifier = Modifier,
    maxLines: Int = 1
) {
    var scaledFontSize by androidx.compose.runtime.remember(text, fontSize) { androidx.compose.runtime.mutableStateOf(fontSize) }
    var scaledLineHeight by androidx.compose.runtime.remember(lineHeight) { androidx.compose.runtime.mutableStateOf(lineHeight) }
    var readyToDraw by androidx.compose.runtime.remember(text) { androidx.compose.runtime.mutableStateOf(false) }

    Text(
        text = text,
        color = color,
        fontSize = scaledFontSize,
        fontWeight = fontWeight,
        lineHeight = scaledLineHeight,
        maxLines = maxLines,
        softWrap = false,
        modifier = modifier.drawWithContent {
            if (readyToDraw) drawContent()
        },
        onTextLayout = { textLayoutResult ->
            if (textLayoutResult.hasVisualOverflow) {
                scaledFontSize *= 0.9f
                if (scaledLineHeight != TextUnit.Unspecified) {
                    scaledLineHeight *= 0.9f
                }
            } else {
                readyToDraw = true
            }
        }
    )
}
