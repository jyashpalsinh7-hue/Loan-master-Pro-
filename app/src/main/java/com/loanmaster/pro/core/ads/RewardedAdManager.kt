package com.loanmaster.pro.core.ads

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
        RewardedAd.load(
            context,
            TEST_AD_UNIT_ID,
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d(TAG, adError.toString())
                    rewardedAd = null
                }

                override fun onAdLoaded(ad: RewardedAd) {
                    Log.d(TAG, "Ad was loaded.")
                    rewardedAd = ad
                }
            }
        )
    }

    fun showAd(activity: Activity, onRewardEarned: () -> Unit) {
        rewardedAd?.let { ad ->
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdShowedFullScreenContent() {
                    Log.d(TAG, "Ad was shown.")
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    Log.d(TAG, "Ad failed to show.")
                    rewardedAd = null
                    loadAd(activity)
                }

                override fun onAdDismissedFullScreenContent() {
                    Log.d(TAG, "Ad was dismissed.")
                    rewardedAd = null
                    loadAd(activity)
                }
            }

            ad.show(activity) { rewardItem ->
                Log.d(TAG, "The user earned the reward: ${rewardItem.amount} ${rewardItem.type}")
                onRewardEarned()
            }
        } ?: run {
            Log.d(TAG, "The rewarded ad wasn't ready yet.")
            onRewardEarned()
            loadAd(activity)
        }
    }
}
