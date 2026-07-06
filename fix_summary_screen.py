import re

path = "app/src/main/java/com/loanmaster/pro/feature/loansummary/LoanSummaryScreen.kt"
with open(path, "r") as f:
    content = f.read()

if "import androidx.lifecycle.compose.collectAsStateWithLifecycle" not in content:
    content = content.replace("import androidx.compose.ui.unit.dp\n", "import androidx.compose.ui.unit.dp\nimport androidx.lifecycle.compose.collectAsStateWithLifecycle\nimport androidx.compose.runtime.getValue\n")

with open(path, "w") as f:
    f.write(content)
