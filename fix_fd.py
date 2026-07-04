import re
file_path = 'app/src/main/java/com/loanmaster/pro/FdCalculatorScreen.kt'
with open(file_path, 'r') as f:
    content = f.read()

content = content.replace('Modifier.padding(start = 8.dp, bottom = 4.dp)', 'Modifier.padding(start = LoanMasterTheme.spacing.sm, bottom = LoanMasterTheme.spacing.xs)')

with open(file_path, 'w') as f:
    f.write(content)
