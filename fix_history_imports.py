import sys

filename = 'app/src/main/java/com/loanmaster/pro/feature/history/HistoryScreen.kt'
with open(filename, 'r') as f:
    content = f.read()

target = """fun HistoryScreen("""
replacement = """import com.loanmaster.pro.feature.history.components.EmptyHistoryIllustration
import com.loanmaster.pro.feature.history.components.HistoryItemCard

@Composable
fun HistoryScreen("""

content = content.replace(target, replacement)

with open(filename, 'w') as f:
    f.write(content)
print("Done")
