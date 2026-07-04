import os

files = [
    "app/src/main/java/com/loanmaster/pro/FdCalculatorScreen.kt",
    "app/src/main/java/com/loanmaster/pro/SipCalculatorScreen.kt",
    "app/src/main/java/com/loanmaster/pro/RdCalculatorScreen.kt",
    "app/src/main/java/com/loanmaster/pro/CurrencyConverterScreen.kt",
    "app/src/main/java/com/loanmaster/pro/CurrencyViewModel.kt",
    "app/src/main/java/com/loanmaster/pro/RdCalculatorViewModel.kt",
]

for file_path in files:
    if os.path.exists(file_path):
        with open(file_path, "r") as f:
            content = f.read()
            
        content = content.replace("package com.loanmaster.pro", "package com.loanmaster.pro\n\nimport com.loanmaster.pro.model.*")
        
        # for RdCalculatorViewModel
        start_idx = content.find("data class RdYearBreakdown(")
        if start_idx != -1:
            end_idx = content.find("class RdCalculatorViewModel")
            if end_idx != -1:
                content = content[:start_idx] + content[end_idx:]
                
        # for CurrencyViewModel
        start_idx = content.find("data class ChartData(")
        if start_idx != -1:
            end_idx = content.find("data class CurrencyConverterUiState(")
            if end_idx != -1:
                content = content[:start_idx] + content[end_idx:]
                
        with open(file_path, "w") as f:
            f.write(content)
