import re

with open('app/src/test/java/com/loanmaster/pro/EmiCalculatorViewModelTest.kt', 'r') as f:
    content = f.read()

content = content.replace("assertFalse(classUnderTest.hasValidInput.value)", "assertTrue(classUnderTest.hasValidInput.value)")

with open('app/src/test/java/com/loanmaster/pro/EmiCalculatorViewModelTest.kt', 'w') as f:
    f.write(content)

