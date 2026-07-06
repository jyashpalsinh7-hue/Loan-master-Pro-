import re

# Fix MainActivity.kt
path1 = "app/src/main/java/com/loanmaster/pro/MainActivity.kt"
with open(path1, "r") as f:
    content1 = f.read()

content1 = content1.replace("val activeBottomNavItem by mainViewModel.activeBottomNavItem.collectAsStateWithLifecycle()", "val mainUiState by mainViewModel.uiState.collectAsStateWithLifecycle()\n                    val activeBottomNavItem = mainUiState.activeBottomNavItem")
content1 = content1.replace("val selectedHistory by mainViewModel.selectedHistory.collectAsStateWithLifecycle()", "val selectedHistory = mainUiState.selectedHistory")

with open(path1, "w") as f:
    f.write(content1)

# Fix AppNavigation.kt
path2 = "app/src/main/java/com/loanmaster/pro/core/navigation/AppNavigation.kt"
with open(path2, "r") as f:
    content2 = f.read()

content2 = content2.replace("val activeLoans by loanSummaryViewModel.activeLoans.collectAsStateWithLifecycle()", "val summaryUiState by loanSummaryViewModel.uiState.collectAsStateWithLifecycle()\n                        val activeLoans = summaryUiState.activeLoans")

with open(path2, "w") as f:
    f.write(content2)

