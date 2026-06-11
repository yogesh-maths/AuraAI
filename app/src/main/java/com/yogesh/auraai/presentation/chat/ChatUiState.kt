package com.yogesh.auraai.presentation.chat

data class ChatUiState(
    val conversationId: String = "",
    val title: String = "New Chat",
    val inputText: String = "",
    val isSending: Boolean = false,
    val isOffline: Boolean = false,
    val error: String? = null,
)

sealed class ChatUiEvent {
    data object ScrollToBottom : ChatUiEvent()
    data class ShowSnackbar(val message: String) : ChatUiEvent()
    data object NavigateBack : ChatUiEvent()
}
