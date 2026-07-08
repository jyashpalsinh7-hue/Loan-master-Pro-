import re
with open("app/src/main/java/com/loanmaster/pro/MainActivity.kt", "r") as f:
    code = f.read()

new_code = re.sub(r'AppNavigation\(.*', '''AppNavigation(
                        historyViewModel = historyViewModel,
                        loanSummaryViewModel = loanSummaryViewModel,
                        mainViewModel = mainViewModel,
                        settingsViewModel = settingsViewModel,
                        navController = navController
                    )
                    }
                }
            }
        }
    }
}''', code, flags=re.DOTALL)

with open("app/src/main/java/com/loanmaster/pro/MainActivity.kt", "w") as f:
    f.write(new_code)
