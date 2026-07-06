import os
import re

vm_path = "app/src/main/java/com/loanmaster/pro/feature/history/HistoryViewModel.kt"
with open(vm_path, "r") as f:
    vm_content = f.read()

new_vm = """data class HistoryUiState(
    val historyList: List<CalculationHistory> = emptyList()
)

class HistoryViewModel(private val repository: HistoryRepository, private val settingsRepository: SettingsRepository) : ViewModel() {
    val uiState: StateFlow<HistoryUiState> = repository.getAllHistory()
        .map { HistoryUiState(historyList = it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HistoryUiState()
        )

    fun insert(history: CalculationHistory, onInserted: (Int) -> Unit = {}) {
        viewModelScope.launch {
            val keepHistory = settingsRepository.keepHistoryEnabled.first()
            if (keepHistory) {
                val id = repository.saveHistory(history).toInt()
                onInserted(id)
            }
        }
    }

    fun deleteById(id: Int) {
        viewModelScope.launch {
            repository.deleteById(id)
        }
    }

    fun clearAll() {
        viewModelScope.launch {
            repository.deleteAllHistory()
        }
    }
}"""

vm_content = re.sub(r'class HistoryViewModel\(private val repository: HistoryRepository, private val settingsRepository: SettingsRepository\) : ViewModel\(\) \{.*\}', new_vm, vm_content, flags=re.DOTALL)
vm_content = vm_content.replace("import kotlinx.coroutines.flow.stateIn", "import kotlinx.coroutines.flow.stateIn\nimport kotlinx.coroutines.flow.map")

with open(vm_path, "w") as f:
    f.write(vm_content)

# Update HistoryScreen.kt
screen_path = "app/src/main/java/com/loanmaster/pro/feature/history/HistoryScreen.kt"
with open(screen_path, "r") as f:
    screen_content = f.read()

screen_content = screen_content.replace("val historyList by viewModel.uiState.collectAsStateWithLifecycle()", "val uiState by viewModel.uiState.collectAsStateWithLifecycle()\n    val historyList = uiState.historyList")
screen_content = screen_content.replace("import androidx.lifecycle.compose.collectAsStateWithLifecycle", "import androidx.lifecycle.compose.collectAsStateWithLifecycle\nimport androidx.compose.runtime.getValue")

with open(screen_path, "w") as f:
    f.write(screen_content)

