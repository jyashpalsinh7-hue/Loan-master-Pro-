package com.example

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LoanSummaryViewModel(private val repository: ActiveLoanRepository) : ViewModel() {
    val activeLoans: StateFlow<List<ActiveLoan>> = repository.getAllActiveLoans()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addLoan(loan: ActiveLoan) {
        viewModelScope.launch {
            repository.insertActiveLoan(loan)
        }
    }

    fun deleteLoan(loan: ActiveLoan) {
        viewModelScope.launch {
            repository.deleteActiveLoan(loan)
        }
    }
}

class LoanSummaryViewModelFactory(private val repository: ActiveLoanRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoanSummaryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoanSummaryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
