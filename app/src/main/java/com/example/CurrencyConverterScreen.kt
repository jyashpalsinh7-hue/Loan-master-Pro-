package com.example

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.util.Locale

// Constants
val CurrBgColor = ResponsiveUtils.BgColor
val CurrSurfaceColor = ResponsiveUtils.SurfaceColor
val CurrPrimaryAccent = ResponsiveUtils.PrimaryAccent
val CurrSecondaryAccent = ResponsiveUtils.SecondaryAccent
val CurrCardStrokeColor = ResponsiveUtils.CardStroke

fun getFlagEmoji(currencyCode: String): String {
    if (currencyCode.length < 2) return "🌍"
    if (currencyCode == "EUR") return "🇪🇺"
    val countryCode = currencyCode.substring(0, 2)
    val firstLetter = Character.codePointAt(countryCode, 0) - 0x41 + 0x1F1E6
    val secondLetter = Character.codePointAt(countryCode, 1) - 0x41 + 0x1F1E6
    return try {
        String(Character.toChars(firstLetter)) + String(Character.toChars(secondLetter))
    } catch (e: Exception) { "🌍" }
}

@Composable
fun CurrencyConverterScreen(onNavigateBack: () -> Unit, viewModel: CurrencyViewModel = viewModel()) {
    // Force reload comment 3
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val sizeClass = when {
        configuration.screenWidthDp < 600 -> WindowWidthSizeClass.Compact
        configuration.screenWidthDp < 840 -> WindowWidthSizeClass.Medium
        else -> WindowWidthSizeClass.Expanded
    }

    var baseAmount by remember { mutableStateOf("") }
    var baseCurrency by remember { mutableStateOf("USD") }
    var targetCurrency by remember { mutableStateOf("EUR") }
    
    var showBaseSelector by remember { mutableStateOf(false) }
    var showTargetSelector by remember { mutableStateOf(false) }
    
    val uiState by viewModel.uiState.collectAsState()
    
    val exchangeRateTarget = uiState.rates[targetCurrency] ?: 1.0
    val lastUpdated = uiState.lastUpdated.ifEmpty { "Recently" }
    
    val allCurrencies = listOf(baseCurrency, targetCurrency) + uiState.rates.keys.toList()
    val distinctCurrencies = allCurrencies.distinct()

    LaunchedEffect(baseCurrency) {
        viewModel.fetchRates(baseCurrency)
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(CurrBgColor).statusBarsPadding()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp).clickable { onNavigateBack() }
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Exchange",
                        color = Color.White,
                        fontSize = ResponsiveUtils.titleFontSize(sizeClass),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { viewModel.fetchRates(baseCurrency) }) {
                        Icon(imageVector = Icons.Rounded.Sync, contentDescription = "Refresh", tint = Color.White)
                    }
                }
            }
        },
        containerColor = CurrBgColor
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentPadding = PaddingValues(
                horizontal = ResponsiveUtils.horizontalPadding(sizeClass),
                vertical = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                PremiumConversionCard(
                    baseAmount = baseAmount,
                    exchangeRate = exchangeRateTarget,
                    baseCurrency = baseCurrency,
                    targetCurrency = targetCurrency,
                    onAmountChange = { baseAmount = it },
                    onSwap = {
                        val temp = baseCurrency
                        baseCurrency = targetCurrency
                        targetCurrency = temp
                    },
                    onSelectBase = { showBaseSelector = true },
                    onSelectTarget = { showTargetSelector = true }
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "1 $baseCurrency = ${String.format(Locale.US, "%.5f", exchangeRateTarget)} $targetCurrency",
                            color = Color.White,
                            fontSize = ResponsiveUtils.subtitleFontSize(sizeClass),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Last updated $lastUpdated",
                            color = ResponsiveUtils.TextSecondary,
                            fontSize = ResponsiveUtils.labelFontSize(sizeClass)
                        )
                    }
                }
            }
            
            item {
                PremiumChartSection(viewModel = viewModel, exchangeRate = exchangeRateTarget, baseCurrency = baseCurrency, targetCurrency = targetCurrency)
            }

            item {
                Text(
                    text = "Market Rates",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )
            }
            
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    val popular = listOf("EUR", "GBP", "JPY", "AED", "AUD", "CAD")
                    items(popular) { currency ->
                        val rate = uiState.rates[currency] ?: 0.0
                        if (rate > 0 && currency != baseCurrency) {
                            MarketRateCard(currency = currency, rate = rate, baseCurrency = baseCurrency)
                        }
                    }
                }
            }
            
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        PremiumQuickAction(
                            title = "Save Quote",
                            icon = Icons.Rounded.BookmarkBorder,
                            color = CurrPrimaryAccent,
                            onClick = {}
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        PremiumQuickAction(
                            title = "Set Alert",
                            icon = Icons.Rounded.NotificationsNone,
                            color = CurrSecondaryAccent,
                            onClick = {}
                        )
                    }
                }
            }
        }
        
        if (showBaseSelector) {
            CurrencySelectorSheet(
                currencies = distinctCurrencies,
                onDismissRequest = { showBaseSelector = false },
                onCurrencySelected = { code -> 
                    baseCurrency = code
                    showBaseSelector = false
                }
            )
        }
        if (showTargetSelector) {
            CurrencySelectorSheet(
                currencies = distinctCurrencies,
                onDismissRequest = { showTargetSelector = false },
                onCurrencySelected = { code -> 
                    targetCurrency = code
                    showTargetSelector = false
                }
            )
        }
    }
}

@Composable
fun PremiumConversionCard(
    baseAmount: String,
    exchangeRate: Double,
    baseCurrency: String,
    targetCurrency: String,
    onAmountChange: (String) -> Unit,
    onSwap: () -> Unit,
    onSelectBase: () -> Unit,
    onSelectTarget: () -> Unit
) {
    val amountValue = baseAmount.toDoubleOrNull() ?: 0.0
    val convertedValue = amountValue * exchangeRate
    val formatDec = { value: Double -> String.format(Locale.US, "%.2f", value) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(ResponsiveUtils.CardShape)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        CurrSurfaceColor,
                        CurrSurfaceColor.copy(alpha = 0.6f)
                    )
                )
            )
            .border(1.dp, CurrCardStrokeColor.copy(alpha = 0.5f), ResponsiveUtils.CardShape)
            .padding(24.dp)
    ) {
        Column {
            // Source Currency
            CurrencyInputSection(
                label = "You Pay",
                amount = baseAmount,
                currencyCode = baseCurrency,
                isEditable = true,
                onAmountChange = onAmountChange,
                onCurrencyClick = onSelectBase
            )
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                HorizontalDivider(color = CurrCardStrokeColor.copy(alpha = 0.5f))
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(CurrBgColor)
                        .border(1.dp, CurrSecondaryAccent.copy(alpha = 0.5f), CircleShape)
                        .clickable { onSwap() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.SwapVert,
                        contentDescription = "Swap",
                        tint = CurrSecondaryAccent,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            // Target Currency
            CurrencyInputSection(
                label = "You Receive",
                amount = formatDec(convertedValue),
                currencyCode = targetCurrency,
                isEditable = false,
                onAmountChange = {},
                onCurrencyClick = onSelectTarget
            )
        }
    }
}

@Composable
fun CurrencyInputSection(
    label: String,
    amount: String,
    currencyCode: String,
    isEditable: Boolean,
    onAmountChange: (String) -> Unit,
    onCurrencyClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    
    val borderColor by animateColorAsState(
        targetValue = if (isFocused) CurrSecondaryAccent else Color.Transparent,
        animationSpec = tween(durationMillis = 300),
        label = "FocusBorderColor"
    )
    val backgroundColor by animateColorAsState(
        targetValue = if (isFocused) CurrSecondaryAccent.copy(alpha = 0.05f) else Color.Transparent,
        animationSpec = tween(durationMillis = 300),
        label = "FocusBackgroundColor"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 16.dp)) {
            Text(label, color = ResponsiveUtils.TextSecondary, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            if (isEditable) {
                val textSize = if (amount.length > 12) 20.sp else if (amount.length > 8) 24.sp else 32.sp
                BasicTextField(
                    value = amount,
                    onValueChange = onAmountChange,
                    modifier = Modifier.fillMaxWidth(),
                    interactionSource = interactionSource,
                    textStyle = TextStyle(
                        color = Color.White,
                        fontSize = textSize,
                        fontWeight = FontWeight.Bold
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    cursorBrush = SolidColor(CurrSecondaryAccent),
                    singleLine = true,
                    decorationBox = { innerTextField ->
                        if (amount.isEmpty()) {
                            Text("0", color = Color.White.copy(alpha = 0.3f), fontSize = textSize, fontWeight = FontWeight.Bold)
                        }
                        innerTextField()
                    }
                )
            } else {
                AutoSizeText(
                    text = amount,
                    color = CurrPrimaryAccent,
                    maxTextSize = 32.sp,
                    minTextSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }
        }
        
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(CurrBgColor)
                .clickable { onCurrencyClick() }
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(getFlagEmoji(currencyCode), fontSize = 24.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(currencyCode, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(4.dp))
            Icon(Icons.Rounded.KeyboardArrowDown, contentDescription = null, tint = Color.White)
        }
    }
}

@Composable
fun PremiumChartSection(viewModel: CurrencyViewModel, exchangeRate: Double, baseCurrency: String, targetCurrency: String) {
    var selectedTab by remember { mutableStateOf("1W") }
    
    LaunchedEffect(exchangeRate, baseCurrency, targetCurrency, selectedTab) {
        viewModel.fetchChartData(baseCurrency, targetCurrency, selectedTab, exchangeRate)
    }
    
    val chartState by viewModel.chartState.collectAsState()
    val dataPoints = chartState.points

    val minVal = chartState.minVal
    val maxVal = chartState.maxVal
    val range = if (maxVal > minVal) maxVal - minVal else exchangeRate * 0.01
    val paddedMin = minVal - range * 0.1
    val paddedMax = maxVal + range * 0.1
    val paddedRange = if (paddedMax > paddedMin) paddedMax - paddedMin else 1.0
    
    val trendIsUp = chartState.trendPercent >= 0
    val trendColor = if (trendIsUp) Color(0xFF4ADE80) else Color(0xFFFF5252)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(ResponsiveUtils.CardShape)
            .background(CurrSurfaceColor)
            .border(1.dp, CurrCardStrokeColor.copy(alpha = 0.3f), ResponsiveUtils.CardShape)
            .padding(vertical = 24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (chartState.isLoading) {
                CircularProgressIndicator(color = CurrSecondaryAccent, modifier = Modifier.size(20.dp))
            } else {
                Text(
                    text = "${String.format(Locale.US, "%+.2f", chartState.trendPercent)}%",
                    color = trendColor,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(CurrBgColor)
                    .padding(4.dp)
            ) {
                val tabs = listOf("1D", "1W", "1M", "1Y", "5Y")
                tabs.forEach { tab ->
                    val isSelected = tab == selectedTab
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSelected) CurrSurfaceColor else Color.Transparent)
                            .clickable { selectedTab = tab }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = tab,
                            color = if (isSelected) Color.White else ResponsiveUtils.TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .padding(horizontal = 24.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                
                if (dataPoints.size > 1) {
                    val path = Path().apply {
                        for (i in dataPoints.indices) {
                            val x = w * (i.toFloat() / (dataPoints.size - 1))
                            val normalizedY = 1f - ((dataPoints[i] - paddedMin) / paddedRange).toFloat()
                            val y = h * normalizedY
                            if (i == 0) moveTo(x, y) else lineTo(x, y)
                        }
                    }
                    
                    // Gradient Fill
                    val fillPath = Path().apply {
                        addPath(path)
                        lineTo(w, h)
                        lineTo(0f, h)
                        close()
                    }
                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(trendColor.copy(alpha = 0.2f), Color.Transparent)
                        )
                    )
                    
                    // Line
                    drawPath(
                        path = path,
                        color = trendColor,
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )
                    
                    // End Point
                    val lastNormalizedY = 1f - ((dataPoints.last() - paddedMin) / paddedRange).toFloat()
                    drawCircle(color = Color.White, radius = 5.dp.toPx(), center = Offset(w, h * lastNormalizedY))
                    drawCircle(color = trendColor, radius = 3.dp.toPx(), center = Offset(w, h * lastNormalizedY))
                } else if (dataPoints.isNotEmpty()) {
                    val normalizedY = 1f - ((dataPoints[0] - paddedMin) / paddedRange).toFloat()
                    val y = h * normalizedY
                    drawPath(
                        path = Path().apply { moveTo(0f, y); lineTo(w, y) },
                        color = trendColor,
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )
                    drawCircle(color = Color.White, radius = 5.dp.toPx(), center = Offset(w, y))
                    drawCircle(color = trendColor, radius = 3.dp.toPx(), center = Offset(w, y))
                }
            }
        }
    }
}

@Composable
fun MarketRateCard(currency: String, rate: Double, baseCurrency: String) {
    val trend = remember(currency, rate) { java.util.Random(currency.hashCode().toLong()).nextDouble() * 2 - 1 } // -1 to 1
    val isUp = trend >= 0
    val trendColor = if (isUp) Color(0xFF4ADE80) else Color(0xFFFF5252)
    val trendIcon = if (isUp) Icons.AutoMirrored.Rounded.TrendingUp else Icons.AutoMirrored.Rounded.TrendingDown
    
    Column(
        modifier = Modifier
            .width(140.dp)
            .clip(ResponsiveUtils.CardShape)
            .background(CurrSurfaceColor)
            .border(1.dp, CurrCardStrokeColor.copy(alpha = 0.3f), ResponsiveUtils.CardShape)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(getFlagEmoji(currency), fontSize = 24.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(currency, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = String.format(Locale.US, "%.4f", rate),
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(trendIcon, contentDescription = null, tint = trendColor, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = String.format(Locale.US, "%+.2f%%", trend),
                color = trendColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun PremiumQuickAction(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(ResponsiveUtils.ButtonShape)
            .background(CurrSurfaceColor)
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencySelectorSheet(
    currencies: List<String>,
    onDismissRequest: () -> Unit,
    onCurrencySelected: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredCurrencies = currencies.filter { it.contains(searchQuery, ignoreCase = true) }
    
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        containerColor = CurrSurfaceColor,
        dragHandle = { BottomSheetDefaults.DragHandle(color = Color.White.copy(alpha = 0.2f)) }
    ) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .fillMaxHeight(0.8f)
        ) {
            Text("Select Currency", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search", color = ResponsiveUtils.TextSecondary) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CurrSecondaryAccent,
                    unfocusedBorderColor = CurrCardStrokeColor,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                singleLine = true,
                leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null, tint = ResponsiveUtils.TextSecondary) },
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(filteredCurrencies) { currencyCode ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onCurrencySelected(currencyCode) }
                            .padding(vertical = 16.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(getFlagEmoji(currencyCode), fontSize = 28.sp)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(currencyCode, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}
