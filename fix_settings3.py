import re

with open("app/src/main/java/com/loanmaster/pro/feature/settings/SettingsScreen.kt", "r") as f:
    content = f.read()

# Fix AccountSyncSection
content = content.replace(
    'private fun AccountSyncSection() {',
    'private fun AccountSyncSection(onPremiumClick: () -> Unit = {}) {'
)
content = content.replace(
    'SupportAppSection()',
    'SupportAppSection(onPremiumClick = { showUnlockDialog = true })'
)

# Fix SupportAppSection
content = content.replace(
    'private fun SupportAppSection() {',
    'private fun SupportAppSection(onPremiumClick: () -> Unit = {}) {'
)

# And replace `showUnlockDialog = true` in those methods
content = re.sub(
    r'Button\(\s*onClick = \{\s*showUnlockDialog = true\s*\},',
    'Button(\n                    onClick = onPremiumClick,',
    content
)
content = re.sub(
    r'Row\(\s*verticalAlignment = Alignment\.CenterVertically,\s*modifier = Modifier\s*\.clickable \{\s*showUnlockDialog = true\s*\}',
    'Row(\n                    verticalAlignment = Alignment.CenterVertically,\n                    modifier = Modifier\n                        .clickable { onPremiumClick() }',
    content
)

# Fix calls in SettingsScreen
content = content.replace(
    'AccountSyncSection()',
    'AccountSyncSection(onPremiumClick = { showUnlockDialog = true })'
)

with open("app/src/main/java/com/loanmaster/pro/feature/settings/SettingsScreen.kt", "w") as f:
    f.write(content)
