import os

file_path = "app/src/main/java/com/loanmaster/pro/feature/sip/SipScreen.kt"
with open(file_path, "r") as f:
    content = f.read()

target = """    if (isWide) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.lg)) {
            Box(Modifier.weight(1f)) { CustomInput("Monthly SIP", amount, onAmount, Icons.Rounded.Edit, prefix = com.loanmaster.pro.core.formatter.currentCurrencySymbol) }
            Box(Modifier.weight(1f)) { CustomInput("Expected Return", returnRate, onRate, Icons.Rounded.Edit, suffix = "%") }
            Box(Modifier.weight(1f)) { CustomInput("Period", years, onYears, Icons.Rounded.KeyboardArrowDown, suffix = " Yr") }
            Box(Modifier.weight(1f)) { CustomInput("Step-Up", stepUp, onStepUp, Icons.Rounded.KeyboardArrowDown, suffix = "%") }
        }
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.md)) {
            Row(horizontalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.md)) {
                Box(Modifier.weight(1f)) { CustomInput("Monthly SIP", amount, onAmount, Icons.Rounded.Edit, prefix = com.loanmaster.pro.core.formatter.currentCurrencySymbol) }
                Box(Modifier.weight(1f)) { CustomInput("Return Rate", returnRate, onRate, Icons.Rounded.Edit, suffix = "%") }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.md)) {
                Box(Modifier.weight(1f)) { CustomInput("Period", years, onYears, Icons.Rounded.KeyboardArrowDown, suffix = " Yr") }
                Box(Modifier.weight(1f)) { CustomInput("Step-Up", stepUp, onStepUp, Icons.Rounded.KeyboardArrowDown, suffix = "%") }
            }
        }
    }"""

replacement = """    if (isWide) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.lg)) {
            Box(Modifier.weight(1f)) { CustomInput("Monthly SIP", amount, onAmount, Icons.Rounded.Edit, prefix = com.loanmaster.pro.core.formatter.currentCurrencySymbol) }
            Box(Modifier.weight(1f)) { CustomInput("Expected Return", returnRate, onRate, Icons.Rounded.Edit, suffix = "%") }
            Box(Modifier.weight(1f)) { CustomInput("Period", years, onYears, Icons.Rounded.KeyboardArrowDown, suffix = " Yr") }
            Box(Modifier.weight(1f)) { CustomInput("Step-Up", stepUp, onStepUp, Icons.Rounded.Edit, suffix = "%") }
        }
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.md)) {
            Row(horizontalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.md)) {
                Box(Modifier.weight(1f)) { CustomInput("Monthly SIP", amount, onAmount, Icons.Rounded.Edit, prefix = com.loanmaster.pro.core.formatter.currentCurrencySymbol) }
                Box(Modifier.weight(1f)) { CustomInput("Return Rate", returnRate, onRate, Icons.Rounded.Edit, suffix = "%") }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.md)) {
                Box(Modifier.weight(1f)) { CustomInput("Period", years, onYears, Icons.Rounded.KeyboardArrowDown, suffix = " Yr") }
                Box(Modifier.weight(1f)) { CustomInput("Step-Up", stepUp, onStepUp, Icons.Rounded.Edit, suffix = "%") }
            }
        }
    }"""

if target in content:
    content = content.replace(target, replacement)
    with open(file_path, "w") as f:
        f.write(content)
    print("Done")
else:
    print("Target not found")
