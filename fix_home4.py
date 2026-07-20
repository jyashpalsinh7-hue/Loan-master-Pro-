with open("app/src/main/java/com/loanmaster/pro/feature/home/HomeScreen.kt", "r") as f:
    lines = f.readlines()

for i in range(len(lines)):
    line = lines[i]
    if "fun SearchAndPremiumRow" in line or "fun PremiumBanner" in line:
        pass
    if "OutlinedButton(" in lines[i-3] or "OutlinedButton(" in lines[i-4] or "OutlinedButton(" in lines[i-2] or "OutlinedButton(" in lines[i-1]:
        if "showUnlockDialog = true" in line and "onPremiumClick" not in line:
            lines[i] = line.replace("showUnlockDialog = true", "onPremiumClick()")
    if "Button(" in lines[i-1] or "Button(" in lines[i-2]:
        if "showUnlockDialog = true" in line and "onPremiumClick" not in line:
            lines[i] = line.replace("showUnlockDialog = true", "onPremiumClick()")

with open("app/src/main/java/com/loanmaster/pro/feature/home/HomeScreen.kt", "w") as f:
    f.writelines(lines)
