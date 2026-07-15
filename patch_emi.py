import sys

file_path = "app/src/main/java/com/loanmaster/pro/feature/emi/EmiScreen.kt"
with open(file_path, "r") as f:
    content = f.read()

target = "val cardWidth = if (isExpanded) 220.dp else LoanMasterTheme.components.featuredCardHeight"
replacement = "val cardWidth = if (isExpanded) LoanMasterTheme.components.bannerHeight else LoanMasterTheme.components.featuredCardHeight"

if target in content:
    content = content.replace(target, replacement)
    with open(file_path, "w") as f:
        f.write(content)
    print("Patched emi screen width")
else:
    print("Target not found")
