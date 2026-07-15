import sys, os

for root, _, files in os.walk("app/src/main/java/com/loanmaster/pro/"):
    for file in files:
        if file.endswith(".kt"):
            path = os.path.join(root, file)
            with open(path, "r") as f:
                content = f.read()
            
            original = content
            content = content.replace("Modifier.height(18.dp)", "Modifier.height(LoanMasterTheme.spacing.md)")
            
            if content != original:
                with open(path, "w") as f:
                    f.write(content)
                print(f"Patched more spacers in {path}")
