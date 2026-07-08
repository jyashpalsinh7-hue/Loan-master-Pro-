with open('app/src/main/java/com/loanmaster/pro/feature/rd/RdScreen.kt', 'r') as f:
    content = f.read()

content = content.replace('PremiumInputField(\n                            label = "Compounding"', 'PremiumInputField(\n                            isNumeric = false,\n                            label = "Compounding"')
content = content.replace('PremiumInputField(\n                                label = "Compounding"', 'PremiumInputField(\n                                isNumeric = false,\n                                label = "Compounding"')

with open('app/src/main/java/com/loanmaster/pro/feature/rd/RdScreen.kt', 'w') as f:
    f.write(content)

with open('app/src/main/java/com/loanmaster/pro/feature/fd/FdScreen.kt', 'r') as f:
    content = f.read()

content = content.replace('PremiumInputField(\n                            label = "Compounding"', 'PremiumInputField(\n                            isNumeric = false,\n                            label = "Compounding"')
content = content.replace('PremiumInputField(\n                                label = "Compounding"', 'PremiumInputField(\n                                isNumeric = false,\n                                label = "Compounding"')

with open('app/src/main/java/com/loanmaster/pro/feature/fd/FdScreen.kt', 'w') as f:
    f.write(content)
