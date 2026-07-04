import re
with open("app/src/main/java/com/loanmaster/pro/AppNavigation.kt", "r") as f:
    content = f.read()

# Let's replace the whole composable("emi") block
start_str = 'composable("emi") {'
start_idx = content.find(start_str)

if start_idx != -1:
    braces = 0
    in_block = False
    for i in range(start_idx, len(content)):
        if content[i] == '{':
            braces += 1
            in_block = True
        elif content[i] == '}':
            braces -= 1
            if braces == 0 and in_block:
                end_idx = i + 1
                break

    replacement = """composable("emi") {
                        val emiViewModel: EmiCalculatorViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                            factory = EmiCalculatorViewModelFactory(historyRepository)
                        )
                        EmiCalculatorScreen(
                            onNavigateBack = { navController.popBackStack() }, 
                            historyViewModel = historyViewModel,
                            initialHistory = selectedHistory?.takeIf { it.calculatorType == "EMI" },
                            onHistoryConsumed = { mainViewModel.clearSelectedHistory() },
                            viewModel = emiViewModel
                        )
                    }"""
    
    content = content[:start_idx] + replacement + content[end_idx:]

with open("app/src/main/java/com/loanmaster/pro/AppNavigation.kt", "w") as f:
    f.write(content)
