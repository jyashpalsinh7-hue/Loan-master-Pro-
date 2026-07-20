import sys

filename = 'app/src/main/java/com/loanmaster/pro/domain/calculator/EmiCalculator.kt'
with open(filename, 'r') as f:
    content = f.read()

content = content.replace("${com.loanmaster.pro.core.formatter.CurrencyHelper.currencySymbol}", "")

with open(filename, 'w') as f:
    f.write(content)
print("Done")
