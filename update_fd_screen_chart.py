import re

with open("app/src/main/java/com/loanmaster/pro/FdCalculatorScreen.kt", "r") as f:
    content = f.read()

# Add yearBreakdown to state collection
state_block_orig = """    val hasValidInput by viewModel.hasValidInput.collectAsStateWithLifecycle()"""

state_block_new = """    val hasValidInput by viewModel.hasValidInput.collectAsStateWithLifecycle()
    val yearBreakdown by viewModel.yearBreakdown.collectAsStateWithLifecycle()"""

content = content.replace(state_block_orig, state_block_new)

# Update the loop
old_loop = """                    val yearsList = listOf(1.0, 2.0, 3.0, 4.0, 5.0) // Matching the screenshot exactly
                    yearsList.forEachIndexed { index, y ->
                        val tMat = p * (1 + r / n).pow(n * y)
                        val tRet = tMat - p
                        val isLast = index == yearsList.size - 1
                        val color = if (isLast) AccentBlue else TextPrimary
                        val formatInrNum = { value: Double -> formatMoneyExact(value) }
                        
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = LoanMasterTheme.spacing.sm),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("${y.toInt()} Yr", color = color, fontSize = LoanMasterTheme.typography.label.fontSize, modifier = Modifier.weight(0.5f).padding(horizontal = LoanMasterTheme.spacing.xs), textAlign = TextAlign.Start)
                            Text(formatInrNum(p), color = color, fontSize = LoanMasterTheme.typography.label.fontSize, modifier = Modifier.weight(1.5f).padding(horizontal = LoanMasterTheme.spacing.xs), textAlign = TextAlign.Center)
                            Text(formatInrNum(tRet), color = color, fontSize = LoanMasterTheme.typography.label.fontSize, modifier = Modifier.weight(1.5f).padding(horizontal = LoanMasterTheme.spacing.xs), textAlign = TextAlign.Center)
                            Text(formatInrNum(tMat), color = color, fontSize = LoanMasterTheme.typography.label.fontSize, modifier = Modifier.weight(1.5f).padding(horizontal = LoanMasterTheme.spacing.xs), textAlign = TextAlign.End)
                        }
                        if (!isLast) {
                            HorizontalDivider(color = BorderDark, thickness = 1.dp)
                        }
                    }"""

new_loop = """                    yearBreakdown.forEachIndexed { index, breakdown ->
                        val isLast = index == yearBreakdown.size - 1
                        val color = if (isLast) AccentBlue else TextPrimary
                        val formatInrNum = { value: Double -> formatMoneyExact(value) }
                        
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = LoanMasterTheme.spacing.sm),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("${breakdown.year} Yr", color = color, fontSize = LoanMasterTheme.typography.label.fontSize, modifier = Modifier.weight(0.5f).padding(horizontal = LoanMasterTheme.spacing.xs), textAlign = TextAlign.Start)
                            Text(formatInrNum(breakdown.deposit), color = color, fontSize = LoanMasterTheme.typography.label.fontSize, modifier = Modifier.weight(1.5f).padding(horizontal = LoanMasterTheme.spacing.xs), textAlign = TextAlign.Center)
                            Text(formatInrNum(breakdown.interest), color = color, fontSize = LoanMasterTheme.typography.label.fontSize, modifier = Modifier.weight(1.5f).padding(horizontal = LoanMasterTheme.spacing.xs), textAlign = TextAlign.Center)
                            Text(formatInrNum(breakdown.maturity), color = color, fontSize = LoanMasterTheme.typography.label.fontSize, modifier = Modifier.weight(1.5f).padding(horizontal = LoanMasterTheme.spacing.xs), textAlign = TextAlign.End)
                        }
                        if (!isLast) {
                            HorizontalDivider(color = BorderDark, thickness = 1.dp)
                        }
                    }"""

content = content.replace(old_loop, new_loop)

with open("app/src/main/java/com/loanmaster/pro/FdCalculatorScreen.kt", "w") as f:
    f.write(content)
