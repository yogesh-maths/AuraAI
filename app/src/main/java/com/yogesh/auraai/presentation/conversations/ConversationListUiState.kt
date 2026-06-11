package com.yogesh.auraai.presentation.conversations

data class ConversationListUiState(
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val isOffline: Boolean = false,
)

sealed class ConversationListUiEvent {
    data class OpenConversation(val id: String) : ConversationListUiEvent()
    data class ShowDeleteConfirm(val id: String) : ConversationListUiEvent()
    data class ShowSnackbar(val message: String) : ConversationListUiEvent()
}
