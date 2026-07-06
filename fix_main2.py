path = "app/src/main/java/com/loanmaster/pro/MainActivity.kt"
with open(path, "r") as f:
    content = f.read()

import re

content = re.sub(
    r'val activeRoute by mainViewModel\.activeBottomNavItem\.collectAsStateWithLifecycle\(\)',
    'val mainUiState by mainViewModel.uiState.collectAsStateWithLifecycle()\n                    val activeRoute = mainUiState.activeBottomNavItem',
    content
)

with open(path, "w") as f:
    f.write(content)

