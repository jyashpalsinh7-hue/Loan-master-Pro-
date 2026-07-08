package com.loanmaster.pro.feature.loaneligibility

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.loanmaster.pro.feature.currency.*
import com.loanmaster.pro.core.navigation.*
import com.loanmaster.pro.feature.compare.*
import com.loanmaster.pro.core.responsive.*
import com.loanmaster.pro.feature.home.*

val loanProfiles = listOf(
    LoanProfile("Home Loan", 0.50, "8.5", "20"),
    LoanProfile("Loan Against Property", 0.50, "10.5", "15"),
    LoanProfile("Car Loan", 0.45, "9.5", "5"),
    LoanProfile("Two-Wheeler Loan", 0.40, "12.0", "3"),
    LoanProfile("Education Loan", 0.45, "11.0", "7"),
    LoanProfile("Personal Loan", 0.40, "13.5", "3"),
    LoanProfile("Business Term Loan", 0.45, "15.0", "5")
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
    val recommendedLoanAmount = uiState.recommendedLoanAmount
    val currentFoir = uiState.currentFoir

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
                    .padding(LoanMasterTheme.spacing.screenPadding)
                    .imePadding()
                    .safeDrawingPadding(),
                verticalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.md),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Text(
                    text = "Loan Eligibility Calculator",
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
                        .clip(RoundedCornerShape(LoanMasterTheme.spacing.sm))
                        .background(surfaceColor.copy(alpha = 0.3f))
                        .clickable { viewModel.updateInputs(isCoBorrowerEnabled = !isCoBorrowerEnabled) }
                        .padding(LoanMasterTheme.spacing.md)
                ) {
                    Icon(Icons.Rounded.GroupAdd, contentDescription = null, tint = brightBlue)
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
                        Text(
                            text = "Improves loan eligibility",
                            color = textSecondary,
                            style = LoanMasterTheme.typography.label,
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
                if (isCoBorrowerEnabled) {
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

                // 5. Loan Details & Credit Score
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
                                label = { Text("Loan Type", maxLines = 1) },
                                leadingIcon = { Icon(Icons.Rounded.HomeWork, contentDescription = null, tint = brightBlue) },
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
                                singleLine = true
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
                            label = "Loan Tenure (Years)",
                            icon = Icons.Rounded.Event,
                            modifier = mod
                        )
                    }
                )

                AdaptiveRowCol(
                    modifier = Modifier.fillMaxWidth(),
                    content1 = { mod ->
                        Column(modifier = mod) {
                            Text(
                                "Credit Score Range",
                                color = textSecondary,
                                style = LoanMasterTheme.typography.label,
                                modifier = Modifier.padding(bottom = LoanMasterTheme.spacing.sm),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .clip(RoundedCornerShape(LoanMasterTheme.components.cardRadius))
                                    .border(1.dp, surfaceColor, RoundedCornerShape(LoanMasterTheme.components.cardRadius))
                            ) {
                                listOf(
                                    "Excellent" to "750+",
                                    "Good" to "650-740",
                                    "Fair" to "<650"
                                ).forEach { (title, range) ->
                                    val isSelected = creditScoreRange == title
                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxHeight()
                                            .background(if (isSelected) brightBlue else surfaceColor.copy(alpha = 0.3f))
                                            .clickable { viewModel.updateInputs(creditScoreRange = title, defaultRate = profile.defaultRate) },
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            title,
                                            color = if (isSelected) Color.White else textColor,
                                            style = LoanMasterTheme.typography.label,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                        Text(
                                            range,
                                            color = if (isSelected) Color.White.copy(alpha = 0.9f) else textSecondary,
                                            style = LoanMasterTheme.typography.label.copy(fontSize = 10.sp),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    },
                    content2 = { mod ->
                        Column(modifier = mod) {
                            LoanInputField(
                                value = interestRate,
                                onValueChange = { viewModel.updateInputs(rate = it) },
                                label = "Interest Rate (%)",
                                icon = Icons.Rounded.Percent,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Text(
                                "Avg. rate for ${profile.name}",
                                color = textSecondary,
                                style = LoanMasterTheme.typography.label,
                                modifier = Modifier.padding(top = LoanMasterTheme.spacing.xs),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                )

                // 6. Approval Probability Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = surfaceColor.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(LoanMasterTheme.components.cardRadius),
                    border = androidx.compose.foundation.BorderStroke(1.dp, surfaceColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val approvalProb = (1.0f - (currentFoir.toFloat() / 100f)).coerceIn(0f, 1f)
                    val probColor = if (approvalProb > 0.6) neonGreen else dangerRed
                    val probTitle = if (approvalProb > 0.6) "High Chance" else "Low Chance"
                    val probDesc = if (approvalProb > 0.6) "Excellent" else "Poor"
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(LoanMasterTheme.spacing.md),
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
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
                            Text(
                                "Approval Probability",
                                color = textColor,
                                style = LoanMasterTheme.typography.body,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                "$probTitle • $probDesc",
                                color = probColor,
                                style = LoanMasterTheme.typography.label,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                // 7. Estimated Loan Amount Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = surfaceColor.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(LoanMasterTheme.components.cardRadius),
                    border = androidx.compose.foundation.BorderStroke(1.dp, surfaceColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(LoanMasterTheme.spacing.md)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.AccountBalance, contentDescription = null, tint = brightBlue, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(LoanMasterTheme.spacing.xs))
                            Text(
                                "Estimated Eligible Loan Amount",
                                color = textColor,
                                style = LoanMasterTheme.typography.body,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(modifier = Modifier.height(LoanMasterTheme.spacing.md))
                        
                        AutoResizedText(
                            text = formatMoney(eligibleLoanAmount),
                            color = neonGreen,
                            fontSize = LoanMasterTheme.typography.display.fontSize,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(LoanMasterTheme.spacing.sm))
                        
                        val isSafe = currentFoir <= (foirLimit * 100)
                        val statusColor = if (isSafe) neonGreen else dangerRed
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(LoanMasterTheme.spacing.xs))
                                .background(statusColor.copy(alpha = 0.1f))
                                .border(1.dp, statusColor, RoundedCornerShape(LoanMasterTheme.spacing.xs))
                                .padding(horizontal = LoanMasterTheme.spacing.sm, vertical = LoanMasterTheme.spacing.xs),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(if (isSafe) Icons.Rounded.CheckCircle else Icons.Rounded.Warning, contentDescription = null, tint = statusColor, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                if (isSafe) "Safe Zone" else "High Debt Burden",
                                color = statusColor,
                                style = LoanMasterTheme.typography.label,
                                maxLines = 1
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(LoanMasterTheme.spacing.lg))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Monthly Income", color = textSecondary, style = LoanMasterTheme.typography.label, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text(formatMoney(totalIncome), color = textColor, style = LoanMasterTheme.typography.body, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                            Spacer(modifier = Modifier.width(LoanMasterTheme.spacing.sm))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Existing EMIs", color = textSecondary, style = LoanMasterTheme.typography.label, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text(formatMoney(totalExistingEmi), color = textColor, style = LoanMasterTheme.typography.body, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                        }
                    }
                }

                // FOIR Visual Gauge
                Card(
                    colors = CardDefaults.cardColors(containerColor = surfaceColor.copy(alpha = 0.3f)),
                    shape = RoundedCornerShape(LoanMasterTheme.components.cardRadius),
                    border = androidx.compose.foundation.BorderStroke(1.dp, surfaceColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(LoanMasterTheme.spacing.md)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "Your Debt-to-Income Ratio (FOIR)",
                                color = textColor,
                                style = LoanMasterTheme.typography.body,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(Icons.Rounded.Info, contentDescription = null, tint = textSecondary, modifier = Modifier.size(16.dp))
                        }
                        Text(
                            "(Total EMIs / Total Income)",
                            color = textSecondary,
                            style = LoanMasterTheme.typography.label,
                            modifier = Modifier.padding(top = 2.dp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(LoanMasterTheme.spacing.md))
                        
                        val limit = (currentFoir.toFloat() / 100f).coerceIn(0f, 1f)
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("0%", color = textSecondary, style = LoanMasterTheme.typography.label)
                                Text(
                                    "${currentFoir.toInt()}%",
                                    color = if (currentFoir > (foirLimit * 100)) dangerRed else neonGreen,
                                    style = LoanMasterTheme.typography.label,
                                    fontWeight = FontWeight.Bold
                                )
                                Text("100%", color = textSecondary, style = LoanMasterTheme.typography.label)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { limit },
                                modifier = Modifier.fillMaxWidth().height(12.dp).clip(CircleShape),
                                color = if (currentFoir > (foirLimit * 100)) dangerRed else neonGreen,
                                trackColor = surfaceColor,
                                strokeCap = StrokeCap.Round
                            )
                        }
                    }
                }

                // What-If Scenarios
                Text(
                    "What if I...",
                    color = textColor,
                    style = LoanMasterTheme.typography.title,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth().padding(top = LoanMasterTheme.spacing.md),
                    maxLines = 1
                )
                
                BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                    val availableWidth = maxWidth
                    val columns = maxOf(1, (availableWidth.value / 160).toInt())
                    val chunkedItems = listOf(
                        Triple("+50K Income", { viewModel.updateInputs(adjustIncomeAmount = 50000.0) }, Icons.Rounded.TrendingUp),
                        Triple("-10K EMI", { viewModel.updateInputs(adjustEmiAmount = -10000.0) }, Icons.Rounded.TrendingDown),
                        Triple("+5 Yrs Tenure", { viewModel.updateInputs(adjustTenureYears = 5.0) }, Icons.Rounded.CalendarMonth),
                        Triple("Best Rate", { viewModel.updateInputs(creditScoreRange = "Excellent", defaultRate = profile.defaultRate) }, Icons.Rounded.Percent)
                    ).chunked(columns)

                    Column(verticalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.sm)) {
                        chunkedItems.forEach { rowItems ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.sm)
                            ) {
                                rowItems.forEach { (title, action, icon) ->
                                    Card(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(80.dp)
                                            .clickable { action() },
                                        colors = CardDefaults.cardColors(containerColor = surfaceColor.copy(alpha = 0.5f)),
                                        shape = RoundedCornerShape(LoanMasterTheme.spacing.sm),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, surfaceColor)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxSize().padding(LoanMasterTheme.spacing.sm),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(icon, contentDescription = null, tint = brightBlue, modifier = Modifier.size(28.dp))
                                            Spacer(modifier = Modifier.width(LoanMasterTheme.spacing.sm))
                                            Text(
                                                title,
                                                color = textColor,
                                                style = LoanMasterTheme.typography.body,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                                // Fill empty spaces if row is not full
                                repeat(columns - rowItems.size) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(LoanMasterTheme.spacing.md))
                
                // Bottom Action Buttons
                AdaptiveRowCol(
                    modifier = Modifier.fillMaxWidth(),
                    content1 = { mod ->
                        ActionButton(
                            text = "Detailed Report",
                            icon = Icons.Rounded.Description,
                            color = Color.Transparent,
                            contentColor = brightBlue,
                            isOutlined = true,
                            modifier = mod,
                            onClick = {}
                        )
                    },
                    content2 = { mod ->
                        ActionButton(
                            text = "Compare Banks",
                            icon = Icons.Rounded.AccountBalance,
                            color = Color(0xFF9C27B0),
                            contentColor = textColor,
                            modifier = mod,
                            onClick = {}
                        )
                    }
                )
                AdaptiveRowCol(
                    modifier = Modifier.fillMaxWidth(),
                    content1 = { mod ->
                        ActionButton(
                            text = "Save Calculation",
                            icon = Icons.Rounded.Bookmark,
                            color = Color(0xFF2E7D32),
                            contentColor = textColor,
                            modifier = mod,
                            onClick = {}
                        )
                    },
                    content2 = { mod ->
                        val context = LocalContext.current
                        ActionButton(
                            text = "Export PDF",
                            icon = Icons.Rounded.PictureAsPdf,
                            color = Color(0xFFE65100),
                            contentColor = textColor,
                            modifier = mod,
                            onClick = {
                                ExportUtils.exportToPdf(
                                    context,
                                    "Loan Eligibility Report",
                                    listOf(
                                        "Monthly Income" to formatMoney(totalIncome),
                                        "Other EMIs" to formatMoney(totalExistingEmi),
                                        "Interest Rate" to "$interestRate%",
                                        "Loan Tenure" to "$tenureYears Years",
                                        "" to "",
                                        "Eligible Loan Amount" to formatMoney(eligibleLoanAmount),
                                        "Eligible EMI" to formatMoney(availableEmi.coerceAtLeast(0.0))
                                    )
                                )
                            }
                        )
                    }
                )
                
                Spacer(modifier = Modifier.height(LoanMasterTheme.spacing.md))
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
        label = { Text(label, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        leadingIcon = { Icon(icon, contentDescription = null, tint = AccentBlue) },
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
        singleLine = true
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
            modifier = modifier.heightIn(min = 56.dp).fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = contentColor),
            border = androidx.compose.foundation.BorderStroke(1.dp, contentColor),
            shape = RoundedCornerShape(LoanMasterTheme.spacing.sm),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text, style = LoanMasterTheme.typography.label, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    } else {
        Button(
            onClick = onClick,
            modifier = modifier.heightIn(min = 56.dp).fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = color, contentColor = contentColor),
            shape = RoundedCornerShape(LoanMasterTheme.spacing.sm),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text, style = LoanMasterTheme.typography.label, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}
