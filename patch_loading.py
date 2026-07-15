import sys

file_path = "app/src/main/java/com/loanmaster/pro/feature/loaneligibility/components/LoadingState.kt"
with open(file_path, "r") as f:
    content = f.read()

content = content.replace("height(280.dp)", "height(LoanMasterTheme.components.chartHeight)")
content = content.replace("height(140.dp)", "height(LoanMasterTheme.components.bannerHeight)")

with open(file_path, "w") as f:
    f.write(content)
print("Patched loading state")
