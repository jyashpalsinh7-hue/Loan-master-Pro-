import re

with open("app/src/main/java/com/loanmaster/pro/MainActivity.kt", "r") as f:
    content = f.read()

test_insert = """
                    // FAKE HISTORY FOR TESTING
                    LaunchedEffect(Unit) {
                        historyViewModel.insert(CalculationHistory(calculatorType = "TEST", title = "Fake History", param1 = "1000"))
                    }
"""

content = content.replace("AppNavigation(", test_insert + "\n                    AppNavigation(")

with open("app/src/main/java/com/loanmaster/pro/MainActivity.kt", "w") as f:
    f.write(content)
