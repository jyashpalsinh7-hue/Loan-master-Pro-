import re

hist_path = "app/src/main/java/com/loanmaster/pro/feature/history/HistoryScreen.kt"
with open(hist_path, "r") as f:
    hist_content = f.read()

hist_content = hist_content.replace("historyItems.isNotEmpty()", "historyItems.isNotEmpty()")
hist_content = hist_content.replace("historyItems.isEmpty()", "historyItems.isEmpty()")
hist_content = hist_content.replace("items(historyItems, key", "items(historyItems, key")

# Let's completely replace the broken state line
new_state_line = """    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val historyItems = uiState.historyList"""

hist_content = re.sub(r'val uiState by viewModel\.uiState\.collectAsStateWithLifecycle\(\)\s*val historyItems = uiState\.historyList', new_state_line, hist_content)
hist_content = re.sub(r'val uiState by viewModel\.uiState\.collectAsStateWithLifecycle\(\)', new_state_line, hist_content)

# Remove duplicate
hist_content = hist_content.replace(new_state_line + "\n    val historyItems = uiState.historyList", new_state_line)
hist_content = hist_content.replace(new_state_line + "    val historyItems = uiState.historyList", new_state_line)


with open(hist_path, "w") as f:
    f.write(hist_content)
