import re

with open("app/src/main/java/com/loanmaster/pro/LoanEligibilityScreen.kt", "r") as f:
    content = f.read()

content = content.replace("package com.loanmaster.pro", "package com.loanmaster.pro\n\nimport com.loanmaster.pro.model.*")

old_verdict_color = "val verdictColor = uiState.verdictColor"
new_verdict_color = """    val verdictColor = when(uiState.verdictGrade) {
        "A" -> Color(0xFF4ADE80)
        "B" -> Color(0xFF81C784)
        "C" -> Color(0xFFFFB74D)
        "D" -> Color(0xFFF06292)
        else -> Color(0xFFE57373)
    }"""
content = content.replace(old_verdict_color, new_verdict_color)

content = content.replace("alert.color", "when(alert.type) { AlertType.CRITICAL -> Color(0xFFF44336); AlertType.WARNING -> Color(0xFFFF9800); AlertType.POSITIVE -> Color(0xFF4CAF50); else -> Color.Gray }")
content = content.replace("alert.type", "alert.type.name")

with open("app/src/main/java/com/loanmaster/pro/LoanEligibilityScreen.kt", "w") as f:
    f.write(content)
