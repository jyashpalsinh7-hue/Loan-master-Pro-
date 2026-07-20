import os

filepath = 'app/src/main/java/com/loanmaster/pro/feature/sip/SipScreen.kt'
with open(filepath, 'r') as f:
    content = f.read()

target = "        ) { focusManager.clearFocus() }\n    ) { paddingValues ->\n        Column("
replacement = "        ) { focusManager.clearFocus() }\n    ) { paddingValues ->\n        com.loanmaster.pro.core.responsive.ResponsiveScreenWrapper(\n            modifier = Modifier\n                .fillMaxSize()\n                .padding(paddingValues)\n        ) {\n        Column("

if target in content:
    content = content.replace(target, replacement)
    content = content[:content.rfind("}")] + "    }\n}"
    with open(filepath, 'w') as f:
        f.write(content)
    print("Done SipScreen")
