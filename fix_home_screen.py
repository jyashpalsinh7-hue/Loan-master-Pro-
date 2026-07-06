import re

path = "app/src/main/java/com/loanmaster/pro/feature/home/HomeScreen.kt"
with open(path, "r") as f:
    content = f.read()

content = content.replace("val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()", 
    "val uiState by viewModel.uiState.collectAsStateWithLifecycle()\n    val searchQuery = uiState.searchQuery\n    val isQuickToolsExpanded = uiState.isQuickToolsExpanded")
content = content.replace("val isQuickToolsExpanded by viewModel.isQuickToolsExpanded.collectAsStateWithLifecycle()\n", "")
content = content.replace("val isQuickToolsExpanded by viewModel.isQuickToolsExpanded.collectAsStateWithLifecycle()", "")
with open(path, "w") as f:
    f.write(content)


nav_path = "app/src/main/java/com/loanmaster/pro/core/navigation/AppNavigation.kt"
with open(nav_path, "r") as f:
    nav_content = f.read()

nav_content = nav_content.replace("val activeBottomNavItem by mainViewModel.activeBottomNavItem.collectAsStateWithLifecycle()", 
    "val mainUiState by mainViewModel.uiState.collectAsStateWithLifecycle()\n    val activeBottomNavItem = mainUiState.activeBottomNavItem")
nav_content = nav_content.replace("val activeBottomNavItem by mainViewModel.activeBottomNavItem.collectAsStateWithLifecycle()\n", "")

with open(nav_path, "w") as f:
    f.write(nav_content)

