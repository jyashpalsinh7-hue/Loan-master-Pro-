package com.loanmaster.pro.core.managers

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.loanmaster.pro.data.datastore.dataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PremiumManager(private val context: Context) {

    companion object {
        val IS_PREMIUM_UNLOCKED = booleanPreferencesKey("is_premium_unlocked")
    }

    private val _isPremium = MutableStateFlow(false)
    val isPremium: StateFlow<Boolean> = _isPremium.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        scope.launch {
            context.dataStore.data.collect { preferences ->
                val persisted = preferences[IS_PREMIUM_UNLOCKED] ?: false
                _isPremium.value = persisted
            }
        }
    }

    fun unlockPermanent() {
        _isPremium.value = true
        scope.launch {
            context.dataStore.edit { preferences ->
                preferences[IS_PREMIUM_UNLOCKED] = true
            }
        }
    }
}
