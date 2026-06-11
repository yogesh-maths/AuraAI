package com.yogesh.auraai.domain.repository

import com.yogesh.auraai.core.common.Result
import com.yogesh.auraai.domain.model.UserSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun observeSettings(): Flow<UserSettings>
    suspend fun updateApiKey(key: String)
    suspend fun updateModel(model: String)
    suspend fun updateSystemPrompt(prompt: String)
    suspend fun updateDarkTheme(isDark: Boolean?)
    suspend fun setOnboardingComplete()
    suspend fun clearAllData()
    suspend fun testConnection(): Result<Unit>
}
