package com.example

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
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

data class LoanProfile(val name: String, val baseFoir: Double, val defaultRate: String, val defaultTenure: String)

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
fun LoanEligibilityScreen() {
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val sizeClass = when {
        configuration.screenWidthDp < 600 -> WindowWidthSizeClass.Compact
        configuration.screenWidthDp < 840 -> WindowWidthSizeClass.Medium
        else -> WindowWidthSizeClass.Expanded
    }

    val bgColor = ResponsiveUtils.BgColor
    val surfaceColor = ResponsiveUtils.SurfaceColor
    val neonGreen = Color(0xFF4ADE80)
    val brightBlue = Color(0xFF3B82F6)
    val warningYellow = Color(0xFFFBBF24)
    val dangerRed = Color(0xFFEF4444)
    val textColor = ResponsiveUtils.TextPrimary
    val textSecondary = ResponsiveUtils.TextSecondary

    val formatMoney = { amount: Double ->
        val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
        format.maximumFractionDigits = 0
        format.format(amount)
    }

    var selectedLoanProfile by remember { mutableStateOf(loanProfiles[0]) }

    var monthlyIncome by remember { mutableStateOf("75000") }
    var existingEMIs by remember { mutableStateOf("12000") }
    var isCoBorrowerEnabled by remember { mutableStateOf(false) }
    var coBorrowerIncome by remember { mutableStateOf("45000") }
    var coBorrowerEMIs by remember { mutableStateOf("5000") }
    
    var tenureYears by remember { mutableStateOf(selectedLoanProfile.defaultTenure) }
    var interestRate by remember { mutableStateOf(selectedLoanProfile.defaultRate) }
    var isSalaried by remember { mutableStateOf(true) }
    var creditScoreRange by remember { mutableStateOf("Excellent") }
    
    LaunchedEffect(selectedLoanProfile) {
        tenureYears = selectedLoanProfile.defaultTenure
    }
    
    LaunchedEffect(selectedLoanProfile, creditScoreRange) {
        val baseRate = selectedLoanProfile.defaultRate.toDoubleOrNull() ?: 8.5
        val additionalRate = when(creditScoreRange) {
            "Excellent" -> 0.0
            "Good" -> 1.0
            else -> 2.0
        }
        interestRate = (baseRate + additionalRate).toString()
    }

    val income1 = monthlyIncome.toDoubleOrNull() ?: 0.0
    val emi1 = existingEMIs.toDoubleOrNull() ?: 0.0
    val income2 = if (isCoBorrowerEnabled) (coBorrowerIncome.toDoubleOrNull() ?: 0.0) else 0.0
    val emi2 = if (isCoBorrowerEnabled) (coBorrowerEMIs.toDoubleOrNull() ?: 0.0) else 0.0

    val totalIncome = income1 + income2
    val totalExistingEmi = emi1 + emi2

    val foirLimit = if (isSalaried) selectedLoanProfile.baseFoir else (selectedLoanProfile.baseFoir - 0.05)
    val maxAllowedEmi = totalIncome * foirLimit
    val availableEmi = maxAllowedEmi - totalExistingEmi

    val r = (interestRate.toDoubleOrNull() ?: 0.0) / 100 / 12
    val n = (tenureYears.toDoubleOrNull() ?: 0.0) * 12
    val eligibleLoanAmount = if (availableEmi > 0 && r > 0 && n > 0.0) {
        availableEmi * ((Math.pow(1 + r, n) - 1) / (r * Math.pow(1 + r, n)))
    } else 0.0
    val recommendedLoanAmount = eligibleLoanAmount * 0.80

    val currentFoir = if (totalIncome > 0) (totalExistingEmi / totalIncome) * 100 else 0.0

    Scaffold(
        containerColor = bgColor,
        bottomBar = {
            NavigationBar(
                containerColor = bgColor,
                contentColor = textSecondary,
                tonalElevation = 0.dp,
                modifier = Modifier.border(1.dp, surfaceColor)
            ) {
                NavigationBarItem(
                    selected = false,
                    onClick = { },
                    icon = { Icon(Icons.Rounded.Home, contentDescription = "Home") },
                    label = { Text("Home", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = textSecondary,
                        unselectedTextColor = textSecondary,
                        indicatorColor = Color.Transparent
                    )
                )
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Rounded.Calculate, contentDescription = "Calculate") },
                    label = { Text("Calculate", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = warningYellow,
                        selectedTextColor = warningYellow,
                        indicatorColor = Color.Transparent
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { },
                    icon = { Icon(Icons.Rounded.History, contentDescription = "History") },
                    label = { Text("History", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = textSecondary,
                        unselectedTextColor = textSecondary,
                        indicatorColor = Color.Transparent
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { },
                    icon = { Icon(Icons.Rounded.Article, contentDescription = "Reports") },
                    label = { Text("Reports", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = textSecondary,
                        unselectedTextColor = textSecondary,
                        indicatorColor = Color.Transparent
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { },
                    icon = { Icon(Icons.Rounded.Settings, contentDescription = "Settings") },
                    label = { Text("Settings", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = textSecondary,
                        unselectedTextColor = textSecondary,
                        indicatorColor = Color.Transparent
                    )
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(bgColor)
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(
                    horizontal = ResponsiveUtils.horizontalPadding(sizeClass),
                    vertical = ResponsiveUtils.verticalPadding(sizeClass)
                ),
            verticalArrangement = Arrangement.spacedBy(ResponsiveUtils.cardSpacing(sizeClass))
        ) {
            // 1. Top App Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Back",
                    tint = textColor,
                    modifier = Modifier.clickable { }
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Loan Eligibility Checker", color = textColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("Check how much loan you can get instantly", color = textSecondary, fontSize = 12.sp)
                }
                Icon(Icons.Rounded.StarBorder, contentDescription = "Star", tint = warningYellow, modifier = Modifier.size(24.dp).clickable { })
                Spacer(modifier = Modifier.width(16.dp))
                Icon(Icons.Rounded.Share, contentDescription = "Share", tint = textColor, modifier = Modifier.size(24.dp).clickable { })
            }

            // 2. Employment Type Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(surfaceColor)
                    .border(1.dp, surfaceColor, RoundedCornerShape(8.dp))
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSalaried) brightBlue.copy(alpha = 0.2f) else Color.Transparent)
                        .border(1.dp, if (isSalaried) brightBlue else Color.Transparent, RoundedCornerShape(8.dp))
                        .clickable { isSalaried = true },
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Work, contentDescription = null, tint = if (isSalaried) textColor else textSecondary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Salaried", color = if (isSalaried) textColor else textSecondary, fontSize = 14.sp, fontWeight = if (isSalaried) FontWeight.Bold else FontWeight.Normal)
                    }
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (!isSalaried) brightBlue.copy(alpha = 0.2f) else Color.Transparent)
                        .border(1.dp, if (!isSalaried) brightBlue else Color.Transparent, RoundedCornerShape(8.dp))
                        .clickable { isSalaried = false },
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Storefront, contentDescription = null, tint = if (!isSalaried) textColor else textSecondary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Self-Employed / Business", color = if (!isSalaried) textColor else textSecondary, fontSize = 12.sp, maxLines = 1, fontWeight = if (!isSalaried) FontWeight.Bold else FontWeight.Normal)
                    }
                }
            }

            // 3. Main Input Section
            ResponsiveRowCol(
                sizeClass = sizeClass,
                modifier = Modifier.fillMaxWidth(),
                content1 = { mod ->
                    AutoResizeTextField(
                        value = monthlyIncome,
                        onValueChange = { monthlyIncome = it },
                        label = "Monthly Income (₹)",
                        modifier = mod,
                        leadingIcon = { Icon(Icons.Rounded.AccountBalanceWallet, contentDescription = null, tint = brightBlue) }
                    )
                },
                content2 = { mod ->
                    AutoResizeTextField(
                        value = existingEMIs,
                        onValueChange = { existingEMIs = it },
                        label = "Existing EMIs (₹)",
                        modifier = mod,
                        leadingIcon = { Icon(Icons.Rounded.CreditCard, contentDescription = null, tint = brightBlue) }
                    )
                }
            )

            // 4. Co-Borrower Section (Animated)
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Add Co-Borrower", color = textColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text("Include spouse, parent or sibling income", color = textSecondary, fontSize = 12.sp)
                }
                Switch(
                    checked = isCoBorrowerEnabled,
                    onCheckedChange = { isCoBorrowerEnabled = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = textColor, checkedTrackColor = brightBlue)
                )
            }

            AnimatedVisibility(visible = isCoBorrowerEnabled) {
                ResponsiveRowCol(
                    sizeClass = sizeClass,
                    modifier = Modifier.fillMaxWidth(),
                    content1 = { mod ->
                        AutoResizeTextField(
                            value = coBorrowerIncome,
                            onValueChange = { coBorrowerIncome = it },
                            label = "Co-Borrower Income (₹)",
                            modifier = mod,
                            leadingIcon = { Icon(Icons.Rounded.Group, contentDescription = null, tint = brightBlue) }
                        )
                    },
                    content2 = { mod ->
                        AutoResizeTextField(
                            value = coBorrowerEMIs,
                            onValueChange = { coBorrowerEMIs = it },
                            label = "Co-Borrower EMIs (₹)",
                            modifier = mod,
                            leadingIcon = { Icon(Icons.Rounded.CreditCard, contentDescription = null, tint = brightBlue) }
                        )
                    }
                )
            }

            // 5. Loan Details & Credit Score
            var loanDropdownExpanded by remember { mutableStateOf(false) }
            ResponsiveRowCol(
                sizeClass = sizeClass,
                modifier = Modifier.fillMaxWidth(),
                content1 = { mod ->
                    ExposedDropdownMenuBox(
                        expanded = loanDropdownExpanded,
                        onExpandedChange = { loanDropdownExpanded = it },
                        modifier = mod
                    ) {
                        AutoResizeTextField(
                            value = selectedLoanProfile.name,
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
                                        selectedLoanProfile = profile
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
                        onValueChange = { tenureYears = it },
                        label = "Loan Tenure (Years)",
                        modifier = mod,
                        leadingIcon = { Icon(Icons.Rounded.Event, contentDescription = null, tint = brightBlue) }
                    )
                }
            )

            ResponsiveRowCol(
                sizeClass = sizeClass,
                modifier = Modifier.fillMaxWidth(),
                content1 = { mod ->
                    Column(modifier = mod) {
                        Text("Credit Score Range", color = textSecondary, fontSize = 12.sp, modifier = Modifier.padding(bottom = 8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth().height(64.dp).clip(RoundedCornerShape(12.dp)).border(1.dp, surfaceColor, RoundedCornerShape(12.dp))
                        ) {
                            listOf(Triple("Excellent", "750+", brightBlue), Triple("Good", "650 - 740", surfaceColor), Triple("Fair", "< 650", surfaceColor)).forEach { (title, range, color) ->
                                val isSelected = creditScoreRange == title
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .background(if (isSelected) brightBlue else surfaceColor.copy(alpha = 0.3f))
                                        .clickable { creditScoreRange = title },
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(title, color = textColor, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                                    Text(range, color = if (isSelected) textColor else textSecondary, fontSize = 10.sp)
                                }
                            }
                        }
                    }
                },
                content2 = { mod ->
                    Column(modifier = mod) {
                        AutoResizeTextField(
                            value = interestRate,
                            onValueChange = { interestRate = it },
                            label = "Interest Rate (%)",
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = { Icon(Icons.Rounded.Percent, contentDescription = null, tint = brightBlue) }
                        )
                        Text("Avg. rate for ${selectedLoanProfile.name}", color = textSecondary, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                    }
                }
            )

            // 6. FOIR Visual Gauge
            Card(
                colors = CardDefaults.cardColors(containerColor = surfaceColor.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, surfaceColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Your Debt-to-Income Ratio (FOIR)", color = textColor, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        Text("(Total EMIs / Total Income)", color = textSecondary, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Canvas(modifier = Modifier.fillMaxWidth().height(16.dp)) {
                            val w = size.width
                            val h = size.height
                            drawRoundRect(color = neonGreen, topLeft = Offset(0f, 0f), size = Size(w * 0.4f, h), cornerRadius = CornerRadius(h/2, h/2))
                            drawRect(color = warningYellow, topLeft = Offset(w * 0.4f, 0f), size = Size(w * 0.1f, h))
                            drawRoundRect(color = dangerRed, topLeft = Offset(w * 0.5f, 0f), size = Size(w * 0.5f, h), cornerRadius = CornerRadius(h/2, h/2))
                            
                            // Marker
                            val markerX = (currentFoir.toFloat() / 100f).coerceIn(0f, 1f) * w
                            drawCircle(color = textColor, radius = h*0.8f, center = Offset(markerX, h/2))
                            drawCircle(color = neonGreen, radius = h*0.5f, center = Offset(markerX, h/2))
                        }
                        
                        // Percentage label above marker
                        BoxWithConstraints(
                            modifier = Modifier
                                .fillMaxWidth()
                                .offset(y = (-24).dp)
                        ) {
                             val limit = (currentFoir.toFloat() / 100f).coerceIn(0f, 1f)
                             val offsetX = (maxWidth * limit) - 16.dp
                             Text(
                                 "${currentFoir.toInt()}%", 
                                 color = bgColor, 
                                 fontSize = 10.sp, 
                                 fontWeight = FontWeight.Bold,
                                 modifier = Modifier
                                    .offset(x = offsetX)
                                    .background(neonGreen, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                             )
                        }
                    }
                    
                    Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("0%", color = textSecondary, fontSize = 10.sp)
                        Text("20%", color = textSecondary, fontSize = 10.sp)
                        Text("40%", color = textSecondary, fontSize = 10.sp)
                        Text("50%", color = textSecondary, fontSize = 10.sp)
                        Text("100%", color = textSecondary, fontSize = 10.sp)
                    }

                    Row(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(neonGreen))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Low Risk (Safe)", color = textSecondary, fontSize = 12.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(warningYellow))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Moderate Risk", color = textSecondary, fontSize = 12.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(dangerRed))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("High Risk (Rejection)", color = textSecondary, fontSize = 12.sp)
                        }
                    }
                }
            }

            // 7. Hero Results Dashboard
            Card(
                colors = CardDefaults.cardColors(containerColor = surfaceColor),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, brightBlue.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Estimated Eligible Loan Amount", color = textColor, fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Rounded.Info, contentDescription = null, tint = textSecondary, modifier = Modifier.size(14.dp))
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            AutoResizeHeroText(
                                text = formatMoney(eligibleLoanAmount),
                                color = neonGreen
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            val isSafe = currentFoir <= (foirLimit * 100)
                            Row(
                                modifier = Modifier.clip(RoundedCornerShape(4.dp)).background((if(isSafe) neonGreen else dangerRed).copy(alpha = 0.1f)).border(1.dp, if(isSafe) neonGreen else dangerRed, RoundedCornerShape(4.dp)).padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Rounded.VerifiedUser, contentDescription = null, tint = if(isSafe) neonGreen else dangerRed, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if(isSafe) "You are in the Safe Zone" else "High Risk of Rejection", color = if(isSafe) neonGreen else dangerRed, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                        Box(modifier = Modifier.size(100.dp), contentAlignment = Alignment.Center) {
                             // Placeholder for 3D graphic
                             Icon(Icons.Rounded.AccountBalance, contentDescription = null, tint = brightBlue.copy(alpha = 0.8f), modifier = Modifier.size(64.dp))
                             Icon(Icons.Rounded.Star, contentDescription = null, tint = warningYellow, modifier = Modifier.size(16.dp).align(Alignment.TopStart).offset(x = (-8).dp, y = 8.dp))
                             Icon(Icons.Rounded.Flare, contentDescription = null, tint = neonGreen, modifier = Modifier.size(12.dp).align(Alignment.BottomEnd).offset(x = 8.dp, y = (-8).dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    HorizontalDivider(color = bgColor, thickness = 2.dp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column { Text("Total Monthly Income", color = textSecondary, fontSize = 10.sp); Text(formatMoney(totalIncome), color = textColor, fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                        Column { Text("Total Existing EMIs", color = textSecondary, fontSize = 10.sp); Text(formatMoney(totalExistingEmi), color = textColor, fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                        Column { Text("Available EMI Capacity", color = textSecondary, fontSize = 10.sp); Text(formatMoney(availableEmi.coerceAtLeast(0.0)), color = neonGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                        Column { Text("FOIR (Limit)", color = textSecondary, fontSize = 10.sp); Text("${(foirLimit * 100).toInt()}%", color = neonGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    }
                }
            }

            // 8. The Three Metric Cards
            ResponsiveRowCol3(
                sizeClass = sizeClass,
                modifier = Modifier.fillMaxWidth(),
                content1 = { mod ->
                    Card(
                        modifier = mod.height(110.dp),
                        colors = CardDefaults.cardColors(containerColor = surfaceColor.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, surfaceColor)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Approval Probability", color = textColor, fontSize = 10.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Rounded.Info, contentDescription = null, tint = textSecondary, modifier = Modifier.size(10.dp))
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                val approvalProb = (1.0f - (currentFoir.toFloat() / 100f)).coerceIn(0f, 1f)
                                Box(modifier = Modifier.size(40.dp), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(progress = { approvalProb }, color = neonGreen, trackColor = bgColor, gapSize = 0.dp, modifier = Modifier.size(40.dp))
                                    Text("${(approvalProb * 100).toInt()}%", color = textColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(if(approvalProb > 0.6) "High Chance" else "Low Chance", color = if(approvalProb > 0.6) neonGreen else dangerRed, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    Text(if(approvalProb > 0.6) "Excellent" else "Poor", color = textColor, fontSize = 10.sp)
                                }
                            }
                        }
                    }
                },
                content2 = { mod ->
                    Card(
                        modifier = mod.height(110.dp),
                        colors = CardDefaults.cardColors(containerColor = surfaceColor.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, surfaceColor)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Recommended Loan Amount", color = textColor, fontSize = 10.sp, maxLines = 1)
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            Text(formatMoney(recommendedLoanAmount), color = brightBlue, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false)
                            Text("For better financial stability", color = textSecondary, fontSize = 9.sp, lineHeight = 12.sp)
                        }
                    }
                },
                content3 = { mod ->
                    Card(
                        modifier = mod.height(110.dp),
                        colors = CardDefaults.cardColors(containerColor = surfaceColor.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, surfaceColor)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Max Affordable EMI", color = textColor, fontSize = 10.sp)
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(formatMoney(maxAllowedEmi), color = warningYellow, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text("/month", color = textColor, fontSize = 10.sp, modifier = Modifier.padding(bottom = 2.dp))
                            }
                            Text("Keep your FOIR under ${(foirLimit * 100).toInt()}%", color = textSecondary, fontSize = 9.sp, lineHeight = 12.sp)
                        }
                    }
                }
            )

            // 9. Interactive "What If?" Section
            Column {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text("What If?", color = textColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Adjust values and see impact on your eligibility", color = textSecondary, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.height(12.dp))
                ResponsiveRowCol(
                    sizeClass = sizeClass,
                    modifier = Modifier.fillMaxWidth(),
                    content1 = { mod ->
                        Card(modifier = mod.clickable { 
                            monthlyIncome = ((monthlyIncome.toDoubleOrNull() ?: 0.0) + 10000).toInt().toString()
                        }, colors = CardDefaults.cardColors(containerColor = surfaceColor.copy(alpha = 0.5f)), shape = RoundedCornerShape(8.dp), border = androidx.compose.foundation.BorderStroke(1.dp, surfaceColor)) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Rounded.TrendingUp, contentDescription = null, tint = neonGreen, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column { Text("+ ₹10,000", color = textColor, fontSize = 10.sp, fontWeight = FontWeight.Bold); Text("Income", color = textSecondary, fontSize = 10.sp) }
                            }
                        }
                    },
                    content2 = { mod ->
                        Card(modifier = mod.clickable { 
                            val newEmi = ((existingEMIs.toDoubleOrNull() ?: 0.0) - 5000)
                            existingEMIs = (if (newEmi > 0) newEmi else 0.0).toInt().toString()
                        }, colors = CardDefaults.cardColors(containerColor = surfaceColor.copy(alpha = 0.5f)), shape = RoundedCornerShape(8.dp), border = androidx.compose.foundation.BorderStroke(1.dp, surfaceColor)) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Rounded.TrendingDown, contentDescription = null, tint = dangerRed, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column { Text("- ₹5,000", color = textColor, fontSize = 10.sp, fontWeight = FontWeight.Bold); Text("Existing EMI", color = textSecondary, fontSize = 10.sp) }
                            }
                        }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                ResponsiveRowCol(
                    sizeClass = sizeClass,
                    modifier = Modifier.fillMaxWidth(),
                    content1 = { mod ->
                        Card(modifier = mod.clickable { 
                            tenureYears = ((tenureYears.toIntOrNull() ?: 0) + 5).toString()
                        }, colors = CardDefaults.cardColors(containerColor = surfaceColor.copy(alpha = 0.5f)), shape = RoundedCornerShape(8.dp), border = androidx.compose.foundation.BorderStroke(1.dp, surfaceColor)) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Rounded.CalendarMonth, contentDescription = null, tint = brightBlue, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column { Text("+5 Years", color = textColor, fontSize = 10.sp, fontWeight = FontWeight.Bold); Text("Tenure", color = textSecondary, fontSize = 10.sp) }
                            }
                        }
                    },
                    content2 = { mod ->
                        Card(modifier = mod.clickable { 
                            creditScoreRange = "Excellent" // just switch to Excellent to simulate drop
                        }, colors = CardDefaults.cardColors(containerColor = surfaceColor.copy(alpha = 0.5f)), shape = RoundedCornerShape(8.dp), border = androidx.compose.foundation.BorderStroke(1.dp, surfaceColor)) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Rounded.Percent, contentDescription = null, tint = brightBlue, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column { Text("Best Interest", color = textColor, fontSize = 10.sp, fontWeight = FontWeight.Bold); Text("Rate", color = textSecondary, fontSize = 10.sp) }
                            }
                        }
                    }
                )
            }

            // 10. Bottom Action Buttons
            ResponsiveRowCol(
                sizeClass = sizeClass,
                modifier = Modifier.fillMaxWidth(),
                content1 = { mod ->
                    OutlinedButton(
                        onClick = { },
                        modifier = mod.height(48.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = brightBlue),
                        border = androidx.compose.foundation.BorderStroke(1.dp, brightBlue),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(Icons.Rounded.Description, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Detailed Report", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                },
                content2 = { mod ->
                    Button(
                        onClick = { },
                        modifier = mod.height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9C27B0), contentColor = textColor),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(Icons.Rounded.AccountBalance, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Compare Banks", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            )
            ResponsiveRowCol(
                sizeClass = sizeClass,
                modifier = Modifier.fillMaxWidth(),
                content1 = { mod ->
                    Button(
                        onClick = { },
                        modifier = mod.height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32), contentColor = textColor),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(Icons.Rounded.Bookmark, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Save Calculation", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                },
                content2 = { mod ->
                    Button(
                        onClick = { },
                        modifier = mod.height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100), contentColor = textColor),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(Icons.Rounded.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Share", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun AutoResizeTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    val inputLength = value.length
    val scaledFontSize = when {
        inputLength >= 12 -> 12.sp
        inputLength >= 9 -> 14.sp
        else -> 16.sp
    }
    
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        readOnly = readOnly,
        label = { Text(label, color = Color.Gray, fontSize = 10.sp, maxLines = 1, softWrap = false) },
        modifier = modifier.height(64.dp),
        singleLine = true,
        maxLines = 1,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        textStyle = LocalTextStyle.current.copy(fontSize = scaledFontSize, color = Color.White),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = Color(0xFF3B82F6),
            unfocusedBorderColor = Color(0xFF152238),
            focusedContainerColor = Color(0xFF152238).copy(alpha = 0.5f),
            unfocusedContainerColor = Color(0xFF152238).copy(alpha = 0.5f)
        ),
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
fun ResponsiveRowCol3(
    sizeClass: WindowWidthSizeClass,
    modifier: Modifier = Modifier,
    content1: @Composable (Modifier) -> Unit,
    content2: @Composable (Modifier) -> Unit,
    content3: @Composable (Modifier) -> Unit
) {
    if (sizeClass == WindowWidthSizeClass.Compact) {
        Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            content1(Modifier.fillMaxWidth())
            content2(Modifier.fillMaxWidth())
            content3(Modifier.fillMaxWidth())
        }
    } else {
        Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            content1(Modifier.weight(1f))
            content2(Modifier.weight(1f))
            content3(Modifier.weight(1f))
        }
    }
}

@Composable
fun ResponsiveRowCol(sizeClass: WindowWidthSizeClass, modifier: Modifier = Modifier, content1: @Composable (Modifier) -> Unit, content2: @Composable (Modifier) -> Unit) {
    if (sizeClass == WindowWidthSizeClass.Compact) {
        Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
            content1(Modifier.fillMaxWidth())
            content2(Modifier.fillMaxWidth())
        }
    } else {
        Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            content1(Modifier.weight(1f))
            content2(Modifier.weight(1f))
        }
    }
}

@Composable
fun AutoResizeHeroText(
    text: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    var scaledFontSize by remember(text) { mutableStateOf(36.sp) }
    
    Text(
        text = text,
        color = color,
        fontSize = scaledFontSize,
        fontWeight = FontWeight.ExtraBold,
        maxLines = 1,
        softWrap = false,
        onTextLayout = { textLayoutResult ->
            if (textLayoutResult.hasVisualOverflow && scaledFontSize > 12.sp) {
                scaledFontSize = (scaledFontSize.value - 2f).sp
            }
        },
        modifier = modifier
    )
}
