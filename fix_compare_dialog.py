import os

def replace_in_file(file_path, old_text, new_text):
    with open(file_path, "r") as f:
        content = f.read()
    if old_text in content:
        content = content.replace(old_text, new_text)
        with open(file_path, "w") as f:
            f.write(content)
        print(f"Updated {file_path}")
    else:
        print(f"Old text not found in {file_path}")


compare_target = """        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showUnlockDialog = false },
            containerColor = SurfaceDark,
            titleContentColor = Color.White,
            textContentColor = TextSecondary,
            title = {
                Text("Unlock Premium", fontWeight = FontWeight.Bold)
            },
            text = {
                Text("Comparing more than 3 loans is a premium feature. Watch a short ad or upgrade to Premium to unlock unlimited comparisons!")
            },
            confirmButton = {
                Button(
                    onClick = {
                        isPremiumUnlocked = true
                        showUnlockDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentYellow, contentColor = Color.Black)
                ) {
                    Text("Watch Ad / Go Premium", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showUnlockDialog = false }) {
                    Text("Cancel", color = Color.White)
                }
            }
        )"""

compare_replacement = """        com.loanmaster.pro.core.ui.PremiumUnlockDialog(
            onDismiss = { showUnlockDialog = false },
            onUnlockSuccessful = { isPremiumUnlocked = true }
        )"""

replace_in_file("app/src/main/java/com/loanmaster/pro/feature/compare/CompareScreen.kt", compare_target, compare_replacement)
