import sys

filename = 'app/src/main/java/com/loanmaster/pro/feature/history/HistoryScreen.kt'
with open(filename, 'r') as f:
    content = f.read()

target = """@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
import com.loanmaster.pro.feature.history.components.EmptyHistoryIllustration
import com.loanmaster.pro.feature.history.components.HistoryItemCard

@Composable"""

replacement = """import com.loanmaster.pro.feature.history.components.EmptyHistoryIllustration
import com.loanmaster.pro.feature.history.components.HistoryItemCard

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable"""

content = content.replace(target, replacement)

with open(filename, 'w') as f:
    f.write(content)
print("Done")
