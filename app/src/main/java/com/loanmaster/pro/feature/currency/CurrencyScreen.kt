package com.loanmaster.pro.feature.currency

import com.loanmaster.pro.domain.model.*
import com.loanmaster.pro.feature.gst.*
import com.loanmaster.pro.feature.sip.*
import com.loanmaster.pro.core.ui.*
import com.loanmaster.pro.feature.history.*
import com.loanmaster.pro.core.theme.*
import com.loanmaster.pro.data.datastore.*
import com.loanmaster.pro.feature.settings.*
import com.loanmaster.pro.feature.rd.*
import com.loanmaster.pro.domain.calculator.*
import com.loanmaster.pro.data.local.entity.*
import com.loanmaster.pro.core.utils.*
import com.loanmaster.pro.data.local.dao.*
import com.loanmaster.pro.data.local.room.*
import com.loanmaster.pro.feature.emi.*
import com.loanmaster.pro.feature.loansummary.*
import com.loanmaster.pro.feature.prepayment.*
import com.loanmaster.pro.core.formatter.*
import com.loanmaster.pro.feature.fd.*
import com.loanmaster.pro.data.repository.*
import com.loanmaster.pro.core.navigation.*
import com.loanmaster.pro.feature.compare.*
import com.loanmaster.pro.feature.loaneligibility.*
import com.loanmaster.pro.core.responsive.*
import com.loanmaster.pro.feature.home.*

import androidx.window.core.layout.WindowWidthSizeClass



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
import androidx.compose.runtime.getValue
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle

import java.util.Locale

// Constants
val CurrBgColor = BackgroundDark
val CurrSurfaceColor = SurfaceDark
val CurrPrimaryAccent = AccentYellow
val CurrSecondaryAccent = AccentBlue
val CurrCardStrokeColor = CardStroke

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
fun CurrencyScreen(onNavigateBack: () -> Unit, viewModel: CurrencyViewModel = viewModel()) {
    // Force reload comment 3
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val sizeClass = when {
        configuration.screenWidthDp < 600 -> WindowWidthSizeClass.COMPACT
        configuration.screenWidthDp < 840 -> WindowWidthSizeClass.MEDIUM
        else -> WindowWidthSizeClass.EXPANDED
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val dummyCurrency = com.loanmaster.pro.LocalCurrency.current
    
    val baseAmount = uiState.baseAmountText
    val baseCurrency = uiState.baseCurrency
    val targetCurrency = uiState.targetCurrency
    val showBaseSelector = uiState.showBaseSelector
    val showTargetSelector = uiState.showTargetSelector
    val selectedTab = uiState.selectedTab
    
    val exchangeRateTarget = uiState.rates[targetCurrency] ?: 1.0
    val lastUpdated = uiState.lastUpdated.ifEmpty { "Recently" }
    
    val allCurrencies = listOf(baseCurrency, targetCurrency) + uiState.rates.keys.toList()
    val distinctCurrencies = allCurrencies.distinct()

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(CurrBgColor).statusBarsPadding()) {
                // FIX: Remove old offline banner, error handled in body
                if (uiState.error != null && uiState.rates.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFE53935))
                            .padding(horizontal = LoanMasterTheme.spacing.md, vertical = LoanMasterTheme.spacing.sm),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Rounded.Warning, contentDescription = null, tint = Color.White, modifier = Modifier.size(LoanMasterTheme.spacing.md))
                        Spacer(Modifier.widthIn(min = LoanMasterTheme.spacing.sm))
                        Text(
                            text = "Offline / Showing Last Known Rates",
                            color = Color.White,
                            fontSize = LoanMasterTheme.typography.label.fontSize,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = LoanMasterTheme.spacing.md, vertical = LoanMasterTheme.spacing.md),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(LoanMasterTheme.components.iconMedium).clickable { onNavigateBack() }
                    )
                    Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.md))
                    Text(
                        text = "Exchange",
                        color = Color.White,
                        fontSize = LoanMasterTheme.typography.title.fontSize,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { viewModel.updateInputs(refreshRates = true) }) {
                        Icon(imageVector = Icons.Rounded.Sync, contentDescription = "Refresh", tint = Color.White)
                    }
                }
            }
        },
        containerColor = CurrBgColor
    ) { innerPadding ->
        // FIX: Show full error card if rates are empty and error is present
        if (uiState.rates.isEmpty() && uiState.error != null) {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(LoanMasterTheme.spacing.screenPadding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Rounded.CloudOff,
                    contentDescription = "Offline",
                    tint = TextSecondary,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(Modifier.height(LoanMasterTheme.spacing.md))
                Text(
                    text = uiState.error ?: "Error",
                    color = TextPrimary,
                    fontSize = LoanMasterTheme.typography.body.fontSize,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(LoanMasterTheme.spacing.lg))
                Button(
                    onClick = { viewModel.updateInputs(refreshRates = true) },
                    colors = ButtonDefaults.buttonColors(containerColor = CurrSecondaryAccent)
                ) {
                    Text("Refresh")
                }
            }
        } else {
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentPadding = PaddingValues(
                horizontal = LoanMasterTheme.spacing.screenPadding,
                vertical = LoanMasterTheme.spacing.md
            ),
            verticalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.lg)
        ) {
            item {
                PremiumConversionCard(
                    baseAmount = baseAmount,
                    exchangeRate = exchangeRateTarget,
                    baseCurrency = baseCurrency,
                    targetCurrency = targetCurrency,
                    onAmountChange = { viewModel.updateInputs(baseAmount = it) },
                    onSwap = { viewModel.updateInputs(swapCurrencies = true) },
                    onSelectBase = { viewModel.updateInputs(showBaseSelector = true) },
                    onSelectTarget = { viewModel.updateInputs(showTargetSelector = true) }
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
                            fontSize = LoanMasterTheme.typography.title.fontSize,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Last updated $lastUpdated",
                            color = TextSecondary,
                            fontSize = LoanMasterTheme.typography.label.fontSize
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
                    fontSize = LoanMasterTheme.typography.title.fontSize,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = LoanMasterTheme.spacing.sm, bottom = LoanMasterTheme.spacing.xs)
                )
            }
            
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.md)
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
                    modifier = Modifier.fillMaxWidth().padding(top = LoanMasterTheme.spacing.sm),
                    horizontalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.md)
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
        
        }
        if (showBaseSelector) {
            CurrencySelectorSheet(
                currencies = distinctCurrencies,
                searchQuery = uiState.searchQuery,
                onSearchQueryChange = { viewModel.updateInputs(searchQuery = it) },
                onDismissRequest = { viewModel.updateInputs(showBaseSelector = false) },
                onCurrencySelected = { code -> viewModel.updateInputs(baseCurrency = code) }
            )
        }
        if (showTargetSelector) {
            CurrencySelectorSheet(
                currencies = distinctCurrencies,
                searchQuery = uiState.searchQuery,
                onSearchQueryChange = { viewModel.updateInputs(searchQuery = it) },
                onDismissRequest = { viewModel.updateInputs(showTargetSelector = false) },
                onCurrencySelected = { code -> viewModel.updateInputs(targetCurrency = code) }
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
            .clip(RoundedCornerShape(LoanMasterTheme.components.cardRadius))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        CurrSurfaceColor,
                        CurrSurfaceColor.copy(alpha = 0.6f)
                    )
                )
            )
            .border(1.dp, CurrCardStrokeColor.copy(alpha = 0.5f), RoundedCornerShape(LoanMasterTheme.components.cardRadius))
            .padding(LoanMasterTheme.spacing.lg)
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
                    .padding(vertical = LoanMasterTheme.spacing.md),
                contentAlignment = Alignment.Center
            ) {
                HorizontalDivider(color = CurrCardStrokeColor.copy(alpha = 0.5f))
                Box(
                    modifier = Modifier
                        .size(LoanMasterTheme.spacing.xl)
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
                        modifier = Modifier.size(LoanMasterTheme.spacing.lg)
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
            .clip(RoundedCornerShape(LoanMasterTheme.spacing.md))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(LoanMasterTheme.spacing.md))
            .padding(LoanMasterTheme.spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = LoanMasterTheme.spacing.md)) {
            Text(label, color = TextSecondary, fontSize = LoanMasterTheme.typography.body.fontSize)
            Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.sm))
            if (isEditable) {
                val textSize = if (amount.length > 12) LoanMasterTheme.typography.title.fontSize else if (amount.length > 8) LoanMasterTheme.typography.title.fontSize else LoanMasterTheme.typography.display.fontSize
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
                    maxTextSize = LoanMasterTheme.typography.display.fontSize,
                    minTextSize = LoanMasterTheme.typography.body.fontSize,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }
        }
        
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(LoanMasterTheme.spacing.md))
                .background(CurrBgColor)
                .clickable { onCurrencyClick() }
                .padding(horizontal = LoanMasterTheme.spacing.md, vertical = LoanMasterTheme.spacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(getFlagEmoji(currencyCode), fontSize = LoanMasterTheme.typography.title.fontSize)
            Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.sm))
            Text(currencyCode, color = Color.White, fontSize = LoanMasterTheme.typography.title.fontSize, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.xs))
            Icon(Icons.Rounded.KeyboardArrowDown, contentDescription = null, tint = Color.White)
        }
    }
}

@Composable
fun PremiumChartSection(viewModel: CurrencyViewModel, exchangeRate: Double, baseCurrency: String, targetCurrency: String) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val dummyCurrency = com.loanmaster.pro.LocalCurrency.current
    val selectedTab = uiState.selectedTab
    
    val dataPoints = uiState.chartPoints
    val minVal = uiState.chartMinVal
    val maxVal = uiState.chartMaxVal
    val range = if (maxVal > minVal) maxVal - minVal else exchangeRate * 0.01
    val paddedMin = minVal - range * 0.1
    val paddedMax = maxVal + range * 0.1
    val paddedRange = if (paddedMax > paddedMin) paddedMax - paddedMin else 1.0
    
    val trendIsUp = uiState.chartTrendPercent >= 0
    val trendColor = if (trendIsUp) Color(0xFF4ADE80) else Color(0xFFFF5252)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(LoanMasterTheme.components.cardRadius))
            .background(CurrSurfaceColor)
            .border(1.dp, CurrCardStrokeColor.copy(alpha = 0.3f), RoundedCornerShape(LoanMasterTheme.components.cardRadius))
            .padding(vertical = LoanMasterTheme.spacing.lg)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = LoanMasterTheme.spacing.lg),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (uiState.isChartLoading) {
                CircularProgressIndicator(color = CurrSecondaryAccent, modifier = Modifier.size(LoanMasterTheme.components.iconSmall))
            } else {
                Text(
                    text = "${String.format(Locale.US, "%+.2f", uiState.chartTrendPercent)}%",
                    color = trendColor,
                    fontSize = LoanMasterTheme.typography.title.fontSize,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(LoanMasterTheme.spacing.sm))
                    .background(CurrBgColor)
                    .padding(LoanMasterTheme.spacing.xs)
            ) {
                listOf("1D", "1W", "1M", "1Y", "5Y").forEach { tab ->
                    val isSelected = selectedTab == tab
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(LoanMasterTheme.spacing.xs))
                            .background(if (isSelected) CurrSurfaceColor else Color.Transparent)
                            .clickable { viewModel.updateInputs(tabSelected = tab) }
                            .padding(horizontal = LoanMasterTheme.spacing.md, vertical = LoanMasterTheme.spacing.xs)
                    ) {
                        Text(
                            text = tab,
                            color = if (isSelected) Color.White else TextSecondary,
                            fontSize = LoanMasterTheme.typography.label.fontSize,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(LoanMasterTheme.spacing.lg))
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = LoanMasterTheme.components.chartHeight, max = LoanMasterTheme.components.chartHeight + 50.dp)
        ) {
            if (dataPoints.size > 1) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height
                    
                    val path = Path()
                    var lastX = 0f
                    var lastY = 0f
                    
                    dataPoints.forEachIndexed { index, point ->
                        val x = (index.toFloat() / (dataPoints.size - 1)) * width
                        val y = height - ((point - paddedMin) / paddedRange * height).toFloat()
                        
                        if (index == 0) {
                            path.moveTo(x, y)
                        } else {
                            path.lineTo(x, y)
                        }
                        lastX = x
                        lastY = y
                    }
                    
                    val gradient = Brush.verticalGradient(
                        colors = listOf(trendColor.copy(alpha = 0.3f), Color.Transparent),
                        startY = 0f,
                        endY = height
                    )
                    
                    val fillPath = Path().apply {
                        addPath(path)
                        lineTo(lastX, height)
                        lineTo(0f, height)
                        close()
                    }
                    
                    drawPath(path = fillPath, brush = gradient)
                    drawPath(
                        path = path,
                        color = trendColor,
                        style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                    )
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
            .widthIn(min = LoanMasterTheme.components.featuredCardHeight)
            .clip(RoundedCornerShape(LoanMasterTheme.components.cardRadius))
            .background(CurrSurfaceColor)
            .border(1.dp, CurrCardStrokeColor.copy(alpha = 0.3f), RoundedCornerShape(LoanMasterTheme.components.cardRadius))
            .padding(LoanMasterTheme.spacing.md)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(getFlagEmoji(currency), fontSize = LoanMasterTheme.typography.title.fontSize)
            Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.sm))
            Text(currency, color = Color.White, fontSize = LoanMasterTheme.typography.body.fontSize, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.md))
        Text(
            text = String.format(Locale.US, "%.4f", rate),
            color = Color.White,
            fontSize = LoanMasterTheme.typography.title.fontSize,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.xs))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(trendIcon, contentDescription = null, tint = trendColor, modifier = Modifier.size(LoanMasterTheme.spacing.md))
            Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.xs))
            Text(
                text = String.format(Locale.US, "%+.2f%%", trend),
                color = trendColor,
                fontSize = LoanMasterTheme.typography.label.fontSize,
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
            .clip(RoundedCornerShape(LoanMasterTheme.components.buttonHeight / 2))
            .background(CurrSurfaceColor)
            .clickable { onClick() }
            .padding(LoanMasterTheme.spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(LoanMasterTheme.spacing.lg))
        Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.md))
        Text(title, color = Color.White, fontSize = LoanMasterTheme.typography.body.fontSize, fontWeight = FontWeight.Medium)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencySelectorSheet(
    currencies: List<String>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onDismissRequest: () -> Unit,
    onCurrencySelected: (String) -> Unit
) {
    val filteredCurrencies = currencies.filter { it.contains(searchQuery, ignoreCase = true) }
    
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        containerColor = CurrSurfaceColor,
        dragHandle = { BottomSheetDefaults.DragHandle(color = Color.White.copy(alpha = 0.2f)) }
    ) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = LoanMasterTheme.spacing.lg, vertical = LoanMasterTheme.spacing.sm)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .fillMaxHeight(0.8f)
        ) {
            Text("Select Currency", color = Color.White, fontSize = LoanMasterTheme.typography.title.fontSize, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.md))
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search", color = TextSecondary) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CurrSecondaryAccent,
                    unfocusedBorderColor = CurrCardStrokeColor,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                singleLine = true,
                leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = "Search", tint = TextSecondary) },
                shape = RoundedCornerShape(LoanMasterTheme.spacing.md)
            )
            Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.md))
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(filteredCurrencies) { currencyCode ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(LoanMasterTheme.spacing.md))
                            .clickable { onCurrencySelected(currencyCode) }
                            .padding(vertical = LoanMasterTheme.spacing.md, horizontal = LoanMasterTheme.spacing.sm),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(getFlagEmoji(currencyCode), fontSize = LoanMasterTheme.typography.display.fontSize)
                        Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.md))
                        Text(currencyCode, color = Color.White, fontSize = LoanMasterTheme.typography.title.fontSize, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
        }

