import os

filepath = 'app/src/main/java/com/loanmaster/pro/feature/fd/FdScreen.kt'
with open(filepath, 'r') as f:
    content = f.read()

target = """    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(BackgroundDark).statusBarsPadding()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = LoanMasterTheme.spacing.md, vertical = LoanMasterTheme.spacing.md),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary,
                        modifier = Modifier.size(LoanMasterTheme.spacing.lg).clickable { onNavigateBack() }
                    )
                    Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.md))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("FD Calculator", color = TextPrimary, fontSize = LoanMasterTheme.typography.title.fontSize, fontWeight = FontWeight.Bold)
                        Text("Calculate fixed deposit returns", color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize)
                    }
                    val context = androidx.compose.ui.platform.LocalContext.current
                    Icon(imageVector = Icons.Rounded.PictureAsPdf, contentDescription = "Export PDF", tint = TextPrimary, modifier = Modifier.size(LoanMasterTheme.spacing.lg).clickable {
                        ExportUtils.exportToPdf(
                            context,
                            "FD Calculator Report",
                            listOf(
                                "Total Investment" to formatInr(totalInvested),
                                "Interest Rate" to "$interestRatePaText%",
                                "Time Period" to "$tenureYearsText Years",
                                "" to "",
                                "Total Interest" to formatInr(totalInterest),
                                "Maturity Value" to formatInr(maturityValue)
                            )
                        )
                    })
                }
            }
        },
        containerColor = BackgroundDark
    ) { innerPadding ->
        com.loanmaster.pro.core.responsive.ResponsiveScreenWrapper(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            CalculatorScreenLayout(
                widthSizeClass = sizeClass,"""

replacement = """        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundDark)
        ) {
            CalculatorScreenLayout(
                widthSizeClass = sizeClass,
                headerSection = {
                    Column(modifier = Modifier.padding(bottom = LoanMasterTheme.spacing.md)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = "Back",
                                tint = TextPrimary,
                                modifier = Modifier.size(LoanMasterTheme.spacing.lg).clickable { onNavigateBack() }
                            )
                            Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.md))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("FD Calculator", color = TextPrimary, fontSize = LoanMasterTheme.typography.title.fontSize, fontWeight = FontWeight.Bold)
                                Text("Calculate fixed deposit returns", color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize)
                            }
                            val context = androidx.compose.ui.platform.LocalContext.current
                            Icon(imageVector = Icons.Rounded.PictureAsPdf, contentDescription = "Export PDF", tint = TextPrimary, modifier = Modifier.size(LoanMasterTheme.spacing.lg).clickable {
                                ExportUtils.exportToPdf(
                                    context,
                                    "FD Calculator Report",
                                    listOf(
                                        "Total Investment" to formatInr(totalInvested),
                                        "Interest Rate" to "$interestRatePaText%",
                                        "Time Period" to "$tenureYearsText Years",
                                        "" to "",
                                        "Total Interest" to formatInr(totalInterest),
                                        "Maturity Value" to formatInr(maturityValue)
                                    )
                                )
                            })
                        }
                    }
                },"""

if target in content:
    content = content.replace(target, replacement)
    with open(filepath, 'w') as f:
        f.write(content)
    print("Fixed FdScreen.kt")
else:
    print("Target not found in FdScreen.kt")
