import sys

filename = 'app/src/main/java/com/loanmaster/pro/feature/history/HistoryScreen.kt'
with open(filename, 'r') as f:
    content = f.read()

# Fix unresolved imports. We removed them by accident earlier.
target_import = """import androidx.compose.foundation.lazy.itemsIndexed

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)"""
replacement_import = """import androidx.compose.foundation.lazy.itemsIndexed
import com.loanmaster.pro.feature.history.components.EmptyHistoryIllustration
import com.loanmaster.pro.feature.history.components.HistoryItemCard

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)"""

content = content.replace(target_import, replacement_import)

with open(filename, 'w') as f:
    f.write(content)
print("Done")
