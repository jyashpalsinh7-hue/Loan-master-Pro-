import os

filepath = 'app/src/main/java/com/loanmaster/pro/feature/prepayment/PrepaymentScreen.kt'
with open(filepath, 'r') as f:
    content = f.read()

target = """    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(bgColor).statusBarsPadding()) {
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
                        text = "Prepayment",
                        color = Color.White,
                        fontSize = LoanMasterTheme.typography.title.fontSize,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        containerColor = bgColor
    ) { paddingValues ->
        com.loanmaster.pro.core.responsive.ResponsiveScreenWrapper(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = LoanMasterTheme.spacing.screenPadding),
            verticalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.lg)
        ) {"""

replacement = """    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        com.loanmaster.pro.core.responsive.ResponsiveScreenWrapper(
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = LoanMasterTheme.spacing.screenPadding),
            verticalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.lg)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = LoanMasterTheme.spacing.md),
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
                        text = "Prepayment",
                        color = Color.White,
                        fontSize = LoanMasterTheme.typography.title.fontSize,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                }
            }"""

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
    print("Fixed PrepaymentScreen.kt")
else:
    print("Target not found in PrepaymentScreen.kt")
