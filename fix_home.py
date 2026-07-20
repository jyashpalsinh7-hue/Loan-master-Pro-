import os

filepath = 'app/src/main/java/com/loanmaster/pro/feature/home/HomeScreen.kt'
with open(filepath, 'r') as f:
    content = f.read()

target = "                columns = androidx.compose.foundation.lazy.grid.GridCells.Adaptive(minSize = 160.dp),"
replacement = "                columns = androidx.compose.foundation.lazy.grid.GridCells.Fixed(columns),"

if target in content:
    content = content.replace(target, replacement)
    with open(filepath, 'w') as f:
        f.write(content)
    print("Fixed GridCells in HomeScreen.kt")
else:
    print("Target not found")
