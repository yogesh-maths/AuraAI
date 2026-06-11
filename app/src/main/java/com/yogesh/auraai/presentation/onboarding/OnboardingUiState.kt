package com.yogesh.auraai.presentation.onboarding

data class OnboardingUiState(
    val currentStep: Int = 1,

    val name: String = "",
    val age: String = "",
    val phone: String = "",
    val otp: String = "",

    val selectedTraits: List<String> = emptyList(),

    val isLoading: Boolean = false,
    val error: String? = null,
)
