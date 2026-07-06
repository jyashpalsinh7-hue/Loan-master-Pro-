import re

path = "app/src/main/java/com/loanmaster/pro/core/navigation/AppNavigation.kt"
with open(path, "r") as f:
    content = f.read()

content = content.replace("val activeRoute by mainViewModel.activeBottomNavItem.collectAsStateWithLifecycle()", 
    "val mainUiState by mainViewModel.uiState.collectAsStateWithLifecycle()\n    val activeRoute = mainUiState.activeBottomNavItem")

with open(path, "w") as f:
    f.write(content)
