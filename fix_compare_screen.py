import os

file_path = "app/src/main/java/com/loanmaster/pro/feature/compare/CompareScreen.kt"
with open(file_path, "r") as f:
    content = f.read()

target = """        Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.md))
        Text("Tenure", color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize)
        Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.sm))
        Row(horizontalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.sm)) {
            androidx.compose.material3.Surface(
                shape = RoundedCornerShape(LoanMasterTheme.spacing.md),
                color = Color(0xFF0D1B36), // SurfaceDark equivalent
                border = androidx.compose.foundation.BorderStroke(1.dp, CardStroke),
                modifier = Modifier.weight(1f).heightIn(min = LoanMasterTheme.components.iconLarge)
            ) {
                Row(modifier = Modifier.padding(horizontal = LoanMasterTheme.spacing.sm), verticalAlignment = Alignment.CenterVertically) {
                    androidx.compose.foundation.text.BasicTextField(
                        value = state.yearsText,
                        onValueChange = { onYearsChange(it.filter { c -> c.isDigit() }) },
                        textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = LoanMasterTheme.typography.label.fontSize),
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        cursorBrush = androidx.compose.ui.graphics.SolidColor(AccentBlue)
                    ) { inner ->
                        Box {
                            if (state.yearsText.isEmpty()) Text("Years", color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                            inner()
                        }
                    }
                }
            }
            androidx.compose.material3.Surface(
                shape = RoundedCornerShape(LoanMasterTheme.spacing.md),
                color = Color(0xFF0D1B36),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardStroke),
                modifier = Modifier.weight(1f).heightIn(min = LoanMasterTheme.components.iconLarge)
            ) {
                Row(modifier = Modifier.padding(horizontal = LoanMasterTheme.spacing.sm), verticalAlignment = Alignment.CenterVertically) {
                    androidx.compose.foundation.text.BasicTextField(
                        value = state.monthsText,
                        onValueChange = { onMonthsChange(it.filter { c -> c.isDigit() }) },
                        textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = LoanMasterTheme.typography.label.fontSize),
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        cursorBrush = androidx.compose.ui.graphics.SolidColor(AccentBlue)
                    ) { inner ->
                        Box {
                            if (state.monthsText.isEmpty()) Text("Months", color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                            inner()
                        }
                    }
                }
            }
        }"""

replacement = """        Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.md))
        Text("Tenure", color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize)
        Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.sm))
        Row(horizontalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.sm)) {
            var yearsFocused by remember { mutableStateOf(false) }
            val yearsBorderColor by androidx.compose.animation.animateColorAsState(targetValue = if (yearsFocused) AccentBlue else CardStroke)
            val yearsBorderWidth by androidx.compose.animation.core.animateDpAsState(targetValue = if (yearsFocused) 2.dp else 1.dp)

            androidx.compose.material3.Surface(
                shape = RoundedCornerShape(LoanMasterTheme.spacing.md),
                color = Color(0xFF0D1B36), // SurfaceDark equivalent
                border = androidx.compose.foundation.BorderStroke(yearsBorderWidth, yearsBorderColor),
                modifier = Modifier.weight(1f).heightIn(min = LoanMasterTheme.components.iconLarge)
            ) {
                Row(modifier = Modifier.padding(horizontal = LoanMasterTheme.spacing.sm), verticalAlignment = Alignment.CenterVertically) {
                    androidx.compose.foundation.text.BasicTextField(
                        value = state.yearsText,
                        onValueChange = { onYearsChange(it.filter { c -> c.isDigit() }) },
                        textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = LoanMasterTheme.typography.label.fontSize),
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                        modifier = Modifier.weight(1f).androidx.compose.ui.focus.onFocusChanged { yearsFocused = it.isFocused },
                        cursorBrush = androidx.compose.ui.graphics.SolidColor(AccentBlue)
                    ) { inner ->
                        Box {
                            if (state.yearsText.isEmpty()) Text("Years", color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                            inner()
                        }
                    }
                }
            }

            var monthsFocused by remember { mutableStateOf(false) }
            val monthsBorderColor by androidx.compose.animation.animateColorAsState(targetValue = if (monthsFocused) AccentBlue else CardStroke)
            val monthsBorderWidth by androidx.compose.animation.core.animateDpAsState(targetValue = if (monthsFocused) 2.dp else 1.dp)

            androidx.compose.material3.Surface(
                shape = RoundedCornerShape(LoanMasterTheme.spacing.md),
                color = Color(0xFF0D1B36),
                border = androidx.compose.foundation.BorderStroke(monthsBorderWidth, monthsBorderColor),
                modifier = Modifier.weight(1f).heightIn(min = LoanMasterTheme.components.iconLarge)
            ) {
                Row(modifier = Modifier.padding(horizontal = LoanMasterTheme.spacing.sm), verticalAlignment = Alignment.CenterVertically) {
                    androidx.compose.foundation.text.BasicTextField(
                        value = state.monthsText,
                        onValueChange = { onMonthsChange(it.filter { c -> c.isDigit() }) },
                        textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = LoanMasterTheme.typography.label.fontSize),
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                        modifier = Modifier.weight(1f).androidx.compose.ui.focus.onFocusChanged { monthsFocused = it.isFocused },
                        cursorBrush = androidx.compose.ui.graphics.SolidColor(AccentBlue)
                    ) { inner ->
                        Box {
                            if (state.monthsText.isEmpty()) Text("Months", color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                            inner()
                        }
                    }
                }
            }
        }"""

content = content.replace(target, replacement)

# Add imports
if "import androidx.compose.ui.focus.onFocusChanged" not in content:
    content = content.replace("import androidx.compose.ui.unit.sp", "import androidx.compose.ui.unit.sp\nimport androidx.compose.ui.focus.onFocusChanged")

with open(file_path, "w") as f:
    f.write(content)

print("Done")
