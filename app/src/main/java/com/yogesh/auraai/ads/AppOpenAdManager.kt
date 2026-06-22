package com.yogesh.auraai.core.ads

import android.app.Activity
import android.app.Application
import android.util.Log
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.yogesh.auraai.AuraAIApplication

class AppOpenAdManager(
    private val application: Application
) {

    private var appOpenAd: AppOpenAd? = null
    private var isLoadingAd = false
    private var isShowingAd = false
    private var firstLaunchPending = true

    init {
        loadAd()
    }

    fun loadAd() {

        if (isLoadingAd || appOpenAd != null) return

        isLoadingAd = true

        val request = AdRequest.Builder().build()

        AppOpenAd.load(
            application,
            "ca-app-pub-3940256099942544/9257395921", // Test App Open ID
            request,
            object : AppOpenAd.AppOpenAdLoadCallback() {

                override fun onAdLoaded(ad: AppOpenAd) {

                    Log.d("AppOpenAd", "Loaded")

                    appOpenAd = ad
                    isLoadingAd = false

                    // Show once on first launch
                    if (firstLaunchPending && !isShowingAd) {

                        val activity =
                            (application as AuraAIApplication)
                                .getCurrentActivity()

                        if (activity != null) {

                            firstLaunchPending = false

                            showAdIfAvailable(activity)
                        }
                    }
                }

                override fun onAdFailedToLoad(error: LoadAdError) {

                    Log.d(
                        "AppOpenAd",
                        "Failed ${error.message}"
                    )

                    isLoadingAd = false
                }
            }
        )
    }

    fun showAdIfAvailable(activity: Activity) {

        if (isShowingAd) return

        if (appOpenAd == null) {
            loadAd()
            return
        }

        appOpenAd?.fullScreenContentCallback =
            object : FullScreenContentCallback() {

                override fun onAdShowedFullScreenContent() {

                    isShowingAd = true
                    appOpenAd = null
                }

                override fun onAdDismissedFullScreenContent() {

                    isShowingAd = false
                    appOpenAd = null

                    loadAd()
                }
            }

        appOpenAd?.show(activity)
    }

    fun isShowingAd(): Boolean = isShowingAd
}