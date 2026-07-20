import os

filepath = 'app/src/main/java/com/loanmaster/pro/feature/gst/GstScreen.kt'
with open(filepath, 'r') as f:
    content = f.read()

target = """    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(BackgroundDark).statusBarsPadding()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = LoanMasterTheme.spacing.md, vertical = LoanMasterTheme.spacing.md),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                    Text(
                        text = "GST Calculator",
                        color = TextPrimary,
                        fontSize = LoanMasterTheme.typography.title.fontSize,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f).padding(start = LoanMasterTheme.spacing.sm)
                    )
                    val context = androidx.compose.ui.platform.LocalContext.current
                    IconButton(onClick = {
                        ExportUtils.exportToPdf(
                            context,
                            "GST Calculator Report",
                            listOf(
                                "Calculation Mode" to if(mode == GstMode.ADD) "Add GST" else "Remove GST",
                                "Base Amount" to com.loanmaster.pro.core.formatter.formatMoney(baseAmount),
                                "GST Rate" to "$actualRate%",
                                "Cess Rate" to "$cessRate%",
                                "" to "",
                                "CGST" to com.loanmaster.pro.core.formatter.formatMoney(cgst),
                                "SGST" to com.loanmaster.pro.core.formatter.formatMoney(sgst),
                                "IGST" to com.loanmaster.pro.core.formatter.formatMoney(igst),
                                "Total GST" to com.loanmaster.pro.core.formatter.formatMoney(totalGst),
                                "Total Cess" to com.loanmaster.pro.core.formatter.formatMoney(totalCess),
                                "Total Amount" to com.loanmaster.pro.core.formatter.formatMoney(totalAmount)
                            )
                        )
                    }) {
                        Icon(Icons.Rounded.PictureAsPdf, contentDescription = "Export PDF", tint = TextSecondary)
                    }
                    IconButton(onClick = { viewModel.updateInputs(reset = true) }) {
                        Icon(Icons.Rounded.Refresh, contentDescription = "Reset", tint = TextSecondary)
                    }
                }
            }
        },
        containerColor = BackgroundDark,
        modifier = Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null
        ) { focusManager.clearFocus() }
    ) { paddingValues ->
        com.loanmaster.pro.core.responsive.ResponsiveScreenWrapper(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(LoanMasterTheme.spacing.md),
                verticalArrangement = Arrangement.spacedBy(LoanMasterTheme.components.iconSmall)
            ) {"""

replacement = """    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { focusManager.clearFocus() }
    ) {
        com.loanmaster.pro.core.responsive.ResponsiveScreenWrapper(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(LoanMasterTheme.spacing.md),
                verticalArrangement = Arrangement.spacedBy(LoanMasterTheme.components.iconSmall)
            ) {
                // Top Bar content
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = LoanMasterTheme.spacing.md),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                    Text(
                        text = "GST Calculator",
                        color = TextPrimary,
                        fontSize = LoanMasterTheme.typography.title.fontSize,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f).padding(start = LoanMasterTheme.spacing.sm)
                    )
                    val context = androidx.compose.ui.platform.LocalContext.current
                    IconButton(onClick = {
                        ExportUtils.exportToPdf(
                            context,
                            "GST Calculator Report",
                            listOf(
                                "Calculation Mode" to if(mode == GstMode.ADD) "Add GST" else "Remove GST",
                                "Base Amount" to com.loanmaster.pro.core.formatter.formatMoney(baseAmount),
                                "GST Rate" to "$actualRate%",
                                "Cess Rate" to "$cessRate%",
                                "" to "",
                                "CGST" to com.loanmaster.pro.core.formatter.formatMoney(cgst),
                                "SGST" to com.loanmaster.pro.core.formatter.formatMoney(sgst),
                                "IGST" to com.loanmaster.pro.core.formatter.formatMoney(igst),
                                "Total GST" to com.loanmaster.pro.core.formatter.formatMoney(totalGst),
                                "Total Cess" to com.loanmaster.pro.core.formatter.formatMoney(totalCess),
                                "Total Amount" to com.loanmaster.pro.core.formatter.formatMoney(totalAmount)
                            )
                        )
                    }) {
                        Icon(Icons.Rounded.PictureAsPdf, contentDescription = "Export PDF", tint = TextSecondary)
                    }
                    IconButton(onClick = { viewModel.updateInputs(reset = true) }) {
                        Icon(Icons.Rounded.Refresh, contentDescription = "Reset", tint = TextSecondary)
                    }
                }
"""

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
    print("Fixed GstScreen.kt")
else:
    print("Target not found in GstScreen.kt")
