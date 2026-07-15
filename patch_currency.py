import sys

file_path = "app/src/main/java/com/loanmaster/pro/feature/currency/CurrencyScreen.kt"
with open(file_path, "r") as f:
    content = f.read()

target = "min = 200.dp, max = 300.dp"
replacement = "min = LoanMasterTheme.components.chartHeight, max = LoanMasterTheme.components.chartHeight + 50.dp"

if target in content:
    content = content.replace(target, replacement)
    with open(file_path, "w") as f:
        f.write(content)
    print("Patched currency screen")
else:
    print("Target not found")
