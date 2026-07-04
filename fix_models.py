with open("app/src/main/java/com/loanmaster/pro/model/CalculatorModels.kt", "r") as f:
    content = f.read()

content = content.replace("val targetTenureMonths: Int", "val targetTenureMonths: Int,\n    val isRecommended: Boolean = false")

with open("app/src/main/java/com/loanmaster/pro/model/CalculatorModels.kt", "w") as f:
    f.write(content)
