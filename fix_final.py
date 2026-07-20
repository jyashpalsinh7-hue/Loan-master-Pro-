import re

# FIX SETTINGS SCREEN
with open("app/src/main/java/com/loanmaster/pro/feature/settings/SettingsScreen.kt", "r") as f:
    content = f.read()

content = content.replace(
    'val language = uiState.language',
    'var showUnlockDialog by rememberSaveable { mutableStateOf(false) }\n    val language = uiState.language'
)

# Fix syntax error in SupportAppSection declaration
content = content.replace(
    'private fun SupportAppSection(onPremiumClick = { showUnlockDialog = true }) {',
    'private fun SupportAppSection(onPremiumClick: () -> Unit = {}) {'
)

# Let's fix the undefined `showUnlockDialog` inside AccountSyncSection and SupportAppSection by just passing the lambda. 
# They are ALREADY taking `onPremiumClick: () -> Unit = {}` so we shouldn't use `showUnlockDialog` inside them!
content = content.replace(
    'onClick = { showUnlockDialog = true },',
    'onClick = onPremiumClick,'
)

content = content.replace(
    '.clickable { showUnlockDialog = true }',
    '.clickable { onPremiumClick() }'
)

with open("app/src/main/java/com/loanmaster/pro/feature/settings/SettingsScreen.kt", "w") as f:
    f.write(content)


# FIX HOME SCREEN
with open("app/src/main/java/com/loanmaster/pro/feature/home/HomeScreen.kt", "r") as f:
    content = f.read()

content = content.replace(
    'onClick = { showUnlockDialog = true },',
    'onClick = onPremiumClick,'
)

# And now we have to make sure `showUnlockDialog = true` is passed to the lambda in `HomeScreen` call
content = content.replace(
    'onPremiumClick = { showUnlockDialog = true }',
    'onPremiumClick = { showUnlockDialog = true }' # This is already correct
)

with open("app/src/main/java/com/loanmaster/pro/feature/home/HomeScreen.kt", "w") as f:
    f.write(content)
