package com.loanmaster.pro.feature.loanintelligence.engine

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material.icons.automirrored.rounded.*
import androidx.compose.ui.graphics.Color
import com.loanmaster.pro.feature.loanintelligence.model.IntelligenceSuggestion
import com.loanmaster.pro.core.theme.AccentGreen
import com.loanmaster.pro.core.theme.AccentBlue

class LoanIntelligenceEngine {
    fun generateSuggestions(
        income: Double,
        existingEmi: Double,
        loanType: String,
        interestRate: Double,
        tenureYears: Int,
        creditScoreRange: String,
        approvalProb: Float,
        eligibleAmount: Double,
        foirLimit: Double
    ): List<IntelligenceSuggestion> {
        val suggestions = mutableListOf<IntelligenceSuggestion>()
        
        val currentFoir = if (income > 0) (existingEmi / income) else 0.0
        
        if (approvalProb < 0.8f && currentFoir > (foirLimit - 0.1)) {
            suggestions.add(
                IntelligenceSuggestion(
                    title = "Reduce Existing EMI",
                    description = "Consolidate or close existing loans to lower your fixed obligations.",
                    estimatedImpact = "Approval Prob +25%",
                    priority = 1,
                    icon = Icons.AutoMirrored.Rounded.TrendingDown,
                    color = AccentGreen
                )
            )
        }
        
        if (approvalProb < 0.7f) {
            suggestions.add(
                IntelligenceSuggestion(
                    title = "Add a Co-Borrower",
                    description = "Adding an earning family member can significantly boost your eligibility.",
                    estimatedImpact = "Higher Loan Amount",
                    priority = 2,
                    icon = Icons.Rounded.GroupAdd,
                    color = AccentBlue
                )
            )
        }
        
        if (creditScoreRange.contains("Fair") || creditScoreRange.contains("Poor")) {
            suggestions.add(
                IntelligenceSuggestion(
                    title = "Improve Credit Score",
                    description = "A score above 750 can reduce your interest rate substantially.",
                    estimatedImpact = "Lower Interest Rate",
                    priority = 3,
                    icon = Icons.Rounded.Speed,
                    color = Color(0xFFFBBF24)
                )
            )
        }
        
        if (tenureYears < 15 && loanType == "Home Loan") {
            suggestions.add(
                IntelligenceSuggestion(
                    title = "Increase Tenure",
                    description = "Stretching the tenure to 20 years will lower your EMI burden.",
                    estimatedImpact = "Lower EMI & Better Approval",
                    priority = 4,
                    icon = Icons.Rounded.Update,
                    color = Color(0xFF9C27B0)
                )
            )
        }
        
        return suggestions.sortedBy { it.priority }
    }
}
