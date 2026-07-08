import re

with open('app/src/main/java/com/loanmaster/pro/feature/loaneligibility/LoanEligibilityScreen.kt', 'r') as f:
    content = f.read()

hero_replacement = """            // 7. Hero Results Dashboard
            Card(
                colors = CardDefaults.cardColors(containerColor = surfaceColor.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(LoanMasterTheme.components.cardRadius),
                border = androidx.compose.foundation.BorderStroke(1.dp, surfaceColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(LoanMasterTheme.spacing.md)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Estimated Eligible Loan Amount", color = textColor, style = LoanMasterTheme.typography.body, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.xs))
                        Icon(Icons.Rounded.Info, contentDescription = null, tint = textSecondary, modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.md))
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                        Column {
                            AutoResizeHeroText(
                                text = com.loanmaster.pro.core.formatter.formatMoney(eligibleLoanAmount),
                                color = neonGreen
                            )
                            Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.sm))
                            val isSafe = currentFoir <= (foirLimit * 100)
                            Row(
                                modifier = Modifier.clip(RoundedCornerShape(LoanMasterTheme.spacing.xs)).background((if(isSafe) neonGreen else dangerRed).copy(alpha = 0.1f)).border(1.dp, if(isSafe) neonGreen else dangerRed, RoundedCornerShape(LoanMasterTheme.spacing.xs)).padding(horizontal = LoanMasterTheme.spacing.sm, vertical = LoanMasterTheme.spacing.xs),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(if(isSafe) Icons.Rounded.CheckCircle else Icons.Rounded.Warning, contentDescription = null, tint = if(isSafe) neonGreen else dangerRed, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.widthIn(min = 4.dp))
                                Text(if(isSafe) "You are in the Safe Zone" else "High Debt Burden", color = if(isSafe) neonGreen else dangerRed, style = LoanMasterTheme.typography.label)
                            }
                        }
                        Icon(Icons.Rounded.AccountBalance, contentDescription = null, tint = brightBlue, modifier = Modifier.size(64.dp))
                    }
                    
                    Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.lg))
                    
                    Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(LoanMasterTheme.spacing.sm)).background(bgColor).padding(LoanMasterTheme.spacing.md), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Info, contentDescription = null, tint = textSecondary, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.md))
                        Text("This is an estimated amount based on the inputs provided. Final approval depends on bank policies.", color = textSecondary, style = LoanMasterTheme.typography.label, modifier = Modifier.weight(1f))
                    }
                    
                    Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.lg))
                    
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Monthly Income", color = textSecondary, style = LoanMasterTheme.typography.label)
                            Spacer(modifier = Modifier.heightIn(min = 4.dp))
                            Text(com.loanmaster.pro.core.formatter.formatMoney(totalIncome), color = textColor, style = LoanMasterTheme.typography.body, fontWeight = FontWeight.Bold)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Total Existing EMIs", color = textSecondary, style = LoanMasterTheme.typography.label)
                            Spacer(modifier = Modifier.heightIn(min = 4.dp))
                            Text(com.loanmaster.pro.core.formatter.formatMoney(totalExistingEmi), color = textColor, style = LoanMasterTheme.typography.body, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }"""

pattern = r"// 7\. Hero Results Dashboard.*?// 8\. The Three Metric Cards"
content = re.sub(pattern, hero_replacement + "\n\n            // 8. The Three Metric Cards", content, flags=re.DOTALL)

with open('app/src/main/java/com/loanmaster/pro/feature/loaneligibility/LoanEligibilityScreen.kt', 'w') as f:
    f.write(content)
