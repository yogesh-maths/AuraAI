package com.yogesh.auraai

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.gms.ads.MobileAds
import com.yogesh.auraai.core.ads.AppOpenAdManager
import com.yogesh.auraai.core.di.AppContainer

class AuraAIApplication : Application(),
    Application.ActivityLifecycleCallbacks,
    DefaultLifecycleObserver {

    lateinit var appContainer: AppContainer
        private set

    lateinit var appOpenAdManager: AppOpenAdManager
        private set

    private var currentActivity: Activity? = null

    override fun onCreate() {
        super<Application>.onCreate()

        appContainer = AppContainer(this)

        MobileAds.initialize(this)

        appOpenAdManager = AppOpenAdManager(this)

        registerActivityLifecycleCallbacks(this)

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    fun getCurrentActivity(): Activity? = currentActivity

   override fun onStart(owner: LifecycleOwner) {
    super<DefaultLifecycleObserver>.onStart(owner)

    currentActivity?.let {
        appOpenAdManager.showAdIfAvailable(it)
    }
}

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

    override fun onActivityStarted(activity: Activity) {
        if (!appOpenAdManager.isShowingAd()) {
            currentActivity = activity
        }
    }

    override fun onActivityResumed(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityPaused(activity: Activity) {}

    override fun onActivityStopped(activity: Activity) {}

    override fun onActivitySaveInstanceState(
        activity: Activity,
        outState: Bundle
    ) {
    }

    override fun onActivityDestroyed(activity: Activity) {}
}