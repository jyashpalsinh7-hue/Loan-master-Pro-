import re

with open("app/src/main/java/com/loanmaster/pro/CurrencyConverterScreen.kt", "r") as f:
    content = f.read()

# Add compose imports
imports_to_add = "import androidx.lifecycle.compose.collectAsStateWithLifecycle\n"
content = content.replace("import androidx.lifecycle.viewmodel.compose.viewModel", "import androidx.lifecycle.viewmodel.compose.viewModel\n" + imports_to_add)

# Change local states in CurrencyConverterScreen
old_states_part1 = """    var baseAmount by remember { mutableStateOf("") }
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
    }"""

new_states_part1 = """    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    val baseAmount = uiState.baseAmountText
    val baseCurrency = uiState.baseCurrency
    val targetCurrency = uiState.targetCurrency
    val showBaseSelector = uiState.showBaseSelector
    val showTargetSelector = uiState.showTargetSelector
    val selectedTab = uiState.selectedTab
    
    val exchangeRateTarget = uiState.rates[targetCurrency] ?: 1.0
    val lastUpdated = uiState.lastUpdated.ifEmpty { "Recently" }
    
    val allCurrencies = listOf(baseCurrency, targetCurrency) + uiState.rates.keys.toList()
    val distinctCurrencies = allCurrencies.distinct()"""

content = content.replace(old_states_part1, new_states_part1)

# Fix PremiumConversionCard callbacks
old_conversion_card = """                    onAmountChange = { baseAmount = it },
                    onSwap = {
                        val temp = baseCurrency
                        baseCurrency = targetCurrency
                        targetCurrency = temp
                    },
                    onSelectBase = { showBaseSelector = true },
                    onSelectTarget = { showTargetSelector = true }"""

new_conversion_card = """                    onAmountChange = { viewModel.onEvent(CurrencyEvent.BaseAmountChanged(it)) },
                    onSwap = { viewModel.onEvent(CurrencyEvent.SwapCurrencies) },
                    onSelectBase = { viewModel.onEvent(CurrencyEvent.ShowBaseSelector(true)) },
                    onSelectTarget = { viewModel.onEvent(CurrencyEvent.ShowTargetSelector(true)) }"""

content = content.replace(old_conversion_card, new_conversion_card)

# Fix Refresh icon
content = content.replace('IconButton(onClick = { viewModel.fetchRates(baseCurrency) }) {', 'IconButton(onClick = { viewModel.onEvent(CurrencyEvent.RefreshRates) }) {')

# Fix Selectors
old_base_selector = """        if (showBaseSelector) {
            CurrencySelectorSheet(
                currencies = distinctCurrencies,
                onDismissRequest = { showBaseSelector = false },
                onCurrencySelected = { code -> 
                    baseCurrency = code
                    showBaseSelector = false
                }
            )
        }"""
        
new_base_selector = """        if (showBaseSelector) {
            CurrencySelectorSheet(
                currencies = distinctCurrencies,
                searchQuery = uiState.searchQuery,
                onSearchQueryChange = { viewModel.onEvent(CurrencyEvent.SearchQueryChanged(it)) },
                onDismissRequest = { viewModel.onEvent(CurrencyEvent.ShowBaseSelector(false)) },
                onCurrencySelected = { code -> viewModel.onEvent(CurrencyEvent.BaseCurrencySelected(code)) }
            )
        }"""
content = content.replace(old_base_selector, new_base_selector)

old_target_selector = """        if (showTargetSelector) {
            CurrencySelectorSheet(
                currencies = distinctCurrencies,
                onDismissRequest = { showTargetSelector = false },
                onCurrencySelected = { code -> 
                    targetCurrency = code
                    showTargetSelector = false
                }
            )
        }"""

new_target_selector = """        if (showTargetSelector) {
            CurrencySelectorSheet(
                currencies = distinctCurrencies,
                searchQuery = uiState.searchQuery,
                onSearchQueryChange = { viewModel.onEvent(CurrencyEvent.SearchQueryChanged(it)) },
                onDismissRequest = { viewModel.onEvent(CurrencyEvent.ShowTargetSelector(false)) },
                onCurrencySelected = { code -> viewModel.onEvent(CurrencyEvent.TargetCurrencySelected(code)) }
            )
        }"""
content = content.replace(old_target_selector, new_target_selector)

# Change PremiumChartSection
old_chart_section = """fun PremiumChartSection(viewModel: CurrencyViewModel, exchangeRate: Double, baseCurrency: String, targetCurrency: String) {
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
    val trendColor = if (trendIsUp) Color(0xFF4ADE80) else Color(0xFFFF5252)"""

new_chart_section = """fun PremiumChartSection(viewModel: CurrencyViewModel, exchangeRate: Double, baseCurrency: String, targetCurrency: String) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedTab = uiState.selectedTab
    
    val dataPoints = uiState.chartPoints
    val minVal = uiState.chartMinVal
    val maxVal = uiState.chartMaxVal
    val range = if (maxVal > minVal) maxVal - minVal else exchangeRate * 0.01
    val paddedMin = minVal - range * 0.1
    val paddedMax = maxVal + range * 0.1
    val paddedRange = if (paddedMax > paddedMin) paddedMax - paddedMin else 1.0
    
    val trendIsUp = uiState.chartTrendPercent >= 0
    val trendColor = if (trendIsUp) Color(0xFF4ADE80) else Color(0xFFFF5252)"""

content = content.replace(old_chart_section, new_chart_section)

# In PremiumChartSection, replace `chartState.isLoading` with `uiState.isChartLoading`
content = content.replace("if (chartState.isLoading)", "if (uiState.isChartLoading)")
content = content.replace("chartState.trendPercent", "uiState.chartTrendPercent")
content = content.replace('selectedTab = tab', 'viewModel.onEvent(CurrencyEvent.TabSelected(tab))')

# Update CurrencySelectorSheet
old_selector_sheet_def = """fun CurrencySelectorSheet(
    currencies: List<String>,
    onDismissRequest: () -> Unit,
    onCurrencySelected: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredCurrencies = currencies.filter { it.contains(searchQuery, ignoreCase = true) }"""

new_selector_sheet_def = """fun CurrencySelectorSheet(
    currencies: List<String>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onDismissRequest: () -> Unit,
    onCurrencySelected: (String) -> Unit
) {
    val filteredCurrencies = currencies.filter { it.contains(searchQuery, ignoreCase = true) }"""

content = content.replace(old_selector_sheet_def, new_selector_sheet_def)

content = content.replace("onValueChange = { searchQuery = it },", "onValueChange = onSearchQueryChange,")

with open("app/src/main/java/com/loanmaster/pro/CurrencyConverterScreen.kt", "w") as f:
    f.write(content)

