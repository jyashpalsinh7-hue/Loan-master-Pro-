package com.example

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material.icons.automirrored.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.window.core.layout.WindowWidthSizeClass as WindowWidthSizeClassCore

enum class GstMode { ADD, REMOVE }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GstCalculatorScreen(
    onNavigateBack: () -> Unit,
    historyViewModel: HistoryViewModel? = null,
    initialHistory: CalculationHistory? = null,
    onHistoryConsumed: () -> Unit = {}
) {
    val focusManager = LocalFocusManager.current
    
    val adaptiveInfo = currentWindowAdaptiveInfo()
    val isWide = adaptiveInfo.windowSizeClass.windowWidthSizeClass != WindowWidthSizeClassCore.COMPACT

    // RESPONSIVE: use rememberSaveable
    var mode by rememberSaveable { mutableStateOf(if (initialHistory?.param1 == "REMOVE") GstMode.REMOVE else GstMode.ADD) }
    var amountText by rememberSaveable { mutableStateOf(initialHistory?.param2 ?: "") }
    var selectedRate by rememberSaveable { mutableDoubleStateOf(initialHistory?.param3?.toDoubleOrNull() ?: 18.0) }
    
    LaunchedEffect(initialHistory) {
        if (initialHistory != null) {
            onHistoryConsumed()
        }
    }
    
    var showAdvanced by remember { mutableStateOf(false) }
    var cessRateText by remember { mutableStateOf("") }
    var isIntrastate by remember { mutableStateOf(true) }
    
    var currentHistoryId by remember { mutableStateOf(initialHistory?.id ?: 0) }

    val amount = amountText.toDoubleOrNull() ?: 0.0
    val actualRate = selectedRate
    val cessRate = cessRateText.toDoubleOrNull() ?: 0.0

    LaunchedEffect(mode, amountText, selectedRate) {
        kotlinx.coroutines.delay(2000)
        if (historyViewModel != null && amount > 0) {
            val history = CalculationHistory(
                id = currentHistoryId,
                calculatorType = "GST",
                title = "${if(mode == GstMode.ADD) "Add" else "Remove"} ${actualRate}% GST on ${formatMoney(amount, com.example.globalCurrencySymbol)}",
                param1 = mode.name,
                param2 = amountText,
                param3 = actualRate.toString(),
                param4 = "false",
                param5 = ""
            )
            historyViewModel.insert(history) { id ->
                currentHistoryId = id
            }
        }
    }

    // Calculations
    val baseAmount: Double
    val totalAmount: Double
    if (mode == GstMode.ADD) {
        baseAmount = amount
        totalAmount = amount * (1 + (actualRate + cessRate) / 100.0)
    } else {
        totalAmount = amount
        baseAmount = amount / (1 + (actualRate + cessRate) / 100.0)
    }

    val totalGst = baseAmount * (actualRate / 100.0)
    val totalCess = baseAmount * (cessRate / 100.0)
    val cgst = if (isIntrastate) totalGst / 2 else 0.0
    val sgst = if (isIntrastate) totalGst / 2 else 0.0
    val igst = if (!isIntrastate) totalGst else 0.0

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(BackgroundDark).statusBarsPadding()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                    Text(
                        text = "GST Calculator",
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f).padding(start = 8.dp)
                    )
                    val context = androidx.compose.ui.platform.LocalContext.current
                    IconButton(onClick = {
                        ExportUtils.exportToPdf(
                            context,
                            "GST Calculator Report",
                            listOf(
                                "Calculation Mode" to if(mode == GstMode.ADD) "Add GST" else "Remove GST",
                                "Base Amount" to formatMoney(baseAmount, com.example.globalCurrencySymbol),
                                "GST Rate" to "$actualRate%",
                                "Cess Rate" to "$cessRate%",
                                "" to "",
                                "CGST" to formatMoney(cgst, com.example.globalCurrencySymbol),
                                "SGST" to formatMoney(sgst, com.example.globalCurrencySymbol),
                                "IGST" to formatMoney(igst, com.example.globalCurrencySymbol),
                                "Total GST" to formatMoney(totalGst, com.example.globalCurrencySymbol),
                                "Total Cess" to formatMoney(totalCess, com.example.globalCurrencySymbol),
                                "Total Amount" to formatMoney(totalAmount, com.example.globalCurrencySymbol)
                            )
                        )
                    }) {
                        Icon(Icons.Rounded.PictureAsPdf, contentDescription = "Export PDF", tint = TextSecondary)
                    }
                    IconButton(onClick = { amountText = ""; cessRateText = ""; mode = GstMode.ADD }) {
                        Icon(Icons.Rounded.Refresh, contentDescription = "Reset", tint = TextSecondary)
                    }
                }
            }
        },
        containerColor = BackgroundDark,
        modifier = Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null
        ) { focusManager.clearFocus() }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Segmented Switch for Mode
                GstModeSelector(mode = mode, onModeSelected = { mode = it })

                // Main Layout (Responsive)
                if (isWide) {
                    Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                            GstHeroResultCard(mode, actualRate, cessRate, baseAmount, totalGst, totalCess, totalAmount)
                            GstBreakupSection(baseAmount, totalGst, cgst, sgst, igst, totalCess, totalAmount, isIntrastate)
                        }
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                            GstInputSection(
                                amountText = amountText,
                                mode = mode,
                                onAmountChange = { amountText = it },
                                selectedRate = selectedRate,
                                onRateSelected = { rate ->
                                    selectedRate = rate
                                },
                                showAdvanced = showAdvanced,
                                onToggleAdvanced = { showAdvanced = !showAdvanced },
                                cessRateText = cessRateText,
                                onCessRateChange = { cessRateText = it },
                                isIntrastate = isIntrastate,
                                onToggleInterstate = { isIntrastate = it }
                            )
                            GstRateQuickCompareSection(amount, actualRate, cessRate, isIntrastate)
                        }
                    }
                } else {
                    GstHeroResultCard(mode, actualRate, cessRate, baseAmount, totalGst, totalCess, totalAmount)
                    
                    GstInputSection(
                        amountText = amountText,
                        mode = mode,
                        onAmountChange = { amountText = it },
                        selectedRate = selectedRate,
                        onRateSelected = { rate ->
                            selectedRate = rate
                        },
                        showAdvanced = showAdvanced,
                        onToggleAdvanced = { showAdvanced = !showAdvanced },
                        cessRateText = cessRateText,
                        onCessRateChange = { cessRateText = it },
                        isIntrastate = isIntrastate,
                        onToggleInterstate = { isIntrastate = it }
                    )

                    GstBreakupSection(baseAmount, totalGst, cgst, sgst, igst, totalCess, totalAmount, isIntrastate)
                    
                    GstRateQuickCompareSection(amount, actualRate, cessRate, isIntrastate)
                }
                
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

@Composable
private fun GstModeSelector(mode: GstMode, onModeSelected: (GstMode) -> Unit) {
    val modes = listOf(GstMode.ADD to "Add GST (+)", GstMode.REMOVE to "Remove GST (-)")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceDark)
            .padding(4.dp)
    ) {
        modes.forEach { (m, label) ->
            val isSelected = mode == m
            val bgColor by animateColorAsState(if (isSelected) AccentBlue else Color.Transparent)
            val textColor by animateColorAsState(if (isSelected) Color.White else TextSecondary)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(bgColor)
                    .clickable { onModeSelected(m) }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    color = textColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun GstHeroResultCard(
    mode: GstMode,
    gstRate: Double,
    cessRate: Double,
    baseAmount: Double,
    totalGst: Double,
    totalCess: Double,
    totalAmount: Double
) {
    val animatedTotal by animateFloatAsState(
        targetValue = totalAmount.toFloat(),
        animationSpec = spring(stiffness = Spring.StiffnessLow)
    )
    val animatedBase by animateFloatAsState(
        targetValue = baseAmount.toFloat(),
        animationSpec = spring(stiffness = Spring.StiffnessLow)
    )
    val animatedTax by animateFloatAsState(
        targetValue = (totalGst + totalCess).toFloat(),
        animationSpec = spring(stiffness = Spring.StiffnessLow)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.linearGradient(listOf(Color(0xFF1E3A8A), Color(0xFF312E81))))
            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(20.dp))
    ) {
        // Decorative background elements
        Canvas(modifier = Modifier.matchParentSize()) {
            drawCircle(
                color = Color.White.copy(alpha = 0.05f),
                radius = size.width * 0.4f,
                center = Offset(size.width, 0f)
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.03f),
                radius = size.width * 0.2f,
                center = Offset(0f, size.height)
            )
        }

        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.White.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (mode == GstMode.ADD) "NET AMOUNT (INCLUSIVE)" else "BASE AMOUNT (EXCLUSIVE)",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "Rate: ${gstRate.toInt()}%" + if(cessRate>0) " + ${cessRate.toInt()}% Cess" else "",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                
                AutoResizedText(
                    text = formatMoney(if (mode == GstMode.ADD) animatedTotal.toDouble() else animatedBase.toDouble(), com.example.globalCurrencySymbol),
                    color = Color.White,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black.copy(alpha = 0.2f))
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = if (mode == GstMode.ADD) "Base Amount" else "Total Amount",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    AutoResizedText(
                        text = formatMoney(if (mode == GstMode.ADD) animatedBase.toDouble() else animatedTotal.toDouble(), com.example.globalCurrencySymbol),
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Box(
                    modifier = Modifier.width(1.dp).height(40.dp).background(Color.White.copy(alpha = 0.2f))
                )
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Total Tax Amount",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    AutoResizedText(
                        text = "+ " + formatMoney(animatedTax.toDouble(), com.example.globalCurrencySymbol),
                        color = AccentGreen,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun GstInputSection(
    amountText: String,
    mode: GstMode,
    onAmountChange: (String) -> Unit,
    selectedRate: Double,
    onRateSelected: (Double) -> Unit,
    showAdvanced: Boolean,
    onToggleAdvanced: () -> Unit,
    cessRateText: String,
    onCessRateChange: (String) -> Unit,
    isIntrastate: Boolean,
    onToggleInterstate: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceDark)
            .border(1.dp, CardStroke, RoundedCornerShape(16.dp))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Amount Input
        Column {
            Text(
                text = if (mode == GstMode.ADD) "Base Amount (Exclusive of GST)" else "Total Amount (Inclusive of GST)",
                color = TextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = amountText,
                onValueChange = onAmountChange,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(fontSize = 18.sp, color = TextPrimary),
                leadingIcon = {
                    Text(com.example.globalCurrencySymbol, color = TextSecondary, fontSize = 20.sp, modifier = Modifier.padding(start = 12.dp))
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentBlue,
                    unfocusedBorderColor = CardStroke,
                    focusedContainerColor = BackgroundDark,
                    unfocusedContainerColor = BackgroundDark
                ),
                shape = RoundedCornerShape(12.dp)
            )
        }

        // GST Rate Selection
        Column {
            Text("GST Rate", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(12.dp))
            
            val standardRates = listOf(0.0, 5.0, 12.0, 18.0, 28.0)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                standardRates.forEach { rate ->
                    val isSelected = selectedRate == rate
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) AccentBlue.copy(alpha = 0.15f) else BackgroundDark)
                            .border(1.dp, if (isSelected) AccentBlue.copy(alpha = 0.5f) else CardStroke, RoundedCornerShape(8.dp))
                            .clickable { onRateSelected(rate) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${rate.toInt()}%",
                            color = if (isSelected) AccentBlue else TextPrimary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        HorizontalDivider(color = CardStroke)

        // Advanced Options Toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggleAdvanced() }
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Settings, contentDescription = null, tint = AccentBlue, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Advanced Options (Cess, Interstate)", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
            Icon(
                imageVector = if (showAdvanced) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown,
                contentDescription = null,
                tint = TextSecondary
            )
        }

        AnimatedVisibility(visible = showAdvanced) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Cess Input
                Column {
                    Text("Cess Rate (Optional)", color = TextSecondary, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = cessRateText,
                        onValueChange = onCessRateChange,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("0", color = TextSecondary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        trailingIcon = { Icon(Icons.Rounded.Percent, contentDescription = null, tint = TextSecondary) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AccentBlue,
                            unfocusedBorderColor = CardStroke,
                            focusedContainerColor = BackgroundDark,
                            unfocusedContainerColor = BackgroundDark
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                // Intrastate vs Interstate Toggle
                Column {
                    Text("Transaction Type", color = TextSecondary, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(BackgroundDark)
                            .border(1.dp, CardStroke, RoundedCornerShape(12.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp))
                                .background(if (isIntrastate) AccentBlue.copy(alpha = 0.2f) else Color.Transparent)
                                .clickable { onToggleInterstate(true) }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Intrastate\n(CGST+SGST)",
                                color = if (isIntrastate) AccentBlue else TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                        Box(modifier = Modifier.width(1.dp).fillMaxHeight().background(CardStroke))
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp))
                                .background(if (!isIntrastate) AccentPurple.copy(alpha = 0.2f) else Color.Transparent)
                                .clickable { onToggleInterstate(false) }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Interstate\n(IGST)",
                                color = if (!isIntrastate) AccentPurple else TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GstBreakupSection(
    baseAmount: Double,
    totalGst: Double,
    cgst: Double,
    sgst: Double,
    igst: Double,
    totalCess: Double,
    totalAmount: Double,
    isIntrastate: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceDark)
            .border(1.dp, CardStroke, RoundedCornerShape(16.dp))
            .padding(20.dp)
    ) {
        Text("Tax Breakup", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(20.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Visual Pie/Ring Chart for taxes
            Box(
                modifier = Modifier.size(100.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val stroke = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
                    val total = if (totalAmount > 0) totalAmount.toFloat() else 1f
                    
                    val baseAngle = (baseAmount.toFloat() / total) * 360f
                    val cgstAngle = (cgst.toFloat() / total) * 360f
                    val sgstAngle = (sgst.toFloat() / total) * 360f
                    val igstAngle = (igst.toFloat() / total) * 360f
                    val cessAngle = (totalCess.toFloat() / total) * 360f
                    
                    var currentAngle = -90f
                    
                    // Base amount ring
                    drawArc(Color(0xFF3B82F6), currentAngle, baseAngle, false, style = stroke)
                    currentAngle += baseAngle
                    
                    if (isIntrastate) {
                        drawArc(Color(0xFF8B5CF6), currentAngle, cgstAngle, false, style = stroke)
                        currentAngle += cgstAngle
                        drawArc(Color(0xFF10B981), currentAngle, sgstAngle, false, style = stroke)
                        currentAngle += sgstAngle
                    } else {
                        drawArc(Color(0xFFEC4899), currentAngle, igstAngle, false, style = stroke)
                        currentAngle += igstAngle
                    }
                    
                    if (totalCess > 0) {
                        drawArc(Color(0xFFF59E0B), currentAngle, cessAngle, false, style = stroke)
                    }
                }
                Icon(Icons.Rounded.PieChartOutline, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(24.dp).alpha(0.5f))
            }
            
            Spacer(modifier = Modifier.width(24.dp))
            
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AnimatedGstBreakupItem("Base Amount", baseAmount, Color(0xFF3B82F6))
                
                if (isIntrastate) {
                    AnimatedGstBreakupItem("CGST", cgst, Color(0xFF8B5CF6))
                    AnimatedGstBreakupItem("SGST", sgst, Color(0xFF10B981))
                } else {
                    AnimatedGstBreakupItem("IGST", igst, Color(0xFFEC4899))
                }
                
                if (totalCess > 0) {
                    AnimatedGstBreakupItem("CESS", totalCess, Color(0xFFF59E0B))
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Detailed textual breakup
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(BackgroundDark)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Taxable Base", color = TextSecondary, fontSize = 14.sp)
                Text(formatMoney(baseAmount, com.example.globalCurrencySymbol), color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Total Tax ${if(totalCess>0) "(GST + CESS)" else ""}", color = TextSecondary, fontSize = 14.sp)
                Text("+ " + formatMoney(totalGst + totalCess, com.example.globalCurrencySymbol), color = AccentGreen, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
            HorizontalDivider(color = CardStroke, modifier = Modifier.padding(vertical = 4.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Net Amount", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(formatMoney(totalAmount, com.example.globalCurrencySymbol), color = AccentBlue, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}

@Composable
private fun AnimatedGstBreakupItem(label: String, value: Double, color: Color) {
    val animatedValue by animateFloatAsState(
        targetValue = value.toFloat(), 
        animationSpec = tween(durationMillis = 500)
    )
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(color))
        Spacer(modifier = Modifier.width(8.dp))
        Text(label, color = TextSecondary, fontSize = 12.sp, modifier = Modifier.weight(1f))
        AutoSizeText(formatMoney(animatedValue.toDouble(), com.example.globalCurrencySymbol), color = TextPrimary, minTextSize = 8.sp, maxTextSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.widthIn(max = 100.dp))
    }
}

@Composable
private fun GstRateQuickCompareSection(baseAmount: Double, currentRate: Double, currentCess: Double, isIntrastate: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceDark)
            .border(1.dp, CardStroke, RoundedCornerShape(16.dp))
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.AutoMirrored.Rounded.CompareArrows, contentDescription = null, tint = AccentYellow)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Quick Rate Comparison", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text("Compare how different GST rates affect this amount", color = TextSecondary, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(16.dp))

        val rates = listOf(5.0, 12.0, 18.0, 28.0)
        
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Rate", color = TextSecondary, fontSize = 12.sp, modifier = Modifier.weight(1f))
                Text("Tax", color = TextSecondary, fontSize = 12.sp, modifier = Modifier.weight(1.5f).padding(start = 16.dp))
                Text("Total", color = TextSecondary, fontSize = 12.sp, modifier = Modifier.weight(1.5f), textAlign = TextAlign.End)
            }
            
            rates.forEach { rate ->
                val isCurrent = rate == currentRate && currentCess == 0.0
                val tax = baseAmount * (rate / 100.0)
                val total = baseAmount + tax
                val bgColor = if (isCurrent) AccentYellow.copy(alpha = 0.1f) else BackgroundDark
                val borderColor = if (isCurrent) AccentYellow.copy(alpha = 0.5f) else CardStroke
                val textColor = if (isCurrent) AccentYellow else TextPrimary
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(bgColor)
                        .border(1.dp, borderColor, RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "${rate.toInt()}%", 
                        color = textColor, 
                        fontSize = 14.sp, 
                        fontWeight = if(isCurrent) FontWeight.Bold else FontWeight.Medium,
                        modifier = Modifier.weight(1f)
                    )
                    
                    Text(
                        formatMoney(tax, com.example.globalCurrencySymbol), 
                        color = if (isCurrent) AccentYellow else TextSecondary, 
                        fontSize = 13.sp,
                        modifier = Modifier.weight(1.5f).padding(start = 16.dp)
                    )
                    
                    Text(
                        formatMoney(total, com.example.globalCurrencySymbol), 
                        color = textColor, 
                        fontSize = 14.sp, 
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1.5f),
                        textAlign = TextAlign.End
                    )
                }
            }
        }
    }
}
