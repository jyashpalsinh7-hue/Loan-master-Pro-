package com.loanmaster.pro.feature.loanintelligence

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.loanmaster.pro.core.managers.PremiumManager
import com.loanmaster.pro.feature.loanintelligence.engine.LoanIntelligenceEngine
import com.loanmaster.pro.feature.loanintelligence.model.LoanIntelligenceState

class LoanIntelligenceViewModel(application: Application) : AndroidViewModel(application) {
    private val premiumManager = PremiumManager(application.applicationContext)
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

    // FIX: Removed fake RewardedAdManager and provide direct unlock method to be called from UI
    fun onTemporaryUnlockEarned() {
        _state.update { it.copy(isTemporaryUnlocked = true) }
    }

    fun unlockPremium() {
        premiumManager.unlockPermanent()
    }
    
    fun resetTemporaryUnlock() {
        _state.update { it.copy(isTemporaryUnlocked = false) }
    }
}
