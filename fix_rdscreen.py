import os
import re

filepath = 'app/src/main/java/com/loanmaster/pro/feature/rd/RdScreen.kt'
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
                        Text("RD Calculator", color = TextPrimary, fontSize = LoanMasterTheme.typography.title.fontSize, fontWeight = FontWeight.Bold)
                        Text("Plan your Recurring Deposit", color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize)
                    }
                    val context = androidx.compose.ui.platform.LocalContext.current
                    Icon(imageVector = Icons.Rounded.PictureAsPdf, contentDescription = "Export PDF", tint = TextPrimary, modifier = Modifier.size(LoanMasterTheme.spacing.lg).clickable {
                        ExportUtils.exportToPdf(
                            context,
                            "RD Calculator Report",
                            listOf(
                                "Monthly Deposit" to formatInr(calculatedMonthlyDeposit),
                                "Interest Rate" to "$interestRatePaText%",
                                "Time Period" to "$tenureYearsText Years",
                                "" to "",
                                "Total Invested" to formatInr(totalInvested),
                                "Total Returns" to formatInr(totalReturns),
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
                widthSizeClass = sizeClass,
                animationTriggerState = maturityValue,
                headerSection = { },"""

replacement = """        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundDark)
        ) {
            CalculatorScreenLayout(
                widthSizeClass = sizeClass,
                animationTriggerState = maturityValue,
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
                                Text("RD Calculator", color = TextPrimary, fontSize = LoanMasterTheme.typography.title.fontSize, fontWeight = FontWeight.Bold)
                                Text("Plan your Recurring Deposit", color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize)
                            }
                            val context = androidx.compose.ui.platform.LocalContext.current
                            Icon(imageVector = Icons.Rounded.PictureAsPdf, contentDescription = "Export PDF", tint = TextPrimary, modifier = Modifier.size(LoanMasterTheme.spacing.lg).clickable {
                                ExportUtils.exportToPdf(
                                    context,
                                    "RD Calculator Report",
                                    listOf(
                                        "Monthly Deposit" to formatInr(calculatedMonthlyDeposit),
                                        "Interest Rate" to "$interestRatePaText%",
                                        "Time Period" to "$tenureYearsText Years",
                                        "" to "",
                                        "Total Invested" to formatInr(totalInvested),
                                        "Total Returns" to formatInr(totalReturns),
                                        "Maturity Value" to formatInr(maturityValue)
                                    )
                                )
                            })
                        }
                    }
                },"""

if target in content:
    content = content.replace(target, replacement)
    
    # We need to remove BOTH closing braces.
    # ResponsiveScreenWrapper is closed by a "        }"
    # Scaffold is closed by a "    }"
    
    # Let's find the end of the file and remove the last two closing braces for those structures
    # We'll rely on the brace balancer to fix it
    with open(filepath, 'w') as f:
        f.write(content)
    print("Fixed RdScreen.kt")
else:
    print("Target not found in RdScreen.kt")
