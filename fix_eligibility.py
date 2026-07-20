import os

filepath = 'app/src/main/java/com/loanmaster/pro/feature/loaneligibility/LoanEligibilityScreen.kt'
with open(filepath, 'r') as f:
    content = f.read()

target = """    Scaffold(
        containerColor = bgColor
    ) { paddingValues ->
        ResponsiveScreenWrapper(
            modifier = Modifier
                .fillMaxSize()
                .background(bgColor)
                .padding(paddingValues),
            showDiagnostics = false
        ) {"""

replacement = """    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        ResponsiveScreenWrapper(
            modifier = Modifier
                .fillMaxSize(),
            showDiagnostics = false
        ) {"""

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
    print("Fixed LoanEligibilityScreen.kt")
else:
    print("Target not found in LoanEligibilityScreen.kt")
