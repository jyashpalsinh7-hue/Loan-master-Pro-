import re

with open("app/src/main/java/com/example/LoanSummaryScreen.kt", "r") as f:
    content = f.read()

# 1. safeDrawingPadding to Scaffold
content = re.sub(r'Scaffold\(\s*containerColor', 'Scaffold(\n        modifier = Modifier.safeDrawingPadding(),\n        containerColor', content)

# 2. Add BoxWithConstraints / Column wrapper inside Scaffold
scaffold_body = """    ) { padding ->
        BoxWithConstraints(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.TopCenter) {
            LazyColumn(
                modifier = Modifier
                    .widthIn(max = 840.dp)
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),"""
content = re.sub(r'\) \{ padding ->\s*LazyColumn\(\s*modifier = Modifier\s*\.fillMaxSize\(\)\s*\.padding\(padding\)\s*\.padding\(horizontal = 16\.dp\),', scaffold_body, content)
content = re.sub(r'\s*\}\s*if \(showAddLoanDialog\)', '        }\n    }\n\n    if (showAddLoanDialog)', content) # Close BoxWithConstraints

# 3. Replace width and height
content = re.sub(r'\.width\((\d+\.dp)\)', r'.widthIn(min = \1)', content)
content = re.sub(r'\.height\((\d+\.dp)\)', r'.heightIn(min = \1)', content)

# 4. Add WindowSizeClass code to top
size_class_code = """
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val windowSizeClass = when {
        configuration.screenWidthDp < 600 -> WindowWidthSizeClass.Compact
        configuration.screenWidthDp < 840 -> WindowWidthSizeClass.Medium
        else -> WindowWidthSizeClass.Expanded
    }
"""
content = re.sub(r'(val activeLoans by viewModel\.activeLoans\.collectAsStateWithLifecycle\(\))', r'\1' + size_class_code, content)

with open("app/src/main/java/com/example/LoanSummaryScreen.kt", "w") as f:
    f.write(content)
