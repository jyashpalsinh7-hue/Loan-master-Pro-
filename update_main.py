import os

file_path = "app/src/main/java/com/loanmaster/pro/MainActivity.kt"
with open(file_path, "r") as f:
    content = f.read()

target = """                    AppNavigation(
                        historyViewModel = historyViewModel,
                        loanSummaryViewModel = loanSummaryViewModel,
                        mainViewModel = mainViewModel,
                        settingsViewModel = settingsViewModel,
                        navController = navController
                    )"""

replacement = """                    Column(modifier = Modifier.fillMaxSize()) {
                        Box(modifier = Modifier.weight(1f)) {
                            AppNavigation(
                                historyViewModel = historyViewModel,
                                loanSummaryViewModel = loanSummaryViewModel,
                                mainViewModel = mainViewModel,
                                settingsViewModel = settingsViewModel,
                                navController = navController
                            )
                        }
                        com.loanmaster.pro.core.ui.AdMobBanner()
                    }"""

if "Column(modifier = Modifier.fillMaxSize())" not in content:
    content = content.replace(target, replacement)
    with open(file_path, "w") as f:
        f.write(content)
    print("Done")
else:
    print("Target not found")
