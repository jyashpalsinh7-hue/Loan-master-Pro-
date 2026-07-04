with open('app/src/main/java/com/loanmaster/pro/SharedUI.kt', 'r') as f:
    content = f.read()

import re

new_wrapper = """fun ResponsiveScreenWrapper(
    widthSizeClass: WindowWidthSizeClass,
    headerSection: @Composable () -> Unit,
    inputControlsSection: @Composable () -> Unit,
    resultsSection: @Composable () -> Unit,
    animationTriggerState: Any? = null
) {
    val scrollState = androidx.compose.foundation.rememberScrollState()
    
    val animatedResults: @Composable () -> Unit = {
        if (animationTriggerState != null) {
            androidx.compose.animation.AnimatedContent(
                targetState = animationTriggerState,
                transitionSpec = {
                    androidx.compose.animation.fadeIn() togetherWith androidx.compose.animation.fadeOut()
                },
                label = "resultsAnimation"
            ) { _ ->
                resultsSection()
            }
        } else {
            resultsSection()
        }
    }

    val globalHeader: @Composable () -> Unit = {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.foundation.layout.Box(modifier = Modifier.weight(1f)) {
                headerSection()
            }
        }
    }

    androidx.compose.foundation.layout.Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        androidx.compose.foundation.layout.Column(
            modifier = Modifier
                .widthIn(max = 840.dp)
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(horizontal = ResponsiveUtils.horizontalPadding(widthSizeClass), vertical = LoanMasterTheme.spacing.md)
        ) {
            globalHeader()

            if (widthSizeClass == WindowWidthSizeClass.Expanded) {
                androidx.compose.foundation.layout.Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.lg))
                androidx.compose.foundation.layout.Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(LoanMasterTheme.spacing.lg)
                ) {
                    androidx.compose.foundation.layout.Column(modifier = Modifier.weight(1f)) {
                        inputControlsSection()
                    }
                    androidx.compose.foundation.layout.Column(modifier = Modifier.weight(1f)) {
                        animatedResults()
                    }
                }
            } else {
                androidx.compose.foundation.layout.Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.md))
                inputControlsSection()
                androidx.compose.foundation.layout.Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.lg))
                animatedResults()
            }
        }
    }
}"""

content = re.sub(r'fun ResponsiveScreenWrapper\([\s\S]*?\}\n\}\n', new_wrapper + '\n', content)

with open('app/src/main/java/com/loanmaster/pro/SharedUI.kt', 'w') as f:
    f.write(content)
