package com.yogesh.auraai.presentation.settings

import com.yogesh.auraai.domain.model.UserSettings

data class SettingsUiState(
    val settings: UserSettings = UserSettings(),
    val isSaving: Boolean = false,
    val showApiKey: Boolean = false,
    val isTestingConnection: Boolean = false,
    val connectionTestResult: String? = null,
)

sealed class SettingsUiEvent {
    data object SettingsSaved : SettingsUiEvent()
    data class ShowError(val message: String) : SettingsUiEvent()
    data class ShowSnackbar(val message: String) : SettingsUiEvent()
    data object NavigateToOnboarding : SettingsUiEvent()
}
