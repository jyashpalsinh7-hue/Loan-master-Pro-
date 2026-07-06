with open("app/src/main/java/com/loanmaster/pro/feature/loansummary/LoanSummaryScreen.kt", "r") as f:
    lines = f.readlines()
for i, line in enumerate(lines[260:280]):
    print(f"{i+261}: {line.strip()}")
