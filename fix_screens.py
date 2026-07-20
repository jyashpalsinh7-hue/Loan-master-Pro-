import os
import re

screens_dir = 'app/src/main/java/com/loanmaster/pro/feature'

def replace_in_file(filepath):
    with open(filepath, 'r') as f:
        content = f.read()

    if 'ResponsiveScreenWrapper' in content and 'HomeScreen' not in filepath and 'HistoryScreen' not in filepath and 'SettingsScreen' not in filepath and 'LoanEligibilityScreen' not in filepath:
        pass # we only want to fix ones without ResponsiveScreenWrapper

    # Look for Scaffold( ... ) { padding_var -> ... Box( modifier = Modifier.padding(padding_var)
    # This is slightly hard to regex safely, let's just do standard string replacements based on how the files are structured.

    # 1. FdScreen
    if 'FdScreen.kt' in filepath:
        content = content.replace(
            ") { innerPadding ->\n        Box(\n            modifier = Modifier\n                .padding(innerPadding)\n                .fillMaxSize()\n        ) {",
            ") { innerPadding ->\n        com.loanmaster.pro.core.responsive.ResponsiveScreenWrapper(\n            modifier = Modifier\n                .padding(innerPadding)\n                .fillMaxSize()\n        ) {"
        )
    # 2. CompareScreen
    elif 'CompareScreen.kt' in filepath:
        content = content.replace(
            ") { padding ->\n        Box(\n            modifier = Modifier\n                .padding(padding)\n                .fillMaxSize()\n        ) {",
            ") { padding ->\n        com.loanmaster.pro.core.responsive.ResponsiveScreenWrapper(\n            modifier = Modifier\n                .padding(padding)\n                .fillMaxSize()\n        ) {"
        )
    # 3. PrepaymentScreen
    elif 'PrepaymentScreen.kt' in filepath:
        content = content.replace(
            ") { paddingValues ->\n        LazyColumn(\n            modifier = Modifier\n                .fillMaxSize()\n                .padding(paddingValues)\n                .padding(horizontal = LoanMasterTheme.spacing.screenPadding),",
            ") { paddingValues ->\n        com.loanmaster.pro.core.responsive.ResponsiveScreenWrapper(\n            modifier = Modifier\n                .fillMaxSize()\n                .padding(paddingValues)\n        ) {\n            LazyColumn(\n                modifier = Modifier\n                    .fillMaxSize()\n                    .padding(horizontal = LoanMasterTheme.spacing.screenPadding),"
        )
        # need to add an extra brace at the end of the Scaffold block
        # the easiest way is to find the last } of the file, assuming it's the end of PrepaymentScreen function
        # Actually, if I just replace the end of LazyColumn block... PrepaymentScreen has multiple composables?
        # Let's check PrepaymentScreen.kt first before automating it.
        pass
    
    with open(filepath, 'w') as f:
        f.write(content)

for root, dirs, files in os.walk(screens_dir):
    for file in files:
        if file.endswith('Screen.kt'):
            replace_in_file(os.path.join(root, file))
