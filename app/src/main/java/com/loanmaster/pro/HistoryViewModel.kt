package com.loanmaster.pro

import com.loanmaster.pro.ui.theme.*

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

import kotlinx.coroutines.flow.first

class HistoryViewModel(private val repository: HistoryRepository, private val settingsRepository: SettingsRepository) : ViewModel() {
    val uiState: StateFlow<List<CalculationHistory>> = repository.getAllHistory()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
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
}

class HistoryViewModelFactory(private val repository: HistoryRepository, private val settingsRepository: SettingsRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HistoryViewModel(repository, settingsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
