with open("app/src/main/java/com/loanmaster/pro/feature/settings/SettingsScreen.kt", "r") as f:
    content = f.read()

# Remove the incorrectly placed dialog block
bad_block = """
        if (showUnlockDialog) {
            val context = androidx.compose.ui.platform.LocalContext.current
            com.loanmaster.pro.core.ui.PremiumUnlockDialog(
                onDismiss = { showUnlockDialog = false },
                onUnlockSuccessful = {
                    com.loanmaster.pro.core.managers.PremiumManager(context).unlockPermanent()
                }
            )
        }
"""
content = content.replace(bad_block, "")

# Insert it at the end of SettingsScreen
# SettingsScreen ends with:
#             item { Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.md)) }
#         }
#     }

good_block = """
            item { Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.md)) }
        }
        if (showUnlockDialog) {
            val context = androidx.compose.ui.platform.LocalContext.current
            com.loanmaster.pro.core.ui.PremiumUnlockDialog(
                onDismiss = { showUnlockDialog = false },
                onUnlockSuccessful = {
                    com.loanmaster.pro.core.managers.PremiumManager(context).unlockPermanent()
                }
            )
        }
"""
content = content.replace("            item { Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.md)) }\n        }", good_block)

with open("app/src/main/java/com/loanmaster/pro/feature/settings/SettingsScreen.kt", "w") as f:
    f.write(content)
