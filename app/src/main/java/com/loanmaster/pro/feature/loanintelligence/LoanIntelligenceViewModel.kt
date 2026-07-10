package com.loanmaster.pro.feature.loanintelligence

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.loanmaster.pro.core.managers.PremiumManager
import com.loanmaster.pro.core.managers.RewardedAdManager
import com.loanmaster.pro.feature.loanintelligence.engine.LoanIntelligenceEngine
import com.loanmaster.pro.feature.loanintelligence.model.LoanIntelligenceState

class LoanIntelligenceViewModel : ViewModel() {
    private val premiumManager = PremiumManager()
    private val adManager = RewardedAdManager()
    private val engine = LoanIntelligenceEngine()
    
    private val _state = MutableStateFlow(LoanIntelligenceState())
    val state: StateFlow<LoanIntelligenceState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            premiumManager.isPremium.collect { isPremium ->
                _state.update { it.copy(isPremiumUnlocked = isPremium) }
            }
        }
    }

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
    ) {
        val suggestions = engine.generateSuggestions(
            income, existingEmi, loanType, interestRate, tenureYears, creditScoreRange, approvalProb, eligibleAmount, foirLimit
        )
        _state.update { it.copy(suggestions = suggestions) }
    }

    fun unlockTemporary() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val loaded = adManager.loadRewardedAd()
            if (loaded) {
                adManager.showRewardedAd {
                    _state.update { it.copy(isTemporaryUnlocked = true) }
                }
            }
            _state.update { it.copy(isLoading = false) }
        }
    }

    fun unlockPremium() {
        premiumManager.unlockPermanent()
    }
    
    fun resetTemporaryUnlock() {
        _state.update { it.copy(isTemporaryUnlocked = false) }
    }
}
