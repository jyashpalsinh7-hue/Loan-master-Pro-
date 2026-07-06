import re

path1 = "app/src/main/java/com/loanmaster/pro/feature/home/HomeScreen.kt"
with open(path1, "r") as f:
    content1 = f.read()
content1 = content1.replace("val activeBottomNavItem by viewModel.activeBottomNavItem.collectAsStateWithLifecycle()", "val activeBottomNavItem = uiState.activeBottomNavItem")
content1 = content1.replace("viewModel.updateSearchQuery", "viewModel::updateSearchQuery")
content1 = content1.replace("viewModel.toggleQuickToolsExpanded", "viewModel::toggleQuickToolsExpanded")
with open(path1, "w") as f:
    f.write(content1)

path2 = "app/src/main/java/com/loanmaster/pro/feature/loansummary/LoanSummaryScreen.kt"
with open(path2, "r") as f:
    content2 = f.read()
content2 = content2.replace("val uiState by viewModel.uiState.collectAsStateWithLifecycle()", "val uiState by viewModel.uiState.collectAsStateWithLifecycle()")
with open(path2, "w") as f:
    f.write(content2)
