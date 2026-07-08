with open('app/src/main/java/com/loanmaster/pro/core/ui/SharedUI.kt', 'r') as f:
    content = f.read()

content = content.replace("maxTextSize = fontSize,", "maxTextSize = if (fontSize == androidx.compose.ui.unit.TextUnit.Unspecified) LoanMasterTheme.typography.body.fontSize else fontSize,")

with open('app/src/main/java/com/loanmaster/pro/core/ui/SharedUI.kt', 'w') as f:
    f.write(content)
