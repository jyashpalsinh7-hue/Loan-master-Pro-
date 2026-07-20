import os

filepath = 'app/src/main/java/com/loanmaster/pro/feature/sip/SipScreen.kt'
with open(filepath, 'r') as f:
    content = f.read()

if content.endswith("    }\n}"):
    content = content[:-6]

target = "\n@Composable\nprivate fun SipTopBar"
replacement = "\n        }\n@Composable\nprivate fun SipTopBar"
if target in content:
    content = content.replace(target, replacement)

with open(filepath, 'w') as f:
    f.write(content)
