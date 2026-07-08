import re

with open('app/src/main/java/com/loanmaster/pro/feature/loaneligibility/LoanEligibilityScreen.kt', 'r') as f:
    content = f.read()

cards_replacement = """            // 8. The Three Metric Cards
            Card(
                colors = CardDefaults.cardColors(containerColor = surfaceColor.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(LoanMasterTheme.components.cardRadius),
                border = androidx.compose.foundation.BorderStroke(1.dp, surfaceColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(LoanMasterTheme.components.cardRadius)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Approval Probability", color = textColor, style = LoanMasterTheme.typography.body, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.xs))
                        Icon(Icons.Rounded.Info, contentDescription = null, tint = textSecondary, modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.md))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val approvalProb = (1.0f - (currentFoir.toFloat() / 100f)).coerceIn(0f, 1f)
                        Box(modifier = Modifier.size(64.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(progress = { approvalProb }, color = neonGreen, trackColor = bgColor, gapSize = 0.dp, modifier = Modifier.size(64.dp))
                            Text("${(approvalProb * 100).toInt()}%", color = textColor, style = LoanMasterTheme.typography.body, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.md))
                        Column {
                            Text(if(approvalProb > 0.6) "High Chance" else "Low Chance", color = if(approvalProb > 0.6) neonGreen else dangerRed, style = LoanMasterTheme.typography.title, fontWeight = FontWeight.Bold)
                            Text(if(approvalProb > 0.6) "Excellent" else "Poor", color = textColor, style = LoanMasterTheme.typography.body)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.md))
            
            AdaptiveRowCol(
                modifier = Modifier.fillMaxWidth(),
                content1 = { mod ->
                    Card(
                        modifier = mod.heightIn(min = 140.dp),
                        colors = CardDefaults.cardColors(containerColor = surfaceColor.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(LoanMasterTheme.components.cardRadius),
                        border = androidx.compose.foundation.BorderStroke(1.dp, surfaceColor)
                    ) {
                        Column(modifier = Modifier.padding(LoanMasterTheme.components.cardRadius)) {
                            Text("Recommended Loan Amount", color = textColor, style = LoanMasterTheme.typography.body, fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                            Spacer(modifier = Modifier.weight(1f))
                            Text(com.loanmaster.pro.core.formatter.formatMoney(recommendedLoanAmount), color = brightBlue, style = LoanMasterTheme.typography.title, fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("For better financial stability", color = textSecondary, style = LoanMasterTheme.typography.label, lineHeight = LoanMasterTheme.typography.label.fontSize)
                        }
                    }
                },
                content2 = { mod ->
                    Card(
                        modifier = mod.heightIn(min = 140.dp),
                        colors = CardDefaults.cardColors(containerColor = surfaceColor.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(LoanMasterTheme.components.cardRadius),
                        border = androidx.compose.foundation.BorderStroke(1.dp, surfaceColor)
                    ) {
                        Column(modifier = Modifier.padding(LoanMasterTheme.components.cardRadius)) {
                            Text("Max Affordable EMI", color = textColor, style = LoanMasterTheme.typography.body, fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                            Spacer(modifier = Modifier.weight(1f))
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(com.loanmaster.pro.core.formatter.formatMoney(maxAllowedEmi), color = warningYellow, style = LoanMasterTheme.typography.title, fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false)
                                Text("/month", color = textSecondary, style = LoanMasterTheme.typography.label, modifier = Modifier.padding(bottom = 2.dp, start = 2.dp))
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("Keep your FOIR under ${(foirLimit * 100).toInt()}%", color = textSecondary, style = LoanMasterTheme.typography.label, lineHeight = LoanMasterTheme.typography.label.fontSize)
                        }
                    }
                }
            )"""

pattern = r"// 8\. The Three Metric Cards.*?// 9\. What If\? Section"
content = re.sub(pattern, cards_replacement + "\n\n            // 9. What If? Section", content, flags=re.DOTALL)

with open('app/src/main/java/com/loanmaster/pro/feature/loaneligibility/LoanEligibilityScreen.kt', 'w') as f:
    f.write(content)
