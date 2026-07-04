import re

# 1. EmiCalculatorViewModel
with open("app/src/main/java/com/loanmaster/pro/EmiCalculatorViewModel.kt", "r") as f:
    content = f.read()
# Let's remove the package prefix if it's there
content = content.replace("List<com.loanmaster.pro.SmartRecommendation>", "List<SmartRecommendation>")
with open("app/src/main/java/com/loanmaster/pro/EmiCalculatorViewModel.kt", "w") as f:
    f.write(content)

# 2. LoanComparisonViewModel
with open("app/src/main/java/com/loanmaster/pro/LoanComparisonViewModel.kt", "r") as f:
    content = f.read()
content = re.sub(r',\s*loanAState\.color', '', content)
content = re.sub(r',\s*loanBState\.color', '', content)
content = re.sub(r',\s*loanA\.color', '', content)
content = re.sub(r',\s*loanB\.color', '', content)
with open("app/src/main/java/com/loanmaster/pro/LoanComparisonViewModel.kt", "w") as f:
    f.write(content)

# 3. LoanEligibilityViewModel
with open("app/src/main/java/com/loanmaster/pro/LoanEligibilityViewModel.kt", "r") as f:
    content = f.read()
content = content.replace("verdictColor = Color(0xFF4ADE80)", "")
content = content.replace("verdictColor = Color(0xFF81C784)", "")
content = content.replace("verdictColor = Color(0xFFFFB74D)", "")
content = content.replace("verdictColor = Color(0xFFF06292)", "")
content = content.replace("verdictColor = Color(0xFFE57373)", "")
content = re.sub(r',\s*verdictColor = [^\)]+', '', content)
content = re.sub(r'verdictColorString: String[^,]+,', '', content)
content = re.sub(r'verdictColorString = "[^"]+",', '', content)
with open("app/src/main/java/com/loanmaster/pro/LoanEligibilityViewModel.kt", "w") as f:
    f.write(content)

# 4. LoanIntelligenceCard
with open("app/src/main/java/com/loanmaster/pro/LoanIntelligenceCard.kt", "r") as f:
    content = f.read()
content = content.replace("when(alert.type.name)", "when(alert.type)")
with open("app/src/main/java/com/loanmaster/pro/LoanIntelligenceCard.kt", "w") as f:
    f.write(content)

