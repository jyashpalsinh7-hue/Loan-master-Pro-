with open('app/src/main/java/com/loanmaster/pro/feature/loaneligibility/LoanEligibilityScreen.kt', 'r') as f:
    content = f.read()

content = content.replace('.heightIn(min = LoanMasterTheme.components.buttonHeight)', '.heightIn(min = 64.dp)')
content = content.replace('.heightIn(min = LoanMasterTheme.components.topAppBarHeight)', '.heightIn(min = 72.dp)')

with open('app/src/main/java/com/loanmaster/pro/feature/loaneligibility/LoanEligibilityScreen.kt', 'w') as f:
    f.write(content)
