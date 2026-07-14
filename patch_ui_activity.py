import sys

file_path = "app/src/main/java/com/loanmaster/pro/feature/loaneligibility/LoanEligibilityScreen.kt"
with open(file_path, "r") as f:
    content = f.read()

target = """fun LoanEligibilityScreen(
    onNavigateBack: () -> Unit
) {"""

replacement = """import android.app.Activity
import androidx.compose.ui.platform.LocalContext

@Composable
fun LoanEligibilityScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity"""

if "fun LoanEligibilityScreen(" in content and "val activity" not in content:
    content = content.replace("fun LoanEligibilityScreen(\n    onNavigateBack: () -> Unit\n) {", replacement)
    with open(file_path, "w") as f:
        f.write(content)
    print("Patched UI activity")
else:
    print("Not found or already patched")
