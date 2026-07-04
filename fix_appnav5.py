import re

with open("app/src/main/java/com/loanmaster/pro/AppNavigation.kt", "r") as f:
    content = f.read()

repo_init = """
    val context = androidx.compose.ui.platform.LocalContext.current
    val database = getDatabase(context)
    val historyRepository = remember { HistoryRepository(database.historyDao()) }
"""

# Insert it right after `fun AppNavigation( ... ) {`
content = re.sub(r'(navController: androidx\.navigation\.NavHostController = rememberNavController\(\)\n\) \{)', r'\1\n' + repo_init, content)

with open("app/src/main/java/com/loanmaster/pro/AppNavigation.kt", "w") as f:
    f.write(content)

