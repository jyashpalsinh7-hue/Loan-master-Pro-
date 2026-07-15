import sys

file_path = "app/src/main/java/com/loanmaster/pro/feature/sip/SipScreen.kt"
with open(file_path, "r") as f:
    content = f.read()

target = "min = 280.dp"
replacement = "min = LoanMasterTheme.components.chartHeight"

if target in content:
    content = content.replace(target, replacement)
    with open(file_path, "w") as f:
        f.write(content)
    print("Patched SipScreen chart height")
else:
    print("Target not found")
