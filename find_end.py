import re
filepath = 'app/src/main/java/com/loanmaster/pro/feature/loansummary/LoanSummaryScreen.kt'
with open(filepath, 'r') as f:
    content = f.read()

match = re.search(r'fun AddLoanDialog', content)
if match:
    print(f"AddLoanDialog found at {match.start()}")
    lines = content[:match.start()].split('\n')
    print(f"Line number: {len(lines)}")
