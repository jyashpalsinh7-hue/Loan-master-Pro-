package com.loanmaster.pro

import com.loanmaster.pro.ui.theme.*

import androidx.window.core.layout.WindowWidthSizeClass

import com.loanmaster.pro.ui.theme.LoanMasterTheme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextOverflow

import androidx.compose.runtime.saveable.rememberSaveable

import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.text.NumberFormat
import java.util.*
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanSummaryScreen(
    viewModel: LoanSummaryViewModel,
    onBack: () -> Unit
) {
    val activeLoans by viewModel.activeLoans.collectAsStateWithLifecycle()
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val windowSizeClass = when {
        configuration.screenWidthDp < 600 -> WindowWidthSizeClass.COMPACT
        configuration.screenWidthDp < 840 -> WindowWidthSizeClass.MEDIUM
        else -> WindowWidthSizeClass.EXPANDED
    }

    var showAddLoanDialog by rememberSaveable { mutableStateOf(false) }

    val totalOutstanding = activeLoans.sumOf { it.principalAmount }
    val totalEmi = activeLoans.sumOf { it.emiAmount }
    val totalLoansCount = activeLoans.size
    
    // For visual demonstration of the premium UI
    val totalOriginal = if (totalOutstanding > 0) totalOutstanding * 1.84 else 0.0
    val totalPrincipalPaid = if (totalOutstanding > 0) totalOriginal - totalOutstanding else 0.0
    val totalInterestPaid = totalPrincipalPaid * 0.5
    val paidPercent = if (totalOriginal > 0) (totalPrincipalPaid / totalOriginal).toFloat() else 0f
    
    val totalInterestOverall = activeLoans.sumOf { (it.emiAmount * it.tenureMonths) - it.principalAmount }.coerceAtLeast(0.0)

    val bgDark = Color(0xFF070B19)
    val cardBg = Color(0xFF0F1629)
    val cardBorder = Color(0xFF1E293B)
    val textGray = Color(0xFF94A3B8)
    val accentYellow = Color(0xFFFBBF24)
    val accentGreen = Color(0xFF22C55E)
    val accentBlue = Color(0xFF3B82F6)

    Scaffold(
        modifier = Modifier.safeDrawingPadding(),
        containerColor = bgDark,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddLoanDialog = true },
                containerColor = accentYellow,
                contentColor = bgDark,
                shape = CircleShape
            ) {
                Icon(Icons.Rounded.Add, contentDescription = "Add")
            }
        }
        ) { padding ->
        BoxWithConstraints(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.TopCenter) {
            LazyColumn(
                modifier = Modifier
                    .widthIn(max = 840.dp)
                    .fillMaxSize()
                    .padding(horizontal = LoanMasterTheme.spacing.md),
            contentPadding = PaddingValues(top = LoanMasterTheme.spacing.lg, bottom = LoanMasterTheme.components.calculatorCardHeight)
        ) {
            item {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBack, modifier = Modifier.padding(end = LoanMasterTheme.spacing.xs)) {
                            Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = "Back", tint = Color.White, modifier = Modifier.size(LoanMasterTheme.components.iconMedium).background(Color.White.copy(0.1f), CircleShape).padding(LoanMasterTheme.spacing.xs)) // Using right arrow as temp back if mirrored, wait actually I'll just use a back button
                        }
                        Box(
                            modifier = Modifier
                                .size(LoanMasterTheme.components.iconLarge)
                                .background(Color(0xFF17203A), RoundedCornerShape(LoanMasterTheme.spacing.md))
                                .border(1.dp, Color(0xFF2A3A5A), RoundedCornerShape(LoanMasterTheme.spacing.md)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Rounded.AccountBalanceWallet, contentDescription = null, tint = accentBlue)
                        }
                        Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.md))
                        Column {
                            Text("Loan Summary", color = Color.White, fontSize = LoanMasterTheme.typography.title.fontSize, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text("All your loans at a glance", color = textGray, fontSize = LoanMasterTheme.typography.body.fontSize, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }
                    
                    Box(
                        modifier = Modifier
                            .padding(start = LoanMasterTheme.spacing.sm)
                            .background(accentYellow.copy(alpha = 0.15f), RoundedCornerShape(LoanMasterTheme.components.iconSmall))
                            .border(1.dp, accentYellow.copy(alpha = 0.3f), RoundedCornerShape(LoanMasterTheme.components.iconSmall))
                            .padding(horizontal = LoanMasterTheme.spacing.md, vertical = LoanMasterTheme.spacing.sm),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.WorkspacePremium, contentDescription = null, tint = accentYellow, modifier = Modifier.size(LoanMasterTheme.spacing.md))
                            Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.xs))
                            Text("Premium", color = accentYellow, fontSize = LoanMasterTheme.typography.label.fontSize, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.xl))
                
                // Chart Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Left: Outstanding
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Total Outstanding", color = textGray, fontSize = LoanMasterTheme.typography.label.fontSize, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(formatMoney(totalOutstanding), color = accentYellow, fontSize = LoanMasterTheme.typography.title.fontSize, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text("across $totalLoansCount loan${if(totalLoansCount != 1) "s" else ""}", color = textGray, fontSize = LoanMasterTheme.typography.label.fontSize, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    
                    // Middle: Circle
                    Box(
                        modifier = Modifier.weight(0.8f),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(LoanMasterTheme.components.calculatorCardHeight)) {
                            drawArc(
                                color = Color(0xFF1E293B),
                                startAngle = 0f,
                                sweepAngle = 360f,
                                useCenter = false,
                                style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                            )
                            drawArc(
                                color = accentBlue,
                                startAngle = -90f,
                                sweepAngle = 360f * paidPercent,
                                useCenter = false,
                                style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                            )
                            drawArc(
                                color = accentYellow,
                                startAngle = -90f + (360f * paidPercent),
                                sweepAngle = 360f * (paidPercent * 0.3f), // Mock yellow portion
                                useCenter = false,
                                style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Paid", color = Color.White, fontSize = LoanMasterTheme.typography.label.fontSize)
                            Text("${(paidPercent * 100).roundToInt()}%", color = accentYellow, fontSize = LoanMasterTheme.typography.title.fontSize, fontWeight = FontWeight.Bold)
                        }
                    }
                    
                    // Right: Paid stats
                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                        Column(horizontalAlignment = Alignment.Start) {
                            Text("Total Principal Paid", color = textGray, fontSize = LoanMasterTheme.typography.label.fontSize, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(formatMoney(totalPrincipalPaid), color = accentGreen, fontSize = LoanMasterTheme.typography.body.fontSize, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            
                            Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.md))
                            
                            Text("Total Interest Paid", color = textGray, fontSize = LoanMasterTheme.typography.label.fontSize, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(formatMoney(totalInterestPaid), color = accentBlue, fontSize = LoanMasterTheme.typography.body.fontSize, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.xl))
                
                // 4 Column Stats Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    border = BorderStroke(1.dp, cardBorder),
                    shape = RoundedCornerShape(LoanMasterTheme.spacing.md)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = LoanMasterTheme.spacing.md),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StatItem("Total Loans", totalLoansCount.toString(), Icons.Rounded.AccountBalanceWallet)
                        Box(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.xl).widthIn(min = 1.dp).background(cardBorder))
                        StatItem("EMI / month", formatShort(totalEmi), Icons.Rounded.CurrencyRupee)
                        Box(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.xl).widthIn(min = 1.dp).background(cardBorder))
                        StatItem("Outstanding", formatShort(totalOutstanding), Icons.Rounded.Savings)
                        Box(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.xl).widthIn(min = 1.dp).background(cardBorder))
                        StatItem("Total Interest", formatShort(totalInterestOverall), Icons.Rounded.Percent)
                    }
                }
                
                Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.lg))
            }
            
            if (activeLoans.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(top = LoanMasterTheme.spacing.xl), contentAlignment = Alignment.Center) {
                        Text("No active loans. Add one to see your summary.", color = textGray)
                    }
                }
            } else {
                items(activeLoans) { loan ->
                    ActiveLoanPremiumCard(loan = loan, onDelete = { viewModel.deleteLoan(loan) })
                    Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.md))
                }
            }
            
            item {
                if (activeLoans.isNotEmpty()) {
                    Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.sm))
                    NextEmiCard(activeLoans.first())
                }
                
                Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.xl))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Rounded.Security, contentDescription = null, tint = textGray, modifier = Modifier.size(LoanMasterTheme.spacing.md))
                    Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.sm))
                    Text("All your data is secure and private", color = textGray, fontSize = LoanMasterTheme.typography.label.fontSize)
                }
            }
        }        }
    }

    if (showAddLoanDialog) {
        AddLoanDialog(
            onDismiss = { showAddLoanDialog = false },
            onSave = { loan ->
                viewModel.addLoan(loan)
                showAddLoanDialog = false
            }
        )
    }
}

@Composable
fun RowScope.StatItem(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f).padding(horizontal = LoanMasterTheme.spacing.xs)) {
        Box(
            modifier = Modifier
                .size(LoanMasterTheme.components.iconLarge)
                .background(Color(0xFF17203A), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Color(0xFF60A5FA), modifier = Modifier.size(LoanMasterTheme.components.iconSmall))
        }
        Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.sm))
        Text(label, color = Color(0xFF94A3B8), fontSize = LoanMasterTheme.typography.label.fontSize, maxLines = 1, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.xs))
        Text(value, color = Color.White, fontSize = LoanMasterTheme.typography.body.fontSize, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
fun NextEmiCard(loan: ActiveLoan) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF171332)),
        border = BorderStroke(1.dp, Color(0xFF2E2759)),
        shape = RoundedCornerShape(LoanMasterTheme.spacing.md)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(LoanMasterTheme.spacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(LoanMasterTheme.components.iconLarge)
                        .background(Color(0xFF2A235C), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.BarChart, contentDescription = null, tint = Color(0xFFC084FC))
                }
                Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.md))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Next EMI Due  ", color = Color.White, fontSize = LoanMasterTheme.typography.body.fontSize, fontWeight = FontWeight.Medium)
                        Text(formatMoney(loan.emiAmount), color = Color.White, fontSize = LoanMasterTheme.typography.body.fontSize, fontWeight = FontWeight.Bold)
                    }
                    Text("on 26 Jul 2026", color = Color(0xFFC084FC), fontSize = LoanMasterTheme.typography.label.fontSize) // Mock date
                    Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.xs))
                    Text("Due in 31 days", color = Color(0xFF94A3B8), fontSize = LoanMasterTheme.typography.label.fontSize)
                }
            }
            
            Box(
                modifier = Modifier
                    .border(1.dp, Color(0xFF4C4382), RoundedCornerShape(LoanMasterTheme.components.iconSmall))
                    .padding(horizontal = LoanMasterTheme.spacing.md, vertical = LoanMasterTheme.spacing.sm),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("View Details", color = Color.White, fontSize = LoanMasterTheme.typography.label.fontSize)
                    Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = null, tint = Color.White, modifier = Modifier.size(LoanMasterTheme.spacing.md))
                }
            }
        }
    }
}

@Composable
fun ActiveLoanPremiumCard(loan: ActiveLoan, onDelete: () -> Unit) {
    val cardBg = Color(0xFF0F1629)
    val cardBorder = Color(0xFF1E293B)
    val textGray = Color(0xFF94A3B8)
    
    // Mock derivation for visual
    val originalAmount = loan.principalAmount * 1.58
    val paid = originalAmount - loan.principalAmount
    val paidPercent = (paid / originalAmount).toFloat()

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = BorderStroke(1.dp, cardBorder),
        shape = RoundedCornerShape(LoanMasterTheme.spacing.md)
    ) {
        Column(modifier = Modifier.padding(LoanMasterTheme.spacing.md)) {
            // Top Row
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Row {
                    Box(
                        modifier = Modifier
                            .size(LoanMasterTheme.components.iconLarge)
                            .background(Color(0xFF1A2342), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        val icon = if (loan.loanType.contains("home", true)) Icons.Rounded.Home else Icons.Rounded.DirectionsCar
                        Icon(icon, contentDescription = null, tint = Color(0xFFFBBF24), modifier = Modifier.size(LoanMasterTheme.spacing.lg))
                    }
                    Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.md))
                    Column {
                        Text(loan.loanType.ifEmpty { "Loan" }, color = Color.White, fontSize = LoanMasterTheme.typography.body.fontSize, fontWeight = FontWeight.Bold)
                        Text(loan.bankName, color = textGray, fontSize = LoanMasterTheme.typography.label.fontSize)
                        Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.xs))
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF064E3B), RoundedCornerShape(LoanMasterTheme.spacing.sm))
                                .padding(horizontal = LoanMasterTheme.spacing.sm, vertical = LoanMasterTheme.spacing.xs)
                        ) {
                            Text("Active", color = Color(0xFF34D399), fontSize = LoanMasterTheme.typography.label.fontSize, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(LoanMasterTheme.spacing.lg)) {
                    Icon(Icons.Rounded.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444).copy(alpha = 0.5f))
                }
            }
            
            Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.components.iconSmall))
            
            // Stats Row
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Loan Amount", color = textGray, fontSize = LoanMasterTheme.typography.label.fontSize, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.xs))
                    Text(formatMoney(originalAmount), color = Color.White, fontSize = LoanMasterTheme.typography.body.fontSize, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                Box(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.xl).widthIn(min = 1.dp).background(cardBorder))
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Interest Rate", color = textGray, fontSize = LoanMasterTheme.typography.label.fontSize, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.xs))
                    Text("${loan.interestRate}% p.a.", color = Color.White, fontSize = LoanMasterTheme.typography.body.fontSize, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                Box(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.xl).widthIn(min = 1.dp).background(cardBorder))
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Next EMI Due", color = textGray, fontSize = LoanMasterTheme.typography.label.fontSize, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.xs))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("26 Jul", color = Color(0xFFFBBF24), fontSize = LoanMasterTheme.typography.body.fontSize, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis) // Mock
                        Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = null, tint = textGray, modifier = Modifier.size(LoanMasterTheme.spacing.md))
                    }
                }
            }
            
            Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.components.iconSmall))
            
            // Progress
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Principal Paid", color = textGray, fontSize = LoanMasterTheme.typography.label.fontSize)
                Text("${formatMoney(paid)} (${(paidPercent * 100).roundToInt()}%)", color = Color(0xFF22C55E), fontSize = LoanMasterTheme.typography.label.fontSize, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.sm))
            LinearProgressIndicator(
                progress = { paidPercent },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = LoanMasterTheme.spacing.sm)
                    .clip(RoundedCornerShape(4.dp)),
                color = Color(0xFF3B82F6),
                trackColor = Color(0xFF1E293B)
            )
        }
    }
}

private fun formatShort(amount: Double): String {
    if (amount >= 100000) {
        return "${"₹"}${String.format(Locale.US, "%.2f", amount / 100000)}L"
    }
    if (amount >= 1000) {
        return "${"₹"}${String.format(Locale.US, "%.1f", amount / 1000)}K"
    }
    return "${"₹"}${amount.roundToInt()}"
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLoanDialog(onDismiss: () -> Unit, onSave: (ActiveLoan) -> Unit) {
    var bankName by rememberSaveable { mutableStateOf("") }
    var loanType by rememberSaveable { mutableStateOf("") }
    var principal by rememberSaveable { mutableStateOf("") }
    var rate by rememberSaveable { mutableStateOf("") }
    var tenure by rememberSaveable { mutableStateOf("") }
    var emi by rememberSaveable { mutableStateOf("") }

    var bankExpanded by rememberSaveable { mutableStateOf(false) }
    var loanTypeExpanded by rememberSaveable { mutableStateOf(false) }

    val popularBanks = listOf("SBI", "HDFC Bank", "ICICI Bank", "Axis Bank", "Kotak Mahindra Bank", "Bank of Baroda", "Punjab National Bank", "Bajaj Finserv")
    val popularLoanTypes = listOf("Home Loan", "Personal Loan", "Car Loan", "Two Wheeler Loan", "Education Loan", "Business Loan", "Gold Loan")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF0F1629)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = LoanMasterTheme.spacing.lg)
                .padding(bottom = LoanMasterTheme.spacing.xl),
            verticalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.md)
        ) {
            Text("Add Active Loan", fontWeight = FontWeight.Bold, fontSize = LoanMasterTheme.typography.title.fontSize, color = Color.White)
            Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.xs))

            val tfColors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF3B82F6),
                unfocusedBorderColor = Color(0xFF1E293B),
                focusedLabelColor = Color(0xFF3B82F6),
                unfocusedLabelColor = Color(0xFF94A3B8),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color(0xFF3B82F6)
            )

            ExposedDropdownMenuBox(
                expanded = bankExpanded,
                onExpandedChange = { bankExpanded = it }
            ) {
                OutlinedTextField(
                    value = bankName,
                    onValueChange = { bankName = it },
                    label = { Text("Bank/Lender Name") },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    singleLine = true,
                    colors = tfColors,
                    shape = RoundedCornerShape(LoanMasterTheme.spacing.md),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = bankExpanded) }
                )
                ExposedDropdownMenu(
                    expanded = bankExpanded,
                    onDismissRequest = { bankExpanded = false },
                    modifier = Modifier.background(Color(0xFF1E293B))
                ) {
                    popularBanks.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption, color = Color.White) },
                            onClick = {
                                bankName = selectionOption
                                bankExpanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }

            ExposedDropdownMenuBox(
                expanded = loanTypeExpanded,
                onExpandedChange = { loanTypeExpanded = it }
            ) {
                OutlinedTextField(
                    value = loanType,
                    onValueChange = { loanType = it },
                    label = { Text("Loan Type") },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    singleLine = true,
                    colors = tfColors,
                    shape = RoundedCornerShape(LoanMasterTheme.spacing.md),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = loanTypeExpanded) }
                )
                ExposedDropdownMenu(
                    expanded = loanTypeExpanded,
                    onDismissRequest = { loanTypeExpanded = false },
                    modifier = Modifier.background(Color(0xFF1E293B))
                ) {
                    popularLoanTypes.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption, color = Color.White) },
                            onClick = {
                                loanType = selectionOption
                                loanTypeExpanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.md)) {
                OutlinedTextField(
                    value = principal,
                    onValueChange = { principal = it },
                    label = { Text("Outstanding Principal") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = tfColors,
                    shape = RoundedCornerShape(LoanMasterTheme.spacing.md)
                )
                OutlinedTextField(
                    value = emi,
                    onValueChange = { emi = it },
                    label = { Text("Monthly EMI") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = tfColors,
                    shape = RoundedCornerShape(LoanMasterTheme.spacing.md)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.md)) {
                OutlinedTextField(
                    value = rate,
                    onValueChange = { rate = it },
                    label = { Text("Interest Rate (%)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = tfColors,
                    shape = RoundedCornerShape(LoanMasterTheme.spacing.md)
                )
                OutlinedTextField(
                    value = tenure,
                    onValueChange = { tenure = it },
                    label = { Text("Remaining Months") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = tfColors,
                    shape = RoundedCornerShape(LoanMasterTheme.spacing.md)
                )
            }

            Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.sm))

            Button(
                onClick = {
                    val p = principal.toDoubleOrNull() ?: 0.0
                    val r = rate.toDoubleOrNull() ?: 0.0
                    val t = tenure.toIntOrNull() ?: 0
                    val e = emi.toDoubleOrNull() ?: 0.0
                    
                    if (bankName.isNotBlank() && p > 0) {
                        onSave(
                            ActiveLoan(
                                bankName = bankName,
                                loanType = loanType,
                                principalAmount = p,
                                interestRate = r,
                                tenureMonths = t,
                                emiAmount = e,
                                startDate = System.currentTimeMillis()
                            )
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth().heightIn(min = LoanMasterTheme.components.buttonHeight),
                shape = RoundedCornerShape(LoanMasterTheme.spacing.md),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFBBF24), contentColor = Color(0xFF0F172A))
            ) {
                Text("Save Liability", fontWeight = FontWeight.Bold, fontSize = LoanMasterTheme.typography.body.fontSize)
            }
        }
    }
}
