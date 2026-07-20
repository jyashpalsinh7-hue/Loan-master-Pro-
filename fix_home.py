import sys

filename = 'app/src/main/java/com/loanmaster/pro/feature/home/HomeScreen.kt'
with open(filename, 'r') as f:
    content = f.read()

target = """            androidx.compose.foundation.lazy.grid.LazyVerticalGrid(
                columns = androidx.compose.foundation.lazy.grid.GridCells.Fixed(columns),
                modifier = Modifier"""

replacement = """            androidx.compose.foundation.lazy.grid.LazyVerticalGrid(
                columns = androidx.compose.foundation.lazy.grid.GridCells.Adaptive(minSize = 160.dp),
                modifier = Modifier"""

content = content.replace(target, replacement)

# Fix dp values
content = content.replace("width(300.dp)", "widthIn(max=300.dp)")
content = content.replace("height(500.dp)", "heightIn(max=500.dp)")
content = content.replace(".offset(x = LoanMasterTheme.spacing.xl, y = (-40).dp)", ".offset(x = LoanMasterTheme.spacing.xl, y = (-40).dp)") # this is probably fine

with open(filename, 'w') as f:
    f.write(content)
