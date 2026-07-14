import os

file_path = "app/src/main/java/com/loanmaster/pro/feature/settings/SettingsScreen.kt"
with open(file_path, "r") as f:
    content = f.read()

target_item = "item { DataBackupSection(onClearHistory = onClearHistory) }"

replacement_item = """item { DataBackupSection(onClearHistory = onClearHistory) }
            item { SupportAppSection() }"""

if "SupportAppSection()" not in content:
    content = content.replace(target_item, replacement_item)
    
    # Add SupportAppSection composable at the end
    support_app_code = """
@Composable
private fun SupportAppSection() {
    val context = androidx.compose.ui.platform.LocalContext.current
    var isAdPlaying by remember { mutableStateOf(false) }

    SettingsSection(title = "Support") {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = LoanMasterTheme.spacing.sm),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Support the Developer",
                    color = Color.White,
                    fontSize = LoanMasterTheme.typography.body.fontSize,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Watch a short ad to support future updates.",
                    color = TextSec,
                    fontSize = LoanMasterTheme.typography.label.fontSize
                )
            }
            Button(
                onClick = {
                    val activity = context as? android.app.Activity
                    if (activity != null) {
                        isAdPlaying = true
                        com.loanmaster.pro.core.ads.RewardedAdManager.showAd(activity) {
                            isAdPlaying = false
                            // You could show a toast here to thank the user
                            android.widget.Toast.makeText(context, "Thank you for your support!", android.widget.Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                enabled = !isAdPlaying,
                colors = ButtonDefaults.buttonColors(containerColor = AccentGreen, contentColor = BackgroundDark)
            ) {
                Text("Watch Ad")
            }
        }
    }
}
"""
    content += support_app_code
    with open(file_path, "w") as f:
        f.write(content)
    print("Done")
else:
    print("Already there")
