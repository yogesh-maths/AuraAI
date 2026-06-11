package com.yogesh.auraai.presentation.navigation

object Routes {
    const val SPLASH = "splash"
    const val ONBOARDING = "onboarding"
    const val CONVERSATIONS = "conversations"
    const val CHAT = "chat/{conversationId}"
    const val SETTINGS = "settings"

    fun chat(conversationId: String) = "chat/$conversationId"
}
