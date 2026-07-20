import os

filepath = 'app/src/main/java/com/loanmaster/pro/feature/emi/EmiScreen.kt'
with open(filepath, 'r') as f:
    content = f.read()

target = """    Scaffold(
        containerColor = bgColor,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(bgColor)
                    .statusBarsPadding()
                    .padding(horizontal = LoanMasterTheme.spacing.md, vertical = LoanMasterTheme.spacing.md),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Calculate, contentDescription = null, tint = Color(0xFF2D7DFF), modifier = Modifier.size(24.dp))
                        Spacer(Modifier.widthIn(min = 8.dp))
                        Text("EMI Calculator", color = primaryText, fontSize = LoanMasterTheme.typography.title.fontSize, fontWeight = FontWeight.Bold, minLines = 1, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    }
                    Text("Calculate your loan EMI and plan better", color = secondaryText, fontSize = LoanMasterTheme.typography.label.fontSize, minLines = 1, maxLines = 2, overflow = TextOverflow.Ellipsis)
                }
            }
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            CalculatorScreenLayout(
                widthSizeClass = sizeClass,
                animationTriggerState = uiState.monthlyEmi,
                headerSection = { },"""

replacement = """        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(bgColor)
        ) {
            CalculatorScreenLayout(
                widthSizeClass = sizeClass,
                animationTriggerState = uiState.monthlyEmi,
                headerSection = { 
                    Column(modifier = Modifier.padding(bottom = LoanMasterTheme.spacing.md)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Calculate, contentDescription = null, tint = Color(0xFF2D7DFF), modifier = Modifier.size(24.dp))
                            Spacer(Modifier.widthIn(min = 8.dp))
                            Text("EMI Calculator", color = primaryText, fontSize = LoanMasterTheme.typography.title.fontSize, fontWeight = FontWeight.Bold, minLines = 1, maxLines = 2, overflow = TextOverflow.Ellipsis)
                        }
                        Spacer(Modifier.heightIn(min = 4.dp))
                        Text("Calculate your loan EMI and plan better", color = secondaryText, fontSize = LoanMasterTheme.typography.label.fontSize, minLines = 1, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    }
                },"""

if target in content:
    content = content.replace(target, replacement)
    
    # Also we need to remove the closing brace of Scaffold
    # Let's find "} // closes Scaffold"
    content = content.replace("    } // closes Scaffold\n", "")
    
    with open(filepath, 'w') as f:
        f.write(content)
    print("Fixed EmiScreen.kt")
else:
    print("Target not found in EmiScreen.kt")
