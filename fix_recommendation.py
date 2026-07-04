import re

with open("app/src/main/java/com/loanmaster/pro/RecommendationBottomSheet.kt", "r") as f:
    content = f.read()

content = content.replace("package com.loanmaster.pro", "package com.loanmaster.pro\n\nimport com.loanmaster.pro.model.*")

start_idx = content.find("data class SmartRecommendation(")
if start_idx != -1:
    end_idx = content.find("fun RecommendationBottomSheet(")
    if end_idx != -1:
        # Also remove @Composable if it's there
        content = content[:start_idx] + "@Composable\n" + content[end_idx:]

with open("app/src/main/java/com/loanmaster/pro/RecommendationBottomSheet.kt", "w") as f:
    f.write(content)
