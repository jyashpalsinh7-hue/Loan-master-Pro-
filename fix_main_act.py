import re
path = "app/src/main/java/com/loanmaster/pro/MainActivity.kt"
with open(path, "r") as f:
    content = f.read()

content = content.replace("val activeRoute by mainViewModel.activeBottomNavItem.collectAsStateWithLifecycle()\n", "")
content = content.replace("val selectedHistory = mainUiState.selectedHistory\n", "")

with open(path, "w") as f:
    f.write(content)
