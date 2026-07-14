import os

file_path = "app/src/main/java/com/loanmaster/pro/feature/rd/RdScreen.kt"
with open(file_path, "r") as f:
    content = f.read()

if "var showPremiumDialog by rememberSaveable { mutableStateOf(false) }" not in content:
    # Wait, RdScreen already has `var showUnlockDialog by rememberSaveable { mutableStateOf(false) }` maybe? Let's check from grep
    pass
