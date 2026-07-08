with open('app/src/main/java/com/loanmaster/pro/core/ui/SharedUI.kt', 'r') as f:
    content = f.read()

content = content.replace("maxTextSize: TextUnit = LoanMasterTheme.typography.body.fontSize,", "maxTextSize: TextUnit = androidx.compose.ui.unit.TextUnit.Unspecified,")
content = content.replace("var textStyle by remember(text, maxTextSize, fontWeight, color) { mutableStateOf(TextStyle(fontSize = if (maxTextSize == androidx.compose.ui.unit.TextUnit.Unspecified) LoanMasterTheme.typography.body.fontSize else maxTextSize, fontWeight = fontWeight, color = color)) }", "val actualMaxTextSize = if (maxTextSize == androidx.compose.ui.unit.TextUnit.Unspecified) LoanMasterTheme.typography.body.fontSize else maxTextSize\n    var textStyle by remember(text, actualMaxTextSize, fontWeight, color) { mutableStateOf(TextStyle(fontSize = actualMaxTextSize, fontWeight = fontWeight, color = color)) }")

content = content.replace("maxTextSize = if (fontSize == androidx.compose.ui.unit.TextUnit.Unspecified) LoanMasterTheme.typography.body.fontSize else fontSize,", "maxTextSize = fontSize,")
content = content.replace("fontSize: TextUnit = TextUnit.Unspecified,", "fontSize: TextUnit = androidx.compose.ui.unit.TextUnit.Unspecified,")

with open('app/src/main/java/com/loanmaster/pro/core/ui/SharedUI.kt', 'w') as f:
    f.write(content)
