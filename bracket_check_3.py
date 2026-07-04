with open("app/src/main/java/com/loanmaster/pro/LoanIntelligenceCard.kt", "r") as f:
    lines = f.readlines()

brace_count = 0
found_open = False
for i, line in enumerate(lines):
    if '{' in line:
        found_open = True
    brace_count += line.count('{') - line.count('}')
    if found_open and brace_count == 0:
        print(f"Function closes at line {i+1}")
        break
