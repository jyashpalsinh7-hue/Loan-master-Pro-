import sys, os

for root, _, files in os.walk("app/src/main/java/com/loanmaster/pro/"):
    for file in files:
        if file.endswith(".kt"):
            path = os.path.join(root, file)
            with open(path, "r") as f:
                content = f.read()
            
            original = content
            content = content.replace("Modifier.height(10.dp)", "Modifier.height(LoanMasterTheme.spacing.sm)")
            content = content.replace("Modifier.height(12.dp)", "Modifier.height(LoanMasterTheme.spacing.md)")
            content = content.replace("Modifier.height(16.dp)", "Modifier.height(LoanMasterTheme.spacing.md)")
            content = content.replace("Modifier.height(20.dp)", "Modifier.height(LoanMasterTheme.spacing.lg)")
            content = content.replace("Modifier.height(24.dp)", "Modifier.height(LoanMasterTheme.spacing.lg)")
            content = content.replace("Modifier.height(40.dp)", "Modifier.height(LoanMasterTheme.spacing.xl)")
            content = content.replace("Modifier.height(72.dp)", "Modifier.height(LoanMasterTheme.components.bottomNavHeight)")
            
            if content != original:
                with open(path, "w") as f:
                    f.write(content)
                print(f"Patched spacers in {path}")
