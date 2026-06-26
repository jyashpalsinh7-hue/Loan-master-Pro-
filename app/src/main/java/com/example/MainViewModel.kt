package com.example

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _activeBottomNavItem = MutableStateFlow("home")
    val activeBottomNavItem: StateFlow<String> = _activeBottomNavItem.asStateFlow()

    private val _isQuickToolsExpanded = MutableStateFlow(false)
    val isQuickToolsExpanded: StateFlow<Boolean> = _isQuickToolsExpanded.asStateFlow()

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateActiveBottomNavItem(item: String) {
        _activeBottomNavItem.value = item
    }

    fun toggleQuickToolsExpanded() {
        _isQuickToolsExpanded.value = !_isQuickToolsExpanded.value
    }
}
