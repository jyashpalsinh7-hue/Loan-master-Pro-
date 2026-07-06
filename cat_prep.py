with open("app/src/main/java/com/loanmaster/pro/feature/prepayment/PrepaymentCalculatorViewModel.kt", "r") as f:
    lines = f.readlines()
for i, line in enumerate(lines[40:70]):
    print(f"{i+41}: {line.strip()}")
