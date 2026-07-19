package com.loanmaster.pro.feature.loaneligibility.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import kotlinx.coroutines.launch
import com.loanmaster.pro.core.theme.*
import com.loanmaster.pro.core.ui.*
import com.loanmaster.pro.core.formatter.formatMoney
import com.loanmaster.pro.core.responsive.*
import com.loanmaster.pro.feature.loaneligibility.util.*
import com.loanmaster.pro.domain.model.LoanProfile


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun InputSection(
    isInputExpanded: Boolean,
    isResultVisible: Boolean,
    monthlyIncome: String,
    onMonthlyIncomeChange: (String) -> Unit,
    existingEMIs: String,
    onExistingEMIsChange: (String) -> Unit,
    isCoBorrowerEnabled: Boolean,
    onCoBorrowerChange: (Boolean) -> Unit,
    coBorrowerIncome: String,
    onCoBorrowerIncomeChange: (String) -> Unit,
    coBorrowerEMIs: String,
    onCoBorrowerEMIsChange: (String) -> Unit,
    selectedLoanProfile: String,
    onSelectedLoanProfileChange: (String) -> Unit,
    tenureYears: String,
    onTenureYearsChange: (String) -> Unit,
    interestRate: String,
    onInterestRateChange: (String) -> Unit,
    isSalaried: Boolean,
    onSalariedChange: (Boolean) -> Unit,
    creditScoreRange: String,
    onCreditScoreRangeChange: (String) -> Unit,
    showEmptyError: Boolean
) {
    val surfaceColor = SurfaceDark
    val brightBlue = AccentBlue
    val textSecondary = TextSecondary
    val textColor = TextPrimary

    AnimatedVisibility(
                        visible = isInputExpanded,
                        enter = expandVertically(animationSpec = tween(300)) + fadeIn(animationSpec = tween(300)),
                        exit = shrinkVertically(animationSpec = tween(300)) + fadeOut(animationSpec = tween(300))
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            // 1. Employment Type
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(64.dp)
                                    .clip(RoundedCornerShape(24.dp))
                                    .border(1.dp, surfaceColor, RoundedCornerShape(24.dp))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .background(if (isSalaried) brightBlue.copy(alpha = 0.15f) else Color.Transparent)
                                        .clickable { onSalariedChange(true) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Rounded.BusinessCenter, contentDescription = null, tint = if (isSalaried) brightBlue else textSecondary, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Salaried",
                                            color = if (isSalaried) brightBlue else textSecondary,
                                            fontSize = 14.sp,
                                            fontWeight = if (isSalaried) FontWeight.Bold else FontWeight.Medium,
                                            maxLines = 1
                                        )
                                    }
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .background(if (!isSalaried) brightBlue.copy(alpha = 0.15f) else Color.Transparent)
                                        .clickable { onSalariedChange(false) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Rounded.Storefront, contentDescription = null, tint = if (!isSalaried) brightBlue else textSecondary, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Self-Employed",
                                            color = if (!isSalaried) brightBlue else textSecondary,
                                            fontSize = 14.sp,
                                            fontWeight = if (!isSalaried) FontWeight.Bold else FontWeight.Medium,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }

                            // 2. Primary Income & EMI
                            AdaptiveRowCol(
                                modifier = Modifier.fillMaxWidth(),
                                content1 = { mod ->
                                    LoanInputField(
                                        value = monthlyIncome,
                                        onValueChange = { onMonthlyIncomeChange(it) },
                                        label = "Monthly Income",
                                        icon = Icons.Rounded.AccountBalanceWallet,
                                        isError = showEmptyError && monthlyIncome.isBlank(),
                                        modifier = mod
                                    )
                                },
                                content2 = { mod ->
                                    LoanInputField(
                                        value = existingEMIs,
                                        onValueChange = { onExistingEMIsChange(it) },
                                        label = "Existing EMIs",
                                        icon = Icons.Rounded.CreditCard,
                                        isError = showEmptyError && existingEMIs.isBlank(),
                                        modifier = mod
                                    )
                                }
                            )

                            // 3. Co-Borrower Switch
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(64.dp)
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(surfaceColor.copy(alpha = 0.3f))
                                    .border(1.dp, surfaceColor, RoundedCornerShape(24.dp))
                                    .clickable { onCoBorrowerChange(!isCoBorrowerEnabled) }
                                    .padding(horizontal = 16.dp)
                            ) {
                                Icon(Icons.Rounded.GroupAdd, contentDescription = null, tint = brightBlue, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Add Co-Borrower",
                                        color = textColor,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1
                                    )
                                }
                                Switch(
                                    checked = isCoBorrowerEnabled,
                                    onCheckedChange = { onCoBorrowerChange(it) },
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
                                            onValueChange = { onCoBorrowerIncomeChange(it) },
                                            label = "Co-Borrower Income",
                                            icon = Icons.Rounded.Group,
                                            modifier = mod
                                        )
                                    },
                                    content2 = { mod ->
                                        LoanInputField(
                                            value = coBorrowerEMIs,
                                            onValueChange = { onCoBorrowerEMIsChange(it) },
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
                                            label = { Text("Loan Type", maxLines = 1, fontWeight = FontWeight.Normal) },
                                            leadingIcon = { Icon(getLoanTypeIcon(selectedLoanProfile), contentDescription = null, tint = brightBlue, modifier = Modifier.size(20.dp)) },
                                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = loanDropdownExpanded) },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .heightIn(min = 56.dp)
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
                                            shape = RoundedCornerShape(24.dp),
                                            singleLine = true,
                                            textStyle = LocalTextStyle.current.copy(fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                        )
                                        ExposedDropdownMenu(
                                            expanded = loanDropdownExpanded,
                                            onDismissRequest = { loanDropdownExpanded = false },
                                            containerColor = surfaceColor
                                        ) {
                                            loanProfiles.forEach { p ->
                                                DropdownMenuItem(
                                                    text = { Text(p.name, color = textColor, fontWeight = FontWeight.Medium) },
                                                    leadingIcon = { Icon(getLoanTypeIcon(p.name), contentDescription = null, tint = textSecondary, modifier = Modifier.size(20.dp)) },
                                                    onClick = {
                                                        onSelectedLoanProfileChange(p.name)
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
                                        onValueChange = { onTenureYearsChange(it) },
                                        label = "Tenure (Yrs)",
                                        icon = Icons.Rounded.Event,
                                        isError = showEmptyError && tenureYears.isBlank(),
                                        modifier = mod
                                    )
                                }
                            )
                            
                            LoanInputField(
                                value = interestRate,
                                onValueChange = { onInterestRateChange(it) },
                                label = "Interest Rate (%)",
                                icon = Icons.Rounded.Percent,
                                isError = showEmptyError && interestRate.isBlank(),
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Credit Score Filter Chips
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 12.dp)) {
                                    Icon(Icons.Rounded.Speed, contentDescription = null, tint = textSecondary, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        "Credit Score",
                                        color = textSecondary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        maxLines = 1
                                    )
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    listOf(
                                        "Excellent" to "750+",
                                        "Good" to "650-749",
                                        "Fair" to "<650"
                                    ).forEach { (title, range) ->
                                        val isSelected = creditScoreRange == title
                                        FilterChip(
                                            selected = isSelected,
                                            onClick = { onCreditScoreRangeChange(title) },
                                            label = {
                                                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
                                                    Text(title, fontWeight = FontWeight.Bold, maxLines = 1, fontSize = 13.sp)
                                                    Spacer(modifier = Modifier.height(2.dp))
                                                    Text(range, fontSize = 11.sp, maxLines = 1, color = if (isSelected) Color.White.copy(alpha = 0.9f) else textSecondary, fontWeight = FontWeight.Normal)
                                                }
                                            },
                                            modifier = Modifier.weight(1f).heightIn(min = 64.dp),
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = brightBlue,
                                                selectedLabelColor = Color.White,
                                                containerColor = surfaceColor.copy(alpha = 0.3f),
                                                labelColor = textColor
                                            ),
                                            shape = RoundedCornerShape(24.dp),
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
