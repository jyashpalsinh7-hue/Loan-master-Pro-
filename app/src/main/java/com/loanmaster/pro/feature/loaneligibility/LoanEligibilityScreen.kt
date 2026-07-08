package com.loanmaster.pro.feature.loaneligibility

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

import com.loanmaster.pro.domain.model.*
import com.loanmaster.pro.core.theme.*
import com.loanmaster.pro.core.utils.*
import com.loanmaster.pro.core.formatter.*
import com.loanmaster.pro.core.responsive.*
import com.loanmaster.pro.core.ui.AutoResizedText
import com.loanmaster.pro.domain.calculator.LoanEligibilityCalculator

val loanProfiles = listOf(
    LoanProfile("Home Loan", 0.60, "8.5", "20"),
    LoanProfile("Personal Loan", 0.50, "12.0", "5"),
    LoanProfile("Car Loan", 0.55, "9.5", "7"),
    LoanProfile("Education Loan", 0.40, "10.5", "10")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanEligibilityScreen(onNavigateBack: () -> Unit = {}, viewModel: LoanEligibilityViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val bgColor = BackgroundDark
    val surfaceColor = SurfaceDark
    val brightBlue = AccentBlue
    val neonGreen = AccentGreen
    val dangerRed = Color(0xFFF44336) 
    val warningYellow = Color(0xFFFBBF24)
    val textColor = TextPrimary
    val textSecondary = TextSecondary

    val monthlyIncome = uiState.monthlyIncomeText
    val existingEMIs = uiState.existingEMIsText
    val isCoBorrowerEnabled = uiState.isCoBorrowerEnabled
    val coBorrowerIncome = uiState.coBorrowerIncomeText
    val coBorrowerEMIs = uiState.coBorrowerEMIsText
    val selectedLoanProfile = uiState.selectedLoanProfile
    val tenureYears = uiState.tenureYearsText
    val interestRate = uiState.interestRateText
    val isSalaried = uiState.isSalaried
    val creditScoreRange = uiState.creditScoreRange
    
    val profile = loanProfiles.find { it.name == selectedLoanProfile } ?: loanProfiles[0]
    
    val totalIncome = uiState.totalIncome
    val totalExistingEmi = uiState.totalExistingEmi
    val foirLimit = uiState.foirLimit
    val eligibleLoanAmount = uiState.eligibleLoanAmount
    val currentFoir = uiState.currentFoir

    var isResultVisible by rememberSaveable { mutableStateOf(false) }
    
    val calculator = remember { LoanEligibilityCalculator() }

    Scaffold(
        containerColor = bgColor
    ) { paddingValues ->
        ResponsiveScreenWrapper(
            modifier = Modifier
                .fillMaxSize()
                .background(bgColor)
                .padding(paddingValues),
            showDiagnostics = false
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = LoanMasterTheme.spacing.md, vertical = LoanMasterTheme.spacing.md)
                    .imePadding()
                    .safeDrawingPadding(),
                verticalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.sm),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Text(
                    text = "Loan Eligibility",
                    color = textColor,
                    style = LoanMasterTheme.typography.title,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // 1. Employment Type
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(LoanMasterTheme.spacing.sm))
                        .border(1.dp, surfaceColor, RoundedCornerShape(LoanMasterTheme.spacing.sm))
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(if (isSalaried) brightBlue.copy(alpha = 0.2f) else Color.Transparent)
                            .clickable { viewModel.updateInputs(isSalaried = true) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Salaried",
                            color = if (isSalaried) brightBlue else textSecondary,
                            style = LoanMasterTheme.typography.body,
                            fontWeight = if (isSalaried) FontWeight.Bold else FontWeight.Normal,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(if (!isSalaried) brightBlue.copy(alpha = 0.2f) else Color.Transparent)
                            .clickable { viewModel.updateInputs(isSalaried = false) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Self-Employed",
                            color = if (!isSalaried) brightBlue else textSecondary,
                            style = LoanMasterTheme.typography.body,
                            fontWeight = if (!isSalaried) FontWeight.Bold else FontWeight.Normal,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // 2. Primary Income & EMI
                AdaptiveRowCol(
                    modifier = Modifier.fillMaxWidth(),
                    content1 = { mod ->
                        LoanInputField(
                            value = monthlyIncome,
                            onValueChange = { viewModel.updateInputs(income = it) },
                            label = "Monthly Income",
                            icon = Icons.Rounded.AccountBalanceWallet,
                            modifier = mod
                        )
                    },
                    content2 = { mod ->
                        LoanInputField(
                            value = existingEMIs,
                            onValueChange = { viewModel.updateInputs(emi = it) },
                            label = "Existing EMIs",
                            icon = Icons.Rounded.CreditCard,
                            modifier = mod
                        )
                    }
                )

                // 3. Co-Borrower Switch
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(LoanMasterTheme.spacing.sm))
                        .background(surfaceColor.copy(alpha = 0.3f))
                        .clickable { viewModel.updateInputs(isCoBorrowerEnabled = !isCoBorrowerEnabled) }
                        .padding(horizontal = LoanMasterTheme.spacing.md, vertical = LoanMasterTheme.spacing.sm)
                ) {
                    Icon(Icons.Rounded.GroupAdd, contentDescription = null, tint = brightBlue, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(LoanMasterTheme.spacing.sm))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Add Co-Borrower",
                            color = textColor,
                            style = LoanMasterTheme.typography.body,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Switch(
                        checked = isCoBorrowerEnabled,
                        onCheckedChange = { viewModel.updateInputs(isCoBorrowerEnabled = it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = brightBlue)
                    )
                }

                // 4. Co-Borrower Inputs
                AnimatedVisibility(
                    visible = isCoBorrowerEnabled,
                    enter = expandVertically(animationSpec = tween(250)) + fadeIn(animationSpec = tween(250)),
                    exit = shrinkVertically(animationSpec = tween(250)) + fadeOut(animationSpec = tween(250))
                ) {
                    AdaptiveRowCol(
                        modifier = Modifier.fillMaxWidth(),
                        content1 = { mod ->
                            LoanInputField(
                                value = coBorrowerIncome,
                                onValueChange = { viewModel.updateInputs(coIncome = it) },
                                label = "Co-Borrower Income",
                                icon = Icons.Rounded.Group,
                                modifier = mod
                            )
                        },
                        content2 = { mod ->
                            LoanInputField(
                                value = coBorrowerEMIs,
                                onValueChange = { viewModel.updateInputs(coEmi = it) },
                                label = "Co-Borrower EMIs",
                                icon = Icons.Rounded.CreditCard,
                                modifier = mod
                            )
                        }
                    )
                }

                // 5. Loan Details
                var loanDropdownExpanded by remember { mutableStateOf(false) }
                AdaptiveRowCol(
                    modifier = Modifier.fillMaxWidth(),
                    content1 = { mod ->
                        ExposedDropdownMenuBox(
                            expanded = loanDropdownExpanded,
                            onExpandedChange = { loanDropdownExpanded = it },
                            modifier = mod
                        ) {
                            OutlinedTextField(
                                value = selectedLoanProfile,
                                onValueChange = {},
                                label = { Text("Loan Type", maxLines = 1, fontSize = 12.sp) },
                                leadingIcon = { Icon(Icons.Rounded.HomeWork, contentDescription = null, tint = brightBlue, modifier = Modifier.size(20.dp)) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = loanDropdownExpanded) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(type = MenuAnchorType.PrimaryEditable, enabled = true),
                                readOnly = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = brightBlue,
                                    unfocusedBorderColor = surfaceColor,
                                    focusedLabelColor = brightBlue,
                                    unfocusedLabelColor = textSecondary,
                                    focusedTextColor = textColor,
                                    unfocusedTextColor = textColor,
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent
                                ),
                                shape = RoundedCornerShape(LoanMasterTheme.spacing.sm),
                                singleLine = true,
                                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
                            )
                            ExposedDropdownMenu(
                                expanded = loanDropdownExpanded,
                                onDismissRequest = { loanDropdownExpanded = false },
                                containerColor = surfaceColor
                            ) {
                                loanProfiles.forEach { p ->
                                    DropdownMenuItem(
                                        text = { Text(p.name, color = textColor) },
                                        onClick = {
                                            viewModel.updateInputs(profile = p.name, defaultTenure = p.defaultTenure, defaultRate = p.defaultRate)
                                            loanDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    },
                    content2 = { mod ->
                        LoanInputField(
                            value = tenureYears,
                            onValueChange = { viewModel.updateInputs(tenure = it) },
                            label = "Tenure (Yrs)",
                            icon = Icons.Rounded.Event,
                            modifier = mod
                        )
                    }
                )
                
                LoanInputField(
                    value = interestRate,
                    onValueChange = { viewModel.updateInputs(rate = it) },
                    label = "Interest Rate (%)",
                    icon = Icons.Rounded.Percent,
                    modifier = Modifier.fillMaxWidth()
                )

                // Credit Score Filter Chips
                Column(modifier = Modifier.fillMaxWidth().padding(top = LoanMasterTheme.spacing.xs)) {
                    Text(
                        "Credit Score",
                        color = textSecondary,
                        style = LoanMasterTheme.typography.label,
                        modifier = Modifier.padding(bottom = LoanMasterTheme.spacing.xs),
                        maxLines = 1
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.xs)
                    ) {
                        listOf(
                            "Excellent" to "750+",
                            "Good" to "650-749",
                            "Fair" to "<650"
                        ).forEach { (title, range) ->
                            val isSelected = creditScoreRange == title
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.updateInputs(creditScoreRange = title, defaultRate = profile.defaultRate) },
                                label = {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                                        Text(title, fontWeight = FontWeight.Bold, maxLines = 1)
                                        Text(range, fontSize = 10.sp, maxLines = 1, color = if (isSelected) Color.White.copy(alpha = 0.9f) else textSecondary)
                                    }
                                },
                                modifier = Modifier.weight(1f).height(56.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = brightBlue,
                                    selectedLabelColor = Color.White,
                                    containerColor = surfaceColor.copy(alpha = 0.3f),
                                    labelColor = textColor
                                ),
                                shape = RoundedCornerShape(LoanMasterTheme.spacing.sm),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    borderColor = surfaceColor,
                                    selectedBorderColor = brightBlue
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(LoanMasterTheme.spacing.sm))

                // Primary Action Button
                Button(
                    onClick = { isResultVisible = true },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = brightBlue, contentColor = Color.White),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Calculate Eligibility", style = LoanMasterTheme.typography.body, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(LoanMasterTheme.spacing.sm))

                // Empty State or Results
                AnimatedVisibility(
                    visible = !isResultVisible,
                    enter = fadeIn(animationSpec = tween(250)),
                    exit = fadeOut(animationSpec = tween(250))
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = surfaceColor.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, surfaceColor),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(LoanMasterTheme.spacing.lg).fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Rounded.Analytics, contentDescription = null, tint = brightBlue, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(LoanMasterTheme.spacing.sm))
                            Text("Loan Eligibility", color = textColor, style = LoanMasterTheme.typography.title, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Enter your monthly income and loan details.", color = textSecondary, style = LoanMasterTheme.typography.label, textAlign = TextAlign.Center)
                        }
                    }
                }

                AnimatedVisibility(
                    visible = isResultVisible,
                    enter = expandVertically(animationSpec = tween(250)) + fadeIn(animationSpec = tween(250)),
                    exit = shrinkVertically(animationSpec = tween(250)) + fadeOut(animationSpec = tween(250))
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.sm)) {
                        // Approval Card
                        Card(
                            colors = CardDefaults.cardColors(containerColor = surfaceColor.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, surfaceColor),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val targetApprovalProb = (1.0f - (currentFoir.toFloat() / (foirLimit.toFloat().coerceAtLeast(0.01f)))).coerceIn(0f, 1f)
                            val approvalProb by animateFloatAsState(targetValue = if (isResultVisible) targetApprovalProb else 0f, animationSpec = tween(1000), label = "probAnim")
                            
                            val probColor = if (targetApprovalProb > 0.6) neonGreen else dangerRed
                            val grade = uiState.verdictGrade.ifEmpty { if (targetApprovalProb > 0.6) "A+" else "C" }
                            val conf = if (targetApprovalProb > 0.8) "Very High" else if (targetApprovalProb > 0.6) "High" else "Low"
                            
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(LoanMasterTheme.spacing.md),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(64.dp)) {
                                    CircularProgressIndicator(
                                        progress = { approvalProb },
                                        modifier = Modifier.size(64.dp),
                                        color = probColor,
                                        trackColor = bgColor,
                                        strokeWidth = 6.dp,
                                        strokeCap = StrokeCap.Round
                                    )
                                    Text(
                                        "${(approvalProb * 100).toInt()}%",
                                        color = textColor,
                                        style = LoanMasterTheme.typography.body,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1
                                    )
                                }
                                Spacer(modifier = Modifier.width(LoanMasterTheme.spacing.md))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("FOIR", color = textSecondary, style = LoanMasterTheme.typography.label)
                                        Text("${(currentFoir * 100).toInt()}%", color = textColor, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Bank Confidence", color = textSecondary, style = LoanMasterTheme.typography.label)
                                        Text(conf, color = probColor, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Grade", color = textSecondary, style = LoanMasterTheme.typography.label)
                                        Text(grade, color = probColor, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        // Estimated Loan Amount Card
                        Card(
                            colors = CardDefaults.cardColors(containerColor = surfaceColor.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, surfaceColor),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(LoanMasterTheme.spacing.md)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Rounded.AccountBalance, contentDescription = null, tint = brightBlue, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(LoanMasterTheme.spacing.xs))
                                    Text(
                                        "Eligible Loan Amount",
                                        color = textColor,
                                        style = LoanMasterTheme.typography.body,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                Spacer(modifier = Modifier.height(LoanMasterTheme.spacing.sm))
                                
                                AutoResizedText(
                                    text = formatMoney(eligibleLoanAmount),
                                    color = neonGreen,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(LoanMasterTheme.spacing.sm))
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Total Income", color = textSecondary, style = LoanMasterTheme.typography.label, maxLines = 1)
                                        Text(formatMoney(totalIncome), color = textColor, style = LoanMasterTheme.typography.body, fontWeight = FontWeight.Bold, maxLines = 1)
                                    }
                                    Spacer(modifier = Modifier.width(LoanMasterTheme.spacing.sm))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Total EMIs", color = textSecondary, style = LoanMasterTheme.typography.label, maxLines = 1)
                                        Text(formatMoney(totalExistingEmi), color = textColor, style = LoanMasterTheme.typography.body, fontWeight = FontWeight.Bold, maxLines = 1)
                                    }
                                }
                            }
                        }

                        // What-If Scenarios
                        Text(
                            "What if I...",
                            color = textColor,
                            style = LoanMasterTheme.typography.title,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth().padding(top = LoanMasterTheme.spacing.sm, bottom = 4.dp),
                            maxLines = 1
                        )
                        
                        val ifIncomeResult = remember(monthlyIncome, existingEMIs, isCoBorrowerEnabled, coBorrowerIncome, coBorrowerEMIs, tenureYears, interestRate, isSalaried, creditScoreRange, selectedLoanProfile) {
                            calculator.calculate(
                                profileName = selectedLoanProfile,
                                incomeStr = ((monthlyIncome.toDoubleOrNull() ?: 0.0) + 50000.0).toString(),
                                emiStr = existingEMIs,
                                isCoBorrower = isCoBorrowerEnabled,
                                coIncomeStr = coBorrowerIncome,
                                coEmiStr = coBorrowerEMIs,
                                tenureStr = tenureYears,
                                rateStr = interestRate,
                                isSal = isSalaried,
                                creditScore = creditScoreRange
                            ).eligibleLoanAmount
                        }
                        
                        val ifEmiResult = remember(monthlyIncome, existingEMIs, isCoBorrowerEnabled, coBorrowerIncome, coBorrowerEMIs, tenureYears, interestRate, isSalaried, creditScoreRange, selectedLoanProfile) {
                            calculator.calculate(
                                profileName = selectedLoanProfile,
                                incomeStr = monthlyIncome,
                                emiStr = maxOf(0.0, (existingEMIs.toDoubleOrNull() ?: 0.0) - 10000.0).toString(),
                                isCoBorrower = isCoBorrowerEnabled,
                                coIncomeStr = coBorrowerIncome,
                                coEmiStr = coBorrowerEMIs,
                                tenureStr = tenureYears,
                                rateStr = interestRate,
                                isSal = isSalaried,
                                creditScore = creditScoreRange
                            ).eligibleLoanAmount
                        }
                        
                        val formatCompact = { amount: Double ->
                            if (amount >= 10000000) "₹${String.format("%.1f", amount / 10000000)}Cr"
                            else if (amount >= 100000) "₹${String.format("%.1f", amount / 100000)}L"
                            else if (amount >= 1000) "₹${String.format("%.0f", amount / 1000)}K"
                            else formatMoney(amount)
                        }

                        AdaptiveRowCol(
                            modifier = Modifier.fillMaxWidth(),
                            content1 = { mod ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = surfaceColor.copy(alpha = 0.5f)),
                                    shape = RoundedCornerShape(12.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, surfaceColor),
                                    modifier = mod
                                ) {
                                    Column(modifier = Modifier.padding(LoanMasterTheme.spacing.sm).fillMaxWidth()) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.AutoMirrored.Rounded.TrendingUp, contentDescription = null, tint = brightBlue, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("+₹50K Income", color = textColor, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        val diff = ifIncomeResult - eligibleLoanAmount
                                        Text("Eligible Loan", color = textSecondary, fontSize = 10.sp)
                                        Row(verticalAlignment = Alignment.Bottom) {
                                            Text("${formatCompact(eligibleLoanAmount)} → ", color = textColor, fontSize = 14.sp)
                                            Text(formatCompact(ifIncomeResult), color = neonGreen, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        }
                                        Text("+${formatCompact(diff)}", color = neonGreen, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            },
                            content2 = { mod ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = surfaceColor.copy(alpha = 0.5f)),
                                    shape = RoundedCornerShape(12.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, surfaceColor),
                                    modifier = mod
                                ) {
                                    Column(modifier = Modifier.padding(LoanMasterTheme.spacing.sm).fillMaxWidth()) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.AutoMirrored.Rounded.TrendingDown, contentDescription = null, tint = warningYellow, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("-₹10K EMI", color = textColor, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        val diff = ifEmiResult - eligibleLoanAmount
                                        Text("Loan Capacity", color = textSecondary, fontSize = 10.sp)
                                        Text("+${formatCompact(diff)}", color = neonGreen, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text("from current", color = textSecondary, fontSize = 10.sp)
                                    }
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(LoanMasterTheme.spacing.xs))
                        
                        // Bottom Action Buttons
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.sm)) {
                            ActionButton(
                                text = "Detailed Report",
                                icon = Icons.Rounded.Description,
                                color = Color.Transparent,
                                contentColor = brightBlue,
                                isOutlined = true,
                                modifier = Modifier.weight(1f),
                                onClick = {}
                            )
                            ActionButton(
                                text = "Compare Banks",
                                icon = Icons.Rounded.AccountBalance,
                                color = Color(0xFF9C27B0),
                                contentColor = textColor,
                                modifier = Modifier.weight(1f),
                                onClick = {}
                            )
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.sm)) {
                            ActionButton(
                                text = "Save Calc",
                                icon = Icons.Rounded.Bookmark,
                                color = Color(0xFF2E7D32),
                                contentColor = textColor,
                                modifier = Modifier.weight(1f),
                                onClick = {}
                            )
                            val context = LocalContext.current
                            ActionButton(
                                text = "Export PDF",
                                icon = Icons.Rounded.PictureAsPdf,
                                color = Color(0xFFE65100),
                                contentColor = textColor,
                                modifier = Modifier.weight(1f),
                                onClick = {}
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(LoanMasterTheme.spacing.sm))
                    }
                }
            }
        }
    }
}

@Composable
fun LoanInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, maxLines = 1, fontSize = 12.sp, overflow = TextOverflow.Ellipsis) },
        leadingIcon = { Icon(icon, contentDescription = null, tint = AccentBlue, modifier = Modifier.size(20.dp)) },
        modifier = modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AccentBlue,
            unfocusedBorderColor = SurfaceDark,
            focusedLabelColor = AccentBlue,
            unfocusedLabelColor = TextSecondary,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent
        ),
        shape = RoundedCornerShape(LoanMasterTheme.spacing.sm),
        singleLine = true,
        textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
    )
}

@Composable
fun ActionButton(
    text: String,
    icon: ImageVector,
    color: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    isOutlined: Boolean = false,
    onClick: () -> Unit
) {
    if (isOutlined) {
        OutlinedButton(
            onClick = onClick,
            modifier = modifier.height(56.dp).fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = contentColor),
            border = androidx.compose.foundation.BorderStroke(1.dp, contentColor),
            shape = RoundedCornerShape(16.dp),
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text, style = LoanMasterTheme.typography.label, fontWeight = FontWeight.Bold, maxLines = 1, fontSize = 11.sp, overflow = TextOverflow.Ellipsis)
        }
    } else {
        Button(
            onClick = onClick,
            modifier = modifier.height(56.dp).fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = color, contentColor = contentColor),
            shape = RoundedCornerShape(16.dp),
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text, style = LoanMasterTheme.typography.label, fontWeight = FontWeight.Bold, maxLines = 1, fontSize = 11.sp, overflow = TextOverflow.Ellipsis)
        }
    }
}
