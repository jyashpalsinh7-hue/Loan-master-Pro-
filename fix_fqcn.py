import os
import re

for filepath in ["app/src/main/java/com/loanmaster/pro/feature/sip/SipScreen.kt", "app/src/main/java/com/loanmaster/pro/feature/loansummary/LoanSummaryScreen.kt"]:
    if os.path.exists(filepath):
        with open(filepath, 'r') as f:
            content = f.read()
        
        # Replace com.loanmaster.pro.core.formatter.CurrencyFormatter.formatMoney with com.loanmaster.pro.core.formatter.formatMoney
        content = content.replace("com.loanmaster.pro.core.formatter.CurrencyFormatter.formatMoney", "com.loanmaster.pro.core.formatter.formatMoney")
        
        with open(filepath, 'w') as f:
            f.write(content)
            
