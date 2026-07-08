import re
with open('app/src/main/java/com/loanmaster/pro/feature/loaneligibility/LoanEligibilityScreen.kt', 'r') as f:
    content = f.read()

content = content.replace('fun AutoResizeTextField(', 'fun AutoResizeTextField(\n    isNumeric: Boolean = false,')
content = content.replace('trailingContent = trailingIcon', 'trailingContent = trailingIcon,\n        isNumeric = isNumeric')

with open('app/src/main/java/com/loanmaster/pro/feature/loaneligibility/LoanEligibilityScreen.kt', 'w') as f:
    f.write(content)
