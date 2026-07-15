import sys

file_path = "app/src/main/java/com/loanmaster/pro/feature/home/HomeScreen.kt"
with open(file_path, "r") as f:
    content = f.read()

target = """                columns = androidx.compose.foundation.lazy.grid.GridCells.Adaptive(minSize = LoanMasterTheme.components.featuredCardHeight),"""
replacement = """                columns = androidx.compose.foundation.lazy.grid.GridCells.Fixed(columns),"""

if target in content:
    content = content.replace(target, replacement)
    with open(file_path, "w") as f:
        f.write(content)
    print("Patched home screen")
else:
    print("Target not found")
