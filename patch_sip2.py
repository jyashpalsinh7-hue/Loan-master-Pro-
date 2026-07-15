import sys

file_path = "app/src/main/java/com/loanmaster/pro/feature/sip/SipScreen.kt"
with open(file_path, "r") as f:
    content = f.read()

content = content.replace("columns = androidx.compose.foundation.lazy.grid.GridCells.Adaptive(minSize = 160.dp)", 
                          "columns = androidx.compose.foundation.lazy.grid.GridCells.Fixed(LoanMasterTheme.grids.calculatorColumns)")

with open(file_path, "w") as f:
    f.write(content)
print("Patched SipScreen grids")
