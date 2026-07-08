with open('app/src/main/java/com/loanmaster/pro/feature/compare/CompareScreen.kt', 'r') as f:
    content = f.read()

content = content.replace('PremiumInputField(\n                value = bankName,', 'PremiumInputField(\n                isNumeric = false,\n                value = bankName,')

with open('app/src/main/java/com/loanmaster/pro/feature/compare/CompareScreen.kt', 'w') as f:
    f.write(content)
