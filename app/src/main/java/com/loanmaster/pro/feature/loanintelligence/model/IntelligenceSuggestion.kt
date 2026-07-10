package com.loanmaster.pro.feature.loanintelligence.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class IntelligenceSuggestion(
    val title: String,
    val description: String,
    val estimatedImpact: String, // will map to Estimated Loan Increase or combined
    val priority: Int, // Lower is higher priority
    val icon: ImageVector,
    val color: Color,
    val difficulty: String = "Medium",
    val confidence: String = "High",
    val estimatedApprovalIncrease: String = "+15%",
    val estimatedLoanIncrease: String = "+₹2.5L"
)
