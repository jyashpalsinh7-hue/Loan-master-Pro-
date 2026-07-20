import os

filepath = 'app/src/main/java/com/loanmaster/pro/feature/sip/SipScreen.kt'
with open(filepath, 'r') as f:
    content = f.read()

target = """    Scaffold(
        containerColor = NavyBg,
        topBar = {
            SipTopBar(
                onNavigateBack = onNavigateBack,
                onExportClick = {
                    ExportUtils.exportToPdf(
                        context,
                        "SIP Calculator Report",
                        listOf(
                            "Monthly Investment" to amountText,
                            "Expected Return Rate" to "$returnRateText%",
                            "Time Period" to "$yearsText Years",
                            "Annual Step-Up" to "$stepUpText%",
                            "" to "",
                            "Total Invested" to com.loanmaster.pro.core.formatter.formatMoney(totalInvested),
                            "Est. Returns" to com.loanmaster.pro.core.formatter.formatMoney(totalGain),
                            "Total Value" to com.loanmaster.pro.core.formatter.formatMoney(maturityValue)
                        )
                    )
                }
            )
        },
        modifier = Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() }, indication = null
        ) { focusManager.clearFocus() }
    ) { paddingValues ->
        com.loanmaster.pro.core.responsive.ResponsiveScreenWrapper(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = if (isWide) LoanMasterTheme.spacing.xl else LoanMasterTheme.spacing.md, vertical = LoanMasterTheme.spacing.lg),
            verticalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.lg)
        ) {"""

replacement = """    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBg)
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { focusManager.clearFocus() }
    ) {
        com.loanmaster.pro.core.responsive.ResponsiveScreenWrapper(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = if (isWide) LoanMasterTheme.spacing.xl else LoanMasterTheme.spacing.md, vertical = LoanMasterTheme.spacing.lg),
                verticalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.lg)
            ) {
                SipTopBar(
                    onNavigateBack = onNavigateBack,
                    onExportClick = {
                        ExportUtils.exportToPdf(
                            context,
                            "SIP Calculator Report",
                            listOf(
                                "Monthly Investment" to amountText,
                                "Expected Return Rate" to "$returnRateText%",
                                "Time Period" to "$yearsText Years",
                                "Annual Step-Up" to "$stepUpText%",
                                "" to "",
                                "Total Invested" to com.loanmaster.pro.core.formatter.formatMoney(totalInvested),
                                "Est. Returns" to com.loanmaster.pro.core.formatter.formatMoney(totalGain),
                                "Total Value" to com.loanmaster.pro.core.formatter.formatMoney(maturityValue)
                            )
                        )
                    }
                )"""

if target in content:
    content = content.replace(target, replacement)
    
    # Balance braces
    total_open = content.count('{')
    total_close = content.count('}')
    diff = total_close - total_open
    if diff > 0:
        lines = content.split('\n')
        for i in range(len(lines)-1, -1, -1):
            if '}' in lines[i]:
                lines[i] = lines[i].replace('}', '', 1)
                diff -= 1
                if diff == 0:
                    break
        content = '\n'.join(lines)
    
    with open(filepath, 'w') as f:
        f.write(content)
    print("Fixed SipScreen.kt")
else:
    print("Target not found in SipScreen.kt")

# For SipTopBar, we need to remove `.statusBarsPadding()` since it's now inside the safe padding.
with open(filepath, 'r') as f:
    content = f.read()

content = content.replace(".statusBarsPadding()", "")
with open(filepath, 'w') as f:
    f.write(content)
