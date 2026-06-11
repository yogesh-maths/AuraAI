package com.yogesh.auraai.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.yogesh.auraai.domain.model.UserSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferencesDataStore(private val context: Context) {

    private object Keys {
        val API_KEY = stringPreferencesKey("api_key")
        val MODEL_NAME = stringPreferencesKey("model_name")
        val SYSTEM_PROMPT = stringPreferencesKey("system_prompt")
        val ONBOARDING_COMPLETE = booleanPreferencesKey("onboarding_complete")
        val DARK_THEME = stringPreferencesKey("dark_theme")
    }

    val settings: Flow<UserSettings> = context.dataStore.data.map { prefs ->
        UserSettings(
            apiKey = prefs[Keys.API_KEY] ?: "",
            modelName = prefs[Keys.MODEL_NAME] ?: UserSettings.DEFAULT_MODEL,
            systemPrompt = prefs[Keys.SYSTEM_PROMPT] ?: UserSettings.DEFAULT_SYSTEM_PROMPT,
            isDarkTheme = when (prefs[Keys.DARK_THEME]) {
                null, "null" -> null
                "true" -> true
                "false" -> false
                else -> null
            },
            isOnboardingComplete = prefs[Keys.ONBOARDING_COMPLETE] ?: false,
        )
    }

    suspend fun updateApiKey(key: String) {
        context.dataStore.edit { it[Keys.API_KEY] = key }
    }

    suspend fun updateModel(model: String) {
        context.dataStore.edit { it[Keys.MODEL_NAME] = model }
    }

    suspend fun updateSystemPrompt(prompt: String) {
        context.dataStore.edit { it[Keys.SYSTEM_PROMPT] = prompt }
    }

    suspend fun updateDarkTheme(isDark: Boolean?) {
        context.dataStore.edit {
            it[Keys.DARK_THEME] = when (isDark) {
                null -> "null"
                true -> "true"
                false -> "false"
            }
        }
    }

    suspend fun setOnboardingComplete() {
        context.dataStore.edit { it[Keys.ONBOARDING_COMPLETE] = true }
    }

    suspend fun clearAll() {
        context.dataStore.edit { it.clear() }
    }

}
