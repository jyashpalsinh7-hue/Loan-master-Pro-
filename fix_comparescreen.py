import os

filepath = 'app/src/main/java/com/loanmaster/pro/feature/compare/CompareScreen.kt'
with open(filepath, 'r') as f:
    content = f.read()

target = """        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("Compare Loans", color = textColor, fontWeight = FontWeight.Bold)
                        Text("Compare up to 3 loan options side by side", color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back", tint = textColor)
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Rounded.Info, contentDescription = "Info", tint = textColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = bgColor)
            )
        }"""

replacement = """        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(bgColor)
                    .statusBarsPadding()
                    .padding(horizontal = LoanMasterTheme.spacing.sm, vertical = LoanMasterTheme.spacing.sm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back", tint = textColor)
                }
                Column(modifier = Modifier.weight(1f).padding(horizontal = LoanMasterTheme.spacing.xs)) {
                    Text("Compare Loans", color = textColor, fontWeight = FontWeight.Bold, fontSize = LoanMasterTheme.typography.title.fontSize)
                    Text("Compare up to 3 loan options side by side", color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize)
                }
                IconButton(onClick = { }) {
                    Icon(Icons.Rounded.Info, contentDescription = "Info", tint = textColor)
                }
            }
        }"""

if target in content:
    content = content.replace(target, replacement)
    with open(filepath, 'w') as f:
        f.write(content)
    print("Fixed CompareScreen.kt")
else:
    print("Target not found in CompareScreen.kt")
