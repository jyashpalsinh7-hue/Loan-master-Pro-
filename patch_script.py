import sys

filename = '/app/applet/app/src/main/java/com/loanmaster/pro/feature/sip/SipScreen.kt'
with open(filename, 'r') as f:
    content = f.read()

target = """                if (isWide) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.lg)) {
                        GrowthVisualizationCard(yearlyDataList, modifier = Modifier.weight(1.5f))
                        Box(modifier = Modifier.weight(1f)) { InflationAdjustedCard(maturityValue, years, uiState, onUnlockPremium = { showPremiumDialog = true }) }
                    }
                } else {
                    GrowthVisualizationCard(yearlyDataList, modifier = Modifier.fillMaxWidth())
                    InflationAdjustedCard(maturityValue, years, uiState, onUnlockPremium = { showPremiumDialog = true })
                }
                
                LifestyleFundsSection(isWide, maturityValue, years)
                
                WealthOpportunityCard(maturityValue, uiState, onUnlockPremium = { showPremiumDialog = true })
                
                SipScheduleCard(yearlyDataList, uiState, onUnlockPremium = { showPremiumDialog = true })"""

replacement = """                val requestPremiumUnlock: () -> Unit = {
                    if (!uiState.isPremiumUnlocked) {
                        showPremiumDialog = true
                    }
                }

                if (isWide) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.lg)) {
                        GrowthVisualizationCard(yearlyDataList, modifier = Modifier.weight(1.5f))
                        Box(modifier = Modifier.weight(1f)) { InflationAdjustedCard(maturityValue, years, uiState, onUnlockPremium = requestPremiumUnlock) }
                    }
                } else {
                    GrowthVisualizationCard(yearlyDataList, modifier = Modifier.fillMaxWidth())
                    InflationAdjustedCard(maturityValue, years, uiState, onUnlockPremium = requestPremiumUnlock)
                }
                
                LifestyleFundsSection(isWide, maturityValue, years)
                
                WealthOpportunityCard(maturityValue, uiState, onUnlockPremium = requestPremiumUnlock)
                
                SipScheduleCard(yearlyDataList, uiState, onUnlockPremium = requestPremiumUnlock)"""

if target in content:
    with open(filename, 'w') as f:
        f.write(content.replace(target, replacement))
    print("Success")
else:
    print("Target not found. Let's try replacing line by line.")
