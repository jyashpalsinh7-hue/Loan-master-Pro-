import re

with open('app/src/main/java/com/loanmaster/pro/feature/loaneligibility/LoanEligibilityScreen.kt', 'r') as f:
    content = f.read()

# Add showResults
content = content.replace('val currentFoir = uiState.currentFoir\n', 'val currentFoir = uiState.currentFoir\n    var showResults by androidx.compose.runtime.rememberSaveable { androidx.compose.runtime.mutableStateOf(false) }\n')

# Wrap results
results_start = """            // 7. Hero Results Dashboard"""
new_results_start = """            if (!showResults) {
                Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.md))
                Button(
                    onClick = { showResults = true },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 64.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = brightBlue, contentColor = bgColor),
                    shape = RoundedCornerShape(LoanMasterTheme.spacing.sm)
                ) {
                    Icon(Icons.Rounded.Calculate, contentDescription = null, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.sm))
                    Text("Calculate Eligibility", style = LoanMasterTheme.typography.body, fontWeight = FontWeight.Bold, color = Color.White)
                }
                Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.sm))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Lock, contentDescription = null, tint = textSecondary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.widthIn(min = 4.dp))
                    Text("Your data is secure and private", color = textSecondary, style = LoanMasterTheme.typography.label)
                }
            } else {
            // 7. Hero Results Dashboard"""

content = content.replace(results_start, new_results_start)

# Now we need to close the else block after 10. Bottom Action Buttons
# We will just find the end of the Column inside ResponsiveScreenWrapper
# Actually, we can just replace the end of the file.
# We'll use regex to find the end of the `Column` block.

# Since we don't want to mess up braces, let's just use `sed` or python to find the last `}` before `}` of `Column`... wait, it's safer to just inject it before `} // End of Column` or something. Let's see the end of the file.
