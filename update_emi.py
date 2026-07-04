import re

with open("app/src/main/java/com/loanmaster/pro/EmiCalculatorScreen.kt", "r") as f:
    content = f.read()

# Add Save button
save_btn = """
                    IconButton(onClick = { viewModel.saveCurrentCalculation() }) {
                        Icon(imageVector = Icons.Rounded.Save, contentDescription = "Save Calculation", tint = primaryText)
                    }
"""

content = content.replace("actions = {", "actions = {" + save_btn)

with open("app/src/main/java/com/loanmaster/pro/EmiCalculatorScreen.kt", "w") as f:
    f.write(content)
