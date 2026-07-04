import re

with open("app/src/main/java/com/loanmaster/pro/MainActivity.kt", "r") as f:
    content = f.read()

repo_init = """
                    val historyViewModel: HistoryViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                        factory = HistoryViewModelFactory(repository, settingsRepository)
                    )
                    
                    // Prepopulate database with 5 items per calculator if empty
                    LaunchedEffect(Unit) {
                        DatabasePrepopulator.prepopulateIfEmpty(context, repository)
                    }
"""

content = content.replace("""                    val historyViewModel: HistoryViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                        factory = HistoryViewModelFactory(repository, settingsRepository)
                    )""", repo_init)

with open("app/src/main/java/com/loanmaster/pro/MainActivity.kt", "w") as f:
    f.write(content)
