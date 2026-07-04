import re
with open("app/src/main/java/com/loanmaster/pro/SharedUI.kt", "r") as f:
    text = f.read()

target = """    androidx.compose.foundation.layout.Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {"""

replacement = """    com.loanmaster.pro.ui.theme.ResponsiveScreenWrapper(
        modifier = Modifier.fillMaxSize()
    ) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {"""

text = text.replace(target, replacement)

# We also need to add a closing brace at the end of the CalculatorScreenLayout function
# The end of the function is:
#             }
#         }
#     }
# }
# Wait, let's find the closing brace.
