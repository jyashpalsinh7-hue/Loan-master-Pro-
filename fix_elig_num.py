import re
with open('app/src/main/java/com/loanmaster/pro/feature/loaneligibility/LoanEligibilityScreen.kt', 'r') as f:
    content = f.read()

content = content.replace('fun AutoResizeTextField(\n    isNumeric: Boolean = false,', 'fun AutoResizeTextField(\n    isNumeric: Boolean = true,')
content = content.replace('AutoResizeTextField(\n                            value = selectedLoanProfile,', 'AutoResizeTextField(\n                            isNumeric = false,\n                            value = selectedLoanProfile,')

with open('app/src/main/java/com/loanmaster/pro/feature/loaneligibility/LoanEligibilityScreen.kt', 'w') as f:
    f.write(content)
