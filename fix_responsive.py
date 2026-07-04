with open('app/src/main/java/com/loanmaster/pro/ui/theme/Responsive.kt', 'r') as f:
    content = f.read()

import re

new_wrapper = """fun ResponsiveScreenWrapper(
    modifier: Modifier = Modifier,
    showDiagnostics: Boolean = false,
    content: @Composable BoxWithConstraintsScope.() -> Unit
) {
    Box(modifier = modifier, contentAlignment = Alignment.TopCenter) {
        BoxWithConstraints(modifier = Modifier.widthIn(max = 840.dp).fillMaxWidth()) {
            content()
            if (showDiagnostics) {
                GridDiagnosticOverlay()
            }
        }
    }
}"""

content = re.sub(r'fun ResponsiveScreenWrapper\([\s\S]*?\}\n\}\n', new_wrapper + '\n', content)

# we need to import Box, Alignment, fillMaxWidth
if "import androidx.compose.foundation.layout.Box\n" not in content:
    content = content.replace('import androidx.compose.foundation.layout.BoxWithConstraints', 'import androidx.compose.foundation.layout.BoxWithConstraints\nimport androidx.compose.foundation.layout.Box\nimport androidx.compose.foundation.layout.fillMaxWidth')
if "import androidx.compose.ui.Alignment" not in content:
    content = content.replace('import androidx.compose.ui.Modifier', 'import androidx.compose.ui.Modifier\nimport androidx.compose.ui.Alignment')

with open('app/src/main/java/com/loanmaster/pro/ui/theme/Responsive.kt', 'w') as f:
    f.write(content)
