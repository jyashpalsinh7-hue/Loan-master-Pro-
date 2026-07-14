import os

file_path = "app/src/main/java/com/loanmaster/pro/feature/settings/SettingsScreen.kt"
with open(file_path, "r") as f:
    content = f.read()

target = """@Composable
private fun SupportAppSection() {
    val context = androidx.compose.ui.platform.LocalContext.current
    var isAdPlaying by remember { mutableStateOf(false) }

    SectionCard(title = "Support") {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = LoanMasterTheme.spacing.sm),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f).padding(end = LoanMasterTheme.spacing.md)) {
                Text(
                    text = "Support the Developer",
                    color = Color.White,
                    fontSize = LoanMasterTheme.typography.body.fontSize,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Watch a short ad to support future updates.",
                    color = TextSecondary,
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
}"""

replacement = """@Composable
private fun SupportAppSection() {
    val context = androidx.compose.ui.platform.LocalContext.current
    var isAdPlaying by remember { mutableStateOf(false) }

    SectionCard(title = "Premium & Support") {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = LoanMasterTheme.spacing.sm),
            verticalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.md)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = LoanMasterTheme.spacing.md)) {
                    Text(
                        text = "Unlock Premium",
                        color = Color.White,
                        fontSize = LoanMasterTheme.typography.body.fontSize,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Remove ads and get advanced features.",
                        color = TextSecondary,
                        fontSize = LoanMasterTheme.typography.label.fontSize
                    )
                }
                Button(
                    onClick = {
                        android.widget.Toast.makeText(context, "Premium coming soon!", android.widget.Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentGreen, contentColor = BackgroundDark)
                ) {
                    Text("Buy Premium")
                }
            }
            
            HorizontalDivider(color = CardBackgroundLight)
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = LoanMasterTheme.spacing.md)) {
                    Text(
                        text = "Support with Ads",
                        color = Color.White,
                        fontSize = LoanMasterTheme.typography.body.fontSize,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Watch a short ad to support the developer.",
                        color = TextSecondary,
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
                                android.widget.Toast.makeText(context, "Thank you for your support!", android.widget.Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    enabled = !isAdPlaying,
                    colors = ButtonDefaults.buttonColors(containerColor = CardBackgroundLight, contentColor = Color.White)
                ) {
                    Text("Watch Ad")
                }
            }
        }
    }
}"""

if target in content:
    content = content.replace(target, replacement)
    with open(file_path, "w") as f:
        f.write(content)
    print("Done")
else:
    print("Target not found")
