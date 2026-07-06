import re

path = "app/src/main/java/com/loanmaster/pro/core/navigation/AppNavigation.kt"
with open(path, "r") as f:
    content = f.read()

content = content.replace("val selectedHistory by mainViewModel.selectedHistory.collectAsStateWithLifecycle()", "val selectedHistory = mainUiState.selectedHistory")
content = content.replace("val selectedHistory by mainViewModel.selectedHistory.collectAsStateWithLifecycle()\n", "")

with open(path, "w") as f:
    f.write(content)
