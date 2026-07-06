import re
import os

sip_screen = "app/src/main/java/com/loanmaster/pro/feature/sip/SipScreen.kt"
if os.path.exists(sip_screen):
    with open(sip_screen, 'r') as f:
        content = f.read()
    
    # We want to remove the extension function blocks. 
    # They look like:
    # fun com.loanmaster.pro.core.formatter.CurrencyFormatter.formatMoney(value: Double): String { ... }
    
    # Let's find them manually and remove them.
    # Because of curly braces, regex might be tricky if they have nested braces.
    # Let's just find the start, and count braces to remove the block.
    
    def remove_function(text, prefix):
        idx = text.find(prefix)
        while idx != -1:
            # find first '{'
            start_brace = text.find('{', idx)
            if start_brace == -1:
                break
            
            brace_count = 1
            end_brace = start_brace + 1
            while brace_count > 0 and end_brace < len(text):
                if text[end_brace] == '{':
                    brace_count += 1
                elif text[end_brace] == '}':
                    brace_count -= 1
                end_brace += 1
                
            text = text[:idx] + text[end_brace:]
            idx = text.find(prefix)
        return text

    prefix = "fun com.loanmaster.pro.core.formatter.CurrencyFormatter.formatMoney("
    content = remove_function(content, prefix)
    
    with open(sip_screen, 'w') as f:
        f.write(content)

summary_screen = "app/src/main/java/com/loanmaster/pro/feature/loansummary/LoanSummaryScreen.kt"
if os.path.exists(summary_screen):
    with open(summary_screen, 'r') as f:
        content = f.read()
        
    prefix = "private fun com.loanmaster.pro.core.formatter.CurrencyFormatter.formatMoney("
    content = remove_function(content, prefix)
    
    with open(summary_screen, 'w') as f:
        f.write(content)
        
