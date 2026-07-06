package com.loanmaster.pro.feature.history

import com.loanmaster.pro.domain.model.*
import com.loanmaster.pro.feature.gst.*
import com.loanmaster.pro.feature.sip.*
import com.loanmaster.pro.core.ui.*
import com.loanmaster.pro.core.theme.*
import com.loanmaster.pro.data.datastore.*
import com.loanmaster.pro.feature.settings.*
import com.loanmaster.pro.feature.rd.*
import com.loanmaster.pro.domain.calculator.*
import com.loanmaster.pro.data.local.entity.*
import com.loanmaster.pro.core.utils.*
import com.loanmaster.pro.data.local.dao.*
import com.loanmaster.pro.data.local.room.*
import com.loanmaster.pro.feature.emi.*
import com.loanmaster.pro.feature.loansummary.*
import com.loanmaster.pro.feature.prepayment.*
import com.loanmaster.pro.core.formatter.*
import com.loanmaster.pro.feature.fd.*
import com.loanmaster.pro.data.repository.*
import com.loanmaster.pro.feature.currency.*
import com.loanmaster.pro.core.navigation.*
import com.loanmaster.pro.feature.compare.*
import com.loanmaster.pro.feature.loaneligibility.*
import com.loanmaster.pro.core.responsive.*
import com.loanmaster.pro.feature.home.*

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

import kotlinx.coroutines.flow.first


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
