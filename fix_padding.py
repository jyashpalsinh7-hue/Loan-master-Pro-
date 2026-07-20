import os

def replace_in_file(filepath, old, new):
    with open(filepath, 'r') as f:
        content = f.read()
    if old in content:
        content = content.replace(old, new)
        with open(filepath, 'w') as f:
            f.write(content)
        print(f"Updated {filepath}")
    else:
        print(f"Pattern not found in {filepath}")

replace_in_file(
    'app/src/main/java/com/loanmaster/pro/core/responsive/Responsive.kt',
    'Box(modifier = Modifier.fillMaxSize().safeDrawingPadding()',
    'Box(modifier = Modifier.fillMaxSize()'
)

replace_in_file(
    'app/src/main/java/com/loanmaster/pro/feature/loaneligibility/LoanEligibilityScreen.kt',
    'modifier = Modifier\n            .fillMaxSize()\n            .background(bgColor)',
    'modifier = Modifier\n            .fillMaxSize()\n            .background(bgColor)\n            .safeDrawingPadding()'
)

replace_in_file(
    'app/src/main/java/com/loanmaster/pro/feature/loansummary/LoanSummaryScreen.kt',
    'modifier = Modifier\n            .fillMaxSize()\n            .background(bgDark)',
    'modifier = Modifier\n            .fillMaxSize()\n            .background(bgDark)\n            .safeDrawingPadding()'
)

replace_in_file(
    'app/src/main/java/com/loanmaster/pro/feature/currency/CurrencyScreen.kt',
    'modifier = Modifier\n            .fillMaxSize()\n            .background(CurrBgColor)',
    'modifier = Modifier\n            .fillMaxSize()\n            .background(CurrBgColor)\n            .safeDrawingPadding()'
)
