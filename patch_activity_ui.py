import sys
import re

file_path = "app/src/main/java/com/loanmaster/pro/feature/loaneligibility/LoanEligibilityScreen.kt"
with open(file_path, "r") as f:
    content = f.read()

# I need to add import android.app.Activity and import androidx.compose.ui.platform.LocalContext at the top
if "import android.app.Activity" not in content:
    content = content.replace("import androidx.compose.runtime.*", "import androidx.compose.runtime.*\nimport android.app.Activity\nimport androidx.compose.ui.platform.LocalContext")

# Now I need to inject `val activity = LocalContext.current as? Activity` at the beginning of the LoanEligibilityScreen function
# We can find the opening brace of LoanEligibilityScreen
target = "fun LoanEligibilityScreen(onNavigateBack: () -> Unit = {}, viewModel: LoanEligibilityViewModel = viewModel()) {"
replacement = target + "\n    val context = LocalContext.current\n    val activity = context as? Activity\n"

if target in content:
    content = content.replace(target, replacement)
    with open(file_path, "w") as f:
        f.write(content)
    print("Patched activity properly")
else:
    print("Target signature not found")
