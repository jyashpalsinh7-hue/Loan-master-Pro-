import sys

filename = 'app/src/main/java/com/loanmaster/pro/feature/fd/FdScreen.kt'
with open(filename, 'r') as f:
    content = f.read()

target = """                if (sizeClass == WindowWidthSizeClass.COMPACT) {
                    PremiumInputField(
                        label = "Deposit Amount", value = depositAmountText, onValueChange = { viewModel.updateInputs(depositAmount = it) },
                        icon = Icons.Rounded.AccountBalanceWallet, iconTint = AccentBlue, modifier = Modifier.fillMaxWidth()
                    )
                    PremiumInputField(
                        label = "Interest Rate (p.a.)", value = interestRatePaText, onValueChange = { viewModel.updateInputs(interestRatePa = it) },
                        icon = Icons.Rounded.Percent, iconTint = AccentBlue, modifier = Modifier.fillMaxWidth()
                    )
                    PremiumInputField(
                        label = "Tenure (Years)", value = tenureYearsText, onValueChange = { viewModel.updateInputs(tenureYears = it) },
                        icon = Icons.Rounded.DateRange, iconTint = AccentBlue, trailingIcon = Icons.Rounded.KeyboardArrowDown, suffix = " Yrs", modifier = Modifier.fillMaxWidth()
                    )
                    Box(modifier = Modifier.fillMaxWidth()) {
                        PremiumInputField(
                            isNumeric = false,
                            label = "Compounding", value = compoundingFrequency, onValueChange = {}, readOnly = true, onClick = { showCompoundingDropdown = true },
                            icon = Icons.Rounded.BarChart, iconTint = AccentBlue, trailingIcon = Icons.Rounded.KeyboardArrowDown, modifier = Modifier.fillMaxWidth(),
                            infoText = "How often interest is calculated and added to your principal. More frequent compounding leads to higher returns."
                        )
                        DropdownMenu(
                            expanded = showCompoundingDropdown,
                            onDismissRequest = { showCompoundingDropdown = false },
                            modifier = Modifier.background(SurfaceDark).fillMaxWidth(0.9f)
                        ) {
                            listOf("Yearly", "Half-Yearly", "Quarterly", "Monthly").forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option, color = TextPrimary) },
                                    onClick = {
                                        viewModel.updateInputs(compoundingFreq = option)
                                        showCompoundingDropdown = false
                                    }
                                )
                            }
                        }
                    }
                    FinancialDisclaimer()
                } else {
                    Row(horizontalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.md), modifier = Modifier.fillMaxWidth()) {
                        Box(modifier = Modifier.weight(1f)) {
                            PremiumInputField(
                                label = "Deposit Amount", value = depositAmountText, onValueChange = { viewModel.updateInputs(depositAmount = it) },
                                icon = Icons.Rounded.AccountBalanceWallet, iconTint = AccentBlue
                            )
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            PremiumInputField(
                                label = "Interest Rate (p.a.)", value = interestRatePaText, onValueChange = { viewModel.updateInputs(interestRatePa = it) },
                                icon = Icons.Rounded.Percent, iconTint = AccentBlue
                            )
                        }
                    }
                    // FIX: Added Financial Disclaimer
                    FinancialDisclaimer()
                    Row(horizontalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.md), modifier = Modifier.fillMaxWidth()) {
                        Box(modifier = Modifier.weight(1f)) {
                            PremiumInputField(
                                label = "Tenure (Years)", value = tenureYearsText, onValueChange = { viewModel.updateInputs(tenureYears = it) },
                                icon = Icons.Rounded.DateRange, iconTint = AccentBlue, trailingIcon = Icons.Rounded.KeyboardArrowDown, suffix = " Yrs"
                            )
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            PremiumInputField(
                                isNumeric = false,
                                label = "Compounding", value = compoundingFrequency, onValueChange = {}, readOnly = true, onClick = { showCompoundingDropdown = true },
                                icon = Icons.Rounded.BarChart, iconTint = AccentBlue, trailingIcon = Icons.Rounded.KeyboardArrowDown,
                                infoText = "How often interest is calculated and added to your principal. More frequent compounding leads to higher returns."
                            )
                            DropdownMenu(
                                expanded = showCompoundingDropdown,
                                onDismissRequest = { showCompoundingDropdown = false },
                                modifier = Modifier.background(SurfaceDark).fillMaxWidth(0.9f)
                            ) {
                                listOf("Yearly", "Half-Yearly", "Quarterly", "Monthly").forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option, color = TextPrimary) },
                                        onClick = {
                                            viewModel.updateInputs(compoundingFreq = option)
                                            showCompoundingDropdown = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }"""

replacement = """                com.loanmaster.pro.core.responsive.AdaptiveRowCol(
                    columns = 2,
                    content1 = { modifier ->
                        PremiumInputField(
                            label = "Deposit Amount", value = depositAmountText, onValueChange = { viewModel.updateInputs(depositAmount = it) },
                            icon = Icons.Rounded.AccountBalanceWallet, iconTint = AccentBlue, modifier = modifier
                        )
                    },
                    content2 = { modifier ->
                        PremiumInputField(
                            label = "Interest Rate (p.a.)", value = interestRatePaText, onValueChange = { viewModel.updateInputs(interestRatePa = it) },
                            icon = Icons.Rounded.Percent, iconTint = AccentBlue, modifier = modifier
                        )
                    }
                )
                com.loanmaster.pro.core.responsive.AdaptiveRowCol(
                    columns = 2,
                    content1 = { modifier ->
                        PremiumInputField(
                            label = "Tenure (Years)", value = tenureYearsText, onValueChange = { viewModel.updateInputs(tenureYears = it) },
                            icon = Icons.Rounded.DateRange, iconTint = AccentBlue, trailingIcon = Icons.Rounded.KeyboardArrowDown, suffix = " Yrs", modifier = modifier
                        )
                    },
                    content2 = { modifier ->
                        Box(modifier = modifier) {
                            PremiumInputField(
                                isNumeric = false,
                                label = "Compounding", value = compoundingFrequency, onValueChange = {}, readOnly = true, onClick = { showCompoundingDropdown = true },
                                icon = Icons.Rounded.BarChart, iconTint = AccentBlue, trailingIcon = Icons.Rounded.KeyboardArrowDown, modifier = Modifier.fillMaxWidth(),
                                infoText = "How often interest is calculated and added to your principal. More frequent compounding leads to higher returns."
                            )
                            DropdownMenu(
                                expanded = showCompoundingDropdown,
                                onDismissRequest = { showCompoundingDropdown = false },
                                modifier = Modifier.background(SurfaceDark).fillMaxWidth(0.9f)
                            ) {
                                listOf("Yearly", "Half-Yearly", "Quarterly", "Monthly").forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option, color = TextPrimary) },
                                        onClick = {
                                            viewModel.updateInputs(compoundingFreq = option)
                                            showCompoundingDropdown = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                )
                FinancialDisclaimer()"""

content = content.replace(target, replacement)

with open(filename, 'w') as f:
    f.write(content)
print("Done")
