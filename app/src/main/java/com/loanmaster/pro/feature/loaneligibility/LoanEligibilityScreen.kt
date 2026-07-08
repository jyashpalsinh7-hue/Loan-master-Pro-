package com.loanmaster.pro.feature.loaneligibility

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.loanmaster.pro.core.formatter.formatMoney
import com.loanmaster.pro.core.responsive.AdaptiveRowCol
import com.loanmaster.pro.core.responsive.ResponsiveScreenWrapper
import com.loanmaster.pro.core.theme.*
import com.loanmaster.pro.core.ui.AutoResizedText
import com.loanmaster.pro.domain.calculator.LoanEligibilityCalculator
import com.loanmaster.pro.domain.model.LoanProfile

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
    val maxAllowedEmi = uiState.maxAllowedEmi
    val availableEmi = uiState.availableEmi
    val eligibleLoanAmount = uiState.eligibleLoanAmount
    val currentFoir = uiState.currentFoir

    var isResultVisible by rememberSaveable { mutableStateOf(false) }
    var isInputExpanded by rememberSaveable { mutableStateOf(true) }
    
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = LoanMasterTheme.spacing.md, vertical = LoanMasterTheme.spacing.md)
                    .imePadding()
                    .safeDrawingPadding(),
                verticalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.sm),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                item {
                    Text(
                        text = "Loan Eligibility",
                        color = textColor,
                        style = LoanMasterTheme.typography.title,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Collapsed Summary
                item {
                    AnimatedVisibility(
                        visible = !isInputExpanded && isResultVisible,
                        enter = expandVertically(animationSpec = tween(300)) + fadeIn(animationSpec = tween(300)),
                        exit = shrinkVertically(animationSpec = tween(300)) + fadeOut(animationSpec = tween(300))
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = surfaceColor.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, surfaceColor),
                            modifier = Modifier.fillMaxWidth().clickable { isInputExpanded = true }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(LoanMasterTheme.spacing.md),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Text(selectedLoanProfile, color = textColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(textSecondary))
                                        Text("$tenureYears Yrs", color = textSecondary, fontSize = 13.sp)
                                        Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(textSecondary))
                                        Text("$interestRate%", color = textSecondary, fontSize = 13.sp)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Text("Income: ${formatMoney(totalIncome)}", color = neonGreen, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                        Text("EMI: ${formatMoney(totalExistingEmi)}", color = warningYellow, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                    }
                                }
                                
                                Surface(
                                    color = brightBlue.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, brightBlue.copy(alpha = 0.3f))
                                ) {
                                    Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Rounded.Edit, contentDescription = "Edit", tint = brightBlue, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Edit", color = brightBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                // Inputs
                item {
                    AnimatedVisibility(
                        visible = isInputExpanded,
                        enter = expandVertically(animationSpec = tween(300)) + fadeIn(animationSpec = tween(300)),
                        exit = shrinkVertically(animationSpec = tween(300)) + fadeOut(animationSpec = tween(300))
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.sm)) {
                            // 1. Employment Type
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
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
                                    .height(56.dp)
                                    .clip(RoundedCornerShape(LoanMasterTheme.spacing.sm))
                                    .background(surfaceColor.copy(alpha = 0.3f))
                                    .border(1.dp, surfaceColor, RoundedCornerShape(LoanMasterTheme.spacing.sm))
                                    .clickable { viewModel.updateInputs(isCoBorrowerEnabled = !isCoBorrowerEnabled) }
                                    .padding(horizontal = LoanMasterTheme.spacing.md)
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
                                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
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
                                                .height(64.dp)
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
                            Column(modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
                                Text(
                                    "Credit Score",
                                    color = textSecondary,
                                    style = LoanMasterTheme.typography.label,
                                    modifier = Modifier.padding(bottom = 8.dp),
                                    maxLines = 1
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
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
                                                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                                                    Text(title, fontWeight = FontWeight.Bold, maxLines = 1, fontSize = 13.sp)
                                                    Text(range, fontSize = 11.sp, maxLines = 1, color = if (isSelected) Color.White.copy(alpha = 0.9f) else textSecondary)
                                                }
                                            },
                                            modifier = Modifier.weight(1f).heightIn(min = 56.dp),
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
                        }
                    }
                }

                // Action Button (Calculate)
                item {
                    val targetHeight = if (isInputExpanded) 64f else 56f
                    val animatedHeight by animateFloatAsState(targetValue = targetHeight, animationSpec = tween(300), label = "")
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Button(
                        onClick = { 
                            if (isInputExpanded) {
                                isResultVisible = true
                                isInputExpanded = false
                            } else {
                                // Just a visual feedback or re-trigger calculation if needed
                                // Currently acts as update button if they changed mind and stayed expanded
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(animatedHeight.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = brightBlue, contentColor = Color.White),
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            if (isInputExpanded) "Calculate Eligibility" else "Update Calculation", 
                            style = LoanMasterTheme.typography.body, 
                            fontWeight = FontWeight.Bold,
                            fontSize = if (isInputExpanded) 16.sp else 14.sp
                        )
                    }
                    
                    if (isInputExpanded) {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                // Empty State
                item {
                    AnimatedVisibility(
                        visible = !isResultVisible && isInputExpanded,
                        enter = fadeIn(animationSpec = tween(500)),
                        exit = fadeOut(animationSpec = tween(300))
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier.size(80.dp).background(brightBlue.copy(alpha = 0.1f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Rounded.AccountBalance, contentDescription = null, tint = brightBlue, modifier = Modifier.size(40.dp))
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Check Your Loan Eligibility", color = textColor, style = LoanMasterTheme.typography.title, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Enter your income and loan details to calculate the maximum amount you can borrow instantly.", color = textSecondary, style = LoanMasterTheme.typography.label, textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 16.dp))
                        }
                    }
                }

                // Results
                item {
                    AnimatedVisibility(
                        visible = isResultVisible && !isInputExpanded,
                        enter = expandVertically(animationSpec = tween(500)) + fadeIn(animationSpec = tween(500)),
                        exit = shrinkVertically(animationSpec = tween(300)) + fadeOut(animationSpec = tween(300))
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            
                            val targetApprovalProb = (1.0f - (currentFoir.toFloat() / (foirLimit.toFloat().coerceAtLeast(0.01f)))).coerceIn(0f, 1f)
                            val approvalProb by animateFloatAsState(targetValue = if (isResultVisible && !isInputExpanded) targetApprovalProb else 0f, animationSpec = tween(1500), label = "probAnim")
                            
                            val isSafe = targetApprovalProb > 0.6f
                            val safeColor = if (isSafe) neonGreen else if (targetApprovalProb > 0.4f) warningYellow else dangerRed
                            val grade = uiState.verdictGrade.ifEmpty { if (isSafe) "A+" else "C" }
                            val conf = if (targetApprovalProb > 0.8) "High" else if (targetApprovalProb > 0.6) "Moderate" else "Low"

                            // 1. Hero Card
                            Card(
                                colors = CardDefaults.cardColors(containerColor = surfaceColor.copy(alpha = 0.4f)),
                                shape = RoundedCornerShape(24.dp),
                                border = BorderStroke(1.dp, surfaceColor.copy(alpha = 0.8f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                        Text("Eligible Loan Amount", color = textSecondary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                        Surface(
                                            color = safeColor.copy(alpha = 0.15f),
                                            shape = RoundedCornerShape(8.dp),
                                            border = BorderStroke(1.dp, safeColor.copy(alpha = 0.3f))
                                        ) {
                                            Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                                Icon(if (isSafe) Icons.Rounded.VerifiedUser else Icons.Rounded.Warning, contentDescription = null, tint = safeColor, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(if (isSafe) "Safe Zone" else "Risk Zone", color = safeColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    AutoResizedText(
                                        text = formatMoney(eligibleLoanAmount),
                                        color = safeColor,
                                        fontSize = 40.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    
                                    Spacer(modifier = Modifier.height(24.dp))
                                    HorizontalDivider(color = surfaceColor, thickness = 1.dp)
                                    Spacer(modifier = Modifier.height(16.dp))
                                    
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("Monthly EMI Capacity", color = textSecondary, fontSize = 12.sp)
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(formatMoney(availableEmi.coerceAtLeast(0.0)), color = textColor, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                                            Text("Max Affordable EMI", color = textSecondary, fontSize = 12.sp)
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(formatMoney(maxAllowedEmi), color = textColor, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("Total Income", color = textSecondary, fontSize = 12.sp)
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(formatMoney(totalIncome), color = textColor, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                                            Text("Existing EMI", color = textSecondary, fontSize = 12.sp)
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(formatMoney(totalExistingEmi), color = textColor, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }

                            // 2. FOIR & Approval Card
                            Card(
                                colors = CardDefaults.cardColors(containerColor = surfaceColor.copy(alpha = 0.4f)),
                                shape = RoundedCornerShape(20.dp),
                                border = BorderStroke(1.dp, surfaceColor.copy(alpha = 0.8f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(20.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(80.dp)) {
                                        CircularProgressIndicator(
                                            progress = { 1f },
                                            modifier = Modifier.size(80.dp),
                                            color = bgColor,
                                            strokeWidth = 8.dp,
                                            strokeCap = StrokeCap.Round
                                        )
                                        CircularProgressIndicator(
                                            progress = { approvalProb },
                                            modifier = Modifier.size(80.dp),
                                            color = safeColor,
                                            strokeWidth = 8.dp,
                                            strokeCap = StrokeCap.Round
                                        )
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                "${(approvalProb * 100).toInt()}%",
                                                color = textColor,
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Bold,
                                            )
                                            Text("Approval", color = textSecondary, fontSize = 9.sp)
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(24.dp))
                                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                            Text("FOIR (Debt-to-Income)", color = textSecondary, fontSize = 13.sp)
                                            Text("${(currentFoir * 100).toInt()}%", color = textColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        }
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                            Text("Bank Confidence", color = textSecondary, fontSize = 13.sp)
                                            Text(conf, color = safeColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        }
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                            Text("Credit Grade", color = textSecondary, fontSize = 13.sp)
                                            Text(grade, color = safeColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        }
                                    }
                                }
                            }

                            // 3. What-If Scenarios
                            Text(
                                "What if I...",
                                color = textColor,
                                style = LoanMasterTheme.typography.title,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                                maxLines = 1
                            )
                            
                            val calcWhatIf = { inc: Double, emi: Double, ten: Double, rt: Double ->
                                calculator.calculate(
                                    profileName = selectedLoanProfile,
                                    incomeStr = ((monthlyIncome.toDoubleOrNull() ?: 0.0) + inc).toString(),
                                    emiStr = maxOf(0.0, (existingEMIs.toDoubleOrNull() ?: 0.0) + emi).toString(),
                                    isCoBorrower = isCoBorrowerEnabled,
                                    coIncomeStr = coBorrowerIncome,
                                    coEmiStr = coBorrowerEMIs,
                                    tenureStr = maxOf(1.0, (tenureYears.toDoubleOrNull() ?: 0.0) + ten).toString(),
                                    rateStr = maxOf(1.0, (interestRate.toDoubleOrNull() ?: 0.0) + rt).toString(),
                                    isSal = isSalaried,
                                    creditScore = creditScoreRange
                                ).eligibleLoanAmount
                            }
                            
                            val ifIncomeResult = remember(monthlyIncome, existingEMIs, isCoBorrowerEnabled, coBorrowerIncome, coBorrowerEMIs, tenureYears, interestRate, isSalaried, creditScoreRange, selectedLoanProfile) {
                                calcWhatIf(50000.0, 0.0, 0.0, 0.0)
                            }
                            val ifEmiResult = remember(monthlyIncome, existingEMIs, isCoBorrowerEnabled, coBorrowerIncome, coBorrowerEMIs, tenureYears, interestRate, isSalaried, creditScoreRange, selectedLoanProfile) {
                                calcWhatIf(0.0, -10000.0, 0.0, 0.0)
                            }
                            val ifTenureResult = remember(monthlyIncome, existingEMIs, isCoBorrowerEnabled, coBorrowerIncome, coBorrowerEMIs, tenureYears, interestRate, isSalaried, creditScoreRange, selectedLoanProfile) {
                                calcWhatIf(0.0, 0.0, 5.0, 0.0)
                            }
                            val ifRateResult = remember(monthlyIncome, existingEMIs, isCoBorrowerEnabled, coBorrowerIncome, coBorrowerEMIs, tenureYears, interestRate, isSalaried, creditScoreRange, selectedLoanProfile) {
                                calcWhatIf(0.0, 0.0, 0.0, -1.0)
                            }
                            
                            val formatCompact = { amount: Double ->
                                if (amount >= 10000000) "₹${String.format("%.1f", amount / 10000000)}Cr"
                                else if (amount >= 100000) "₹${String.format("%.1f", amount / 100000)}L"
                                else if (amount >= 1000) "₹${String.format("%.0f", amount / 1000)}K"
                                else formatMoney(amount)
                            }

                            @Composable
                            fun WhatIfCard(title: String, icon: ImageVector, iconColor: Color, newValue: Double, mod: Modifier) {
                                val diff = newValue - eligibleLoanAmount
                                val isPositive = diff > 0
                                val diffColor = if (isPositive) neonGreen else textColor
                                val diffSign = if (isPositive) "+" else ""
                                
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = surfaceColor.copy(alpha = 0.4f)),
                                    shape = RoundedCornerShape(16.dp),
                                    border = BorderStroke(1.dp, surfaceColor.copy(alpha = 0.8f)),
                                    modifier = mod
                                ) {
                                    Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(title, color = textColor, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        }
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Row(verticalAlignment = Alignment.Bottom) {
                                            Text("${formatCompact(eligibleLoanAmount)} → ", color = textSecondary, fontSize = 13.sp)
                                            Text(formatCompact(newValue), color = textColor, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("$diffSign${formatCompact(diff)}", color = diffColor, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }

                            AdaptiveRowCol(
                                modifier = Modifier.fillMaxWidth(),
                                content1 = { mod ->
                                    WhatIfCard("+₹50K Income", Icons.AutoMirrored.Rounded.TrendingUp, brightBlue, ifIncomeResult, mod)
                                },
                                content2 = { mod ->
                                    WhatIfCard("-₹10K EMI", Icons.AutoMirrored.Rounded.TrendingDown, warningYellow, ifEmiResult, mod)
                                }
                            )
                            
                            AdaptiveRowCol(
                                modifier = Modifier.fillMaxWidth(),
                                content1 = { mod ->
                                    WhatIfCard("+5 Yrs Tenure", Icons.Rounded.Event, Color(0xFF9C27B0), ifTenureResult, mod)
                                },
                                content2 = { mod ->
                                    WhatIfCard("-1% Int. Rate", Icons.Rounded.Percent, neonGreen, ifRateResult, mod)
                                }
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            
                            // 4. Action Buttons
                            // Primary Action
                            Button(
                                onClick = {},
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = brightBlue, contentColor = Color.White),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Icon(Icons.Rounded.AccountBalance, contentDescription = null, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Compare Banks", style = LoanMasterTheme.typography.body, fontWeight = FontWeight.Bold)
                            }
                            
                            // Secondary Actions
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.sm)) {
                                ActionButton(
                                    text = "Detailed Report",
                                    icon = Icons.Rounded.Description,
                                    color = surfaceColor,
                                    contentColor = brightBlue,
                                    modifier = Modifier.weight(1f),
                                    onClick = {}
                                )
                                ActionButton(
                                    text = "Save Calc",
                                    icon = Icons.Rounded.Bookmark,
                                    color = surfaceColor,
                                    contentColor = textColor,
                                    modifier = Modifier.weight(1f),
                                    onClick = {}
                                )
                            }
                            
                            val context = LocalContext.current
                            OutlinedButton(
                                onClick = {},
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = textColor),
                                border = BorderStroke(1.dp, surfaceColor),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Icon(Icons.Rounded.PictureAsPdf, contentDescription = null, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Export PDF", style = LoanMasterTheme.typography.body, fontWeight = FontWeight.Medium)
                            }
                            
                            Spacer(modifier = Modifier.height(24.dp))
                        }
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
        modifier = modifier.fillMaxWidth().height(64.dp),
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
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(56.dp).fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = color, contentColor = contentColor),
        shape = RoundedCornerShape(16.dp),
        contentPadding = PaddingValues(horizontal = 8.dp)
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text, style = LoanMasterTheme.typography.label, fontWeight = FontWeight.Medium, maxLines = 1, fontSize = 12.sp, overflow = TextOverflow.Ellipsis)
    }
}
