import os
import re

screens_dir = 'app/src/main/java/com/loanmaster/pro/feature'

def replace_in_file(filepath):
    with open(filepath, 'r') as f:
        content = f.read()

    if 'ResponsiveScreenWrapper' in content and 'HomeScreen' not in filepath and 'HistoryScreen' not in filepath and 'SettingsScreen' not in filepath and 'LoanEligibilityScreen' not in filepath and 'FdScreen' not in filepath and 'CompareScreen' not in filepath and 'PrepaymentScreen' not in filepath:
        pass

    changed = False

    # For GstScreen and others that might use paddingValues
    target_padding_values = ") { paddingValues ->\n        Box(\n            modifier = Modifier\n                .padding(paddingValues)\n                .fillMaxSize()\n        ) {"
    replacement_padding_values = ") { paddingValues ->\n        com.loanmaster.pro.core.responsive.ResponsiveScreenWrapper(\n            modifier = Modifier\n                .padding(paddingValues)\n                .fillMaxSize()\n        ) {"
    if target_padding_values in content:
        content = content.replace(target_padding_values, replacement_padding_values)
        changed = True

    # For RdScreen and SipScreen that use innerPadding
    target_inner_padding = ") { innerPadding ->\n        Box(\n            modifier = Modifier\n                .padding(innerPadding)\n                .fillMaxSize()\n        ) {"
    replacement_inner_padding = ") { innerPadding ->\n        com.loanmaster.pro.core.responsive.ResponsiveScreenWrapper(\n            modifier = Modifier\n                .padding(innerPadding)\n                .fillMaxSize()\n        ) {"
    if target_inner_padding in content:
        content = content.replace(target_inner_padding, replacement_inner_padding)
        changed = True

    # For LoanSummaryScreen
    target_padding = "        ) { padding ->\n        BoxWithConstraints(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.TopCenter) {"
    replacement_padding = "        ) { padding ->\n        com.loanmaster.pro.core.responsive.ResponsiveScreenWrapper(modifier = Modifier.padding(padding)) {\n        BoxWithConstraints(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {"
    if target_padding in content:
        content = content.replace(target_padding, replacement_padding)
        # add closing brace before the last bracket
        content = content[:content.rfind("}")] + "    }\n}"
        changed = True

    # For CurrencyScreen
    target_curr = "    ) { innerPadding ->\n        // FIX:"
    replacement_curr = "    ) { innerPadding ->\n        com.loanmaster.pro.core.responsive.ResponsiveScreenWrapper(modifier = Modifier.padding(innerPadding).fillMaxSize()) {\n        // FIX:"
    
    # but wait, let's just see if CurrencyScreen has innerPadding
    if "    ) { innerPadding ->\n        // FIX:" in content:
        content = content.replace(target_curr, replacement_curr)
        content = content[:content.rfind("}")] + "    }\n}"
        changed = True

    if changed:
        with open(filepath, 'w') as f:
            f.write(content)
        print(f"Updated {filepath}")

for root, dirs, files in os.walk(screens_dir):
    for file in files:
        if file.endswith('Screen.kt'):
            replace_in_file(os.path.join(root, file))
