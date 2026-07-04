import re

with open("app/src/main/java/com/loanmaster/pro/EmiCalculatorViewModel.kt", "r") as f:
    content = f.read()

start_idx = content.find("data class SmartRecommendation(")
if start_idx != -1:
    end_idx = content.find("class EmiCalculatorViewModel")
    if end_idx != -1:
        content = content[:start_idx] + content[end_idx:]

content = re.sub(r'\s*icon = [^,]+,', '', content)
content = re.sub(r'\s*accentColor = [^,]+,', '', content)

with open("app/src/main/java/com/loanmaster/pro/EmiCalculatorViewModel.kt", "w") as f:
    f.write(content)
