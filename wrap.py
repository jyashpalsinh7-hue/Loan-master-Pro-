import os
import re

files = [
    "app/src/main/java/com/loanmaster/pro/SettingsScreen.kt",
    "app/src/main/java/com/loanmaster/pro/HistoryScreen.kt",
    "app/src/main/java/com/loanmaster/pro/CurrencyConverterScreen.kt",
    "app/src/main/java/com/loanmaster/pro/LoanSummaryScreen.kt"
]

for file in files:
    with open(file, "r") as f:
        text = f.read()

    # Find innerPadding block start
    pattern = r"(\)\s*\{\s*innerPadding\s*->\n?)"
    replacement = r"\1        com.loanmaster.pro.ui.theme.ResponsiveScreenWrapper(modifier = androidx.compose.ui.Modifier.fillMaxSize()) {\n"
    
    # Check if file has innerPadding block
    if re.search(pattern, text):
        new_text = re.sub(pattern, replacement, text, count=1)
        # Find the last closing brace and add one before it (the Scaffold closing brace)
        last_brace_index = new_text.rfind("}")
        if last_brace_index != -1:
             new_text = new_text[:last_brace_index] + "        }\n" + new_text[last_brace_index:]
        with open(file, "w") as f:
            f.write(new_text)

