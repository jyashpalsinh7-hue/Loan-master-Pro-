import sys

file_path = "app/src/main/java/com/loanmaster/pro/core/ads/RewardedAdManager.kt"
content = """package com.loanmaster.pro.core.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

object RewardedAdManager {
    private var rewardedAd: RewardedAd? = null
    private const val TAG = "RewardedAdManager"
    private const val TEST_AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917" // Test ID

    fun loadAd(context: Context) {
        val adRequest = AdRequest.Builder().build()
        Log.d(TAG, "Reward requested")
        RewardedAd.load(
            context,
            TEST_AD_UNIT_ID,
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    // FIX: Log reward failed with full error
                    Log.d(TAG, "Reward failed: " + adError.toString())
                    rewardedAd = null
                }

                override fun onAdLoaded(ad: RewardedAd) {
                    // FIX: Log reward loaded
                    Log.d(TAG, "Reward loaded")
                    rewardedAd = ad
                }
            }
        )
    }

    fun showAd(activity: Activity, onRewardEarned: () -> Unit) {
        rewardedAd?.let { ad ->
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdShowedFullScreenContent() {
                    // FIX: Log reward shown
                    Log.d(TAG, "Reward shown")
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    Log.d(TAG, "Reward failed to show: " + adError.message)
                    rewardedAd = null
                    loadAd(activity)
                }

                override fun onAdDismissedFullScreenContent() {
                    Log.d(TAG, "Reward dismissed")
                    rewardedAd = null
                    loadAd(activity)
                }
            }
            ad.show(activity) { rewardItem ->
                // FIX: Log reward earned
                Log.d(TAG, "Reward earned: ${rewardItem.amount} ${rewardItem.type}")
                onRewardEarned()
            }
        } ?: run {
            Log.d(TAG, "The rewarded ad wasn't ready yet.")
            android.widget.Toast.makeText(activity, "Ad is still loading. Please try again in a few seconds.", android.widget.Toast.LENGTH_SHORT).show()
            loadAd(activity)
        }
    }
}
"""

with open(file_path, "w") as f:
    f.write(content)
print("Updated RewardedAdManager")
