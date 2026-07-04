import re

with open("app/src/main/java/com/loanmaster/pro/AppNavigation.kt", "r") as f:
    content = f.read()

# I need to get the historyRepository.
# `AppNavigation` is a composable. I can do:
# val context = LocalContext.current
# val database = getDatabase(context)
# val historyRepository = HistoryRepository(database.historyDao())
repo_init = """
    val context = androidx.compose.ui.platform.LocalContext.current
    val database = getDatabase(context)
    val historyRepository = remember { HistoryRepository(database.historyDao()) }
"""
content = content.replace("fun AppNavigation(", "import androidx.compose.runtime.remember\n\n@Composable\nfun AppNavigation(")
# The previous line might add duplicate @Composable, wait.
content = re.sub(r'(@Composable\n)?fun AppNavigation\(', '@Composable\nfun AppNavigation(', content)

# Insert repo_init at the top of AppNavigation
content = re.sub(r'(fun AppNavigation\([^\)]+\) \{)', r'\1\n' + repo_init, content)

# Change EmiCalculatorViewModel instantiation in composable("emi")
emi_route = """                    composable("emi") {
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
content = re.sub(r'\s*composable\("emi"\) \{[^\}]+\}\s*\}', '\n' + emi_route, content)

with open("app/src/main/java/com/loanmaster/pro/AppNavigation.kt", "w") as f:
    f.write(content)
