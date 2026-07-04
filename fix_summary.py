import re
file_path = 'app/src/main/java/com/loanmaster/pro/LoanSummaryScreen.kt'
with open(file_path, 'r') as f:
    content = f.read()

content = content.replace('.size(36.dp)', '.size(LoanMasterTheme.components.iconLarge)')
content = content.replace('RoundedCornerShape(3.dp)', 'RoundedCornerShape(4.dp)') # 3dp is close to 4dp

with open(file_path, 'w') as f:
    f.write(content)
