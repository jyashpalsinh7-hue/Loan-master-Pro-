with open("app/src/main/java/com/loanmaster/pro/feature/sip/SipViewModel.kt", "r") as f:
    content = f.read()

# Replace the init block from before _uiState to after uiState
bad_block = """class SipViewModel(application: Application) : AndroidViewModel(application) {
    private val premiumManager = com.loanmaster.pro.core.managers.PremiumManager(application.applicationContext)
    
    init {
        viewModelScope.launch {
            premiumManager.isPremium.collect { isPremium ->
                _uiState.update { it.copy(isPremiumUnlocked = isPremium) }
            }
        }
    }
    private val _uiState = MutableStateFlow(SipUiState())
    val uiState: StateFlow<SipUiState> = _uiState.asStateFlow()"""

good_block = """class SipViewModel(application: Application) : AndroidViewModel(application) {
    private val premiumManager = com.loanmaster.pro.core.managers.PremiumManager(application.applicationContext)
    
    private val _uiState = MutableStateFlow(SipUiState())
    val uiState: StateFlow<SipUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            premiumManager.isPremium.collect { isPremium ->
                _uiState.update { it.copy(isPremiumUnlocked = isPremium) }
            }
        }
    }"""

content = content.replace(bad_block, good_block)

with open("app/src/main/java/com/loanmaster/pro/feature/sip/SipViewModel.kt", "w") as f:
    f.write(content)
