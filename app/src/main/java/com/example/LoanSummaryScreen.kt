package com.example

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
        configuration.screenWidthDp < 600 -> WindowWidthSizeClass.Compact
        configuration.screenWidthDp < 840 -> WindowWidthSizeClass.Medium
        else -> WindowWidthSizeClass.Expanded
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
                    .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 24.dp, bottom = 100.dp)
        ) {
            item {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBack, modifier = Modifier.padding(end = 4.dp)) {
                            Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = "Back", tint = Color.White, modifier = Modifier.size(28.dp).background(Color.White.copy(0.1f), CircleShape).padding(4.dp)) // Using right arrow as temp back if mirrored, wait actually I'll just use a back button
                        }
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color(0xFF17203A), RoundedCornerShape(12.dp))
                                .border(1.dp, Color(0xFF2A3A5A), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Rounded.AccountBalanceWallet, contentDescription = null, tint = accentBlue)
                        }
                        Spacer(modifier = Modifier.widthIn(min = 12.dp))
                        Column {
                            Text("Loan Summary", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                            Text("All your loans at a glance", color = textGray, fontSize = 14.sp)
                        }
                    }
                    
                    Box(
                        modifier = Modifier
                            .background(accentYellow.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                            .border(1.dp, accentYellow.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.WorkspacePremium, contentDescription = null, tint = accentYellow, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.widthIn(min = 4.dp))
                            Text("Premium", color = accentYellow, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.heightIn(min = 32.dp))
                
                // Chart Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Left: Outstanding
                    Column(modifier = Modifier.weight(1.2f)) {
                        Text("Total Outstanding", color = textGray, fontSize = 13.sp)
                        Text(formatMoney(totalOutstanding), color = accentYellow, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                        Text("across $totalLoansCount loan${if(totalLoansCount != 1) "s" else ""}", color = textGray, fontSize = 12.sp)
                    }
                    
                    // Middle: Circle
                    Box(
                        modifier = Modifier.size(100.dp).weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(100.dp)) {
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
                            Text("Paid", color = Color.White, fontSize = 12.sp)
                            Text("${(paidPercent * 100).roundToInt()}%", color = accentYellow, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    
                    // Right: Paid stats
                    Column(modifier = Modifier.weight(1.2f), horizontalAlignment = Alignment.End) {
                        Column(horizontalAlignment = Alignment.Start) {
                            Text("Total Principal Paid", color = textGray, fontSize = 12.sp)
                            Text(formatMoney(totalPrincipalPaid), color = accentGreen, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            
                            Spacer(modifier = Modifier.heightIn(min = 12.dp))
                            
                            Text("Total Interest Paid", color = textGray, fontSize = 12.sp)
                            Text(formatMoney(totalInterestPaid), color = accentBlue, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.heightIn(min = 32.dp))
                
                // 4 Column Stats Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    border = BorderStroke(1.dp, cardBorder),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StatItem("Total Loans", totalLoansCount.toString(), Icons.Rounded.AccountBalanceWallet)
                        Box(modifier = Modifier.heightIn(min = 40.dp).widthIn(min = 1.dp).background(cardBorder))
                        StatItem("EMI / month", formatShort(totalEmi), Icons.Rounded.CurrencyRupee)
                        Box(modifier = Modifier.heightIn(min = 40.dp).widthIn(min = 1.dp).background(cardBorder))
                        StatItem("Outstanding", formatShort(totalOutstanding), Icons.Rounded.Savings)
                        Box(modifier = Modifier.heightIn(min = 40.dp).widthIn(min = 1.dp).background(cardBorder))
                        StatItem("Total Interest", formatShort(totalInterestOverall), Icons.Rounded.Percent)
                    }
                }
                
                Spacer(modifier = Modifier.heightIn(min = 24.dp))
            }
            
            if (activeLoans.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                        Text("No active loans. Add one to see your summary.", color = textGray)
                    }
                }
            } else {
                items(activeLoans) { loan ->
                    ActiveLoanPremiumCard(loan = loan, onDelete = { viewModel.deleteLoan(loan) })
                    Spacer(modifier = Modifier.heightIn(min = 16.dp))
                }
            }
            
            item {
                if (activeLoans.isNotEmpty()) {
                    Spacer(modifier = Modifier.heightIn(min = 8.dp))
                    NextEmiCard(activeLoans.first())
                }
                
                Spacer(modifier = Modifier.heightIn(min = 32.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Rounded.Security, contentDescription = null, tint = textGray, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.widthIn(min = 6.dp))
                    Text("All your data is secure and private", color = textGray, fontSize = 12.sp)
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
fun StatItem(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(horizontal = 4.dp)) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(Color(0xFF17203A), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Color(0xFF60A5FA), modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.heightIn(min = 8.dp))
        Text(label, color = Color(0xFF94A3B8), fontSize = 11.sp)
        Spacer(modifier = Modifier.heightIn(min = 2.dp))
        Text(value, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun NextEmiCard(loan: ActiveLoan) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF171332)),
        border = BorderStroke(1.dp, Color(0xFF2E2759)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(0xFF2A235C), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.BarChart, contentDescription = null, tint = Color(0xFFC084FC))
                }
                Spacer(modifier = Modifier.widthIn(min = 16.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Next EMI Due  ", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                        Text(formatMoney(loan.emiAmount), color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                    Text("on 26 Jul 2026", color = Color(0xFFC084FC), fontSize = 13.sp) // Mock date
                    Spacer(modifier = Modifier.heightIn(min = 2.dp))
                    Text("Due in 31 days", color = Color(0xFF94A3B8), fontSize = 12.sp)
                }
            }
            
            Box(
                modifier = Modifier
                    .border(1.dp, Color(0xFF4C4382), RoundedCornerShape(20.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("View Details", color = Color.White, fontSize = 12.sp)
                    Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
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
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top Row
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Row {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color(0xFF1A2342), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        val icon = if (loan.loanType.contains("home", true)) Icons.Rounded.Home else Icons.Rounded.DirectionsCar
                        Icon(icon, contentDescription = null, tint = Color(0xFFFBBF24), modifier = Modifier.size(24.dp))
                    }
                    Spacer(modifier = Modifier.widthIn(min = 12.dp))
                    Column {
                        Text(loan.loanType.ifEmpty { "Loan" }, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text(loan.bankName, color = textGray, fontSize = 13.sp)
                        Spacer(modifier = Modifier.heightIn(min = 4.dp))
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF064E3B), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("Active", color = Color(0xFF34D399), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Rounded.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444).copy(alpha = 0.5f))
                }
            }
            
            Spacer(modifier = Modifier.heightIn(min = 20.dp))
            
            // Stats Row
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Loan Amount", color = textGray, fontSize = 12.sp)
                    Spacer(modifier = Modifier.heightIn(min = 4.dp))
                    Text(formatMoney(originalAmount), color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
                HorizontalDivider(modifier = Modifier.heightIn(min = 30.dp).widthIn(min = 1.dp), color = cardBorder)
                Column {
                    Text("Interest Rate", color = textGray, fontSize = 12.sp)
                    Spacer(modifier = Modifier.heightIn(min = 4.dp))
                    Text("${loan.interestRate}% p.a.", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
                HorizontalDivider(modifier = Modifier.heightIn(min = 30.dp).widthIn(min = 1.dp), color = cardBorder)
                Column {
                    Text("Next EMI Due", color = textGray, fontSize = 12.sp)
                    Spacer(modifier = Modifier.heightIn(min = 4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("26 Jul 2026", color = Color(0xFFFBBF24), fontSize = 15.sp, fontWeight = FontWeight.Bold) // Mock
                        Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = null, tint = textGray, modifier = Modifier.size(16.dp))
                    }
                }
            }
            
            Spacer(modifier = Modifier.heightIn(min = 20.dp))
            
            // Progress
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Principal Paid", color = textGray, fontSize = 12.sp)
                Text("${formatMoney(paid)} (${(paidPercent * 100).roundToInt()}%)", color = Color(0xFF22C55E), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.heightIn(min = 8.dp))
            LinearProgressIndicator(
                progress = { paidPercent },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = Color(0xFF3B82F6),
                trackColor = Color(0xFF1E293B)
            )
        }
    }
}

private fun formatShort(amount: Double): String {
    if (amount >= 100000) {
        return "${globalCurrencySymbol}${String.format(Locale.US, "%.2f", amount / 100000)}L"
    }
    if (amount >= 1000) {
        return "${globalCurrencySymbol}${String.format(Locale.US, "%.1f", amount / 1000)}K"
    }
    return "${globalCurrencySymbol}${amount.roundToInt()}"
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
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Add Active Loan", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Color.White)
            Spacer(modifier = Modifier.heightIn(min = 4.dp))

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
                    shape = RoundedCornerShape(12.dp),
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
                    shape = RoundedCornerShape(12.dp),
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

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = principal,
                    onValueChange = { principal = it },
                    label = { Text("Outstanding Principal") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = tfColors,
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = emi,
                    onValueChange = { emi = it },
                    label = { Text("Monthly EMI") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = tfColors,
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = rate,
                    onValueChange = { rate = it },
                    label = { Text("Interest Rate (%)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = tfColors,
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = tenure,
                    onValueChange = { tenure = it },
                    label = { Text("Remaining Months") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = tfColors,
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Spacer(modifier = Modifier.heightIn(min = 8.dp))

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
                modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFBBF24), contentColor = Color(0xFF0F172A))
            ) {
                Text("Save Liability", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}
