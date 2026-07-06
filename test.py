import re

hist_path = "app/src/main/java/com/loanmaster/pro/feature/history/HistoryScreen.kt"
with open(hist_path, "r") as f:
    hist_content = f.read()

hist_content = hist_content.replace("val historyItems by viewModel.historyItems.collectAsStateWithLifecycle()", "val uiState by viewModel.uiState.collectAsStateWithLifecycle()")
hist_content = hist_content.replace("val historyItems = uiState.historyList", "val historyItems = uiState.historyList")
hist_content = hist_content.replace("val uiState by viewModel.uiState.collectAsStateWithLifecycle()\n    val historyItems = uiState.historyList", "val uiState by viewModel.uiState.collectAsStateWithLifecycle()\n    val historyItems = uiState.historyList")

print(hist_content[:300])

