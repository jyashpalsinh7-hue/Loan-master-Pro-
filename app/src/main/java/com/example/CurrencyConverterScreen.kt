package com.example

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.NumberFormat
import java.util.Locale

val CurrBgColor = Color(0xFF0B132B)
val CurrSurfaceColor = Color(0xFF152238)
val CurrNeonGreen = Color(0xFF4ADE80)
val CurrAccentBlue = Color(0xFF3B82F6)
val CurrAccentRed = Color(0xFFFF5252)
val CurrCardStrokeColor = Color(0xFF263238)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyConverterScreen(onNavigateBack: () -> Unit, viewModel: CurrencyViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    var baseAmount by remember { mutableStateOf("10000") }
    var baseCurrency by remember { mutableStateOf("INR") }
    var targetCurrency by remember { mutableStateOf("USD") }
    
    var showBaseSelector by remember { mutableStateOf(false) }
    var showTargetSelector by remember { mutableStateOf(false) }
    
    val uiState by viewModel.uiState.collectAsState()
    
    val exchangeRateUsd = uiState.rates[targetCurrency] ?: 1.0
    val exchangeRateEur = uiState.rates["EUR"] ?: 0.010981
    val exchangeRateGbp = uiState.rates["GBP"] ?: 0.009498
    val exchangeRateAed = uiState.rates["AED"] ?: 0.044002
    val lastUpdated = uiState.lastUpdated.ifEmpty { "20 May 2025" }
    
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
                        imageVector = Icons.Rounded.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp).clickable { onNavigateBack() }
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Currency Converter", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Live exchange rates", color = Color(0xFFB0BEC5), fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(CurrNeonGreen))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Live", color = CurrNeonGreen, fontSize = 12.sp)
                        }
                    }
                    Icon(imageVector = Icons.Rounded.StarBorder, contentDescription = "Favorite", tint = Color.White, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(imageVector = Icons.Rounded.History, contentDescription = "History", tint = Color.White, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(imageVector = Icons.Rounded.MoreVert, contentDescription = "More", tint = Color.White, modifier = Modifier.size(24.dp))
                }
            }
        },
        bottomBar = { AppBottomBar(selectedRoute = "currency") },
        containerColor = CurrBgColor
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Main Conversion Card
            MainConversionCard(
                baseAmount = baseAmount,
                exchangeRate = exchangeRateUsd,
                baseCurrency = baseCurrency,
                targetCurrency = targetCurrency,
                lastUpdated = lastUpdated,
                onAmountChange = { baseAmount = it },
                onSwap = {
                    val temp = baseCurrency
                    baseCurrency = targetCurrency
                    targetCurrency = temp
                    // LaunchEffect fetches rates automatically when baseCurrency updates
                },
                onRefresh = { viewModel.fetchRates(baseCurrency) },
                onSelectBase = { showBaseSelector = true },
                onSelectTarget = { showTargetSelector = true }
            )

            // Editable Other Currencies
            Column(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(CurrSurfaceColor).border(1.dp, CurrCardStrokeColor, RoundedCornerShape(12.dp)).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Instant Multi-Currency", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(CurrAccentBlue.copy(alpha = 0.2f)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                        Text("PRO", color = CurrAccentBlue, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
                EditableCurrencyRow("🇪🇺", "EUR", "Euro", exchangeRateEur, baseAmount)
                EditableCurrencyRow("🇬🇧", "GBP", "British Pound", exchangeRateGbp, baseAmount)
                EditableCurrencyRow("🇦🇪", "AED", "UAE Dirham", exchangeRateAed, baseAmount)
            }

            // Quick Actions Row
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    QuickConvertCard(baseAmount, baseCurrency) { baseAmount = it }
                }
                Box(modifier = Modifier.weight(1f)) {
                    FavoritesCard()
                }
            }

            // Popular Currencies
            PopularCurrenciesRow(uiState.rates)

            // Data & Summary Row
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(modifier = Modifier.weight(0.55f)) {
                    ExchangeRateChartCard(exchangeRateUsd, baseCurrency, targetCurrency)
                }
                Box(modifier = Modifier.weight(0.45f)) {
                    ConverterSummaryCard(baseAmount, exchangeRateUsd, baseCurrency, targetCurrency)
                }
            }

            // Recent Conversions List
            RecentConversionsList()

            // Action Button Grid
            ActionButtonGrid { viewModel.fetchRates(baseCurrency) }
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
        containerColor = CurrSurfaceColor
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp).fillMaxHeight(0.8f)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search currency...", color = Color(0xFFB0BEC5)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CurrAccentBlue,
                    unfocusedBorderColor = CurrCardStrokeColor,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(filteredCurrencies) { currencyCode ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onCurrencySelected(currencyCode) }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(getFlagEmoji(currencyCode), fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(currencyCode, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

fun getFlagEmoji(currencyCode: String): String {
    if (currencyCode.length < 2) return "🌍"
    if (currencyCode == "EUR") return "🇪🇺"
    val countryCode = currencyCode.substring(0, 2)
    val firstLetter = Character.codePointAt(countryCode, 0) - 0x41 + 0x1F1E6
    val secondLetter = Character.codePointAt(countryCode, 1) - 0x41 + 0x1F1E6
    return try {
        String(Character.toChars(firstLetter)) + String(Character.toChars(secondLetter))
    } catch (e: Exception) {
        "🌍"
    }
}

@Composable
fun MainConversionCard(
    baseAmount: String, 
    exchangeRate: Double, 
    baseCurrency: String,
    targetCurrency: String,
    lastUpdated: String, 
    onAmountChange: (String) -> Unit,
    onSwap: () -> Unit = {},
    onRefresh: () -> Unit = {},
    onSelectBase: () -> Unit = {},
    onSelectTarget: () -> Unit = {}
) {
    val amountValue = baseAmount.toDoubleOrNull() ?: 0.0
    val convertedValue = amountValue * exchangeRate
    val formatDec = { value: Double -> String.format(Locale.US, "%.2f", value) }

    Column(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(CurrSurfaceColor).border(1.dp, CurrCardStrokeColor, RoundedCornerShape(12.dp))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // You Send
            Column(modifier = Modifier.weight(1f)) {
                Text("You Send", color = Color(0xFFB0BEC5), fontSize = 12.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { onSelectBase() }) {
                    Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(Color.White), contentAlignment = Alignment.Center) {
                        Text(getFlagEmoji(baseCurrency), fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(baseCurrency, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Icon(Icons.Rounded.KeyboardArrowDown, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                        Text("Base Currency", color = Color(0xFFB0BEC5), fontSize = 10.sp)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                val inputLength = baseAmount.length
                val scaledInputFontSize = when {
                    inputLength >= 15 -> 16.sp
                    inputLength >= 12 -> 18.sp
                    inputLength >= 10 -> 24.sp
                    inputLength >= 8 -> 28.sp
                    else -> 32.sp
                }
                BasicTextField(
                    value = baseAmount,
                    onValueChange = onAmountChange,
                    textStyle = TextStyle(color = CurrNeonGreen, fontSize = scaledInputFontSize, fontWeight = FontWeight.ExtraBold),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    cursorBrush = SolidColor(CurrNeonGreen),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    decorationBox = { innerTextField ->
                        if (baseAmount.isEmpty()) {
                            Text("0", color = CurrNeonGreen.copy(alpha = 0.5f), fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
                        }
                        innerTextField()
                    }
                )
            }

            // Swap Icon
            Box(
                modifier = Modifier.padding(horizontal = 8.dp).size(48.dp).clip(CircleShape).background(CurrBgColor).border(1.dp, CurrAccentBlue, CircleShape).clickable { onSwap() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.SyncAlt, contentDescription = "Swap", tint = CurrAccentBlue, modifier = Modifier.size(24.dp))
            }

            // You Get
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                Text("You Get", color = Color(0xFFB0BEC5), fontSize = 12.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { onSelectTarget() }) {
                    Column(horizontalAlignment = Alignment.End) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(targetCurrency, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Icon(Icons.Rounded.KeyboardArrowDown, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                        Text("Target Currency", color = Color(0xFFB0BEC5), fontSize = 10.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(Color.White), contentAlignment = Alignment.Center) {
                        Text(getFlagEmoji(targetCurrency), fontSize = 20.sp)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                val targetText = formatDec(convertedValue)
                val targetLength = targetText.length
                val scaledTargetFontSize = when {
                    targetLength >= 15 -> 16.sp
                    targetLength >= 12 -> 18.sp
                    targetLength >= 10 -> 24.sp
                    targetLength >= 8 -> 28.sp
                    else -> 32.sp
                }
                Text(
                    text = targetText,
                    color = CurrNeonGreen,
                    fontSize = scaledTargetFontSize,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.End,
                    maxLines = 1,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        HorizontalDivider(color = CurrCardStrokeColor)

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("1 $baseCurrency = ${String.format(Locale.US, "%.5f", exchangeRate)} $targetCurrency", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.width(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(CurrNeonGreen.copy(alpha = 0.1f)).padding(horizontal = 4.dp, vertical = 2.dp)) {
                    Icon(Icons.Rounded.TrendingUp, contentDescription = null, tint = CurrNeonGreen, modifier = Modifier.size(12.dp))
                    Text("+0.15%", color = CurrNeonGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Last Updated: $lastUpdated", color = Color(0xFFB0BEC5), fontSize = 11.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Rounded.Refresh, contentDescription = "Refresh", tint = CurrAccentBlue, modifier = Modifier.size(16.dp).clickable { onRefresh() })
            }
        }
    }
}

@Composable
fun EditableCurrencyRow(flag: String, code: String, name: String, rate: Double, baseAmount: String) {
    val amountValue = baseAmount.toDoubleOrNull() ?: 0.0
    val convertedValue = amountValue * rate
    val formatDec = { value: Double -> String.format(Locale.US, "%.2f", value) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(Color.White), contentAlignment = Alignment.Center) {
            Text(flag, fontSize = 18.sp)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(code, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(name, color = Color(0xFFB0BEC5), fontSize = 10.sp)
        }
        OutlinedTextField(
            value = formatDec(convertedValue),
            onValueChange = {},
            readOnly = true,
            textStyle = TextStyle(color = CurrNeonGreen, fontSize = 16.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.End),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = CurrAccentBlue,
                unfocusedBorderColor = CurrCardStrokeColor,
                focusedContainerColor = CurrBgColor,
                unfocusedContainerColor = CurrBgColor
            ),
            modifier = Modifier.width(140.dp).height(50.dp)
        )
    }
}

@Composable
fun QuickConvertCard(currentAmount: String, baseCurrency: String, onAmountChange: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(CurrSurfaceColor).border(1.dp, CurrCardStrokeColor, RoundedCornerShape(12.dp)).padding(16.dp)
    ) {
        Text("Quick Convert ($baseCurrency)", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.horizontalScroll(rememberScrollState())) {
            QuickChip("1000", currentAmount, baseCurrency, onAmountChange)
            QuickChip("10000", currentAmount, baseCurrency, onAmountChange)
            QuickChip("50000", currentAmount, baseCurrency, onAmountChange)
            QuickChip("100000", currentAmount, baseCurrency, onAmountChange)
        }
    }
}

@Composable
fun QuickChip(amount: String, currentAmount: String, baseCurrency: String, onAmountChange: (String) -> Unit) {
    val isSelected = amount == currentAmount
    val bgColor = if (isSelected) CurrAccentBlue else CurrBgColor
    val textColor = if (isSelected) Color.White else Color(0xFFB0BEC5)
    val borderColor = if (isSelected) CurrAccentBlue else CurrCardStrokeColor
    
    Box(
        modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(bgColor).border(1.dp, borderColor, RoundedCornerShape(6.dp)).clickable { onAmountChange(amount) }.padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        val formatNum = { value: Double ->
            val format = NumberFormat.getNumberInstance(Locale.US)
            format.maximumFractionDigits = 0
            format.format(value)
        }
        Text("$baseCurrency ${formatNum(amount.toDoubleOrNull() ?: 0.0)}", color = textColor, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun FavoritesCard() {
    Column(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(CurrSurfaceColor).border(1.dp, CurrCardStrokeColor, RoundedCornerShape(12.dp)).padding(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Favorites", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Text("Edit", color = CurrAccentBlue, fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.horizontalScroll(rememberScrollState())) {
            FavChip("INR \u2192", "🇺🇸 USD")
            FavChip("INR \u2192", "🇦🇪 AED")
            FavChip("INR \u2192", "🇸🇦 SAR")
        }
    }
}

@Composable
fun FavChip(prefix: String, target: String) {
    Row(
        modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(CurrBgColor).border(1.dp, CurrCardStrokeColor, RoundedCornerShape(6.dp)).padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Rounded.Star, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(12.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(prefix, color = Color(0xFFB0BEC5), fontSize = 10.sp)
        Spacer(modifier = Modifier.width(2.dp))
        Text(target, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun PopularCurrenciesRow(rates: Map<String, Double> = emptyMap()) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Popular Currencies", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("View All", color = CurrAccentBlue, fontSize = 12.sp)
                Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = CurrAccentBlue, modifier = Modifier.size(16.dp))
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(5) { index ->
                val currency = when(index) {
                    0 -> Triple("🇺🇸", "USD", "US Dollar" to Triple(String.format(Locale.US, "%.5f", rates["USD"] ?: 0.01198), "+0.15%", CurrNeonGreen))
                    1 -> Triple("🇪🇺", "EUR", "Euro" to Triple(String.format(Locale.US, "%.5f", rates["EUR"] ?: 0.01098), "+0.21%", CurrNeonGreen))
                    2 -> Triple("🇬🇧", "GBP", "British Pound" to Triple(String.format(Locale.US, "%.5f", rates["GBP"] ?: 0.00949), "+0.18%", CurrNeonGreen))
                    3 -> Triple("🇦🇪", "AED", "UAE Dirham" to Triple(String.format(Locale.US, "%.5f", rates["AED"] ?: 0.04400), "+0.16%", CurrNeonGreen))
                    else -> Triple("🇯🇵", "JPY", "Japanese Yen" to Triple(String.format(Locale.US, "%.4f", rates["JPY"] ?: 1.8478), "-0.08%", CurrAccentRed))
                }
                
                Column(
                    modifier = Modifier.width(130.dp).clip(RoundedCornerShape(12.dp)).background(CurrSurfaceColor).border(1.dp, CurrCardStrokeColor, RoundedCornerShape(12.dp)).padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(Color.White), contentAlignment = Alignment.Center) {
                            Text(currency.first, fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(currency.second, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(currency.third.first, color = Color(0xFFB0BEC5), fontSize = 9.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(currency.third.second.first, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(if (currency.third.second.third == CurrNeonGreen) Icons.Rounded.TrendingUp else Icons.Rounded.TrendingDown, contentDescription = null, tint = currency.third.second.third, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(currency.third.second.second, color = currency.third.second.third, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}

@Composable
fun ExchangeRateChartCard(exchangeRate: Double, baseCurrency: String, targetCurrency: String) {
    Column(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(CurrSurfaceColor).border(1.dp, CurrCardStrokeColor, RoundedCornerShape(12.dp)).padding(16.dp)
    ) {
        Text("Chart ($baseCurrency \u2192 $targetCurrency)", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            val tabs = listOf("1D", "1W", "1M", "1Y", "5Y")
            tabs.forEach { tab ->
                val isSelected = tab == "1D"
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (isSelected) CurrAccentBlue.copy(alpha = 0.2f) else Color.Transparent)
                        .border(1.dp, if (isSelected) CurrAccentBlue else CurrCardStrokeColor, RoundedCornerShape(4.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(tab, color = if (isSelected) CurrAccentBlue else Color(0xFFB0BEC5), fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(modifier = Modifier.fillMaxWidth().height(160.dp)) {
            Column(modifier = Modifier.fillMaxHeight().padding(end = 8.dp), verticalArrangement = Arrangement.SpaceBetween) {
                listOf("0.0122", "0.0120", "0.0118", "0.0116", "0.0114").forEach {
                    Text(it, color = Color(0xFFB0BEC5), fontSize = 10.sp)
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    Canvas(modifier = Modifier.fillMaxSize().padding(vertical = 12.dp)) {
                        val w = size.width
                        val h = size.height
                        
                        val path = Path().apply {
                            moveTo(0f, h * 0.8f)
                            lineTo(w * 0.1f, h * 0.7f)
                            lineTo(w * 0.2f, h * 0.75f)
                            lineTo(w * 0.3f, h * 0.5f)
                            lineTo(w * 0.4f, h * 0.6f)
                            lineTo(w * 0.5f, h * 0.45f)
                            lineTo(w * 0.6f, h * 0.5f)
                            lineTo(w * 0.7f, h * 0.3f)
                            lineTo(w * 0.8f, h * 0.2f)
                            lineTo(w * 0.9f, h * 0.35f)
                            lineTo(w, h * 0.25f)
                        }
                        
                        drawPath(path, color = CurrAccentBlue, style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round))
                        drawCircle(color = CurrAccentBlue, radius = 4.dp.toPx(), center = Offset(w, h * 0.25f))
                    }
                    
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(y = 8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(CurrAccentBlue)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(String.format(Locale.US, "%.5f", exchangeRate), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Row(modifier = Modifier.fillMaxWidth().padding(top = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("14 May", color = Color(0xFFB0BEC5), fontSize = 10.sp)
                    Text("20 May", color = Color(0xFFB0BEC5), fontSize = 10.sp)
                }
            }
        }
    }
}

@Composable
fun ConverterSummaryCard(baseAmount: String, exchangeRate: Double, baseCurrency: String, targetCurrency: String) {
    val amountValue = baseAmount.toDoubleOrNull() ?: 0.0
    val formatDec = { value: Double -> String.format(Locale.US, "%.2f", value) }
    
    val formatInrNum = { value: Double ->
        val format = NumberFormat.getNumberInstance(Locale.US)
        format.maximumFractionDigits = 0
        format.format(value)
    }

    Column(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(CurrSurfaceColor).border(1.dp, CurrCardStrokeColor, RoundedCornerShape(12.dp)).padding(16.dp)
    ) {
        Text("Converter Summary", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(24.dp))
        
        SummaryRow("Amount You Send", "$baseCurrency ${formatInrNum(amountValue)}", Color.White)
        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = CurrCardStrokeColor)
        Spacer(modifier = Modifier.height(16.dp))
        SummaryRow("Exchange Rate", "1 $baseCurrency = ${String.format(Locale.US, "%.5f", exchangeRate)} $targetCurrency", Color(0xFFB0BEC5))
        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = CurrCardStrokeColor)
        Spacer(modifier = Modifier.height(16.dp))
        SummaryRow("Amount You Get", "$targetCurrency ${formatDec(amountValue * exchangeRate)}", CurrNeonGreen, isBold = true)
        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = CurrCardStrokeColor)
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("You Save", color = Color(0xFFB0BEC5), fontSize = 12.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(Icons.Rounded.Info, contentDescription = null, tint = Color(0xFFB0BEC5), modifier = Modifier.size(12.dp))
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("₹0.00", color = Color.White, fontSize = 13.sp)
                Text("(No markup applied)", color = Color(0xFFB0BEC5), fontSize = 10.sp)
            }
        }
    }
}

@Composable
fun SummaryRow(label: String, value: String, valueColor: Color, isBold: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = Color(0xFFB0BEC5), fontSize = 12.sp)
        Text(value, color = valueColor, fontSize = 13.sp, fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal)
    }
}

@Composable
fun RecentConversionsList() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Recent Conversions", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("View All History", color = CurrAccentBlue, fontSize = 12.sp)
                Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = CurrAccentBlue, modifier = Modifier.size(16.dp))
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        
        Column(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(CurrSurfaceColor).border(1.dp, CurrCardStrokeColor, RoundedCornerShape(12.dp))
        ) {
            RecentRow("🇺🇸", "INR \u2192 USD", "20 May 2025, 09:41 AM", "₹10,000.00", "$119.85", CurrNeonGreen)
            HorizontalDivider(color = CurrCardStrokeColor)
            RecentRow("🇪🇺", "INR \u2192 EUR", "19 May 2025, 06:22 PM", "₹10,000.00", "€109.81", CurrNeonGreen)
            HorizontalDivider(color = CurrCardStrokeColor)
            RecentRow("🇦🇪", "INR \u2192 AED", "19 May 2025, 03:45 PM", "₹10,000.00", "د.إ 440.02", CurrNeonGreen)
        }
    }
}

@Composable
fun RecentRow(flag: String, title: String, date: String, amount1: String, amount2: String, amount2Color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(Color.White), contentAlignment = Alignment.Center) {
            Text(flag, fontSize = 18.sp)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(2.dp))
            Text(date, color = Color(0xFFB0BEC5), fontSize = 10.sp)
        }
        Text(amount1, color = Color.White, fontSize = 13.sp)
        Spacer(modifier = Modifier.width(12.dp))
        Icon(Icons.Rounded.ArrowForward, contentDescription = null, tint = Color(0xFFB0BEC5), modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(amount2, color = amount2Color, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.width(12.dp))
        Icon(Icons.Rounded.StarBorder, contentDescription = null, tint = Color(0xFFB0BEC5), modifier = Modifier.size(20.dp))
    }
}

@Composable
fun ActionButtonGrid(onUpdateRates: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(modifier = Modifier.weight(1f)) {
            ActionBtn("Share Result", "Share conversion", Icons.Rounded.Share, Color(0xFF7C4DFF)) { }
        }
        Box(modifier = Modifier.weight(1f)) {
            ActionBtn("Download PDF", "Save as PDF", Icons.Rounded.Download, CurrNeonGreen) { }
        }
        Box(modifier = Modifier.weight(1f)) {
            ActionBtn("Add to Favorites", "Save this pair", Icons.Rounded.StarBorder, Color(0xFFFFB300)) { }
        }
        Box(modifier = Modifier.weight(1f)) {
            ActionBtn("Update Rates", "Get latest rates", Icons.Rounded.Sync, CurrAccentBlue) { onUpdateRates() }
        }
    }
}

@Composable
fun ActionBtn(title: String, subtitle: String, icon: ImageVector, color: Color, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(CurrSurfaceColor).border(1.dp, CurrCardStrokeColor, RoundedCornerShape(8.dp)).clickable { onClick() }.padding(horizontal = 8.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(title, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Text(subtitle, color = Color(0xFFB0BEC5), fontSize = 10.sp)
        }
    }
}
