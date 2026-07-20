import sys

filename = 'app/src/main/java/com/loanmaster/pro/core/responsive/Responsive.kt'
with open(filename, 'r') as f:
    content = f.read()

target = """@Composable
fun ResponsiveScreenWrapper(
    modifier: Modifier = Modifier,
    showDiagnostics: Boolean = false,
    content: @Composable BoxWithConstraintsScope.() -> Unit
) {
    BoxWithConstraints(modifier = modifier) {
        content()
        if (showDiagnostics) {
            GridDiagnosticOverlay()
        }
    }
}"""

replacement = """@Composable
fun ResponsiveScreenWrapper(
    modifier: Modifier = Modifier,
    showDiagnostics: Boolean = false,
    content: @Composable BoxWithConstraintsScope.() -> Unit
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.TopCenter) {
        BoxWithConstraints(modifier = modifier.widthIn(max = 840.dp)) {
            content()
            if (showDiagnostics) {
                GridDiagnosticOverlay()
            }
        }
    }
}"""

content = content.replace(target, replacement)

with open(filename, 'w') as f:
    f.write(content)
print("Done")
