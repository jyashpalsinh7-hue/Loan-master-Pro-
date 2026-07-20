import re

with open("app/src/main/java/com/loanmaster/pro/feature/home/HomeScreen.kt", "r") as f:
    content = f.read()

# Fix SearchAndPremiumRow signature
content = content.replace(
    'fun SearchAndPremiumRow(searchQuery: String, onSearchQueryChange: (String) -> Unit) {',
    'fun SearchAndPremiumRow(searchQuery: String, onSearchQueryChange: (String) -> Unit, onPremiumClick: () -> Unit = {}) {'
)
content = re.sub(
    r'OutlinedButton\(\s*onClick = \{\s*showUnlockDialog = true\s*\},',
    'OutlinedButton(\n            onClick = onPremiumClick,',
    content
)

# Fix PremiumBanner signature
content = content.replace(
    'fun PremiumBanner() {',
    'fun PremiumBanner(onPremiumClick: () -> Unit = {}) {'
)
content = re.sub(
    r'Button\(\s*onClick = \{\s*showUnlockDialog = true\s*\},',
    'Button(\n                        onClick = onPremiumClick,',
    content
)

# Fix calls in HomeScreen
content = content.replace(
    'SearchAndPremiumRow(searchQuery) { viewModel.updateSearchQuery(it) }',
    'SearchAndPremiumRow(searchQuery, { viewModel.updateSearchQuery(it) }, onPremiumClick = { showUnlockDialog = true })'
)
content = content.replace(
    'PremiumBanner()',
    'PremiumBanner(onPremiumClick = { showUnlockDialog = true })'
)

with open("app/src/main/java/com/loanmaster/pro/feature/home/HomeScreen.kt", "w") as f:
    f.write(content)
