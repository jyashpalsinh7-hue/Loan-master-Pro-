import re

hist_path = "app/src/main/java/com/loanmaster/pro/feature/history/HistoryScreen.kt"
with open(hist_path, "r") as f:
    hist_content = f.read()

hist_content = hist_content.replace("val historyItems by viewModel.uiState.collectAsStateWithLifecycle()", "val uiState by viewModel.uiState.collectAsStateWithLifecycle()")
hist_content = hist_content.replace("historyItems.isNotEmpty()", "uiState.historyList.isNotEmpty()")
hist_content = hist_content.replace("historyItems.isEmpty()", "uiState.historyList.isEmpty()")
hist_content = hist_content.replace("items(historyItems, key", "items(uiState.historyList, key")
hist_content = hist_content.replace("items(historyItems)", "items(uiState.historyList)")

with open(hist_path, "w") as f:
    f.write(hist_content)
