import re
path = "app/src/main/java/com/loanmaster/pro/feature/settings/SettingsViewModel.kt"
with open(path, "r") as f:
    content = f.read()

content = re.sub(r'val language = [^\n]+\n', '', content)
content = re.sub(r'val currency = [^\n]+\n', '', content)
content = re.sub(r'val notificationsEnabled = [^\n]+\n', '', content)
content = re.sub(r'val keepHistoryEnabled = [^\n]+\n', '', content)

with open(path, "w") as f:
    f.write(content)
