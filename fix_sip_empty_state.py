import os

file_path = "app/src/main/java/com/loanmaster/pro/feature/sip/SipScreen.kt"
with open(file_path, "r") as f:
    content = f.read()

target = """            InputsSection(
                uiState = uiState, 
                updateInputs = { amount, rate, years, stepUp ->
                    viewModel.updateInputs(amount = amount, rate = rate, years = years, stepUp = stepUp)
                }, 
                isWide = isWide
            )
            HeroCard(totalInvested, totalGain, maturityValue, returnRate, years, isWide, uiState)
            
            if (isWide) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.lg)) {
                    GrowthVisualizationCard(yearlyDataList, modifier = Modifier.weight(1.5f))
                    Box(modifier = Modifier.weight(1f)) { InflationAdjustedCard(maturityValue, years, uiState, onUnlockPremium = { viewModel.unlockPremium() }) }
                }
            } else {
                GrowthVisualizationCard(yearlyDataList, modifier = Modifier.fillMaxWidth())
                InflationAdjustedCard(maturityValue, years, uiState, onUnlockPremium = { viewModel.unlockPremium() })
            }
            
            LifestyleFundsSection(isWide, maturityValue, years)
            
            WealthOpportunityCard(maturityValue, uiState, onUnlockPremium = { viewModel.unlockPremium() })
            
            SipScheduleCard(yearlyDataList, uiState, onUnlockPremium = { viewModel.unlockPremium() })
            Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.xl))"""

replacement = """            InputsSection(
                uiState = uiState, 
                updateInputs = { amount, rate, years, stepUp ->
                    viewModel.updateInputs(amount = amount, rate = rate, years = years, stepUp = stepUp)
                }, 
                isWide = isWide
            )
            
            if (hasValidInput) {
                HeroCard(totalInvested, totalGain, maturityValue, returnRate, years, isWide, uiState)
                
                if (isWide) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.lg)) {
                        GrowthVisualizationCard(yearlyDataList, modifier = Modifier.weight(1.5f))
                        Box(modifier = Modifier.weight(1f)) { InflationAdjustedCard(maturityValue, years, uiState, onUnlockPremium = { viewModel.unlockPremium() }) }
                    }
                } else {
                    GrowthVisualizationCard(yearlyDataList, modifier = Modifier.fillMaxWidth())
                    InflationAdjustedCard(maturityValue, years, uiState, onUnlockPremium = { viewModel.unlockPremium() })
                }
                
                LifestyleFundsSection(isWide, maturityValue, years)
                
                WealthOpportunityCard(maturityValue, uiState, onUnlockPremium = { viewModel.unlockPremium() })
                
                SipScheduleCard(yearlyDataList, uiState, onUnlockPremium = { viewModel.unlockPremium() })
            } else {
                EmptyStateUi()
            }
            Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.xl))"""

if target in content:
    content = content.replace(target, replacement)
    
    empty_state_code = """
@Composable
private fun EmptyStateUi() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = LoanMasterTheme.spacing.xl, horizontal = LoanMasterTheme.spacing.md),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(Color.White.copy(alpha = 0.05f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Analytics,
                contentDescription = null,
                tint = TextSec.copy(alpha = 0.5f),
                modifier = Modifier.size(40.dp)
            )
        }
        Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.lg))
        Text(
            "Enter details to see projection",
            color = Color.White,
            fontSize = LoanMasterTheme.typography.title.fontSize,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.sm))
        Text(
            "Fill in your monthly SIP amount, expected return, and period to calculate your wealth growth.",
            color = TextSec,
            fontSize = LoanMasterTheme.typography.body.fontSize,
            textAlign = TextAlign.Center,
            lineHeight = LoanMasterTheme.typography.title.fontSize
        )
    }
}
"""
    if "private fun EmptyStateUi" not in content:
        content += empty_state_code
    
    with open(file_path, "w") as f:
        f.write(content)
    print("Done")
else:
    print("Target not found")
