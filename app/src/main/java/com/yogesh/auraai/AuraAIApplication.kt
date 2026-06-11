package com.yogesh.auraai

import android.app.Application
import com.yogesh.auraai.core.di.AppContainer

class AuraAIApplication : Application() {

    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer(this)
    }
}
