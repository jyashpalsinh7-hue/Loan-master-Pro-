import re
file_path = 'app/src/main/java/com/loanmaster/pro/ui/theme/LoanMasterTheme.kt'
with open(file_path, 'r') as f:
    content = f.read()
content = content.replace('fontSize = LoanMasterTheme.typography.label.fontSize', 'fontSize = 14.sp')
with open(file_path, 'w') as f:
    f.write(content)
