import re

# 1. Update LoanOffer definition in LoanComparisonScreen.kt
with open("app/src/main/java/com/loanmaster/pro/LoanComparisonScreen.kt", "r") as f:
    content = f.read()

content = re.sub(r'\s*val color: Color,', '', content)
content = re.sub(r'fun toLoanOffer\(id: String, bankName: String, color: Color\): LoanOffer {', 'fun toLoanOffer(id: String, bankName: String): LoanOffer {', content)

# 2. Update LoanOffer usages in LoanComparisonScreen.kt
content = content.replace("LoanOffer(loanAState.id, loanAState.bankName, loanAState.interestRate, loanAState.tenureYears, loanAState.tenureMonths, loanAState.loanAmount, 0.0, 0.0, Color(0xFF3B82F6))", 
                          "LoanOffer(loanAState.id, loanAState.bankName, loanAState.interestRate, loanAState.tenureYears, loanAState.tenureMonths, loanAState.loanAmount, 0.0, 0.0)")
content = content.replace("LoanOffer(loanBState.id, loanBState.bankName, loanBState.interestRate, loanBState.tenureYears, loanBState.tenureMonths, loanBState.loanAmount, 0.0, 0.0, Color(0xFF10B981))", 
                          "LoanOffer(loanBState.id, loanBState.bankName, loanBState.interestRate, loanBState.tenureYears, loanBState.tenureMonths, loanBState.loanAmount, 0.0, 0.0)")

# 3. Update occurrences of loan.color in LoanComparisonScreen.kt
# We'll use a dynamic color map based on loan ID inside Composables
color_val = """    val loanColor = when(loan.id) {
        "A" -> Color(0xFF3B82F6)
        "B" -> Color(0xFF10B981)
        else -> Color.Gray
    }"""

content = content.replace("loan.color", "loanColor")
# Insert val loanColor into composables where missing
content = re.sub(r'(fun LoanCard\(loan: LoanOffer, onEdit: \(\) -> Unit, modifier: Modifier = Modifier\) \{\n)', r'\1' + color_val + '\n', content)

with open("app/src/main/java/com/loanmaster/pro/LoanComparisonScreen.kt", "w") as f:
    f.write(content)

