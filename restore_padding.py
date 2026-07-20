import os

def restore_padding(filepath):
    with open(filepath, 'r') as f:
        content = f.read()

    # in ResponsiveScreenWrapper
    content = content.replace("Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.TopCenter)",
                              "Box(modifier = Modifier.fillMaxSize().safeDrawingPadding(), contentAlignment = androidx.compose.ui.Alignment.TopCenter)")

    # in CalculatorScreenLayout
    content = content.replace("androidx.compose.foundation.layout.Box(\n        modifier = Modifier.fillMaxSize(),\n        contentAlignment = Alignment.TopCenter\n    )",
                              "androidx.compose.foundation.layout.Box(\n        modifier = Modifier.fillMaxSize().safeDrawingPadding(),\n        contentAlignment = Alignment.TopCenter\n    )")

    with open(filepath, 'w') as f:
        f.write(content)
    print(f"Restored {filepath}")

restore_padding('app/src/main/java/com/loanmaster/pro/core/responsive/Responsive.kt')
restore_padding('app/src/main/java/com/loanmaster/pro/core/ui/SharedUI.kt')
