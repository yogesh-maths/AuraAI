package com.yogesh.auraai.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yogesh.auraai.core.common.Result
import com.yogesh.auraai.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState = _uiState.asStateFlow()


    fun updateName(name: String) {

        _uiState.update {
            it.copy(name = name, error = null)
        }

    }

    fun updateAge(age: String) {
        _uiState.update {
            it.copy(age = age, error = null)
        }
    }

    fun updatePhone(phone: String) {
        _uiState.update {
            it.copy(phone = phone, error = null)
        }
    }

    fun updateOtp(otp: String) {
        _uiState.update {
            it.copy(otp = otp, error = null)
        }
    }

    fun toggleTrait(trait: String) {

        val currentTraits =
            _uiState.value.selectedTraits.toMutableList()

        if (currentTraits.contains(trait)) {
            currentTraits.remove(trait)
        } else {
            if (currentTraits.size < 3) {
                currentTraits.add(trait)
            }
        }

        _uiState.update {
            it.copy(
                selectedTraits = currentTraits,
                error = null
            )
        }
    }

    fun nextStep() {

        when (_uiState.value.currentStep) {

            1 -> {
                _uiState.update {
                    it.copy(currentStep = 2)
                }
            }

            2 -> {

                val state = _uiState.value

                when {

                    state.name.isBlank() ->
                        showError("Enter name")

                    state.age.isBlank() ->
                        showError("Enter age")

                    state.phone.length != 10 ->
                        showError("Phone must be 10 digits")

                    state.otp != "1234" ->
                        showError("Invalid OTP")

                    else ->
                        _uiState.update {
                            it.copy(currentStep = 3)
                        }
                }
            }

            3 -> {

                if (_uiState.value.selectedTraits.size != 3) {

                    showError("Select exactly 3 traits")

                } else {

                    finishOnboarding()

                }
            }
        }
    }

    fun previousStep() {

        val step = _uiState.value.currentStep

        if (step > 1) {

            _uiState.update {
                it.copy(currentStep = step - 1)
            }
        }
    }

    private fun showError(message: String) {

        _uiState.update {
            it.copy(error = message)
        }
    }

    private fun finishOnboarding() {

        viewModelScope.launch {

            settingsRepository.setOnboardingComplete()

        }
    }
    }

