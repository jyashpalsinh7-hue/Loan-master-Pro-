import re
with open("app/src/main/java/com/loanmaster/pro/MainActivity.kt", "r") as f:
    code = f.read()

code = code.replace(
    'LocalCurrency provides currency,',
    'LocalCurrency provides currency,\n                LocalCurrencySymbol provides symbol,'
)

with open("app/src/main/java/com/loanmaster/pro/MainActivity.kt", "w") as f:
    f.write(code)
