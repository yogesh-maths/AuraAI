package com.yogesh.auraai.core.ads

import android.app.Activity
import android.app.Application
import android.util.Log
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd

class AppOpenAdManager(
    private val application: Application
) {

    private var appOpenAd: AppOpenAd? = null
    private var isLoadingAd = false
    private var isShowingAd = false

    init {
        loadAd()
    }

    fun loadAd() {

        if (isLoadingAd || appOpenAd != null) return

        isLoadingAd = true

        val request = AdRequest.Builder().build()

        AppOpenAd.load(
            application,
            "ca-app-pub-3940256099942544/9257395921",
            request,
            object : AppOpenAd.AppOpenAdLoadCallback() {

                override fun onAdLoaded(ad: AppOpenAd) {

                    Log.d("AppOpenAd", "Loaded")

                    appOpenAd = ad
                    isLoadingAd = false
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

        if (isShowingAd || isLoadingAd) return

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