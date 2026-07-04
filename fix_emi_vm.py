import re

with open("app/src/main/java/com/loanmaster/pro/EmiCalculatorViewModel.kt", "r") as f:
    content = f.read()

content = content.replace("fun loadFromHistory(history: CalculationHistory)", """fun updateHistoryId(id: Int) {
        _currentHistoryId.value = id
    }
    
    fun loadFromHistory(history: CalculationHistory)""")

with open("app/src/main/java/com/loanmaster/pro/EmiCalculatorViewModel.kt", "w") as f:
    f.write(content)
