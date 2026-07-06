import re

path = "app/src/main/java/com/loanmaster/pro/feature/home/MainViewModel.kt"
with open(path, "r") as f:
    content = f.read()

if "data class HomeUiState" not in content:
    ui_state = """data class HomeUiState(
    val searchQuery: String = "",
    val activeBottomNavItem: String = "home",
    val isQuickToolsExpanded: Boolean = false,
    val selectedHistory: CalculationHistory? = null
)

"""
    new_impl = """class MainViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun setSelectedHistory(history: CalculationHistory?) {
        _uiState.value = _uiState.value.copy(selectedHistory = history)
    }

    fun clearSelectedHistory() {
        _uiState.value = _uiState.value.copy(selectedHistory = null)
    }

    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun updateActiveBottomNavItem(item: String) {
        _uiState.value = _uiState.value.copy(activeBottomNavItem = item)
    }

    fun toggleQuickToolsExpanded() {
        _uiState.value = _uiState.value.copy(isQuickToolsExpanded = !_uiState.value.isQuickToolsExpanded)
    }
}"""

    content = re.sub(r'class MainViewModel[\s\S]*?\}', ui_state + new_impl, content)
    
    with open(path, "w") as f:
        f.write(content)

