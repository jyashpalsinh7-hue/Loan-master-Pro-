import sys

def fix_file(path):
    with open(path, "r") as f:
        lines = f.readlines()
    
    if len(lines) > 0 and "import com.loanmaster.pro.core.theme.*" in lines[0]:
        lines.pop(0) # remove from line 1
        # find where imports start, or insert after package
        insert_idx = 1
        for i, line in enumerate(lines):
            if line.startswith("package "):
                insert_idx = i + 1
                break
        
        lines.insert(insert_idx, "\nimport com.loanmaster.pro.core.theme.LoanMasterTheme\n")
        
        with open(path, "w") as f:
            f.writelines(lines)
        print(f"Fixed {path}")

fix_file("app/src/main/java/com/loanmaster/pro/feature/loanintelligence/components/IntelligenceSuggestionCard.kt")
fix_file("app/src/main/java/com/loanmaster/pro/feature/splash/SplashScreen.kt")
