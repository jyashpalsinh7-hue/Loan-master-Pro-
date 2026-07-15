import sys, os, glob

for root, _, files in os.walk("app/src/main/java/com/loanmaster/pro/"):
    for file in files:
        if file.endswith(".kt"):
            path = os.path.join(root, file)
            with open(path, "r") as f:
                content = f.read()
            
            original = content
            content = content.replace("padding(14.dp)", "padding(LoanMasterTheme.spacing.md)")
            content = content.replace("padding(16.dp)", "padding(LoanMasterTheme.spacing.md)")
            content = content.replace("padding(24.dp)", "padding(LoanMasterTheme.spacing.lg)")
            content = content.replace("padding(12.dp)", "padding(LoanMasterTheme.spacing.sm)")
            content = content.replace("padding(10.dp)", "padding(LoanMasterTheme.spacing.sm)")
            
            if content != original:
                with open(path, "w") as f:
                    f.write(content)
                print(f"Patched {path}")
