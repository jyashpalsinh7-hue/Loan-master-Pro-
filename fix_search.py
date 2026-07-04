import re

with open("app/src/main/java/com/loanmaster/pro/HomeScreen.kt", "r") as f:
    text = f.read()

# Change topAppBarHeight to buttonHeight in SearchAndPremiumRow
text = text.replace(
    ".heightIn(min = LoanMasterTheme.components.topAppBarHeight)",
    ".heightIn(min = LoanMasterTheme.components.buttonHeight)"
)

with open("app/src/main/java/com/loanmaster/pro/HomeScreen.kt", "w") as f:
    f.write(text)
