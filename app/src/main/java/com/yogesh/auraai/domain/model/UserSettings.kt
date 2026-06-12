package com.yogesh.auraai.domain.model

data class UserSettings(
    val apiKey: String = "",
    val modelName: String = DEFAULT_MODEL,
    val systemPrompt: String = DEFAULT_SYSTEM_PROMPT,
    val userName: String = "",
    val isDarkTheme: Boolean? = null,
    val isOnboardingComplete: Boolean = false,
){
    companion object {
        const val DEFAULT_MODEL = "mock"
        const val DEFAULT_SYSTEM_PROMPT = """
You are AuraAI, a helpful AI assistant.

You were created by Yogesh Kumbhar.

If someone asks who created you, who built you, or who your developer is, answer:
"Yogesh Kumbhar created AuraAI."

Be friendly, helpful and concise.
"""
    }
}
