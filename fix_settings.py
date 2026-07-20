import sys

filename = 'app/src/main/java/com/loanmaster/pro/feature/settings/SettingsScreen.kt'
with open(filename, 'r') as f:
    content = f.read()

target = """    Scaffold(
        topBar = { SettingsTopBar(onNavigateBack) },
        containerColor = BackgroundDark
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = LoanMasterTheme.spacing.md, vertical = LoanMasterTheme.spacing.sm),
            verticalArrangement = Arrangement.spacedBy(LoanMasterTheme.components.iconSmall)
        ) {"""

replacement = """    Scaffold(
        topBar = { SettingsTopBar(onNavigateBack) },
        containerColor = BackgroundDark
    ) { innerPadding ->
        ResponsiveScreenWrapper(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = LoanMasterTheme.spacing.md, vertical = LoanMasterTheme.spacing.sm)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(LoanMasterTheme.components.iconSmall)
            ) {"""

content = content.replace(target, replacement)
content = content.replace("            item { Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.md)) }\n        }\n        if (showUnlockDialog)", "            item { Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.md)) }\n            }\n        }\n        if (showUnlockDialog)")

with open(filename, 'w') as f:
    f.write(content)
print("Done")
