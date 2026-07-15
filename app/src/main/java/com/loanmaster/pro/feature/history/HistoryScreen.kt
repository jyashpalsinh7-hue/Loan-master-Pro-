package com.loanmaster.pro.feature.history

import com.loanmaster.pro.data.local.entity.CalculationHistory
import com.loanmaster.pro.core.theme.*
import com.loanmaster.pro.core.formatter.*
import com.loanmaster.pro.domain.calculator.*
import com.loanmaster.pro.domain.model.GstMode
import androidx.window.core.layout.WindowWidthSizeClass
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material.icons.automirrored.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Calendar
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.foundation.lazy.itemsIndexed

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToCalculator: (CalculationHistory) -> Unit,
    onNavigateBottomNav: (String) -> Unit = {},
    activeBottomNavItem: String = "history"
) {
    val configuration = LocalConfiguration.current
    val sizeClass = when {
        configuration.screenWidthDp < 600 -> WindowWidthSizeClass.COMPACT
        configuration.screenWidthDp < 840 -> WindowWidthSizeClass.MEDIUM
        else -> WindowWidthSizeClass.EXPANDED
    }
    
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val historyItems = uiState.historyList
    
    var selectedFilter by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    
    val selectedItems = remember { mutableStateListOf<Int>() }
    val isMultiSelectMode = selectedItems.isNotEmpty()

    val filters = listOf("All", "EMI", "SIP", "FD", "RD", "GST", "Currency", "Loan")

    val groupedItems = remember(historyItems, selectedFilter, searchQuery) {
        var filtered = historyItems
        if (selectedFilter != "All") {
            filtered = filtered.filter { 
                if (selectedFilter == "Loan") {
                    it.calculatorType == "Prepayment" || it.calculatorType == "Compare" || it.calculatorType == "Eligibility"
                } else {
                    it.calculatorType.equals(selectedFilter, ignoreCase = true) 
                }
            }
        }
        if (searchQuery.isNotBlank()) {
            filtered = filtered.filter {
                (it.title ?: "").contains(searchQuery, ignoreCase = true) || it.calculatorType.contains(searchQuery, ignoreCase = true)
            }
        }
        
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfToday = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        val startOfYesterday = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_YEAR, -5)
        val startOfWeek = calendar.timeInMillis
        
        val map = mutableMapOf<String, MutableList<CalculationHistory>>()
        filtered.sortedByDescending { it.timestamp }.forEach {
            when {
                it.timestamp >= startOfToday -> map.getOrPut("Today") { mutableListOf() }.add(it)
                it.timestamp >= startOfYesterday -> map.getOrPut("Yesterday") { mutableListOf() }.add(it)
                it.timestamp >= startOfWeek -> map.getOrPut("This Week") { mutableListOf() }.add(it)
                else -> map.getOrPut("Older") { mutableListOf() }.add(it)
            }
        }
        map
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(BackgroundDark)) {
                TopAppBar(
                    title = { 
                        if (isMultiSelectMode) {
                            Text("${selectedItems.size} Selected", color = Color.White, fontWeight = FontWeight.Bold)
                        } else if (isSearchActive) {
                            val focusRequester = remember { FocusRequester() }
                            LaunchedEffect(Unit) {
                                focusRequester.requestFocus()
                            }
                            TextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = { Text("Search history...", color = TextSecondary) },
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                ),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().focusRequester(focusRequester)
                            )
                        } else {
                            Text("Calculation History", color = Color.White, fontWeight = FontWeight.Bold) 
                        }
                    },
                    actions = {
                        if (isMultiSelectMode) {
                            IconButton(onClick = { 
                                selectedItems.forEach { viewModel.deleteById(it) }
                                selectedItems.clear()
                            }) {
                                Icon(Icons.Rounded.Delete, contentDescription = "Delete Selected", tint = Color.White)
                            }
                            IconButton(onClick = { selectedItems.clear() }) {
                                Icon(Icons.Rounded.Close, contentDescription = "Cancel", tint = Color.White)
                            }
                        } else {
                            IconButton(onClick = { 
                                isSearchActive = !isSearchActive 
                                if (!isSearchActive) searchQuery = ""
                            }) {
                                Icon(if (isSearchActive) Icons.Rounded.Close else Icons.Rounded.Search, contentDescription = "Search", tint = Color.White)
                            }
                            if (historyItems.isNotEmpty()) {
                                IconButton(onClick = { viewModel.clearAll() }) {
                                    Icon(Icons.Rounded.DeleteSweep, contentDescription = "Clear All", tint = Color.White)
                                }
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark)
                )
                if (!isMultiSelectMode) {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = LoanMasterTheme.spacing.screenPadding, vertical = LoanMasterTheme.spacing.sm),
                        horizontalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.sm)
                    ) {
                        items(filters) { filter ->
                            FilterChip(
                                selected = selectedFilter == filter,
                                onClick = { selectedFilter = filter },
                                label = { Text(filter) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = AccentYellow.copy(alpha = 0.2f),
                                    selectedLabelColor = AccentYellow,
                                    containerColor = SurfaceDark,
                                    labelColor = TextSecondary
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    borderColor = if (selectedFilter == filter) AccentYellow else SurfaceDark,
                                    enabled = true,
                                    selected = selectedFilter == filter
                                ),
                                shape = RoundedCornerShape(16.dp)
                            )
                        }
                    }
                }
            }
        },
        containerColor = BackgroundDark
    ) { innerPadding ->
        if (groupedItems.isEmpty()) {
            EmptyHistoryIllustration(modifier = Modifier.fillMaxSize().padding(innerPadding))
        } else {
            LazyColumn(
                contentPadding = PaddingValues(
                    horizontal = LoanMasterTheme.spacing.screenPadding,
                    vertical = LoanMasterTheme.spacing.md
                ),
                verticalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.md),
                modifier = Modifier.fillMaxSize().padding(innerPadding)
            ) {
                groupedItems.forEach { (groupName, items) ->
                    item {
                        Text(
                            text = groupName,
                            color = TextSecondary,
                            fontSize = LoanMasterTheme.typography.label.fontSize,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = LoanMasterTheme.spacing.sm, bottom = LoanMasterTheme.spacing.xs)
                        )
                    }
                    items(items, key = { it.id }) { item ->
                        val isSelected = selectedItems.contains(item.id)
                        HistoryItemCard(
                            item = item,
                            isSelected = isSelected,
                            isMultiSelectMode = isMultiSelectMode,
                            onItemClick = { 
                                if (isMultiSelectMode) {
                                    if (isSelected) selectedItems.remove(item.id) else selectedItems.add(item.id)
                                    if (selectedItems.isEmpty()) { } // auto exit mode because derived state handles it
                                } else {
                                    onNavigateToCalculator(item) 
                                }
                            },
                            onLongClick = {
                                if (!isMultiSelectMode) {
                                    selectedItems.add(item.id)
                                }
                            },
                            onDeleteClick = { viewModel.deleteById(item.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyHistoryIllustration(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(SurfaceDark),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Article,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp),
                    tint = AccentYellow.copy(alpha = 0.8f)
                )
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = null,
                    modifier = Modifier.size(30.dp).align(Alignment.BottomEnd).padding(8.dp),
                    tint = AccentBlue
                )
            }
            Spacer(modifier = Modifier.height(LoanMasterTheme.spacing.lg))
            Text(
                text = "No calculations yet",
                color = Color.White,
                fontSize = LoanMasterTheme.typography.title.fontSize,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Your saved calculations will appear here.",
                color = TextSecondary,
                fontSize = LoanMasterTheme.typography.body.fontSize
            )
        }
    }
}

data class CardData(
    val calculatorName: String,
    val icon: ImageVector,
    val iconColor: Color,
    val dateString: String,
    val param1Label: String,
    val param1Value: String,
    val param2Label: String,
    val param2Value: String,
    val param3Label: String,
    val param3Value: String,
    val mainResultLabel: String,
    val mainResultValue: String
)

@Composable
fun extractCardData(item: CalculationHistory): CardData {
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()) }
    val dateString = dateFormat.format(Date(item.timestamp))
    
    val formatMoney = { value: String -> 
        val d = value.replace(",", "").toDoubleOrNull() ?: 0.0
        formatMoney(d)
    }

    return when(item.calculatorType) {
        "EMI" -> {
            CardData(
                calculatorName = "EMI Calculator",
                icon = Icons.Rounded.Calculate,
                iconColor = Color(0xFF2563EB),
                dateString = dateString,
                param1Label = "Principal",
                param1Value = formatMoney(item.param1 ?: "0"),
                param2Label = "Rate",
                param2Value = "${item.param2 ?: "0"}%",
                param3Label = "Tenure",
                param3Value = "${item.param3 ?: "0"} Yrs",
                mainResultLabel = "Monthly EMI",
                mainResultValue = formatMoney((item.result1 ?: 0.0).toString())
            )
        }
        "SIP" -> {
            val calc = remember { SipCalculator() }
            val res = calc.calculate(item.param1 ?: "0", item.param2 ?: "0", item.param3 ?: "0", item.param4 ?: "0")
            CardData(
                calculatorName = "SIP Calculator",
                icon = Icons.AutoMirrored.Rounded.TrendingUp,
                iconColor = Color(0xFF43A047),
                dateString = dateString,
                param1Label = "Monthly",
                param1Value = formatMoney(item.param1 ?: "0"),
                param2Label = "Rate",
                param2Value = "${item.param2 ?: "0"}%",
                param3Label = "Duration",
                param3Value = "${item.param3 ?: "0"} Yrs",
                mainResultLabel = "Final Corpus",
                mainResultValue = formatMoney(res.maturityValue.toString())
            )
        }
        "FD" -> {
            val calc = remember { FdCalculator() }
            val freq = CompoundingFrequency.values().find { it.displayName.equals(item.param4, true) } ?: CompoundingFrequency.QUARTERLY
            val res = calc.calculate(item.param1 ?: "0", item.param2 ?: "0", item.param3 ?: "0", freq)
            CardData(
                calculatorName = "FD Calculator",
                icon = Icons.Rounded.Savings,
                iconColor = Color(0xFFD81B60),
                dateString = dateString,
                param1Label = "Deposit",
                param1Value = formatMoney(item.param1 ?: "0"),
                param2Label = "Rate",
                param2Value = "${item.param2 ?: "0"}%",
                param3Label = "Tenure",
                param3Value = "${item.param3 ?: "0"} Yrs",
                mainResultLabel = "Maturity Amount",
                mainResultValue = formatMoney(res.maturityValue.toString())
            )
        }
        "RD" -> {
            val calc = remember { RdCalculator() }
            val res = calc.calculate(item.param5 ?: "Standard", item.param1 ?: "0", item.param2 ?: "0", item.param3 ?: "0", item.param4 ?: "Quarterly", "0")
            CardData(
                calculatorName = "RD Calculator",
                icon = Icons.Rounded.CalendarToday,
                iconColor = Color(0xFFFF9800),
                dateString = dateString,
                param1Label = "Monthly",
                param1Value = formatMoney(item.param1 ?: "0"),
                param2Label = "Rate",
                param2Value = "${item.param2 ?: "0"}%",
                param3Label = "Tenure",
                param3Value = "${item.param3 ?: "0"} Yrs",
                mainResultLabel = "Maturity Amount",
                mainResultValue = formatMoney(res.maturityValue.toString())
            )
        }
        "Prepayment" -> {
            val calc = remember { PrepaymentCalculator() }
            val res = calc.calculate(item.param1 ?: "0", item.param2 ?: "0", item.param3 ?: "0", item.param4 ?: "0", item.param5 ?: "Tenure", "0", "0")
            CardData(
                calculatorName = "Loan Prepayment",
                icon = Icons.Rounded.EditNote,
                iconColor = Color(0xFF5E35B1),
                dateString = dateString,
                param1Label = "Time Saved",
                param1Value = "${res.tenureReducedMonths.toInt()} mo",
                param2Label = "Extra EMI",
                param2Value = formatMoney(item.param4 ?: "0"),
                param3Label = "Strategy",
                param3Value = item.param5 ?: "Tenure",
                mainResultLabel = "Interest Saved",
                mainResultValue = formatMoney(res.interestSaved.toString())
            )
        }
        "GST" -> {
            val calc = remember { GstCalculator() }
            val mode = if (item.param1 == "ADD") GstMode.ADD else GstMode.REMOVE
            val res = calc.calculate(mode, item.param2 ?: "0", item.param3?.toDoubleOrNull() ?: 0.0, item.param4 ?: "0", true)
            CardData(
                calculatorName = "GST Calculator",
                icon = Icons.Rounded.Receipt,
                iconColor = Color(0xFFE53935),
                dateString = dateString,
                param1Label = "Mode",
                param1Value = item.param1 ?: "ADD",
                param2Label = "Amount",
                param2Value = formatMoney(item.param2 ?: "0"),
                param3Label = "GST %",
                param3Value = "${item.param3 ?: "0"}%",
                mainResultLabel = "Total",
                mainResultValue = formatMoney(res.totalAmount.toString())
            )
        }
        "Compare" -> {
            CardData(
                calculatorName = "Loan Compare",
                icon = Icons.Rounded.Balance,
                iconColor = Color(0xFF8E24AA),
                dateString = dateString,
                param1Label = "Loan 1",
                param1Value = formatMoney(item.param1 ?: "0"),
                param2Label = "Loan 2",
                param2Value = formatMoney(item.param2 ?: "0"),
                param3Label = "Difference",
                param3Value = formatMoney(item.param3 ?: "0"),
                mainResultLabel = "Better Option",
                mainResultValue = item.param4 ?: "-"
            )
        }
        "Eligibility" -> {
            CardData(
                calculatorName = "Loan Eligibility",
                icon = Icons.Rounded.PersonSearch,
                iconColor = Color(0xFF1E88E5),
                dateString = dateString,
                param1Label = "Income",
                param1Value = formatMoney(item.param1 ?: "0"),
                param2Label = "Obligations",
                param2Value = formatMoney(item.param2 ?: "0"),
                param3Label = "Tenure",
                param3Value = "${item.param3 ?: "0"} Yrs",
                mainResultLabel = "Eligible Loan",
                mainResultValue = formatMoney(item.param4 ?: "0")
            )
        }
        "Currency" -> {
            CardData(
                calculatorName = "Currency Converter",
                icon = Icons.Rounded.CurrencyExchange,
                iconColor = Color(0xFF00ACC1),
                dateString = dateString,
                param1Label = "From",
                param1Value = item.param1 ?: "-",
                param2Label = "To",
                param2Value = item.param2 ?: "-",
                param3Label = "Rate",
                param3Value = item.param3 ?: "-",
                mainResultLabel = "Converted",
                mainResultValue = item.param4 ?: "-"
            )
        }
        else -> {
            CardData(
                calculatorName = item.calculatorType,
                icon = Icons.Rounded.Calculate,
                iconColor = AccentYellow,
                dateString = dateString,
                param1Label = "-",
                param1Value = "-",
                param2Label = "-",
                param2Value = "-",
                param3Label = "-",
                param3Value = "-",
                mainResultLabel = "Result",
                mainResultValue = "-"
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HistoryItemCard(
    item: CalculationHistory,
    isSelected: Boolean = false,
    isMultiSelectMode: Boolean = false,
    onItemClick: () -> Unit,
    onLongClick: () -> Unit = {},
    onDeleteClick: () -> Unit
) {
    val cardData = extractCardData(item)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onItemClick,
                onLongClick = onLongClick
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) AccentYellow.copy(alpha = 0.1f) else SurfaceDark
        ),
        shape = RoundedCornerShape(LoanMasterTheme.spacing.md),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, AccentYellow) else null,
        elevation = CardDefaults.cardElevation(defaultElevation = LoanMasterTheme.spacing.xs)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(LoanMasterTheme.spacing.md)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(cardData.iconColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(cardData.icon, contentDescription = null, tint = cardData.iconColor, modifier = Modifier.size(24.dp))
                }
                
                Spacer(modifier = Modifier.width(LoanMasterTheme.spacing.md))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = cardData.calculatorName,
                        color = Color.White,
                        fontSize = LoanMasterTheme.typography.body.fontSize,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = cardData.dateString,
                        color = TextSecondary,
                        fontSize = LoanMasterTheme.typography.label.fontSize
                    )
                }
                
                if (!isMultiSelectMode) {
                    IconButton(onClick = onDeleteClick, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Rounded.Delete, contentDescription = "Delete", tint = TextSecondary, modifier = Modifier.size(20.dp))
                    }
                } else {
                    Icon(
                        imageVector = if (isSelected) Icons.Rounded.CheckCircle else Icons.Rounded.RadioButtonUnchecked,
                        contentDescription = "Select",
                        tint = if (isSelected) AccentYellow else TextSecondary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(LoanMasterTheme.spacing.md))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ParamRow(cardData.param1Label, cardData.param1Value)
                    ParamRow(cardData.param2Label, cardData.param2Value)
                    ParamRow(cardData.param3Label, cardData.param3Value)
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = cardData.mainResultLabel,
                        color = TextSecondary,
                        fontSize = LoanMasterTheme.typography.label.fontSize
                    )
                    Text(
                        text = cardData.mainResultValue,
                        color = AccentBlue,
                        fontSize = LoanMasterTheme.typography.title.fontSize,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}

@Composable
fun ParamRow(label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "$label: ",
            color = TextSecondary,
            fontSize = LoanMasterTheme.typography.label.fontSize
        )
        Text(
            text = value,
            color = Color.White,
            fontSize = LoanMasterTheme.typography.label.fontSize,
            fontWeight = FontWeight.Medium
        )
    }
}
