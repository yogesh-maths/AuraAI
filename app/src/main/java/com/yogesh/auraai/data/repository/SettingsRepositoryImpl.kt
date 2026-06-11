package com.yogesh.auraai.data.repository

import com.yogesh.auraai.core.common.Result
import com.yogesh.auraai.data.local.AuraDatabase
import com.yogesh.auraai.data.local.preferences.UserPreferencesDataStore
import com.yogesh.auraai.data.remote.AiApiService
import com.yogesh.auraai.data.remote.dto.ChatCompletionRequest
import com.yogesh.auraai.data.remote.dto.ChatMessageDto
import com.yogesh.auraai.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.first

class SettingsRepositoryImpl(
    private val dataStore: UserPreferencesDataStore,
    private val database: AuraDatabase,
    private val aiApiService: AiApiService,
) : SettingsRepository {

    override fun observeSettings() = dataStore.settings

    override suspend fun updateApiKey(key: String) {
        dataStore.updateApiKey(key.trim())
    }

    override suspend fun updateModel(model: String) {
        dataStore.updateModel(model.trim())
    }

    override suspend fun updateSystemPrompt(prompt: String) {
        dataStore.updateSystemPrompt(prompt.trim())
    }

    override suspend fun updateDarkTheme(isDark: Boolean?) {
        dataStore.updateDarkTheme(isDark)
    }

    override suspend fun setOnboardingComplete() {
        dataStore.setOnboardingComplete()
    }

    override suspend fun clearAllData() {
        database.clearAllTables()
        dataStore.clearAll()
    }

    override suspend fun testConnection(): Result<Unit> {
        return try {
            val settings = dataStore.settings.first()
            if (settings.apiKey.isBlank()) {
                return Result.Error("API key is not configured")
            }
            aiApiService.listModels()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Connection failed", e)
        }
    }
}
