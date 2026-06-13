package com.example

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.pow

@Composable
fun LoanComparisonScreen(onNavigateBack: () -> Unit) {
    var principalA by remember { mutableDoubleStateOf(1500000.0) }
    var rateA by remember { mutableDoubleStateOf(8.75) }
    var tenureA by remember { mutableDoubleStateOf(20.0) }

    var principalB by remember { mutableDoubleStateOf(1500000.0) }
    var rateB by remember { mutableDoubleStateOf(8.25) }
    var tenureB by remember { mutableDoubleStateOf(20.0) }

    var editingLoan by remember { mutableStateOf<String?>(null) }
    var editPrincipal by remember { mutableStateOf("") }
    var editRate by remember { mutableStateOf("") }
    var editTenure by remember { mutableStateOf("") }

    val calcEmi = { p: Double, r: Double, tYears: Double ->
        if (p == 0.0 || r == 0.0 || tYears == 0.0) 0.0
        else {
            val rMon = r / (12 * 100)
            val nMonths = tYears * 12
            (p * rMon * (1 + rMon).pow(nMonths)) / ((1 + rMon).pow(nMonths) - 1)
        }
    }

    val formatInr = { value: Double ->
        val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
        format.maximumFractionDigits = 0
        format.format(value).replace("₹", "₹")
    }
    
    val formatDec = { value: Double ->
        String.format(Locale.US, "%.2f", value)
    }

    val emiA = calcEmi(principalA, rateA, tenureA)
    val emiB = calcEmi(principalB, rateB, tenureB)

    val totalPaymentA = emiA * tenureA * 12
    val totalPaymentB = emiB * tenureB * 12

    val interestA = totalPaymentA - principalA
    val interestB = totalPaymentB - principalB

    val emiDiff = emiA - emiB
    val interestDiff = interestA - interestB
    val totalPaymentDiff = totalPaymentA - totalPaymentB

    val aIsBetter = totalPaymentA < totalPaymentB
    val winnerName = if (aIsBetter) "Loan A" else "Loan B"
    val winnerColor = if (aIsBetter) AccentBlue else AccentGreen
    val loserName = if (aIsBetter) "Loan B" else "Loan A"
    
    val emiSavings = if (aIsBetter) emiB - emiA else emiA - emiB
    val interestSavings = if (aIsBetter) interestB - interestA else interestA - interestB
    val totalSavings = if (aIsBetter) totalPaymentB - totalPaymentA else totalPaymentA - totalPaymentB

    val highlightPurple = Color(0xFFB388FF)

    if (editingLoan != null) {
        AlertDialog(
            onDismissRequest = { editingLoan = null },
            title = { Text("Edit Loan $editingLoan", color = TextPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = editPrincipal,
                        onValueChange = { editPrincipal = it },
                        label = { Text("Loan Amount") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = AccentYellow,
                            unfocusedBorderColor = CardStroke
                        )
                    )
                    OutlinedTextField(
                        value = editRate,
                        onValueChange = { editRate = it },
                        label = { Text("Interest Rate (%)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = AccentYellow,
                            unfocusedBorderColor = CardStroke
                        )
                    )
                    OutlinedTextField(
                        value = editTenure,
                        onValueChange = { editTenure = it },
                        label = { Text("Tenure (Years)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = AccentYellow,
                            unfocusedBorderColor = CardStroke
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val p = editPrincipal.toDoubleOrNull() ?: 0.0
                        val r = editRate.toDoubleOrNull() ?: 0.0
                        val t = editTenure.toDoubleOrNull() ?: 0.0
                        if (editingLoan == "A") {
                            principalA = p
                            rateA = r
                            tenureA = t
                        } else {
                            principalB = p
                            rateB = r
                            tenureB = t
                        }
                        editingLoan = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentYellow, contentColor = BackgroundDark)
                ) { Text("Save", fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { editingLoan = null }) { Text("Cancel", color = TextSecondary) }
            },
            containerColor = SurfaceDark
        )
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(BackgroundDark).statusBarsPadding()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary,
                        modifier = Modifier.size(24.dp).clickable { onNavigateBack() }
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Loan Comparison", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text("Compare loan options side by side", color = TextSecondary, fontSize = 12.sp)
                    }
                    Icon(imageVector = Icons.Rounded.StarBorder, contentDescription = "Favorite", tint = TextPrimary, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(imageVector = Icons.Rounded.Share, contentDescription = "Share", tint = TextPrimary, modifier = Modifier.size(24.dp))
                }
                
                // Tabs
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TabItem(text = "2 Loans", isActive = true)
                        TabItem(text = "3 Loans", isActive = false)
                        TabItem(text = "4 Loans", isActive = false)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Row(
                        modifier = Modifier.clip(RoundedCornerShape(20.dp)).clickable { }.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Rounded.Add, contentDescription = "Add", tint = AccentBlue, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Loan", color = AccentBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        bottomBar = { AppBottomBar(selectedRoute = "compare") },
        containerColor = BackgroundDark
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Hero Section (Row with 2 cards)
            Box(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    LoanCard(
                        modifier = Modifier.weight(1f),
                        title = "Loan A",
                        letter = "A",
                        themeColor = AccentBlue,
                        principal = principalA,
                        rate = rateA,
                        tenure = tenureA,
                        emi = emiA,
                        formatInr = formatInr,
                        formatDec = formatDec,
                        onEditClick = {
                            editingLoan = "A"
                            editPrincipal = principalA.toLong().toString()
                            editRate = rateA.toString()
                            editTenure = tenureA.toInt().toString()
                        }
                    )
                    LoanCard(
                        modifier = Modifier.weight(1f),
                        title = "Loan B",
                        letter = "B",
                        themeColor = AccentGreen,
                        principal = principalB,
                        rate = rateB,
                        tenure = tenureB,
                        emi = emiB,
                        formatInr = formatInr,
                        formatDec = formatDec,
                        onEditClick = {
                            editingLoan = "B"
                            editPrincipal = principalB.toLong().toString()
                            editRate = rateB.toString()
                            editTenure = tenureB.toInt().toString()
                        }
                    )
                }
                Box(
                    modifier = Modifier.align(Alignment.Center).size(36.dp).clip(CircleShape).background(BackgroundDark).border(1.dp, CardStroke, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("VS", color = TextSecondary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Best Option Banner
            Row(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(SurfaceDark).border(1.dp, CardStroke, RoundedCornerShape(12.dp)).padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(modifier = Modifier.weight(0.4f), verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Rounded.EmojiEvents, contentDescription = "Trophy", tint = AccentYellow, modifier = Modifier.size(40.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Best Option", color = winnerColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(winnerName, color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text(if (totalSavings > 0) "Lower Total Interest" else "Same Total Payment", color = TextSecondary, fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
                Row(
                    modifier = Modifier.weight(0.6f),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    val vsText = "vs $loserName"
                    SavingsColumn("EMI Savings (Monthly)", formatInr(emiSavings), vsText)
                    SavingsColumn("Interest Savings", formatInr(interestSavings), vsText)
                    SavingsColumn("Total Payment Savings", formatInr(totalSavings), vsText)
                }
            }

            // Key Comparison Table
            Column(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(SurfaceDark).border(1.dp, CardStroke, RoundedCornerShape(12.dp)).padding(16.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                    Text("Key Comparison", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.5f))
                    Text("Loan A", color = AccentBlue, fontSize = 12.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                    Text("Loan B", color = AccentGreen, fontSize = 12.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                    Text("Difference", color = highlightPurple, fontSize = 12.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                }
                HorizontalDivider(color = CardStroke)
                
                TableRow("Monthly EMI", Icons.Rounded.DateRange, formatInr(emiA), formatInr(emiB), formatInr(emiDiff), emiDiff > 0)
                TableRow("Total Interest", Icons.Rounded.Percent, formatInr(interestA), formatInr(interestB), formatInr(interestDiff), interestDiff > 0)
                TableRow("Total Payment", Icons.Rounded.CurrencyRupee, formatInr(totalPaymentA), formatInr(totalPaymentB), formatInr(totalPaymentDiff), totalPaymentDiff > 0)
                TableRow("Principal Amount", Icons.Rounded.AccountBalanceWallet, formatInr(principalA), formatInr(principalB), "₹0", false)
                TableRow("Tenure", Icons.Rounded.AccessTime, "${tenureA.toInt()} Years", "${tenureB.toInt()} Years", "Same", false)
                TableRow("Interest Rate (p.a.)", Icons.Rounded.Percent, "${formatDec(rateA)}%", "${formatDec(rateB)}%", "${formatDec(rateA - rateB)}%", (rateA - rateB) > 0)
            }

            // What if Section
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("What if?", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Change tenure and see how EMI changes", color = TextSecondary, fontSize = 12.sp)
                    }
                    Row(
                        modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(SurfaceDark).border(1.dp, CardStroke, RoundedCornerShape(8.dp)).padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Based on Loan B", color = TextSecondary, fontSize = 10.sp)
                        Icon(imageVector = Icons.Rounded.KeyboardArrowDown, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(14.dp))
                    }
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TenureOptionCard("20 Years", "(240 Months)", formatInr(calcEmi(principalB, rateB, 20.0)), "Current", isCurrent = true)
                    TenureOptionCard("15 Years", "(180 Months)", formatInr(calcEmi(principalB, rateB, 15.0)), "EMI + ₹2,399")
                    TenureOptionCard("10 Years", "(120 Months)", formatInr(calcEmi(principalB, rateB, 10.0)), "EMI + ₹6,099")
                    TenureOptionCard("25 Years", "(300 Months)", formatInr(calcEmi(principalB, rateB, 25.0)), "EMI - ₹1,855")
                    Box(
                        modifier = Modifier.width(100.dp).height(100.dp).clip(RoundedCornerShape(8.dp)).background(SurfaceDark).border(1.dp, CardStroke, RoundedCornerShape(8.dp)).clickable { },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Rounded.Add, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Custom", color = TextSecondary, fontSize = 12.sp)
                            Text("Enter Tenure", color = TextSecondary, fontSize = 10.sp)
                        }
                    }
                }
            }

            // Charts Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                LoanChartCard(modifier = Modifier.weight(1f), title = "EMI Comparison", subtitle = "Monthly EMI", valA = emiA, valB = emiB, formatInr(emiA), formatInr(emiB))
                LoanChartCard(modifier = Modifier.weight(1f), title = "Total Interest Comparison", subtitle = "You Pay in Interest", valA = interestA, valB = interestB, "${formatDec(interestA / 100000)}L", "${formatDec(interestB / 100000)}L")
                LoanChartCard(modifier = Modifier.weight(1f), title = "Total Payment Comparison", subtitle = "Principal + Interest", valA = totalPaymentA, valB = totalPaymentB, "${formatDec(totalPaymentA / 100000)}L", "${formatDec(totalPaymentB / 100000)}L")
            }

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                LoanActionButton("View Amortization", "See payment breakup", Icons.Rounded.EventNote, AccentBlue)
                LoanActionButton("View Charts", "Detailed analysis", Icons.Rounded.PieChart, highlightPurple)
                LoanActionButton("Download Report", "Save as PDF", Icons.Rounded.Download, AccentGreen)
                LoanActionButton("Share Comparison", "Share results", Icons.Rounded.Share, AccentYellow)
            }
        }
    }
}

@Composable
private fun LoanChartCard(modifier: Modifier, title: String, subtitle: String, valA: Double, valB: Double, textA: String, textB: String) {
    Box(
        modifier = modifier.clip(RoundedCornerShape(8.dp)).background(SurfaceDark).border(1.dp, CardStroke, RoundedCornerShape(8.dp)).padding(12.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            ScrollingTitleText(title, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            ScrollingTitleText(subtitle, color = TextSecondary, fontSize = 9.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth().height(80.dp)
            ) {
                val maxVal = maxOf(valA, valB)
                val heightA = if(maxVal > 0) (valA / maxVal) * 60 else 0.0
                val heightB = if(maxVal > 0) (valB / maxVal) * 60 else 0.0
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(textA, color = TextPrimary, fontSize = 10.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(modifier = Modifier.width(36.dp).height(heightA.dp).background(AccentBlue))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Loan A", color = TextSecondary, fontSize = 9.sp)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(textB, color = TextPrimary, fontSize = 10.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(modifier = Modifier.width(36.dp).height(heightB.dp).background(AccentGreen))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Loan B", color = TextSecondary, fontSize = 9.sp)
                }
            }
        }
    }
}

@Composable
private fun LoanActionButton(title: String, subtitle: String, icon: ImageVector, color: Color) {
    Row(
        modifier = Modifier.defaultMinSize(minWidth = 160.dp).clip(RoundedCornerShape(8.dp)).background(SurfaceDark).border(1.dp, CardStroke, RoundedCornerShape(8.dp)).clickable { }.padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(color.copy(alpha = 0.2f)), contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f, fill = false)) {
            ScrollingTitleText(title, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            ScrollingTitleText(subtitle, color = TextSecondary, fontSize = 10.sp)
        }
    }
}

@Composable
private fun TenureOptionCard(years: String, months: String, emi: String, diffText: String, isCurrent: Boolean = false) {
    val borderColor = if (isCurrent) AccentGreen else CardStroke
    Column(
        modifier = Modifier.defaultMinSize(minWidth = 100.dp).clip(RoundedCornerShape(8.dp)).background(SurfaceDark).border(1.dp, borderColor, RoundedCornerShape(8.dp)).clickable { }.padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(years, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Text(months, color = TextSecondary, fontSize = 10.sp)
        Spacer(modifier = Modifier.height(8.dp))
        AutoResizedText(emi, color = if (isCurrent) AccentGreen else TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))
        if (isCurrent) {
            Box(modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(AccentGreen.copy(alpha = 0.2f)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                Text("Current", color = AccentGreen, fontSize = 10.sp)
            }
        } else {
            Text(diffText, color = TextSecondary, fontSize = 10.sp)
        }
    }
}

@Composable
private fun TableRow(title: String, icon: ImageVector, valA: String, valB: String, diff: String, isPositive: Boolean) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Row(modifier = Modifier.weight(1.5f).padding(end = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(6.dp))
            ScrollingTitleText(title, color = TextPrimary, fontSize = 12.sp)
        }
        AutoResizedText(valA, color = TextPrimary, fontSize = 12.sp, modifier = Modifier.weight(1f).padding(horizontal = 2.dp), maxLines = 2)
        AutoResizedText(valB, color = TextPrimary, fontSize = 12.sp, modifier = Modifier.weight(1f).padding(horizontal = 2.dp), maxLines = 2)
        
        Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
            AutoResizedText(diff, color = if(diff == "Same" || diff == "₹0") TextSecondary else AccentGreen, fontSize = 12.sp, maxLines = 2)
            if (isPositive) {
                Spacer(modifier = Modifier.width(2.dp))
                Icon(Icons.Rounded.ArrowDownward, contentDescription = null, tint = AccentGreen, modifier = Modifier.size(12.dp))
            }
        }
    }
    HorizontalDivider(color = CardStroke)
}

@Composable
private fun TabItem(text: String, isActive: Boolean) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, if (isActive) AccentYellow else Color.Transparent, RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (text.startsWith("2")) { // Quick hack for icon
                Icon(Icons.Rounded.Balance, contentDescription = null, tint = if (isActive) AccentYellow else TextSecondary, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(text, color = if (isActive) AccentYellow else TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun SavingsColumn(title: String, value: String, vsText: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.wrapContentWidth()) {
        ScrollingTitleText(title, color = TextSecondary, fontSize = 9.sp)
        Spacer(modifier = Modifier.height(2.dp))
        AutoResizedText(value, color = AccentGreen, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        ScrollingTitleText(vsText, color = TextSecondary, fontSize = 9.sp)
    }
}

@Composable
private fun LoanCard(
    modifier: Modifier,
    title: String,
    letter: String,
    themeColor: Color,
    principal: Double,
    rate: Double,
    tenure: Double,
    emi: Double,
    formatInr: (Double) -> String,
    formatDec: (Double) -> String,
    onEditClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceDark)
            .border(1.dp, if(letter == "B") themeColor else CardStroke, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().clickable { onEditClick() }, horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(24.dp).clip(RoundedCornerShape(4.dp)).background(themeColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(letter, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            Icon(Icons.Rounded.Edit, contentDescription = "Edit", tint = TextSecondary, modifier = Modifier.size(16.dp))
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f).padding(end = 4.dp)) {
                ScrollingTitleText("Loan Amount", color = TextSecondary, fontSize = 10.sp)
                Spacer(modifier = Modifier.height(2.dp))
                AutoResizedText(formatInr(principal), color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Box(modifier = Modifier.width(1.dp).height(24.dp).background(CardStroke).align(Alignment.CenterVertically))
            Column(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
                ScrollingTitleText("Interest Rate (p.a.)", color = TextSecondary, fontSize = 10.sp)
                Spacer(modifier = Modifier.height(2.dp))
                AutoResizedText("${formatDec(rate)}%", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(color = CardStroke)
        Spacer(modifier = Modifier.height(8.dp))
        Column {
            Text("Tenure", color = TextSecondary, fontSize = 10.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text("${tenure.toInt()} Years", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text("(${tenure.toInt() * 12} Months)", color = TextSecondary, fontSize = 9.sp)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(6.dp)).background(if (letter == "B") themeColor.copy(alpha = 0.2f) else HighlightBlue).padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f, fill=false).padding(end=4.dp)) {
                Icon(Icons.Rounded.DateRange, contentDescription = null, tint = if (letter == "B") themeColor else TextPrimary, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                ScrollingTitleText("Monthly EMI", color = if (letter == "B") themeColor else TextPrimary, fontSize = 11.sp)
            }
            AutoResizedText(formatInr(emi), color = if (letter == "B") themeColor else TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier=Modifier.weight(1f))
        }
    }
}
