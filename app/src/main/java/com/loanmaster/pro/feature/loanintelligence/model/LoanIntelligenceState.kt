package com.loanmaster.pro.feature.loanintelligence.model

data class LoanIntelligenceState(
    val isPremiumUnlocked: Boolean = false,
    val isTemporaryUnlocked: Boolean = false,
    val isLoading: Boolean = false,
    val suggestions: List<IntelligenceSuggestion> = emptyList()
) {
    val isUnlocked: Boolean
        get() = isPremiumUnlocked || isTemporaryUnlocked
}
