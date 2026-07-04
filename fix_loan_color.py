with open("app/src/main/java/com/loanmaster/pro/LoanComparisonScreen.kt", "r") as f:
    content = f.read()

content = content.replace("processedLoans.forEach { loan ->\n                            val emi", "processedLoans.forEach { loan ->\n                            val loanColor = if(loan.id == \"A\") Color(0xFF3B82F6) else Color(0xFF10B981)\n                            val emi")
content = content.replace("loans.forEachIndexed { i, loan ->\n                            val emi", "loans.forEachIndexed { i, loan ->\n                            val loanColor = if(loan.id == \"A\") Color(0xFF3B82F6) else Color(0xFF10B981)\n                            val emi")
content = content.replace("Row(modifier = Modifier.weight(1f)) {\n                        Text(loan.bankName, color = loanColor", "Row(modifier = Modifier.weight(1f)) {\n                        val loanColor = if(loan.id == \"A\") Color(0xFF3B82F6) else Color(0xFF10B981)\n                        Text(loan.bankName, color = loanColor")

with open("app/src/main/java/com/loanmaster/pro/LoanComparisonScreen.kt", "w") as f:
    f.write(content)
