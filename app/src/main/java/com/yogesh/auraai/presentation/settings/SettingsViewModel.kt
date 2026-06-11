package com.yogesh.auraai.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yogesh.auraai.core.common.Result
import com.yogesh.auraai.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<SettingsUiEvent>()
    val events = _events.asSharedFlow()

    init {
        viewModelScope.launch {
            settingsRepository.observeSettings().collect { settings ->
                _uiState.update { it.copy(settings = settings) }
            }
        }
    }

    fun updateApiKey(key: String) {
        _uiState.update { state ->
            state.copy(settings = state.settings.copy(apiKey = key))
        }
    }

    fun updateModel(model: String) {
        _uiState.update { state ->
            state.copy(settings = state.settings.copy(modelName = model))
        }
    }

    fun updateSystemPrompt(prompt: String) {
        _uiState.update { state ->
            state.copy(settings = state.settings.copy(systemPrompt = prompt))
        }
    }

    fun toggleShowApiKey() {
        _uiState.update { it.copy(showApiKey = !it.showApiKey) }
    }

    fun toggleTheme(isDark: Boolean?) {
        viewModelScope.launch {
            settingsRepository.updateDarkTheme(isDark)
        }
    }

    fun saveSettings() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val settings = _uiState.value.settings
            settingsRepository.updateApiKey(settings.apiKey)
            settingsRepository.updateModel(settings.modelName)
            settingsRepository.updateSystemPrompt(settings.systemPrompt)
            _uiState.update { it.copy(isSaving = false) }
            _events.emit(SettingsUiEvent.SettingsSaved)
            _events.emit(SettingsUiEvent.ShowSnackbar("Settings saved"))
        }
    }

    fun testConnection() {
        viewModelScope.launch {
            _uiState.update { it.copy(isTestingConnection = true, connectionTestResult = null) }
            when (val result = settingsRepository.testConnection()) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            isTestingConnection = false,
                            connectionTestResult = "Connection successful",
                        )
                    }
                    _events.emit(SettingsUiEvent.ShowSnackbar("Connection successful"))
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isTestingConnection = false,
                            connectionTestResult = result.message,
                        )
                    }
                    _events.emit(SettingsUiEvent.ShowError(result.message))
                }
                Result.Loading -> Unit
            }
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            settingsRepository.clearAllData()
            _events.emit(SettingsUiEvent.NavigateToOnboarding)
        }
    }
}
