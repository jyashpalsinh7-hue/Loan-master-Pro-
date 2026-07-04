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

    # Remove the ResponsiveScreenWrapper start
    pattern = r"(\)\s*\{\s*innerPadding\s*->\n?)\s*com\.loanmaster\.pro\.ui\.theme\.ResponsiveScreenWrapper\(modifier = androidx\.compose\.ui\.Modifier\.fillMaxSize\(\)\) \{\n"
    if re.search(pattern, text):
        new_text = re.sub(pattern, r"\1", text, count=1)
        # Find the last closing brace and remove it
        last_brace_index = new_text.rfind("}")
        if last_brace_index != -1:
             # Remove that brace and the preceding spaces/newline if any
             # Just remove the last brace character.
             new_text = new_text[:last_brace_index] + new_text[last_brace_index+1:]
        with open(file, "w") as f:
            f.write(new_text)
