import sys

# FIX 1
filename = 'app/src/main/java/com/loanmaster/pro/feature/rd/RdScreen.kt'
with open(filename, 'r') as f:
    content = f.read()

target = """            if (isLocked) {
                Box(modifier = Modifier.matchParentSize().background(BackgroundDark.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clip(RoundedCornerShape(LoanMasterTheme.spacing.md)).background(SurfaceDark).border(1.dp, CardStroke, RoundedCornerShape(LoanMasterTheme.spacing.md)).padding(LoanMasterTheme.spacing.lg).clickable { showUnlockDialog = true }
                    ) {
                        Box(modifier = Modifier.size(LoanMasterTheme.components.buttonHeight).clip(CircleShape).background(AccentYellow.copy(alpha = 0.2f)), contentAlignment = Alignment.Center) {
                            Icon(Icons.Rounded.Lock, contentDescription = "Premium", tint = AccentYellow, modifier = Modifier.size(LoanMasterTheme.components.iconMedium))
                        }
                        Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.md))
                        Text("Goal-Based RD is Premium", color = TextPrimary, fontSize = LoanMasterTheme.typography.body.fontSize, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.sm))
                        Text("Calculate exactly how much to invest.", color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize)
                        Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.md))
                        Button(
                            onClick = { showUnlockDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentYellow, contentColor = Color(0xFF020B1F))
                        ) {
                            Text("Unlock Premium", fontWeight = FontWeight.Bold)
                        }
                    }
                }"""

replacement = """            if (isLocked) {
                Box(modifier = Modifier.matchParentSize().background(BackgroundDark.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clip(RoundedCornerShape(LoanMasterTheme.spacing.md)).background(SurfaceDark).border(1.dp, CardStroke, RoundedCornerShape(LoanMasterTheme.spacing.md)).padding(LoanMasterTheme.spacing.lg).clickable { if (!isPremiumUnlocked) showUnlockDialog = true }
                    ) {
                        Box(modifier = Modifier.size(LoanMasterTheme.components.buttonHeight).clip(CircleShape).background(AccentYellow.copy(alpha = 0.2f)), contentAlignment = Alignment.Center) {
                            Icon(Icons.Rounded.Lock, contentDescription = "Premium", tint = AccentYellow, modifier = Modifier.size(LoanMasterTheme.components.iconMedium))
                        }
                        Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.md))
                        Text("Goal-Based RD is Premium", color = TextPrimary, fontSize = LoanMasterTheme.typography.body.fontSize, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.sm))
                        Text("Calculate exactly how much to invest.", color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize)
                        Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.md))
                        Button(
                            onClick = { if (!isPremiumUnlocked) showUnlockDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentYellow, contentColor = Color(0xFF020B1F))
                        ) {
                            Text("Unlock Premium", fontWeight = FontWeight.Bold)
                        }
                    }
                }"""

if target in content:
    content = content.replace(target, replacement)
    with open(filename, 'w') as f:
        f.write(content)
    print("Fix 1 (RdScreen.kt) Applied")
else:
    print("Fix 1 (RdScreen.kt) Target not found")


# FIX 2
filename = 'app/src/main/java/com/loanmaster/pro/feature/settings/SettingsScreen.kt'
with open(filename, 'r') as f:
    content = f.read()

import_statement = "import androidx.lifecycle.compose.collectAsStateWithLifecycle"
if import_statement not in content:
    content = content.replace("import androidx.compose.runtime.Composable", f"import androidx.compose.runtime.Composable\n{import_statement}")

target1 = """    var showUnlockDialog by rememberSaveable { mutableStateOf(false) }"""
replacement1 = """    var showUnlockDialog by rememberSaveable { mutableStateOf(false) }
    val premiumStateContext = androidx.compose.ui.platform.LocalContext.current
    val premiumStateManager = remember { com.loanmaster.pro.core.managers.PremiumManager(premiumStateContext.applicationContext) }
    val isPremiumUnlocked by premiumStateManager.isPremium.collectAsStateWithLifecycle(initialValue = false)"""

target2 = """            item { SupportAppSection(onPremiumClick = { showUnlockDialog = true }) }"""
replacement2 = """            item { SupportAppSection(onPremiumClick = { if (!isPremiumUnlocked) showUnlockDialog = true }) }"""

target3 = """            item { AccountSyncSection(onPremiumClick = { showUnlockDialog = true }) }"""
replacement3 = """            item { AccountSyncSection(onPremiumClick = { if (!isPremiumUnlocked) showUnlockDialog = true }) }"""

if target1 in content:
    content = content.replace(target1, replacement1)
    content = content.replace(target2, replacement2)
    content = content.replace(target3, replacement3)
    
    # Optional: Reuse premiumStateManager instead of PremiumManager(context) in PremiumUnlockDialog call
    content = content.replace("com.loanmaster.pro.core.managers.PremiumManager(context).unlockPermanent()", "premiumStateManager.unlockPermanent()")
    
    with open(filename, 'w') as f:
        f.write(content)
    print("Fix 2 (SettingsScreen.kt) Applied")
else:
    print("Fix 2 (SettingsScreen.kt) Target not found")


# FIX 3
filename = 'app/src/main/java/com/loanmaster/pro/feature/home/HomeScreen.kt'
with open(filename, 'r') as f:
    content = f.read()

target1 = """    var showUnlockDialog by rememberSaveable { mutableStateOf(false) }"""
replacement1 = """    var showUnlockDialog by rememberSaveable { mutableStateOf(false) }
    val premiumStateContext = androidx.compose.ui.platform.LocalContext.current
    val premiumStateManager = remember { com.loanmaster.pro.core.managers.PremiumManager(premiumStateContext.applicationContext) }
    val isPremiumUnlocked by premiumStateManager.isPremium.collectAsStateWithLifecycle(initialValue = false)"""

target2 = """                    SearchAndPremiumRow(searchQuery, onSearchQueryChange = { viewModel.updateSearchQuery(it) }, onPremiumClick = { showUnlockDialog = true })"""
replacement2 = """                    SearchAndPremiumRow(searchQuery, onSearchQueryChange = { viewModel.updateSearchQuery(it) }, onPremiumClick = { if (!isPremiumUnlocked) showUnlockDialog = true })"""

target3 = """                        HeroBanner(onPremiumClick = { showUnlockDialog = true })"""
replacement3 = """                        HeroBanner(onPremiumClick = { if (!isPremiumUnlocked) showUnlockDialog = true })"""

if target1 in content:
    content = content.replace(target1, replacement1)
    content = content.replace(target2, replacement2)
    content = content.replace(target3, replacement3)
    
    content = content.replace("com.loanmaster.pro.core.managers.PremiumManager(context).unlockPermanent()", "premiumStateManager.unlockPermanent()")
    
    with open(filename, 'w') as f:
        f.write(content)
    print("Fix 3 (HomeScreen.kt) Applied")
else:
    print("Fix 3 (HomeScreen.kt) Target not found")

