path = "app/src/main/java/com/loanmaster/pro/MainActivity.kt"
with open(path, "r") as f:
    content = f.read()

# Let's fix the activeBottomNavItem and mainUiState thing
import re

content = re.sub(
    r'val mainUiState by mainViewModel\.uiState\.collectAsStateWithLifecycle\(\)\s*val activeBottomNavItem = mainUiState\.activeBottomNavItem',
    'val mainUiState by mainViewModel.uiState.collectAsStateWithLifecycle()\n                    val activeBottomNavItem = mainUiState.activeBottomNavItem',
    content
)

# wait, the error says:
# e: file:///app/applet/app/src/main/java/com/loanmaster/pro/MainActivity.kt:147:54 Unresolved reference 'activeBottomNavItem'.
# Which means it still has: val activeBottomNavItem by mainViewModel.activeBottomNavItem...
# Let's completely replace it:
content = re.sub(r'val activeBottomNavItem by mainViewModel\.activeBottomNavItem\.collectAsStateWithLifecycle\(\)', 'val mainUiState by mainViewModel.uiState.collectAsStateWithLifecycle()\n                    val activeBottomNavItem = mainUiState.activeBottomNavItem', content)
content = re.sub(r'val selectedHistory by mainViewModel\.selectedHistory\.collectAsStateWithLifecycle\(\)', 'val selectedHistory = mainUiState.selectedHistory', content)

# I should also ensure import androidx.lifecycle.compose.collectAsStateWithLifecycle is present
if "import androidx.lifecycle.compose.collectAsStateWithLifecycle" not in content:
    content = content.replace("import androidx.compose.runtime.getValue", "import androidx.compose.runtime.getValue\nimport androidx.lifecycle.compose.collectAsStateWithLifecycle")

with open(path, "w") as f:
    f.write(content)
