import os
import re

sip_screen = "app/src/main/java/com/loanmaster/pro/feature/sip/SipScreen.kt"
if os.path.exists(sip_screen):
    with open(sip_screen, 'r') as f:
        content = f.read()
    
    # Replace usages
    content = re.sub(r'formatMoneyObj\(', 'com.loanmaster.pro.core.formatter.CurrencyFormatter.formatMoney(', content)
    content = re.sub(r'formatMoneyExact\(', 'com.loanmaster.pro.core.formatter.CurrencyFormatter.formatMoney(', content)
    
    # Remove the functions
    content = re.sub(r'fun formatMoneyObj\([^\{]+\{[\s\S]*?\n\}', '', content)
    content = re.sub(r'fun formatMoneyExact\([^\{]+\{[\s\S]*?\n\}', '', content)
    
    with open(sip_screen, 'w') as f:
        f.write(content)

summary_screen = "app/src/main/java/com/loanmaster/pro/feature/loansummary/LoanSummaryScreen.kt"
if os.path.exists(summary_screen):
    with open(summary_screen, 'r') as f:
        content = f.read()
    
    content = re.sub(r'formatShort\(', 'com.loanmaster.pro.core.formatter.CurrencyFormatter.formatMoney(', content)
    content = re.sub(r'private fun formatShort\([^\{]+\{[\s\S]*?\n\}', '', content)
    
    with open(summary_screen, 'w') as f:
        f.write(content)

