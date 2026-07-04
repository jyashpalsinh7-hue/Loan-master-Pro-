import re

with open("app/src/main/java/com/loanmaster/pro/LoanEligibilityViewModel.kt", "r") as f:
    content = f.read()

content = re.sub(r'val color = if \(totalScore [^\n]+\n', '', content)
content = content.replace('verdictDesc = "Based on your income and requested details."),', 'verdictDesc = "Based on your income and requested details.",')

# Also fix the data class LoanEligibilityUiState
content = re.sub(r'val verdictColor[^,]+,', '', content)

with open("app/src/main/java/com/loanmaster/pro/LoanEligibilityViewModel.kt", "w") as f:
    f.write(content)
