with open('app/src/main/java/com/loanmaster/pro/feature/loaneligibility/LoanEligibilityScreen.kt', 'r') as f:
    lines = f.readlines()

for i, line in enumerate(lines):
    # Employment Type Selector height
    if '// 2. Employment Type Selector' in line:
        for j in range(i, i+15):
            if '.heightIn(min = 64.dp)' in lines[j]:
                lines[j] = lines[j].replace('.heightIn(min = 64.dp)', '.heightIn(min = 80.dp)')
                break
    # Credit Score Range height
    if 'listOf(Triple("Excellent"' in line:
        for j in range(i-5, i):
            if '.heightIn(min = 64.dp)' in lines[j]:
                lines[j] = lines[j].replace('.heightIn(min = 64.dp)', '.heightIn(min = 80.dp)')
                break

with open('app/src/main/java/com/loanmaster/pro/feature/loaneligibility/LoanEligibilityScreen.kt', 'w') as f:
    f.writelines(lines)
