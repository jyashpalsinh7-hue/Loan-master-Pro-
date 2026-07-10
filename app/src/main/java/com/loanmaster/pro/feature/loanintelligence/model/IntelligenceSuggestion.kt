package com.loanmaster.pro.feature.loanintelligence.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class IntelligenceSuggestion(
    val title: String,
    val description: String,
    val estimatedImpact: String,
    val priority: Int, // Lower is higher priority
    val icon: ImageVector,
    val color: Color
)
