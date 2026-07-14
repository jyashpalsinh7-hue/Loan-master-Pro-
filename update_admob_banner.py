import sys

file_path = "app/src/main/java/com/loanmaster/pro/core/ui/AdMobBanner.kt"
content = """package com.loanmaster.pro.core.ui

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

@Composable
fun AdMobBanner(modifier: Modifier = Modifier) {
    var isAdFailedToLoad by remember { mutableStateOf(false) }
    val context = LocalContext.current
    
    // Determine the adaptive ad size
    val adSize = remember(context) {
        val displayMetrics = context.resources.displayMetrics
        val adWidthPixels = displayMetrics.widthPixels.toFloat()
        val density = displayMetrics.density
        val adWidth = (adWidthPixels / density).toInt()
        AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidth)
    }

    if (!isAdFailedToLoad) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            AndroidView(
                modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                factory = { ctx ->
                    AdView(ctx).apply {
                        setAdSize(adSize)
                        // Test Banner Ad Unit ID
                        adUnitId = "ca-app-pub-3940256099942544/6300978111"
                        
                        adListener = object : AdListener() {
                            override fun onAdLoaded() {
                                Log.d("AdMobBanner", "Banner loaded")
                            }
                            override fun onAdOpened() {
                                Log.d("AdMobBanner", "Banner opened")
                            }
                            override fun onAdClicked() {
                                Log.d("AdMobBanner", "Banner clicked")
                            }
                            override fun onAdImpression() {
                                Log.d("AdMobBanner", "Banner impression")
                            }
                            override fun onAdClosed() {
                                Log.d("AdMobBanner", "Banner closed")
                            }
                            override fun onAdFailedToLoad(error: LoadAdError) {
                                Log.e("AdMobBanner", "Banner failed: ${error.message}")
                                isAdFailedToLoad = true
                            }
                        }
                        
                        loadAd(AdRequest.Builder().build())
                    }
                }
            )
        }
    }
}
"""

with open(file_path, "w") as f:
    f.write(content)
print("Updated AdMobBanner")
