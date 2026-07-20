import re

# 1. RdScreen.kt
with open("app/src/main/java/com/loanmaster/pro/feature/rd/RdScreen.kt", "r") as f:
    content = f.read()

content = content.replace(
    'var isPremiumUnlocked by rememberSaveable { mutableStateOf(false) }',
    '''val premiumManagerContext = androidx.compose.ui.platform.LocalContext.current
    val premiumManager = remember { com.loanmaster.pro.core.managers.PremiumManager(premiumManagerContext.applicationContext) }
    val isPremiumUnlocked by premiumManager.isPremium.collectAsStateWithLifecycle()'''
)

content = content.replace(
    'isPremiumUnlocked = true\n                com.loanmaster.pro.core.managers.PremiumManager(dialogContext).unlockPermanent()',
    'premiumManager.unlockPermanent()'
)

with open("app/src/main/java/com/loanmaster/pro/feature/rd/RdScreen.kt", "w") as f:
    f.write(content)


# 2. CompareScreen.kt
with open("app/src/main/java/com/loanmaster/pro/feature/compare/CompareScreen.kt", "r") as f:
    content = f.read()

content = content.replace(
    'var isPremiumUnlocked by remember { mutableStateOf(false) }',
    '''val premiumManagerContext = androidx.compose.ui.platform.LocalContext.current
    val premiumManager = remember { com.loanmaster.pro.core.managers.PremiumManager(premiumManagerContext.applicationContext) }
    val isPremiumUnlocked by premiumManager.isPremium.collectAsStateWithLifecycle()'''
)

content = content.replace(
    'isPremiumUnlocked = true \n                com.loanmaster.pro.core.managers.PremiumManager(dialogContext).unlockPermanent()',
    'premiumManager.unlockPermanent()'
)

with open("app/src/main/java/com/loanmaster/pro/feature/compare/CompareScreen.kt", "w") as f:
    f.write(content)


# 3. PrepaymentScreen.kt
with open("app/src/main/java/com/loanmaster/pro/feature/prepayment/PrepaymentScreen.kt", "r") as f:
    content = f.read()

content = content.replace(
    'var isAiUnlocked by rememberSaveable { mutableStateOf(false) }',
    '''val premiumManagerContext = androidx.compose.ui.platform.LocalContext.current
    val premiumManager = remember { com.loanmaster.pro.core.managers.PremiumManager(premiumManagerContext.applicationContext) }
    val isAiUnlocked by premiumManager.isPremium.collectAsStateWithLifecycle()'''
)

content = content.replace(
    'isAiUnlocked = true \n                com.loanmaster.pro.core.managers.PremiumManager(dialogContext).unlockPermanent()',
    'premiumManager.unlockPermanent()'
)

# And in the alert dialog for "Watch Ad or Upgrade" where isAiUnlocked = true is done manually
content = content.replace(
    'isAiUnlocked = true \n                        showUnlockDialog = false',
    'premiumManager.unlockPermanent()\n                        showUnlockDialog = false'
)

with open("app/src/main/java/com/loanmaster/pro/feature/prepayment/PrepaymentScreen.kt", "w") as f:
    f.write(content)


# 4. SipViewModel.kt
with open("app/src/main/java/com/loanmaster/pro/feature/sip/SipViewModel.kt", "r") as f:
    content = f.read()

content = content.replace(
    'import androidx.lifecycle.ViewModel',
    'import android.app.Application\nimport androidx.lifecycle.AndroidViewModel'
)

content = content.replace(
    'class SipViewModel : ViewModel() {',
    '''class SipViewModel(application: Application) : AndroidViewModel(application) {
    private val premiumManager = com.loanmaster.pro.core.managers.PremiumManager(application.applicationContext)
    
    init {
        viewModelScope.launch {
            premiumManager.isPremium.collect { isPremium ->
                _uiState.update { it.copy(isPremiumUnlocked = isPremium) }
            }
        }
    }'''
)

content = content.replace(
    '_uiState.update { it.copy(isPremiumUnlocked = true) }',
    'premiumManager.unlockPermanent()'
)

with open("app/src/main/java/com/loanmaster/pro/feature/sip/SipViewModel.kt", "w") as f:
    f.write(content)


