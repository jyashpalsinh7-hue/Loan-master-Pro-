import re

# 1. RdScreen.kt
with open("app/src/main/java/com/loanmaster/pro/feature/rd/RdScreen.kt", "r") as f:
    content = f.read()

# Use regex to remove `isPremiumUnlocked = true` anywhere inside the onUnlockSuccessful lambda
content = re.sub(r'isPremiumUnlocked\s*=\s*true', '', content)

with open("app/src/main/java/com/loanmaster/pro/feature/rd/RdScreen.kt", "w") as f:
    f.write(content)

# 2. CompareScreen.kt
with open("app/src/main/java/com/loanmaster/pro/feature/compare/CompareScreen.kt", "r") as f:
    content = f.read()

content = re.sub(r'isPremiumUnlocked\s*=\s*true', '', content)

with open("app/src/main/java/com/loanmaster/pro/feature/compare/CompareScreen.kt", "w") as f:
    f.write(content)


# 3. SipViewModel.kt
with open("app/src/main/java/com/loanmaster/pro/feature/sip/SipViewModel.kt", "r") as f:
    content = f.read()

if 'import kotlinx.coroutines.launch' not in content:
    content = content.replace(
        'import kotlinx.coroutines.flow.update',
        'import kotlinx.coroutines.flow.update\nimport kotlinx.coroutines.launch'
    )

with open("app/src/main/java/com/loanmaster/pro/feature/sip/SipViewModel.kt", "w") as f:
    f.write(content)
