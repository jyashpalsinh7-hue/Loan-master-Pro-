with open('app/src/main/java/com/loanmaster/pro/core/theme/LoanMasterTheme.kt', 'r') as f:
    content = f.read()

content = content.replace('calculatorColumns = 1,\n            scheduleColumns = 1\n        )\n    }', 'calculatorColumns = 2,\n            scheduleColumns = 1\n        )\n    }')

with open('app/src/main/java/com/loanmaster/pro/core/theme/LoanMasterTheme.kt', 'w') as f:
    f.write(content)
