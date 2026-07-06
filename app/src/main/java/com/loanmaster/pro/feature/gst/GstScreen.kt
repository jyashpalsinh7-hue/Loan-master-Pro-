package com.loanmaster.pro.feature.gst

import com.loanmaster.pro.domain.model.*
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
import com.loanmaster.pro.feature.currency.*
import com.loanmaster.pro.core.navigation.*
import com.loanmaster.pro.feature.compare.*
import com.loanmaster.pro.feature.loaneligibility.*
import com.loanmaster.pro.core.responsive.*
import com.loanmaster.pro.feature.home.*
import androidx.window.core.layout.WindowWidthSizeClass


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
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

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
import kotlin.math.cos
import kotlin.math.sin
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GstScreen(
    onNavigateBack: () -> Unit,
    historyViewModel: HistoryViewModel? = null,
    initialHistory: CalculationHistory? = null,
    onHistoryConsumed: () -> Unit = {},
    viewModel: GstViewModel = viewModel()
) {
    val focusManager = LocalFocusManager.current
    
    val adaptiveInfo = currentWindowAdaptiveInfo()
    val isWide = adaptiveInfo.windowSizeClass.windowWidthSizeClass != WindowWidthSizeClass.COMPACT

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    val mode = uiState.mode
    val amountText = uiState.amountText
    val selectedRate = uiState.selectedRate
    val showAdvanced = uiState.showAdvanced
    val cessRateText = uiState.cessRateText
    val isIntrastate = uiState.isIntrastate
    val currentHistoryId = uiState.currentHistoryId
    
    val amount = amountText.toDoubleOrNull() ?: 0.0
    val actualRate = selectedRate
    val cessRate = cessRateText.toDoubleOrNull() ?: 0.0
    
    val baseAmount = uiState.baseAmount
    val totalAmount = uiState.totalAmount
    val totalGst = uiState.totalGst
    val totalCess = uiState.totalCess
    val cgst = uiState.cgst
    val sgst = uiState.sgst
    val igst = uiState.igst

    LaunchedEffect(initialHistory) {
        if (initialHistory != null) {
            viewModel.onEvent(GstEvent.InitializeFromHistory(initialHistory))
            onHistoryConsumed()
        }
    }
    
    LaunchedEffect(mode, amountText, selectedRate) {
        kotlinx.coroutines.delay(2000)
        if (historyViewModel != null && amount > 0) {
            val history = CalculationHistory(
                id = currentHistoryId,
                calculatorType = "GST",
                title = "${if(mode == GstMode.ADD) "Add" else "Remove"} ${actualRate}% GST on ${formatMoney(amount)}",
                param1 = mode.name,
                param2 = amountText,
                param3 = actualRate.toString(),
                param4 = "false",
                param5 = ""
            )
            historyViewModel.insert(history) { id ->
                viewModel.onEvent(GstEvent.HistoryIdUpdated(id))
            }
        }
    }
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(BackgroundDark).statusBarsPadding()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = LoanMasterTheme.spacing.md, vertical = LoanMasterTheme.spacing.md),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                    Text(
                        text = "GST Calculator",
                        color = TextPrimary,
                        fontSize = LoanMasterTheme.typography.title.fontSize,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f).padding(start = LoanMasterTheme.spacing.sm)
                    )
                    val context = androidx.compose.ui.platform.LocalContext.current
                    IconButton(onClick = {
                        ExportUtils.exportToPdf(
                            context,
                            "GST Calculator Report",
                            listOf(
                                "Calculation Mode" to if(mode == GstMode.ADD) "Add GST" else "Remove GST",
                                "Base Amount" to formatMoney(baseAmount),
                                "GST Rate" to "$actualRate%",
                                "Cess Rate" to "$cessRate%",
                                "" to "",
                                "CGST" to formatMoney(cgst),
                                "SGST" to formatMoney(sgst),
                                "IGST" to formatMoney(igst),
                                "Total GST" to formatMoney(totalGst),
                                "Total Cess" to formatMoney(totalCess),
                                "Total Amount" to formatMoney(totalAmount)
                            )
                        )
                    }) {
                        Icon(Icons.Rounded.PictureAsPdf, contentDescription = "Export PDF", tint = TextSecondary)
                    }
                    IconButton(onClick = { viewModel.onEvent(GstEvent.Reset) }) {
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
                    .padding(LoanMasterTheme.spacing.md),
                verticalArrangement = Arrangement.spacedBy(LoanMasterTheme.components.iconSmall)
            ) {
                // Segmented Switch for Mode
                GstModeSelector(mode = mode, onModeSelected = { viewModel.onEvent(GstEvent.ModeChanged(it)) })

                // Main Layout (Responsive)
                if (isWide) {
                    Row(horizontalArrangement = Arrangement.spacedBy(LoanMasterTheme.components.iconSmall)) {
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(LoanMasterTheme.components.iconSmall)) {
                            GstHeroResultCard(mode, actualRate, cessRate, baseAmount, totalGst, totalCess, totalAmount)
                            GstBreakupSection(baseAmount, totalGst, cgst, sgst, igst, totalCess, totalAmount, isIntrastate)
                        }
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(LoanMasterTheme.components.iconSmall)) {
                            GstInputSection(
                                amountText = amountText,
                                mode = mode,
                                onAmountChange = { viewModel.updateInputs(amount = it) },
                                selectedRate = selectedRate,
                                onRateSelected = { rate ->
                                    viewModel.updateInputs(rate = rate)
                                },
                                showAdvanced = showAdvanced,
                                onToggleAdvanced = { viewModel.onEvent(GstEvent.ShowAdvancedToggled(!showAdvanced)) },
                                cessRateText = cessRateText,
                                onCessRateChange = { viewModel.onEvent(GstEvent.CessRateChanged(it)) },
                                isIntrastate = isIntrastate,
                                onToggleInterstate = { viewModel.onEvent(GstEvent.IntrastateToggled(it)) }
                            )
                            GstRateQuickCompareSection(amount, actualRate, cessRate, isIntrastate)
                        }
                    }
                } else {
                    GstHeroResultCard(mode, actualRate, cessRate, baseAmount, totalGst, totalCess, totalAmount)
                    
                    GstInputSection(
                        amountText = amountText,
                        mode = mode,
                        onAmountChange = { viewModel.updateInputs(amount = it) },
                        selectedRate = selectedRate,
                        onRateSelected = { rate ->
                            viewModel.updateInputs(rate = rate)
                        },
                        showAdvanced = showAdvanced,
                        onToggleAdvanced = { viewModel.onEvent(GstEvent.ShowAdvancedToggled(!showAdvanced)) },
                        cessRateText = cessRateText,
                        onCessRateChange = { viewModel.onEvent(GstEvent.CessRateChanged(it)) },
                        isIntrastate = isIntrastate,
                        onToggleInterstate = { viewModel.onEvent(GstEvent.IntrastateToggled(it)) }
                    )

                    GstBreakupSection(baseAmount, totalGst, cgst, sgst, igst, totalCess, totalAmount, isIntrastate)
                    
                    GstRateQuickCompareSection(amount, actualRate, cessRate, isIntrastate)
                }
                
                Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.xl))
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
            .clip(RoundedCornerShape(LoanMasterTheme.spacing.md))
            .background(SurfaceDark)
            .padding(LoanMasterTheme.spacing.xs)
    ) {
        modes.forEach { (m, label) ->
            val isSelected = mode == m
            val bgColor by animateColorAsState(if (isSelected) AccentBlue else Color.Transparent)
            val textColor by animateColorAsState(if (isSelected) Color.White else TextSecondary)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(LoanMasterTheme.spacing.gridGutter))
                    .background(bgColor)
                    .clickable { onModeSelected(m) }
                    .padding(vertical = LoanMasterTheme.spacing.md),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    color = textColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = LoanMasterTheme.typography.body.fontSize
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
            .clip(RoundedCornerShape(LoanMasterTheme.components.iconSmall))
            .background(Brush.linearGradient(listOf(Color(0xFF1E3A8A), Color(0xFF312E81))))
            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(LoanMasterTheme.components.iconSmall))
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
            modifier = Modifier.padding(LoanMasterTheme.spacing.lg),
            verticalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.lg)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(LoanMasterTheme.spacing.sm))
                            .background(Color.White.copy(alpha = 0.2f))
                            .padding(horizontal = LoanMasterTheme.spacing.sm, vertical = LoanMasterTheme.spacing.xs)
                    ) {
                        Text(
                            text = if (mode == GstMode.ADD) "NET AMOUNT (INCLUSIVE)" else "BASE AMOUNT (EXCLUSIVE)",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = LoanMasterTheme.typography.label.fontSize,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "Rate: ${gstRate.toInt()}%" + if(cessRate>0) " + ${cessRate.toInt()}% Cess" else "",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = LoanMasterTheme.typography.label.fontSize
                    )
                }
                Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.md))
                
                AutoResizedText(
                    text = formatMoney(if (mode == GstMode.ADD) animatedTotal.toDouble() else animatedBase.toDouble()),
                    color = Color.White,
                    fontSize = LoanMasterTheme.typography.display.fontSize,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(LoanMasterTheme.spacing.md))
                    .background(Color.Black.copy(alpha = 0.2f))
                    .padding(LoanMasterTheme.spacing.md),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = if (mode == GstMode.ADD) "Base Amount" else "Total Amount",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = LoanMasterTheme.typography.label.fontSize
                    )
                    Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.xs))
                    AutoResizedText(
                        text = formatMoney(if (mode == GstMode.ADD) animatedBase.toDouble() else animatedTotal.toDouble()),
                        color = Color.White,
                        fontSize = LoanMasterTheme.typography.title.fontSize,
                        fontWeight = FontWeight.Bold
                    )
                }
                Box(
                    modifier = Modifier.widthIn(min = 1.dp).heightIn(min = LoanMasterTheme.spacing.xl).background(Color.White.copy(alpha = 0.2f))
                )
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Total Tax Amount",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = LoanMasterTheme.typography.label.fontSize
                    )
                    Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.xs))
                    AutoResizedText(
                        text = "+ " + formatMoney(animatedTax.toDouble()),
                        color = AccentGreen,
                        fontSize = LoanMasterTheme.typography.title.fontSize,
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
            .clip(RoundedCornerShape(LoanMasterTheme.spacing.md))
            .background(SurfaceDark)
            .border(1.dp, CardStroke, RoundedCornerShape(LoanMasterTheme.spacing.md))
            .padding(LoanMasterTheme.components.iconSmall),
        verticalArrangement = Arrangement.spacedBy(LoanMasterTheme.components.iconSmall)
    ) {
        // Amount Input
        Column {
            Text(
                text = if (mode == GstMode.ADD) "Base Amount (Exclusive of GST)" else "Total Amount (Inclusive of GST)",
                color = TextSecondary,
                fontSize = LoanMasterTheme.typography.label.fontSize,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.sm))
            OutlinedTextField(
                value = amountText,
                onValueChange = onAmountChange,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(fontSize = LoanMasterTheme.typography.title.fontSize, color = TextPrimary),
                leadingIcon = {
                    Text("₹", color = TextSecondary, fontSize = LoanMasterTheme.typography.title.fontSize, modifier = Modifier.padding(start = LoanMasterTheme.spacing.md))
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentBlue,
                    unfocusedBorderColor = CardStroke,
                    focusedContainerColor = BackgroundDark,
                    unfocusedContainerColor = BackgroundDark
                ),
                shape = RoundedCornerShape(LoanMasterTheme.spacing.md)
            )
        }

        // GST Rate Selection
        Column {
            Text("GST Rate", color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.md))
            
            val standardRates = listOf(0.0, 5.0, 12.0, 18.0, 28.0)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.sm)
            ) {
                standardRates.forEach { rate ->
                    val isSelected = selectedRate == rate
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(LoanMasterTheme.spacing.sm))
                            .background(if (isSelected) AccentBlue.copy(alpha = 0.15f) else BackgroundDark)
                            .border(1.dp, if (isSelected) AccentBlue.copy(alpha = 0.5f) else CardStroke, RoundedCornerShape(LoanMasterTheme.spacing.sm))
                            .clickable { onRateSelected(rate) }
                            .padding(vertical = LoanMasterTheme.spacing.gridGutter),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${rate.toInt()}%",
                            color = if (isSelected) AccentBlue else TextPrimary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = LoanMasterTheme.typography.body.fontSize
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
                .padding(vertical = LoanMasterTheme.spacing.sm),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Settings, contentDescription = null, tint = AccentBlue, modifier = Modifier.size(LoanMasterTheme.components.iconSmall))
                Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.sm))
                Text("Advanced Options (Cess, Interstate)", color = TextPrimary, fontSize = LoanMasterTheme.typography.body.fontSize, fontWeight = FontWeight.Bold)
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
                verticalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.md)
            ) {
                // Cess Input
                Column {
                    Text("Cess Rate (Optional)", color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize)
                    Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.sm))
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
                        shape = RoundedCornerShape(LoanMasterTheme.spacing.md)
                    )
                }

                // Intrastate vs Interstate Toggle
                Column {
                    Text("Transaction Type", color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize)
                    Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.sm))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(LoanMasterTheme.spacing.md))
                            .background(BackgroundDark)
                            .border(1.dp, CardStroke, RoundedCornerShape(LoanMasterTheme.spacing.md))
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(topStart = LoanMasterTheme.spacing.md, bottomStart = LoanMasterTheme.spacing.md))
                                .background(if (isIntrastate) AccentBlue.copy(alpha = 0.2f) else Color.Transparent)
                                .clickable { onToggleInterstate(true) }
                                .padding(vertical = LoanMasterTheme.spacing.md),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Intrastate\n(CGST+SGST)",
                                color = if (isIntrastate) AccentBlue else TextSecondary,
                                fontSize = LoanMasterTheme.typography.label.fontSize,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                        Box(modifier = Modifier.widthIn(min = 1.dp).fillMaxHeight().background(CardStroke))
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(topEnd = LoanMasterTheme.spacing.md, bottomEnd = LoanMasterTheme.spacing.md))
                                .background(if (!isIntrastate) AccentPurple.copy(alpha = 0.2f) else Color.Transparent)
                                .clickable { onToggleInterstate(false) }
                                .padding(vertical = LoanMasterTheme.spacing.md),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Interstate\n(IGST)",
                                color = if (!isIntrastate) AccentPurple else TextSecondary,
                                fontSize = LoanMasterTheme.typography.label.fontSize,
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
            .clip(RoundedCornerShape(LoanMasterTheme.spacing.md))
            .background(SurfaceDark)
            .border(1.dp, CardStroke, RoundedCornerShape(LoanMasterTheme.spacing.md))
            .padding(LoanMasterTheme.components.iconSmall)
    ) {
        Text("Tax Breakup", color = TextPrimary, fontSize = LoanMasterTheme.typography.body.fontSize, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.components.iconSmall))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Visual Pie/Ring Chart for taxes
            Box(
                modifier = Modifier.size(LoanMasterTheme.components.calculatorCardHeight),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val stroke = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
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
                Icon(Icons.Rounded.PieChartOutline, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(LoanMasterTheme.spacing.lg).alpha(0.5f))
            }
            
            Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.lg))
            
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.md)
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
        
        Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.lg))
        
        // Detailed textual breakup
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(LoanMasterTheme.spacing.md))
                .background(BackgroundDark)
                .padding(LoanMasterTheme.spacing.md),
            verticalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.gridGutter)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Taxable Base", color = TextSecondary, fontSize = LoanMasterTheme.typography.body.fontSize)
                Text(formatMoney(baseAmount), color = TextPrimary, fontSize = LoanMasterTheme.typography.body.fontSize, fontWeight = FontWeight.Bold)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Total Tax ${if(totalCess>0) "(GST + CESS)" else ""}", color = TextSecondary, fontSize = LoanMasterTheme.typography.body.fontSize)
                Text("+ " + formatMoney(totalGst + totalCess), color = AccentGreen, fontSize = LoanMasterTheme.typography.body.fontSize, fontWeight = FontWeight.Bold)
            }
            HorizontalDivider(color = CardStroke, modifier = Modifier.padding(vertical = LoanMasterTheme.spacing.xs))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Net Amount", color = TextPrimary, fontSize = LoanMasterTheme.typography.body.fontSize, fontWeight = FontWeight.Bold)
                Text(formatMoney(totalAmount), color = AccentBlue, fontSize = LoanMasterTheme.typography.body.fontSize, fontWeight = FontWeight.ExtraBold)
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
        Box(modifier = Modifier.size(LoanMasterTheme.spacing.gridGutter).clip(CircleShape).background(color))
        Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.sm))
        Text(label, color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize, modifier = Modifier.weight(1f))
        AutoSizeText(formatMoney(animatedValue.toDouble()), color = TextPrimary, minTextSize = 8.sp, maxTextSize = LoanMasterTheme.typography.label.fontSize, fontWeight = FontWeight.Bold, modifier = Modifier.widthIn(max = LoanMasterTheme.components.calculatorCardHeight))
    }
}

@Composable
private fun GstRateQuickCompareSection(baseAmount: Double, currentRate: Double, currentCess: Double, isIntrastate: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(LoanMasterTheme.spacing.md))
            .background(SurfaceDark)
            .border(1.dp, CardStroke, RoundedCornerShape(LoanMasterTheme.spacing.md))
            .padding(LoanMasterTheme.components.iconSmall)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.AutoMirrored.Rounded.CompareArrows, contentDescription = null, tint = AccentYellow)
            Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.sm))
            Text("Quick Rate Comparison", color = TextPrimary, fontSize = LoanMasterTheme.typography.body.fontSize, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.sm))
        Text("Compare how different GST rates affect this amount", color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize)
        Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.md))

        val rates = listOf(5.0, 12.0, 18.0, 28.0)
        
        Column(verticalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.sm)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = LoanMasterTheme.spacing.sm, vertical = LoanMasterTheme.spacing.xs),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Rate", color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize, modifier = Modifier.weight(1f))
                Text("Tax", color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize, modifier = Modifier.weight(1.5f).padding(start = LoanMasterTheme.spacing.md))
                Text("Total", color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize, modifier = Modifier.weight(1.5f), textAlign = TextAlign.End)
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
                        .clip(RoundedCornerShape(LoanMasterTheme.spacing.sm))
                        .background(bgColor)
                        .border(1.dp, borderColor, RoundedCornerShape(LoanMasterTheme.spacing.sm))
                        .padding(horizontal = LoanMasterTheme.spacing.md, vertical = LoanMasterTheme.spacing.gridGutter),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "${rate.toInt()}%", 
                        color = textColor, 
                        fontSize = LoanMasterTheme.typography.body.fontSize, 
                        fontWeight = if(isCurrent) FontWeight.Bold else FontWeight.Medium,
                        modifier = Modifier.weight(1f)
                    )
                    
                    Text(
                        formatMoney(tax), 
                        color = if (isCurrent) AccentYellow else TextSecondary, 
                        fontSize = LoanMasterTheme.typography.label.fontSize,
                        modifier = Modifier.weight(1.5f).padding(start = LoanMasterTheme.spacing.md)
                    )
                    
                    Text(
                        formatMoney(total), 
                        color = textColor, 
                        fontSize = LoanMasterTheme.typography.body.fontSize, 
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1.5f),
                        textAlign = TextAlign.End
                    )
                }
            }
        }
    }
}
