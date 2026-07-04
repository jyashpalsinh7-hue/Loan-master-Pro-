with open("app/src/main/java/com/loanmaster/pro/LoanIntelligenceCard.kt", "r") as f:
    lines = f.readlines()

brace_count = 0
for i, line in enumerate(lines):
    brace_count += line.count('{') - line.count('}')
    if brace_count == 0 and i > 30:
        print(f"Function closes at line {i+1}")
        break
