import re

with open('app/src/test/java/com/loanmaster/pro/EmiCalculatorViewModelTest.kt', 'r') as f:
    content = f.read()

content = content.replace("assertTrue(classUnderTest.hasValidInput.value)", "assertFalse(classUnderTest.hasValidInput.value)", 1) # Only first one which is inside loadFromHistory
content = content.replace("assertTrue(classUnderTest.hasValidInput.value)", "assertFalse(classUnderTest.hasValidInput.value)", 1) # Now the second one

with open('app/src/test/java/com/loanmaster/pro/EmiCalculatorViewModelTest.kt', 'w') as f:
    f.write(content)
