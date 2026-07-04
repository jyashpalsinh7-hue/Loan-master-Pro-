with open("app/src/main/java/com/loanmaster/pro/LoanComparisonScreen.kt", "r") as f:
    content = f.read()

content = content.replace("loans.forEach { loan ->\n                        Text(loan.bankName, color = loanColor", "loans.forEach { loan ->\n                        val loanColor = if(loan.id == \"A\") Color(0xFF3B82F6) else Color(0xFF10B981)\n                        Text(loan.bankName, color = loanColor")

# Also check around line 556 for loanColor, is it defined there?
content = content.replace("loans.forEachIndexed { i, loan ->\n                            val breakEvenMonths", "loans.forEachIndexed { i, loan ->\n                            val loanColor = if(loan.id == \"A\") Color(0xFF3B82F6) else Color(0xFF10B981)\n                            val breakEvenMonths")

with open("app/src/main/java/com/loanmaster/pro/LoanComparisonScreen.kt", "w") as f:
    f.write(content)
