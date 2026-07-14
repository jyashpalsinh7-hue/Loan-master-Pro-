import os

file_path = "app/src/main/java/com/loanmaster/pro/feature/sip/SipScreen.kt"
with open(file_path, "r") as f:
    content = f.read()

if "var showPremiumDialog by rememberSaveable { mutableStateOf(false) }" not in content:
    content = content.replace("val dummyCurrency = com.loanmaster.pro.LocalCurrency.current", "val dummyCurrency = com.loanmaster.pro.LocalCurrency.current\n    var showPremiumDialog by rememberSaveable { mutableStateOf(false) }")
    
    content = content.replace("onUnlockPremium = { viewModel.unlockPremium() }", "onUnlockPremium = { showPremiumDialog = true }")
    
    dialog_code = """
            if (showPremiumDialog) {
                com.loanmaster.pro.core.ui.PremiumUnlockDialog(
                    onDismiss = { showPremiumDialog = false },
                    onUnlockSuccessful = { viewModel.unlockPremium() }
                )
            }
            Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.xl))
"""
    content = content.replace("Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.xl))", dialog_code)
    
    with open(file_path, "w") as f:
        f.write(content)
    print("SipScreen updated")
else:
    print("Already updated")
