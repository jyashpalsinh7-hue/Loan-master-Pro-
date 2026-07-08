package com.loanmaster.pro.feature.loaneligibility

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
import androidx.window.core.layout.WindowWidthSizeClass


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.NumberFormat
import java.util.Locale
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo


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
    val adaptiveInfo = currentWindowAdaptiveInfo()
    val sizeClass = when (adaptiveInfo.windowSizeClass.windowWidthSizeClass) {
        WindowWidthSizeClass.COMPACT -> WindowWidthSizeClass.COMPACT
        WindowWidthSizeClass.MEDIUM -> WindowWidthSizeClass.MEDIUM
        else -> WindowWidthSizeClass.EXPANDED
    }

    val bgColor = BackgroundDark
    val surfaceColor = SurfaceDark
    val neonGreen = Color(0xFF4ADE80)
    val brightBlue = Color(0xFF3B82F6)
    val warningYellow = Color(0xFFFBBF24)
    val dangerRed = Color(0xFFEF4444)
    val textColor = TextPrimary
    val textSecondary = TextSecondary
    val formatMoney = { amount: Double ->
        com.loanmaster.pro.core.formatter.formatMoney(amount)
    }

        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val dummyCurrency = com.loanmaster.pro.LocalCurrency.current
    
    val selectedLoanProfile = uiState.selectedLoanProfile
    val monthlyIncome = uiState.monthlyIncomeText
    val existingEMIs = uiState.existingEMIsText
    val isCoBorrowerEnabled = uiState.isCoBorrowerEnabled
    val coBorrowerIncome = uiState.coBorrowerIncomeText
    val coBorrowerEMIs = uiState.coBorrowerEMIsText
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
                    .imePadding(),
                verticalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.md)
            ) {
            // 1. Top App Bar
            Row(
                modifier = Modifier.fillMaxWidth().statusBarsPadding(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Back",
                    tint = textColor,
                    modifier = Modifier.clickable { onNavigateBack() }
                )
                Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.md))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Loan Eligibility Checker", color = textColor, style = LoanMasterTheme.typography.title)
                    Text("Check how much loan you can get instantly", color = textSecondary, style = LoanMasterTheme.typography.label)
                }
                Icon(Icons.Rounded.StarBorder, contentDescription = "Star", tint = warningYellow, modifier = Modifier.size(LoanMasterTheme.spacing.lg).clickable { })
                Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.md))
                val context = androidx.compose.ui.platform.LocalContext.current
                Icon(Icons.Rounded.PictureAsPdf, contentDescription = "Export PDF", tint = textColor, modifier = Modifier.size(LoanMasterTheme.spacing.lg).clickable {
                    ExportUtils.exportToPdf(
                        context,
                        "Loan Eligibility Report",
                        listOf(
                            "Monthly Income" to com.loanmaster.pro.core.formatter.formatMoney(totalIncome),
                            "Other EMIs" to com.loanmaster.pro.core.formatter.formatMoney(totalExistingEmi),
                            "Interest Rate" to "$interestRate%",
                            "Loan Tenure" to "$tenureYears Years",
                            "" to "",
                            "Eligible Loan Amount" to com.loanmaster.pro.core.formatter.formatMoney(eligibleLoanAmount),
                            "Eligible EMI" to com.loanmaster.pro.core.formatter.formatMoney(availableEmi.coerceAtLeast(0.0))
                        )
                    )
                })
            }

            // 2. Employment Type Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 80.dp)
                    .clip(RoundedCornerShape(LoanMasterTheme.spacing.sm))
                    .background(surfaceColor)
                    .border(1.dp, surfaceColor, RoundedCornerShape(LoanMasterTheme.spacing.sm))
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(LoanMasterTheme.spacing.sm))
                        .background(if (isSalaried) brightBlue.copy(alpha = 0.2f) else Color.Transparent)
                        .border(1.dp, if (isSalaried) brightBlue else Color.Transparent, RoundedCornerShape(LoanMasterTheme.spacing.sm))
                        .clickable { viewModel.updateInputs(isSalaried = true) },
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Work, contentDescription = null, tint = if (isSalaried) textColor else textSecondary, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.sm))
                        Text("Salaried", color = if (isSalaried) textColor else textSecondary, style = LoanMasterTheme.typography.label, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis, fontWeight = if (isSalaried) FontWeight.Bold else FontWeight.Normal)
                    }
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(LoanMasterTheme.spacing.sm))
                        .background(if (!isSalaried) brightBlue.copy(alpha = 0.2f) else Color.Transparent)
                        .border(1.dp, if (!isSalaried) brightBlue else Color.Transparent, RoundedCornerShape(LoanMasterTheme.spacing.sm))
                        .clickable { viewModel.updateInputs(isSalaried = false) },
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Storefront, contentDescription = null, tint = if (!isSalaried) textColor else textSecondary, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.sm))
                        Text("Self-Employed", color = if (!isSalaried) textColor else textSecondary, style = LoanMasterTheme.typography.label, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis, fontWeight = if (!isSalaried) FontWeight.Bold else FontWeight.Normal)
                    }
                }
            }

            // 3. Main Input Section
            AdaptiveRowCol(
                modifier = Modifier.fillMaxWidth(),
                content1 = { mod ->
                    AutoResizeTextField(
                        value = monthlyIncome,
                        onValueChange = { viewModel.updateInputs(income = it) },
                        label = "Monthly Income (${com.loanmaster.pro.core.formatter.currentCurrencySymbol})",
                        modifier = mod,
                        leadingIcon = { Icon(Icons.Rounded.AccountBalanceWallet, contentDescription = null, tint = brightBlue) }
                    )
                },
                content2 = { mod ->
                    AutoResizeTextField(
                        value = existingEMIs,
                        onValueChange = { viewModel.updateInputs(emi = it) },
                        label = "Existing EMIs (${com.loanmaster.pro.core.formatter.currentCurrencySymbol})",
                        modifier = mod,
                        leadingIcon = { Icon(Icons.Rounded.CreditCard, contentDescription = null, tint = brightBlue) }
                    )
                }
            )

            // 4. Co-Borrower Section (Animated)
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = LoanMasterTheme.spacing.sm),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Add Co-Borrower", color = textColor, style = LoanMasterTheme.typography.body, fontWeight = FontWeight.Bold)
                    Text("Include spouse, parent or sibling income", color = textSecondary, style = LoanMasterTheme.typography.label)
                }
                Switch(
                    checked = isCoBorrowerEnabled,
                    onCheckedChange = { viewModel.updateInputs(isCoBorrowerEnabled = it) },
                    colors = SwitchDefaults.colors(checkedThumbColor = textColor, checkedTrackColor = brightBlue)
                )
            }

            AnimatedVisibility(visible = isCoBorrowerEnabled) {
                AdaptiveRowCol(
                    modifier = Modifier.fillMaxWidth(),
                    content1 = { mod ->
                        AutoResizeTextField(
                            value = coBorrowerIncome,
                            onValueChange = { viewModel.updateInputs(coIncome = it) },
                            label = "Co-Borrower Income (${com.loanmaster.pro.core.formatter.currentCurrencySymbol})",
                            modifier = mod,
                            leadingIcon = { Icon(Icons.Rounded.Group, contentDescription = null, tint = brightBlue) }
                        )
                    },
                    content2 = { mod ->
                        AutoResizeTextField(
                            value = coBorrowerEMIs,
                            onValueChange = { viewModel.updateInputs(coEmi = it) },
                            label = "Co-Borrower EMIs (${com.loanmaster.pro.core.formatter.currentCurrencySymbol})",
                            modifier = mod,
                            leadingIcon = { Icon(Icons.Rounded.CreditCard, contentDescription = null, tint = brightBlue) }
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
                        AutoResizeTextField(
                            isNumeric = false,
                            value = selectedLoanProfile,
                            onValueChange = {},
                            label = "Loan Type",
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(type = MenuAnchorType.PrimaryEditable, enabled = true),
                            readOnly = true,
                            leadingIcon = { Icon(Icons.Rounded.HomeWork, contentDescription = null, tint = brightBlue) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = loanDropdownExpanded) }
                        )
                        ExposedDropdownMenu(
                            expanded = loanDropdownExpanded,
                            onDismissRequest = { loanDropdownExpanded = false },
                            containerColor = surfaceColor
                        ) {
                            loanProfiles.forEach { profile ->
                                DropdownMenuItem(
                                    text = { Text(profile.name, color = textColor) },
                                    onClick = {
                                        viewModel.updateInputs(profile = profile.name, defaultTenure = profile.defaultTenure, defaultRate = profile.defaultRate)
                                        loanDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                },
                content2 = { mod ->
                    AutoResizeTextField(
                        value = tenureYears,
                        onValueChange = { viewModel.updateInputs(tenure = it) },
                        label = "Loan Tenure (Years)",
                        modifier = mod,
                        leadingIcon = { Icon(Icons.Rounded.Event, contentDescription = null, tint = brightBlue) }
                    )
                }
            )

            AdaptiveRowCol(
                modifier = Modifier.fillMaxWidth(),
                content1 = { mod ->
                    Column(modifier = mod) {
                        Text("Credit Score Range", color = textSecondary, style = LoanMasterTheme.typography.label, modifier = Modifier.padding(bottom = LoanMasterTheme.spacing.sm))
                        Row(
                            modifier = Modifier.fillMaxWidth().heightIn(min = 80.dp).clip(RoundedCornerShape(LoanMasterTheme.components.cardRadius)).border(1.dp, surfaceColor, RoundedCornerShape(LoanMasterTheme.components.cardRadius))
                        ) {
                            listOf(Triple("Excellent", "750+", brightBlue), Triple("Good", "650 - 740", surfaceColor), Triple("Fair", "< 650", surfaceColor)).forEach { (title, range, color) ->
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
                                    Text(title, color = textColor, style = LoanMasterTheme.typography.label, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                                    Text(range, color = if (isSelected) textColor else textSecondary, style = LoanMasterTheme.typography.label, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                                }
                            }
                        }
                    }
                },
                content2 = { mod ->
                    Column(modifier = mod) {
                        AutoResizeTextField(
                            value = interestRate,
                            onValueChange = { viewModel.updateInputs(rate = it) },
                            label = "Interest Rate (%)",
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = { Icon(Icons.Rounded.Percent, contentDescription = null, tint = brightBlue) }
                        )
                        Text("Avg. rate for ${profile.name}", color = textSecondary, style = LoanMasterTheme.typography.label, modifier = Modifier.padding(top = LoanMasterTheme.spacing.xs))
                    }
                }
            )

                        // 6. FOIR Visual Gauge
            Card(
                colors = CardDefaults.cardColors(containerColor = surfaceColor.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(LoanMasterTheme.components.cardRadius),
                border = androidx.compose.foundation.BorderStroke(1.dp, surfaceColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(LoanMasterTheme.spacing.md)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Your Debt-to-Income Ratio (FOIR)", color = textColor, style = LoanMasterTheme.typography.body, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.xs))
                        Icon(Icons.Rounded.Info, contentDescription = null, tint = textSecondary, modifier = Modifier.size(16.dp))
                    }
                    Text("(Total EMIs / Total Income)", color = textSecondary, style = LoanMasterTheme.typography.label, modifier = Modifier.padding(top = 2.dp))
                    Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.lg))
                    
                    Box(modifier = Modifier.fillMaxWidth().heightIn(min = 60.dp)) {
                        val limit = (currentFoir.toFloat() / 100f).coerceIn(0f, 1f)
                        
                        // Percentage label above marker
                        BoxWithConstraints(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                             val offsetX = (maxWidth * limit) - 16.dp
                             Text(
                                 "${currentFoir.toInt()}%", 
                                 color = bgColor, 
                                 style = LoanMasterTheme.typography.label, 
                                 fontWeight = FontWeight.Bold,
                                 modifier = Modifier
                                    .offset(x = offsetX)
                                    .background(neonGreen, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                             )
                        }
                        
                        // The track and thumb
                        Canvas(modifier = Modifier.fillMaxWidth().heightIn(min = 20.dp).align(Alignment.BottomCenter).padding(bottom = 10.dp)) {
                            val w = size.width
                            val h = size.height
                            val trackY = h / 2
                            
                            // Track line
                            drawLine(color = Color.DarkGray, start = Offset(0f, trackY), end = Offset(w, trackY), strokeWidth = 4f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                            
                            // Ticks
                            for (i in 0..5) {
                                val tickX = w * (i * 0.2f)
                                drawLine(color = Color.DarkGray, start = Offset(tickX, trackY - 6f), end = Offset(tickX, trackY + 6f), strokeWidth = 2f)
                            }
                            
                            // Thumb
                            val markerX = limit * w
                            drawCircle(color = neonGreen, radius = 8.dp.toPx(), center = Offset(markerX, trackY))
                        }
                    }
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("0%", color = textSecondary, style = LoanMasterTheme.typography.label)
                        Text("20%", color = textSecondary, style = LoanMasterTheme.typography.label)
                        Text("40%", color = textSecondary, style = LoanMasterTheme.typography.label)
                        Text("60%", color = textSecondary, style = LoanMasterTheme.typography.label)
                        Text("80%", color = textSecondary, style = LoanMasterTheme.typography.label)
                        Text("100%", color = textSecondary, style = LoanMasterTheme.typography.label)
                    }

                    Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.md))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(neonGreen))
                            Spacer(modifier = Modifier.widthIn(min = 4.dp))
                            Text("Low Risk (Safe)", color = textSecondary, style = LoanMasterTheme.typography.label)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(warningYellow))
                            Spacer(modifier = Modifier.widthIn(min = 4.dp))
                            Text("Moderate Risk", color = textSecondary, style = LoanMasterTheme.typography.label)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(dangerRed))
                            Spacer(modifier = Modifier.widthIn(min = 4.dp))
                            Text("High Risk", color = textSecondary, style = LoanMasterTheme.typography.label)
                        }
                    }
                }
            }

                        // 7. Hero Results Dashboard
            Card(
                colors = CardDefaults.cardColors(containerColor = surfaceColor.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(LoanMasterTheme.components.cardRadius),
                border = androidx.compose.foundation.BorderStroke(1.dp, surfaceColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(LoanMasterTheme.spacing.md)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Estimated Eligible Loan Amount", color = textColor, style = LoanMasterTheme.typography.body, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.xs))
                        Icon(Icons.Rounded.Info, contentDescription = null, tint = textSecondary, modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.md))
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                        Column {
                            AutoResizeHeroText(
                                text = com.loanmaster.pro.core.formatter.formatMoney(eligibleLoanAmount),
                                color = neonGreen
                            )
                            Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.sm))
                            val isSafe = currentFoir <= (foirLimit * 100)
                            Row(
                                modifier = Modifier.clip(RoundedCornerShape(LoanMasterTheme.spacing.xs)).background((if(isSafe) neonGreen else dangerRed).copy(alpha = 0.1f)).border(1.dp, if(isSafe) neonGreen else dangerRed, RoundedCornerShape(LoanMasterTheme.spacing.xs)).padding(horizontal = LoanMasterTheme.spacing.sm, vertical = LoanMasterTheme.spacing.xs),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(if(isSafe) Icons.Rounded.CheckCircle else Icons.Rounded.Warning, contentDescription = null, tint = if(isSafe) neonGreen else dangerRed, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.widthIn(min = 4.dp))
                                Text(if(isSafe) "You are in the Safe Zone" else "High Debt Burden", color = if(isSafe) neonGreen else dangerRed, style = LoanMasterTheme.typography.label)
                            }
                        }
                        Icon(Icons.Rounded.AccountBalance, contentDescription = null, tint = brightBlue, modifier = Modifier.size(64.dp))
                    }
                    
                    Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.lg))
                    
                    Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(LoanMasterTheme.spacing.sm)).background(bgColor).padding(LoanMasterTheme.spacing.md), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Info, contentDescription = null, tint = textSecondary, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.md))
                        Text("This is an estimated amount based on the inputs provided. Final approval depends on bank policies.", color = textSecondary, style = LoanMasterTheme.typography.label, modifier = Modifier.weight(1f))
                    }
                    
                    Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.lg))
                    
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Monthly Income", color = textSecondary, style = LoanMasterTheme.typography.label)
                            Spacer(modifier = Modifier.heightIn(min = 4.dp))
                            Text(com.loanmaster.pro.core.formatter.formatMoney(totalIncome), color = textColor, style = LoanMasterTheme.typography.body, fontWeight = FontWeight.Bold)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Total Existing EMIs", color = textSecondary, style = LoanMasterTheme.typography.label)
                            Spacer(modifier = Modifier.heightIn(min = 4.dp))
                            Text(com.loanmaster.pro.core.formatter.formatMoney(totalExistingEmi), color = textColor, style = LoanMasterTheme.typography.body, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // 8. The Three Metric Cards
            AdaptiveRowCol3(
                modifier = Modifier.fillMaxWidth(),
                content1 = { mod ->
                    Card(
                        modifier = mod.heightIn(min = 124.dp),
                        colors = CardDefaults.cardColors(containerColor = surfaceColor.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(LoanMasterTheme.components.cardRadius),
                        border = androidx.compose.foundation.BorderStroke(1.dp, surfaceColor)
                    ) {
                        Column(modifier = Modifier.padding(LoanMasterTheme.components.cardRadius)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Approval Probability", color = textColor, style = LoanMasterTheme.typography.body, fontWeight = FontWeight.SemiBold)
                                Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.xs))
                                Icon(Icons.Rounded.Info, contentDescription = null, tint = textSecondary, modifier = Modifier.size(LoanMasterTheme.spacing.md))
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                val approvalProb = (1.0f - (currentFoir.toFloat() / 100f)).coerceIn(0f, 1f)
                                Box(modifier = Modifier.size(56.dp), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(progress = { approvalProb }, color = neonGreen, trackColor = bgColor, gapSize = 0.dp, modifier = Modifier.size(56.dp))
                                    Text("${(approvalProb * 100).toInt()}%", color = textColor, style = LoanMasterTheme.typography.body, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.sm))
                                Column {
                                    Text(if(approvalProb > 0.6) "High Chance" else "Low Chance", color = if(approvalProb > 0.6) neonGreen else dangerRed, style = LoanMasterTheme.typography.body, fontWeight = FontWeight.Bold)
                                    Text(if(approvalProb > 0.6) "Excellent" else "Poor", color = textColor, style = LoanMasterTheme.typography.body)
                                }
                            }
                        }
                    }
                },
                content2 = { mod ->
                    Card(
                        modifier = mod.heightIn(min = 124.dp),
                        colors = CardDefaults.cardColors(containerColor = surfaceColor.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(LoanMasterTheme.components.cardRadius),
                        border = androidx.compose.foundation.BorderStroke(1.dp, surfaceColor)
                    ) {
                        Column(modifier = Modifier.padding(LoanMasterTheme.components.cardRadius)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Recommended Loan Amount", color = textColor, style = LoanMasterTheme.typography.body, fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            Text(com.loanmaster.pro.core.formatter.formatMoney(recommendedLoanAmount), color = brightBlue, style = LoanMasterTheme.typography.title, fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("For better financial stability", color = textSecondary, style = LoanMasterTheme.typography.label, lineHeight = LoanMasterTheme.typography.label.fontSize)
                        }
                    }
                },
                content3 = { mod ->
                    Card(
                        modifier = mod.heightIn(min = 124.dp),
                        colors = CardDefaults.cardColors(containerColor = surfaceColor.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(LoanMasterTheme.components.cardRadius),
                        border = androidx.compose.foundation.BorderStroke(1.dp, surfaceColor)
                    ) {
                        Column(modifier = Modifier.padding(LoanMasterTheme.components.cardRadius)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Max Affordable EMI", color = textColor, style = LoanMasterTheme.typography.body, fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(com.loanmaster.pro.core.formatter.formatMoney(maxAllowedEmi), color = warningYellow, style = LoanMasterTheme.typography.title, fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false)
                                Text("/month", color = textColor, style = LoanMasterTheme.typography.label, modifier = Modifier.padding(bottom = LoanMasterTheme.spacing.xs))
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("Keep your FOIR under ${(foirLimit * 100).toInt()}%", color = textSecondary, style = LoanMasterTheme.typography.label, lineHeight = LoanMasterTheme.typography.label.fontSize)
                        }
                    }
                }
            )

            // 9. Interactive "What If?" Section
            Column {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text("What If?", color = textColor, style = LoanMasterTheme.typography.body, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.sm))
                    Text("Adjust values and see impact on your eligibility", color = textSecondary, style = LoanMasterTheme.typography.label)
                }
                Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.components.cardRadius))
                AdaptiveRowCol(
                    modifier = Modifier.fillMaxWidth(),
                    content1 = { mod ->
                        Card(modifier = mod.clickable { 
                            viewModel.updateInputs(adjustIncomeAmount = 10000.toDouble())
                        }, colors = CardDefaults.cardColors(containerColor = surfaceColor.copy(alpha = 0.5f)), shape = RoundedCornerShape(LoanMasterTheme.spacing.sm), border = androidx.compose.foundation.BorderStroke(1.dp, surfaceColor)) {
                            Row(modifier = Modifier.padding(LoanMasterTheme.components.cardRadius), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.AutoMirrored.Rounded.TrendingUp, contentDescription = null, tint = neonGreen, modifier = Modifier.size(32.dp))
                                Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.sm))
                                Column { Text("+ ${com.loanmaster.pro.core.formatter.currentCurrencySymbol}10,000", color = textColor, style = LoanMasterTheme.typography.body, fontWeight = FontWeight.Bold); Text("Income", color = textSecondary, style = LoanMasterTheme.typography.body) }
                            }
                        }
                    },
                    content2 = { mod ->
                        Card(modifier = mod.clickable { 
                            viewModel.updateInputs(adjustEmiAmount = -5000.toDouble())
                        }, colors = CardDefaults.cardColors(containerColor = surfaceColor.copy(alpha = 0.5f)), shape = RoundedCornerShape(LoanMasterTheme.spacing.sm), border = androidx.compose.foundation.BorderStroke(1.dp, surfaceColor)) {
                            Row(modifier = Modifier.padding(LoanMasterTheme.components.cardRadius), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.AutoMirrored.Rounded.TrendingDown, contentDescription = null, tint = dangerRed, modifier = Modifier.size(32.dp))
                                Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.sm))
                                Column { Text("- ${com.loanmaster.pro.core.formatter.currentCurrencySymbol}5,000", color = textColor, style = LoanMasterTheme.typography.body, fontWeight = FontWeight.Bold); Text("Existing EMI", color = textSecondary, style = LoanMasterTheme.typography.body) }
                            }
                        }
                    }
                )
                Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.sm))
                AdaptiveRowCol(
                    modifier = Modifier.fillMaxWidth(),
                    content1 = { mod ->
                        Card(modifier = mod.clickable { 
                            viewModel.updateInputs(adjustTenureYears = 5.toDouble())
                        }, colors = CardDefaults.cardColors(containerColor = surfaceColor.copy(alpha = 0.5f)), shape = RoundedCornerShape(LoanMasterTheme.spacing.sm), border = androidx.compose.foundation.BorderStroke(1.dp, surfaceColor)) {
                            Row(modifier = Modifier.padding(LoanMasterTheme.components.cardRadius), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Rounded.CalendarMonth, contentDescription = null, tint = brightBlue, modifier = Modifier.size(32.dp))
                                Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.sm))
                                Column { Text("+5 Years", color = textColor, style = LoanMasterTheme.typography.body, fontWeight = FontWeight.Bold); Text("Tenure", color = textSecondary, style = LoanMasterTheme.typography.body) }
                            }
                        }
                    },
                    content2 = { mod ->
                        Card(modifier = mod.clickable { 
                            viewModel.updateInputs(creditScoreRange = "Excellent", defaultRate = profile.defaultRate)
                        }, colors = CardDefaults.cardColors(containerColor = surfaceColor.copy(alpha = 0.5f)), shape = RoundedCornerShape(LoanMasterTheme.spacing.sm), border = androidx.compose.foundation.BorderStroke(1.dp, surfaceColor)) {
                            Row(modifier = Modifier.padding(LoanMasterTheme.components.cardRadius), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Rounded.Percent, contentDescription = null, tint = brightBlue, modifier = Modifier.size(32.dp))
                                Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.sm))
                                Column { Text("Best Interest", color = textColor, style = LoanMasterTheme.typography.body, fontWeight = FontWeight.Bold); Text("Rate", color = textSecondary, style = LoanMasterTheme.typography.body) }
                            }
                        }
                    }
                )
            }

            // 10. Bottom Action Buttons
            AdaptiveRowCol(
                modifier = Modifier.fillMaxWidth(),
                content1 = { mod ->
                    OutlinedButton(
                        onClick = { },
                        modifier = mod.heightIn(min = 64.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = brightBlue),
                        border = androidx.compose.foundation.BorderStroke(1.dp, brightBlue),
                        shape = RoundedCornerShape(LoanMasterTheme.spacing.sm),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(Icons.Rounded.Description, contentDescription = null, modifier = Modifier.size(LoanMasterTheme.spacing.md))
                        Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.xs))
                        Text("Detailed Report", style = LoanMasterTheme.typography.label, fontWeight = FontWeight.Bold)
                    }
                },
                content2 = { mod ->
                    Button(
                        onClick = { },
                        modifier = mod.heightIn(min = 64.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9C27B0), contentColor = textColor),
                        shape = RoundedCornerShape(LoanMasterTheme.spacing.sm),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(Icons.Rounded.AccountBalance, contentDescription = null, modifier = Modifier.size(LoanMasterTheme.spacing.md))
                        Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.xs))
                        Text("Compare Banks", style = LoanMasterTheme.typography.label, fontWeight = FontWeight.Bold)
                    }
                }
            )
            AdaptiveRowCol(
                modifier = Modifier.fillMaxWidth(),
                content1 = { mod ->
                    Button(
                        onClick = { },
                        modifier = mod.heightIn(min = 64.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32), contentColor = textColor),
                        shape = RoundedCornerShape(LoanMasterTheme.spacing.sm),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(Icons.Rounded.Bookmark, contentDescription = null, modifier = Modifier.size(LoanMasterTheme.spacing.md))
                        Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.xs))
                        Text("Save Calculation", style = LoanMasterTheme.typography.label, fontWeight = FontWeight.Bold)
                    }
                },
                content2 = { mod ->
                    val context = androidx.compose.ui.platform.LocalContext.current
                    Button(
                        onClick = {
                            ExportUtils.exportToPdf(
                                context,
                                "Loan Eligibility Report",
                                listOf(
                                    "Monthly Income" to com.loanmaster.pro.core.formatter.formatMoney(totalIncome),
                                    "Other EMIs" to com.loanmaster.pro.core.formatter.formatMoney(totalExistingEmi),
                                    "Interest Rate" to "$interestRate%",
                                    "Loan Tenure" to "$tenureYears Years",
                                    "" to "",
                                    "Eligible Loan Amount" to com.loanmaster.pro.core.formatter.formatMoney(eligibleLoanAmount),
                                    "Eligible EMI" to com.loanmaster.pro.core.formatter.formatMoney(availableEmi.coerceAtLeast(0.0))
                                )
                            )
                        },
                        modifier = mod.heightIn(min = 64.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100), contentColor = textColor),
                        shape = RoundedCornerShape(LoanMasterTheme.spacing.sm),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(Icons.Rounded.PictureAsPdf, contentDescription = null, modifier = Modifier.size(LoanMasterTheme.spacing.md))
                        Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.xs))
                        Text("Export PDF", style = LoanMasterTheme.typography.label, fontWeight = FontWeight.Bold)
                    }
                }
            )
            Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.md))
            }
        }
    }
}

@Composable
fun AutoResizeTextField(
    isNumeric: Boolean = true,
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    PremiumInputField(
        label = label,
        value = value,
        onValueChange = onValueChange,
        icon = Icons.Rounded.Edit, // Default icon
        iconTint = Color(0xFF3B82F6),
        modifier = modifier,
        readOnly = readOnly,
        trailingContent = trailingIcon,
        isNumeric = isNumeric
    )
}

@Composable
fun AutoResizeHeroText(
    text: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    val initialFontSize = LoanMasterTheme.typography.display.fontSize
    var scaledFontSize by remember(text) { mutableStateOf(initialFontSize) }
    val minFontSize = LoanMasterTheme.typography.label.fontSize
    
    Text(
        text = text,
        color = color,
        fontSize = scaledFontSize,
        fontWeight = FontWeight.ExtraBold,
        maxLines = 1,
        softWrap = false,
        onTextLayout = { textLayoutResult ->
            if (textLayoutResult.hasVisualOverflow && scaledFontSize > minFontSize) {
                scaledFontSize = (scaledFontSize.value - 2f).sp
            }
        },
        modifier = modifier
    )
}
