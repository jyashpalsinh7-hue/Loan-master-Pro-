import re

with open("app/src/main/java/com/loanmaster/pro/EmiCalculatorScreen.kt", "r") as f:
    content = f.read()

# Replace initializeFromHistory with loadFromHistory
content = content.replace("viewModel.initializeFromHistory(initialHistory)", "viewModel.loadFromHistory(initialHistory)")

# Remove LaunchedEffect auto save
# Need to be careful. Let's find it.
start_str = "LaunchedEffect(loanAmountText, interestRateText, tenureInputText, isTenureInMonths, loanType) {"
start_idx = content.find(start_str)
if start_idx != -1:
    end_idx = content.find("val isSavedSuccessfully", start_idx) # wait, I don't know the exact end.
    
    # Just find the matching brace
    braces = 0
    in_effect = False
    for i in range(start_idx, len(content)):
        if content[i] == '{':
            braces += 1
            in_effect = True
        elif content[i] == '}':
            braces -= 1
            if braces == 0 and in_effect:
                end_idx = i + 1
                break
    
    content = content[:start_idx] + content[end_idx:]


# Add Save button
# Find TopAppBar
#             TopAppBar(
#                title = { Text("EMI Calculator", color = Color.White, fontWeight = FontWeight.Bold) },
#                navigationIcon = { ... }
#             )
save_action = """
                actions = {
                    val isSaved by viewModel.isSavedSuccessfully.collectAsStateWithLifecycle()
                    androidx.compose.material3.IconButton(onClick = { viewModel.saveCurrentCalculation() }) {
                        if (isSaved) {
                            androidx.compose.material3.Icon(
                                androidx.compose.material.icons.Icons.Rounded.Check,
                                contentDescription = "Saved",
                                tint = Color(0xFF22C55E)
                            )
                        } else {
                            androidx.compose.material3.Icon(
                                androidx.compose.material.icons.Icons.Rounded.Save,
                                contentDescription = "Save",
                                tint = Color.White
                            )
                        }
                    }
                },"""
content = re.sub(r'(title = \{ Text\("EMI Calculator".*\},\n)(\s*navigationIcon)', r'\1' + save_action + r'\n\2', content)

with open("app/src/main/java/com/loanmaster/pro/EmiCalculatorScreen.kt", "w") as f:
    f.write(content)
