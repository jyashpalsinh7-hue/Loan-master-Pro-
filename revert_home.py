import sys

filename = 'app/src/main/java/com/loanmaster/pro/feature/home/HomeScreen.kt'
with open(filename, 'r') as f:
    content = f.read()

target1 = """    val context = androidx.compose.ui.platform.LocalContext.current
    val premiumManager = androidx.compose.runtime.remember { com.loanmaster.pro.core.managers.PremiumManager(context) }
    val isPremiumUnlocked by premiumManager.isPremium.collectAsStateWithLifecycle(initialValue = false)
    var showUnlockDialog by rememberSaveable { mutableStateOf(false) }"""

replacement1 = """    var showUnlockDialog by rememberSaveable { mutableStateOf(false) }"""

target2 = """                item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) {
                    SearchAndPremiumRow(searchQuery, onSearchQueryChange = { viewModel.updateSearchQuery(it) }, onPremiumClick = { showUnlockDialog = true }, isPremiumUnlocked = isPremiumUnlocked)
                }
                if (searchQuery.isBlank() && !isPremiumUnlocked) {
                    item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) {
                        HeroBanner(onPremiumClick = { showUnlockDialog = true })
                    }
                }"""

replacement2 = """                item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) {
                    SearchAndPremiumRow(searchQuery, onSearchQueryChange = { viewModel.updateSearchQuery(it) }, onPremiumClick = { showUnlockDialog = true })
                }
                if (searchQuery.isBlank()) {
                    item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) {
                        HeroBanner(onPremiumClick = { showUnlockDialog = true })
                    }
                }"""

target3 = """fun SearchAndPremiumRow(searchQuery: String, onSearchQueryChange: (String) -> Unit, onPremiumClick: () -> Unit = {}, isPremiumUnlocked: Boolean = false) {"""

replacement3 = """fun SearchAndPremiumRow(searchQuery: String, onSearchQueryChange: (String) -> Unit, onPremiumClick: () -> Unit = {}) {"""

target4 = """        val context = androidx.compose.ui.platform.LocalContext.current
        // Premium Button
        if (isPremiumUnlocked) {
            Row(
                modifier = Modifier
                    .heightIn(min = LoanMasterTheme.components.buttonHeight)
                    .clip(RoundedCornerShape(LoanMasterTheme.components.cardRadius))
                    .background(androidx.compose.ui.graphics.Brush.horizontalGradient(listOf(Color(0xFFF59E0B), Color(0xFFD97706))))
                    .padding(horizontal = LoanMasterTheme.spacing.md),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.WorkspacePremium,
                    contentDescription = "Premium User",
                    tint = Color.White,
                    modifier = Modifier.size(LoanMasterTheme.components.iconSmall)
                )
                Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.xs))
                Text(text = "Premium User", style = LoanMasterTheme.typography.label, fontWeight = FontWeight.Bold, color = Color.White)
            }
        } else {
            OutlinedButton(
                onClick = onPremiumClick,
                modifier = Modifier
                    .heightIn(min = LoanMasterTheme.components.buttonHeight)
                    .testTag("premium_button"),
                shape = RoundedCornerShape(LoanMasterTheme.components.cardRadius),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = AccentYellow.copy(alpha = 0.1f),
                    contentColor = AccentYellow
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, AccentYellow),
                contentPadding = PaddingValues(horizontal = LoanMasterTheme.spacing.md)
            ) {
                Icon(
                    imageVector = Icons.Rounded.WorkspacePremium,
                    contentDescription = null,
                    modifier = Modifier.size(LoanMasterTheme.components.iconSmall)
                )
                Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.xs))
                Text(text = "Premium", style = LoanMasterTheme.typography.label, fontWeight = FontWeight.Bold)
            }
        }"""

replacement4 = """        val context = androidx.compose.ui.platform.LocalContext.current
        // Premium Button
        OutlinedButton(
            onClick = onPremiumClick,
            modifier = Modifier
                .heightIn(min = LoanMasterTheme.components.buttonHeight)
                .testTag("premium_button"),
            shape = RoundedCornerShape(LoanMasterTheme.components.cardRadius),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = AccentYellow.copy(alpha = 0.1f),
                contentColor = AccentYellow
            ),
            border = androidx.compose.foundation.BorderStroke(1.dp, AccentYellow),
            contentPadding = PaddingValues(horizontal = LoanMasterTheme.spacing.md)
        ) {
            Icon(
                imageVector = Icons.Rounded.WorkspacePremium,
                contentDescription = null,
                modifier = Modifier.size(LoanMasterTheme.components.iconSmall)
            )
            Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.xs))
            Text(text = "Premium", style = LoanMasterTheme.typography.label, fontWeight = FontWeight.Bold)
        }"""

content = content.replace(target1, replacement1)
content = content.replace(target2, replacement2)
content = content.replace(target3, replacement3)
content = content.replace(target4, replacement4)

with open(filename, 'w') as f:
    f.write(content)
