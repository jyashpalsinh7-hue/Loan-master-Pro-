with open('app/src/main/java/com/loanmaster/pro/feature/loaneligibility/LoanEligibilityScreen.kt', 'r') as f:
    lines = f.readlines()

with open('app/src/main/java/com/loanmaster/pro/feature/loaneligibility/LoanEligibilityScreen.kt', 'w') as f:
    for i, line in enumerate(lines):
        if i == 740:
            continue
        f.write(line)
