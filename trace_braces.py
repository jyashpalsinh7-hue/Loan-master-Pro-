filepath = 'app/src/main/java/com/loanmaster/pro/feature/loansummary/LoanSummaryScreen.kt'
with open(filepath, 'r') as f:
    lines = f.readlines()

open_count = 0
for i, line in enumerate(lines):
    open_count += line.count('{')
    open_count -= line.count('}')
    if i >= 115 and i <= 288:
        pass
        #print(f"Line {i+1}: {line.strip()} | Open count: {open_count}")
print(f"Count at 288 is {open_count}")
