package com.loanmaster.pro.core.managers

import kotlinx.coroutines.delay

class RewardedAdManager {
    suspend fun loadRewardedAd(): Boolean {
        // Placeholder for loading ad
        delay(1000)
        return true
    }

    suspend fun showRewardedAd(onRewardEarned: () -> Unit) {
        // Placeholder for showing ad
        delay(2000) // Simulate ad watching
        onRewardEarned()
    }
}
