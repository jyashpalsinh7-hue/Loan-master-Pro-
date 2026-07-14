import sys

file_path = "app/src/main/java/com/loanmaster/pro/feature/loanintelligence/LoanIntelligenceViewModel.kt"
with open(file_path, "r") as f:
    content = f.read()

target = """import com.loanmaster.pro.core.managers.PremiumManager
import com.loanmaster.pro.core.managers.RewardedAdManager
import com.loanmaster.pro.feature.loanintelligence.engine.LoanIntelligenceEngine
import com.loanmaster.pro.feature.loanintelligence.model.LoanIntelligenceState

class LoanIntelligenceViewModel : ViewModel() {
    private val premiumManager = PremiumManager()
    private val adManager = RewardedAdManager()
    private val engine = LoanIntelligenceEngine()"""

replacement = """import com.loanmaster.pro.core.managers.PremiumManager
import com.loanmaster.pro.feature.loanintelligence.engine.LoanIntelligenceEngine
import com.loanmaster.pro.feature.loanintelligence.model.LoanIntelligenceState

class LoanIntelligenceViewModel : ViewModel() {
    private val premiumManager = PremiumManager()
    private val engine = LoanIntelligenceEngine()"""

target2 = """    fun unlockTemporary() {
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
    }"""

replacement2 = """    // FIX: Removed fake RewardedAdManager and provide direct unlock method to be called from UI
    fun onTemporaryUnlockEarned() {
        _state.update { it.copy(isTemporaryUnlocked = true) }
    }"""

if target in content and target2 in content:
    content = content.replace(target, replacement).replace(target2, replacement2)
    with open(file_path, "w") as f:
        f.write(content)
    print("Patched VM")
else:
    print("Not found")
