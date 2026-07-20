import os

filepath = 'app/src/main/java/com/loanmaster/pro/feature/currency/CurrencyScreen.kt'
with open(filepath, 'r') as f:
    content = f.read()

target = """    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(CurrBgColor).statusBarsPadding()) {
                // FIX: Remove old offline banner, error handled in body
                if (uiState.error != null && uiState.rates.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFE53935))
                            .padding(horizontal = LoanMasterTheme.spacing.md, vertical = LoanMasterTheme.spacing.sm),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Rounded.Warning, contentDescription = null, tint = Color.White, modifier = Modifier.size(LoanMasterTheme.spacing.md))
                        Spacer(Modifier.widthIn(min = LoanMasterTheme.spacing.sm))
                        Text(
                            text = "Offline / Showing Last Known Rates",
                            color = Color.White,
                            fontSize = LoanMasterTheme.typography.label.fontSize,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = LoanMasterTheme.spacing.md, vertical = LoanMasterTheme.spacing.md),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(LoanMasterTheme.components.iconMedium).clickable { onNavigateBack() }
                    )
                    Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.md))
                    Text(
                        text = "Exchange",
                        color = Color.White,
                        fontSize = LoanMasterTheme.typography.title.fontSize,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { viewModel.updateInputs(refreshRates = true) }) {
                        Icon(imageVector = Icons.Rounded.Sync, contentDescription = "Refresh", tint = Color.White)
                    }
                }
            }
        },
        containerColor = CurrBgColor
    ) { innerPadding ->
        com.loanmaster.pro.core.responsive.ResponsiveScreenWrapper(modifier = Modifier.padding(innerPadding).fillMaxSize()) {"""

replacement = """    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CurrBgColor)
    ) {
        com.loanmaster.pro.core.responsive.ResponsiveScreenWrapper(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.background(CurrBgColor)) {
                if (uiState.error != null && uiState.rates.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFE53935))
                            .padding(horizontal = LoanMasterTheme.spacing.md, vertical = LoanMasterTheme.spacing.sm),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Rounded.Warning, contentDescription = null, tint = Color.White, modifier = Modifier.size(LoanMasterTheme.spacing.md))
                        Spacer(Modifier.widthIn(min = LoanMasterTheme.spacing.sm))
                        Text(
                            text = "Offline / Showing Last Known Rates",
                            color = Color.White,
                            fontSize = LoanMasterTheme.typography.label.fontSize,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = LoanMasterTheme.spacing.md, vertical = LoanMasterTheme.spacing.md),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(LoanMasterTheme.components.iconMedium).clickable { onNavigateBack() }
                    )
                    Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.md))
                    Text(
                        text = "Exchange",
                        color = Color.White,
                        fontSize = LoanMasterTheme.typography.title.fontSize,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { viewModel.updateInputs(refreshRates = true) }) {
                        Icon(imageVector = Icons.Rounded.Sync, contentDescription = "Refresh", tint = Color.White)
                    }
                }
            }"""

if target in content:
    content = content.replace(target, replacement)
    
    # Let's remove the `.padding(innerPadding)` from inside the view
    content = content.replace(".padding(innerPadding)", "")
    
    with open(filepath, 'w') as f:
        f.write(content)
    print("Fixed CurrencyScreen.kt")
else:
    print("Target not found.")

