package com.example

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

@Composable
fun GstCalculatorScreen(onNavigateBack: () -> Unit) {
    var amountText by remember { mutableStateOf("100000") }
    var gstRate by remember { mutableDoubleStateOf(18.0) }
    var cessRate by remember { mutableDoubleStateOf(0.0) }
    var isIntrastate by remember { mutableStateOf(true) }

    val formatInr = { value: Double ->
        val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
        format.maximumFractionDigits = 0
        format.format(value).replace("₹", "₹")
    }

    val amount = amountText.toDoubleOrNull() ?: 0.0
    val totalGst = amount * (gstRate / 100.0)
    val cess = amount * (cessRate / 100.0)
    val cgst = if (isIntrastate) totalGst / 2 else 0.0
    val sgst = if (isIntrastate) totalGst / 2 else 0.0
    val igst = if (isIntrastate) 0.0 else totalGst
    val totalAmount = amount + totalGst + cess

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
                        Text("GST Calculator", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text("Calculate GST amount instantly", color = TextSecondary, fontSize = 12.sp)
                    }
                    Icon(imageVector = Icons.Rounded.StarBorder, contentDescription = "Favorite", tint = TextPrimary, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(imageVector = Icons.Rounded.History, contentDescription = "History", tint = TextPrimary, modifier = Modifier.size(24.dp))
                }
            }
        },
        bottomBar = { AppBottomBar(selectedRoute = "gst") },
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
            // Top Tab Row
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                GstTab("Add GST", "Price + GST", Icons.Rounded.PostAdd, true)
                GstTab("Remove GST", "Price from Total", Icons.Rounded.RemoveCircleOutline, false)
                GstTab("GST on Margin", "(Scheme)", Icons.Rounded.Percent, false)
                GstTab("HSN Finder", "Search Code", Icons.Rounded.Search, false)
            }

            // Enter Details Section
            Column(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(SurfaceDark).border(1.dp, CardStroke, RoundedCornerShape(12.dp)).padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Enter Details", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(Icons.Rounded.Info, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(14.dp))
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(modifier = Modifier.weight(1.5f)) {
                        AutoResizedText("Amount (Without GST)", color = TextSecondary, fontSize = 12.sp, maxLines = 1)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = amountText,
                            onValueChange = { amountText = it },
                            leadingIcon = { Text("₹", color = TextSecondary, fontSize = 16.sp, modifier = Modifier.padding(start = 12.dp)) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = AccentBlue,
                                unfocusedBorderColor = CardStroke,
                                focusedContainerColor = BackgroundDark,
                                unfocusedContainerColor = BackgroundDark
                            ),
                            modifier = Modifier.fillMaxWidth().height(52.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Enter taxable amount", color = TextSecondary, fontSize = 10.sp)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        AutoResizedText("GST Rate", color = TextSecondary, fontSize = 12.sp, maxLines = 1)
                        Spacer(modifier = Modifier.height(4.dp))
                        var isGstExpanded by remember { mutableStateOf(false) }
                        Box {
                            OutlinedTextField(
                                value = "${gstRate.toInt()}%",
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { Icon(Icons.Rounded.KeyboardArrowDown, contentDescription = null, tint = TextSecondary) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary,
                                    focusedBorderColor = CardStroke,
                                    unfocusedBorderColor = CardStroke,
                                    focusedContainerColor = BackgroundDark,
                                    unfocusedContainerColor = BackgroundDark
                                ),
                                modifier = Modifier.fillMaxWidth().height(52.dp).clickable { isGstExpanded = true }
                            )
                            DropdownMenu(expanded = isGstExpanded, onDismissRequest = { isGstExpanded = false }) {
                                listOf(5.0, 12.0, 18.0, 28.0).forEach { rate ->
                                    DropdownMenuItem(text = { Text("${rate.toInt()}%") }, onClick = { gstRate = rate; isGstExpanded = false })
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Select GST rate", color = TextSecondary, fontSize = 10.sp)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        AutoResizedText("Cess Rate (if any)", color = TextSecondary, fontSize = 12.sp, maxLines = 1)
                        Spacer(modifier = Modifier.height(4.dp))
                        var isCessExpanded by remember { mutableStateOf(false) }
                        Box {
                            OutlinedTextField(
                                value = "${cessRate.toInt()}%",
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { Icon(Icons.Rounded.KeyboardArrowDown, contentDescription = null, tint = TextSecondary) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary,
                                    focusedBorderColor = CardStroke,
                                    unfocusedBorderColor = CardStroke,
                                    focusedContainerColor = BackgroundDark,
                                    unfocusedContainerColor = BackgroundDark
                                ),
                                modifier = Modifier.fillMaxWidth().height(52.dp).clickable { isCessExpanded = true }
                            )
                            DropdownMenu(expanded = isCessExpanded, onDismissRequest = { isCessExpanded = false }) {
                                listOf(0.0, 1.0, 3.0, 5.0, 12.0).forEach { rate ->
                                    DropdownMenuItem(text = { Text("${rate.toInt()}%") }, onClick = { cessRate = rate; isCessExpanded = false })
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Select cess rate", color = TextSecondary, fontSize = 10.sp)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Row(
                        modifier = Modifier.weight(1.5f).clip(RoundedCornerShape(8.dp)).border(1.dp, CardStroke, RoundedCornerShape(8.dp)).padding(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f).clickable { isIntrastate = true }.padding(horizontal = 8.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = if (isIntrastate) Icons.Rounded.RadioButtonChecked else Icons.Rounded.RadioButtonUnchecked,
                                contentDescription = null,
                                tint = if (isIntrastate) AccentBlue else TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            AutoResizedText("Intrastate (CGST + SGST)", color = if (isIntrastate) TextPrimary else TextSecondary, fontSize = 12.sp, maxLines = 1)
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f).clickable { isIntrastate = false }.padding(horizontal = 8.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = if (!isIntrastate) Icons.Rounded.RadioButtonChecked else Icons.Rounded.RadioButtonUnchecked,
                                contentDescription = null,
                                tint = if (!isIntrastate) AccentBlue else TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            AutoResizedText("Interstate (IGST)", color = if (!isIntrastate) TextPrimary else TextSecondary, fontSize = 12.sp, maxLines = 1)
                        }
                    }
                    Button(
                        onClick = { },
                        modifier = Modifier.weight(1f).height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentBlue, contentColor = Color.White),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Rounded.Calculate, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Calculate", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }

            // GST Calculation Summary
            Text("GST Calculation Summary", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                GstSummaryCard("Taxable Amount", "(Without GST)", formatInr(amount), Icons.Rounded.AccountBalanceWallet, AccentBlue)
                GstSummaryCard("Total GST", "(${String.format(Locale.US, "%.2f", gstRate)}%)", formatInr(totalGst), Icons.Rounded.Percent, Color(0xFF7C4DFF))
                GstSummaryCard("Total Amount", "(With GST)", formatInr(totalAmount), Icons.Rounded.Payments, AccentGreen, AccentGreen.copy(alpha = 0.1f), AccentGreen.copy(alpha = 0.3f))
                GstSummaryCard("You Save", "(No Cess)", "₹0", Icons.Rounded.ShoppingBag, AccentYellow)
            }

            // Middle Split Section
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                // GST Breakup
                Column(
                    modifier = Modifier.weight(1f).clip(RoundedCornerShape(12.dp)).background(SurfaceDark).border(1.dp, CardStroke, RoundedCornerShape(12.dp))
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(16.dp)) {
                        Text("GST Breakup", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.Rounded.Info, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(14.dp))
                    }
                    Divider(color = CardStroke)
                    
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        GstBreakupRow("Taxable Amount", formatInr(amount), TextPrimary)
                        
                        if (isIntrastate) {
                            GstBreakupRow("CGST (${gstRate/2}%)", formatInr(cgst), TextSecondary)
                            GstBreakupRow("SGST (${gstRate/2}%)", formatInr(sgst), TextSecondary)
                        } else {
                            GstBreakupRow("IGST (${gstRate}%)", formatInr(igst), TextSecondary)
                        }
                        
                        Canvas(modifier = Modifier.fillMaxWidth().height(1.dp)) {
                            drawLine(color = CardStroke, start = androidx.compose.ui.geometry.Offset(0f, 0f), end = androidx.compose.ui.geometry.Offset(size.width, 0f), pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f))
                        }
                        GstBreakupRow("Total GST", formatInr(totalGst), Color(0xFF7C4DFF))
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth().background(AccentGreen.copy(alpha = 0.1f)).padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Total Amount (With GST)", color = AccentGreen, fontSize = 12.sp, modifier = Modifier.weight(1f).padding(end = 8.dp))
                        AutoResizedText(formatInr(totalAmount), color = AccentGreen, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Rate Wise Quick Calculator
                Column(
                    modifier = Modifier.weight(1f).clip(RoundedCornerShape(12.dp)).background(SurfaceDark).border(1.dp, CardStroke, RoundedCornerShape(12.dp)).padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Rate Wise Quick Calculator", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.Rounded.Info, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(14.dp))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(5.0, 12.0, 18.0, 28.0).forEach { rate ->
                            val isActive = rate == gstRate
                            val rateTotal = amount + (amount * (rate / 100.0))
                            
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(1.dp, if (isActive) AccentYellow else CardStroke, RoundedCornerShape(8.dp))
                                    .background(if (isActive) AccentYellow.copy(alpha = 0.05f) else Color.Transparent)
                                    .clickable { gstRate = rate }
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("${rate.toInt()}%", color = if (isActive) AccentYellow else TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    Text("GST", color = TextSecondary, fontSize = 10.sp)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1f, fill = false)) {
                                        AutoResizedText("Total Amount", color = TextSecondary, fontSize = 10.sp, maxLines = 1)
                                        AutoResizedText(formatInr(rateTotal), color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    }
                                    if (isActive) {
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Box(modifier = Modifier.size(20.dp).clip(CircleShape).background(AccentBlue), contentAlignment = Alignment.Center) {
                                            Icon(Icons.Rounded.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Summary Table
            Column(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(SurfaceDark).border(1.dp, CardStroke, RoundedCornerShape(12.dp)).padding(vertical = 16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 12.dp)) {
                    Text("Summary Table", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(Icons.Rounded.Info, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(14.dp))
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())
                ) {
                    Column {
                        Row(modifier = Modifier.background(CardStroke.copy(alpha = 0.5f)).padding(vertical = 10.dp).wrapContentHeight()) {
                            Text("Rate", color = TextSecondary, fontSize = 12.sp, modifier = Modifier.widthIn(min = 60.dp), textAlign = TextAlign.Center, maxLines = 2, overflow = TextOverflow.Ellipsis)
                            Text("Taxable Amount", color = TextSecondary, fontSize = 12.sp, modifier = Modifier.widthIn(min = 120.dp), textAlign = TextAlign.Center, maxLines = 2, overflow = TextOverflow.Ellipsis)
                            Text("CGST", color = TextSecondary, fontSize = 12.sp, modifier = Modifier.widthIn(min = 80.dp), textAlign = TextAlign.Center, maxLines = 2, overflow = TextOverflow.Ellipsis)
                            Text("SGST", color = TextSecondary, fontSize = 12.sp, modifier = Modifier.widthIn(min = 80.dp), textAlign = TextAlign.Center, maxLines = 2, overflow = TextOverflow.Ellipsis)
                            Text("IGST", color = TextSecondary, fontSize = 12.sp, modifier = Modifier.widthIn(min = 80.dp), textAlign = TextAlign.Center, maxLines = 2, overflow = TextOverflow.Ellipsis)
                            Text("Total GST", color = TextSecondary, fontSize = 12.sp, modifier = Modifier.widthIn(min = 100.dp), textAlign = TextAlign.Center, maxLines = 2, overflow = TextOverflow.Ellipsis)
                            Text("Total Amount", color = TextSecondary, fontSize = 12.sp, modifier = Modifier.widthIn(min = 120.dp), textAlign = TextAlign.Center, maxLines = 2, overflow = TextOverflow.Ellipsis)
                        }
                        
                        listOf(5.0, 12.0, 18.0, 28.0).forEachIndexed { index, rate ->
                            val isActive = rate == gstRate
                            val textColor = if (isActive) AccentYellow else TextPrimary
                            
                            val listTotalGst = amount * (rate / 100.0)
                            val listCgst = if (isIntrastate) listTotalGst / 2 else 0.0
                            val listSgst = if (isIntrastate) listTotalGst / 2 else 0.0
                            val listIgst = if (isIntrastate) 0.0 else listTotalGst
                            val listTotalAmt = amount + listTotalGst
                            
                            Row(modifier = Modifier.padding(vertical = 12.dp).wrapContentHeight()) {
                                Text("${rate.toInt()}%", color = textColor, fontSize = 12.sp, modifier = Modifier.widthIn(min = 60.dp), textAlign = TextAlign.Center, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                Text(formatInr(amount), color = textColor, fontSize = 12.sp, modifier = Modifier.widthIn(min = 120.dp), textAlign = TextAlign.Center, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                Text(if (isIntrastate) formatInr(listCgst) else "-", color = textColor, fontSize = 12.sp, modifier = Modifier.widthIn(min = 80.dp), textAlign = TextAlign.Center, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                Text(if (isIntrastate) formatInr(listSgst) else "-", color = textColor, fontSize = 12.sp, modifier = Modifier.widthIn(min = 80.dp), textAlign = TextAlign.Center, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                Text(if (!isIntrastate) formatInr(listIgst) else "-", color = textColor, fontSize = 12.sp, modifier = Modifier.widthIn(min = 80.dp), textAlign = TextAlign.Center, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                Text(formatInr(listTotalGst), color = textColor, fontSize = 12.sp, modifier = Modifier.widthIn(min = 100.dp), textAlign = TextAlign.Center, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                Text(formatInr(listTotalAmt), color = textColor, fontSize = 12.sp, modifier = Modifier.widthIn(min = 120.dp), textAlign = TextAlign.Center, maxLines = 2, overflow = TextOverflow.Ellipsis)
                            }
                            if (index < 3) Divider(color = CardStroke)
                        }
                    }
                }
            }

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                GstActionButton("Save Calculation", Icons.Rounded.BookmarkBorder, AccentBlue)
                GstActionButton("Share Result", Icons.Rounded.Share, Color(0xFF7C4DFF))
                GstActionButton("Download PDF", Icons.Rounded.Download, AccentGreen)
                GstActionButton("Clear All", Icons.Rounded.DeleteOutline, Color(0xFFE53935))
            }
        }
    }
}

@Composable
fun GstTab(title: String, subtitle: String, icon: ImageVector, isActive: Boolean) {
    val borderColor = if (isActive) AccentYellow else CardStroke
    val bgColor = if (isActive) AccentYellow.copy(alpha = 0.05f) else SurfaceDark
    val contentColor = if (isActive) AccentYellow else TextSecondary
    
    Row(
        modifier = Modifier.defaultMinSize(minWidth = 140.dp).clip(RoundedCornerShape(8.dp)).background(bgColor).border(1.dp, borderColor, RoundedCornerShape(8.dp)).clickable { }.padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.wrapContentWidth()) {
            ScrollingTitleText(title, color = if (isActive) AccentYellow else TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            ScrollingTitleText(subtitle, color = TextSecondary, fontSize = 10.sp)
        }
    }
}

@Composable
fun GstSummaryCard(title: String, subtitle: String, value: String, icon: ImageVector, iconColor: Color, bgColor: Color = SurfaceDark, borderColor: Color = CardStroke) {
    ResponsiveCard(
        minWidth = 140.dp,
        bgColor = bgColor,
        borderColor = borderColor,
        modifier = Modifier.wrapContentWidth()
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(28.dp).clip(CircleShape).background(iconColor.copy(alpha = 0.2f)), contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(16.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f, fill = false)) {
                    ScrollingTitleText(title, color = TextSecondary, fontSize = 10.sp)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            AutoResizedText(value, color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            ScrollingTitleText(subtitle, color = TextSecondary, fontSize = 10.sp)
        }
    }
}

@Composable
fun GstBreakupRow(title: String, value: String, valueColor: Color) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(title, color = TextSecondary, fontSize = 12.sp, modifier = Modifier.weight(1f).padding(end = 8.dp))
        AutoResizedText(value, color = valueColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun GstActionButton(title: String, icon: ImageVector, color: Color) {
    Row(
        modifier = Modifier.clip(RoundedCornerShape(8.dp)).border(1.dp, CardStroke, RoundedCornerShape(8.dp)).clickable { }.padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(title, color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}
