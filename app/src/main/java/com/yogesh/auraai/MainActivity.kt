package com.yogesh.auraai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yogesh.auraai.domain.model.UserSettings
import com.yogesh.auraai.presentation.navigation.AuraNavGraph
import com.yogesh.auraai.ui.theme.AuraAITheme
import android.util.Log
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.yogesh.auraai.data.remote.GeminiService
import com.yogesh.auraai.BuildConfig
class MainActivity : ComponentActivity() {

    private val appContainer by lazy {
        (application as AuraAIApplication).appContainer
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        setContent {

            val settings by appContainer.settingsRepository
                .observeSettings()
                .collectAsStateWithLifecycle(
                    initialValue = UserSettings()
                )

            AuraAITheme(
                darkTheme = true
            ) {

                AuraNavGraph(
                    appContainer = appContainer
                )

            }
        }
    }
    }

