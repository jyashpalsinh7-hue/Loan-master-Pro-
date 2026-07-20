import sys

def balance_braces(filepath):
    with open(filepath, 'r') as f:
        content = f.read()

    # Find the outermost braces
    open_count = 0
    for i, char in enumerate(content):
        if char == '{':
            open_count += 1
        elif char == '}':
            open_count -= 1
            if open_count < 0:
                print(f"Error: Too many closing braces in {filepath} at index {i}")
                # Remove the extra brace
                content = content[:i] + content[i+1:]
                open_count = 0
    
    while open_count > 0:
        content += "\n}"
        open_count -= 1

    with open(filepath, 'w') as f:
        f.write(content)
    print(f"Balanced {filepath}")

balance_braces('app/src/main/java/com/loanmaster/pro/feature/prepayment/PrepaymentScreen.kt')
balance_braces('app/src/main/java/com/loanmaster/pro/feature/loansummary/LoanSummaryScreen.kt')
balance_braces('app/src/main/java/com/loanmaster/pro/feature/currency/CurrencyScreen.kt')
balance_braces('app/src/main/java/com/loanmaster/pro/feature/sip/SipScreen.kt')
