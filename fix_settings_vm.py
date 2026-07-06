import os
import re

vm_path = "app/src/main/java/com/loanmaster/pro/feature/settings/SettingsViewModel.kt"
with open(vm_path, "r") as f:
    vm_content = f.read()

new_vm = """data class SettingsUiState(
    val language: String = "English",
    val currency: String = "INR (₹)",
    val notificationsEnabled: Boolean = true,
    val keepHistoryEnabled: Boolean = true,
    val remindersEnabled: Boolean = false,
    val emiDueDay: Int = 5,
    val emiReminderTimeHour: Int = 10,
    val emiReminderTimeMinute: Int = 0,
    val emiReminderDays: Set<String> = setOf("3")
)

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = SettingsRepository(application)
    
    val uiState: StateFlow<SettingsUiState> = combine(
        repository.language,
        repository.currency,
        repository.notificationsEnabled,
        repository.keepHistoryEnabled,
        repository.remindersEnabled,
        repository.emiDueDay,
        repository.emiReminderTimeHour,
        repository.emiReminderTimeMinute,
        repository.emiReminderDays
    ) { lang, curr, notif, hist, rem, day, hour, min, rDays ->
        SettingsUiState(lang, curr, notif, hist, rem, day, hour, min, rDays)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState()
    )

    fun setLanguage(lang: String) { viewModelScope.launch { repository.setLanguage(lang) } }
    fun setCurrency(curr: String) { viewModelScope.launch { repository.setCurrency(curr) } }
    fun setNotificationsEnabled(enabled: Boolean) { viewModelScope.launch { repository.setNotificationsEnabled(enabled) } }
    fun setKeepHistoryEnabled(enabled: Boolean) { viewModelScope.launch { repository.setKeepHistoryEnabled(enabled) } }
    fun setRemindersEnabled(enabled: Boolean) { viewModelScope.launch { repository.setRemindersEnabled(enabled) } }
    fun setEmiDueDay(day: Int) { viewModelScope.launch { repository.setEmiDueDay(day) } }
    fun setEmiReminderTime(hour: Int, minute: Int) { viewModelScope.launch { repository.setEmiReminderTime(hour, minute) } }
    fun setEmiReminderDays(days: Set<String>) { viewModelScope.launch { repository.setEmiReminderDays(days) } }
}"""

vm_content = re.sub(r'class SettingsViewModel\(application: Application\) : AndroidViewModel\(application\) \{.*\}', new_vm, vm_content, flags=re.DOTALL)
vm_content = vm_content.replace("import kotlinx.coroutines.flow.stateIn", "import kotlinx.coroutines.flow.stateIn\nimport kotlinx.coroutines.flow.combine")

with open(vm_path, "w") as f:
    f.write(vm_content)

# Update SettingsScreen.kt
screen_path = "app/src/main/java/com/loanmaster/pro/feature/settings/SettingsScreen.kt"
with open(screen_path, "r") as f:
    screen_content = f.read()

screen_content = screen_content.replace("val language by viewModel.language.collectAsStateWithLifecycle()", "val uiState by viewModel.uiState.collectAsStateWithLifecycle()\n    val language = uiState.language")
screen_content = screen_content.replace("val currency by viewModel.currency.collectAsStateWithLifecycle()", "val currency = uiState.currency")
screen_content = screen_content.replace("val notificationsEnabled by viewModel.notificationsEnabled.collectAsStateWithLifecycle()", "val notificationsEnabled = uiState.notificationsEnabled")
screen_content = screen_content.replace("val keepHistoryEnabled by viewModel.keepHistoryEnabled.collectAsStateWithLifecycle()", "val keepHistoryEnabled = uiState.keepHistoryEnabled")
screen_content = screen_content.replace("val remindersEnabled by viewModel.remindersEnabled.collectAsStateWithLifecycle()", "val remindersEnabled = uiState.remindersEnabled")
screen_content = screen_content.replace("val emiDueDay by viewModel.emiDueDay.collectAsStateWithLifecycle()", "val emiDueDay = uiState.emiDueDay")
screen_content = screen_content.replace("val emiReminderTimeHour by viewModel.emiReminderTimeHour.collectAsStateWithLifecycle()", "val emiReminderTimeHour = uiState.emiReminderTimeHour")
screen_content = screen_content.replace("val emiReminderTimeMinute by viewModel.emiReminderTimeMinute.collectAsStateWithLifecycle()", "val emiReminderTimeMinute = uiState.emiReminderTimeMinute")
screen_content = screen_content.replace("val emiReminderDays by viewModel.emiReminderDays.collectAsStateWithLifecycle()", "val emiReminderDays = uiState.emiReminderDays")

with open(screen_path, "w") as f:
    f.write(screen_content)

